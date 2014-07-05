package atotb;

import atotb.controller.*;
import atotb.model.*;
import atotb.model.items.*;
import atotb.util.PathFinder;
import atotb.view.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.PooledLinkedList;
import java.awt.Point;
import static java.lang.Math.abs;
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
	private PathFinder pathFinder;
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
		u = Unit.createUnit("Dale", "The oldest of the two brothers.", "Dale picked up a pitchfork, lacking a better weapon.", player, 5, 2.5);
		u.addAction(defense);
		u = Unit.createUnit("Harryn", "The younger of the two brothers.", "Harryn is decent with a bow.", player, 5, 2.5);
		u.setWeapon(new RangedWeapon("Old hunting bow", "", "", 3));

		model = new Model(player);
	}

	private void setUpView() {
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

	public Unit getSelectedUnit() {
		if (selectedUnit < 0) {
			return null;
		}
		return model.getCurrentArmy().getUnits().get(selectedUnit);
	}

	// Game state modifiers
	//
	private void startBattle() {
		Army enemy = new Army("Wolves", "A pack of wolves.", "Uh, oh, it seems you've encountered a pack of ferocious wolves!");
		Unit w1 = Unit.createUnit("Wolf", "A ferocious wolf!", "A hungry wolf is very dangerous.", enemy, 2, 5);
		Unit w2 = Unit.createUnit("Wolf", "A ferocious wolf!", "A hungry wolf is very dangerous.", enemy, 2, 5);
		Unit w3 = Unit.createUnit("Wolf", "A ferocious wolf!", "A hungry wolf is very dangerous.", enemy, 2, 5);

		Tile[] tiles = new Tile[20 * 20]; //TODO hardcoded
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new Tile(new Point(i % 20, i / 20), 0); //TODO hardcoded
		}
		BattleMap map = new BattleMap(20, 20, tiles); //TODO hardcoded
		map.addUnit(w1, 4, 9);
		map.addUnit(w2, 11, 2);
		map.addUnit(w3, 13, 5);
		map.addUnit(model.getPlayerParty().getUnits().get(0), 9, 11);
		map.addUnit(model.getPlayerParty().getUnits().get(1), 6, 14);

		pathFinder = new PathFinder(20, 20); //TODO hardcoded
		model.startBattle(map, enemy);
		selectedUnit = 0;
		setScreen(battleScreen);
		inputHandlers.addProcessor(battleHandler);
		startTurn(model.getCurrentArmy());
	}

	private void startTurn(Army army) {
		if (model.getCurrentPlayer() == 0) {
			model.incrementTurn();
		}
		//turnStartTime = System.currentTimeMillis();

		for (Unit u : army.getUnits()) {
			u.reset();
		}

		selectedUnit = -1;
		nextUnit();
	}

	public void endTurn() {
		// Finalize turn
		deselectUnit();

		// Start next player's turn
		model.incrementCurrentPlayer();
		startTurn(model.getCurrentArmy());
	}

	public void selectUnit(int number) {
		selectedUnit = number;
	}

	public void selectUnit(Unit unit) {
		selectUnit(model.getCurrentArmy().getUnits().indexOf(unit));
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
		ArrayList<Unit> units = model.getCurrentArmy().getUnits();
		int unit = selectedUnit;
		do {
			unit = ((unit + step) % units.size() + units.size()) % units.size();
		} while (!units.get(unit).isAlive());
		selectUnit(unit);
	}

	public void moveUnit(Unit u, int destX, int destY) {
		double distance = walkingDistance(u, destX, destY);
		if (distance <= u.getMovesRemaining()) {
			actuallyMoveUnit(u, destX, destY);
			u.reduceMoves(distance);
		} else if (u.mayDash() 
				&& distance <= u.getMovesRemaining() + u.getDashDistance()) {
			actuallyMoveUnit(u, destX, destY);
			u.setMovesRemaining(0);
			u.setHasDashed();
			System.out.println("Dashing!");
		}
	}
	
	public void actuallyMoveUnit(Unit u, int destX, int destY) {
		BattleMap map = getModel().getBattleMap();
		map.getTile(u.getPosition()).removeUnit();
		map.getTile(destX, destY).setUnit(u);
		u.setPosition(destX, destY);
	}

	public void targetUnit(Unit target) {
		//TODO check for action
		// else...

		System.out.println("Targeting " + target.getName());
		boolean acted = false;
		Weapon w = getSelectedUnit().getWeapon();

		if (w != null) {
			if (getSelectedUnit().mayAct()) {
				if (w instanceof RangedWeapon) {
					acted = fireRangedWeapon(getSelectedUnit(), target, w);
				} //else if (w instanceof MeleeWeapon) {}
			} else {
				System.out.println("May not act anymore.");
			}
		} else {
			System.out.println("No weapon!");
		}
		if (acted) {
			getSelectedUnit().setMovesRemaining(0);
			getSelectedUnit().setMayAct(false);
			getSelectedUnit().setMayDash(false);
		}
		if (!target.isAlive()) {
			System.out.println("Killed " + target.getName());
			model.getBattleMap().getTile(target.getPosition()).removeUnit();
		}
	}

	private boolean fireRangedWeapon(Unit user, Unit target, Weapon weapon) {
		System.out.println("Firing! (damage  = " + weapon.getPower() + ")");
		target.applyDamage(weapon.getPower());
		System.out.println(target.getName() + "'s remaining health = " + target.getCurrentHealth());
		return true;
	}

	// Queries
	//
	public double walkingDistance(Unit unit, int destX, int destY) {
		int unitX = unit.getPosition().x;
		int unitY = unit.getPosition().y;

		// Check if destination is on map
		if (!model.getBattleMap().contains(destX, destY)) {
			return Double.MAX_VALUE;
		}

		// Get max distance allowed
		double maxMoves = unit.getMovesRemaining();
		if (unit.mayDash()) {
			maxMoves += unit.getDashDistance();
		}

		// Rough check
		if (roughDistance(unitX, unitY, destX, destY) > maxMoves) {
			return Double.MAX_VALUE;
		}

		// Precise check
		return preciseDistance(unitX, unitY, destX, destY, maxMoves);
	}

	public double roughDistance(int x1, int y1, int x2, int y2) {
		int dx = abs(x1 - x2);
		int dy = abs(y1 - y2);
		int min, max;
		if (dx > dy) {
			min = dy;
			max = dx;
		} else {
			min = dx;
			max = dy;
		}
		return min * 1.5 + max - min;
	}

	public double preciseDistance(int x1, int y1, int x2, int y2) {
		return preciseDistance(x1, y1, x2, y2, Integer.MAX_VALUE);
	}

	public double preciseDistance(int x1, int y1, int x2, int y2, double limit) {
		return pathFinder.findDistance(x1, y1, x2, y2, limit);
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
