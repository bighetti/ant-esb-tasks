package bighetti;

import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 * The task to start a bundle.
 */
public class StartBundleTask extends AbstractBundleTask
{
	public StartBundleTask()
	{
		super("start", "Starting");
	}

	protected void validateExistingState(List<BundleDetails> bundles) throws BuildException
	{
		for (BundleDetails bundle : bundles)
		{
			if (bundle.isStarted())
				throw new BuildException("The bundle is already started: " + bundle.getDisplayName());
		}
	}
}
