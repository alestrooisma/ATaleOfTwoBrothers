package atotb.view;

import atotb.TwoBrothersGame;
import atotb.controller.Resources;
import atotb.controller.events.InputEvent;
import atotb.controller.events.KeyEvent;
import atotb.controller.events.MouseEvent;
import atotb.model.Army;
import atotb.model.Unit;
import atotb.util.Enum.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 *
 * @author Ale Strooisma
 */
public class BattleScreen implements Screen {

	// Received
	private final TwoBrothersGame game;
	private final SpriteBatch batch;
	private final BitmapFont font;
	//
	//Owned
	private final OrthographicCamera camera;
	private final OrthographicCamera uiCamera;
	private final Viewport viewport;
	private final Viewport uiViewport;
	private IsometricTiledMapRenderer mapRenderer;
	private final Vector3 vec = new Vector3();
	private int windowWidth;
	private int windowHeight;
	//
	// Event handling
	private final InputEvent.List events;
	private boolean dragging = false;
	private int startX = 0;
	private int startY = 0;

	public BattleScreen(TwoBrothersGame game, SpriteBatch batch, BitmapFont font) {
		this.game = game;
		this.batch = batch;
		this.font = font;

		// Set up the main camera
		camera = new OrthographicCamera();
//		camera.translate(480, 16);
		camera.translate(640, 16);
		viewport = new ScreenViewport(camera);

		// Set up the UI camera
		uiCamera = new OrthographicCamera();
		uiViewport = new ScreenViewport(uiCamera);

		// Set up the event handler
		events = new InputEvent.List();
	}

	public void setMap(TiledMap map) {
		if (mapRenderer != null) {
			mapRenderer.dispose();
		}
		mapRenderer = new IsometricTiledMapRenderer(map, 1, batch);
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	@Override
	public void render(float dt) {
		boolean wasBattleOver = game.isBattleOver();
		handleCameraControl();
		processEvents();
		if (game.isBattleOver() && !wasBattleOver) {
			game.endBattle();
		}
		draw();
	}

	private void handleCameraControl() {
		// Handle input
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			camera.translate(-3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			camera.translate(3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			camera.translate(0, -3, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			camera.translate(0, 3, 0);
		}

		// Recalculate camera matrix
		camera.update();
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
		if (button == Buttons.LEFT) {
			vec.x = screenX;
			vec.y = screenY;
			unproject(vec);
			screenToTileCoords(vec);
			int x = (int) vec.x;
			int y = (int) vec.y;
			Unit u = game.getModel().getBattle().getBattleMap().getTile(x, y).getUnit();
			MouseAction ma = getMouseAction(x, y);
			switch (ma) {
				case SELECT:
					game.selectUnit(u);
					break;
				case MOVE:
					game.moveUnit(game.getSelectedUnit(), x, y);
					break;
				case TARGET:
					game.targetUnit(game.getSelectedUnit(), u, game.getPathFinder());
					break;
			}
		} else if (button == Buttons.RIGHT) {
			dragging = true;
			startX = screenX;
			startY = screenY;
		}
	}

	private void processMouseReleasedEvent(int screenX, int screenY, int button) {
		if (button == Buttons.RIGHT) {
			dragging = false;
		}
	}

	private void processMouseDraggedEvent(int screenX, int screenY) {
		if (dragging) {
			getCamera().translate(startX - screenX, screenY - startY);
			startX = screenX;
			startY = screenY;
		}
	}

	private void processKeyEvent(int keycode) {
		switch (keycode) {
			case Keys.R: //TODO temp
				getCamera().zoom = 1;
				getCamera().position.x = 640;
				getCamera().position.y = 16;
				break;
			case Keys.B:
				game.previousUnit();
				break;
			case Keys.N:
				game.nextUnit();
				break;
			case Keys.BACKSPACE:
				game.deselectUnit();
				break;
			case Keys.ENTER:
				game.nextTurn();
				break;
		}
	}

	private void draw() {
		// Clear buffers and paint the background dark gray
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Calculate cursor position
		vec.x = Gdx.input.getX();
		vec.y = Gdx.input.getY();
		camera.unproject(vec);
		screenToTileCoords(vec);
		floor(vec);
		int x = (int) vec.x;
		int y = (int) vec.y;
		tileToScreenCoords(vec);

		// Get potential mouse action for hover position
		MouseAction ma = getMouseAction(x, y);

		// Render map
		mapRenderer.setView(camera);
		mapRenderer.render();

		// Start drawing
		batch.begin();

		// Draw cursor, hover or target marker underlayer
		switch (ma) {
			case SELECT:
				batch.draw(Resources.hoverMarkerUnder, vec.x - 4, vec.y + 16);
				break;
			case TARGET:
				batch.draw(Resources.targetMarkerUnder, vec.x - 4, vec.y + 16);
				break;
			case MOVE:
			case NOTHING:
				batch.draw(Resources.cursor, vec.x, vec.y);
				break;
//			case OUT_OF_BOUNDS:
//				break;
		}

		// Draw walk/dash range markers - TODO order
		Unit u = game.getSelectedUnit();
		if (u != null) {
			for (int i = 0; i < game.getModel().getBattle().getBattleMap().getWidth(); i++) {
				for (int j = 0; j < game.getModel().getBattle().getBattleMap().getHeight(); j++) {
					double d = game.getPathFinder().getDistanceTo(i, j);
					if (d == 0) {
						// Don't draw
					} else if (d <= u.getMovesRemaining()) {
						tileToScreenCoords(i, j, vec);
						batch.draw(Resources.walkMarker, vec.x, vec.y);
					} else if (u.mayDash()
							&& d <= u.getMovesRemaining() + u.getDashDistance()) {
						tileToScreenCoords(i, j, vec);
						batch.draw(Resources.dashMarker, vec.x, vec.y);
					}
//				tileToScreenCoords(i, j, vec);
//				font.draw(batch, "" + game.getModel().getBattle().getBattleMap().getTile(i, j).getTerrain(),vec.x+32-5,vec.y+16+20);
				}
			}
		}

		// Draw unit selection marker underlayer
		if (u != null) {
			tileToScreenCoords(u.getPosition().x,
					u.getPosition().y, vec);
			batch.draw(Resources.selectionMarkerUnder, vec.x - 4, vec.y + 16);
		}

		// Render units on map - TODO order
		for (Army army : game.getModel().getBattle().getArmies()) {
			for (Unit unit : army.getUnits()) {
				if (unit.isAlive()) {
					Texture t;
					if (unit.getName().equals("Dale")) {
						t = Resources.dale;
					} else if (unit.getName().equals("Harryn")) {
						t = Resources.harryn;
					} else if (unit.getName().equals("Wolf")) {
						t = Resources.wolf;
					} else {
						continue;
					}
					tileToScreenCoords(unit.getPosition().x, unit.getPosition().y, vec);
					batch.draw(t, vec.x - 4, vec.y + 16);
				}
			}
		}

		// Draw hover or target marker overlayer
		tileToScreenCoords(x, y, vec);
		switch (ma) {
			case SELECT:
				batch.draw(Resources.hoverMarkerOver, vec.x - 4, vec.y + 16);
				break;
			case TARGET:
				batch.draw(Resources.targetMarkerOver, vec.x - 4, vec.y + 16);
				break;
		}

		// Draw unit selection marker overlayer
		if (u != null) {
			tileToScreenCoords(u.getPosition().x,
					u.getPosition().y, vec);
			batch.draw(Resources.selectionMarkerOver, vec.x - 4, vec.y + 16);
		}

		// Finish drawing
		batch.end();

		// Render UI
		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();

		// Tile coordinates of mouse position
		font.draw(batch, "" + x + ", " + y, windowWidth - 50, 20);

		// Selected unit information
		if (u != null) {
			double dash = u.mayDash() ? u.getDashDistance() : 0;
			font.draw(batch,
					u.getName(),
					10, windowHeight - 10);
			font.draw(batch,
					"HP: " + u.getCurrentHealth() + "\\" + u.getMaxHealth(),
					10, windowHeight - 30);
			font.draw(batch,
					"Moves remaining: " + u.getMovesRemaining() + "+" + dash,
					10, windowHeight - 50);
			font.draw(batch,
					"May act: " + (u.mayAct() ? "yes" : "false"),
					10, windowHeight - 70);
		}

		// Message log
		int N = game.getLog().getLogSize();
		for (int i = N - 1; i >= 0; i--) {
			font.draw(batch, game.getLog().get(i), 10, 20 * (i + 1));
		}

		// Finish UI drawing
		batch.end();
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

	public void addEvent(InputEvent event) {
		events.add(event);
	}

	@Override
	public void resize(int width, int height) {
		windowWidth = width;
		windowHeight = height;
		viewport.update(width, height);
		uiViewport.update(width, height);
		uiCamera.translate(width / 2, height / 2);
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	public Vector3 unproject(Vector3 vec) {
		return camera.unproject(vec);
	}

	public Vector3 screenToTileCoords(Vector3 vec) {
		return screenToTileCoords(vec, vec);
	}

	public Vector3 screenToTileCoords(Vector3 in, Vector3 out) {
		return screenToTileCoords(in.x, in.y, out);
	}

	public Vector3 screenToTileCoords(float xIn, float yIn, Vector3 out) {
		out.x = (xIn / 32 - (16 - yIn) / 16) / 2; //TODO hardcoded
		out.y = (1 - yIn / 16 + xIn / 32) / 2; //TODO hardcoded
		return out;
	}

	public Vector3 tileToScreenCoords(Vector3 vec) {
		return tileToScreenCoords(vec, vec);
	}

	public Vector3 tileToScreenCoords(Vector3 in, Vector3 out) {
		return tileToScreenCoords(in.x, in.y, out);
	}

	public Vector3 tileToScreenCoords(float xIn, float yIn, Vector3 out) {
		out.x = (xIn + yIn) * 32; //TODO hardcoded
		out.y = (xIn - yIn - 1) * 16; //TODO hardcoded
		return out;
	}

	public Vector3 floor(Vector3 vec) {
		return floor(vec, vec);
	}

	public Vector3 floor(Vector3 in, Vector3 out) {
		return floor(in.x, in.y, out);
	}

	public Vector3 floor(float xIn, float yIn, Vector3 out) {
		out.x = MathUtils.floor(xIn);
		out.y = MathUtils.floor(yIn);
		return out;
	}

	public enum MouseAction {

		SELECT, MOVE, TARGET, NOTHING, OUT_OF_BOUNDS;
	}
}
