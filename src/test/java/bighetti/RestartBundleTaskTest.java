package bighetti;

import org.junit.Before;

public class RestartBundleTaskTest extends AbstractBundleTaskTest
{
	@Before
	public void before()
	{
		task = new RestartBundleTask();

		task.setId(111);
		task.setHost("localhost");
		task.setPassword("password");
		task.setUsername("username");

		TestUtils.setProject(task);
	}
}
