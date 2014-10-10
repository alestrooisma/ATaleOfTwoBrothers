package atotb.model;

/**
 * The base class for all game elements. Provides a name, description and 
 * summary.
 * 
 * @author Ale Strooisma
 */
public abstract class Element {
	private final String name;
	private final String summary;
	private final String description;

	/**
	 * Creates a new element.
	 * 
	 * @param name The name of the element
	 * @param summary A one-line summary of the description
	 * @param description A full description of the element
	 */
	public Element(String name, String summary, String description) {
		this.name = name;
		this.summary = summary;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getSummary() {
		return summary;
	}
}
