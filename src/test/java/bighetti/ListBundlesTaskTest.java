package bighetti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ListBundlesTaskTest
{
	private static final String ANOTHER_BUNDLE_STARTED = "[ 222] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)\n";
	private static final String BUNDLE_ACTIVE_SAVED_OUTPUT = "111,Active,,Test Bundle,2.0.0\n";
	private static final String BUNDLE_FAILED = "[ 111] [Resolved   ] [            ] [Failed ] [   60] Test Bundle (2.0.0)\n";
	private static final String BUNDLE_FAILED_SAVED_OUTPUT = "111,Active,Failed,Test Bundle,2.0.0\n";
	private static final String BUNDLE_INSTALLED = "[ 111] [Installed  ] [            ] [Started] [   60] Test Bundle (2.0.0)\n";
	private static final String BUNDLE_RESOLVED_SAVED_OUTPUT = "111,Resolved,Started,Test Bundle,2.0.0\n";
	private static final String BUNDLE_STARTED = "[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)\n";
	private static final String BUNDLE_STARTED_RENAMED = "[ 111] [Active     ] [            ] [Started] [   60] xTest Bundlex (3.0.0)\n";
	private static final String BUNDLE_STARTED_SAVED_OUTPUT = "111,Active,Started,Test Bundle,2.0.0\n";

	private List<String> commandOutput;
	private Project project;
	private ListBundlesTask task;

	@Before
	public void before()
	{
		commandOutput = new ArrayList<String>();
		project = new Project();
		task = new ListBundlesTask();

		task.setHost("localhost");
		task.setPassword("password");
		task.setPreview(true);
		task.setUsername("user");

		TestUtils.setProject(task, project);
	}

	@Test
	public void configure()
	{
		task.setGrep("");
		task.configure();

		task.setGrep("test");
		task.configure();
	}

	@Test
	public void eval()
	{
		commandOutput.add(BUNDLE_STARTED);

		project.setNewProperty("x", BUNDLE_STARTED_SAVED_OUTPUT);
		task.setCommandOutput(commandOutput);
		task.setVerifyProperty("x");

		Assert.assertTrue(task.eval());
	}

	@Test
	public void evalBundleDeleted()
	{
		commandOutput.add(ANOTHER_BUNDLE_STARTED);

		project.setNewProperty("x", BUNDLE_STARTED_SAVED_OUTPUT);
		task.setCommandOutput(commandOutput);
		task.setVerifyProperty("x");

		Assert.assertTrue(task.eval());
	}

	@Test
	public void evalBundleRenamed()
	{
		commandOutput.add(BUNDLE_STARTED_RENAMED);

		project.setNewProperty("x", BUNDLE_STARTED_SAVED_OUTPUT);
		task.setCommandOutput(commandOutput);
		task.setVerifyProperty("x");

		Assert.assertTrue(task.eval());
	}

	@Test
	public void evalBundleStartedAfterRestartFromActive()
	{
		commandOutput.add(BUNDLE_STARTED);

		project.setNewProperty("x", BUNDLE_ACTIVE_SAVED_OUTPUT);
		task.setCommandOutput(commandOutput);
		task.setVerifyProperty("x");

		Assert.assertTrue(task.eval());
	}

	@Test
	public void evalBundleStartedAfterRestartFromFailed()
	{
		commandOutput.add(BUNDLE_STARTED);

		project.setNewProperty("x", BUNDLE_FAILED_SAVED_OUTPUT);
		task.setCommandOutput(commandOutput);
		task.setVerifyProperty("x");

		Assert.assertTrue(task.eval());
	}

	@Test
	public void evalBundleStoppedAfterRestart()
	{
		commandOutput.add(BUNDLE_INSTALLED);

		project.setNewProperty("x", BUNDLE_RESOLVED_SAVED_OUTPUT);
		task.setCommandOutput(commandOutput);
		task.setVerifyProperty("x");

		Assert.assertTrue(task.eval());
	}

	@Test
	public void evalFalse()
	{
		commandOutput.add(BUNDLE_FAILED);

		project.setNewProperty("x", BUNDLE_STARTED_SAVED_OUTPUT);
		task.setCommandOutput(commandOutput);
		task.setVerifyProperty("x");

		Assert.assertFalse(task.eval());
	}

	@Test
	public void evalNotConfigured()
	{
		Assert.assertTrue(task.eval());

		task.setVerifyProperty("");
		Assert.assertTrue(task.eval());

		task.setVerifyProperty("x");
		Assert.assertTrue(task.eval());
	}

	@Test
	public void execute()
	{
		task.setCommandOutput(Arrays.asList(BUNDLE_STARTED));
		task.execute();

		Assert.assertEquals(Arrays.asList(BUNDLE_STARTED), task.getCommandOutput());
	}

	@Test
	public void executeWithOutputProperty()
	{
		task.setCommandOutput(Arrays.asList(BUNDLE_STARTED));
		task.setOutputProperty("x");
		task.execute();

		Assert.assertEquals(BUNDLE_STARTED_SAVED_OUTPUT, project.getProperty("x"));
	}

	@Test
	public void executeWithOutputPropertyAlreadySet()
	{
		project.setNewProperty("x", "y");
		task.setOutputProperty("x");

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
	public void executeWithOutputPropertyBlank()
	{
		task.setCommandOutput(Arrays.asList(BUNDLE_STARTED));
		task.setOutputProperty("");
		task.execute();
	}

	@Test
	public void findBundleID()
	{
		Assert.assertNull(task.findBundleID(null));
		Assert.assertNull(task.findBundleID(""));
		Assert.assertEquals("111,", task.findBundleID(BUNDLE_STARTED_SAVED_OUTPUT));
	}
}
