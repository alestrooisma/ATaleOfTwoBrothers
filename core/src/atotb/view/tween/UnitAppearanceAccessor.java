package atotb.view.tween;

import atotb.view.UnitAppearance;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * A TweenAccessor implementation for UnitAppearance
 *
 * @author Ale Strooisma
 */
public class UnitAppearanceAccessor implements TweenAccessor<UnitAppearance> {

	public static final int OPACITY = 1;
	public static final int POSITION = 2;

	@Override
	public int getValues(UnitAppearance target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
			case OPACITY:
				returnValues[0] = target.getOpacity();
				return 1;
			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(UnitAppearance target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION:
				target.setX(newValues[0]);
				target.setY(newValues[1]);
				break;
			case OPACITY:
				target.setOpacity(newValues[0]);
				break;
			default:
				assert false;
				break;
		}

	}

}
