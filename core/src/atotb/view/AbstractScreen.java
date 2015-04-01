package atotb.view;

import atotb.TwoBrothersGame;
import atotb.controller.AbstractScreenController;
import atotb.controller.input.InputEvent;
import atotb.controller.input.InputEventAdapter;
import atotb.controller.input.InputEventListener;
import com.badlogic.gdx.Screen;
import java.util.LinkedList;

/**
 *
 * @author Ale Strooisma
 */
public abstract class AbstractScreen<T extends AbstractScreenController> extends InputEventAdapter implements Screen {
	protected final TwoBrothersGame game;
	protected final T controller;
	private final InputEvent.List inputEvents;
	private final LinkedList<InputEventListener> inputEventListeners;

	public AbstractScreen(TwoBrothersGame game, T controller) {
		this.game = game;
		this.controller = controller;
		inputEvents = new InputEvent.List();
		inputEventListeners = new LinkedList<InputEventListener>();
		inputEventListeners.add(game);
	}

	public T getController() {
		return controller;
	}
	
	public void addEvent(InputEvent event) {
		inputEvents.add(event);
	}
	
	public void processEvents() {
		InputEvent event = inputEvents.next();
		while (event != null) {
			for (InputEventListener listener : inputEventListeners) {
				if (event.visit(listener)) {
					break;
				}
			}
			event = inputEvents.next();
		}
	}

	public LinkedList<InputEventListener> getInputEventListeners() {
		return inputEventListeners;
	}
}
