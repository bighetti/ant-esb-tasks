package bighetti;

/**
 * The details of a bundle.
 */
public class BundleDetails
{
	/** The bundle id */
	private int id;

	/** The bundle name */
	private String name;

	/** The Spring state */
	private String springState;

	/** The bundle state */
	private String state;

	/** The bundle version */
	private String version;

	/**
	 * The default constructor.
	 */
	public BundleDetails()
	{
	}

	/**
	 * The minimal constructor.
	 *
	 * @param id The bundle id.
	 * @param name The bundle name.
	 * @param version The bundle version.
	 */
	public BundleDetails(int id, String name, String version)
	{
		this.id = id;
		this.name = name;
		this.version = version;
	}

	/**
	 * The constructor that parses the karaf "list" command output.
	 *
	 * @param line A line from the karaf "list" command output.
	 */
	public BundleDetails(String line)
	{
		try
		{
			id = Integer.parseInt(findValue(line, 1));
		}
		catch (Exception e)
		{
		}

		name = findName(line);
		springState = findValue(line, 4);
		state = findValue(line, 2);
		version = findVersion(line);
	}

	/**
	 * The constructor that parses either the karaf "list" command output or a saved toString() output.
	 *
	 * @param line A line containing bundle details.
	 * @param karafOutput If the line is from the karaf "list" command.
	 */
	public BundleDetails(String line, boolean karafOutput)
	{
		if (karafOutput)
		{
			try
			{
				id = Integer.parseInt(findValue(line, 1));
			}
			catch (Exception e)
			{
			}

			name = findName(line);
			springState = findValue(line, 4);
			state = findValue(line, 2);
			version = findVersion(line);
		}
		else
		{
			int idIndex = line.indexOf(",");
			int stateIndex = line.indexOf(",", idIndex + 1);
			int sprintStateIndex = line.indexOf(",", stateIndex + 1);
			int nameIndex = line.indexOf(",", sprintStateIndex + 1);

			try
			{
				id = Integer.parseInt(line.substring(0, idIndex));
			}
			catch (Exception e)
			{
			}

			name = line.substring(sprintStateIndex + 1, nameIndex);
			springState = line.substring(stateIndex + 1, sprintStateIndex);
			state = line.substring(idIndex + 1, stateIndex);
			version = line.substring(nameIndex + 1);
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		BundleDetails that = (BundleDetails) o;

		if (id != that.id)
			return false;

		if (springState == null && that.springState != null)
			return false;

		if (!springState.equals(that.springState))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id;

		result = 31 * result + (springState != null ? springState.hashCode() : 0);

		return result;
	}

	/**
	 * Find the bundle name from the karaf "list" command output.
	 *
	 * @param line A line from the karaf "list" command output.
	 * @return The bundle name.
	 */
	protected String findName(String line)
	{
		int begin = StringUtils.findIndex(line, "]", "]", 5);
		int end = line.lastIndexOf("(");

		if (begin == -1 || end == -1)
			return null;

		return line.substring(begin + 2, end - 1);
	}

	/**
	 * Find a value from the karaf "list" command output.
	 *
	 * @param line A line from the karaf "list" command output.
	 * @param occurrence The position of the value in the line.
	 * @return The value.
	 */
	protected String findValue(String line, int occurrence)
	{
		int begin = StringUtils.findIndex(line, "[", "]", occurrence);

		if (begin == -1)
			return null;

		int end = line.indexOf("]", begin);

		return line.substring(begin + 1, end).replace(" ", "");
	}

	/**
	 * Find the bundle version from the karaf "list" command output.
	 *
	 * @param line A line from the karaf "list" command output.
	 * @return The bundle version.
	 */
	protected String findVersion(String line)
	{
		int begin = line.lastIndexOf("(");
		int end = line.lastIndexOf(")");

		if (begin == -1 || end == -1)
			return null;

		return line.substring(begin + 1, end);
	}

	/**
	 * The bundle display name.
	 *
	 * @return The bundle display name.
	 */
	public String getDisplayName()
	{
		return "[" + id + "] " + name + " (" + version + ")";
	}

	/**
	 * Get the bundle id.
	 *
	 * @return The bundle id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Get the bundle name.
	 *
	 * @return The bundle name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the Spring state.
	 *
	 * @return The Spring state.
	 */
	public String getSpringState()
	{
		return springState;
	}

	/**
	 * Get the bundle state.
	 *
	 * @return The bundle state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * Get the bundle version.
	 *
	 * @return The bundle version.
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Check if the bundle is started.
	 *
	 * @return If the bundle is started.
	 */
	public boolean isStarted()
	{
		return "Active".equals(state) && ("Started".equals(springState) || "".equals(springState));
	}

	/**
	 * Check if the bundle is stopped.
	 *
	 * @return If the bundle is stopped.
	 */
	public boolean isStopped()
	{
		return !"Active".equals(state);
	}

	/**
	 * Set the bundle id.
	 *
	 * @param id The bundle id.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Set the bundle name.
	 *
	 * @param name The bundle name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Set the Spring state.
	 *
	 * @param springState The Spring state.
	 */
	public void setSpringState(String springState)
	{
		this.springState = springState;
	}

	/**
	 * Set the bundle state.
	 *
	 * @param state The bundle state.
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 * Set the bundle version.
	 *
	 * @param version The bundle version.
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append(id);
		builder.append(",");
		builder.append(state);
		builder.append(",");
		builder.append(springState);
		builder.append(",");
		builder.append(name);
		builder.append(",");
		builder.append(version);

		return builder.toString();
	}
}
