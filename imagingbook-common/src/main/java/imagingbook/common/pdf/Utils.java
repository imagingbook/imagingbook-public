package imagingbook.common.pdf;

public abstract class Utils {
	
	public static boolean verifyPdfLib() {
		try {
			if (Class.forName("com.lowagie.text.Document") != null) {
				return true;
			}
		} catch (ClassNotFoundException e) { }
		return false;
	}

}
