package atotb.view;

import atotb.model.Unit;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A class maintaining the view part of a unit.
 *
 * @author Ale Strooisma
 */
public class UnitAppearance extends Drawable {

	private final Unit unit;
	private Texture sprite;

	public UnitAppearance(Unit unit) {
		super(unit.getPosition().x, unit.getPosition().y);
		this.unit = unit;
	}

	public void setSprite(Texture sprite) {
		this.sprite = sprite;
	}

	@Override
	protected void drawHook(SpriteBatch batch, float x, float y) {
		batch.draw(sprite, x, y);
	}
}
