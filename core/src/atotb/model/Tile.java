package atotb.model;

import java.awt.Point;
import java.util.LinkedList;

/**
 * 
 * @author Ale Strooisma
 */
public class Tile {
	private final Point position;
	private final int terrain;
	private Unit unit;

	public Tile(Point position, int terrain) {
		this.position = position;
		this.terrain = terrain;
	}

	public Point getPosition() {
		return position;
	}

	public int getTerrain() {
		return terrain;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public void removeUnit() {
		setUnit(null);
	}

	public boolean isAccessible() {
		return getUnit() == null && terrain == 1; //TODO terrain == 1 is temporary!
	}
}
