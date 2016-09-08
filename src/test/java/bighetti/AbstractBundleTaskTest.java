package bighetti;

import java.util.Arrays;

import org.apache.tools.ant.BuildException;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractBundleTaskTest
{
	public static final String BUNDLE_1 = "[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)";
	public static final String BUNDLE_2 = "[ 222] [Active     ] [            ] [Started] [   60] Test Bundle (3.0.0)";
	public static final String BUNDLE_3 = "[ 333] [Active     ] [            ] [       ] [   60] Other Bundle (3.0.0)";
	public static final String BUNDLE_4 = "[ 444] [Resolved   ] [            ] [       ] [   60] Resolved Bundled (2.0.0)";
	public static final String LIST_HEADER = "START LEVEL 100 , List Threshold: -1";

	protected AbstractBundleTask task;

	public void executeWithError()
	{
		try
		{
			task.execute();
			Assert.fail("Expected an exception.");
		}
		catch (BuildException e)
		{
		}
	}

	@Test
	public void execute()
	{
		task.setCommandOutput(Arrays.asList(LIST_HEADER, BUNDLE_1));
		task.setPreview(true);
		task.execute();
	}

	@Test
	public void executeWithBundleName()
	{
		task.setCommandOutput(Arrays.asList(LIST_HEADER, BUNDLE_1, BUNDLE_2, BUNDLE_3));
		task.setId(0);
		task.setName("Test Bundle");
		task.setPreview(true);
		executeWithError();
	}

	@Test
	public void executeWithBundleNameAndVersion()
	{
		task.setCommandOutput(Arrays.asList(LIST_HEADER, BUNDLE_1, BUNDLE_2, BUNDLE_3));
		task.setId(0);
		task.setName("Test Bundle");
		task.setPreview(true);
		task.setVersion("2.0.0");
		task.execute();

		task.setVersion("3.0.0");
		task.execute();

		task.setVersion("*");
		task.execute();

		task.setVersion("4.0.0");
		executeWithError();
	}

	@Test
	public void executeWithBundleNameRegex()
	{
		task.setCommandOutput(Arrays.asList(LIST_HEADER, BUNDLE_1, BUNDLE_2, BUNDLE_3));
		task.setId(0);
		task.setName("test bundle");
		task.setPreview(true);
		task.setRegex(true);
		executeWithError();

		task.setName("t.*bundle");
		executeWithError();
	}

	@Test
	public void executeWithBundleNameRegexAndVersion()
	{
		task.setCommandOutput(Arrays.asList(LIST_HEADER, BUNDLE_1, BUNDLE_2, BUNDLE_3));
		task.setId(0);
		task.setName("test bundle");
		task.setPreview(true);
		task.setRegex(true);
		task.setVersion("2.0.0");
		task.execute();

		task.setVersion("3.0.0");
		task.execute();

		task.setVersion("*");
		task.execute();

		task.setName("t.*bundle");
		task.execute();

		task.setVersion("4.0.0");
		executeWithError();
	}

	@Test
	public void executeWithInvalidId()
	{
		task.setId(0);
		executeWithError();

		task.setCommandOutput(Arrays.asList(LIST_HEADER, BUNDLE_1));
		task.setId(222);
		task.setPreview(true);
		executeWithError();
	}

	@Test
	public void executeWithInvalidHost()
	{
		task.setHost(null);
		executeWithError();
	}

	@Test
	public void executeWithInvalidName()
	{
		task.setId(0);
		task.setName(null);
		executeWithError();
	}

	@Test
	public void executeWithInvalidKeyfile()
	{
		task.setPassword(null);
		task.setKeyfile(null);
		executeWithError();

		task.setKeyfile("");
		executeWithError();
	}

	@Test
	public void executeWithInvalidPassword()
	{
		task.setPassword(null);
		executeWithError();
	}

	@Test
	public void executeWithInvalidUsername()
	{
		task.setUsername(null);
		executeWithError();
	}

	@Test
	public void init()
	{
		Assert.assertEquals(22, task.getPort());

		task.init();
		Assert.assertEquals(8101, task.getPort());
	}
}
