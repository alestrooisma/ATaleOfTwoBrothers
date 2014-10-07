package atotb.controller;

import com.badlogic.gdx.Screen;

/**
 *
 * @author ale
 */
public abstract class ScreenController implements Screen {

	private final Screen view;

	public ScreenController(Screen view) {
		this.view = view;
	}

	public abstract void update(float f);

	public Screen getView() {
		return view;
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
