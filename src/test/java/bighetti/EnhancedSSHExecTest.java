package bighetti;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EnhancedSSHExecTest
{
	private EnhancedSSHExec task;

	@Before
	public void before()
	{
		task = new EnhancedSSHExec();

		TestUtils.setProject(task);
	}

	@Test
	public void clearCommandOutput()
	{
		Assert.assertEquals(0, task.getCommandOutput().size());

		task.getCommandOutput().add("x");
		Assert.assertEquals(1, task.getCommandOutput().size());

		task.clearCommandOutput();
		Assert.assertEquals(0, task.getCommandOutput().size());
	}

	@Test
	public void findBundlesByIdWithPreview()
	{
		task.setPreview(true);

		List<BundleDetails> result = task.findBundles(111);

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(111, result.get(0).getId());
		Assert.assertEquals("preview", result.get(0).getName());
		Assert.assertEquals("0.0", result.get(0).getVersion());
	}

	@Test
	public void findBundlesByIdWithPreviewError()
	{
		task.getCommandOutput().add("test");
		task.setPreview(true);

		try
		{
			task.findBundles(111);
			Assert.fail("Expected an exception.");
		}
		catch (Exception e)
		{
		}
	}

	@Test
	public void findBundlesByNameWithPreview()
	{
		task.setPreview(true);

		List<BundleDetails> result = task.findBundles("test", false, null);

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, result.get(0).getId());
		Assert.assertEquals("preview", result.get(0).getName());
		Assert.assertEquals("0.0", result.get(0).getVersion());
	}

	@Test
	public void findBundlesByNameWithPreviewError()
	{
		task.getCommandOutput().add("test");
		task.setPreview(true);

		try
		{
			task.findBundles("test", false, null);
			Assert.fail("Expected an exception.");
		}
		catch (Exception e)
		{
		}
	}

	@Test
	public void handleOutput()
	{
		task.setLogOutput(true);
		task.handleOutput("abc \u001B[m def  ");
		Assert.assertEquals(0, task.getCommandOutput().size());

		task.handleOutput("ghi \u001B[m jkl\n");
		Assert.assertEquals(1, task.getCommandOutput().size());
		Assert.assertEquals("abc  def  ghi  jkl\n", task.getCommandOutput().get(0));
	}

	@Test
	public void isBundleMatch()
	{
		BundleDetails details = new BundleDetails(111, "test1", "1.0");

		Assert.assertFalse(task.isBundleMatch(details, "test1", false, "0.0"));
		Assert.assertTrue(task.isBundleMatch(details, "test1", false, "1.0"));
		Assert.assertFalse(task.isBundleMatch(details, "test1", false, "2.0"));
		Assert.assertTrue(task.isBundleMatch(details, "test1", false, "*"));
		Assert.assertTrue(task.isBundleMatch(details, "test1", false, null));

		Assert.assertFalse(task.isBundleMatch(details, "test2", false, "1.0"));
		Assert.assertFalse(task.isBundleMatch(details, "test.*", false, "1.0"));
	}

	@Test
	public void isBundleMatchWithNull()
	{
		Assert.assertFalse(task.isBundleMatch(null, "test", false, null));
		Assert.assertFalse(task.isBundleMatch(new BundleDetails(), "test", false, null));
	}

	@Test
	public void isBundleMatchWithRegex()
	{
		BundleDetails details = new BundleDetails(111, "test1", "1.0");

		Assert.assertFalse(task.isBundleMatch(details, "test", true, "0.0"));
		Assert.assertTrue(task.isBundleMatch(details, "test", true, "1.0"));
		Assert.assertFalse(task.isBundleMatch(details, "test", true, "2.0"));
		Assert.assertTrue(task.isBundleMatch(details, "test", true, "*"));
		Assert.assertTrue(task.isBundleMatch(details, "test", true, null));

		Assert.assertFalse(task.isBundleMatch(details, "test2", true, "1.0"));
		Assert.assertTrue(task.isBundleMatch(details, "t.*t", true, "1.0"));
	}
}
