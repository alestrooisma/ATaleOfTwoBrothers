package atotb.view;

import atotb.model.Unit;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * A class maintaining the view part of a unit.
 *
 * @author Ale Strooisma
 */
public class UnitAppearance {

	public static final float TIME_PER_STEP = 0.2f;
	private final Unit unit;
	private Texture sprite;
	private float x, y;
	private float opacity = 1;

	public UnitAppearance(Unit unit) {
		this.unit = unit;
		x = unit.getPosition().x;
		y = unit.getPosition().y;
	}

	public void setSprite(Texture sprite) {
		this.sprite = sprite;
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

	public void draw(BattleScreen screen, SpriteBatch batch, Vector3 vec) {
		if (opacity > 0) {
			batch.setColor(1, 1, 1, opacity);
			screen.tileToScreenCoords(x, y, vec);
			batch.draw(sprite, vec.x - 4, vec.y + 16); //TODO hardcoded
			batch.setColor(1, 1, 1, 1);
		}
	}
}
