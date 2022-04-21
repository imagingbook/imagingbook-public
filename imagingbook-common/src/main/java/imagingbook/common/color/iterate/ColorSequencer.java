package imagingbook.common.color.iterate;

import java.awt.Color;
import java.util.Iterator;

/**
 * Interface for color sequencers.
 * They are supposed to iterate infinitely, thus 
 * {@link #hasNext()} returns {@code true} by default.
 * 
 * @author WB
 *
 */
public interface ColorSequencer extends Iterator<Color> {
	
	@Override
	public default boolean hasNext() {
		return true;
	}

}

