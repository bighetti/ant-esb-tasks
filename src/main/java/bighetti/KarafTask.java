package bighetti;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHExec;

/**
 * The main karaf task which supports embedded tasks.
 */
public class KarafTask extends SSHExec implements TaskContainer
{
	/** The path to the known hosts file */
	private String knownHosts;

	/** If the task should only run in preview mode */
	private boolean preview;

	/** The nested tasks */
	private List<Task> tasks = new ArrayList<Task>();

	public void addTask(Task task)
	{
		tasks.add(task);
	}

	/**
	 * Add a boolean value to the configuration of a nested task.
	 *
	 * @param runtimeConfigurable The configuration of a nested task.
	 * @param key The property key.
	 * @param value The property value.
	 */
	protected void configure(RuntimeConfigurable runtimeConfigurable, String key, boolean value)
	{
		configure(runtimeConfigurable, key, Boolean.toString(value));
	}

	/**
	 * Add an integer value to the configuration of a nested task.
	 *
	 * @param runtimeConfigurable The configuration of a nested task.
	 * @param key The property key.
	 * @param value The property value.
	 */
	protected void configure(RuntimeConfigurable runtimeConfigurable, String key, int value)
	{
		configure(runtimeConfigurable, key, Integer.toString(value));
	}

	/**
	 * Add a string value to the configuration of a nested task.
	 *
	 * @param runtimeConfigurable The configuration of a nested task.
	 * @param key The property key.
	 * @param value The property value.
	 */
	protected void configure(RuntimeConfigurable runtimeConfigurable, String key, String value)
	{
		if (runtimeConfigurable == null || key == null || value == null)
			return;

		if (runtimeConfigurable.getAttributeMap().get(key) == null)
			runtimeConfigurable.setAttribute(key, value);
	}

	/**
	 * Configure a nested task with shared SSH settings.
	 *
	 * @param task The nested task.
	 */
	protected void configure(Task task)
	{
		if (task instanceof UnknownElement)
		{
			RuntimeConfigurable runtimeConfigurable = ((UnknownElement) task).getWrapper();

			configure(runtimeConfigurable, "failonerror", getFailonerror());
			configure(runtimeConfigurable, "host", getHost());
			configure(runtimeConfigurable, "knownhosts", knownHosts);
			configure(runtimeConfigurable, "port", getPort());
			configure(runtimeConfigurable, "preview", preview);
			configure(runtimeConfigurable, "verbose", getVerbose());

			if (getUserInfo() != null)
			{
				configure(runtimeConfigurable, "keyfile", getUserInfo().getKeyfile());
				configure(runtimeConfigurable, "passphrase", getUserInfo().getPassphrase());
				configure(runtimeConfigurable, "password", getUserInfo().getPassword());
				configure(runtimeConfigurable, "trust", getUserInfo().getTrust());
				configure(runtimeConfigurable, "username", getUserInfo().getName());
			}
		}
	}

	@Override
	public void execute()
	{
		for (Task task : tasks)
		{
			configure(task);

			task.perform();
		}
	}

	@Override
	public void init() throws BuildException
	{
		super.init();

		setPort(8101);
		setTrust(true);
	}

	/**
	 * Set the path to the known hosts file.
	 *
	 * @param knownHosts The path to the known hosts file.
	 */
	@Override
	public void setKnownhosts(String knownHosts)
	{
		super.setKnownhosts(knownHosts);

		this.knownHosts = knownHosts;
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
