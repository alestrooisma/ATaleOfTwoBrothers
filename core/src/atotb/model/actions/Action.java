package atotb.model.actions;

import atotb.model.Element;
import atotb.model.HistoryItem;
import atotb.model.Unit;

public abstract class Action extends Element {

	public static final byte SELF = 1;
	public static final byte FRIENDLY = 2;
	public static final byte ENEMY = 4;
	private final byte targetType;
	private String message = null;

	public Action(String name, String summary, String description, byte targetType) {
		super(name, summary, description);
		this.targetType = targetType;
	}

	public void execute(Unit actor) {
		execute(actor, actor);
	}

	public abstract void execute(Unit actor, Unit target);

	public boolean isApplicableTarget(byte type) {
		return (type & targetType) != 0;
	}

	public boolean isApplicableTarget(Unit actor, Unit target) {
		boolean enemy = actor.isEnemy(target);
		return (actor == target && (SELF & targetType) != 0)
				|| (!enemy && (FRIENDLY & targetType) != 0)
				|| (enemy && (ENEMY & targetType) != 0);
	}

	public boolean isAllowed(Unit actor) {
		return !actor.isLockedIntoCombat();
	}

	public boolean isAllowed(Unit actor, Unit target) {
		return isAllowed(actor) 
				&& isApplicableTarget(actor, target) 
				&& checkHistory(actor);
	}

	public boolean checkHistory(Unit actor) {
		HistoryItem item;
		boolean allowed = true;
		actor.getHistory().iter();
		while (allowed && (item = actor.getHistory().next()) != null) {
			allowed = checkHistoryItem(item);
		}
		return allowed;
	}

	public boolean checkHistoryItem(HistoryItem item) {
		return item instanceof HistoryItem.Move;
	}

	public boolean isImmediate() {
		return targetType == SELF;
	}

	public String getSelectMessage(Unit actor) {
		return "Please select a target.";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
