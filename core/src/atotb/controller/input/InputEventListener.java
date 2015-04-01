package atotb.controller.input;

/**
 *
 * @author Ale Strooisma
 */
public interface InputEventListener {
	public boolean processMousePressedEvent(int screenX, int screenY, int button);

	public boolean processMouseReleasedEvent(int screenX, int screenY, int button);

	public boolean processMouseDraggedEvent(int screenX, int screenY);

	public boolean processKeyEvent(int keycode);
}
