package imagingbook.common.morphology;

import java.util.ArrayList;
import java.util.List;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

public class BinaryThinning implements BinaryMorphologyOperator {
	
	private static final byte B0 = (byte) 0;
	private static final byte B1 = (byte) 1;
	private static final boolean T = true;
	private static final boolean F = false;
	
	private static final boolean[][] Q = {	// = Q[c][pass]
			{F, F}, // 0
			{F, F}, // 1
			{F, F}, // 2
			{T, T}, // 3
			{F, F}, // 4
			{F, F}, // 5
			{T, T}, // 6
			{T, T}, // 7
			{F, F}, // 8
			{F, F}, // 9
			{F, F}, // 10
			{F, F}, // 11
			{T, T}, // 12
			{F, F}, // 13
			{T, T}, // 14
			{T, T}, // 15
			{F, F}, // 16
			{F, F}, // 17
			{F, F}, // 18
			{F, F}, // 19
			{F, F}, // 20
			{F, F}, // 21
			{F, F}, // 22
			{F, F}, // 23
			{T, T}, // 24
			{F, F}, // 25
			{F, F}, // 26
			{F, F}, // 27
			{T, T}, // 28
			{F, F}, // 29
			{T, T}, // 30
			{T, F}, // 31
			{F, F}, // 32
			{F, F}, // 33
			{F, F}, // 34
			{F, F}, // 35
			{F, F}, // 36
			{F, F}, // 37
			{F, F}, // 38
			{F, F}, // 39
			{F, F}, // 40
			{F, F}, // 41
			{F, F}, // 42
			{F, F}, // 43
			{F, F}, // 44
			{F, F}, // 45
			{F, F}, // 46
			{F, F}, // 47
			{T, T}, // 48
			{F, F}, // 49
			{F, F}, // 50
			{F, F}, // 51
			{F, F}, // 52
			{F, F}, // 53
			{F, F}, // 54
			{F, F}, // 55
			{T, T}, // 56
			{F, F}, // 57
			{F, F}, // 58
			{F, F}, // 59
			{T, T}, // 60
			{F, F}, // 61
			{T, T}, // 62
			{T, F}, // 63
			{F, F}, // 64
			{F, F}, // 65
			{F, F}, // 66
			{F, F}, // 67
			{F, F}, // 68
			{F, F}, // 69
			{F, F}, // 70
			{F, F}, // 71
			{F, F}, // 72
			{F, F}, // 73
			{F, F}, // 74
			{F, F}, // 75
			{F, F}, // 76
			{F, F}, // 77
			{F, F}, // 78
			{F, F}, // 79
			{F, F}, // 80
			{F, F}, // 81
			{F, F}, // 82
			{F, F}, // 83
			{F, F}, // 84
			{F, F}, // 85
			{F, F}, // 86
			{F, F}, // 87
			{F, F}, // 88
			{F, F}, // 89
			{F, F}, // 90
			{F, F}, // 91
			{F, F}, // 92
			{F, F}, // 93
			{F, F}, // 94
			{F, F}, // 95
			{T, T}, // 96
			{F, F}, // 97
			{F, F}, // 98
			{F, F}, // 99
			{F, F}, // 100
			{F, F}, // 101
			{F, F}, // 102
			{F, F}, // 103
			{F, F}, // 104
			{F, F}, // 105
			{F, F}, // 106
			{F, F}, // 107
			{F, F}, // 108
			{F, F}, // 109
			{F, F}, // 110
			{F, F}, // 111
			{T, T}, // 112
			{F, F}, // 113
			{F, F}, // 114
			{F, F}, // 115
			{F, F}, // 116
			{F, F}, // 117
			{F, F}, // 118
			{F, F}, // 119
			{T, T}, // 120
			{F, F}, // 121
			{F, F}, // 122
			{F, F}, // 123
			{T, F}, // 124
			{F, F}, // 125
			{T, F}, // 126
			{F, F}, // 127
			{F, F}, // 128
			{T, T}, // 129
			{F, F}, // 130
			{T, T}, // 131
			{F, F}, // 132
			{F, F}, // 133
			{F, F}, // 134
			{T, T}, // 135
			{F, F}, // 136
			{F, F}, // 137
			{F, F}, // 138
			{F, F}, // 139
			{F, F}, // 140
			{F, F}, // 141
			{F, F}, // 142
			{T, T}, // 143
			{F, F}, // 144
			{F, F}, // 145
			{F, F}, // 146
			{F, F}, // 147
			{F, F}, // 148
			{F, F}, // 149
			{F, F}, // 150
			{F, F}, // 151
			{F, F}, // 152
			{F, F}, // 153
			{F, F}, // 154
			{F, F}, // 155
			{F, F}, // 156
			{F, F}, // 157
			{F, F}, // 158
			{T, F}, // 159
			{F, F}, // 160
			{F, F}, // 161
			{F, F}, // 162
			{F, F}, // 163
			{F, F}, // 164
			{F, F}, // 165
			{F, F}, // 166
			{F, F}, // 167
			{F, F}, // 168
			{F, F}, // 169
			{F, F}, // 170
			{F, F}, // 171
			{F, F}, // 172
			{F, F}, // 173
			{F, F}, // 174
			{F, F}, // 175
			{F, F}, // 176
			{F, F}, // 177
			{F, F}, // 178
			{F, F}, // 179
			{F, F}, // 180
			{F, F}, // 181
			{F, F}, // 182
			{F, F}, // 183
			{F, F}, // 184
			{F, F}, // 185
			{F, F}, // 186
			{F, F}, // 187
			{F, F}, // 188
			{F, F}, // 189
			{F, F}, // 190
			{F, F}, // 191
			{T, T}, // 192
			{T, T}, // 193
			{F, F}, // 194
			{T, T}, // 195
			{F, F}, // 196
			{F, F}, // 197
			{F, F}, // 198
			{F, T}, // 199
			{F, F}, // 200
			{F, F}, // 201
			{F, F}, // 202
			{F, F}, // 203
			{F, F}, // 204
			{F, F}, // 205
			{F, F}, // 206
			{F, T}, // 207
			{F, F}, // 208
			{F, F}, // 209
			{F, F}, // 210
			{F, F}, // 211
			{F, F}, // 212
			{F, F}, // 213
			{F, F}, // 214
			{F, F}, // 215
			{F, F}, // 216
			{F, F}, // 217
			{F, F}, // 218
			{F, F}, // 219
			{F, F}, // 220
			{F, F}, // 221
			{F, F}, // 222
			{F, F}, // 223
			{T, T}, // 224
			{T, T}, // 225
			{F, F}, // 226
			{T, T}, // 227
			{F, F}, // 228
			{F, F}, // 229
			{F, F}, // 230
			{F, T}, // 231
			{F, F}, // 232
			{F, F}, // 233
			{F, F}, // 234
			{F, F}, // 235
			{F, F}, // 236
			{F, F}, // 237
			{F, F}, // 238
			{F, F}, // 239
			{T, T}, // 240
			{F, T}, // 241
			{F, F}, // 242
			{F, T}, // 243
			{F, F}, // 244
			{F, F}, // 245
			{F, F}, // 246
			{F, F}, // 247
			{T, T}, // 248
			{F, T}, // 249
			{F, F}, // 250
			{F, F}, // 251
			{T, F}, // 252
			{F, F}, // 253
			{F, F}, // 254
			{F, F}  // 255
	};
	
//	private final boolean[][] Q; 
	private int iterations = -1;	// iterations performed in last applyTo()
	
	public BinaryThinning() {
//		this.Q = makeDeletionCodeTable();
	}
	
	// ----------------------------------------------------------------------------
	
	@Override
	public void applyTo(ByteProcessor ip) {
		int iMax = ip.getWidth() + ip.getHeight();
		applyTo(ip, iMax);
	}
	
	public void applyTo(ByteProcessor ip, int iMax) {
		int nd;
		int iter = 0;
		do {
			nd = thinOnce(ip);
			iter++;
		} while (nd > 0 && iter <  iMax);
		this.iterations = iter;
	}
	
	public int getIterations() {
		if (iterations < 0) {
			throw new IllegalStateException("no iteration count available, call applyTo() first");
		}
		return iterations;
	}
	
	// ----------------------------------------------------------------------------
	
	// Single thinning iteration. Returns the number of deletions performed (for debugging only).
	public int thinOnce(ByteProcessor ip) {
		final int M = ip.getWidth();
		final int N = ip.getHeight();
		final List<PntInt> D = new ArrayList<>();
//		final byte[] NH = new byte[8];
		int n = 0;
		for (int pass = 0; pass < 2; pass++) {	// make 2 passes
			D.clear();
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					if (ip.get(u, v) > 0) {
//						get8Neighborhood(ip, u, v, NH);
//						int c = get8NeighborhoodIndex(NH);
						int c = get8NeighborhoodIndex(ip, u, v);
						if (Q[c][pass]) {
							D.add(PntInt.from(u, v));
							n = n + 1;
						}
					}
				}
			}			
			for (PntInt p : D) {
				ip.putPixel(p.x, p.y, 0);
			}

		}
		return n;
	}
	
	@SuppressWarnings("unused")
	private void get8Neighborhood(ByteProcessor I, int u, int v, byte[] NH) {
		NH[0] =  binarize(I.getPixel(u+1, v));
		NH[1] =  binarize(I.getPixel(u+1, v-1));
		NH[2] =  binarize(I.getPixel(u, v-1));
		NH[3] =  binarize(I.getPixel(u-1, v-1));
		NH[4] =  binarize(I.getPixel(u-1, v));
		NH[5] =  binarize(I.getPixel(u-1, v+1));
		NH[6] =  binarize(I.getPixel(u, v+1));
		NH[7] =  binarize(I.getPixel(u+1, v+1));
	}
	
	@SuppressWarnings("unused")
	private int get8NeighborhoodIndex(byte[] NH) {
//		int c = 0;
//		for (int i = 0; i < 8; i++) {
//			if (NH[i] != 0) {
//				c = c | (1 << i);
//			}
//		}
//		return c;	// c = 0,...,255
		return NH[0] + NH[1] * 2 + NH[2] * 4 + NH[3] * 8 + NH[4] * 16 
				+ NH[5] * 32 + NH[6] * 64 + NH[7] * 128;
	}
	
	// get neighborhood index directly, without extracting neighborhood vector itself:
	private int get8NeighborhoodIndex(ByteProcessor I, int u, int v) {
		int c = 0;
		if (I.getPixel(u+1, v)   != 0) 	c+= 1;		// NH[0]
		if (I.getPixel(u+1, v-1) != 0) 	c+= 2;		// NH[1]
		if (I.getPixel(u, v-1)   != 0) 	c+= 4; 		// NH[2]
		if (I.getPixel(u-1, v-1) != 0) 	c+= 8;		// NH[3]
		if (I.getPixel(u-1, v)   != 0) 	c+= 16;		// NH[4]
		if (I.getPixel(u-1, v+1) != 0) 	c+= 32;		// NH[5]
		if (I.getPixel(u, v+1)   != 0) 	c+= 64;		// NH[6]
		if (I.getPixel(u+1, v+1) != 0) 	c+= 128;	// NH[7]
		return c;	// c = 0,...,255
	}

	// --------------------------------------------------------------
	
	// only used once to calculate Q
	@SuppressWarnings("unused")
	private static boolean[][] makeDeletionCodeTable() {
		boolean[][] Q = new boolean[256][2];
		for (int i = 0; i < 256; i++) {
			byte[] N = new byte[8];
			for (int j = 0; j < 8; j++) {
				N[j] = ((i & (1 << j)) != 0) ? B1 : B0;		// test bit j of i
			}
//			System.out.format("%d: %s\n", i, Arrays.toString(N));
			Q[i][0] = R1(N);
			Q[i][1] = R2(N);
		}		
		return Q;
	}
	
	// --------------------------------------------------------------

	private static byte binarize(int i) {
		return (i == 0) ? B0 : B1;
	}

	private static boolean R1(byte[] NH) {
		final int b = B(NH);
		final int c = C(NH);
		return 
			//NH[0] == 1 &&
			2 <= b && b <= 6 &&
			c == 1 &&
			NH[6] * NH[0] * NH[2] == 0 &&
			NH[4] * NH[6] * NH[0] == 0;
	}
	
	private static boolean R2(byte[] NH) {
		final int b = B(NH);
		final int c = C(NH);
		return 
			//NH[0] == 1 &&
			2 <= b && b <= 6 &&
			c == 1 &&
			NH[0] * NH[2] * NH[4] == 0 &&
				NH[2] * NH[4] * NH[6] == 0;
	}
	
	private static int B(byte[] NH) {
		return NH[0] + NH[1] + NH[2] + NH[3] + NH[4] + NH[5] + NH[6] + NH[7];
	}
	
	private static int C(byte[] NH) { // NH = (N0, N1, N2, N3, N4, N5, N6, N7)
		int c = 0;
		for (int i = 0; i < 8; i++) {
			c = c + NH[i] * (NH[i] - NH[(i+1) % 8]);
		}
		return c;
	}

	
	// ----------------------------------------------------------------------
	
//	public static void main(String[] ards) {
//		 boolean[][] Q = makeDeletionCodeTable();
//		 for (int i= 0; i < Q.length; i++) {
//			 System.out.println(Arrays.toString(Q[i]) + " // " + i);
//		 }
//	}
}
