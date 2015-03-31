package atotb.view;

import atotb.TwoBrothersGame;
import atotb.controller.AbstractScreenController;
import atotb.controller.input.InputEvent;
import atotb.controller.input.InputEventListener;
import atotb.controller.input.KeyEvent;
import atotb.controller.input.MouseEvent;
import com.badlogic.gdx.Screen;
import java.util.LinkedList;

/**
 *
 * @author Ale Strooisma
 */
public abstract class AbstractScreen<T extends AbstractScreenController> extends InputEventListener implements Screen {
	protected final TwoBrothersGame game;
	protected final T controller;
	private final InputEvent.List inputEvents;
	private final LinkedList<InputEventListener> inputEventListeners;

	public AbstractScreen(TwoBrothersGame game, T controller) {
		this.game = game;
		this.controller = controller;
		inputEvents = new InputEvent.List();
		inputEventListeners = new LinkedList<InputEventListener>();
	}

	public T getController() {
		return controller;
	}
	
	public void addEvent(InputEvent event) {
		inputEvents.add(event);
	}
	
	private void processEvents() {
		InputEvent event = inputEvents.next();
		while (event != null && controller.canContinueProcessingInputEvents()) {
			for (InputEventListener listener : inputEventListeners) {
				event.visit(listener);
			}
			event = inputEvents.next();
		}
	}
	
}
