package atotb.controller;

import atotb.TwoBrothersGame;
import atotb.view.BattleScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author ale
 */
public class BattleController extends ScreenController {

	public BattleController(TwoBrothersGame game, SpriteBatch batch, BitmapFont font) {
		super(new BattleScreen(game, batch, font));
	}

	@Override
	public void update(float f) {
		
	}
	
}
