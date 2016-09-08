package bighetti;

import org.junit.Before;

public class RefreshBundleTaskTest extends AbstractBundleTaskTest
{
	@Before
	public void before()
	{
		task = new RefreshBundleTask();

		task.setId(111);
		task.setHost("localhost");
		task.setPassword("password");
		task.setUsername("username");

		TestUtils.setProject(task);
	}
}
