/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.cie;

import java.awt.geom.Path2D;

/**
 * This class renders the famous CIE xy-chromaticity diagram, also known as the "horeseshoe" curve, delineating the
 * range of visible colors in the xy-diagram. The shape is created in a square canvas whose size can be specified.
 * The result is "upside down", i.e., the vertical coordinate runs from top to bottom.
 */
public class CieXyPlot extends Path2D.Double {

    private final double canvasSize;

    /**
     * Constructor, creates a xy-diagram in a canvas with the specified size.
     * @param CanvasSize the size of the canvas
     */
    public CieXyPlot(double CanvasSize) {
        this.canvasSize = CanvasSize;
        this.drawSelf();
    }

    private void drawSelf() {
        double[][] coords = CIE1964xyz;
        for (int i = 0; i < coords.length; i++) {
            double[] entry = coords[i];
            // double lambda = entry[0];
            double X = entry[1];
            double Y = entry[2];
            double Z = entry[3];
            double[] XYZ = {X, Y, Z};
            double[] xy = CieUtils.XYZToxy(XYZ);
            double xx = xy[0] * canvasSize;
            double yy = (1 - xy[1]) * canvasSize;
            if (i == 0) {
                this.moveTo(xx, yy);
            }
            else {
                this.lineTo(xx, yy);
            }
        }
        this.closePath();
    }

    /**
     * CIE 1964 10-deg chromaticity coordinates. Copied from
     * <a href="http://www.cvrl.org/ccs.htm">http://www.cvrl.org/ccs.htm</a>.
     * The entry format is [ λ, x(λ), y(λ), z(λ)], with wavelength λ (in nm) and xyz chromaticity coordinates. The array
     * is truncated above 720nm, thereby skipping the "dangling" measurements.
     */
    public static final double[][] CIE1964xyz = {
            {360, 0.182218, 0.019978, 0.797804},
            {361, 0.182195, 0.019971, 0.797833},
            {362, 0.182172, 0.019966, 0.797862},
            {363, 0.182149, 0.019957, 0.797893},
            {364, 0.182124, 0.019947, 0.797929},
            {365, 0.182098, 0.019938, 0.797964},
            {366, 0.182069, 0.019928, 0.798003},
            {367, 0.182037, 0.019918, 0.798045},
            {368, 0.182003, 0.019908, 0.798089},
            {369, 0.181962, 0.019895, 0.798142},
            {370, 0.181923, 0.019879, 0.798198},
            {371, 0.181882, 0.019861, 0.798257},
            {372, 0.181830, 0.019850, 0.798320},
            {373, 0.181780, 0.019833, 0.798387},
            {374, 0.181727, 0.019811, 0.798462},
            {375, 0.181671, 0.019797, 0.798532},
            {376, 0.181612, 0.019778, 0.798610},
            {377, 0.181551, 0.019756, 0.798692},
            {378, 0.181482, 0.019735, 0.798783},
            {379, 0.181411, 0.019711, 0.798878},
            {380, 0.181333, 0.019685, 0.798982},
            {381, 0.181252, 0.019658, 0.799090},
            {382, 0.181171, 0.019630, 0.799198},
            {383, 0.181086, 0.019602, 0.799312},
            {384, 0.180997, 0.019572, 0.799430},
            {385, 0.180906, 0.019542, 0.799552},
            {386, 0.180801, 0.019513, 0.799686},
            {387, 0.180696, 0.019475, 0.799830},
            {388, 0.180578, 0.019434, 0.799988},
            {389, 0.180449, 0.019392, 0.800158},
            {390, 0.180313, 0.019348, 0.800339},
            {391, 0.180159, 0.019289, 0.800552},
            {392, 0.180000, 0.019241, 0.800759},
            {393, 0.179830, 0.019177, 0.800993},
            {394, 0.179651, 0.019112, 0.801238},
            {395, 0.179466, 0.019044, 0.801491},
            {396, 0.179271, 0.018969, 0.801760},
            {397, 0.179064, 0.018906, 0.802030},
            {398, 0.178849, 0.018839, 0.802312},
            {399, 0.178620, 0.018771, 0.802609},
            {400, 0.178387, 0.018711, 0.802902},
            {401, 0.178151, 0.018653, 0.803196},
            {402, 0.177899, 0.018586, 0.803514},
            {403, 0.177649, 0.018525, 0.803827},
            {404, 0.177389, 0.018463, 0.804148},
            {405, 0.177122, 0.018402, 0.804476},
            {406, 0.176840, 0.018341, 0.804818},
            {407, 0.176530, 0.018291, 0.805179},
            {408, 0.176209, 0.018232, 0.805559},
            {409, 0.175861, 0.018181, 0.805958},
            {410, 0.175488, 0.018134, 0.806378},
            {411, 0.175085, 0.018083, 0.806832},
            {412, 0.174654, 0.018017, 0.807328},
            {413, 0.174199, 0.017943, 0.807858},
            {414, 0.173723, 0.017870, 0.808406},
            {415, 0.173231, 0.017806, 0.808963},
            {416, 0.172723, 0.017756, 0.809521},
            {417, 0.172208, 0.017724, 0.810068},
            {418, 0.171684, 0.017727, 0.810589},
            {419, 0.171159, 0.017766, 0.811075},
            {420, 0.170634, 0.017849, 0.811517},
            {421, 0.170100, 0.017969, 0.811931},
            {422, 0.169568, 0.018114, 0.812318},
            {423, 0.169020, 0.018284, 0.812696},
            {424, 0.168462, 0.018480, 0.813058},
            {425, 0.167902, 0.018708, 0.813390},
            {426, 0.167333, 0.018968, 0.813698},
            {427, 0.166762, 0.019246, 0.813992},
            {428, 0.166186, 0.019560, 0.814254},
            {429, 0.165607, 0.019911, 0.814483},
            {430, 0.165027, 0.020283, 0.814690},
            {431, 0.164451, 0.020682, 0.814867},
            {432, 0.163883, 0.021099, 0.815018},
            {433, 0.163318, 0.021528, 0.815153},
            {434, 0.162751, 0.021990, 0.815259},
            {435, 0.162170, 0.022487, 0.815343},
            {436, 0.161589, 0.023024, 0.815387},
            {437, 0.160984, 0.023610, 0.815406},
            {438, 0.160358, 0.024251, 0.815391},
            {439, 0.159706, 0.024954, 0.815340},
            {440, 0.159022, 0.025725, 0.815253},
            {441, 0.158320, 0.026530, 0.815150},
            {442, 0.157612, 0.027342, 0.815046},
            {443, 0.156892, 0.028181, 0.814927},
            {444, 0.156153, 0.029066, 0.814780},
            {445, 0.155391, 0.030017, 0.814592},
            {446, 0.154598, 0.031053, 0.814349},
            {447, 0.153769, 0.032194, 0.814037},
            {448, 0.152897, 0.033459, 0.813644},
            {449, 0.151977, 0.034861, 0.813161},
            {450, 0.151001, 0.036439, 0.812560},
            {451, 0.150010, 0.038086, 0.811904},
            {452, 0.149031, 0.039740, 0.811229},
            {453, 0.148043, 0.041448, 0.810509},
            {454, 0.147021, 0.043258, 0.809721},
            {455, 0.145945, 0.045217, 0.808838},
            {456, 0.144788, 0.047373, 0.807839},
            {457, 0.143530, 0.049770, 0.806700},
            {458, 0.142149, 0.052467, 0.805384},
            {459, 0.140620, 0.055500, 0.803880},
            {460, 0.138922, 0.058920, 0.802158},
            {461, 0.137143, 0.062497, 0.800360},
            {462, 0.135380, 0.066053, 0.798567},
            {463, 0.133564, 0.069716, 0.796721},
            {464, 0.131628, 0.073612, 0.794760},
            {465, 0.129520, 0.077870, 0.792610},
            {466, 0.127184, 0.082616, 0.790199},
            {467, 0.124602, 0.087978, 0.787420},
            {468, 0.121766, 0.094084, 0.784150},
            {469, 0.118589, 0.101061, 0.780350},
            {470, 0.115180, 0.109040, 0.775780},
            {471, 0.111711, 0.117809, 0.770480},
            {472, 0.108041, 0.127140, 0.764820},
            {473, 0.104128, 0.137089, 0.758783},
            {474, 0.100014, 0.147718, 0.752268},
            {475, 0.095732, 0.159090, 0.745178},
            {476, 0.091311, 0.171265, 0.737424},
            {477, 0.086777, 0.184300, 0.728924},
            {478, 0.082157, 0.198274, 0.719569},
            {479, 0.077482, 0.213231, 0.709287},
            {480, 0.072777, 0.229239, 0.697984},
            {481, 0.067816, 0.246541, 0.685643},
            {482, 0.062437, 0.265234, 0.672329},
            {483, 0.056780, 0.285099, 0.658121},
            {484, 0.050990, 0.305930, 0.643081},
            {485, 0.045194, 0.327537, 0.627269},
            {486, 0.039531, 0.349718, 0.610751},
            {487, 0.034145, 0.372263, 0.593592},
            {488, 0.029166, 0.394980, 0.575853},
            {489, 0.024740, 0.417662, 0.557599},
            {490, 0.020987, 0.440113, 0.538900},
            {491, 0.017705, 0.463082, 0.519212},
            {492, 0.014628, 0.487198, 0.498174},
            {493, 0.011822, 0.512071, 0.476107},
            {494, 0.009358, 0.537309, 0.453333},
            {495, 0.007302, 0.562523, 0.430175},
            {496, 0.005725, 0.587321, 0.406953},
            {497, 0.004697, 0.611313, 0.383990},
            {498, 0.004281, 0.634109, 0.361610},
            {499, 0.004559, 0.655314, 0.340127},
            {500, 0.005586, 0.674543, 0.319871},
            {501, 0.007435, 0.692178, 0.300387},
            {502, 0.010071, 0.708839, 0.281090},
            {503, 0.013411, 0.724485, 0.262103},
            {504, 0.017374, 0.739078, 0.243548},
            {505, 0.021874, 0.752578, 0.225548},
            {506, 0.026828, 0.764952, 0.208220},
            {507, 0.032156, 0.776141, 0.191703},
            {508, 0.037772, 0.786126, 0.176102},
            {509, 0.043598, 0.794857, 0.161545},
            {510, 0.049540, 0.802302, 0.148157},
            {511, 0.055800, 0.808180, 0.136020},
            {512, 0.062554, 0.812377, 0.125068},
            {513, 0.069726, 0.815109, 0.115165},
            {514, 0.077244, 0.816566, 0.106190},
            {515, 0.085024, 0.816976, 0.098000},
            {516, 0.092995, 0.816520, 0.090485},
            {517, 0.101079, 0.815409, 0.083511},
            {518, 0.109199, 0.813848, 0.076953},
            {519, 0.117276, 0.812043, 0.070681},
            {520, 0.125236, 0.810194, 0.064569},
            {521, 0.133216, 0.807952, 0.058832},
            {522, 0.141380, 0.804898, 0.053722},
            {523, 0.149651, 0.801183, 0.049166},
            {524, 0.158017, 0.796891, 0.045091},
            {525, 0.166408, 0.792172, 0.041421},
            {526, 0.174775, 0.787131, 0.038094},
            {527, 0.183074, 0.781897, 0.035030},
            {528, 0.191260, 0.776595, 0.032144},
            {529, 0.199260, 0.771359, 0.029381},
            {530, 0.207057, 0.766282, 0.026661},
            {531, 0.214640, 0.761260, 0.024100},
            {532, 0.222066, 0.756094, 0.021839},
            {533, 0.229362, 0.750797, 0.019841},
            {534, 0.236550, 0.745380, 0.018070},
            {535, 0.243642, 0.739873, 0.016486},
            {536, 0.250670, 0.734269, 0.015060},
            {537, 0.257660, 0.728601, 0.013739},
            {538, 0.264627, 0.722872, 0.012501},
            {539, 0.271596, 0.717100, 0.011304},
            {540, 0.278588, 0.711300, 0.010112},
            {541, 0.285581, 0.705445, 0.008974},
            {542, 0.292539, 0.699506, 0.007955},
            {543, 0.299468, 0.693488, 0.007044},
            {544, 0.306360, 0.687413, 0.006227},
            {545, 0.313230, 0.681278, 0.005492},
            {546, 0.320079, 0.675100, 0.004821},
            {547, 0.326904, 0.668877, 0.004219},
            {548, 0.333714, 0.662630, 0.003656},
            {549, 0.340511, 0.656364, 0.003125},
            {550, 0.347296, 0.650090, 0.002614},
            {551, 0.354082, 0.643787, 0.002131},
            {552, 0.360869, 0.637436, 0.001695},
            {553, 0.367651, 0.631043, 0.001306},
            {554, 0.374420, 0.624621, 0.000959},
            {555, 0.381161, 0.618164, 0.000675},
            {556, 0.387871, 0.611694, 0.000435},
            {557, 0.394540, 0.605214, 0.000246},
            {558, 0.401160, 0.598730, 0.000110},
            {559, 0.407720, 0.592252, 0.000028},
            {560, 0.414213, 0.585787, 0.000000 },
            {561, 0.420698, 0.579302, 0.000000 },
            {562, 0.427229, 0.572771, 0.000000},
            {563, 0.433789, 0.566211, 0.000000},
            {564, 0.440360, 0.559640, 0.000000},
            {565, 0.446924, 0.553076, 0.000000},
            {566, 0.453464, 0.546536, 0.000000},
            {567, 0.459963, 0.540037, 0.000000},
            {568, 0.466404, 0.533596, 0.000000},
            {569, 0.472768, 0.527232, 0.000000},
            {570, 0.479038, 0.520962, 0.000000},
            {571, 0.485242, 0.514758, 0.000000},
            {572, 0.491413, 0.508587, 0.000000},
            {573, 0.497542, 0.502458, 0.000000},
            {574, 0.503620, 0.496380, 0.000000},
            {575, 0.509641, 0.490359, 0.000000},
            {576, 0.515594, 0.484406, 0.000000},
            {577, 0.521471, 0.478529, 0.000000},
            {578, 0.527264, 0.472736, 0.000000},
            {579, 0.532960, 0.467040, 0.000000},
            {580, 0.538560, 0.461440, 0.000000},
            {581, 0.544082, 0.455918, 0.000000},
            {582, 0.549537, 0.450463, 0.000000},
            {583, 0.554921, 0.445079, 0.000000},
            {584, 0.560219, 0.439781, 0.000000},
            {585, 0.565444, 0.434556, 0.000000},
            {586, 0.570568, 0.429432, 0.000000},
            {587, 0.575590, 0.424410, 0.000000},
            {588, 0.580502, 0.419498, 0.000000},
            {589, 0.585296, 0.414704, 0.000000},
            {590, 0.589960, 0.410040, 0.000000},
            {591, 0.594512, 0.405488, 0.000000},
            {592, 0.598946, 0.401054, 0.000000},
            {593, 0.603271, 0.396729, 0.000000},
            {594, 0.607487, 0.392513, 0.000000},
            {595, 0.611597, 0.388403, 0.000000},
            {596, 0.615604, 0.384396, 0.000000},
            {597, 0.619508, 0.380492, 0.000000},
            {598, 0.623312, 0.376688, 0.000000},
            {599, 0.627019, 0.372981, 0.000000},
            {600, 0.630629, 0.369371, 0.000000},
            {601, 0.634137, 0.365863, 0.000000},
            {602, 0.637539, 0.362461, 0.000000},
            {603, 0.640836, 0.359164, 0.000000},
            {604, 0.644031, 0.355969, 0.000000},
            {605, 0.647127, 0.352873, 0.000000},
            {606, 0.650128, 0.349872, 0.000000},
            {607, 0.653035, 0.346965, 0.000000},
            {608, 0.655851, 0.344149, 0.000000},
            {609, 0.658580, 0.341420, 0.000000},
            {610, 0.661224, 0.338776, 0.000000},
            {611, 0.663777, 0.336223, 0.000000},
            {612, 0.666239, 0.333761, 0.000000},
            {613, 0.668600, 0.331400, 0.000000},
            {614, 0.670873, 0.329127, 0.000000},
            {615, 0.673055, 0.326945, 0.000000},
            {616, 0.675148, 0.324852, 0.000000},
            {617, 0.677160, 0.322840, 0.000000},
            {618, 0.679075, 0.320925, 0.000000},
            {619, 0.680911, 0.319089, 0.000000},
            {620, 0.682660, 0.317340, 0.000000},
            {621, 0.684307, 0.315693, 0.000000},
            {622, 0.685819, 0.314181, 0.000000},
            {623, 0.687221, 0.312779, 0.000000},
            {624, 0.688530, 0.311470, 0.000000},
            {625, 0.689759, 0.310241, 0.000000},
            {626, 0.690945, 0.309055, 0.000000},
            {627, 0.692088, 0.307912, 0.000000},
            {628, 0.693213, 0.306787, 0.000000},
            {629, 0.694338, 0.305662, 0.000000},
            {630, 0.695483, 0.304517, 0.000000},
            {631, 0.696632, 0.303368, 0.000000},
            {632, 0.697757, 0.302243, 0.000000},
            {633, 0.698859, 0.301141, 0.000000},
            {634, 0.699936, 0.300064, 0.000000},
            {635, 0.700989, 0.299011, 0.000000},
            {636, 0.702017, 0.297983, 0.000000},
            {637, 0.703019, 0.296981, 0.000000},
            {638, 0.703996, 0.296004, 0.000000},
            {639, 0.704947, 0.295053, 0.000000},
            {640, 0.705873, 0.294127, 0.000000},
            {641, 0.706782, 0.293218, 0.000000},
            {642, 0.707680, 0.292320, 0.000000},
            {643, 0.708561, 0.291439, 0.000000},
            {644, 0.709419, 0.290581, 0.000000},
            {645, 0.710249, 0.289751, 0.000000},
            {646, 0.711040, 0.288960, 0.000000},
            {647, 0.711791, 0.288209, 0.000000},
            {648, 0.712491, 0.287509, 0.000000},
            {649, 0.713132, 0.286868, 0.000000},
            {650, 0.713713, 0.286287, 0.000000},
            {651, 0.714218, 0.285782, 0.000000},
            {652, 0.714652, 0.285348, 0.000000},
            {653, 0.715021, 0.284979, 0.000000},
            {654, 0.715340, 0.284660, 0.000000},
            {655, 0.715619, 0.284381, 0.000000},
            {656, 0.715868, 0.284132, 0.000000},
            {657, 0.716100, 0.283900, 0.000000},
            {658, 0.716323, 0.283677, 0.000000},
            {659, 0.716549, 0.283451, 0.000000},
            {660, 0.716790, 0.283210, 0.000000},
            {661, 0.717034, 0.282966, 0.000000},
            {662, 0.717272, 0.282728, 0.000000},
            {663, 0.717482, 0.282518, 0.000000},
            {664, 0.717692, 0.282308, 0.000000},
            {665, 0.717887, 0.282113, 0.000000},
            {666, 0.718080, 0.281920, 0.000000},
            {667, 0.718252, 0.281748, 0.000000},
            {668, 0.718420, 0.281580, 0.000000},
            {669, 0.718580, 0.281420, 0.000000},
            {670, 0.718732, 0.281268, 0.000000},
            {671, 0.718875, 0.281125, 0.000000},
            {672, 0.719007, 0.280993, 0.000000},
            {673, 0.719128, 0.280872, 0.000000},
            {674, 0.719240, 0.280760, 0.000000},
            {675, 0.719344, 0.280656, 0.000000},
            {676, 0.719440, 0.280560, 0.000000},
            {677, 0.719529, 0.280471, 0.000000},
            {678, 0.719612, 0.280388, 0.000000},
            {679, 0.719689, 0.280311, 0.000000},
            {680, 0.719763, 0.280237, 0.000000},
            {681, 0.719830, 0.280170, 0.000000},
            {682, 0.719888, 0.280112, 0.000000},
            {683, 0.719935, 0.280065, 0.000000},
            {684, 0.719980, 0.280020, 0.000000},
            {685, 0.720016, 0.279984, 0.000000},
            {686, 0.720050, 0.279950, 0.000000},
            {687, 0.720081, 0.279919, 0.000000},
            {688, 0.720107, 0.279893, 0.000000},
            {689, 0.720139, 0.279861, 0.000000},
            {690, 0.720160, 0.279840, 0.000000},
            {691, 0.720194, 0.279806, 0.000000},
            {692, 0.720221, 0.279779, 0.000000},
            {693, 0.720247, 0.279753, 0.000000},
            {694, 0.720275, 0.279725, 0.000000},
            {695, 0.720296, 0.279704, 0.000000},
            {696, 0.720314, 0.279686, 0.000000},
            {697, 0.720331, 0.279669, 0.000000},
            {698, 0.720344, 0.279656, 0.000000},
            {699, 0.720354, 0.279646, 0.000000},
            {700, 0.720358, 0.279642, 0.000000},
            {701, 0.720359, 0.279641, 0.000000},
            {702, 0.720355, 0.279645, 0.000000},
            {703, 0.720348, 0.279652, 0.000000},
            {704, 0.720338, 0.279662, 0.000000},
            {705, 0.720324, 0.279676, 0.000000},
            {706, 0.720308, 0.279692, 0.000000},
            {707, 0.720290, 0.279710, 0.000000},
            {708, 0.720270, 0.279730, 0.000000},
            {709, 0.720248, 0.279752, 0.000000},
            {710, 0.720227, 0.279773, 0.000000},
            {711, 0.720205, 0.279795, 0.000000},
            {712, 0.720178, 0.279822, 0.000000},
            {713, 0.720150, 0.279850, 0.000000},
            {714, 0.720121, 0.279879, 0.000000},
            {715, 0.720090, 0.279910, 0.000000},
            {716, 0.720056, 0.279944, 0.000000},
            {717, 0.720022, 0.279978, 0.000000},
            {718, 0.719987, 0.280013, 0.000000},
            {719, 0.719949, 0.280051, 0.000000},
            {720, 0.719911, 0.280089, 0.000000},
            // {721, 0.719871, 0.280129, 0.000000},
            // {722, 0.719829, 0.280171, 0.000000},
            // {723, 0.719783, 0.280217, 0.000000},
            // {724, 0.719739, 0.280261, 0.000000},
            // {725, 0.719694, 0.280306, 0.000000},
            // {726, 0.719638, 0.280362, 0.000000},
            // {727, 0.719598, 0.280402, 0.000000},
            // {728, 0.719546, 0.280454, 0.000000},
            // {729, 0.719495, 0.280505, 0.000000},
            // {730, 0.719447, 0.280553, 0.000000},
            // {731, 0.719396, 0.280604, 0.000000},
            // {732, 0.719345, 0.280655, 0.000000},
            // {733, 0.719293, 0.280707, 0.000000},
            // {734, 0.719240, 0.280760, 0.000000},
            // {735, 0.719186, 0.280814, 0.000000},
            // {736, 0.719131, 0.280869, 0.000000},
            // {737, 0.719076, 0.280924, 0.000000},
            // {738, 0.719020, 0.280980, 0.000000},
            // {739, 0.718963, 0.281037, 0.000000},
            // {740, 0.718906, 0.281094, 0.000000},
            // {741, 0.718848, 0.281152, 0.000000},
            // {742, 0.718789, 0.281211, 0.000000},
            // {743, 0.718730, 0.281270, 0.000000},
            // {744, 0.718669, 0.281331, 0.000000},
            // {745, 0.718609, 0.281391, 0.000000},
            // {746, 0.718548, 0.281452, 0.000000},
            // {747, 0.718480, 0.281520, 0.000000},
            // {748, 0.718422, 0.281578, 0.000000},
            // {749, 0.718357, 0.281643, 0.000000},
            // {750, 0.718292, 0.281708, 0.000000},
            // {751, 0.718228, 0.281772, 0.000000},
            // {752, 0.718160, 0.281840, 0.000000},
            // {753, 0.718099, 0.281901, 0.000000},
            // {754, 0.718028, 0.281972, 0.000000},
            // {755, 0.717959, 0.282041, 0.000000},
            // {756, 0.717888, 0.282112, 0.000000},
            // {757, 0.717821, 0.282179, 0.000000},
            // {758, 0.717748, 0.282252, 0.000000},
            // {759, 0.717677, 0.282323, 0.000000},
            // {760, 0.717607, 0.282393, 0.000000},
            // {761, 0.717536, 0.282464, 0.000000},
            // {762, 0.717462, 0.282538, 0.000000},
            // {763, 0.717391, 0.282609, 0.000000},
            // {764, 0.717319, 0.282681, 0.000000},
            // {765, 0.717240, 0.282760, 0.000000},
            // {766, 0.717159, 0.282841, 0.000000},
            // {767, 0.717089, 0.282911, 0.000000},
            // {768, 0.717013, 0.282987, 0.000000},
            // {769, 0.716936, 0.283064, 0.000000},
            // {770, 0.716859, 0.283141, 0.000000},
            // {771, 0.716781, 0.283219, 0.000000},
            // {772, 0.716703, 0.283297, 0.000000},
            // {773, 0.716624, 0.283376, 0.000000},
            // {774, 0.716544, 0.283456, 0.000000},
            // {775, 0.716464, 0.283536, 0.000000},
            // {776, 0.716384, 0.283616, 0.000000},
            // {777, 0.716303, 0.283697, 0.000000},
            // {778, 0.716221, 0.283779, 0.000000},
            // {779, 0.716139, 0.283861, 0.000000},
            // {780, 0.716057, 0.283943, 0.000000},
            // {781, 0.715974, 0.284026, 0.000000},
            // {782, 0.715890, 0.284110, 0.000000},
            // {783, 0.715807, 0.284193, 0.000000},
            // {784, 0.715722, 0.284278, 0.000000},
            // {785, 0.715637, 0.284363, 0.000000},
            // {786, 0.715553, 0.284447, 0.000000},
            // {787, 0.715466, 0.284534, 0.000000},
            // {788, 0.715380, 0.284620, 0.000000},
            // {789, 0.715293, 0.284707, 0.000000},
            // {790, 0.715208, 0.284792, 0.000000},
            // {791, 0.715120, 0.284880, 0.000000},
            // {792, 0.715033, 0.284967, 0.000000},
            // {793, 0.714948, 0.285052, 0.000000},
            // {794, 0.714860, 0.285140, 0.000000},
            // {795, 0.714770, 0.285230, 0.000000},
            // {796, 0.714683, 0.285317, 0.000000},
            // {797, 0.714592, 0.285408, 0.000000},
            // {798, 0.714501, 0.285499, 0.000000},
            // {799, 0.714418, 0.285582, 0.000000},
            // {800, 0.714325, 0.285675, 0.000000},
            // {801, 0.714240, 0.285760, 0.000000},
            // {802, 0.714145, 0.285855, 0.000000},
            // {803, 0.714054, 0.285946, 0.000000},
            // {804, 0.713963, 0.286037, 0.000000},
            // {805, 0.713872, 0.286128, 0.000000},
            // {806, 0.713780, 0.286220, 0.000000},
            // {807, 0.713688, 0.286312, 0.000000},
            // {808, 0.713596, 0.286404, 0.000000},
            // {809, 0.713504, 0.286496, 0.000000},
            // {810, 0.713411, 0.286589, 0.000000},
            // {811, 0.713319, 0.286681, 0.000000},
            // {812, 0.713224, 0.286776, 0.000000},
            // {813, 0.713131, 0.286869, 0.000000},
            // {814, 0.713038, 0.286962, 0.000000},
            // {815, 0.712943, 0.287057, 0.000000},
            // {816, 0.712849, 0.287151, 0.000000},
            // {817, 0.712759, 0.287241, 0.000000},
            // {818, 0.712661, 0.287339, 0.000000},
            // {819, 0.712567, 0.287433, 0.000000},
            // {820, 0.712471, 0.287529, 0.000000},
            // {821, 0.712377, 0.287623, 0.000000},
            // {822, 0.712284, 0.287716, 0.000000},
            // {823, 0.712187, 0.287813, 0.000000},
            // {824, 0.712095, 0.287905, 0.000000},
            // {825, 0.711999, 0.288001, 0.000000},
            // {826, 0.711903, 0.288097, 0.000000},
            // {827, 0.711809, 0.288191, 0.000000},
            // {828, 0.711713, 0.288287, 0.000000},
            // {829, 0.711619, 0.288381, 0.000000},
            // {830, 0.711523, 0.288477, 0.000000}
    };
}
