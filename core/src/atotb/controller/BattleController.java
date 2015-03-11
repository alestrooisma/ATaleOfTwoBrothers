package atotb.controller;

import atotb.TwoBrothersGame;
import static atotb.controller.BattleController.MouseAction.MOVE;
import static atotb.controller.BattleController.MouseAction.SELECT;
import static atotb.controller.BattleController.MouseAction.TARGET;
import atotb.controller.ai.ArtificialIntelligence;
import atotb.controller.input.InputEvent;
import atotb.controller.input.KeyEvent;
import atotb.controller.input.MouseEvent;
import atotb.controller.log.AbstractMoveEvent;
import atotb.controller.log.DamageEvent;
import atotb.controller.log.ChargeEvent;
import atotb.controller.log.DashEvent;
import atotb.controller.log.DeathEvent;
import atotb.controller.log.Event;
import atotb.controller.log.EventProcessor;
import atotb.controller.log.EventVisitor;
import atotb.controller.log.FireEvent;
import atotb.controller.log.MoveEvent;
import atotb.model.actions.Action;
import atotb.model.Army;
import atotb.model.Battle;
import atotb.model.BattleMap;
import atotb.model.HistoryItem;
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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 * The controller component for battles. Manages the game state during battles.
 *
 * @author Ale Strooisma
 */
public class BattleController extends ScreenController<BattleScreen> {

	public static final int NONE_SELECTED = -1;

	// Received
	private final TwoBrothersGame game;
	private Battle battle;
	private ArtificialIntelligence[] ai;
	//
	// Controller
	private final BattleEventProcessor processor;
	private boolean battleEnded;
	private int selectedUnit;
	private int selectedAction;
	private PathFinder pathfinder;
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

		// Set up the input event handler
		events = new InputEvent.List();

		// Set up the battle event processor
		processor = new BattleEventProcessor();
		game.getEventLog().register(processor);
	}

	@Override
	public void update(float dt) {
		boolean wasBattleOver = isBattleOver();
		processEvents();
		if (isBattleOver() && !wasBattleOver) {
			endBattle();
		}
		handleCameraControl();
	}

	/**
	 * Sets the game state for the next battle. Does not start the battle.
	 *
	 * @param battle the model component representing the battle
	 * @param tileMap the graphical representation of the map on which the
	 * battle is played
	 * @param ai the AIs used for the various players
	 */
	public void initBattle(Battle battle, TiledMap tileMap, ArtificialIntelligence[] ai) {
		this.battle = battle;
		this.ai = ai;

		// Prepare view
		getView().setMap(tileMap);
		getView().initBattle(battle);

		// Prepare gamestate
		battleEnded = false;
		deselectUnit();
		pathfinder = new PathFinder(battle.getBattleMap());
	}

	/**
	 * Called to start the battle. Main functionality is starting the first
	 * turn.
	 */
	public void startBattle() {
		// Kick it off
		startTurn();
		if (ai[battle.getCurrentPlayer()] == null) {
			nextUnit();
		}
	}

	/**
	 * Called when the battle has ended.
	 */
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

		// Player turn - select first unit
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
		deselectAction();
		selectedUnit = number;
		Unit u = getSelectedUnit();
		pathfinder.calculateDistancesFrom(
				u.getPosition().x, u.getPosition().y, u.getTotalMovesRemaining());
	}

	public void deselectUnit() {
		selectedUnit = NONE_SELECTED;
		deselectAction();
	}

	public void previousUnit() {
		cycleUnit(-1);
	}

	public void nextUnit() {
		cycleUnit(1);
	}

	private void cycleUnit(int step) {
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

	// Selected action management
	//
	public void selectAction(int number) {
		Unit unit = getSelectedUnit();
		if (unit == null) {
			return;
		}

		Action action = unit.getAction(number);
		if (action != null && action.isAllowed(unit)) {
			if (action.isImmediate()) {
				action.execute(unit);
				if (action.getMessage() != null) {
					game.getLog().push(action.getMessage());
				}
				unit.addHistoryItem(new HistoryItem.Ability(action));
			} else {
				selectedAction = number;
				game.getLog().push(action.getSelectMessage(unit));
			}
		}
	}

	public void deselectAction() {
		selectedAction = NONE_SELECTED;
	}

	public int getSelectedActionNumber() {
		return selectedAction;
	}

	public Action getSelectedAction() {
		if (getSelectedUnit() == null) {
			return null;
		} else {
			return getSelectedUnit().getAction(getSelectedActionNumber());
		}
	}

	// Game state modifiers
	//
	public void moveUnit(Unit u, int destX, int destY, PathFinder pf) {
		// Calculate distance
		double distance = pf.getDistanceTo(destX, destY);

		// Check range and move if possible
		if (distance <= u.getMovesRemaining()) {
			game.getEventLog().push(new MoveEvent(u,
					u.getPosition().x, u.getPosition().y, destX, destY, distance));
			pf.calculateDistancesFrom(
					u.getPosition().x, u.getPosition().y, u.getTotalMovesRemaining());
		} else if (u.mayDash()
				&& distance <= u.getMovesRemaining() + u.getDashDistance()) {
			game.getEventLog().push(new DashEvent(u,
					u.getPosition().x, u.getPosition().y, destX, destY, distance));
		}
	}

	public void targetUnit(Unit user, Unit target, Action action, PathFinder pf) {

		// Execute action
		if (action != null) {
			if (action.isAllowed(user, target)) {
				action.execute(user, target);
				if (action.getMessage() != null) {
					game.getLog().push(action.getMessage());
				}
				deselectAction();
				user.addHistoryItem(new HistoryItem.Ability(action));
			}
		} else if (user.mayAttack()) {
			// Perform default attack

			// Check if target can be attacked
			if (target.isLockedIntoCombat()) {
				game.getLog().push(target.getName() + " is locked into combat - can't attack.");
				return;
			}

			// Get the weapon
			Weapon weapon = user.getWeapon();
			if (weapon == null) {
				weapon = game.getUnarmed();
			}

			// Execute action, depending on weapon type
			if (weapon instanceof RangedWeapon) {
				game.getEventLog().push(new FireEvent(user, target, weapon));
			} else if (weapon instanceof MeleeWeapon) {
				charge(user, target, pf);
			}
		}
	}

	private void charge(Unit user, Unit target, PathFinder pf) {
		int tx = target.getPosition().x;
		int ty = target.getPosition().y;

		// Find best target location
		Direction dir = getChargingDirection(tx, ty, pf);
		double distance = getChargingDistance(tx, ty, pf, dir);

		// Check for space and range
		if (dir != null && distance <= user.getTotalMovesRemaining()) {
			// In range, so move there
			game.getEventLog().push(new ChargeEvent(user, target,
					user.getPosition().x, user.getPosition().y,
					dir.getX(tx), dir.getY(ty), distance));

			// Resolve initial round of combat
			resolveCombat(user, target);
		}
	}

	public Direction getChargingDirection(int targetX, int targetY, PathFinder pf) {
		double nw = pf.getDistanceTo(targetX, targetY - 1);
		double ne = pf.getDistanceTo(targetX + 1, targetY);
		double se = pf.getDistanceTo(targetX, targetY + 1);
		double sw = pf.getDistanceTo(targetX - 1, targetY);

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
		return pf.getDistanceToWithoutCheck(dir.getX(targetX), dir.getY(targetY));
	}

	private void resolveCombat(Unit attacker, Unit defender) {
		int min = 1;
		int max = 1;
		int number = MathUtils.random(min, max);
		for (int i = 0; i < number; i++) {
			// Get the weapon
			Weapon weapon = attacker.getWeapon();
			if (weapon == null || weapon instanceof RangedWeapon) {
				weapon = game.getUnarmed();
			}

			// Calculate and apply damage
			double damage = weapon.getPower();
			game.getEventLog().push(new DamageEvent(defender, damage));

			// If the blow is fatal, end the combat
			if (!defender.isAlive()) {
				break;
			}

			// Switch roles of attacker and defender
			Unit temp = attacker;
			attacker = defender;
			defender = temp;
		}
	}

	// Input handling
	// TODO should be move to separate class, but waiting for input handling revision.
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
					targetUnit(getSelectedUnit(), u, getSelectedAction(), getPathFinder());
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
			case Input.Keys.NUM_1:
				selectAction(1);
				break;
			case Input.Keys.NUM_2:
				selectAction(2);
				break;
			case Input.Keys.NUM_3:
				selectAction(3);
				break;
			case Input.Keys.NUM_4:
				selectAction(4);
				break;
			case Input.Keys.NUM_5:
				selectAction(5);
				break;
			case Input.Keys.NUM_6:
				selectAction(6);
				break;
			case Input.Keys.NUM_7:
				selectAction(7);
				break;
			case Input.Keys.NUM_8:
				selectAction(8);
				break;
			case Input.Keys.NUM_9:
				selectAction(9);
				break;
			case Input.Keys.B:
				previousUnit();
				break;
			case Input.Keys.N:
			case Input.Keys.TAB:
				nextUnit();
				break;
			case Input.Keys.BACKSPACE:
				if (getSelectedAction() != null) {
					deselectAction();
				} else {
					deselectUnit();
				}
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
		if (!game.getModel().getBattle().getBattleMap().contains(x, y)) {
			return MouseAction.OUT_OF_BOUNDS;
		}

		// Clicked on a tile
		Unit target = game.getModel().getBattle().getBattleMap().getTile(x, y).getUnit();
		if (target != null) {
			// There is a unit on the tile
			boolean friendly = (target.getArmy() == game.getModel().getBattle().getCurrentArmy());
			Action action = getSelectedAction();
			if (action == null) {
				if (friendly) {
					// Unit is friendly -> select it
					return MouseAction.SELECT;
				} else if (getSelectedUnit() != null
						&& getSelectedUnit().mayAttack()) {
					// Unit is enemy -> default attack
					return MouseAction.TARGET;
				}
			} else if (action.isApplicableTarget(getSelectedUnit(), target)) {
				// Unit is applicable target -> execute action
				return MouseAction.TARGET;
			} else {
				// No applicable target
				return MouseAction.NO_TARGET;
			}
		} else if (getSelectedUnit() != null) {
			if (getSelectedAction() != null) {
				return MouseAction.NO_TARGET;
			} else {
				return MouseAction.MOVE;
			}
		}
		return MouseAction.NOTHING;
	}

	public enum MouseAction {

		SELECT, MOVE, TARGET, NO_TARGET, NOTHING, OUT_OF_BOUNDS;
	}

	/**
	 * Processes BattleEvents such that the model is updated accordingly.
	 *
	 * @author Ale Strooisma
	 */
	public class BattleEventProcessor implements EventProcessor, EventVisitor {

		@Override
		public void push(Event e) {
			e.visit(this);
		}

		@Override
		public void visitDeathEvent(DeathEvent event) {
			Unit unit = event.getUnit();

			// Remove unit from map
			battle.getBattleMap().getTile(unit.getPosition()).removeUnit();

			// End combat
			if (unit.isLockedIntoCombat()) {
				Unit opponent = unit.getOpponent();
				unit.setLockedIntoCombat(null);
				opponent.setLockedIntoCombat(null);
			}

			// Check if this death causes the end of the battle...
			boolean unitsLeft = false;
			for (Unit u : unit.getArmy().getUnits()) {
				if (u.isAlive()) {
					unitsLeft = true;
					break;
				}
			}

			if (!unitsLeft) {
				// End of the battle
				battleEnded = true;
				deselectUnit();
			} else if (unit == getSelectedUnit()) {
				// Make sure no dead unit is selected
				deselectUnit();
			}
		}

		@Override
		public void visitMoveEvent(MoveEvent event) {// Log the movement
			handleAbstractMoveEvent(event);
			event.getUnit().addHistoryItem(new HistoryItem.Move(event.getDistance()));
		}

		@Override
		public void visitDashEvent(DashEvent event) {
			handleAbstractMoveEvent(event);
			event.getUnit().addHistoryItem(new HistoryItem.Dash());
		}

		@Override
		public void visitChargeEvent(ChargeEvent event) {
			Unit unit = event.getUnit();
			Unit opponent = event.getOpponent();

			// Handle the movement
			handleAbstractMoveEvent(event);
			event.getUnit().addHistoryItem(new HistoryItem.Charge());

			// Lock in combat
			unit.setLockedIntoCombat(opponent);
			opponent.setLockedIntoCombat(unit);
		}

		private void handleAbstractMoveEvent(AbstractMoveEvent event) {
			Unit unit = event.getUnit();
			int x = event.getDestX();
			int y = event.getDestY();

			// Actually move the unit
			BattleMap map = battle.getBattleMap();
			map.getTile(unit.getPosition()).removeUnit();
			map.getTile(x, y).setUnit(unit);
			unit.setPosition(x, y);
		}

		@Override
		public void visitDamageEvent(DamageEvent event) {
			double remainingHealth = event.getTarget().getCurrentHealth();
			remainingHealth -= event.getDamage();
			if (remainingHealth > 0) {
				event.getTarget().setCurrentHealth(remainingHealth);
			} else {
				event.getTarget().setCurrentHealth(0);
				game.getEventLog().push(new DeathEvent(event.getTarget()));
			}
		}

		@Override
		public void visitFireEvent(FireEvent event) {
			double damage = event.getWeapon().getPower();
			event.getUser().addHistoryItem(new HistoryItem.Fire());
			game.getEventLog().push(new DamageEvent(event.getTarget(), damage));
		}
	}
}
