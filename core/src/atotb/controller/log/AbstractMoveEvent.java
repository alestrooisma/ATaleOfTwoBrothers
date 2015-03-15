package atotb.controller.log;

import atotb.model.Unit;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

/**
 * A superclass for all events involving movement.
 *
 * @author Ale Strooisma
 */
public abstract class AbstractMoveEvent implements BattleEvent {

	private final Unit unit;
	private final int fromX, fromY;
	private final Array<Point> path;
	private final double distance;

	public AbstractMoveEvent(Unit unit, int fromX, int fromY, Array<Point> path, double distance) {
		this.unit = unit;
		this.fromX = fromX;
		this.fromY = fromY;
		this.path = path;
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

	public int getDestinationX() {
		return path.peek().x;
	}

	public int getDestinationY() {
		return path.peek().y;
	}

	public Array<Point> getPath() {
		return path;
	}

	public double getDistance() {
		return distance;
	}
}
