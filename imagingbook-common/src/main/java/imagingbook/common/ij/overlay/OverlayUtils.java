package imagingbook.common.ij.overlay;

import ij.gui.Overlay;
import ij.gui.Roi;

public abstract class OverlayUtils {

	/**
	 * Returns a new overlay produced by joining two existing
	 * overlays, which remain unchanged. All involved ROIs are cloned.
	 * 
	 * @param olyA the first overlay
	 * @param olyB the second overlay
	 * @return the new overlay
	 */
	public static Overlay join(Overlay olyA, Overlay olyB) {
		Overlay oly = olyA.duplicate();
		for (Roi roi : olyB) {
			oly.add((Roi)roi.clone());
		}
		return oly;
	}

}
