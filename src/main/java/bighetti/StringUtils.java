package bighetti;

public class StringUtils
{
	/**
	 * Find an index in a repeating pattern, e.g. [one][two][three].<br/><br/>
	 *
	 * If begin is "[" and end is "]" then occurrence 2 would be the index where "[two]" begins.
	 *
	 * @param line The entire string.
	 * @param begin The string that marks the beginning of a sequence.
	 * @param end The string that marks the ending of a sequence.
	 * @param occurrence The occurrence to find, starting from one.
	 * @return The index.
	 */
	public static int findIndex(String line, String begin, String end, int occurrence)
	{
		int index = 0;

		while (occurrence > 1)
		{
			index = line.indexOf(end, index) + 1;

			if (index == 0)
				return -1;

			occurrence--;
		}

		return line.indexOf(begin, index);
	}

	/**
	 * Check if the string is null or empty.
	 *
	 * @param value The string value.
	 * @return If the string is null or empty.
	 */
	public static boolean isEmpty(String value)
	{
		return value == null || value.length() == 0;
	}

	/**
	 * Check if the string array is null or empty.
	 *
	 * @param value The string array.
	 * @return If the string array is null or empty.
	 */
	public static boolean isEmpty(String[] value)
	{
		return value == null || value.length == 0;
	}

	/**
	 * Check if the string is not null and not empty.
	 *
	 * @param value The string value.
	 * @return If the string is not null and not empty.
	 */
	public static boolean isNotEmpty(String value)
	{
		return !isEmpty(value);
	}
}
