package atotb.model;

/**
 * The central class for the model of A Tale of Two Brothers.
 *
 * @author ale
 */
public class Model {

	// General
	private Army[] armies;
	//
	// Battle
	private BattleMap battleMap;
	private int currentPlayer;
	private int turn;

	public Model(Army playerParty) {
		armies = new Army[2];
		armies[0] = playerParty;
	}

	public void startBattle(BattleMap map, Army opponent) {
		battleMap = map;
		armies[1] = opponent;
	}

	public BattleMap getBattleMap() {
		return battleMap;
	}

	public Army[] getArmies() {
		return armies;
	}

	public Army getPlayerParty() {
		return armies[0];
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

	public void incrementCurrentPlayer() {
//		int p = currentPlayer;
//		do {
		currentPlayer = (currentPlayer + 1) % armies.length;
//			if (currentPlayer == p) {
//				// To prevent endless loop
//				currentPlayer = p;
//			}
//		} while (getCurrentArmy().isDefeated());
	}

	public int getTurn() {
		return turn;
	}

	public void incrementTurn() {
		turn++;
	}
}
