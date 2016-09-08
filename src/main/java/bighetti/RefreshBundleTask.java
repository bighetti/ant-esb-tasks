package bighetti;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 * The task to refresh a bundle.
 */
public class RefreshBundleTask extends AbstractBundleTask
{
	/** If all failed bundles should be refreshed */
	private boolean failed;

	public RefreshBundleTask()
	{
		super("refresh", "Refreshing");
	}

	@Override
	protected List<BundleDetails> findBundles(String name, boolean regex, String version) throws BuildException
	{
		List<BundleDetails> bundles = super.findBundles(name, regex, version);
		List<BundleDetails> result = new ArrayList<BundleDetails>();

		if (!failed)
			return bundles;

		for (BundleDetails bundle : bundles)
		{
			if ("Failed".equals(bundle.getSpringState()))
				result.add(bundle);
		}

		return result;
	}

	protected void validateExistingState(List<BundleDetails> bundles) throws BuildException
	{
	}

	/**
	 * Set if all failed bundles should be refreshed.
	 *
	 * @param failed If all failed bundles should be refreshed.
	 */
	public void setFailed(boolean failed)
	{
		this.failed = failed;
	}
}
