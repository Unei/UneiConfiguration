package me.unei.configuration.api;

import java.util.List;

public interface IFlatCSVConfiguration extends IFlatConfiguration {

	/**
	 * Returns a {@link List List} of lines located at the top of the file,
	 * which are not part of the data, and that contains the different keys.
	 *
	 * @return the {@link List List} of lines at the top of the file
	 */
	public List<String> getHeaderLine();

	/**
	 * Resets the {@link List List} of lines located at the top of the file,
	 * which are not part of the data, and that contains the different keys,
	 * to an empty {@link List List}.
	 */
	public void resetHeaderLine(); //TODO Javadoc - Default Header is not empty !!

	/**
	 * Sets the value relative to the specified key to the specified
	 * {@link String String} {@link List List}.
	 *
	 * @param key the key to give to the specified value
	 * @param value the {@link String String} {@link List List} to set relative to the specified key
	 */
	public void setList(String key, List<String> value);

	/**
	 * Returns the value relative to the specified key as {@link String String} {@link List List},
	 * if possible.
	 *
	 * @param key the key of the value to retrieve
	 * @return the {@link String String} {@link List List} relative to the specified key
	 */
	public List<String> getList(String key);
}
