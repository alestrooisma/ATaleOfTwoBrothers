package atotb.controller.input;

/**
 *
 * @author Ale Strooisma
 */
public class InputEventAdapter implements InputEventListener {
	@Override
	public boolean processMousePressedEvent(int screenX, int screenY, int button) {
		return false;
	}

	@Override
	public boolean processMouseReleasedEvent(int screenX, int screenY, int button) {
		return false;
	}

	@Override
	public boolean processMouseDraggedEvent(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean processKeyEvent(int keycode) {
		return false;
	}
}
