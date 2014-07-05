package atotb.model;

import java.util.ArrayList;

public class Army extends Element {

	private boolean defeated = false;
	private final ArrayList<Unit> units = new ArrayList<Unit>();

	public Army(String name, String summary, String description) {
		super(name, summary, description);
	}

	public boolean isDefeated() {
		return defeated;
	}

	public void setDefeated(boolean defeated) {
		this.defeated = defeated;
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
