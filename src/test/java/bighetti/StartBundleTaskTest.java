package bighetti;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class StartBundleTaskTest extends AbstractBundleTaskTest
{
	@Before
	public void before()
	{
		task = new StartBundleTask();

		task.setId(111);
		task.setHost("localhost");
		task.setPassword("password");
		task.setUsername("username");

		TestUtils.setProject(task);
	}

	@Test
	public void executeWithFailExistingState()
	{
		task.setCommandOutput(Arrays.asList(LIST_HEADER, BUNDLE_1, BUNDLE_4));
		task.setFailExistingState(true);
		task.setId(444);
		task.setPreview(true);
		task.execute();

		task.setId(111);
		executeWithError();
	}
}
