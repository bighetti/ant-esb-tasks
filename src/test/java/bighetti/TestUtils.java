package bighetti;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;

public class TestUtils
{
	public static Project setProject(ProjectComponent projectComponent)
	{
		return setProject(projectComponent, new Project());
	}

	public static Project setProject(ProjectComponent projectComponent, Project project)
	{
		BuildLogger logger = new BuildLogger();

		project.addBuildListener(logger);
		projectComponent.setProject(project);

		return project;
	}
}
