package imagingbook.common.color;

import imagingbook.core.resource.NamedResource;

public enum IccProfile implements NamedResource {
		AdobeRGB1998("AdobeRGB1998.icc"),
		CIERGB("CIERGB.icc"),
		PAL_SECAM("PAL_SECAM.icc"),
		SMPTE_C("SMPTE-C.icc"),
		VideoHD("VideoHD.icc"),
		VideoNTSC("VideoNTSC.icc"),
		VideoPAL("VideoPAL.icc");
 
		private static final String BASEDIR = "iccProfiles/";
		
		private final String relPath;
		
		@Override
		public String getRelativePath() {
			return relPath;
		}
		
		IccProfile(String filename) {
			this.relPath = BASEDIR + filename;
		}

}
