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

	@Override
	public int getValues(UnitAppearance target, int tweenType, float[] returnValues) {
		switch (tweenType) {
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
			case OPACITY:
				target.setOpacity(newValues[0]);
				break;
			default:
				assert false;
				break;
		}

	}

}
