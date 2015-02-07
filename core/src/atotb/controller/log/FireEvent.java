package atotb.controller.log;

import atotb.model.Unit;
import atotb.model.items.Weapon;

/**
 * An event where a unit fires a projectile at another unit.
 *
 * @author Ale Strooisma
 */
public class FireEvent implements BattleEvent {

	private final Unit user, target;
	private final Weapon weapon;

	public FireEvent(Unit user, Unit target, Weapon weapon) {
		this.user = user;
		this.target = target;
		this.weapon = weapon;
	}

	public Unit getUser() {
		return user;
	}

	public Unit getTarget() {
		return target;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitFireEvent(this);
	}
}
