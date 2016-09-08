package bighetti;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * The task to list all bundles.
 */
public class ListBundlesTask extends EnhancedSSHExec implements Condition
{
	/** The grep string */
	private String grep;

	/** The property to save the output to */
	private String outputProperty;

	/** The property indicating if bundles with incorrect state should be refreshed during verification */
	private boolean refresh;

	/** The property containing the previous output to compare and verify against */
	private String verifyProperty;

	public ListBundlesTask()
	{
		setTrust(true);
	}

	/**
	 * Configure the task properties.
	 */
	protected void configure()
	{
		if (grep == null || "".equals(grep))
			setCommand(getListCommand());
		else
			setCommand(getListAndGrepCommand() + "\"" + grep + "\"");
	}

	public boolean eval() throws BuildException
	{
		if (StringUtils.isEmpty(verifyProperty))
		{
			logPlain("Verify Property was not specified, skipping evaluation.", Project.MSG_VERBOSE);
			return true;
		}

		String verifyOutput = getProject().getProperty(verifyProperty);

		if (verifyOutput == null)
		{
			logPlain("Verify Property has no value, skipping evaluation.", Project.MSG_VERBOSE);
			return true;
		}

		List<BundleDetails> verifyBundles = getSavedOutput(verifyOutput);

		clearCommandOutput();
		configure();
		logAsVerbose();
		perform();
		logAsInfo();

		return verify(verifyBundles, getOutput());
	}

	@Override
	public void execute()
	{
		if (getProject().getProperty(outputProperty) != null)
			throw new BuildException("The Output Property has already been assigned.");

		configure();
		setLogOutput(false);
		super.execute();
		setLogOutput(true);
		process();
	}

	/**
	 * Refresh a bundle.
	 *
	 * @param bundleDetails The bundle details.
	 */
	protected void executeRefresh(BundleDetails bundleDetails)
	{
		RefreshBundleTask task = new RefreshBundleTask();

		task.setHost(getHost());
		task.setId(bundleDetails.getId());
		task.setKeyfile(getUserInfo().getKeyfile());
		task.setNested(true);
		task.setOwningTarget(getOwningTarget());
		task.setOutputproperty("executeRefresh");
		task.setPassword(getUserInfo().getPassword());
		task.setPort(getPort());
		task.setPreview(isPreview());
		task.setProject(getProject());
		task.setSuppressSystemOut(true);
		task.setTaskName(getTaskName());
		task.setTrust(getUserInfo().getTrust());
		task.setUsername(getUserInfo().getName());
		task.setVerbose(false);

		task.execute();
	}

	/**
	 * Find the Bundle ID text fragment from the OSGI list output.
	 *
	 * @param output A line from the OSGI list output.
	 * @return The Bundle ID text fragment, or null if not found.
	 */
	protected String findBundleID(String output)
	{
		if (output == null)
			return null;

		int index = output.indexOf(",");

		if (index < 1)
			return null;

		return output.substring(0, index + 1);
	}

	/**
	 * Get the complete output from the SSH command.
	 *
	 * @return The complete output from the SSH command.
	 */
	public List<BundleDetails> getOutput()
	{
		List<BundleDetails> result = new ArrayList<BundleDetails>();

		for (String output : getCommandOutput())
			result.add(new BundleDetails(output, true));

		return result;
	}

	/**
	 * Parse the lines of output returned from the SSH command and create objects.
	 *
	 * @param output The output returned from the SSH command.
	 * @return The BundleDetails objects.
	 */
	protected List<BundleDetails> getSavedOutput(String output)
	{
		List<BundleDetails> result = new ArrayList<BundleDetails>();
		StringTokenizer tokenizer = new StringTokenizer(output, "\n", false);

		while (tokenizer.hasMoreTokens())
			result.add(new BundleDetails(tokenizer.nextToken(), false));

		return result;
	}

	/**
	 * Process the lines of output returned from the SSH command.<br/><br/>
	 *
	 * Log the output and optionally save it into a project property.
	 */
	protected void process()
	{
		StringBuilder builder = null;

		if (StringUtils.isNotEmpty(outputProperty))
			builder = new StringBuilder();

		for (String line : getCommandOutput())
		{
			line = new BundleDetails(line, true).toString() + "\n";

			log(line, Project.MSG_INFO);

			if (builder != null)
				builder.append(line);
		}

		if (builder != null)
			getProject().setNewProperty(outputProperty, builder.toString());
	}

	/**
	 * Set the grep string.
	 *
	 * @param grep The grep string.
	 */
	public void setGrep(String grep)
	{
		this.grep = grep;
	}

	/**
	 * Set the property to save the output to.
	 *
	 * @param outputProperty The property to save the output to.
	 */
	public void setOutputProperty(String outputProperty)
	{
		this.outputProperty = outputProperty;
	}

	/**
	 * Set The property indicating if bundles with incorrect state should be refreshed during verification.
	 *
	 * @param refresh The property indicating if bundles with incorrect state should be refreshed during verification.
	 */
	public void setRefresh(boolean refresh)
	{
		this.refresh = refresh;
	}

	/**
	 * The property containing the previous output to compare and verify against.
	 *
	 * @param verifyProperty The property containing the previous output to compare and verify against.
	 */
	public void setVerifyProperty(String verifyProperty)
	{
		this.verifyProperty = verifyProperty;
	}

	/**
	 * Verify the new list of bundles contains all of the old bundles with the same state.<br/><br/>
	 *
	 * Note: Any bundles that have been deleted will be ignored.
	 *
	 * @param oldBundles The old bundles.
	 * @param newBundles The new bundles.
	 * @return If the new list of bundles matches the old list.
	 */
	protected boolean verify(List<BundleDetails> oldBundles, List<BundleDetails> newBundles)
	{
		for (BundleDetails oldBundle : oldBundles)
		{
			logPlain("Checking for: " + oldBundle, Project.MSG_DEBUG);

			// Check each acceptable status for the bundle after restarting.
			if (newBundles.contains(oldBundle))
				continue;

			// The bundle was not found, or did not have the expected status.
			BundleDetails newBundle = null;

			for (BundleDetails obj : newBundles)
			{
				if (oldBundle.getId() == obj.getId())
				{
					newBundle = obj;
					break;
				}
			}

			// Continue with the next verification if the bundle has been deleted.
			if (newBundle == null)
			{
				logPlain("Bundle no longer exists: " + oldBundle, Project.MSG_VERBOSE);
				continue;
			}
			// Continue with the next verification if the bundle went from Failed to Started.
			else if (oldBundle.getSpringState().equals("Failed") && newBundle.getSpringState().equals("Started"))
			{
				continue;
			}
			// Continue with the next verification if the bundle went from None to Started.
			else if (oldBundle.getSpringState().equals("") && newBundle.getSpringState().equals("Started"))
			{
				continue;
			}

			logPlain("Did not find: " + oldBundle, Project.MSG_VERBOSE);

			if (refresh)
			{
				logPlain("Refreshing bundle with incorrect state: " + newBundle, Project.MSG_VERBOSE);
				executeRefresh(newBundle);
			}

			return false;
		}

		return true;
	}
}
