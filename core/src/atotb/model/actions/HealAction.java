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
	public void execute(Unit actor, Unit target) {
		double actualAmount = amount;
		if (actor.getCurrentHealth() + actualAmount > actor.getMaxHealth()) {
			actor.setCurrentHealth(actor.getMaxHealth());
			actualAmount = actor.getMaxHealth() - actor.getCurrentHealth();
		} else {
			actor.setCurrentHealth(actor.getCurrentHealth() + actualAmount);
		}
		setMessage(actor.getName() + " healed " + actualAmount + " HP.");
	}

	@Override
	public boolean isAllowed(Unit actor) {
		return super.isAllowed(actor) && actor.getCurrentHealth() < actor.getMaxHealth();
	}
	
}
