package atotb.view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A text message that is to be displayed on the screen.
 *
 * @author Ale Strooisma
 */
public class Message extends Drawable {

	private final String message;
	private final BitmapFont font;

	public Message(String message, BitmapFont font, float x, float y) {
		super(x, y);
		this.message = message;
		this.font = font;
	}

	@Override
	protected void drawHook(SpriteBatch batch, float x, float y) {
		font.setColor(1, 0, 0, getOpacity());
		font.draw(batch, message, x+32, y+48); //TODO Hardcoded
		font.setColor(1, 1, 1, 1);
	}

	@Override
	protected void opacityZeroHook(BattleScreen screen) {
		screen.removeMessage();
	}
}
