package atotb.controller;

import atotb.controller.input.KeyEvent;
import atotb.controller.input.MouseEvent;
import atotb.view.AbstractScreen;
import com.badlogic.gdx.InputAdapter;

/**
 * Dispatches input event to the BattleController to be handled in the game loop.
 * 
 * @author Ale Strooisma
 */
public class GeneralInputHandler extends InputAdapter {

	private final AbstractScreen view;

	public GeneralInputHandler(AbstractScreen view) {
		this.view = view;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		view.addEvent(new MouseEvent(MouseEvent.Type.PRESSED, 
				screenX, screenY, button));
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		view.addEvent(new MouseEvent(MouseEvent.Type.DRAGGED, 
				screenX, screenY, -1));
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		view.addEvent(new MouseEvent(MouseEvent.Type.RELEASED, 
				screenX, screenY, button));
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		view.addEvent(new KeyEvent(keycode));
		return true;
	}
}
