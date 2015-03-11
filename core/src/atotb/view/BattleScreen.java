package atotb.view;

import atotb.TwoBrothersGame;
import atotb.controller.BattleController;
import atotb.controller.BattleController.MouseAction;
import atotb.controller.Resources;
import atotb.controller.log.AbstractMoveEvent;
import atotb.controller.log.DamageEvent;
import atotb.controller.log.ChargeEvent;
import atotb.controller.log.DashEvent;
import atotb.controller.log.DeathEvent;
import atotb.controller.log.Event;
import atotb.controller.log.EventLog;
import atotb.controller.log.EventVisitor;
import atotb.controller.log.FireEvent;
import atotb.controller.log.MoveEvent;
import atotb.model.Battle;
import atotb.model.HistoryItem;
import atotb.model.Unit;
import atotb.view.tween.DrawableAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.Gdx;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * The view component for battles. Only responsibility is rendering the game
 * screen.
 *
 * @author Ale Strooisma
 */
public class BattleScreen implements Screen, EventVisitor {

	// Received
	private final TwoBrothersGame game;
	private final BattleController controller;
	private final SpriteBatch batch;
	private final BitmapFont font;
	//
	// Owned
	private final OrthographicCamera camera;
	private final OrthographicCamera uiCamera;
	private final Viewport viewport;
	private final Viewport uiViewport;
	private IsometricTiledMapRenderer mapRenderer;
	private final Vector3 vec = new Vector3();
	private int windowWidth;
	private int windowHeight;
	//
	// Animation
	public static final float DEATH_ANIMATION_DURATION = 1f; //Seconds
	public static final float MOVE_ANIMATION_DURATION = 0.2f; //Seconds per square
	public static final float DASH_ANIMATION_DURATION = 0.1f; //Seconds per square
	public static final float DAMAGE_MESSAGE_DURATION = 3f; //Seconds
	public static final float DAMAGE_MESSAGE_DELAY = 0.5f; //Seconds
	public static final float DAMAGE_MESSAGE_MOVE_SPEED = 0.5f; //Squares per second
	public static final float CHARGE_ANIMATION_DURATION = DASH_ANIMATION_DURATION;
	private UnitAppearance[][] appearances;
	private final TweenManager manager = new TweenManager();
	private final EventLog events;
	private float animationDelay = 0;
	private final LinkedList<Message> messages = new LinkedList<Message>();
	private ListIterator<Message> messageIterator = messages.listIterator();

	public BattleScreen(TwoBrothersGame game, BattleController controller, SpriteBatch batch, BitmapFont font) {
		this.game = game;
		this.controller = controller;
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

		// Set up the event log
		events = new EventLog();
		game.getEventLog().register(events);
	}

	public void initBattle(Battle battle) {
		// Populate appearance list
		int numberOfArmies = battle.getArmies().length;
		appearances = new UnitAppearance[numberOfArmies][];
		for (int i = 0; i < numberOfArmies; i++) {
			ArrayList<Unit> units = battle.getArmy(i).getUnits();
			appearances[i] = new UnitAppearance[units.size()];
			for (int j = 0; j < units.size(); j++) {
				Texture t;
				if (units.get(j).getName().equals("Dale")) {
					t = Resources.dale;
				} else if (units.get(j).getName().equals("Harryn")) {
					t = Resources.harryn;
				} else if (units.get(j).getName().equals("Wolf")) {
					t = Resources.wolf;
				} else {
					System.err.println("Unknown unit!");
					t = null;
				}
				appearances[i][j] = new UnitAppearance(units.get(j));
				appearances[i][j].setSprite(t);
			}
		}
	}

	/**
	 * Sets the graphical representation of the map that must be rendered. This
	 * must be set before this screen is set active.
	 *
	 * @param map the map to be rendered
	 */
	public void setMap(TiledMap map) {
		if (mapRenderer != null) {
			mapRenderer.dispose();
		}
		mapRenderer = new IsometricTiledMapRenderer(map, 1, batch);
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public TweenManager getTweenManager() {
		return manager;
	}

	@Override
	public void render(float dt) {
		// Clear buffers and paint the background dark gray
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update the tweens
		manager.update(dt);
		if (animationDelay < dt) {
			animationDelay = 0;
		} else {
			animationDelay -= dt;
		}

		// Process events
		while (!events.isEmpty()) {
			Event e = events.pull();
			e.visit(this);
		}

		// Calculate cursor position
		vec.x = Gdx.input.getX();
		vec.y = Gdx.input.getY();
		camera.unproject(vec);
		screenToTileCoords(vec);
		floor(vec);
		int mouseTileX = (int) vec.x;
		int mouseTileY = (int) vec.y;
		tileToScreenCoords(vec);

		// Render
		renderScene(mouseTileX, mouseTileY);
		renderUI(mouseTileX, mouseTileY);
	}
	
	private void renderScene(int mouseTileX, int mouseTileY) {
		// Get potential mouse action for hover position
		MouseAction ma = controller.getMouseAction(mouseTileX, mouseTileY);
		
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
			case MOVE: //TODO custom cursor
			case NO_TARGET: //TODO custom cursor
			case NOTHING:
				batch.draw(Resources.cursor, vec.x, vec.y);
				break;
//			case OUT_OF_BOUNDS:
//				break;
		}

		// Draw walk/dash range markers - TODO order
		Unit su = controller.getSelectedUnit();
		if (su != null) {
			for (int i = 0; i < game.getModel().getBattle().getBattleMap().getWidth(); i++) {
				for (int j = 0; j < game.getModel().getBattle().getBattleMap().getHeight(); j++) {
					double d = controller.getPathFinder().getDistanceToWithoutCheck(i, j);
					if (d == 0) {
						// Don't draw
					} else if (d <= su.getMovesRemaining()) {
						tileToScreenCoords(i, j, vec);
						batch.draw(Resources.walkMarker, vec.x, vec.y);
					} else if (su.mayDash()
							&& d <= su.getMovesRemaining() + su.getDashDistance()) {
						tileToScreenCoords(i, j, vec);
						batch.draw(Resources.dashMarker, vec.x, vec.y);
					}
//				tileToScreenCoords(i, j, vec);
//				font.draw(batch, "" + game.getModel().getBattle().getBattleMap().getTile(i, j).getTerrain(),vec.x+32-5,vec.y+16+20);
				}
			}
		}

		// Draw unit selection marker underlayer
		if (su != null) {
			tileToScreenCoords(su.getPosition().x,
					su.getPosition().y, vec);
			batch.draw(Resources.selectionMarkerUnder, vec.x - 4, vec.y + 16);
		}

		// Draw units (TODO overlapping?)
		for (UnitAppearance[] appearanceArray : appearances) {
			for (UnitAppearance appearance : appearanceArray) {
				appearance.draw(this, batch, vec);
			}
		}

		// Draw hover or target marker overlayer
		tileToScreenCoords(mouseTileX, mouseTileY, vec);
		switch (ma) {
			case SELECT:
				batch.draw(Resources.hoverMarkerOver, vec.x - 4, vec.y + 16);
				break;
			case TARGET:
				batch.draw(Resources.targetMarkerOver, vec.x - 4, vec.y + 16);
				break;
		}

		// Draw unit selection marker overlayer
		if (su != null) {
			tileToScreenCoords(su.getPosition().x,
					su.getPosition().y, vec);
			batch.draw(Resources.selectionMarkerOver, vec.x - 4, vec.y + 16);
		}

		// Print messages
		messageIterator = messages.listIterator();
		while (messageIterator.hasNext()) {
			Message m = messageIterator.next();
			if (!m.isHidden()) {
				m.draw(this, batch, vec);
			}
		}

		// Finish drawing
		batch.end();
	}

	private void renderUI(int x, int y) {
		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();

		// Tile coordinates of mouse position
		font.draw(batch, "" + x + ", " + y, windowWidth - 50, 20);

		// Selected unit information
		Unit su = controller.getSelectedUnit();
		if (su != null) {
			double dash = su.mayDash() ? su.getDashDistance() : 0;
			int h = 0;
			h = drawString(su.getName(), h);
			h = drawString("HP: " + su.getCurrentHealth() + "\\" + su.getMaxHealth(), h);
			h = drawString("(x, y): " + su.getPosition().x + ", " + su.getPosition().y, h);
			h = drawString("Moves remaining: " + su.getMovesRemaining() + "+" + dash, h);
			h = drawString("May attack: " + (su.mayAttack() ? "yes" : "false"), h);
			int a = controller.getSelectedActionNumber();
			h = drawString("Selected ability: " + (a == -1 ? "none" : "#" + a + " - " + su.getAction(a).getName()), h);
			h = drawString("Opponent: " + (su.getOpponent() == null ? "none" : su.getOpponent().getName()), h);
			h = drawString("History:", h);
			su.getHistory().iter();
			HistoryItem item;
			while ((item = su.getHistory().next()) != null) {
				if (item instanceof HistoryItem.Move) {
					h = drawString("    Moved a distance of " + ((HistoryItem.Move) item).getDistance(), h);
				} else if (item instanceof HistoryItem.Dash) {
					h = drawString("    Dashed", h);
				} else if (item instanceof HistoryItem.Charge) {
					h = drawString("    Charged", h);
				} else if (item instanceof HistoryItem.Fire) {
					h = drawString("    Fired", h);
				} else {
					h = drawString("    Performed " + ((HistoryItem.Ability) item).getAction().getName(), h);
				}
			}
		}

		// Message log
		int N = game.getLog().getLogSize();
		for (int i = N - 1; i >= 0; i--) {
			font.draw(batch, game.getLog().get(i), 10, 20 * (i + 1));
		}

		// Finish UI drawing
		batch.end();
	}
	
	private int drawString(String str, int h) {
		font.draw(batch, str, 10, windowHeight - 10 - h);
		return h + 20;
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
		if (mapRenderer != null) {
			mapRenderer.dispose();
		}
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

	@Override
	public void visitDeathEvent(DeathEvent event) {
		Tween.to(getAppearance(event.getUnit()),
				DrawableAccessor.OPACITY, DEATH_ANIMATION_DURATION)
				.target(0)
				.ease(Linear.INOUT)
				.delay(animationDelay)
				.start(manager);
		animationDelay += DEATH_ANIMATION_DURATION;
	}

	@Override
	public void visitMoveEvent(MoveEvent event) {
		animateAbstractMoveEvent(event, MOVE_ANIMATION_DURATION);
	}

	@Override
	public void visitDashEvent(DashEvent event) {
		animateAbstractMoveEvent(event, DASH_ANIMATION_DURATION);
	}

	@Override
	public void visitChargeEvent(ChargeEvent event) {
		animateAbstractMoveEvent(event, CHARGE_ANIMATION_DURATION);
	}

	@Override
	public void visitDamageEvent(DamageEvent event) {
		Message m = new Message("-" + Math.round(event.getDamage()), font,
				event.getTarget().getPosition().x,
				event.getTarget().getPosition().y);
		m.setHidden(true);
		Tween.set(m, DrawableAccessor.HIDDEN)
				.target(0)
				.delay(animationDelay)
				.start(manager);
		Tween.to(m, DrawableAccessor.OPACITY, DAMAGE_MESSAGE_DURATION)
				.target(0)
				.ease(Quint.IN)
				.delay(animationDelay)
				.start(manager);
		Tween.to(m, DrawableAccessor.POSITION, DAMAGE_MESSAGE_DURATION)
				.targetRelative(DAMAGE_MESSAGE_MOVE_SPEED*DAMAGE_MESSAGE_DURATION, 
						-DAMAGE_MESSAGE_MOVE_SPEED*DAMAGE_MESSAGE_DURATION)
				.ease(Linear.INOUT)
				.delay(animationDelay)
				.start(manager);
		animationDelay += DAMAGE_MESSAGE_DELAY;
		messages.add(m);
	}

	@Override
	public void visitFireEvent(FireEvent event) {
		//TODO animate arrow
	}

	private void animateAbstractMoveEvent(AbstractMoveEvent event, float timePerStep) {
		Unit u = event.getUnit();
		float dx = event.getDestX() - event.getFromX();
		float dy = event.getDestY() - event.getFromY();
		float distance = (float) Math.sqrt(dx * dx + dy * dy);
		Tween.to(getAppearance(u),
				DrawableAccessor.POSITION, distance * timePerStep)
				.target(u.getPosition().x, u.getPosition().y)
				.ease(Linear.INOUT)
				.delay(animationDelay)
				.start(manager);
		animationDelay += distance * timePerStep;
	}

	private UnitAppearance getAppearance(Unit unit) {
		return appearances[unit.getArmy().getIndex()][unit.getIndex()];
	}

	public void removeMessage() {
		messageIterator.remove();
	}
}
