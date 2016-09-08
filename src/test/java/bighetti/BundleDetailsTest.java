package bighetti;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BundleDetailsTest
{
	private BundleDetails details;

	@Before
	public void before()
	{
		details = new BundleDetails(1, "name", "version");
	}

	@Test
	public void constructorInvalid()
	{
		details = new BundleDetails("invalid", true);

		Assert.assertEquals(0, details.getId());
		Assert.assertNull(details.getName());
		Assert.assertNull(details.getSpringState());
		Assert.assertNull(details.getState());
		Assert.assertNull(details.getVersion());
	}

	@Test
	public void findName()
	{
		Assert.assertEquals("Test Bundle", details.findName("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)"));
		Assert.assertNull(details.findName("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle"));
	}

	@Test
	public void findValue()
	{
		Assert.assertEquals("111", details.findValue("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)", 1));
		Assert.assertEquals("Active", details.findValue("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)", 2));
		Assert.assertEquals("", details.findValue("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)", 3));
		Assert.assertEquals("Started", details.findValue("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)", 4));
		Assert.assertEquals("60", details.findValue("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)", 5));

		Assert.assertNull(details.findValue("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle", 6));
		Assert.assertNull(details.findValue("[ 111] [Active     ] [            ] [Started] [   60 Test Bundle", 6));
	}

	@Test
	public void findVersion()
	{
		Assert.assertEquals("2.0.0", details.findVersion("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0)"));

		Assert.assertNull(details.findVersion("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle (2.0.0"));
		Assert.assertNull(details.findVersion("[ 111] [Active     ] [            ] [Started] [   60] Test Bundle"));
	}

	@Test
	public void isStarted()
	{
		Assert.assertFalse(details.isStarted());

		details.setState("Active");
		details.setSpringState("Started");
		Assert.assertTrue(details.isStarted());

		details.setSpringState("");
		Assert.assertTrue(details.isStarted());

		details.setSpringState("Failed");
		Assert.assertFalse(details.isStarted());

		details.setState("Installed");
		details.setSpringState("Started");
		Assert.assertFalse(details.isStarted());

		details.setState("Resolved");
		Assert.assertFalse(details.isStarted());
	}
}
