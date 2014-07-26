package atotb.util;

/**
 *
 * @author Ale Strooisma
 */
public interface Enum {

	public enum GameState {
		START_TURN, END_TURN, END_BATTLE;
	}
	
	public enum Direction {

		N(1, -1), NE(1, 0), E(1, 1), SE(0, 1),
		S(-1, 1), SW(-1, 0), W(-1, -1), NW(0, -1);

		private int dx, dy;

		private Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public int getX(int x) {
			return x + dx;
		}

		public int getY(int y) {
			return y + dy;
		}
	}
}
