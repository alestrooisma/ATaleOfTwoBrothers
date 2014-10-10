package atotb.model.items;

import atotb.model.Element;

/**
 * Base class for all items. That is weapons, armor, quest items etc.
 * 
 * @author Ale Strooisma
 */
public abstract class Item extends Element {

	public Item(String name, String summary, String description) {
		super(name, summary, description);
	}
	
}
