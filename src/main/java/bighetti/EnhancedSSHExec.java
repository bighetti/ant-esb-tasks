package bighetti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHExec;

/**
 * An enhancement of the SSH Exec task for executing karaf console commands.
 */
public class EnhancedSSHExec extends SSHExec
{
	/** The headers from the karaf "list" command */
	public static final String[] LIST_HEADERS = new String[] { "START LEVEL", "   ID   State" };

	/** A local copy of the command to be run */
	private String command;

	/** The complete output from the SSH command */
	private List<String> commandOutput = new ArrayList<String>();

	/** The karaf command to list bundles and filter the results */
	private String listAndGrepCommand = "list | grep --color never -i ";

	/** The karaf command to list bundles */
	private String listCommand = "list";

	/** The default log level to use */
	private int logLevel = Project.MSG_INFO;

	/** If the SSH command output should be logged */
	private boolean logOutput = true;

	/** The name of the property that that will contain the output */
	private String outputProperty;

	/** The number of times the output property name has been changed, since property override is prevented */
	private int outputPropertyCounter;

	/** If the task should only run in preview mode */
	boolean preview;

	/** The previous output line from the SSH command */
	private String previousOutput;

	/**
	 * Clear the output from the SSH command.
	 */
	protected void clearCommandOutput()
	{
		if (!preview)
		{
			if (outputProperty != null)
				setOutputproperty(outputProperty + ++outputPropertyCounter);

			commandOutput.clear();
		}
	}

	@Override
	public void execute()
	{
		executeCommand();
	}

	/**
	 * Execute a series of commands via SSH. The original failOnError setting will be restored afterwards.
	 *
	 * @param commandFile The command file.
	 * @param failOnError If the task should fail if an error occurs.
	 */
	protected void execute(File commandFile, boolean failOnError)
	{
		if (preview)
		{
			BufferedReader bufferedReader = null;
			String cmd;

			try
			{
				bufferedReader = new BufferedReader(new FileReader(commandFile));

				while ((cmd = bufferedReader.readLine()) != null)
					super.log("[PREVIEW] " + cmd, Project.MSG_INFO);
			}
			catch (Exception e)
			{
				if (bufferedReader != null)
				{
					try
					{
						bufferedReader.close();
					}
					catch (Exception ex)
					{
					}
				}
			}

			return;
		}

		boolean oldFailOnError = getFailonerror();

		setCommand(null);
		setCommandResource(commandFile.getAbsolutePath());
		setFailonerror(failOnError);
		executeCommand();

		setFailonerror(oldFailOnError);
	}

	/**
	 * Execute a command via SSH. The original failOnError setting will be restored afterwards.
	 *
	 * @param command The command.
	 * @param failOnError If the task should fail if an error occurs.
	 */
	protected void execute(String command, boolean failOnError)
	{
		boolean oldFailOnError = getFailonerror();

		setCommand(command);
		setFailonerror(failOnError);
		executeCommand();

		setCommand(null);
		setFailonerror(oldFailOnError);
	}

	/**
	 * Execute the SSH command.
	 */
	protected void executeCommand()
	{
		if (preview)
		{
			super.log("[PREVIEW] " + command, Project.MSG_INFO);
			return;
		}

		super.execute();
	}

	/**
	 * Find a bundle by id.
	 *
	 * @param id The id.
	 * @return The bundles that matched, should only be one.
	 */
	protected List<BundleDetails> findBundles(int id)
	{
		// Do not show the SSH output at INFO level.
		logAsVerbose();
		setCommand(listAndGrepCommand + id);

		if (preview)
			super.log("[PREVIEW] " + command, Project.MSG_INFO);
		else
			super.execute();

		List<BundleDetails> result = new ArrayList<BundleDetails>();

		for (String line : getCommandOutput())
		{
			if (isHeader(line))
				continue;

			BundleDetails bundleDetails = new BundleDetails(line, true);

			if (bundleDetails.getId() == id)
				result.add(bundleDetails);
		}

		if (preview && commandOutput.isEmpty() && result.size() == 0)
			result.add(new BundleDetails(id, "preview", "0.0"));

		if (result.size() == 0)
			throw new BuildException("No bundle found with id: " + id);

		return result;
	}

	/**
	 * Find bundles by name.
	 *
	 * @param name The name to match.
	 * @param regex If regex should be used to match the name.
	 * @param version The version number to match, can be null or "*" for all.
	 * @return The bundles that matched.
	 * @throws BuildException If exactly one bundle is not found (without version specified), or no bundles are found (with version specified).
	 */
	protected List<BundleDetails> findBundles(String name, boolean regex, String version) throws BuildException
	{
		// Do not show the SSH output at INFO level.
		logAsVerbose();
		setCommand(listCommand);

		if (preview)
			super.log("[PREVIEW] " + command, Project.MSG_INFO);
		else
			super.execute();

		List<BundleDetails> result = new ArrayList<BundleDetails>();

		for (String line : getCommandOutput())
		{
			if (isHeader(line))
				continue;

			BundleDetails bundleDetails = new BundleDetails(line, true);

			if (isBundleMatch(bundleDetails, name, regex, version))
				result.add(bundleDetails);
		}

		if (preview && commandOutput.isEmpty() && result.size() == 0)
			result.add(new BundleDetails(0, "preview", "0.0"));

		if (result.size() == 0)
			throw new BuildException("No bundles found with name: " + name);

		if (result.size() > 1 && StringUtils.isEmpty(version))
			throw new BuildException("More than one bundle found with name: " + name + ". Specify an exact version number or \"*\".");

		return result;
	}

	/**
	 * Get the karaf command to list bundles and filter the results.
	 *
	 * @return The karaf command to list bundles and filter the results.
	 */
	public String getListAndGrepCommand()
	{
		return listAndGrepCommand;
	}

	/**
	 * Get the karaf command to list bundles.
	 *
	 * @return The karaf command to list bundles.
	 */
	public String getListCommand()
	{
		return listCommand;
	}

	/**
	 * Get the complete output from the SSH command.
	 *
	 * @return The complete output from the SSH command.
	 */
	public List<String> getCommandOutput()
	{
		if (outputProperty != null)
		{
			String output = getProject().getProperty(outputProperty);

			if (output == null)
				return new ArrayList<String>();

			output = output.replace("\u001B[m", "");

			return Arrays.asList(output.split("\\n"));
		}

		return commandOutput;
	}

	/**
	 * Save the output line from the SSH command so that it can be handled as a batch, including handling for incomplete lines.
	 *
	 * @param output The output line from the SSH command.
	 */
	@Override
	protected void handleOutput(String output)
	{
		output = output.replace("\u001B[m", "");

		if (previousOutput != null)
		{
			output = previousOutput + output;
			previousOutput = null;
		}

		if (output.endsWith("\n"))
		{
			if (logOutput)
				log(output);

			this.commandOutput.add(output);
		}
		else
		{
			previousOutput = output;
		}
	}

	@Override
	public void init() throws BuildException
	{
		super.init();

		setTrust(true);
	}

	/**
	 * Check if the bundle matches the search parameters.
	 *
	 * @param bundleDetails The bundle details.
	 * @param name The name to match.
	 * @param regex If regex should be used to match the name.
	 * @param version The version number to match, can be null or "*" for all.
	 * @return If the bundle matches the search parameters.
	 */
	protected boolean isBundleMatch(BundleDetails bundleDetails, String name, boolean regex, String version)
	{
		if (bundleDetails == null || bundleDetails.getName() == null)
			return false;

		String bundleName = bundleDetails.getName().toLowerCase();
		String bundleVersion = bundleDetails.getVersion();
		String nameLower = name.toLowerCase();
		boolean result;

		if (regex)
			result = bundleName.matches(".*" + nameLower + ".*");
		else
			result = bundleName.contains(nameLower);

		if (result && version != null && !version.equals("*"))
			result = version.equals(bundleVersion);

		return result;
	}

	/**
	 * Check if the SSH output is a header line from the karaf "list" command.
	 *
	 * @param line The SSH output line.
	 * @return If the output is a header line.
	 */
	protected boolean isHeader(String line)
	{
		for (String header : LIST_HEADERS)
		{
			if (line.startsWith(header))
				return true;
		}

		return false;
	}

	/**
	 * Get if the task should only run in preview mode.
	 *
	 * @return If the task should only run in preview mode.
	 */
	public boolean isPreview()
	{
		return preview;
	}

	/**
	 * Log the message using the configured log level instead of {@code org.apache.tools.ant.Project.MSG_INFO} always.
	 *
	 * @param msg The message.
	 */
	@Override
	public void log(String msg)
	{
		super.log(msg, logLevel);
	}

	/**
	 * Log the message using the configured log level, if the message level is not {@code org.apache.tools.ant.Project.MSG_WARN} or higher.
	 *
	 * @param msg The message.
	 * @param msgLevel The desired message level (may be overriden).
	 */
	public void log(String msg, int msgLevel)
	{
		if (msgLevel > Project.MSG_WARN && msgLevel < logLevel)
			super.log(msg, logLevel);
		else
			super.log(msg, msgLevel);
	}

	/**
	 * Change the default log level to {@code org.apache.tools.ant.Project.MSG_INFO}.
	 */
	public void logAsInfo()
	{
		logLevel = Project.MSG_INFO;
	}

	/**
	 * Change the default log level to {@code org.apache.tools.ant.Project.MSG_VERBOSE}.
	 */
	public void logAsVerbose()
	{
		logLevel = Project.MSG_VERBOSE;
	}

	/**
	 * Log a plain message, without the source identifier prefix, e.g. [taskName].
	 *
	 * @param msg The message.
	 * @param msgLevel The message level.
	 */
	public void logPlain(String msg, int msgLevel)
	{
		if (getProject() != null)
		{
			getProject().log(msg, msgLevel);
		}
		else
		{
			if (msgLevel <= Project.MSG_INFO)
				System.err.println(msg);
		}
	}

	@Override
	public void setCommand(String command)
	{
		this.command = command;

		super.setCommand(command);
	}

	protected void setCommandOutput(List<String> commandOutput)
	{
		this.commandOutput = commandOutput;
	}

	/**
	 * Set the karaf command to list bundles and filter the results.
	 *
	 * @param listAndGrepCommand The karaf command to list bundles and filter the results.
	 */
	public void setListAndGrepCommand(String listAndGrepCommand)
	{
		this.listAndGrepCommand = listAndGrepCommand;
	}

	/**
	 * Set the karaf command to list bundles.
	 *
	 * @param listCommand The karaf command to list bundles.
	 */
	public void setListCommand(String listCommand)
	{
		this.listCommand = listCommand;
	}

	/**
	 * Set if the SSH command output should be logged.
	 *
	 * @param logOutput If the SSH command output should be logged.
	 */
	public void setLogOutput(boolean logOutput)
	{
		this.logOutput = logOutput;
	}

	/**
	 * Set the name of the property that that will contain the output.
	 *
	 * @param outputProperty The name of the property that that will contain the output.
	 */
	public void setOutputproperty(String outputProperty)
	{
		super.setOutputproperty(outputProperty);

		this.outputProperty = outputProperty;
	}

	/**
	 * Set if the task should only run in preview mode.
	 *
	 * @param preview If the task should only run in preview mode.
	 */
	public void setPreview(boolean preview)
	{
		this.preview = preview;
	}
}
