package imagingbook.common.geometry.mappings;

/**
 * Implementing classes provide an inverse of the associated mapping. 
 * 
 * @author WB
 *
 */
public interface Inversion {
	
	/**
	 * Returns the inverse of this mapping.
	 * 
	 * @return the inverse mapping
	 */
	public Mapping2D getInverse();

}
