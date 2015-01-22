package atotb.controller;

import atotb.controller.input.KeyEvent;
import atotb.controller.input.MouseEvent;
import com.badlogic.gdx.InputAdapter;

/**
 * Dispatches input event to the BattleController to be handled in the game loop.
 * 
 * @author Ale Strooisma
 */
public class BattleInputHandler extends InputAdapter {

	private final BattleController controller;

	public BattleInputHandler(BattleController screen) {
		this.controller = screen;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		controller.addEvent(new MouseEvent(MouseEvent.Type.PRESSED, 
				screenX, screenY, button));
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		controller.addEvent(new MouseEvent(MouseEvent.Type.DRAGGED, 
				screenX, screenY, -1));
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		controller.addEvent(new MouseEvent(MouseEvent.Type.RELEASED, 
				screenX, screenY, button));
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		controller.addEvent(new KeyEvent(keycode));
		return true;
	}
}
