package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/*

        No. of Surfaces : 3

         s0   s1    s2
         |    |     |       |
         r0   r1    r2
           d0    d1    d2
      nd0   nd1    nd2    nd3
     abbe0 abbe1  abbe2  abbe3
      gi0   gi1    gi2    gi3

*/

/**
 * レンズ系クラス<BR>
 * 
 * @author Kenta Hirano, isao
 */
public class TLens implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Fナンバー */
	private double fFNumber;

	/** 焦点距離 */
	private double fFocusLength;

	/** 中心軸と像面に届く光束がなす角の最大値, 最大画角 */
	private double fWMax;

	/** 球曲面数 */
	private int fNoOfSurfaces;

	/** 次の面までの間隔 */
	private double fD[]; /* NoOfSurfaces */

	/** ガラス */
	private TGlass fGlasses[]; /* NoOfSurfaces+1 */

	/** 曲面 */
	private TSurface fSurfaces[]; /* NoOfSurfaces */

	/** 絞りの位置 */
	private double fStopPosition;

	/** 絞りの半径 */
	private double fStopR;

	/** レンズ構成情報 */
	private TLensConfig fConfig = new TLensConfig();

	/** 実行可能性 */
	private boolean fFeasible;

	/** 基準線(D線)以外に，C線，g線も評価対象として，色収差の評価を行っているか？ */
	private boolean fChromatic;

	/** 各入射角の歪曲．主光線と理想位置(=f*tan(w))の２乗距離．基準線(D線)で計算． */
	private double[] fDistortions;

	/** 各入射角の解像度．主光線とそれ以外の10本の光線の２乗距離の和．基準線(D線)で計算． */
	private double[] fResolutions;

	/**
	 * 各波長における各入射角の色収差．
	 * 主光線を含む11本の光線について，
	 * 基準線(D線)の光線と対象線(C線またはg線)の光線との２乗距離の和をとったもの．
	 * 配列の添字は[入射各ID][波長ID]．
	 */
	private double[][] fChromaticAbberations;

	/**
	 * コンストラクタ
	 *
	 */
	public TLens() {
		fFeasible = false;
		fChromatic = true;
		fDistortions = new double[TRayConstant.NO_OF_WS];
		fResolutions = new double[TRayConstant.NO_OF_WS];
		fChromaticAbberations = new double[TRayConstant.NO_OF_WS][TWavelength.NO_OF_WAVELENGTHS];
		fFNumber = fFocusLength = fWMax = fStopPosition = -1.0;
		fNoOfSurfaces = 0;
		fSurfaces = new TSurface[fNoOfSurfaces];
		fGlasses = new TGlass[fNoOfSurfaces + 1];
		fGlasses[0] = TGlass.AIR;
		fD = new double[fNoOfSurfaces];
		fConfig = new TLensConfig();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param config       レンズ構成情報
	 * @param noOfSurfaces 面数
	 * @param fNumber      Ｆナンバー
	 * @param focusLength  焦点距離
	 * @param wMax         半画角
	 */
	public TLens(TLensConfig config, double fNumber, double focusLength, double wMax) {
		fFeasible = false;
		fChromatic = true;
		fDistortions = new double[TRayConstant.NO_OF_WS];
		fResolutions = new double[TRayConstant.NO_OF_WS];
		fChromaticAbberations = new double[TRayConstant.NO_OF_WS][TWavelength.NO_OF_WAVELENGTHS];
		setConfig(config);
		fStopPosition = -1.0;
		fFNumber = fNumber;
		fFocusLength = focusLength;
		fWMax = wMax;
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src コピー元
	 */
	public TLens(final TLens src) {
		fFeasible = src.fFeasible;
		fChromatic = src.fChromatic;
		fDistortions = new double[TRayConstant.NO_OF_WS];
		fResolutions = new double[TRayConstant.NO_OF_WS];
		fChromaticAbberations = new double[TRayConstant.NO_OF_WS][TWavelength.NO_OF_WAVELENGTHS];
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			fDistortions[w] = src.fDistortions[w];
			fResolutions[w] = src.fResolutions[w];
			for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
				fChromaticAbberations[w][wl] = src.fChromaticAbberations[w][wl];
			}
		}
		fFNumber = src.fFNumber;
		fFocusLength = src.fFocusLength;
		fWMax = src.fWMax;
		fStopPosition = src.fStopPosition;
		fNoOfSurfaces = src.fNoOfSurfaces;
		fSurfaces = new TSurface[fNoOfSurfaces];
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			fSurfaces[i] = new TSurface();
		}
		fD = new double[fNoOfSurfaces];
		fGlasses = new TGlass[fNoOfSurfaces + 1];
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			fSurfaces[i] = src.fSurfaces[i];
			fD[i] = src.fD[i];
		}
		for (int i = 0; i < fNoOfSurfaces + 1; ++i) {
			fGlasses[i] = src.fGlasses[i];
		}
		fConfig = new TLensConfig(src.fConfig);
	}

	/**
	 * srcをコピーする．
	 * 
	 * @param src コピー元
	 */
	public final void copy(final TLens src) {
		fFeasible = src.fFeasible;
		fChromatic = src.fChromatic;
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			fDistortions[w] = src.fDistortions[w];
			fResolutions[w] = src.fResolutions[w];
			for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
				fChromaticAbberations[w][wl] = src.fChromaticAbberations[w][wl];
			}
		}
		fFNumber = src.fFNumber;
		fFocusLength = src.fFocusLength;
		fWMax = src.fWMax;
		fStopPosition = src.fStopPosition;
		setNoOfSurfaces(src.fNoOfSurfaces);
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			fSurfaces[i].copy(src.fSurfaces[i]);
			fD[i] = src.fD[i];
		}
		for (int i = 0; i < fNoOfSurfaces + 1; ++i) {
			fGlasses[i] = src.fGlasses[i];
		}
		fConfig.copyFrom(src.fConfig);
	}

	/**
	 * 入力ストリームから読み込む
	 * 
	 * @param br 入力ストリーム
	 * @throws IOException
	 */
	public final void readFrom(BufferedReader br) throws IOException {
		String[] tokens;
		tokens = br.readLine().split(" ");
		fFeasible = Boolean.parseBoolean(tokens[0]);
		fChromatic = Boolean.parseBoolean(tokens[1]);
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			tokens = br.readLine().split(" ");
			fDistortions[w] = Double.parseDouble(tokens[0]);
			fResolutions[w] = Double.parseDouble(tokens[1]);
			for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
				fChromaticAbberations[w][wl] = Double.parseDouble(tokens[2 + wl]);
			}
		}
		tokens = br.readLine().split(" ");
		fFNumber = Double.parseDouble(tokens[0]);
		fFocusLength = Double.parseDouble(tokens[1]);
		fWMax = Double.parseDouble(tokens[2]);
		tokens = br.readLine().split(" ");
		fNoOfSurfaces = Integer.parseInt(tokens[0]);
		fStopPosition = Double.parseDouble(tokens[1]);
		fSurfaces = new TSurface[fNoOfSurfaces];
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			fSurfaces[i] = new TSurface();
			fSurfaces[i].readFrom(br);
		}
		fD = new double[fNoOfSurfaces];
		tokens = br.readLine().split(" ");
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			fD[i] = Double.parseDouble(tokens[i]);
		}
		fGlasses = new TGlass[fNoOfSurfaces + 1];
		for (int i = 0; i < fNoOfSurfaces + 1; ++i) {
			fGlasses[i] = new TGlass(br);
		}
		updateSurfacePositions();
		fConfig.readFrom(br);
	}

	/**
	 * 出力ストリームへ書き出す
	 * 
	 * @param pw 出力ストリーム
	 */
	public final void writeTo(PrintWriter pw) {
		pw.println(fFeasible + " " + fChromatic);
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			pw.print(fDistortions[w] + " " + fResolutions[w]);
			for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
				pw.print(" " + fChromaticAbberations[w][wl]);
			}
			pw.println();
		}
		pw.println(fFNumber + " " + fFocusLength + " " + fWMax);
		pw.println(fNoOfSurfaces + " " + fStopPosition);
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			fSurfaces[i].writeTo(pw);
		}
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			pw.print(fD[i] + " ");
		}
		pw.println();
		for (int i = 0; i < fNoOfSurfaces + 1; ++i) {
			fGlasses[i].writeTo(pw);
		}
		fConfig.writeTo(pw);
	}

	/**
	 * 面数を返す．
	 * 
	 * @return 面数
	 */
	public final int getNoOfSurfaces() {
		return fNoOfSurfaces;
	}

	/**
	 * 面数を設定する．
	 * 
	 * @param n 面数
	 */
	public final void setNoOfSurfaces(int n) {
		if (fNoOfSurfaces == n) {
			return;
		}
		fSurfaces = new TSurface[n];
		for (int i = 0; i < n; ++i) {
			fSurfaces[i] = new TSurface();
		}
		fD = new double[n];
		fGlasses = new TGlass[n + 1];
		for (int i = 0; i < n + 1; ++i) {
			fGlasses[i] = TGlass.AIR;
		}
		fNoOfSurfaces = n;
	}

	/**
	 * 面間隔を返す．
	 * 
	 * @param index 添え字
	 * @return 面間隔
	 */
	public final double getD(int index) {
		return fD[index];
	}

	/**
	 * 面間隔を設定する．
	 * 
	 * @param index 添え字
	 * @param d     面間隔
	 */
	public final void setD(int index, double d) {
		fD[index] = d;
	}

	/**
	 * ガラスを設定する．
	 * 
	 * @param index 添字
	 * @param g     ガラス
	 */
	public void setGlass(int index, TGlass g) {
		fGlasses[index] = g;
	}

	/**
	 * ガラスを返す．
	 * 
	 * @param index 添字
	 * @return ガラス
	 */
	public TGlass getGlass(int index) {
		return fGlasses[index];
	}

	/**
	 * 面を返す．
	 * 
	 * @param index 添え字
	 * @return 面
	 */
	public final TSurface surface(int index) {
		return fSurfaces[index];
	}

	/**
	 * 面間隔に基づき面の位置を更新する．
	 *
	 */
	public final void updateSurfacePositions() {
		fSurfaces[0].setPosition(0.0);
		for (int i = 1; i < fNoOfSurfaces; ++i)
			fSurfaces[i].setPosition(fSurfaces[i - 1].getPosition() + fD[i - 1]);
	}

	/**
	 * Fナンバーを返す．
	 * 
	 * @return Fナンバー
	 */
	public final double getFNumber() {
		return fFNumber;
	}

	/**
	 * Ｆナンバーを設定する．
	 * 
	 * @param f Fナンバー
	 */
	public final void setFNumber(double f) {
		fFNumber = f;
	}

	/**
	 * 焦点距離を返す．
	 * 
	 * @return 焦点距離
	 */
	public final double getFocusLength() {
		return fFocusLength;
	}

	/**
	 * 焦点距離を設定する．
	 * 
	 * @param l 焦点距離
	 */
	public final void setFocusLength(double l) {
		fFocusLength = l;
	}

	/**
	 * 最大半画角を返す．
	 * 
	 * @return 最大半画角
	 */
	public final double getWMax() {
		return fWMax;
	}

	/**
	 * 最大半画角を設定する．
	 * 
	 * @param w 最大半画角
	 */
	public final void setWMax(double w) {
		fWMax = w;
	}

	/**
	 * 絞りの位置を返す．
	 * 
	 * @return 絞りの位置
	 */
	public final double getStopPosition() {
		return fStopPosition;
	}

	/**
	 * 絞りの位置を設定する．
	 * 
	 * @param x 絞りの位置
	 */
	public final void setStopPosition(double x) {
		fStopPosition = x;
	}

	/**
	 * 絞りの半径を返す．
	 * 
	 * @return 絞りの半径
	 */
	public final double getStopR() {
		return fStopR;
	}

	/**
	 * 絞りの半径を設定する．
	 * 
	 * @param r 絞りの半径
	 */
	public final void setStopR(double r) {
		fStopR = r;
	}

	/**
	 * フィルム位置を返す．
	 * 
	 * @return フィルム位置
	 */
	public final double getFilmPosition() {
		return fSurfaces[fNoOfSurfaces - 1].getPosition() + fD[fNoOfSurfaces - 1];
	}

	/**
	 * 曲面の交点を計算. 交点があれば, x,y に代入し true を返す.
	 * なければ false を返す.
	 * 
	 * @param index1 曲面番号その1
	 * @param index2 曲面番号その2
	 * @param x      交点のx座標(代入する)
	 * @param y      交点のy座標(代入する)
	 * @return 交点があれば true なければ false
	 */
	public final boolean calcIntersection(int index1, int index2, double x, double y) {
		double a = fSurfaces[index1].getCenter();
		double b = fSurfaces[index2].getCenter();
		double r1 = Math.abs(fSurfaces[index1].getR());
		double r2 = Math.abs(fSurfaces[index2].getR());
		if (b - a == 0.0) {
			return false;
		}
		x = (b * b - a * a + r1 * r1 - r2 * r2) / (2.0 * (b - a));
		double y2 = r1 * r1 - (x - a) * (x - a);
		if (y2 <= 0.0) {
			return false;
		}
		y = Math.sqrt(y2);
		return true;
	}

	/**
	 * レンズ系の範囲を代入する.
	 * 
	 * @param ltrb 4次元配列 順に 左端,トップ(一番高いところ),右端,ボトム(一番低いところ)
	 */
	public final void getExtent(double ltrb[]) {
		ltrb[0] = -10.0;
		ltrb[2] = this.getFilmPosition();
		double top = fFocusLength * Math.tan(fWMax * Math.PI / 180.0);
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			if (top < fSurfaces[i].getHeight())
				top = fSurfaces[i].getHeight();
		}
		ltrb[1] = top;
		ltrb[3] = -top;
	}

	/**
	 * レンズ系の左端を返す.
	 * 
	 * @return 左端
	 */
	public final double getLeft() {
		return -10.0;
	}

	/**
	 * レンズ系のトップ(一番高いところ)を返す.
	 * 
	 * @return トップ(一番高いところ)
	 */
	public double getTop() {
		double top = fFocusLength * Math.tan(fWMax * Math.PI / 180.0);
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			if (top < fSurfaces[i].getHeight())
				top = fSurfaces[i].getHeight();
		}
		return top;
	}

	/**
	 * レンズ系の右端を返す.
	 * 
	 * @return 右端
	 */
	public final double getRight() {
		return this.getFilmPosition();
	}

	/**
	 * レンズ系のボトム(一番低いところ)を返す.
	 * 
	 * @return ボトム(一番低いところ)
	 */
	public final double getBottom() {
		double top = fFocusLength * Math.tan(fWMax * Math.PI / 180.0);
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			if (top < fSurfaces[i].getHeight())
				top = fSurfaces[i].getHeight();
		}
		return -top;
	}

	/**
	 * レンズ構成情報を返す．
	 * 
	 * @return レンズ構成情報
	 */
	public TLensConfig getConfig() {
		return fConfig;
	}

	/**
	 * レンズ構成情報を設定する．
	 * 
	 * @param config レンズ構成情報
	 */
	public void setConfig(TLensConfig config) {
		fConfig.copyFrom(config);
		fNoOfSurfaces = fConfig.getNoOfSurfaces();
		fSurfaces = new TSurface[fNoOfSurfaces];
		for (int i = 0; i < fNoOfSurfaces; ++i) {
			fSurfaces[i] = new TSurface();
		}
		fD = new double[fNoOfSurfaces];
		fGlasses = new TGlass[fNoOfSurfaces + 1];
		for (int i = 0; i < fGlasses.length; ++i) {
			fGlasses[i] = TGlass.AIR;
		}
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		writeTo(pw);
		return sw.toString();
	}

	public static void main(String[] args) throws IOException {
		TLens lens = new TLens();
		BufferedReader br = new BufferedReader(new FileReader("3lens-F3_0-f100-w19.txt"));
		lens.readFrom(br);
		br.close();
		System.out.println(lens);
	}

	/**
	 * 実行可能かどうかを返す．
	 * 
	 * @return 実行可能性
	 */
	public boolean isFeasible() {
		return fFeasible;
	}

	/**
	 * 実行可能性を設定する．
	 * 
	 * @param feasible 実行可能性
	 */
	public void setFeasible(boolean feasible) {
		fFeasible = feasible;
	}

	/**
	 * 色収差の評価の有無を返す．
	 * 
	 * @return true:色収差あり, false:色収差なし
	 */
	public boolean isChromatic() {
		return fChromatic;
	}

	/**
	 * 色収差の評価の有無を設定する．
	 * 
	 * @param chromatic 色収差の評価の有無
	 */
	public void setChromatic(boolean chromatic) {
		fChromatic = chromatic;
	}

	/**
	 * 指定された入射角の歪曲を返す．
	 * 
	 * @param w 入射角 (0:W_0, 1:W_065, 2:W_MAX)．
	 * @return 歪曲
	 */
	public double getDistortion(int w) {
		return fDistortions[w];
	}

	/**
	 * 指定された入射角の歪曲を設定する．．
	 * 
	 * @param w          入射角 (0:W_0, 1:W_065, 2:W_MAX)．
	 * @param distortion 歪曲
	 */
	public void setDistortion(int w, double distortion) {
		fDistortions[w] = distortion;
	}

	/**
	 * 指定された入射角の解像度を返す．
	 * 
	 * @param w 入射角 (0:W_0, 1:W_065, 2:W_MAX)．
	 * @return 解像度
	 */
	public double getResolution(int w) {
		return fResolutions[w];
	}

	/**
	 * 指定された入射角の解像度を設定する．．
	 * 
	 * @param w          入射角 (0:W_0, 1:W_065, 2:W_MAX)．
	 * @param resolution 解像度
	 */
	public void setResolution(int w, double resolution) {
		fResolutions[w] = resolution;
	}

	/**
	 * 指定された入射角および波長の色収差を返す．ただし，wavelength=0（基準線）の場合，もしくは，isChromaticがfalseの場合はゼロを返す．
	 * 
	 * @param w          入射角 (0:W_0, 1:W_065, 2:W_MAX)．
	 * @param wavelength 波長 (0:REF_D, 1:REF_C, 2:REF_G)
	 * @return 色収差．
	 */
	public double getChromaticAbberation(int w, int wavelength) {
		return fChromaticAbberations[w][wavelength];
	}

	/**
	 * 指定された入射角および波長の色収差を設定する．
	 * 
	 * @param w          入射角 (0:W_0, 1:W_065, 2:W_MAX)．
	 * @param wavelength 波長 (0:REF_D, 1:REF_C, 2:REF_G)
	 * @return 色収差．
	 */
	public void setChromaticAbberation(int w, int wavelength, double abberation) {
		fChromaticAbberations[w][wavelength] = abberation;
	}

	public String getLensType() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getNoOfSurfaces(); ++i) {
			if (surface(i).getR() > 0.0) {
				sb.append("+");
			} else {
				sb.append("-");
			}
		}
		return sb.toString();
	}

}
