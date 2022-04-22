package imagingbook.sampleimages;

import imagingbook.core.resource.ImageResource;

public enum RansacTestImage implements ImageResource {
	
	NoisyLines("noisy-lines.png"), 
	NoisyCircles("noisy-circles.png"), 
	NoisyEllipses("noisy-ellipses.png");
	
	private static final String BASEDIR = "ransac/";
	
	private final String relPath;
	
	@Override
	public String getRelativePath() {
		return relPath;
	}
	
	RansacTestImage(String filename) {
		this.relPath = BASEDIR + filename;
	}
}
