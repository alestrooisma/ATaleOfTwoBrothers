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
		actor.setCurrentHealth(actor.getCurrentHealth() + amount);
		return Status.DONE;
	}

	@Override
	protected Status uponTargeting(Unit actor, Unit target) {
		return Status.NOT_ALLOWED;
	}
	
}
