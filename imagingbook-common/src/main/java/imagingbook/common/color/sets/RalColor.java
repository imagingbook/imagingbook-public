/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.sets;

import java.awt.Color;


// Experimental!
// from https://gist.github.com/lunohodov/1995178 CORRECT??
// same as in https://woehr-tabellen.industriegehaeuse.de/en/ral-tabellen/ral-colors/
// different to: https://www.ralcolorchart.com/ral-classic
//    https://en.wikipedia.org/wiki/List_of_RAL_colors
//    https://abes-online.com/en/colors/
public enum RalColor implements ColorEnumeration {
	RAL1000(0xBEBD7F, "Green beige"),
	RAL1001(0xC2B078, "Beige"),
	RAL1002(0xC6A664, "Sand yellow"),
	RAL1003(0xE5BE01, "Signal yellow"),
	RAL1004(0xCDA434, "Golden yellow"),
	RAL1005(0xA98307, "Honey yellow"),
	RAL1006(0xE4A010, "Maize yellow"),
	RAL1007(0xDC9D00, "Daffodil yellow"),
	RAL1011(0x8A6642, "Brown beige"),
	RAL1012(0xC7B446, "Lemon yellow"),
	RAL1013(0xEAE6CA, "Oyster white"),
	RAL1014(0xE1CC4F, "Ivory"),
	RAL1015(0xE6D690, "Light ivory"),
	RAL1016(0xEDFF21, "Sulfur yellow"),
	RAL1017(0xF5D033, "Saffron yellow"),
	RAL1018(0xF8F32B, "Zinc yellow"),
	RAL1019(0x9E9764, "Grey beige"),
	RAL1020(0x999950, "Olive yellow"),
	RAL1021(0xF3DA0B, "Rape yellow"),
	RAL1023(0xFAD201, "Traffic yellow"),
	RAL1024(0xAEA04B, "Ochre yellow"),
	RAL1026(0xFFFF00, "Luminous yellow"),
	RAL1027(0x9D9101, "Curry"),
	RAL1028(0xF4A900, "Melon yellow"),
	RAL1032(0xD6AE01, "Broom yellow"),
	RAL1033(0xF3A505, "Dahlia yellow"),
	RAL1034(0xEFA94A, "Pastel yellow"),
	RAL1035(0x6A5D4D, "Pearl beige"),
	RAL1036(0x705335, "Pearl gold"),
	RAL1037(0xF39F18, "Sun yellow"),
	RAL2000(0xED760E, "Yellow orange"),
	RAL2001(0xC93C20, "Red orange"),
	RAL2002(0xCB2821, "Vermilion"),
	RAL2003(0xFF7514, "Pastel orange"),
	RAL2004(0xF44611, "Pure orange"),
	RAL2005(0xFF2301, "Luminous orange"),
	RAL2007(0xFFA420, "Luminous bright orange"),
	RAL2008(0xF75E25, "Bright red orange"),
	RAL2009(0xF54021, "Traffic orange"),
	RAL2010(0xD84B20, "Signal orange"),
	RAL2011(0xEC7C26, "Deep orange"),
	RAL2012(0xE55137, "Salmon range"),
	RAL2013(0xC35831, "Pearl orange"),
	RAL3000(0xAF2B1E, "Flame red"),
	RAL3001(0xA52019, "Signal red"),
	RAL3002(0xA2231D, "Carmine red"),
	RAL3003(0x9B111E, "Ruby red"),
	RAL3004(0x75151E, "Purple red"),
	RAL3005(0x5E2129, "Wine red"),
	RAL3007(0x412227, "Black red"),
	RAL3009(0x642424, "Oxide red"),
	RAL3011(0x781F19, "Brown red"),
	RAL3012(0xC1876B, "Beige red"),
	RAL3013(0xA12312, "Tomato red"),
	RAL3014(0xD36E70, "Antique pink"),
	RAL3015(0xEA899A, "Light pink"),
	RAL3016(0xB32821, "Coral red"),
	RAL3017(0xE63244, "Rose"),
	RAL3018(0xD53032, "Strawberry red"),
	RAL3020(0xCC0605, "Traffic red"),
	RAL3022(0xD95030, "Salmon pink"),
	RAL3024(0xF80000, "Luminous red"),
	RAL3026(0xFE0000, "Luminous bright red"),
	RAL3027(0xC51D34, "Raspberry red"),
	RAL3028(0xCB3234, "Pure  red"),
	RAL3031(0xB32428, "Orient red"),
	RAL3032(0x721422, "Pearl ruby red"),
	RAL3033(0xB44C43, "Pearl pink"),
	RAL4001(0x6D3F5B, "Red lilac"),
	RAL4002(0x922B3E, "Red violet"),
	RAL4003(0xDE4C8A, "Heather violet"),
	RAL4004(0x641C34, "Claret violet"),
	RAL4005(0x6C4675, "Blue lilac"),
	RAL4006(0xA03472, "Traffic purple"),
	RAL4007(0x4A192C, "Purple violet"),
	RAL4008(0x924E7D, "Signal violet"),
	RAL4009(0xA18594, "Pastel violet"),
	RAL4010(0xCF3476, "Telemagenta"),
	RAL4011(0x8673A1, "Pearl violet"),
	RAL4012(0x6C6874, "Pearl black berry"),
	RAL5000(0x354D73, "Violet blue"),
	RAL5001(0x1F3438, "Green blue"),
	RAL5002(0x20214F, "Ultramarine blue"),
	RAL5003(0x1D1E33, "Saphire blue"),
	RAL5004(0x18171C, "Black blue"),
	RAL5005(0x1E2460, "Signal blue"),
	RAL5007(0x3E5F8A, "Brillant blue"),
	RAL5008(0x26252D, "Grey blue"),
	RAL5009(0x025669, "Azure blue"),
	RAL5010(0x0E294B, "Gentian blue"),
	RAL5011(0x231A24, "Steel blue"),
	RAL5012(0x3B83BD, "Light blue"),
	RAL5013(0x1E213D, "Cobalt blue"),
	RAL5014(0x606E8C, "Pigeon blue"),
	RAL5015(0x2271B3, "Sky blue"),
	RAL5017(0x063971, "Traffic blue"),
	RAL5018(0x3F888F, "Turquoise blue"),
	RAL5019(0x1B5583, "Capri blue"),
	RAL5020(0x1D334A, "Ocean blue"),
	RAL5021(0x256D7B, "Water blue"),
	RAL5022(0x252850, "Night blue"),
	RAL5023(0x49678D, "Distant blue"),
	RAL5024(0x5D9B9B, "Pastel blue"),
	RAL5025(0x2A6478, "Pearl gentian blue"),
	RAL5026(0x102C54, "Pearl night blue"),
	RAL6000(0x316650, "Patina green"),
	RAL6001(0x287233, "Emerald green"),
	RAL6002(0x2D572C, "Leaf green"),
	RAL6003(0x424632, "Olive green"),
	RAL6004(0x1F3A3D, "Blue green"),
	RAL6005(0x2F4538, "Moss green"),
	RAL6006(0x3E3B32, "Grey olive"),
	RAL6007(0x343B29, "Bottle green"),
	RAL6008(0x39352A, "Brown green"),
	RAL6009(0x31372B, "Fir green"),
	RAL6010(0x35682D, "Grass green"),
	RAL6011(0x587246, "Reseda green"),
	RAL6012(0x343E40, "Black green"),
	RAL6013(0x6C7156, "Reed green"),
	RAL6014(0x47402E, "Yellow olive"),
	RAL6015(0x3B3C36, "Black olive"),
	RAL6016(0x1E5945, "Turquoise green"),
	RAL6017(0x4C9141, "May green"),
	RAL6018(0x57A639, "Yellow green"),
	RAL6019(0xBDECB6, "Pastel green"),
	RAL6020(0x2E3A23, "Chrome green"),
	RAL6021(0x89AC76, "Pale green"),
	RAL6022(0x25221B, "Olive drab"),
	RAL6024(0x308446, "Traffic green"),
	RAL6025(0x3D642D, "Fern green"),
	RAL6026(0x015D52, "Opal green"),
	RAL6027(0x84C3BE, "Light green"),
	RAL6028(0x2C5545, "Pine green"),
	RAL6029(0x20603D, "Mint green"),
	RAL6032(0x317F43, "Signal green"),
	RAL6033(0x497E76, "Mint turquoise"),
	RAL6034(0x7FB5B5, "Pastel turquoise"),
	RAL6035(0x1C542D, "Pearl green"),
	RAL6036(0x193737, "Pearl opal green"),
	RAL6037(0x008F39, "Pure green"),
	RAL6038(0x00BB2D, "Luminous green"),
	RAL7000(0x78858B, "Squirrel grey"),
	RAL7001(0x8A9597, "Silver grey"),
	RAL7002(0x7E7B52, "Olive grey"),
	RAL7003(0x6C7059, "Moss grey"),
	RAL7004(0x969992, "Signal grey"),
	RAL7005(0x646B63, "Mouse grey"),
	RAL7006(0x6D6552, "Beige grey"),
	RAL7008(0x6A5F31, "Khaki grey"),
	RAL7009(0x4D5645, "Green grey"),
	RAL7010(0x4C514A, "Tarpaulin grey"),
	RAL7011(0x434B4D, "Iron grey"),
	RAL7012(0x4E5754, "Basalt grey"),
	RAL7013(0x464531, "Brown grey"),
	RAL7015(0x434750, "Slate grey"),
	RAL7016(0x293133, "Anthracite grey"),
	RAL7021(0x23282B, "Black grey"),
	RAL7022(0x332F2C, "Umbra grey"),
	RAL7023(0x686C5E, "Concrete grey"),
	RAL7024(0x474A51, "Graphite grey"),
	RAL7026(0x2F353B, "Granite grey"),
	RAL7030(0x8B8C7A, "Stone grey"),
	RAL7031(0x474B4E, "Blue grey"),
	RAL7032(0xB8B799, "Pebble grey"),
	RAL7033(0x7D8471, "Cement grey"),
	RAL7034(0x8F8B66, "Yellow grey"),
	RAL7035(0xCBD0CC, "Light grey"),
	RAL7036(0x7F7679, "Platinum grey"),
	RAL7037(0x7D7F7D, "Dusty grey"),
	RAL7038(0xB5B8B1, "Agate grey"),
	RAL7039(0x6C6960, "Quartz grey"),
	RAL7040(0x9DA1AA, "Window grey"),
	RAL7042(0x8D948D, "Traffic grey A"),
	RAL7043(0x4E5452, "Traffic grey B"),
	RAL7044(0xCAC4B0, "Silk grey"),
	RAL7045(0x909090, "Telegrey 1"),
	RAL7046(0x82898F, "Telegrey 2"),
	RAL7047(0xD0D0D0, "Telegrey 4"),
	RAL7048(0x898176, "Pearl mouse grey"),
	RAL8000(0x826C34, "Green brown"),
	RAL8001(0x955F20, "Ochre brown"),
	RAL8002(0x6C3B2A, "Signal brown"),
	RAL8003(0x734222, "Clay brown"),
	RAL8004(0x8E402A, "Copper brown"),
	RAL8007(0x59351F, "Fawn brown"),
	RAL8008(0x6F4F28, "Olive brown"),
	RAL8011(0x5B3A29, "Nut brown"),
	RAL8012(0x592321, "Red brown"),
	RAL8014(0x382C1E, "Sepia brown"),
	RAL8015(0x633A34, "Chestnut brown"),
	RAL8016(0x4C2F27, "Mahogany brown"),
	RAL8017(0x45322E, "Chocolate brown"),
	RAL8019(0x403A3A, "Grey brown"),
	RAL8022(0x212121, "Black brown"),
	RAL8023(0xA65E2E, "Orange brown"),
	RAL8024(0x79553D, "Beige brown"),
	RAL8025(0x755C48, "Pale brown"),
	RAL8028(0x4E3B31, "Terra brown"),
	RAL8029(0x763C28, "Pearl copper"),
	RAL9001(0xFDF4E3, "Cream"),
	RAL9002(0xE7EBDA, "Grey white"),
	RAL9003(0xF4F4F4, "Signal white"),
	RAL9004(0x282828, "Signal black"),
	RAL9005(0x0A0A0A, "Jet black"),
	RAL9006(0xA5A5A5, "White aluminium"),
	RAL9007(0x8F8F8F, "Grey aluminium"),
	RAL9010(0xFFFFFF, "Pure white"),
	RAL9011(0x1C1C1C, "Graphite black"),
	RAL9016(0xF6F6F6, "Traffic white"),
	RAL9017(0x1E1E1E, "Traffic black"),
	RAL9018(0xCFD3CD, "Papyrus white"),
	RAL9022(0x9C9C9C, "Pearl light grey"),
	RAL9023(0x828282, "Pearl dark grey");
	
	private final int r, g, b;
	private final String name;

	RalColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.name = null;
	}
	
	RalColor(int rgb, String name) {
		Color c = new Color(rgb);
		this.r = c.getRed();
		this.g = c.getGreen();
		this.b = c.getBlue();
		this.name = name;
	}

	@Override
	public Color getColor() {
		return new Color(r, g, b);
	}
	
	public String getName() {
		return this.name;
	}

//	public String getRGBString() {
//		/*
//		 * toHexString will return "0" instead of "00" String.format will not
//		 * 0-pad Strings or Hex Have to do it manually...
//		 */
//		return String.format("#%s%s%s",
//				(r < 0x10 ? "0" : "") + Integer.toHexString(r), (g < 0x10 ? "0"
//						: "") + Integer.toHexString(g), (b < 0x10 ? "0" : "")
//						+ Integer.toHexString(b));
//	}
		
	public static Color[] getColors(RalColor... wcols) {
		Color[] rgbColors = new Color[wcols.length];
		for (int i = 0; i < wcols.length; i++) {
			rgbColors[i] = wcols[i].getColor();
		}
		return rgbColors;
	}
	
	public static final Color[] PreferredColors =
			getColors(
					RAL1004, 
					RAL1026, 
					RAL3020, 
					RAL4006, 
					RAL5002, 
					RAL6037,
					RAL8004
					);
	
}
