package atotb;

import atotb.controller.*;
import atotb.model.*;
import atotb.model.items.*;
import atotb.util.MessageLog;
import atotb.util.PathFinder;
import atotb.view.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import java.awt.Point;
import java.util.ArrayList;

public class TwoBrothersGame extends Game {

	// Model
	private Model model;
	private int selectedUnit = -1;
	//
	// View
	private BattleScreen battleScreen;
	//
	// Controller
	private InputMultiplexer inputHandlers;
	private InputAdapter battleHandler;
	private PathFinder pathfinder;
	private MessageLog log;
	//
	// Shared resources
	private SpriteBatch batch;
	private BitmapFont font;

	// Initialization
	//
	@Override
	public void create() {
		setUpModel();
		setUpView();
		setUpController();

		startBattle();
	}

	private void setUpModel() {
		Unit u;
		Action defense = new Action("Defensive stance", "", "") {

			@Override
			protected Action.Status uponSelection(Unit actor) {
				return Action.Status.DONE;
			}

			@Override
			protected Action.Status uponTargeting(Unit actor, Unit target) {
				return Action.Status.NOT_ALLOWED;
			}
		};

		Army player = new Army("player1", "Your army.", "The army you command.");
		u = Unit.createUnit("Dale", "The oldest of the two brothers.", "Dale picked up a pitchfork, lacking a better weapon.", player, 10, 3.5);
		u.setWeapon(new MeleeWeapon("Pitchfork", "", "", 2));
		u.addAction(defense);
		u = Unit.createUnit("Harryn", "The younger of the two brothers.", "Harryn is decent with a bow.", player, 10, 3.5);
		u.setWeapon(new RangedWeapon("Old hunting bow", "", "", 6));

		model = new Model(player);
	}

	private void setUpView() {
		Gdx.graphics.setVSync(true);
		// Create one sprite batch and bitmap font to be used throughout the 
		// entire program.
		batch = new SpriteBatch();
		font = new BitmapFont();

		// Load resources
		Resources.loadResources();

		// Create and set the battle screen
		battleScreen = new BattleScreen(this, batch, font);
	}

	private void setUpController() {
		// Create message log
		log = new MessageLog(10);

		// Set up input listeners
		inputHandlers = new InputMultiplexer();
		inputHandlers.addProcessor(new MainInputHandler(this));
		Gdx.input.setInputProcessor(inputHandlers);
		battleHandler = new BattleInputHandler(this, battleScreen);
	}

	// Getters
	//
	public Model getModel() {
		return model;
	}

	public MessageLog getLog() {
		return log;
	}

	public Unit getSelectedUnit() {
		if (selectedUnit < 0) {
			return null;
		}
		return model.getBattle().getCurrentArmy().getUnits().get(selectedUnit);
	}

	public PathFinder getPathfinder() {
		return pathfinder;
	}

	// Game state modifiers
	//
	private void startBattle() {

		// Create enemy army
		Army enemy = new Army("Wolves", "A pack of wolves.", "Uh, oh, it seems you've encountered a pack of ferocious wolves!");
		Weapon fangs = new MeleeWeapon("Fangs", "", "", 4);
		Unit w1 = Unit.createUnit("Wolf", "A ferocious wolf!", "A hungry wolf is very dangerous.", enemy, 5, 5);
		w1.setWeapon(fangs);
		Unit w2 = Unit.createUnit("Wolf", "A ferocious wolf!", "A hungry wolf is very dangerous.", enemy, 5, 5);
		w2.setWeapon(fangs);
		Unit w3 = Unit.createUnit("Wolf", "A ferocious wolf!", "A hungry wolf is very dangerous.", enemy, 5, 5);
		w3.setWeapon(fangs);

		// Load the map
		TiledMap tileMap = new TmxMapLoader().load("maps/testmap.tmx");
		MapProperties prop = tileMap.getProperties();
		int mapWidth = prop.get("width", Integer.class);
		int mapHeight = prop.get("height", Integer.class);
		TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get(0);
		
		BattleMap battleMap = new BattleMap(mapWidth, mapHeight);
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				int t; //TODO temporary stuff
				prop = layer.getCell(y, x).getTile().getProperties();
				if (prop.containsKey("accessible")) {
					t = 1;
				} else {
					t = 0;
				}
				battleMap.setTile(new Tile(new Point(x, y), t));
			}
		}
		
		battleMap.addUnit(w1, 4, 9);
		battleMap.addUnit(w2, 11, 2);
		battleMap.addUnit(w3, 13, 5);
		battleMap.addUnit(model.getPlayerParty().getUnits().get(0), 9, 11);
		battleMap.addUnit(model.getPlayerParty().getUnits().get(1), 6, 14);

		model.setBattle(new Battle(battleMap, model.getPlayerParty(), enemy));
		pathfinder = new PathFinder(model.getBattle().getBattleMap());
		selectedUnit = 0;
		battleScreen.setMap(tileMap);
		setScreen(battleScreen);
		inputHandlers.addProcessor(battleHandler);
		startTurn(model.getBattle().getCurrentArmy());
	}

	private void startTurn(Army army) {
		if (model.getBattle().getCurrentPlayer() == 0) {
			model.getBattle().incrementTurn();
		}

		// Reset unit turn status
		for (Unit u : army.getUnits()) {
			u.reset();
		}

		// Resolve combat
		for (Unit u : army.getUnits()) {
			if (u.isLockedIntoCombat()) {
				resolveCombat(u, u.getLockedIntoCombat());
			}
		}

		// Select unit
		selectedUnit = -1;
		nextUnit();
	}

	public void endTurn() {
		// Finalize turn
		deselectUnit();

		// Start next player's turn
		model.getBattle().nextPlayer();
		startTurn(model.getBattle().getCurrentArmy());
	}

	public void selectUnit(Unit unit) {
		selectUnit(model.getBattle().getCurrentArmy().getUnits().indexOf(unit));
	}

	public void selectUnit(int number) {
		selectedUnit = number;
		preparePathFinder();
	}

	private void preparePathFinder() {
		Unit u = getSelectedUnit();
		double maxMoves = u.getMovesRemaining();
		if (u.mayDash()) {
			maxMoves += u.getDashDistance();
		}
		pathfinder.calculateDistancesFrom(
				u.getPosition().x, u.getPosition().y, maxMoves);
	}

	public void deselectUnit() {
		selectedUnit = -1;
	}

	public void previousUnit() {
		cycleUnit(-1);
	}

	public void nextUnit() {
		cycleUnit(1);
	}

	public void cycleUnit(int step) {
		ArrayList<Unit> units = model.getBattle().getCurrentArmy().getUnits();
		int unit = selectedUnit;
		do {
			unit = ((unit + step) % units.size() + units.size()) % units.size();
		} while (!units.get(unit).isAlive());
		selectUnit(unit);
	}

	public void moveUnit(Unit u, int destX, int destY) {
		// Calculate distance
		double distance = pathfinder.getDistanceTo_safe(destX, destY);

		// Check range and move if possible
		if (distance <= u.getMovesRemaining()) {
			actuallyMoveUnit(u, destX, destY);
			u.reduceMoves(distance);
		} else if (u.mayDash()
				&& distance <= u.getMovesRemaining() + u.getDashDistance()) {
			actuallyMoveUnit(u, destX, destY);
			u.setMovesRemaining(0);
			u.setHasDashed();
			u.setMayAct(false); //TODO temp
			log.push("Dashing!");
		}
	}

	public void actuallyMoveUnit(Unit u, int destX, int destY) {
		BattleMap map = model.getBattle().getBattleMap();
		map.getTile(u.getPosition()).removeUnit();
		map.getTile(destX, destY).setUnit(u);
		u.setPosition(destX, destY);

		// Recalculate movement range
		preparePathFinder();
	}

	public void targetUnit(Unit user, Unit target) {
		//TODO check for action, otherwise use main weapon.

		if (target.isLockedIntoCombat()) {
			log.push(target.getName() + " is locked into combat - can't attack.");
			return;
		}

		boolean acted = false;
		Weapon w = user.getWeapon();

		if (w != null) {
			if (user.mayAct()) {
				if (w instanceof RangedWeapon) {
					acted = fireRangedWeapon(user, target, w);
				} else if (w instanceof MeleeWeapon) {
					acted = charge(user, target, w);
				}
			} else {
				log.push(user.getName() + " may not act anymore.");
			}
		} else {
			log.push(user.getName()
					+ " is targeting " + target.getName()
					+ " but has no weapon!");
		}
		if (acted) {
			user.setMovesRemaining(0);
			user.setMayAct(false);
			user.setMayDash(false);
		}
	}

	private boolean fireRangedWeapon(Unit user, Unit target, Weapon weapon) {
		log.push(user.getName() + " fires at " + target.getName() + " - " + weapon.getPower() + " damage!");
		applyDamage(target, weapon.getPower());
		if (target.getCurrentHealth() > 0) {
			log.push(target.getName() + "'s remaining health = " + target.getCurrentHealth());
		} else {
			log.push(user.getName() + " killed " + target.getName() + "!");
			model.getBattle().getBattleMap().getTile(target.getPosition()).removeUnit();
		}
		return true;
	}

	private boolean charge(Unit user, Unit target, Weapon weapon) {
		int tx = target.getPosition().x;
		int ty = target.getPosition().y;
		double nw = pathfinder.getDistanceTo_safe(tx, ty - 1);
		double ne = pathfinder.getDistanceTo_safe(tx + 1, ty);
		double se = pathfinder.getDistanceTo_safe(tx, ty + 1);
		double sw = pathfinder.getDistanceTo_safe(tx - 1, ty);

		// Find best target location
		int dir = 1;
		double d = nw;
		if (ne < d) {
			dir = 2;
			d = ne;
		}
		if (se < d) {
			dir = 3;
			d = se;
		}
		if (sw < d) {
			dir = 4;
			d = sw;
		}

		// Check range
		double maxMoves = user.getMovesRemaining();
		if (user.mayDash()) {
			maxMoves += user.getDashDistance();
		}
		if (d > maxMoves) {
			log.push(target.getName() + " is out of charging range for "
					+ user.getName());
			return false;
		}

		// In range, so move there
		switch (dir) {
			case 1: //nw
				actuallyMoveUnit(user, tx, ty - 1);
				break;
			case 2: //ne
				actuallyMoveUnit(user, tx + 1, ty);
				break;
			case 3: //se
				actuallyMoveUnit(user, tx, ty + 1);
				break;
			case 4: //sw
				actuallyMoveUnit(user, tx - 1, ty);
				break;
		}

		// Set locked into combat
		user.setLockedIntoCombat(target);
		target.setLockedIntoCombat(user);
		log.push(user.getName() + " charges at " + target.getName());
		resolveCombat(user, target);
		return true;
	}

	private void resolveCombat(Unit attacker, Unit defender) {
		int min = 3;
		int max = 5;
		int number = MathUtils.random(min, max);
		for (int i = 0; i < number; i++) {
			double damage = attacker.getWeapon().getPower();
			applyDamage(defender, damage);
			if (!defender.isAlive()) {
				log.push(attacker.getName() + " hits " + defender.getName()
						+ " for " + damage + " damage!");
				log.push(attacker.getName() + " killed " + defender.getName() + "!");
				model.getBattle().getBattleMap().getTile(defender.getPosition()).removeUnit();
				attacker.setLockedIntoCombat(null);
				defender.setLockedIntoCombat(null);
				break;
			}
			log.push(attacker.getName() + " hits " + defender.getName()
					+ " for " + damage + " damage! (HP left: "
					+ defender.getCurrentHealth() + ")");
			Unit temp = attacker;
			attacker = defender;
			defender = temp;
		}
	}

	public void applyDamage(Unit target, double damage) {
		double health = target.getCurrentHealth();
		health -= damage;
		if (health > 0) {
			target.setCurrentHealth(health);
		} else {
			target.setCurrentHealth(0);
			boolean unitsLeft = false;
			for (Unit u : target.getArmy().getUnits()) {
				if (u.isAlive()) {
					unitsLeft = true;
					break;
				}
			}
			if (!unitsLeft) {
				inputHandlers.removeProcessor(battleHandler);
				if (target.getArmy() == model.getPlayerParty()) {
					log.push("Defeat!");
				} else {
					log.push("Victory!");
				}
			} else if (target == getSelectedUnit()) {
				nextUnit();
			}
		}
	}

	// The dispose method
	//
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		battleScreen.dispose();
		Resources.unload();
	}
}
