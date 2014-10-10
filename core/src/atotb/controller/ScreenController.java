package atotb.controller;

import com.badlogic.gdx.Screen;

/**
 * An implementation of this abstract class can be used by an implementation of 
 * Game as if it were a screen, but actually renders a different screen, and is 
 * intended to be used as a controller component.
 *
 * @author Ale Strooisma
 * @param <ViewScreen> The type of the view that is controlled by this class
 */
public abstract class ScreenController<ViewScreen extends Screen> implements Screen {

	private ViewScreen view;
	
	public abstract void update(float f);

	public ViewScreen getView() {
		return view;
	}

	/**
	 * Sets the view managed by this controller.
	 * Must be called before calling setting this ScreenController as an active 
	 * screen by calling Game.setScreen.
	 * @param view 
	 */
	public void setView(ViewScreen view) {
		this.view = view;
	}
	
	@Override
	public void render(float dt) {
		update(dt);
		view.render(dt);
	}

	@Override
	public void resize(int width ,int height) {
		view.resize(width, height);
	}

	@Override
	public void show() {
		view.show();
	}

	@Override
	public void hide() {
		view.hide();
	}

	@Override
	public void pause() {
		view.pause();
	}

	@Override
	public void resume() {
		view.resume();
	}

	@Override
	public void dispose() {
		view.dispose();
	}
}
