package atotb.model;

public abstract class Action extends Element {

	public Action(String name, String summary, String description) {
		super(name, summary, description);
	}

	public Status select(Unit unit) {
		if (isAllowed(unit)) {
			return uponSelection(unit);
		} else {
			return Status.NOT_ALLOWED;
		}
	}

	public Status target(Unit actor, Unit target) {
		if (isAllowed(actor)) {
			return uponTargeting(actor, target);
		} else {
			return Status.NOT_ALLOWED;
		}
	}
	
	protected abstract Status uponSelection(Unit actor);
	
	protected abstract Status uponTargeting(Unit actor, Unit target);
	
	public boolean isAllowed(Unit actor) {
		return true;
	}
	
	public boolean isAllowed(Unit actor, Unit target) {
		return isAllowed(actor);
	}
	
	public enum Status {
		NOT_ALLOWED, WAITING_FOR_TARGET, DONE;
	}
}
