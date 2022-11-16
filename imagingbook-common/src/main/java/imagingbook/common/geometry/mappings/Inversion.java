package imagingbook.common.geometry.mappings;

/**
 * Implementing classes provide an inverse of the associated mapping. 
 * 
 * @author WB
 *
 */
public interface Inversion {
	
	/**
	 * The inverse of this mapping is calculated and returned.
	 * 
	 * @return the inverse mapping
	 */
	public Mapping2D getInverse();

}
