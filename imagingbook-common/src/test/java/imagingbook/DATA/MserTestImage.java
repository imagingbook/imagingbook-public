package imagingbook.DATA;

import imagingbook.core.resource.ImageResource;


public enum MserTestImage implements ImageResource {
	AllBlack("all-black.png"),
	Blob1("blob1.png"),  
	Blob3("blob3.png"),            
	BlobLevelTestNoise("blob-level-test-noise.png"),
	BlobsInWhite("blobs-in-white.png"),  
	BoatsTinyB("boats-tiny-b.png"),   
	BoatsTinyW("boats-tiny-w.png"),
	AllWhite("all-white.png"),  
	Blob2("blob2.png"),  
	BlobLevelTest("blob-level-test.png"),  
	BlobOriented("blob-oriented.png"),          
	BoatsTiny("boats-tiny.png"),      
	BoatsTinyBW("boats-tiny-bw.png"),  
	BoatsTinyW2("boats-tiny-w2.png");
	
	private static final String BASEDIR = "mser/";
	
	private final String relPath;
	
	@Override
	public String getRelativePath() {
		return relPath;
	}
	
	MserTestImage(String filename) {
		this.relPath = BASEDIR + filename;
	}
}
