package atotb.view;

import atotb.model.Unit;
import atotb.view.tween.UnitAppearanceAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.equations.Linear;
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
	private float opacity = 1;

	public UnitAppearance(Unit unit) {
		this.unit = unit;
	}

	public void setSprite(Texture sprite) {
		this.sprite = sprite;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public void draw(BattleScreen screen, SpriteBatch batch, Vector3 vec) {
		if (!unit.isAlive() && opacity == 1) {
			Tween.to(this, UnitAppearanceAccessor.OPACITY, 1.0f)
					.target(0)
					.ease(Linear.INOUT)
					.start(screen.getTweenManager());
		}
		if (opacity > 0) {
			batch.setColor(1, 1, 1, opacity);
			screen.tileToScreenCoords(unit.getPosition().x, unit.getPosition().y, vec);
			batch.draw(sprite, vec.x - 4, vec.y + 16); //TODO hardcoded
			batch.setColor(1, 1, 1, 1);
		}
	}
}
