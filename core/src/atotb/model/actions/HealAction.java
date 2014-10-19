package atotb.model.actions;

import atotb.model.Unit;

/**
 * A simple action where the user heals itself.
 * 
 * @author Ale Strooisma
 */
public class HealAction extends Action {
	private final double amount;

	public HealAction(double amount) {
		super("Heal", "The user heals itself (gain " + amount + " HP)", 
				"The user gains " + amount + " HP, limited to his maximum hit points.",
				SELF);
		this.amount = amount;
	}


	@Override
	protected Status uponSelection(Unit actor) {
		double actualAmount = amount;
		if (actor.getCurrentHealth() >= actor.getMaxHealth()) {
			setMessage(actor.getName() + " is already at full health.");
			return Status.NOT_ALLOWED;
		} else if (actor.getCurrentHealth() + actualAmount > actor.getMaxHealth()) {
			actor.setCurrentHealth(actor.getMaxHealth());
			actualAmount = actor.getMaxHealth() - actor.getCurrentHealth();
		} else {
			actor.setCurrentHealth(actor.getCurrentHealth() + actualAmount);
		}
		setMessage(actor.getName() + " healed " + actualAmount + " HP.");
		return Status.DONE;
	}

	@Override
	protected Status uponTargeting(Unit actor, Unit target) {
		return Status.NOT_ALLOWED;
	}
	
}
