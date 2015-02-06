package atotb.controller.log;

import atotb.model.Unit;

/**
 * A superclass for all events involving movement.
 *
 * @author Ale Strooisma
 */
public abstract class AbstractMoveEvent implements BattleEvent {

	protected final Unit unit;
	protected final int fromX, fromY, destX, destY;
	protected final double distance;

	public AbstractMoveEvent(Unit unit, int fromX, int fromY, int destX, int destY, double distance) {
		this.unit = unit;
		this.fromX = fromX;
		this.fromY = fromY;
		this.destX = destX;
		this.destY = destY;
		this.distance = distance;
	}

	public Unit getUnit() {
		return unit;
	}

	public int getFromX() {
		return fromX;
	}

	public int getFromY() {
		return fromY;
	}

	public int getDestX() {
		return destX;
	}

	public int getDestY() {
		return destY;
	}

	public double getDistance() {
		return distance;
	}
}
