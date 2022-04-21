package imagingbook.common.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;

import ij.IJ;

/**
 * Creates a 1-page PDF with samples of all 14 standard Type1 fonts embedded.
 * 
 * https://stackoverflow.com/questions/1775008/embed-font-into-pdf-file-by-using-itext
 * https://stackoverflow.com/questions/2019607/how-to-embed-helvetica-font-in-pdf-using-itext?rq=1
 * https://stackoverflow.com/questions/34328953/embedding-helvetica-neue-deskinterface-font-with-itext
 * https://stackoverflow.com/questions/32457258/pdf-partial-font-embedding-with-itext
 * 
 * AWT/PDF fonts issue: 
 * https://stackoverflow.com/questions/17667615/how-can-itext-embed-font-used-by-jfreechart-for-chart-title-and-labels?rq=1
 * 
 * 
 * TODO: check if change to https://pdfbox.apache.org/ = https://github.com/apache/pdfbox
 * @author WB
 *
 */
public class Type1FontPdfDemo {
	
	static float fontSize = 14f;

	public static void main(String[] args) {
		
//		File file = new File("D:/tmp/Type1FontDemo.pdf");
		File file = new File(System.getProperty("java.io.tmpdir") + "/Type1FontDemo.pdf");
    	System.out.println("writting to " + file.getAbsolutePath());

	    // creation of the document with a certain size and certain margins
	    // may want to use PageSize.LETTER instead
	    Document document = new Document(PageSize.A4, 50, 50, 50, 50);
	    try {
	    	FileOutputStream strm = new FileOutputStream(file);
	        PdfWriter writer = PdfWriter.getInstance(document, strm);

	        document.open();
	        Phrase phrase = new Phrase();

	        phrase.add(new Chunk("Courier", new Font(Type1CoreFont.Courier.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        
	        phrase.add(new Chunk("Courier-Bold", new Font(Type1CoreFont.CourierBold.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        
	        phrase.add(new Chunk("Courier-Oblique", new Font(Type1CoreFont.CourierOblique.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        
	        phrase.add(new Chunk("Courier-BoldOblique", new Font(Type1CoreFont.CourierBoldOblique.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        phrase.add(Chunk.NEWLINE); 

	        phrase.add(new Chunk("Helvetica", new Font(Type1CoreFont.Helvetica.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        
	        phrase.add(new Chunk("Helvetica-Bold", new Font(Type1CoreFont.HelveticaBold.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        
	        phrase.add(new Chunk("Helvetica-BoldOblique", new Font(Type1CoreFont.HelveticaBoldOblique.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        phrase.add(Chunk.NEWLINE);
	        
	        phrase.add(new Chunk("Times", new Font(Type1CoreFont.TimesRoman.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        
	        phrase.add(new Chunk("Times-Bold", new Font(Type1CoreFont.TimesBold.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);

	        phrase.add(new Chunk("Times-BoldItalic", new Font(Type1CoreFont.TimesBoldItalic.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);            

	        phrase.add(new Chunk("Times-Italic", new Font(Type1CoreFont.TimesItalic.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);
	        phrase.add(Chunk.NEWLINE);

	        
	        phrase.add(new Chunk("Symbol", new Font(Type1CoreFont.Symbol.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE); 
	        
	        phrase.add(new Chunk("ZapfDingbats", new Font(Type1CoreFont.ZapfDingbats.getBaseFont(), fontSize)));
	        phrase.add(Chunk.NEWLINE);  

	        document.add(phrase);

	        document.close();
	        writer.close();
	        strm.close();
	        System.out.println("file written to " + file.getAbsolutePath());
	        

	    } catch (Exception ex) {
	        System.err.println(ex.getMessage());
	    }
	    
	    // try to open the PDF:
	    Desktop dt = Desktop.getDesktop();
		try {
			dt.open(file);
		} catch (Exception ex) {
			IJ.error("Could not open PDF file " + file.getAbsolutePath());
		}
	}
	    
}
