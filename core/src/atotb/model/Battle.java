package atotb.model;

/**
 *
 * @author Ale Strooisma
 */
public class Battle {

	private final BattleMap battleMap;
	private final Army[] armies;
	private int currentPlayer;
	private int turn;

	public Battle(BattleMap battleMap, Army player, Army opponent) {
		this.battleMap = battleMap;
		armies = new Army[2];
		armies[0] = player;
		armies[1] = opponent;
	}

	public BattleMap getBattleMap() {
		return battleMap;
	}

	public Army[] getArmies() {
		return armies;
	}

	public Army getArmy(int player) {
		return armies[player];
	}

	public Army getCurrentArmy() {
		return armies[currentPlayer];
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void nextPlayer() {
		currentPlayer = (currentPlayer + 1) % armies.length;
	}

	public int getTurn() {
		return turn;
	}

	public void incrementTurn() {
		turn++;
	}
}
