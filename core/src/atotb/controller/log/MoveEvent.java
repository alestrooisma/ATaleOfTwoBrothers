package atotb.controller.log;

import atotb.model.Unit;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

/**
 * The event of a unit moving.
 *
 * @author Ale Strooisma
 */
public class MoveEvent extends AbstractMoveEvent {

	public MoveEvent(Unit unit, Array<Point> path, double distance) {
		super(unit, path, distance);
	}

	@Override
	public String toString() {
		return getUnit().getName() + " moved to " + getDestinationX() + ", " + getDestinationY();
	}

	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitMoveEvent(this);
	}
}
