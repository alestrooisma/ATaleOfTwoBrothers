package atotb.controller.input;

/**
 *
 * @author Ale Strooisma
 */
public class InputEventListener {
	public boolean processMousePressedEvent(int screenX, int screenY, int button) {
		return false;
	}

	public boolean processMouseReleasedEvent(int screenX, int screenY, int button) {
		return false;
	}

	public boolean processMouseDraggedEvent(int screenX, int screenY) {
		return false;
	}

	public boolean processKeyEvent(int keycode) {
		return false;
	}
}
