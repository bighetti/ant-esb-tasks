package bighetti;

import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 * The task to stop a bundle.
 */
public class StopBundleTask extends AbstractBundleTask
{
	public StopBundleTask()
	{
		super("stop", "Stopping");
	}

	protected void validateExistingState(List<BundleDetails> bundles) throws BuildException
	{
		for (BundleDetails bundle : bundles)
		{
			if (bundle.isStopped())
				throw new BuildException("The bundle is already stopped: " + bundle.getDisplayName());
		}
	}
}
