package atotb.model;

import atotb.model.items.Weapon;
import java.awt.Point;
import java.util.ArrayList;

public class Unit extends Element {

	// Core fields
	private Army army;
	private Point position;
	//
	// Stats
	private int maxHealth;
	private double speed;
	//
	// Abilities
	private ArrayList<Action> actions;
	//
	// Equipment
	private Weapon weapon;
	//
	// Current status
	private double currentHealth;
	private double movesRemaining;
	private boolean mayDash;
	private boolean mayAct;
	private Unit opponent;

	public Unit(String name, String summary, String description,
			Army army, int maxHealth, double speed) {
		//TODO how large pre-allocation size of actions?
		//TODO 9-element quick-selection list and a large full list?
		this(name, summary, description, army, maxHealth, speed, new ArrayList<Action>(9));
	}

	public Unit(String name, String summary, String description,
			Army army, int maxHealth, double speed, ArrayList<Action> actions) {
		super(name, summary, description);
		this.army = army;
		this.maxHealth = maxHealth;
		this.speed = speed;
		this.actions = actions;
	}

	// Basic getters and setters
	// 
	public Army getArmy() {
		return army;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y;
	}

	// Stat getters and setters
	// 
	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double movesPerTurn) {
		this.speed = movesPerTurn;
	}

	public double getDashDistance() {
		return speed;
	}

	// Ability getters and setters
	// 
	public ArrayList<Action> getActions() {
		return actions;
	}

	public Action getAction(int i) {
		if (i > actions.size()) {
			return null;
		}
		return actions.get(i - 1);
	}

	public boolean addAction(Action a) {
		return actions.add(a);
	}

	public boolean removeAction(Action a) {
		return actions.remove(a);
	}

	// Equipment getters and setters
	// 
	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	// Status getters and setters
	// 
	public double getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(double currentHealth) {
		this.currentHealth = currentHealth;
	}

	public void applyDamage(double damage) {
		currentHealth -= damage;
		if (currentHealth < 0) {
			currentHealth = 0;
		}
	}

	public double getMovesRemaining() {
		return movesRemaining;
	}

	public void setMovesRemaining(double movesRemaining) {
		this.movesRemaining = movesRemaining;
	}

	public void reduceMoves(double moves) {
		movesRemaining -= moves;
	}

	public boolean mayDash() {
		return mayDash;
	}

	public void setMayDash(boolean mayDash) {
		this.mayDash = mayDash;
	}

	public void setHasDashed() {
		setMayDash(false);
	}

	public boolean mayAct() {
		return mayAct;
	}

	public void setMayAct(boolean mayAct) {
		this.mayAct = mayAct;
	}

	public void setHasActed() {
		setMayAct(false);
	}

	public void reset() {
		movesRemaining = speed;
		mayDash = true;
		mayAct = true;
	}

	public void setLockedIntoCombat(Unit opponent) {
		this.opponent = opponent;
	}

	public Unit getLockedIntoCombat() {
		return opponent;
	}

	// Queries
	// 
	public boolean isEnemy(Unit unit) {
		return getArmy().isEnemy(unit);
	}

	public boolean isEnemy(Army army) {
		return getArmy().isEnemy(army);
	}

	public boolean isAlive() {
		return currentHealth > 0;
	}

	// Factory functions
	// 
	public static Unit createUnit(String name, String summary, String description, Army army, int maxHealth, double speed) {
		Unit unit = new Unit(name, summary, description, army, maxHealth, speed);
		unit.setCurrentHealth(maxHealth);
		army.addUnit(unit);
		return unit;
	}

	public static Unit createUnit(String name, String summary, String description, Army army, int maxHealth, double speed,
			Point pos, BattleMap map) {
		Unit unit = createUnit(name, summary, description, army, maxHealth, speed);
		unit.setPosition(pos);
		map.getTile(pos).setUnit(unit);
		return unit;
	}
}
