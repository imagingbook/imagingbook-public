package imagingbook.common.pdf;

import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

import imagingbook.core.resource.NamedResource;

/**
 * This class collects the set of 14 Type-1 standard fonts that every
 * PS/PDF reader is supposed to have available by default.
 * The associated resource directory contains both .AFM and .PFB files.
 * iText/OpenPDF never embeds any of these fonts on the exported PDF,
 * which may cause problems during pre-press checking, where generally
 * ALL fonts should be embedded.
 * 
 * @author WB
 *
 */
public enum Type1CoreFont implements NamedResource {
	
	Courier("Courier.afm"),
	CourierBold("Courier-Bold.afm"),
	CourierBoldOblique("Courier-BoldOblique.afm"),
	CourierOblique("Courier-Oblique.afm"),	
	Helvetica("Helvetica.afm"),
	HelveticaBold("Helvetica-Bold.afm"),
	HelveticaBoldOblique("Helvetica-BoldOblique.afm"),
	HelveticaOblique("Helvetica-Oblique.afm"),	
	TimesRoman("Times-Roman.afm"),
	TimesBold("Times-Bold.afm"),
	TimesBoldItalic("Times-BoldItalic.afm"),
	TimesItalic("Times-Italic.afm"),	
	Symbol("Symbol.afm"),
	ZapfDingbats("ZapfDingbats.afm")
	;
	
	private static final String BASEDIR = "fonts/type1/";
	
	private final String relPath;
	private BaseFont basefont = null;		// cached BaseFont instance

	/**
	 * Constructor.
	 * @param afmName the name of the associated AFM file
	 */
	Type1CoreFont(String afmName) {
		this.relPath = BASEDIR + afmName;
	}
	
	@Override
	public String getRelativePath() {
		return relPath;
	}
	
	
	private String getFontPath() {
		return getURL().toString();
	}
	
	/** 
	 * Returns the {@link BaseFont} instance associated with this enum element.
	 * The font is created when this method is called the first time.
	 * 
	 * @return the {@link BaseFont} instance associated with this font
	 */
	public BaseFont getBaseFont() {
		if (basefont == null) {
			try {
				basefont = BaseFont.createFont(getFontPath(), "", BaseFont.EMBEDDED);
			} catch (DocumentException | IOException e) { 
				System.out.println("Trouble: " + e);
			}
		}
		return basefont;
	}

	// -----------------------------------------------
	
	public static void main(String[] args) {
		System.out.println("Anchor is in JAR: " + Type1CoreFont.Courier.isInsideJar());
		for (Type1CoreFont sf : Type1CoreFont.values()) {
//			System.out.println(sf.getRelativePath());
//			System.out.println(sf.getURL());
//			System.out.println(sf.getURI());
//			System.out.println(sf.getStream());
//			System.out.println(sf.getFontPath());
//			System.out.println(sf.getBaseFont());
			System.out.println(sf.toString() + ": " + sf.getBaseFont().getPostscriptFontName());
		}
	}


}

