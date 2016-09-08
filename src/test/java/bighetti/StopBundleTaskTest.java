package bighetti;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class StopBundleTaskTest extends AbstractBundleTaskTest
{
	@Before
	public void before()
	{
		task = new StopBundleTask();

		task.setId(111);
		task.setHost("localhost");
		task.setPassword("password");
		task.setUsername("username");

		TestUtils.setProject(task);
	}

	@Test
	public void executeWithFailExistingState()
	{
		task.setCommandOutput(Arrays.asList(BUNDLE_1, BUNDLE_4));
		task.setFailExistingState(true);
		task.setId(111);
		task.setPreview(true);
		task.execute();

		task.setId(444);
		executeWithError();
	}
}
