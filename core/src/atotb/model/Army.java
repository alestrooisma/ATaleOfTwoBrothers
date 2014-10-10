package atotb.model;

import java.util.ArrayList;

/**
 * An army is a collection of units and often represents a player as a whole.
 * 
 * @author Ale Strooisma
 */
public class Army extends Element {

	private final ArrayList<Unit> units = new ArrayList<Unit>();

	public Army(String name, String summary, String description) {
		super(name, summary, description);
	}
	
	public ArrayList<Unit> getUnits() {
		return units;
	}
	
	public void addUnit(Unit unit) {
		units.add(unit);
	}
	
	public void removeUnit(Unit unit) {
		units.remove(unit);
	}
	
	public boolean isEnemy(Army army) {
		//TODO allies
		return army != this;
	}
	
	public boolean isEnemy(Unit unit) {
		return isEnemy(unit.getArmy());
	}
}
