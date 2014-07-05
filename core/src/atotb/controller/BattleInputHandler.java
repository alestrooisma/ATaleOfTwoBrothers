package atotb.controller;

import atotb.TwoBrothersGame;
import atotb.model.Unit;
import atotb.view.BattleScreen;
import atotb.view.BattleScreen.MouseAction;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Ale Strooisma
 */
public class BattleInputHandler extends InputAdapter {

	private final TwoBrothersGame game;
	private final BattleScreen screen;
	private final Vector3 vec = new Vector3();

	public BattleInputHandler(TwoBrothersGame game, BattleScreen screen) {
		this.game = game;
		this.screen = screen;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		vec.x = screenX;
		vec.y = screenY;
		screen.unproject(vec);
		screen.screenToTileCoords(vec);
		int x = (int) vec.x;
		int y = (int) vec.y;
		Unit u = game.getModel().getBattleMap().getTile(x, y).getUnit();
		MouseAction ma = screen.getMouseAction(x, y);
		switch (ma) {
			case SELECT:
				game.selectUnit(u);
				break;
			case MOVE:
				game.moveUnit(game.getSelectedUnit(), x, y);
				break;
			case TARGET:
				game.targetUnit(u);
				break;
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Keys.B:
				game.previousUnit();
				break;
			case Keys.N:
				game.nextUnit();
				break;
			case Keys.ENTER:
				game.endTurn();
				break;
		}
		return false;
	}
}
