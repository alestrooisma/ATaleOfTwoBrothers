package atotb.view;

import atotb.model.Unit;
import atotb.view.tween.UnitAppearanceAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
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

	public static final float TIME_PER_STEP = 0.2f;
	private final Unit unit;
	private Texture sprite;
	private boolean moving = false;
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
		BaseTween tween = null;
		if (moving && x == unit.getPosition().x && y == unit.getPosition().y) {
			moving = false;
		} else if (!moving && (x != unit.getPosition().x || y != unit.getPosition().y)) {
			float dx = x - unit.getPosition().x;
			float dy = y - unit.getPosition().y;
			float distance = (float) Math.sqrt(dx * dx + dy * dy);
			tween = Tween
					.to(this, UnitAppearanceAccessor.POSITION, distance * TIME_PER_STEP)
					.target(unit.getPosition().x, unit.getPosition().y)
					.ease(Linear.INOUT);
			moving = true;
		}
		if (!unit.isAlive() && opacity == 1) {
			if (tween == null) {
				tween = dyingTween(screen);
			} else {
				tween = Timeline.createSequence().push((Tween) tween).push(dyingTween(screen));
			}
		}
		if (tween != null) {
			tween.start(screen.getTweenManager());
		}
		if (opacity > 0) {
			batch.setColor(1, 1, 1, opacity);
			screen.tileToScreenCoords(x, y, vec);
			batch.draw(sprite, vec.x - 4, vec.y + 16); //TODO hardcoded
			batch.setColor(1, 1, 1, 1);
		}
	}

	private Tween dyingTween(BattleScreen screen) {
		return Tween
				.to(this, UnitAppearanceAccessor.OPACITY, 1.0f)
				.target(0)
				.ease(Linear.INOUT)
				.start(screen.getTweenManager());
	}
}
