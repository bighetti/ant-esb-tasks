package bighetti;

import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KarafTaskTest
{
	private KarafTask task;

	@Before
	public void before()
	{
		task = new KarafTask();

		TestUtils.setProject(task);
	}

	@Test
	public void execute()
	{
		StartBundleTask startBundleTask = new StartBundleTask();

		startBundleTask.setId(111);
		startBundleTask.setHost("localhost");
		startBundleTask.setPassword("password");
		startBundleTask.setPreview(true);
		startBundleTask.setProject(new Project());
		startBundleTask.setUsername("username");

		task.addTask(startBundleTask);
		task.execute();
	}

	@Test
	public void executeWithError()
	{
		StartBundleTask startBundleTask = new StartBundleTask();

		startBundleTask.setPreview(true);
		startBundleTask.setProject(new Project());

		task.addTask(startBundleTask);

		try
		{
			task.execute();
			Assert.fail("Expected an exception.");
		}
		catch (Exception e)
		{
		}
	}

	@Test
	public void init()
	{
		Assert.assertEquals(22, task.getPort());

		task.init();
		Assert.assertEquals(8101, task.getPort());
	}
}
