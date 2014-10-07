package atotb.controller;

import atotb.TwoBrothersGame;
import static atotb.controller.BattleController.MouseAction.MOVE;
import static atotb.controller.BattleController.MouseAction.SELECT;
import static atotb.controller.BattleController.MouseAction.TARGET;
import atotb.controller.events.InputEvent;
import atotb.controller.events.KeyEvent;
import atotb.controller.events.MouseEvent;
import atotb.model.Unit;
import atotb.view.BattleScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author ale
 */
public class BattleController extends ScreenController<BattleScreen> {

	// Received
	private final TwoBrothersGame game;
	//
	// Owned
	private final Vector3 vec = new Vector3();
	//
	// Event handling
	private final InputEvent.List events;
	private boolean dragging = false;
	private int startX = 0;
	private int startY = 0;
	
	public BattleController(TwoBrothersGame game) {
		this.game = game;
		
		// Set up the event handler
		events = new InputEvent.List();
	}

	@Override
	public void update(float dt) {
		boolean wasBattleOver = game.isBattleOver();
		handleCameraControl();
		processEvents();
		if (game.isBattleOver() && !wasBattleOver) {
			game.endBattle();
		}
	}
	
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
		while (event != null && !game.isBattleOver()) {
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
					game.selectUnit(u);
					break;
				case MOVE:
					game.moveUnit(game.getSelectedUnit(), x, y);
					break;
				case TARGET:
					u = game.getModel().getBattle().getBattleMap().getTile(x, y).getUnit();
					game.targetUnit(game.getSelectedUnit(), u, game.getPathFinder());
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
				game.previousUnit();
				break;
			case Input.Keys.N:
				game.nextUnit();
				break;
			case Input.Keys.BACKSPACE:
				game.deselectUnit();
				break;
			case Input.Keys.ENTER:
				game.nextTurn();
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
				} else if (game.getSelectedUnit() != null
						&& game.getSelectedUnit().mayAct()) {
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
