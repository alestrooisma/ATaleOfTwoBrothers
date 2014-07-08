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
	private int startX = 0;
	private int startY = 0;
	private final Vector3 vec = new Vector3();

	public BattleInputHandler(TwoBrothersGame game, BattleScreen screen) {
		this.game = game;
		this.screen = screen;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == 0) {
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
			return true;
		} else if (button == 1) {
			startX = screenX;
			startY = screenY;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screen.getCamera().translate(startX - screenX, screenY - startY);
		startX = screenX;
		startY = screenY;
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Keys.R: //TODO temp
				screen.getCamera().zoom = 1;
				screen.getCamera().position.x = 640;
				screen.getCamera().position.y = 16;
				break;
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
