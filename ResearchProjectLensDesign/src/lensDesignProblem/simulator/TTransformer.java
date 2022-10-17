package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;


/** GA決定変数空間-レンズパラメータ空間変換器<BR>
    @author Kenta Hirano */
public class TTransformer {
	
	/** レンズギャップ:最小値 (レンズのガラス幅に関するパラメータ) */
	private double fLensGapMin;
	
	/** レンズギャップ:最大値 (レンズのガラス幅に関するパラメータ) */
	private double fLensGapMax;
	
	/** エアギャップ:最小値 (レンズ間の空気幅に関するパラメータ) */
	private double fAirGapMin;
	
	/** エアギャップ:最大値 (レンズ間の空気幅に関するパラメータ) */
	private double fAirGapMax;
	
	/** 曲率半径:最小値 (レンズ間の空気幅に関するパラメータ) */
	private double fRadiusMin;
	
	/** 曲率半径:最大値 (レンズ間の空気幅に関するパラメータ) */
	private double fRadiusMax;
	
	/** 効率優先の曲率半径計算用パラメータA */
	private double fA;
	
	/** 効率優先の曲率半径計算用パラメータB */
	private double fB;

	/** GAの決定変数の最小値 */
	public static final double GA_MIN = -1000.0;
	
	/** GAの決定変数の最大値 */
	public static final double GA_MAX = 1000.0;

	/** コンバータを作成する */
	public TTransformer() {
		fLensGapMin = 0.0;
		fLensGapMax = 20.0;
		fAirGapMin = 0.0;
		fAirGapMax = 30.0;
		fRadiusMin = 1.0;
		fRadiusMax = 1000.0;
		//	this.calculateAB();
	}

	/** コンバータを作成する
	@param src コピー元     */
	public TTransformer(double lensGapMin, double lensGapMax, 
			              double airGapMin, double airGapMax, 
			              double radiusMin, double radiusMax) {
		fLensGapMin = lensGapMin;
		fLensGapMax = lensGapMax;
		fAirGapMin = airGapMin;
		fAirGapMax = airGapMax;
		fRadiusMin = radiusMin;
		fRadiusMax = radiusMax;
		calculateAB();
	}

	/** コンバータを作成する (コピーコンストラクタ)
	@param src コピー元     */
	public TTransformer(TTransformer src) {
		fLensGapMin = src.fLensGapMin;
		fLensGapMax = src.fLensGapMax;
		fAirGapMin = src.fAirGapMin;
		fAirGapMax = src.fAirGapMax;
		fRadiusMin = src.fRadiusMin;
		fRadiusMax = src.fRadiusMax;
		calculateAB();
	}

	/** 標準出力に出力する */
	public void writeTo() {
		System.out.println(fLensGapMin + " " + fLensGapMax);
		System.out.println(fAirGapMin + " " + fAirGapMax);
		System.out.println(fRadiusMin + " " + fRadiusMax);
	}

	/** ファイルに出力する
	@param pw 出力ストリーム     */
	public void writeTo(PrintWriter pw) {
		pw.println(fLensGapMin + " " + fLensGapMax);
		pw.println(fAirGapMin + " " + fAirGapMax);
		pw.println(fRadiusMin + " " + fRadiusMax);
	}

	/** ファイルから読み込む
	@param br 入力ストリーム     */
	public void readFrom(BufferedReader br) throws IOException {
		try {
			String s;
			StringTokenizer st;
			st = new StringTokenizer(br.readLine(), " ");
			if (st.countTokens() != 2) {
				System.out.println("TConverter readFrom :Read Error");
				System.exit(1);
			}
			s = st.nextToken();
			fLensGapMin = Double.parseDouble(s);
			s = st.nextToken();
			fLensGapMax = Double.parseDouble(s);
			st = new StringTokenizer(br.readLine(), " ");
			if (st.countTokens() != 2) {
				System.out.println("TConverter readFrom :Read Error");
				System.exit(1);
			}
			s = st.nextToken();
			fAirGapMin = Double.parseDouble(s);
			s = st.nextToken();
			fAirGapMax = Double.parseDouble(s);
			st = new StringTokenizer(br.readLine(), " ");
			if (st.countTokens() != 2) {
				System.out.println("TConverter readFrom :Read Error");
				System.exit(1);
			}
			s = st.nextToken();
			fRadiusMin = Double.parseDouble(s);
			s = st.nextToken();
			fRadiusMax = Double.parseDouble(s);
			calculateAB();
		} catch (IOException e) {
			System.out.println("TVector3D readForm: " + e);
			throw e;
		}
	}

	/** 標準出力にコンバータの状態を出力する */
	public void printOn() {
		this.writeTo();
		System.out.println(fA + " " + fB);
	}

	/** レンズギャップパラメータを設定する
	@param min 最小値
	@param max 最大値     */
	public void setLensGapParameters(final double min, final double max) {
		fLensGapMin = min;
		fLensGapMax = max;
	}

	/** エアギャップパラメータを設定する
	@param min 最小値
	@param max 最大値     */
	public void setAirGapParameters(final double min, final double max) {
		fAirGapMin = min;
		fAirGapMax = max;
	}

	/** 曲率半径パラメータを設定する
	@param min 最小値
	@param max 最大値     */
	public void setRadiusParameters(final double min, final double max) {
		fRadiusMin = min;
		fRadiusMax = max;
		this.calculateAB();
	}

//	/** 引数にレンズギャップパラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getLensGapParameters(double[] min, double[] max) {
//		min[0] = fLensGapMin;
//		max[0] = fLensGapMax;
//	}

//	/** 引数にエアギャップパラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getAirGapParameters(double[] min, double[] max) {
//		min[0] = fAirGapMin;
//		max[0] = fAirGapMax;
//	}

//	/** 引数に曲率半径パラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getRadiusParameters(double[] min, double[] max) {
//		min[0] = fRadiusMin;
//		max[0] = fRadiusMax;
//	}

//	/** 引数に屈折率パラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getNdParameters(double[] min, double[] max) {
//		min[0] = TGlassDatabase.ND_MIN;
//		max[0] = TGlassDatabase.ND_MAX;
//	}

//	/** 引数にアッベ数パラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getAbbeParameters(double[] min, double[] max) {
//		min[0] = TGlassDatabase.ABBE_MIN;
//		max[0] = TGlassDatabase.ABBE_MAX;
//	}

	/** レンズ幅について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean lensGap_GaToLens(final double srcX, double[] dstX) {
		/* GAの範囲外 */
		if (srcX < GA_MIN || srcX > GA_MAX) {
			return false;
		}
		/* 一様になるように計算 */
		dstX[0] = fLensGapMin + (srcX - GA_MIN) * (fLensGapMax - fLensGapMin) / (GA_MAX - GA_MIN);
		/* レンズの範囲外 */
		if (dstX[0] < fLensGapMin || dstX[0] > fLensGapMax) {
			return false;
		}
		return true;
	}

	/** レンズ幅について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean lensGap_LensToGa(final double srcD, double[] dstD) {
		/* レンズの範囲外 */
		if (srcD < fLensGapMin || srcD > fLensGapMax) {
			return false;
		}
		/* 一様になるように計算 */
		dstD[0] = GA_MIN + (srcD - fLensGapMin) * (GA_MAX - GA_MIN) / (fLensGapMax - fLensGapMin);
		/* GAの範囲外 */
		if (dstD[0] < GA_MIN || dstD[0] > GA_MAX) {
			return false;
		}
		return true;
	}

	/** 空気幅について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean airGap_GaToLens(final double srcX, double[] dstX) {
		/* GAの範囲外 */
		if (srcX < GA_MIN || srcX > GA_MAX) {
			return false;
		}
		/* 一様になるように計算 */
		dstX[0] = fAirGapMin + (srcX - GA_MIN) * (fAirGapMax - fAirGapMin) / (GA_MAX - GA_MIN);
		/* レンズの範囲外 */
		if (dstX[0] < fAirGapMin || dstX[0] > fAirGapMax) {
			return false;
		}
		return true;
	}

	/** 空気幅について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean airGap_LensToGa(final double srcD, double[] dstD) {
		/* レンズの範囲外 */
		if (srcD < fAirGapMin || srcD > fAirGapMax) {
			return false;
		}
		/* 一様になるように計算 */
		dstD[0] = GA_MIN + (srcD - fAirGapMin) * (GA_MAX - GA_MIN) / (fAirGapMax - fAirGapMin);
		/* GAの範囲外 */ /* c++版? */
		if (dstD[0] < GA_MIN || dstD[0] > GA_MAX) {
			return false;
		}
		return true;
	}

	/** 曲率半径について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean radius_GaToLens(final double srcX, double[] dstX) {
		if (0 <= srcX && srcX <= GA_MAX) {
			dstX[0] = fB / (srcX - fA);
			if (fRadiusMin <= dstX[0] && dstX[0] <= fRadiusMax) {
				return true;
			} else {
				return false;
			}
		} else if (-GA_MAX <= srcX && srcX < 0) {
			dstX[0] = - (fB / (-srcX - fA));
			if (-fRadiusMax <= dstX[0] && dstX[0] <= -fRadiusMin) {
				return true;
			} else {
				return false;
			}
		} else {
			dstX[0] = 0.0;
			return false;
		}
	}

	/** 曲率半径について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean radius_LensToGa(final double srcR, double[] dstR) {
		if (fRadiusMin <= srcR && srcR <= fRadiusMax) {
			dstR[0] = fA + fB / srcR;
			if (0.0 <= dstR[0] && dstR[0] <= GA_MAX) {
				return true;
			} else {
				return false;
			}
		} else if (-fRadiusMax < srcR && srcR <= -fRadiusMin) {
			dstR[0] = - (fA + fB / (-srcR));
			if (GA_MIN <= dstR[0] && dstR[0] < 0.0) {
				return true;
			} else {
				return false;
			}
		} else {
			dstR[0] = 0.0;
			return false;
		}
	}

	/** 屈折率について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean nd_GaToLens(final double srcX, double[] dstX) {
		dstX[0] = (0.7 * srcX + 3500.0) / 2000.0;
		return true;
	}

	/** 屈折率について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean nd_LensToGa(final double srcN, double[] dstN) {
		dstN[0] = (2000.0 * srcN - 3500.0) / 0.7;
		return true;
	}

	/** アッベ数について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean abbe_GaToLens(final double srcX, double[] dstX) {
		dstX[0] = (srcX + 1500.0) / 25.0;
		return true;
	}

	/** アッベ数について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean abbe_LensToGa(final double srcA, double[] dstA) {
		dstA[0] = 25.0 * srcA - 1500.0;
		return true;
	}

	/** 効率の良い計算のためのパラメータ, A,Bを計算し設定する     */
	private void calculateAB() {
		fB = fRadiusMin * fRadiusMax * GA_MAX / (fRadiusMax - fRadiusMin);
		fA = -fB / fRadiusMax;
	}

	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

//	/** 引数にレンズギャップパラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getLensGapParameters(TMyPtrDouble min, TMyPtrDouble max) {
//		min.setValue(fLensGapMin);
//		max.setValue(fLensGapMax);
//	}

	public double getLensGapMin() {
		return fLensGapMin;
	}
	
	public double getLensGapMax() {
		return fLensGapMax;
	}

//	/** 引数にエアギャップパラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getAirGapParameters(TMyPtrDouble min, TMyPtrDouble max) {
//		min.setValue(fAirGapMin);
//		max.setValue(fAirGapMax);
//	}
	
	public double getAirGapMin() {
		return fAirGapMin;
	}
	
	public double getAirGapMax() {
		return fAirGapMax;
	}

//	/** 引数に曲率半径パラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getRadiusParameters(TMyPtrDouble min, TMyPtrDouble max) {
//		min.setValue(fRadiusMin);
//		max.setValue(fRadiusMax);
//	}
	
	public double getRadiumMin() {
		return fRadiusMin;
	}
	
	public double getRadiumMax() {
		return fRadiusMax;
	}

//	/** 引数に屈折率パラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getNdParameters(TMyPtrDouble min, TMyPtrDouble max) {
//		min.setValue(TGlassDatabase.ND_MIN);
//		max.setValue(TGlassDatabase.ND_MAX);
//	}
	
	public double getNdMin() {
		return TGlassDatabase.ND_MIN;
	}
	
	public double getNdMax() {
		return TGlassDatabase.ND_MAX;
	}

//	/** 引数にアッベ数パラメータを代入する
//	@param min 最小値
//	@param max 最大値     */
//	public void getAbbeParameters(TMyPtrDouble min, TMyPtrDouble max) {
//		min.setValue(TGlassDatabase.ABBE_MIN);
//		max.setValue(TGlassDatabase.ABBE_MAX);
//	}
	
	public double getAbbeMin() {
		return TGlassDatabase.ABBE_MIN;
	}
	
	public double getAbbeMax() {
		return TGlassDatabase.ABBE_MAX;
	}

	/** レンズ幅について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean lensGap_GaToLens(final double srcX, TMyPtrDouble dstX) {
		/* GAの範囲外 */
		if (srcX < GA_MIN || srcX > GA_MAX) {
			return false;
		}
		/* 一様になるように計算 */
		dstX.setValue(fLensGapMin + (srcX - GA_MIN) * (fLensGapMax - fLensGapMin) / (GA_MAX - GA_MIN));
		/* レンズの範囲外 */
		if (dstX.getValue() < fLensGapMin || dstX.getValue() > fLensGapMax) {
			return false;
		}
		return true;
	}

	/** レンズ幅について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean lensGap_LensToGa(final double srcD, TMyPtrDouble dstD) {
		/* レンズの範囲外 */
		if (srcD < fLensGapMin || srcD > fLensGapMax) {
			return false;
		}
		/* 一様になるように計算 */
		dstD.setValue(GA_MIN + (srcD - fLensGapMin) * (GA_MAX - GA_MIN) / (fLensGapMax - fLensGapMin));
		/* GAの範囲外 */
		if (dstD.getValue() < GA_MIN || dstD.getValue() > GA_MAX) {
			return false;
		}
		return true;
	}

	/** 空気幅について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean airGap_GaToLens(final double srcX, TMyPtrDouble dstX) {
		/* GAの範囲外 */
		if (srcX < GA_MIN || srcX > GA_MAX) {
			return false;
		}
		/* 一様になるように計算 */
		dstX.setValue(fAirGapMin + (srcX - GA_MIN) * (fAirGapMax - fAirGapMin) / (GA_MAX - GA_MIN));
		/* レンズの範囲外 */
		if (dstX.getValue() < fAirGapMin || dstX.getValue() > fAirGapMax) {
			return false;
		}
		return true;
	}

	/** 空気幅について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean airGap_LensToGa(final double srcD, TMyPtrDouble dstD) {
		/* レンズの範囲外 */
		if (srcD < fAirGapMin || srcD > fAirGapMax) {
			return false;
		}
		/* 一様になるように計算 */
		dstD.setValue(GA_MIN + (srcD - fAirGapMin) * (GA_MAX - GA_MIN) / (fAirGapMax - fAirGapMin));
		/* GAの範囲外 */ /* c++版? */
		if (dstD.getValue() < GA_MIN || dstD.getValue() > GA_MAX) {
			return false;
		}
		return true;
	}

	/** 曲率半径について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean radius_GaToLens(final double srcX, TMyPtrDouble dstX) {
		if (0 <= srcX && srcX <= GA_MAX) {
			dstX.setValue(fB / (srcX - fA));
			if (fRadiusMin <= dstX.getValue() && dstX.getValue() <= fRadiusMax) {
				return true;
			} else {
				return false;
			}
		} else if (-GA_MAX <= srcX && srcX < 0) {
			dstX.setValue(- (fB / (-srcX - fA)));
			if (-fRadiusMax <= dstX.getValue() && dstX.getValue() <= -fRadiusMin) {
				return true;
			} else {
				return false;
			}
		} else {
			dstX.setValue(0.0);
			return false;
		}
	}

	/** 曲率半径について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean radius_LensToGa(final double srcR, TMyPtrDouble dstR) {
		if (fRadiusMin <= srcR && srcR <= fRadiusMax) {
			dstR.setValue(fA + fB / srcR);
			if (0.0 <= dstR.getValue() && dstR.getValue() <= GA_MAX) {
				return true;
			} else {
				return false;
			}
		} else if (-fRadiusMax < srcR && srcR <= -fRadiusMin) {
			dstR.setValue(- (fA + fB / (-srcR)));
			if (GA_MIN <= dstR.getValue() && dstR.getValue() < 0.0) {
				return true;
			} else {
				return false;
			}
		} else {
			dstR.setValue(0.0);
			return false;
		}
	}

	/** 屈折率について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean nd_GaToLens(final double srcX, TMyPtrDouble dstX) {
		dstX.setValue((0.7 * srcX + 3500.0) / 2000.0);
		return true;
	}

	/** 屈折率について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean nd_LensToGa(final double srcN, TMyPtrDouble dstN) {
		dstN.setValue((2000.0 * srcN - 3500.0) / 0.7);
		return true;
	}

	/** アッベ数について, GAの数値からレンズの数値を求める
	@param srcX GAの数値
	@param dstX レンズの数値(幅)
	@return 成否     */
	public boolean abbe_GaToLens(final double srcX, TMyPtrDouble dstX) {
		dstX.setValue((srcX + 1500.0) / 25.0);
		return true;
	}

	/** アッベ数について, レンズの数値からGAの数値を求める
	@param srcD レンズの数値(幅)
	@param dstD GAの数値
	@return 成否     */
	public boolean abbe_LensToGa(final double srcA, TMyPtrDouble dstA) {
		dstA.setValue(25.0 * srcA - 1500.0);
		return true;
	}
	
	public static void main(String[] args) throws IOException {
		TTransformer conv = new TTransformer();
		BufferedReader br = new BufferedReader(new FileReader("TConverterTest.txt"));
		conv.readFrom(br);
		br.close();
		conv.printOn();
	}
	
}
