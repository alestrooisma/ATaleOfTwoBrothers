package atotb.controller;

import atotb.TwoBrothersGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * An input handler for global functionality.
 * 
 * @author Ale Strooisma
 */
public class MainInputHandler extends InputAdapter {

	private final TwoBrothersGame game;

	public MainInputHandler(TwoBrothersGame game) {
		this.game = game;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Keys.ESCAPE:
				Gdx.app.exit();
				return true;
		} 
		return false;
	}

}
