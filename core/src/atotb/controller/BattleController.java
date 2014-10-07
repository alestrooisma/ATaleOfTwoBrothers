package atotb.controller;

import atotb.TwoBrothersGame;
import static atotb.controller.BattleController.MouseAction.MOVE;
import static atotb.controller.BattleController.MouseAction.SELECT;
import static atotb.controller.BattleController.MouseAction.TARGET;
import atotb.controller.ai.ArtificialIntelligence;
import atotb.controller.events.InputEvent;
import atotb.controller.events.KeyEvent;
import atotb.controller.events.MouseEvent;
import atotb.model.Army;
import atotb.model.Battle;
import atotb.model.BattleMap;
import atotb.model.Unit;
import atotb.model.items.MeleeWeapon;
import atotb.model.items.RangedWeapon;
import atotb.model.items.Weapon;
import atotb.util.Enum.Direction;
import static atotb.util.Enum.Direction.*;
import atotb.util.PathFinder;
import atotb.view.BattleScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 *
 * @author ale
 */
public class BattleController extends ScreenController<BattleScreen> {

	// Received
	private final TwoBrothersGame game;
	private Battle battle;
	//
	// Controller
	private boolean battleEnded;
	private int selectedUnit = -1;
	private PathFinder pathfinder;
	private ArtificialIntelligence[] ai;
	//
	// Event handling
	private final InputEvent.List events;
	private boolean dragging = false;
	private int startX = 0;
	private int startY = 0;
	//
	// Other
	private final Vector3 vec = new Vector3();

	// Core functions
	//
	public BattleController(TwoBrothersGame game) {
		this.game = game;

		// Set up the event handler
		events = new InputEvent.List();
	}

	@Override
	public void update(float dt) {
		boolean wasBattleOver = isBattleOver();
		processEvents();
		if (isBattleOver() && !wasBattleOver) {
			game.endBattle();
		}
		handleCameraControl();
	}

	public void startBattle(Battle battle, ArtificialIntelligence[] ai) {
		this.battle = battle;
		this.ai = ai;

		// Prepare gamestate
		battleEnded = false;
		pathfinder = new PathFinder(battle.getBattleMap());

		// Kick it off
		startTurn();
		if (ai[battle.getCurrentPlayer()] == null) {
			nextUnit();
		}
	}

	public void endBattle() {
		boolean victory = false;
		for (Unit u : game.getModel().getPlayerParty().getUnits()) {
			if (u.isAlive()) {
				victory = true;
				break;
			}
		}

		if (victory) {
			game.getLog().push("Victory!");
		} else {
			game.getLog().push("Defeat...");
		}

		game.endBattle();
	}

	public Battle getBattle() {
		return battle;
	}

	public boolean isBattleOver() {
		return battleEnded;
	}

	// Turn switching
	//
	public void nextTurn() {
		// End this turn
		endTurn();

		// Start next players turn
		startTurn();

		// Handle AI until it's a human player's turn 
		int player = battle.getCurrentPlayer();
		while (ai[player] != null && !isBattleOver()) {
			ai[player].playTurn(this);
			endTurn();
			startTurn();
			player = battle.getCurrentPlayer();
		}

		// Select first unit
		if (!isBattleOver()) {
			nextUnit();
		}
	}

	public void startTurn() {
		// Hand turn to next player
		battle.nextPlayer();
		int player = battle.getCurrentPlayer();

		// Increment turn count if first player starts a turn
		if (player == 0) {
			battle.incrementTurn();
		}

		// Reset unit turn status
		Army army = battle.getCurrentArmy();
		for (Unit u : army.getUnits()) {
			u.reset();
		}

		// Resolve combat
		for (Unit u : army.getUnits()) {
			if (u.isLockedIntoCombat()) {
				resolveCombat(u, u.getOpponent());
				if (isBattleOver()) {
					return;
				}
			}
		}
	}

	public void endTurn() {
		// Finalize turn
		deselectUnit();
	}

	// Selected unit management
	//
	public void selectUnit(Unit unit) {
		selectUnit(battle.getCurrentArmy().getUnits().indexOf(unit));
	}

	public void selectUnit(int number) {
		selectedUnit = number;
		Unit u = getSelectedUnit();
		pathfinder.calculateDistancesFrom(
				u.getPosition().x, u.getPosition().y, u.getTotalMovesRemaining());
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
		ArrayList<Unit> units = battle.getCurrentArmy().getUnits();
		int unit = selectedUnit;
		do {
			unit = ((unit + step) % units.size() + units.size()) % units.size();
		} while (!units.get(unit).isAlive());
		selectUnit(unit);
	}

	public Unit getSelectedUnit() {
		if (selectedUnit < 0) {
			return null;
		}
		return battle.getCurrentArmy().getUnits().get(selectedUnit);
	}

	public PathFinder getPathFinder() {
		return pathfinder;
	}

	// Game state modifiers
	//
	public void moveUnit(Unit u, int destX, int destY, PathFinder pf) {
		// Calculate distance
		double distance = pf.getDistanceTo_safe(destX, destY);

		// Check range and move if possible
		if (distance <= u.getMovesRemaining()) {
			actuallyMoveUnit(u, destX, destY, pf);
			u.reduceMoves(distance);
		} else if (u.mayDash()
				&& distance <= u.getMovesRemaining() + u.getDashDistance()) {
			actuallyMoveUnit(u, destX, destY, pf);
			u.setMovesRemaining(0);
			u.setHasDashed();
			u.setMayAct(false); //TODO temp
			game.getLog().push("Dashing!");
		}
	}

	public void actuallyMoveUnit(Unit u, int destX, int destY, PathFinder pf) {
		BattleMap map = battle.getBattleMap();
		map.getTile(u.getPosition()).removeUnit();
		map.getTile(destX, destY).setUnit(u);
		u.setPosition(destX, destY);

		// Recalculate movement range TODO only for human player?
		pf.calculateDistancesFrom(
				u.getPosition().x, u.getPosition().y, u.getTotalMovesRemaining());
	}

	public void targetUnit(Unit user, Unit target, PathFinder pf) {
		// Check if the unit is allowed to act
		if (!user.mayAct()) {
			game.getLog().push(user.getName() + " may not act anymore.");
			return;
		}

		// Check if target can be attacked
		if (target.isLockedIntoCombat()) {
			game.getLog().push(target.getName() + " is locked into combat - can't attack.");
			return;
		}

		// Get the weapon
		//TODO check for action, otherwise use main weapon.
		Weapon w = user.getWeapon();
		if (w == null) {
			w = game.getUnarmed();
		}

		// Execute action, depending on weapon type
		boolean acted = false;
		if (w instanceof RangedWeapon) {
			acted = fireRangedWeapon(user, target, w);
		} else if (w instanceof MeleeWeapon) {
			acted = charge(user, target, pf);
		}

		// If the unit has acted, make it unable to act again
		// TODO depending on exact action performed
		if (acted) {
			user.setMovesRemaining(0);
			user.setMayAct(false);
			user.setMayDash(false);
		}
	}

	private boolean fireRangedWeapon(Unit user, Unit target, Weapon weapon) {
		game.getLog().push(user.getName() + " fires at " + target.getName() + " - " + weapon.getPower() + " damage!");
		applyDamage(target, weapon.getPower());
		if (target.getCurrentHealth() > 0) {
			game.getLog().push(target.getName() + "'s remaining health = " + target.getCurrentHealth());
		} else {
			game.getLog().push(user.getName() + " killed " + target.getName() + "!");
			battle.getBattleMap().getTile(target.getPosition()).removeUnit();
		}
		return true;
	}

	private boolean charge(Unit user, Unit target, PathFinder pf) {
		int tx = target.getPosition().x;
		int ty = target.getPosition().y;

		// Find best target location
		Direction dir = getChargingDirection(tx, ty, pf);
		if (dir == null) {
			game.getLog().push("There's no room next to " + target.getName() + "!");
			return false;
		}
		double d = getChargingDistance(tx, ty, pf, dir);

		// Check range
		if (d > user.getTotalMovesRemaining()) {
			game.getLog().push(target.getName() + " is out of charging range for "
					+ user.getName());
			return false;
		}

		// In range, so move there
		switch (dir) {
			case NW:
				actuallyMoveUnit(user, tx, ty - 1, pf);
				break;
			case NE:
				actuallyMoveUnit(user, tx + 1, ty, pf);
				break;
			case SE:
				actuallyMoveUnit(user, tx, ty + 1, pf);
				break;
			case SW:
				actuallyMoveUnit(user, tx - 1, ty, pf);
				break;
		}

		// Set locked into combat
		user.setLockedIntoCombat(target);
		target.setLockedIntoCombat(user);
		game.getLog().push(user.getName() + " charges at " + target.getName());
		resolveCombat(user, target);
		return true;
	}

	public Direction getChargingDirection(int targetX, int targetY, PathFinder pf) {
		double nw = pf.getDistanceTo_safe(targetX, targetY - 1);
		double ne = pf.getDistanceTo_safe(targetX + 1, targetY);
		double se = pf.getDistanceTo_safe(targetX, targetY + 1);
		double sw = pf.getDistanceTo_safe(targetX - 1, targetY);

		Direction dir = Direction.NW;
		double d = nw;
		if (ne < d) {
			dir = NE;
			d = ne;
		}
		if (se < d) {
			dir = SE;
			d = se;
		}
		if (sw < d) {
			dir = SW;
		}
		if (d == PathFinder.INACCESSIBLE) {
			return null;
		}
		return dir;
	}

	public double getChargingDistance(
			int targetX, int targetY, PathFinder pf, Direction dir) {
		return pf.getDistanceTo(dir.getX(targetX), dir.getY(targetY));
	}

	private void resolveCombat(Unit attacker, Unit defender) {
		int min = 3;
		int max = 5;
		int number = MathUtils.random(min, max);
		for (int i = 0; i < number; i++) {
			// Get the weapon
			Weapon w = attacker.getWeapon();
			if (w == null || w instanceof RangedWeapon) {
				w = game.getUnarmed();
			}

			// Calculate and apply damage
			double damage = w.getPower();
			applyDamage(defender, damage);

			// If the blow is fatal, handle the defender's death
			if (!defender.isAlive()) {
				game.getLog().push(attacker.getName() + " hits " + defender.getName()
						+ " for " + damage + " damage!");
				game.getLog().push(attacker.getName() + " killed " + defender.getName() + "!");
				battle.getBattleMap().getTile(defender.getPosition()).removeUnit();
				attacker.setLockedIntoCombat(null);
				defender.setLockedIntoCombat(null);
				break;
			} else {
				game.getLog().push(attacker.getName() + " hits " + defender.getName()
						+ " for " + damage + " damage! (HP left: "
						+ defender.getCurrentHealth() + ")");
			}

			// Switch roles of attacker and defender
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
				battleEnded = true;
				deselectUnit();
			} else if (target == getSelectedUnit()) {
				deselectUnit();
			}
		}
	}

	// Input handling
	//
	private void handleCameraControl() {
		// Handle input
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			getView().getCamera().translate(-3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			getView().getCamera().translate(3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			getView().getCamera().translate(0, -3, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			getView().getCamera().translate(0, 3, 0);
		}

		// Recalculate camera matrix
		getView().getCamera().update();
	}

	private void processEvents() {
		InputEvent event = events.next();
		while (event != null && !isBattleOver()) {
			if (event instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) event;
				switch (me.getType()) {
					case PRESSED:
						processMousePressedEvent(
								me.getScreenX(), me.getScreenY(), me.getButton());
						break;
					case RELEASED:
						processMouseReleasedEvent(
								me.getScreenX(), me.getScreenY(), me.getButton());
						break;
					case DRAGGED:
						processMouseDraggedEvent(
								me.getScreenX(), me.getScreenY());
						break;
				}
			} else if (event instanceof KeyEvent) {
				processKeyEvent(((KeyEvent) event).getKeycode());
			}
			event = events.next();
		}
	}

	private void processMousePressedEvent(int screenX, int screenY, int button) {
		if (button == Input.Buttons.LEFT) {
			vec.x = screenX;
			vec.y = screenY;
			getView().unproject(vec);
			getView().screenToTileCoords(vec);
			int x = (int) vec.x;
			int y = (int) vec.y;
			MouseAction ma = getMouseAction(x, y);
			Unit u;
			switch (ma) {
				case SELECT:
					u = game.getModel().getBattle().getBattleMap().getTile(x, y).getUnit();
					selectUnit(u);
					break;
				case MOVE:
					moveUnit(getSelectedUnit(), x, y, getPathFinder());
					break;
				case TARGET:
					u = game.getModel().getBattle().getBattleMap().getTile(x, y).getUnit();
					targetUnit(getSelectedUnit(), u, getPathFinder());
					break;
			}
		} else if (button == Input.Buttons.RIGHT) {
			dragging = true;
			startX = screenX;
			startY = screenY;
		}
	}

	private void processMouseReleasedEvent(int screenX, int screenY, int button) {
		if (button == Input.Buttons.RIGHT) {
			dragging = false;
		}
	}

	private void processMouseDraggedEvent(int screenX, int screenY) {
		if (dragging) {
			getView().getCamera().translate(startX - screenX, screenY - startY);
			startX = screenX;
			startY = screenY;
		}
	}

	private void processKeyEvent(int keycode) {
		switch (keycode) {
			case Input.Keys.R: //TODO temp
				getView().getCamera().zoom = 1;
				getView().getCamera().position.x = 640;
				getView().getCamera().position.y = 16;
				break;
			case Input.Keys.B:
				previousUnit();
				break;
			case Input.Keys.N:
				nextUnit();
				break;
			case Input.Keys.BACKSPACE:
				deselectUnit();
				break;
			case Input.Keys.ENTER:
				nextTurn();
				break;
		}
	}

	public void addEvent(InputEvent event) {
		events.add(event);
	}

	public MouseAction getMouseAction(int x, int y) {
		if (game.getModel().getBattle().getBattleMap().contains(x, y)) {
			// Clicked on a tile
			Unit u = game.getModel().getBattle().getBattleMap().getTile(x, y).getUnit();
			if (u != null) {
				// There is a unit on the tile
				if (!u.isEnemy(game.getModel().getBattle().getCurrentArmy())) {
					// Unit is friendly -> select it
					return MouseAction.SELECT;
				} else if (getSelectedUnit() != null
						&& getSelectedUnit().mayAct()) {
					// Unit is enemy
					return MouseAction.TARGET;
				}
			} else {
				return MouseAction.MOVE;
			}
		} else {
			return MouseAction.OUT_OF_BOUNDS;
		}
		return MouseAction.NOTHING;
	}

	public enum MouseAction {

		SELECT, MOVE, TARGET, NOTHING, OUT_OF_BOUNDS;
	}
}
