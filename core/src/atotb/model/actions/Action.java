package atotb.model.actions;

import atotb.model.Element;
import atotb.model.Unit;

public abstract class Action extends Element {
	public static final byte SELF = 1;
	public static final byte FRIENDLY = 2;
	public static final byte ENEMY = 4;
	private final byte targetType;

	public Action(String name, String summary, String description, byte targetType) {
		super(name, summary, description);
		this.targetType = targetType;
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

	public boolean isSelfTargetingOnly() {
		return SELF == targetType;
	}

	public boolean isApplicableTarget(byte type) {
		return (type & targetType) != 0;
	}

	public boolean isApplicableTarget(Unit actor, Unit target, boolean friendly) {
		return (actor == target && (SELF & targetType) != 0)
				|| (friendly && (FRIENDLY & targetType) != 0)
				|| (actor == target && (ENEMY & targetType) != 0);
	}
	
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
