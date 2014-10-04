package atotb.controller.events;

/**
 * An event class used to delay mouse events such that they can be processed in 
 * the game loop.
 * 
 * @author Ale Strooisma
 * 
 * @todo pooling
 */
public class MouseEvent extends InputEvent {
	private Type type;
	private int screenX, screenY, button;

	public MouseEvent(Type type, int screenX, int screenY, int button) {
		this.type = type;
		this.screenX = screenX;
		this.screenY = screenY;
		this.button = button;
	}

	public Type getType() {
		return type;
	}

	public int getScreenX() {
		return screenX;
	}

	public int getScreenY() {
		return screenY;
	}

	public int getButton() {
		return button;
	}

	public static enum Type {
		PRESSED, RELEASED, DRAGGED;
	}
}
