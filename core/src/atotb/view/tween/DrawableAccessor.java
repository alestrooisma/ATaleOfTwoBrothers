package atotb.view.tween;

import atotb.view.Drawable;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * A TweenAccessor implementation for UnitAppearance
 *
 * @author Ale Strooisma
 */
public class DrawableAccessor implements TweenAccessor<Drawable> {

	public static final int OPACITY = 1;
	public static final int POSITION = 2;
	public static final int HIDDEN = 3;

	@Override
	public int getValues(Drawable target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
			case OPACITY:
				returnValues[0] = target.getOpacity();
				return 1;
			case HIDDEN:
				returnValues[0] = target.isHidden() ? 1 : 0;
				return 1;
			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(Drawable target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION:
				target.setX(newValues[0]);
				target.setY(newValues[1]);
				break;
			case OPACITY:
				target.setOpacity(newValues[0]);
				break;
			case HIDDEN:
				target.setHidden(newValues[0] == 1);
				break;
			default:
				assert false;
				break;
		}

	}

}
