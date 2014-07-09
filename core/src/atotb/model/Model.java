package atotb.model;

/**
 * The central class for the model of A Tale of Two Brothers.
 *
 * @author Ale Strooisma
 */
public class Model {

	private final Army playerParty;
	private Battle battle;

	public Model(Army playerParty) {
		this.playerParty = playerParty;
	}

	public Army getPlayerParty() {
		return playerParty;
	}

	public Battle getBattle() {
		return battle;
	}

	public void setBattle(Battle battle) {
		this.battle = battle;
	}
}
