package atotb;

import atotb.controller.*;
import atotb.controller.ai.ArtificialIntelligence;
import atotb.controller.ai.WolfAI;
import atotb.model.*;
import atotb.model.items.*;
import atotb.util.MessageLog;
import atotb.view.BattleScreen;
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
import java.awt.Point;

public class TwoBrothersGame extends Game {

	// Model
	private Model model;
	private Weapon unarmed;
	//
	// View
	private TiledMap tileMap;
	//
	// Controller
	private BattleController battleController;
	private InputMultiplexer inputHandlers;
	private InputAdapter battleHandler;
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

		unarmed = new MeleeWeapon("Unarmed", "Lacking a melee weapon",
				"This unit does not have a melee weapon, so it has to fight unarmed in hand-to-hand combat.",
				1);

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
		battleController = new BattleController(this);
		battleController.setView(new BattleScreen(this, battleController, batch, font));
	}

	private void setUpController() {
		// Create message log
		log = new MessageLog(10);

		// Set up input listeners
		inputHandlers = new InputMultiplexer();
		inputHandlers.addProcessor(new MainInputHandler(this));
		Gdx.input.setInputProcessor(inputHandlers);
		battleHandler = new BattleInputHandler(battleController);
	}

	// Getters
	//
	public Model getModel() {
		return model;
	}

	public MessageLog getLog() {
		return log;
	}

	public Weapon getUnarmed() {
		return unarmed;
	}
	
	// Game state modifiers
	//
	public void startBattle() {

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
		tileMap = new TmxMapLoader().load("maps/testmap.tmx");
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

		// Add armies to map
		battleMap.addUnit(w1, 4, 9);
		battleMap.addUnit(w2, 11, 2);
		battleMap.addUnit(w3, 13, 5);
		battleMap.addUnit(model.getPlayerParty().getUnits().get(0), 9, 11);
		battleMap.addUnit(model.getPlayerParty().getUnits().get(1), 6, 14);

		// Prepare stuff
		model.setBattle(new Battle(battleMap, model.getPlayerParty(), enemy));
		ArtificialIntelligence[] ai = new ArtificialIntelligence[2];
		ai[1] = new WolfAI();
		battleController.getView().setMap(tileMap);
		setScreen(battleController);
		inputHandlers.addProcessor(battleHandler);
		battleController.startBattle(model.getBattle(), ai);
	}

	public void endBattle() {
		inputHandlers.removeProcessor(battleHandler);
		
		// This is what should be done, depending on how battle end is rendered
		// Possibly this should only be done after leaving the victory screen.
		//setScreen(some other screen);
		//model.setBattle(null);
		//tileMap.dispose();
	}
	
	// The dispose method
	//
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		battleController.dispose();
		Resources.unload();
	}
}
