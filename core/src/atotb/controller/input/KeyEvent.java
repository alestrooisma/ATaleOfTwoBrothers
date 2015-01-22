package atotb.controller.input;

/**
 * An event class used to delay keyboard events such that they can be processed in 
 * the game loop.
 * 
 * @author Ale Strooisma
 * 
 * @todo pooling
 */
public class KeyEvent extends InputEvent {
	private int keycode;

	public KeyEvent(int keycode) {
		this.keycode = keycode;
	}

	public int getKeycode() {
		return keycode;
	}
}
