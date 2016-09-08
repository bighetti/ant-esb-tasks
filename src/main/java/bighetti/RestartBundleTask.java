package bighetti;

import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 * The task to restart a bundle.
 */
public class RestartBundleTask extends AbstractBundleTask
{
	public RestartBundleTask()
	{
		super("restart", "Restarting");
	}

	protected void validateExistingState(List<BundleDetails> bundles) throws BuildException
	{
	}
}
