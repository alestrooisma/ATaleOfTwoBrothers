package atotb.model;

/**
 * The base class for all game elements. Provides a name, description and 
 * summary.
 * 
 * @author Ale Strooisma
 */
public abstract class Element {
	private String name;
	private String summary;
	private String description;

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
