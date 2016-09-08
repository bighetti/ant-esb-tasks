package bighetti;

import java.util.List;

import bighetti.BundleDetails;
import bighetti.EnhancedSSHExec;
import bighetti.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * The abstract task for all karaf bundle commands.
 */
public abstract class AbstractBundleTask extends EnhancedSSHExec
{
	/** The karaf command */
	private String command;

	/** The description of the karaf command */
	private String commandDescription;

	/** If the task should fail if the existing state is invalid */
	private boolean failExistingState;

	/** The bundle id */
	private int id;

	/** The bundle name */
	private String name;

	/** If this task is being invoked by another task */
	private boolean nested;

	/** If regex should be used to match the name */
	private boolean regex;

	/** The bundle version */
	private String version;

	/**
	 * The default constructor.
	 *
	 * @param command The karaf command to execute.
	 * @param commandDescription The description of the command being performed.
	 */
	public AbstractBundleTask(String command, String commandDescription)
	{
		this.command = command;
		this.commandDescription = commandDescription;
	}

	@Override
	public void execute()
	{
		if (id == 0 && StringUtils.isEmpty(name))
			throw new BuildException("ID or Name is required.");

		List<BundleDetails> bundles;
		String commandLine = command;

		if (name == null)
			bundles = findBundles(id);
		else
			bundles = findBundles(name, regex, version);

		if (failExistingState)
			validateExistingState(bundles);

		clearCommandOutput();

		if (!nested)
			logAsInfo();

		for (BundleDetails bundle : bundles)
		{
			commandLine = commandLine + " " + bundle.getId();
			log(commandDescription + ": " + bundle.getDisplayName(), Project.MSG_INFO);
		}

		setCommand(commandLine);
		super.execute();

		for (String line : getCommandOutput())
			log(line, Project.MSG_INFO);

		if (nested)
			clearCommandOutput();
	}

	@Override
	public void init() throws BuildException
	{
		super.init();

		setPort(8101);
	}

	/**
	 * Set if the task should fail if the existing state is invalid.
	 *
	 * @param failExistingState If the task should fail if the existing state is invalid.
	 */
	public void setFailExistingState(boolean failExistingState)
	{
		this.failExistingState = failExistingState;
	}

	/**
	 * Set the bundle id.
	 *
	 * @param id The bundle id.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Set the bundle name.
	 *
	 * @param name The bundle name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Set if this task is being invoked by another task.
	 *
	 * @param nested If this task is being invoked by another task.
	 */
	public void setNested(boolean nested)
	{
		this.nested = nested;
	}

	/**
	 * Set if regex should be used to match the name.
	 *
	 * @param regex If regex should be used to match the name.
	 */
	public void setRegex(boolean regex)
	{
		this.regex = regex;
	}

	/**
	 * Set the bundle version.
	 *
	 * @param version The bundle version.
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 * Check if the bundles have an invalid existing state to perform the operation.
	 *
	 * @param bundles The bundles to perform the operation on.
	 * @throws BuildException
	 */
	protected abstract void validateExistingState(List<BundleDetails> bundles) throws BuildException;
}
