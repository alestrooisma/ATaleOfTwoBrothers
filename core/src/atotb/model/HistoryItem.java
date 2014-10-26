package atotb.model;

import atotb.model.actions.Action;

/**
 * An interface for objects describing the history of a unit.
 *
 * @author Ale Strooisma
 */
public interface HistoryItem {

	public static class Move implements HistoryItem {
		private final double distance;

		public Move(double distance) {
			this.distance = distance;
		}

		public double getDistance() {
			return distance;
		}
	}

	public static class Dash implements HistoryItem {
	}

	public static class Charge implements HistoryItem {
	}

	public static class Ability implements HistoryItem {
		private final Action action;

		public Ability(Action action) {
			this.action = action;
		}

		public Action getAction() {
			return action;
		}
	}
}
