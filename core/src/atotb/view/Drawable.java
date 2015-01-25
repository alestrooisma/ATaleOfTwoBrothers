package atotb.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Base class for drawable objects.
 *
 * @author Ale Strooisma
 */
public abstract class Drawable {

	private float x, y;
	private float opacity = 1;
	private boolean hidden = false;

	public Drawable(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void draw(BattleScreen screen, SpriteBatch batch, Vector3 vec) {
		if (getOpacity() > 0) {
			batch.setColor(1, 1, 1, getOpacity());
			screen.tileToScreenCoords(getX(), getY(), vec);
			drawHook(batch, vec.x - 4, vec.y + 16);
			batch.setColor(1, 1, 1, 1);
		} else {
			opacityZeroHook(screen);
		}
	}

	protected abstract void drawHook(SpriteBatch batch, float x, float y);

	protected void opacityZeroHook(BattleScreen screen) {
	}
}
