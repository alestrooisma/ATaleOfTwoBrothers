package atotb.controller.log;

import atotb.model.Unit;

/**
 * The event of a unit dying.
 *
 * @author Ale Strooisma
 */
public class DeathEvent implements BattleEvent {

	private final Unit unit;

	public DeathEvent(Unit unit) {
		this.unit = unit;
	}

	public Unit getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return unit.getName() + " died!";
	}

	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitDeathEvent(this);
	}
}
