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
	private final Array<Point> path;
	private final double distance;

	public AbstractMoveEvent(Unit unit, Array<Point> path, double distance) {
		this.unit = unit;
		this.path = path;
		this.distance = distance;
	}

	public Unit getUnit() {
		return unit;
	}

	public int getFromX() {
		return path.first().x;
	}

	public int getFromY() {
		return path.first().y;
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
