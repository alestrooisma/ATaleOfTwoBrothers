package atotb.controller.log;

import atotb.model.Unit;

/**
 * The event of a unit moving.
 *
 * @author Ale Strooisma
 */
public class MoveEvent extends AbstractMoveEvent {

	public MoveEvent(Unit unit, int fromX, int fromY, int destX, int destY, double distance) {
		super(unit, fromX, fromY, destX, destY, distance);
	}

	@Override
	public String toString() {
		return unit.getName() + " moved to " + destX + ", " + destY;
	}

	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitMoveEvent(this);
	}
}
