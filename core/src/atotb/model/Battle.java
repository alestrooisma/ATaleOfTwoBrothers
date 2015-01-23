package atotb.model;

/**
 * The model component that represents the part of the game state that is needed 
 * during a battle.
 * 
 * @author Ale Strooisma
 */
public class Battle {

	private final BattleMap battleMap;
	private final Army[] armies;
	private int currentPlayer;
	private int turn;

	public Battle(BattleMap battleMap, Army player, Army opponent) {
		this(battleMap, player, opponent, -1, 0);
	}

	public Battle(BattleMap battleMap, Army player, Army opponent,
			int currentPlayer, int turn) {
		this.battleMap = battleMap;
		armies = new Army[2];
		player.setIndex(0);
		armies[player.getIndex()] = player;
		opponent.setIndex(1);
		armies[opponent.getIndex()] = opponent;
		this.currentPlayer = currentPlayer;
		this.turn = turn;
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

	public int otherPlayer(Army army) {
		for (int i = 0; i < armies.length; i++) {
			if (army != armies[i]) {
				return i;
			}
		}
		return 0;
	}

	public int otherPlayer(int player) {
		return (player + 1) % armies.length;
	}
}
