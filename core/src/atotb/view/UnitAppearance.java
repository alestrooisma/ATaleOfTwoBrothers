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

	private final Unit unit;
	private Texture sprite;

	public UnitAppearance(Unit unit) {
		this.unit = unit;
	}

	public void setSprite(Texture sprite) {
		this.sprite = sprite;
	}

	public void draw(BattleScreen screen, SpriteBatch batch, Vector3 vec) {
		if (unit.isAlive()) {
			screen.tileToScreenCoords(unit.getPosition().x, unit.getPosition().y, vec);
			batch.draw(sprite, vec.x - 4, vec.y + 16); //TODO hardcoded
		}
	}
}
