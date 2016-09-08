package bighetti;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildLogger implements BuildListener
{
	private final Logger LOGGER = LoggerFactory.getLogger(BuildLogger.class);

	public void buildFinished(BuildEvent event)
	{
	}

	public void buildStarted(BuildEvent event)
	{
	}

	public void messageLogged(BuildEvent event)
	{
		String message;

		if (event.getTask() != null)
			message = "[" + event.getTask().getTaskName() + "] " + event.getMessage();
		else
			message = event.getMessage();

		if (message.endsWith("\r\n"))
			message = message.substring(0, message.length() - 2);
		else if (message.endsWith("\n"))
			message = message.substring(0, message.length() - 1);

		switch (event.getPriority())
		{
			case Project.MSG_DEBUG:
				LOGGER.debug(message);
				break;
			case Project.MSG_ERR:
				LOGGER.error(message);
				break;
			case Project.MSG_INFO:
				LOGGER.info(message);
				break;
			default:
				LOGGER.debug(message);
		}
	}

	public void targetFinished(BuildEvent event)
	{
	}

	public void targetStarted(BuildEvent event)
	{
	}

	public void taskFinished(BuildEvent event)
	{
	}

	public void taskStarted(BuildEvent event)
	{
	}
}
