package atotb.controller;

import atotb.TwoBrothersGame;
import atotb.controller.events.KeyEvent;
import atotb.controller.events.MouseEvent;
import atotb.view.BattleScreen;
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
		screen.addEvent(new MouseEvent(MouseEvent.Type.PRESSED, 
				screenX, screenY, button));
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screen.addEvent(new MouseEvent(MouseEvent.Type.DRAGGED, 
				screenX, screenY, -1));
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screen.addEvent(new MouseEvent(MouseEvent.Type.RELEASED, 
				screenX, screenY, button));
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		screen.addEvent(new KeyEvent(keycode));
		return true;
	}
}
