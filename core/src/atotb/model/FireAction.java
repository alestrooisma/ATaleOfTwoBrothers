package atotb.model;

public class FireAction extends Action {

	public FireAction() {
		super("Fire", "Fire main weapon.", "The character fires its main weapon.");
	}

	@Override
	public Status uponSelection(Unit actor) {
		System.out.println("Please choose a target!");
		return Status.WAITING_FOR_TARGET;
	}

	@Override
	public Status uponTargeting(Unit actor, Unit target) {
		System.out.println("Firing at " + target.getName() + "!");
		target.setCurrentHealth(target.getCurrentHealth() - 1);
		return Status.DONE;
	}

	@Override
	public boolean isAllowed(Unit actor, Unit target) {
		return super.isAllowed(actor) && actor.isEnemy(target); //TODO range
	}
}
