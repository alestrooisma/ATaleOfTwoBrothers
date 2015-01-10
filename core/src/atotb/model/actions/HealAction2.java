package atotb.model.actions;

import atotb.model.Unit;

/**
 * A simple action where the user heals itself.
 * 
 * @author Ale Strooisma
 */
public class HealAction2 extends Action {
	private final double amount;

	public HealAction2(double amount) {
		super("Heal", "The user heals an ally (gain " + amount + " HP)", 
				"The target gains " + amount + " HP, limited to its maximum hit points.",
				(byte)(SELF | FRIENDLY));
		this.amount = amount;
	}

	@Override
	public void execute(Unit actor, Unit target) {
		double actualAmount = amount;
		if (target.getCurrentHealth() + actualAmount > target.getMaxHealth()) {
			target.setCurrentHealth(target.getMaxHealth());
			actualAmount = target.getMaxHealth() - target.getCurrentHealth();
		} else {
			target.setCurrentHealth(target.getCurrentHealth() + actualAmount);
		}
		setMessage(target.getName() + " healed " + actualAmount + " HP.");
	}

	@Override
	public boolean isAllowed(Unit actor, Unit target) {
		return super.isAllowed(actor, target) && target.getCurrentHealth() < target.getMaxHealth();
	}
	
}
