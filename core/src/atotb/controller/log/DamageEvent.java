package atotb.controller.log;

import atotb.model.Unit;

/**
 * The event that a unit receives damage.
 * 
 * @author Ale Strooisma
 */
public class DamageEvent implements BattleEvent{

	private final Unit target;
	private final double damage;

	public DamageEvent(Unit target, double damage) {
		this.target = target;
		this.damage = damage;
	}

	public Unit getTarget() {
		return target;
	}

	public double getDamage() {
		return damage;
	}
	
	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitDamageEvent(this);
	}
	
}
