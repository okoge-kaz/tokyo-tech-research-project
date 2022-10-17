package lensDesignProblem.simulator;

/**
 * レンズ系の評価器
 * @author hirano, isao
*/
public class TChromaticLensEvaluator {

	/** 色収差を考慮するかどうか，のデフォルト値. trueならする. */
	public static final boolean DEFAULT_CHROMATIC = true;

	/** 絞りの位置の強制(ガラスのはばにあるときの移動)を行うか，のデフォルト値 */
	public static final boolean DEFAULT_ENFORCE_STOP_POSITION = true;

	/** オリジナルの絞りの位置を使うか，のデフォルト値 */
	public static final boolean DEFAULT_USE_ORINAIL_STOP_POSITION = false;

	/** 光線を表示に使うかどうか，のデフォルト値 */
	public static final boolean DEFAULT_USE_RAYS_FOR_DISPLAY = false;

	/** 色収差を考慮するかどうか. trueならする. */
	private boolean fChromatic = TChromaticLensEvaluator.DEFAULT_CHROMATIC;

	/** 絞りの位置の強制(ガラスのはばにあるときの移動)を行うか */
	private boolean fEnforceStopPosition = TChromaticLensEvaluator.DEFAULT_ENFORCE_STOP_POSITION;

	/** オリジナルの絞りの位置を使うか */
	private boolean fUseOriginalStopPosition = TChromaticLensEvaluator.DEFAULT_USE_ORINAIL_STOP_POSITION;

	/** 光線を表示に使うかどうか */
	private boolean fUseRaysForDisplay = TChromaticLensEvaluator.DEFAULT_USE_RAYS_FOR_DISPLAY;

	/** 評価対象になるレンズ系 */
	private TLens fLens;

	/** 光線追跡オペレータ */
	private TRayTracer fRayTracer;

	/** レンズ系に平行に入射する光束の半径 */
	private double fRadiusOfFNumberRay; /* 焦点距離/(2*Fナンバー) */

	/** 光線追跡の開始位置ポジション */
	private double fInitD;

	/** スポットダイアグラムの角度(ラジアン) */
	private double[] fW; /* [NO_OF_WS] */

	/** 光線が最初の曲面入る方向ベクトル */
	private TVector3D[] fInitQ; /* [NO_OF_WS] */

	/** 光線が最初の曲面入る位置ベクトル */
	private TVector3D[][][] fInitT;

	/** 追跡光線 */
	private TRay[][][] fRaysForDisplay;

	/** 計算用の光線 */
	private TRay fRay;

	/** 像面のスポット */
	private TSpot[][] fSpots;

	/** オリジナルの絞り */
	private double fOriginalStopPosition;

	/**
	 * コンストラクタ
	 */
	public TChromaticLensEvaluator() {
		fLens = null;
		fRayTracer = new TRayTracer();
		fInitD = 10.0;
		fW = new double[TRayConstant.NO_OF_WS];
		fInitQ = new TVector3D[TRayConstant.NO_OF_WS];
		for (int i = 0; i < TRayConstant.NO_OF_WS; ++i) {
			fInitQ[i] = TVector3D.newInstance();
		}
		fSpots = new TSpot[TWavelength.NO_OF_WAVELENGTHS][TRayConstant.NO_OF_WS];
		for (int i = 0; i < TWavelength.NO_OF_WAVELENGTHS; ++i) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				fSpots[i][w] = new TSpot(TRayConstant.NO_OF_RAYS);
			}
		}
		fRay = new TRay();
		fInitT = new TVector3D[TWavelength.NO_OF_WAVELENGTHS][TRayConstant.NO_OF_WS][TRayConstant.NO_OF_RAYS];
		for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				for (int i = 0; i < TRayConstant.NO_OF_RAYS; ++i) {
					fInitT[wl][w][i] = TVector3D.newInstance();
				}
			}
		}
		fRaysForDisplay = new TRay[TWavelength.NO_OF_WAVELENGTHS][TRayConstant.NO_OF_WS][TRayConstant.NO_OF_RAYS];
		for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				for (int i = 0; i < TRayConstant.NO_OF_RAYS; ++i) {
					fRaysForDisplay[wl][w][i] = new TRay();
				}
			}
		}
	}

	/**
	 * コンストラクタ
	 * @param chromatic 色収差を考慮するかどうかの設定
	 * @param enforceStopPosition 絞りの位置の強制
	 * @param useRaysForDisplay 追跡光線の表示への使用
	 * @param useOriginalPosition 絞りのオリジナルポジションの使用
	*/
	public TChromaticLensEvaluator(boolean chromatic, boolean enforceStopPosition,
		                             boolean useRaysForDisplay, boolean useOriginalStopPosition) {
		fChromatic = chromatic;
		fEnforceStopPosition = enforceStopPosition;
		fUseRaysForDisplay = useRaysForDisplay;
		fUseOriginalStopPosition = useOriginalStopPosition;
		fLens = null;
		fRayTracer = new TRayTracer();
		fInitD = 10.0;
		fW = new double[TRayConstant.NO_OF_WS];
		fInitQ = new TVector3D[TRayConstant.NO_OF_WS];
		for (int i = 0; i < TRayConstant.NO_OF_WS; ++i) {
			fInitQ[i] = TVector3D.newInstance();
		}
		fSpots = new TSpot[TWavelength.NO_OF_WAVELENGTHS][TRayConstant.NO_OF_WS];
		for (int i = 0; i < TWavelength.NO_OF_WAVELENGTHS; ++i) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				fSpots[i][w] = new TSpot(TRayConstant.NO_OF_RAYS);
			}
		}
		fRay = new TRay();
		fInitT = new TVector3D[TWavelength.NO_OF_WAVELENGTHS][TRayConstant.NO_OF_WS][TRayConstant.NO_OF_RAYS];
		for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				for (int i = 0; i < TRayConstant.NO_OF_RAYS; ++i) {
					fInitT[wl][w][i] = TVector3D.newInstance();
				}
			}
		}
		fRaysForDisplay = new TRay[TWavelength.NO_OF_WAVELENGTHS][TRayConstant.NO_OF_WS][TRayConstant.NO_OF_RAYS];
		for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				for (int i = 0; i < TRayConstant.NO_OF_RAYS; ++i) {
					fRaysForDisplay[wl][w][i] = new TRay();
				}
			}
		}
	}

	/** レンズを評価する.
	@param lens 評価するレンズ
	@return 成否    */
	public final boolean doIt(TLens lens) {
		fLens = lens;
		fLens.setChromatic(fChromatic);
		fLens.setFeasible(false);
		fOriginalStopPosition = fLens.getStopPosition();
		initParameters();
		if (!determineStopPosition()) {
			return false;
		}
		int noOfWavelengths = fChromatic ? TWavelength.NO_OF_WAVELENGTHS : 1;
		for (int wl = 0; wl < noOfWavelengths; ++wl) {
			if (!calculateRaysForDisplay(wl)) {
				return false;
			}
			if (!checkBackFocus(wl)) {
				return false;
			}
			if (!calculateOtherRays(wl)) {
				return false;
			}
			makeSpots(wl);
		}
		calculateDistortionAndResolution();
		if (fChromatic) {
			calculateChromaticAbberations();
		}
		if (fUseRaysForDisplay) {
			prepareForDisplay(noOfWavelengths);
		}
		fLens.setFeasible(true);
		return true;
	}

	private void prepareForDisplay(int noOfWavelengths) {
		adjustLensEdges();
		TVector3D d0 = TVector3D.newInstance(0.0, 0.0, 0.0);
		TVector3D d1 = TVector3D.newInstance();
		for (int wl = 0; wl < noOfWavelengths; ++wl) {
			TRay ray0 = fRaysForDisplay[wl][TRayConstant.W_0][0];
			ray0.clear();
			TRay ray2 = fRaysForDisplay[wl][TRayConstant.W_0][2];
			ray2.clear();
			int max = fRaysForDisplay[wl][TRayConstant.W_0][1].getNoOfVertexes();
			for (int i = 0; i < max; ++i) {
				d1.copy(fRaysForDisplay[wl][TRayConstant.W_0][1].getVector3D(i).getData(0),
					     -fRaysForDisplay[wl][TRayConstant.W_0][1].getVector3D(i).getData(1),
					      fRaysForDisplay[wl][TRayConstant.W_0][1].getVector3D(i).getData(2));
				ray0.appendVertex(d0);
				ray2.appendVertex(d1);
			}
		}
		TVector3D.deleteInstance(d0);
		TVector3D.deleteInstance(d1);
	}

	/** 追跡光線を返す
	@param wavelength 波長番号
	@param w 角度番号
	@param index 光線番号     */
	public final TRay getRaysForDisplay(int wavelength, int w, int index) {
		return fRaysForDisplay[wavelength][w][index];
	}

	/**
	 * 追跡光線を返す．
	 * @param wavelength 波長番号
	 * @param w 角度番号
	 * @return 光線
	 */
	public TRay[] getRaysForDisplay(int wavelength, int w) {
		return fRaysForDisplay[wavelength][w];
	}


	/**
	 * 追跡光線を返す．
	 * @param wavelength 波長番号
	 * @return 光線
	 */
	public TRay[][] getRaysForDisplay(int wavelength) {
		return fRaysForDisplay[wavelength];
	}

	/** スポットを返す
	@param wavelength 波長番号
	@param w 角度番号     */
	public final TSpot getSpot(int wavelength, int w) {
		return fSpots[wavelength][w];
	}

	/**
	 * スポットを返す．
	 * @param wavelength 波長番号
	 * @return スポット
	 */
	public TSpot[] getSpot(int wavelength) {
		return fSpots[wavelength];
	}

	/** 光線が最初の曲面入る位置ベクトルを返す
	@param wavelength 波長番号
	@param w 角度番号
	@param index 光線番号
	@return 初期位置ベクトル T     */
	public final TVector3D getInitT(int wavelength, int w, int index) {
		return fInitT[wavelength][w][index];
	}

	/** パラメータを初期化する.      */
	private final void initParameters() {
		fRay.setNoOfSegments(fLens.getNoOfSurfaces() + 1);
		for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				for (int i = 0; i < TRayConstant.NO_OF_RAYS; ++i) {
					fRaysForDisplay[wl][w][i].setNoOfSegments(fLens.getNoOfSurfaces() + 1);
				}
			}
		}
		clearLensHeight();
		fW[TRayConstant.W_0] = 0.0;
		fW[TRayConstant.W_MAX] = fLens.getWMax() * Math.PI / 180.0;
		/*
		fW[TRayConstant.W_MAX] = fLens.getWMax() * 3.141592 / 180.0;
		*/
		fW[TRayConstant.W_065] = fW[TRayConstant.W_MAX] * 0.65;
		fInitQ[TRayConstant.W_0].copy(1.0, 0.0, 0.0);
		fInitQ[TRayConstant.W_065].copy(Math.cos(fW[TRayConstant.W_065]), Math.sin(fW[TRayConstant.W_065]), 0.0);
		fInitQ[TRayConstant.W_MAX].copy(Math.cos(fW[TRayConstant.W_MAX]), Math.sin(fW[TRayConstant.W_MAX]), 0.0);
		fRadiusOfFNumberRay = fLens.getFocusLength() / (2.0 * fLens.getFNumber());
		double r = fLens.surface(0).getR();
		fInitD = r < -10.0 ? Math.abs(r - 10.0) : 10.0;
	}

	/**
	 *  画面に表示する光線の追跡を行う
	 *  @param wavelength 波長番号
	 */
	private final boolean calculateRaysForDisplay(int wavelength) {
		fInitT[wavelength][TRayConstant.W_0][0].copy(0.0, 0.0, 0.0);
		if (wavelength != TWavelength.REF_D) {
			TVector3D in = TVector3D.newInstance(0.0, 0.0, 0.0);
			TVector3D out = TVector3D.newInstance(0.0, -2.0 * fRadiusOfFNumberRay, 0.0);
			if (!searchLimitT(fInitD, fInitQ[TRayConstant.W_0], in, out,
					              fInitT[wavelength][TRayConstant.W_0][1], wavelength,
					              fRaysForDisplay[wavelength][TRayConstant.W_0][1], false, true)) {
				TVector3D.deleteInstance(in);
				TVector3D.deleteInstance(out);
				return false;
			}
			TVector3D.deleteInstance(in);
			TVector3D.deleteInstance(out);
		}
		fInitT[wavelength][TRayConstant.W_0][2].copy(fInitT[wavelength][TRayConstant.W_0][1]);
		fInitT[wavelength][TRayConstant.W_0][2].scalerProduct(-1.0);
		double x1 = fRaysForDisplay[wavelength][TRayConstant.W_0][1].getVector3D(1).getData(0);
		double y1 = fRaysForDisplay[wavelength][TRayConstant.W_0][1].getVector3D(1).getData(1);
		TVector3D inT = TVector3D.newInstance();
		TVector3D v3duy = TVector3D.newInstance();
		TVector3D v3dly = TVector3D.newInstance();
		for (int w = 1; w < TRayConstant.NO_OF_WS; ++w) {
			double upperY = y1 - (x1 + fInitD) * Math.tan(fW[w]);
			double lowerY = calcLowerY(w);
			//		assert( upperY >= lowerY );
			int div = (int) ((upperY - lowerY) / fRadiusOfFNumberRay) + 1;
			double stepSize = 1.0 / (double) div;
			inT.copy(0.0, 0.0, 0.0);
			v3duy.copy(0.0, upperY, 0.0);
			v3dly.copy(0.0, lowerY, 0.0);
			if (!findFeasibleRay(fInitD, fInitQ[w], v3duy, v3dly, inT, stepSize, wavelength, false, true)) {
				TVector3D.deleteInstance(inT);
				TVector3D.deleteInstance(v3duy);
				TVector3D.deleteInstance(v3dly);
				return false;
			}
			if (!searchLimitT(fInitD, fInitQ[w], inT, v3dly, fInitT[wavelength][w][1], wavelength,
					              fRaysForDisplay[wavelength][w][1], false, true)) {
				TVector3D.deleteInstance(inT);
				TVector3D.deleteInstance(v3duy);
				TVector3D.deleteInstance(v3dly);
				return false;
			}
			upperY = -1.0 * y1 - (x1 + fInitD) * Math.tan(fW[w]);
			v3duy.copy(0.0, upperY, 0.0);
			if (!searchLimitT(fInitD, fInitQ[w], fInitT[wavelength][w][1], v3duy, fInitT[wavelength][w][2],
					              wavelength, fRaysForDisplay[wavelength][w][2], false, true)) {
				TVector3D.deleteInstance(inT);
				TVector3D.deleteInstance(v3duy);
				TVector3D.deleteInstance(v3dly);
				return false;
			}
		}
		TVector3D.deleteInstance(inT);
		TVector3D.deleteInstance(v3duy);
		TVector3D.deleteInstance(v3dly);
		if (fInitT[TWavelength.REF_D][TRayConstant.W_MAX][2].getData(1)
			  - fInitT[TWavelength.REF_D][TRayConstant.W_MAX][1].getData(1) < fRadiusOfFNumberRay) {
			return false;
		}
		for (int w = 1; w < TRayConstant.NO_OF_WS; ++w) {
			searchMainT(fInitD, fInitQ[w], fInitT[wavelength][w][1], fInitT[wavelength][w][2],
					        fInitT[wavelength][w][0], wavelength);
			if (!fRayTracer.doIt(fInitD, fInitT[wavelength][w][0], fInitQ[w], fLens, wavelength,
					                 fRaysForDisplay[wavelength][w][0], false, true)) {
				return false;
			}
			/* 主光線の偏りをチェック */
			double l = fInitT[wavelength][w][2].getData(1) - fInitT[wavelength][w][0].getData(1);
			double u = fInitT[wavelength][w][0].getData(1) - fInitT[wavelength][w][1].getData(1);
			if (l / u < 0.0) {
				throw new RuntimeException("Error in TChromaticLensEvaluator::" + "calculateRaysForDisplay()");
			} else if (l / u < 0.5 || l / u > 2) {
				return false;
			}
		}
		return true;
	}

	/** 画面に表示しない光線の追跡を行う
	@param wavelength 波長番号     */
	private final boolean calculateOtherRays(int wavelength) {
		boolean checkLensHeight = false;
		boolean checkStopR = true;
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			fInitT[wavelength][w][3].copy(fInitT[wavelength][w][0]);
			fInitT[wavelength][w][3].add(fInitT[wavelength][w][1]);
			fInitT[wavelength][w][3].scalerQuotient(2.0);
			if (!fRayTracer.doIt(fInitD, fInitT[wavelength][w][3], fInitQ[w], fLens, wavelength,
					                 fRaysForDisplay[wavelength][w][3], false, true)) {
				return false;
			}
			if (w != 0) {
				fInitT[wavelength][w][4].copy(fInitT[wavelength][w][0]);
				fInitT[wavelength][w][4].add(fInitT[wavelength][w][2]);
				fInitT[wavelength][w][4].scalerQuotient(2.0);
				if (!fRayTracer.doIt(fInitD, fInitT[wavelength][w][4], fInitQ[w], fLens, wavelength,
						                 fRaysForDisplay[wavelength][w][4], false, true)) {
					return false;
				}
				TVector3D out5 = TVector3D.newInstance(0.0, 0.0, fRadiusOfFNumberRay);
				out5.add(fInitT[wavelength][w][0]);
				if (!searchLimitT(fInitD, fInitQ[w], fInitT[wavelength][w][0], out5, fInitT[wavelength][w][5],
						              wavelength, fRaysForDisplay[wavelength][w][5], checkLensHeight, checkStopR)) {
					TVector3D.deleteInstance(out5);
					return false;
				}
				fInitT[wavelength][w][6].copy(fInitT[wavelength][w][0]);
				fInitT[wavelength][w][6].add(fInitT[wavelength][w][5]);
				fInitT[wavelength][w][6].scalerQuotient(2.0);
				if (!fRayTracer.doIt(fInitD, fInitT[wavelength][w][6], fInitQ[w], fLens, wavelength,
						                 fRaysForDisplay[wavelength][w][6], false, true)) {
					TVector3D.deleteInstance(out5);
					return false;
				}
				double d1 = fInitT[wavelength][w][2].getData(1) - fInitT[wavelength][w][0].getData(1);
				TVector3D out7 = TVector3D.newInstance(0.0, d1, d1);
				out7.add(fInitT[wavelength][w][0]);
				if (!searchLimitT(fInitD, fInitQ[w], fInitT[wavelength][w][0], out7, fInitT[wavelength][w][7],
						              wavelength, fRaysForDisplay[wavelength][w][7], checkLensHeight, checkStopR)) {
					TVector3D.deleteInstance(out5);
					TVector3D.deleteInstance(out7);
					return false;
				}
				fInitT[wavelength][w][8].copy(fInitT[wavelength][w][0]);
				fInitT[wavelength][w][8].add(fInitT[wavelength][w][7]);
				fInitT[wavelength][w][8].scalerQuotient(2.0);
				if (!fRayTracer.doIt(fInitD, fInitT[wavelength][w][8], fInitQ[w], fLens, wavelength,
						                 fRaysForDisplay[wavelength][w][8], false, true)) {
					TVector3D.deleteInstance(out5);
					TVector3D.deleteInstance(out7);
					return false;
				}
				double d2 = fInitT[wavelength][w][0].getData(1) - fInitT[wavelength][w][1].getData(1);
				TVector3D out9 = TVector3D.newInstance(0.0, -d2, d2);
				out9.add(fInitT[wavelength][w][0]);
				if (!searchLimitT(fInitD, fInitQ[w], fInitT[wavelength][w][0], out9, fInitT[wavelength][w][9],
						              wavelength, fRaysForDisplay[wavelength][w][9], checkLensHeight, checkStopR)) {
					TVector3D.deleteInstance(out5);
					TVector3D.deleteInstance(out7);
					TVector3D.deleteInstance(out9);
					return false;
				}
				fInitT[wavelength][w][10].copy(fInitT[wavelength][w][0]);
				fInitT[wavelength][w][10].add(fInitT[wavelength][w][9]);
				fInitT[wavelength][w][10].scalerQuotient(2.0);
				if (!fRayTracer.doIt(fInitD, fInitT[wavelength][w][10], fInitQ[w], fLens,
						                 wavelength, fRaysForDisplay[wavelength][w][10], false, true)) {
					TVector3D.deleteInstance(out5);
					TVector3D.deleteInstance(out7);
					TVector3D.deleteInstance(out9);
					return false;
				}
				TVector3D.deleteInstance(out5);
				TVector3D.deleteInstance(out7);
				TVector3D.deleteInstance(out9);
			}
		}
		return true;
	}

	/**
	 * 絞りの位置を決める
	 */
	private final boolean determineStopPosition() {
		fInitT[TWavelength.REF_D][TRayConstant.W_0][0].copy(0.0, 0.0, 0.0);
		fInitT[TWavelength.REF_D][TRayConstant.W_0][1].copy(0.0, -fRadiusOfFNumberRay, 0.0);
		fInitT[TWavelength.REF_D][TRayConstant.W_0][2].copy(0.0, fRadiusOfFNumberRay, 0.0);
		if (!fRayTracer.doIt(fInitD, fInitT[TWavelength.REF_D][TRayConstant.W_0][1], fInitQ[TRayConstant.W_0],
				                 fLens, TWavelength.REF_D, fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_0][1],
				                 false, false)) {
			return false;
		}
		double x1 = fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_0][1].getVector3D(1).getData(0);
		int w = TRayConstant.W_MAX;
		double upperY = -fRadiusOfFNumberRay - (x1 + fInitD) * Math.tan(fW[w]);
		double lowerY = calcLowerY(w);
		//	assert( upperY >= lowerY );
		int div = (int) ((upperY - lowerY) / fRadiusOfFNumberRay) + 1;
		double stepSize = 1.0 / (double) div;
		TVector3D inT = TVector3D.newInstance();
		TVector3D v3duy = TVector3D.newInstance(0.0, upperY, 0.0);
		TVector3D v3dly = TVector3D.newInstance(0.0, lowerY, 0.0);
		if (!findFeasibleRay(fInitD, fInitQ[w], v3duy, v3dly, inT, stepSize, TWavelength.REF_D, false, false)) {
			TVector3D.deleteInstance(inT);
			TVector3D.deleteInstance(v3duy);
			TVector3D.deleteInstance(v3dly);
			return false;
		}
		if (!searchLimitT(fInitD, fInitQ[w], inT, v3dly, fInitT[TWavelength.REF_D][w][1], TWavelength.REF_D, fRaysForDisplay[TWavelength.REF_D][w][1], false, false)) {
			TVector3D.deleteInstance(inT);
			TVector3D.deleteInstance(v3duy);
			TVector3D.deleteInstance(v3dly);
			return false;
		}
		upperY = fRadiusOfFNumberRay - (x1 + fInitD) * Math.tan(fW[w]);
		v3duy.copy(0.0, upperY, 0.0);
		if (!searchLimitT(fInitD, fInitQ[w], fInitT[TWavelength.REF_D][w][1], v3duy, fInitT[TWavelength.REF_D][w][2], TWavelength.REF_D, fRaysForDisplay[TWavelength.REF_D][w][2], false, false)) {
			TVector3D.deleteInstance(inT);
			TVector3D.deleteInstance(v3duy);
			TVector3D.deleteInstance(v3dly);
			return false;
		}
		if (fInitT[TWavelength.REF_D][TRayConstant.W_MAX][2].getData(1) - fInitT[TWavelength.REF_D][TRayConstant.W_MAX][1].getData(1) < fRadiusOfFNumberRay) {
			TVector3D.deleteInstance(inT);
			TVector3D.deleteInstance(v3duy);
			TVector3D.deleteInstance(v3dly);
			return false;
		}
		if (!setStop()) {
			return false;
		}
		if (!doesMainRayExist()) {
			TVector3D.deleteInstance(inT);
			TVector3D.deleteInstance(v3duy);
			TVector3D.deleteInstance(v3dly);
			return false;
		}
		TVector3D.deleteInstance(inT);
		TVector3D.deleteInstance(v3duy);
		TVector3D.deleteInstance(v3dly);
		return true;
	}

	/** 像面のスポットを作成する
	@param wavelength 波長番号     */
	private final void makeSpots(int wavelength) {
		makeSpotAtW0(wavelength);
		TVector3D p = TVector3D.newInstance();
		TVector2D d = TVector2D.newInstance();
		for (int w = 1; w < TRayConstant.NO_OF_WS; ++w) {
			for (int i = 0; i < TRayConstant.NO_OF_RAYS; ++i) {
				p.copy(fRaysForDisplay[wavelength][w][i].getLastVertex());
				d.setData(0, p.getData(2));
				d.setData(1, p.getData(1));
				fSpots[wavelength][w].setVector2D(i, d);
			}
		}
		TVector3D.deleteInstance(p);
		TVector2D.deleteInstance(d);
	}

	/** 像面のスポットのうち角度0の光線のものを作成する
	@param wavelength 波長番号     */
	private final void makeSpotAtW0(int wavelength) {
		TVector2D bufv2d = TVector2D.newInstance();
		final double root2 = Math.sqrt(2.0);
		TVector3D p1 = TVector3D.newInstance();
		p1.copy(fRaysForDisplay[wavelength][TRayConstant.W_0][1].getLastVertex());
		TVector3D p3 = TVector3D.newInstance();
		p3.copy(fRaysForDisplay[wavelength][TRayConstant.W_0][3].getLastVertex());
		bufv2d.copy(0.0, 0.0);
		fSpots[wavelength][TRayConstant.W_0].setVector2D(0, bufv2d);
		bufv2d.copy(p1.getData(2), p1.getData(1));
		fSpots[wavelength][TRayConstant.W_0].setVector2D(1, bufv2d);
		bufv2d.copy(p1.getData(2), -p1.getData(1));
		fSpots[wavelength][TRayConstant.W_0].setVector2D(2, bufv2d);
		bufv2d.copy(p3.getData(2), p3.getData(1));
		fSpots[wavelength][TRayConstant.W_0].setVector2D(3, bufv2d);
		bufv2d.copy(p3.getData(2), -p3.getData(1));
		fSpots[wavelength][TRayConstant.W_0].setVector2D(4, bufv2d);
		bufv2d.copy(p1.getData(1), -p1.getData(2));
		fSpots[wavelength][TRayConstant.W_0].setVector2D(5, bufv2d);
		bufv2d.copy(-p3.getData(1), -p3.getData(2));
		fSpots[wavelength][TRayConstant.W_0].setVector2D(6, bufv2d);
		bufv2d.copy(p1.getData(1) / root2, p1.getData(1) / root2);
		fSpots[wavelength][TRayConstant.W_0].setVector2D(7, bufv2d);
		bufv2d.copy(-p3.getData(1) / root2, p3.getData(1) / root2);
		fSpots[wavelength][TRayConstant.W_0].setVector2D(8, bufv2d);
		bufv2d.copy(p1.getData(1) / root2, -p1.getData(1) / root2);
		fSpots[wavelength][TRayConstant.W_0].setVector2D(9, bufv2d);
		bufv2d.copy(-p3.getData(1) / root2, -p3.getData(1) / root2);
		fSpots[wavelength][TRayConstant.W_0].setVector2D(10, bufv2d);
		TVector2D.deleteInstance(bufv2d);
		TVector3D.deleteInstance(p1);
		TVector3D.deleteInstance(p3);
	}

	/**
	 * 絞りの位置を探す
	 * @param lowerRay 探索範囲の下端
	 * @param upperRay 探索範囲の上端
	 * @return 絞りの位置
	 */
	private final double searchStopPosition(TRay lowerRay, TRay upperRay) {
		/* (ズームレンズ系の進化的設計[谷] p22,p23) */
		double curPos = 0.0;
		double curErr = 0.0;
		TVector3D[] p = new TVector3D[1];
		p[0] = TVector3D.newInstance();
		int n;
		double left = fLens.surface(1).getPosition();
		double right = fLens.surface(fLens.getNoOfSurfaces() - 1).getPosition();
		n = lowerRay.searchPointsWithY(0.0, p, 1);
		if (n > 0 && p[0].getData(0) < right) {
				right = p[0].getData(0);
		}
		curPos = (left + right) / 2.0;
		for (int i = 0; i < 15; ++i) {
			TVector3D p1 = TVector3D.newInstance();
			upperRay.searchPointWithX(curPos, p1);
			TVector3D p2 = TVector3D.newInstance();
			lowerRay.searchPointWithX(curPos, p2);
			curErr = p1.getData(1) + p2.getData(1);
			TVector3D.deleteInstance(p1);
			TVector3D.deleteInstance(p2);
			if (curErr < 0.0) {
					left = curPos;
			} else if (curErr > 0.0) {
					right = curPos;
			} else {
					break;
			}
			curPos = (left + right) / 2.0;
		}
		TVector3D.deleteInstance(p[0]);
		return curPos;
	}

	/**
	 * 光線追跡可能な, 開始位置が外側の光線の進入位置Tを求める
	 *
	 * @param initD 間隔 d の初期値
	 * @param initQ 位置ベクトル T の初期値
	 * @param inT 探索開始位置 内側
	 * @param outT 探索開始位置 外側
	 * @param resultT 結果の位置ベクトル T これに格納
	 * @param wavelength 波長番号
	 * @param ray, 追跡光線. これに登録
	 * @param checkLensHeight 高さのチェックするかどうか
	 * @param checkStopR 絞りに光線が当たらないかチェックするかどうか
	 *
	 */
	private final boolean searchLimitT(double initD, final TVector3D initQ, final TVector3D inT, final TVector3D outT,
		                                 TVector3D resultT, int wavelength, TRay ray, boolean checkLensHeight, boolean checkStopR) {
		if (!fRayTracer.doIt(initD, inT, initQ, fLens, wavelength, fRay, checkLensHeight, checkStopR)) { //注意 true しか返さない
			//throw new RuntimeException("Error in TChromaticLensEvaluator::SearchLimitT");
			return false;
		}
		ray.copy(fRay);
		TVector3D iT = TVector3D.newInstance(inT);
		TVector3D oT = TVector3D.newInstance(outT);
		TVector3D curT = TVector3D.newInstance(iT);
		curT.add(oT);
		curT.scalerQuotient(2.0);
		for (int i = 0; i < 15; ++i) { //二分探索
			if (fRayTracer.doIt(initD, curT, initQ, fLens, wavelength, fRay, checkLensHeight, checkStopR)) {
				ray.copy(fRay);
				iT.copy(curT);
			} else {
				oT.copy(curT);
			}
			curT.putAdd(iT, oT);
			curT.scalerQuotient(2.0);
		}
		resultT.copy(iT);
		TVector3D.deleteInstance(iT);
		TVector3D.deleteInstance(oT);
		TVector3D.deleteInstance(curT);
		return true;
	}

	/**
	 *  レンズの端を調整する(表示用)
	 */
	private final void adjustLensEdges() {
		for (int i = 0; i < fLens.getNoOfSurfaces(); ++i) {
			fLens.surface(i).setHeight(0.0);
		}
		int rayNo[] = { 1, 2, 5, 7, 9 };
		for (int wl = 0; wl < TWavelength.NO_OF_WAVELENGTHS; ++wl) {
			setLensHeight(fRaysForDisplay[wl][TRayConstant.W_0][1]);
			for (int w = 1; w < TRayConstant.NO_OF_WS; ++w) {
				for (int i = 0; i < 5; ++i) {
					setLensHeight(fRaysForDisplay[wl][w][rayNo[i]]);
				}
			}
		}
	}

	/**
	 * 主光線のあるべき位置を探す
	 * @param initD 間隔 d の初期値
	 * @param initQ 位置ベクトル T の初期値
	 * @param lowerT 探索の下端
	 * @param upperT 探索の上端
	 * @param mainT  結果の主光線の初期位置
	 * @param wavelength 波長番号
	 */
	private final boolean searchMainT(double initD, final TVector3D initQ, final TVector3D lowerT, final TVector3D upperT,
			                              TVector3D mainT, int wavelength) {
		TVector3D uT = TVector3D.newInstance(upperT);
		TVector3D lT = TVector3D.newInstance(lowerT);
		TVector3D curT = TVector3D.newInstance(lT);
		curT.add(uT);
		curT.scalerQuotient(2.0);
		TMyPtrDouble y = TMyPtrDouble.newInstance();
		double stopPos = fLens.getStopPosition();
		for (int i = 0; i < 15; ++i) {
			fRay.clear();
			if (!fRayTracer.searchPointWithX(initD, curT, initQ, fLens, wavelength, stopPos, y)) {
				TVector3D.deleteInstance(uT);
				TVector3D.deleteInstance(lT);
				TVector3D.deleteInstance(curT);
				return false;
			}
			if (y.getValue() > 0.0) {
				uT.copy(curT);
				curT.putAdd(lT, uT);
				curT.scalerQuotient(2.0);
			} else if (y.getValue() < 0.0) {
				lT.copy(curT);
				curT.putAdd(lT, uT);
				curT.scalerQuotient(2.0);
			} else {
				mainT.copy(curT);
				break;
			}
		}
		mainT.copy(curT);
		TVector3D.deleteInstance(uT);
		TVector3D.deleteInstance(lT);
		TVector3D.deleteInstance(curT);
		return true;
	}

	/**
	 * 絞りの位置と半径を設定する.
	 *
	 */
	private final boolean setStop() {
		double filmPos = 0.0;
		for (int i = 0; i < fLens.getNoOfSurfaces(); ++i) {
			filmPos += fLens.getD(i);
		}
		double stopPos = searchStopPosition(fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_MAX][1],
				                                fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_MAX][2]);
		fLens.setStopPosition(stopPos);
		if (fEnforceStopPosition) {
			enforceStopPosition();
		}
		stopPos = fLens.getStopPosition();
		if (fUseOriginalStopPosition) {
			stopPos = fOriginalStopPosition;
			fLens.setStopPosition(stopPos);
		}
		if (stopPos > filmPos) { // 絞りの位置が像面を超えた場合は実行不可能にする． by isao
			return false;
		}
		TVector3D stopP = TVector3D.newInstance();
		fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_0][1].searchPointWithX(stopPos, stopP);
		fLens.setStopR(Math.abs(stopP.getData(1)));
		TVector3D.deleteInstance(stopP);
		return true;
	}

	/**
	 * 主光線が絞りの位置で正しい位置を通っているか
	 */
	private final boolean doesMainRayExist() {
		TVector3D tmpP = TVector3D.newInstance();
		final TRay r1 = fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_MAX][1];
		r1.searchPointWithX(fLens.getStopPosition(), tmpP);
		if (tmpP.getData(1) >= 0.0) {
			TVector3D.deleteInstance(tmpP);
			return false;
		}
		final TRay r2 = fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_MAX][2];
		r2.searchPointWithX(fLens.getStopPosition(), tmpP);
		if (tmpP.getData(1) <= 0.0) {
			TVector3D.deleteInstance(tmpP);
			return false;
		}
		TVector3D.deleteInstance(tmpP);
		return true;
	}

	/**
	 * 像面が最後の面より後ろであることを確認
	 */
	private final boolean checkBackFocus(int wavelength) {
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			for (int i = 0; i < TRayConstant.NO_OF_RAYS_FOR_DISPLAY; ++i) {
				if (w == 0 && (i == 0 || i == 2)) {
					continue;
				}
				int n = fRaysForDisplay[wavelength][w][i].getNoOfVertexes();
				double lastSurface = fRaysForDisplay[wavelength][w][i].getVector3D(n - 2).getData(0);
				double imageSurface = fRaysForDisplay[wavelength][w][i].getVector3D(n - 1).getData(0);
				if (imageSurface - lastSurface <= 0.0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * レンズの高さの設定(低いものから高いものに更新)
	 */
	private final void setLensHeight(TRay ray) {
		for (int i = 0; i < fLens.getNoOfSurfaces(); ++i) {
			double h1 = Math.sqrt(ray.getVector3D(i + 1).getData(1) * ray.getVector3D(i + 1).getData(1)
						                + ray.getVector3D(i + 1).getData(2) * ray.getVector3D(i + 1).getData(2));
			double h2 = fLens.surface(i).getHeight();
			double h = h1 > h2 ? h1 : h2;
			fLens.surface(i).setHeight(h);
		}
	}

	/**
	 * レンズの高さを初期化する
	 */
	private final void clearLensHeight() {
		for (int i = 0; i < fLens.getNoOfSurfaces(); ++i)
			fLens.surface(i).setHeight(0.0);
	}

	/**
	 * 実行可能な光線の追跡開始位置を探す
	 *
	 * @param fInitD 間隔 d の初期値
	 * @param initQ 位置ベクトル T の初期値
	 * @param start 探索開始位置
	 * @param end 探索終了位置
	 * @param result 結果の光線追跡開始位置
	 * @param stepSize 探索幅
	 * @param wavelength 波長番号
	 * @param checkHeight 高さのチェックするかどうか
	 * @param checkStopR 絞りに光線が当たらないかチェックするかどうか
	 */
	private final boolean findFeasibleRay(double fInitD, final TVector3D initQ, final TVector3D start, final TVector3D end,
		                                    TVector3D result, double stepSize, int wavelength, boolean checkHeight, boolean checkStopR) {
		TVector3D d = TVector3D.newInstance(end);
		d.subtract(start);
		TVector3D td = TVector3D.newInstance();
		for (double t = 0.0; t < 1.0; t += stepSize) {
			td.putScalerProduct(t, d);
			result.putAdd(start, td);
			if (fRayTracer.doIt(fInitD, result, initQ, fLens, wavelength, fRay, checkHeight, checkStopR)) {
				TVector3D.deleteInstance(d);
				TVector3D.deleteInstance(td);
				return true;
			}
		}
		result = end;
		if (fRayTracer.doIt(fInitD, result, initQ, fLens, wavelength, fRay, checkHeight,checkStopR)) {
			TVector3D.deleteInstance(d);
			TVector3D.deleteInstance(td);
			return true;
		}
		TVector3D.deleteInstance(d);
		TVector3D.deleteInstance(td);
		return false;
	}

	/**
	 * レンズの下端へ向かう位置ベクトルTy座標を返す
	 *
	 * @param w 角度番号
	 */
	private final double calcLowerY(int w) {
		double r = fLens.surface(0).getR();
		if (r > 0.0) {
			return -Math.abs(r) - (r + fInitD) * Math.tan(fW[w]);
		} else {
			double x2 = Math.abs(r) * (Math.sin(fW[w]) - 1.0);
			double y2 = -Math.abs(r) * Math.cos(fW[w]);
			return y2 - (x2 + fInitD) * Math.tan(fW[w]);
		}
	}

	/**
	 * レンズ系の形態によって絞りの位置を強制する
	 */
	private final void enforceStopPosition() {
		clearLensHeight();
		setLensHeight(fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_0][1]);
		setLensHeight(fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_MAX][1]);
		setLensHeight(fRaysForDisplay[TWavelength.REF_D][TRayConstant.W_MAX][2]);
		double curStopPos = fLens.getStopPosition();
		TMyPtrDouble leftPos = TMyPtrDouble.newInstance();
		TMyPtrDouble rightPos = TMyPtrDouble.newInstance();
		TMyPtrInt leftIndex = TMyPtrInt.newInstance(-1);
		TMyPtrInt rightIndex = TMyPtrInt.newInstance(-1);
		searchLeftStopPosition(leftPos, leftIndex);
		searchRightStopPosition(rightPos, rightIndex);
		/* レンズの幅に入らないとき そのまま*/
		if (leftIndex.getValue() == -1 && rightIndex.getValue() == -1) {
			return;
		} else if (leftIndex.getValue() == -1 || rightIndex.getValue() == -1) {
			throw new RuntimeException("Error in TChromaticLensEvaluator::EnforceStopPosition.\nleftIndex:" + leftIndex + ", rightIndex:" + rightIndex);
		}
		/* レンズの幅に入るとき */
		if (leftIndex.getValue() == 0) {
			fLens.setStopPosition(rightPos.getValue());
		} else { /* もとの位置に近いほうにする. */
			double result = curStopPos - leftPos.getValue() < rightPos.getValue() - curStopPos ? leftPos.getValue() : rightPos.getValue();
			fLens.setStopPosition(result);
		}
		TMyPtrDouble.deleteInstance(leftPos);
		TMyPtrDouble.deleteInstance(rightPos);
		TMyPtrInt.deleteInstance(leftIndex);
		TMyPtrInt.deleteInstance(rightIndex);
	}

//	/**
//	 * ガラスの幅に絞りが入る箇所を探し, あれば, 右端代入しをその空間番号に変更
//	 * @param stopPos 探索結果
//	 * @param mIndex 空間の番号
//	 */
//	private final void searchLeftStopPosition(double[] stopPos, int[] mIndex) {
//		stopPos[0] = fLens.getStopPosition();
//		for (int i = fLens.getNoOfSurfaces() - 1; i > 0; --i) {
//			if (fLens.getConfig().isGlass(i)) {
//				TSurface s1 = fLens.surface(i - 1);
//				TSurface s2 = fLens.surface(i);
//				double x1 = s1.getPosition() < s1.getEdgePosition() ? s1.getPosition() : s1.getEdgePosition();
//				double x2 = s2.getPosition() > s2.getEdgePosition() ? s2.getPosition() : s2.getEdgePosition();
//				/* レンズの幅に入るとき */
//				if (x1 <= stopPos[0] && stopPos[0] <= x2) {
//					stopPos[0] = x1;
//					mIndex[0] = i - 1;
//				}
//			}
//		}
//	}

	/**
	 * ガラスの幅に絞りが入る箇所を探し, あれば, 右端代入しをその空間番号に変更
	 * @param stopPos 探索結果
	 * @param mIndex 空間の番号
	 */
	private final void searchLeftStopPosition(TMyPtrDouble stopPos, TMyPtrInt mIndex) {
		stopPos.setValue(fLens.getStopPosition());
		for (int i = fLens.getNoOfSurfaces() - 1; i > 0; --i) {
			if (fLens.getConfig().isGlass(i)) {
				TSurface s1 = fLens.surface(i - 1);
				TSurface s2 = fLens.surface(i);
				double x1 = s1.getPosition() < s1.getEdgePosition() ? s1.getPosition() : s1.getEdgePosition();
				double x2 = s2.getPosition() > s2.getEdgePosition() ? s2.getPosition() : s2.getEdgePosition();
				/* レンズの幅に入るとき */
				if (x1 <= stopPos.getValue() && stopPos.getValue() <= x2) {
					stopPos.setValue(x1);
					mIndex.setValue(i - 1);
				}
			}
		}
	}

//	/**
//	 * ガラスの幅に絞りが入る箇所を探し，あれば, 左端代入しをその空間番号に変更
//	 * @param stopPos 探索結果
//	 * @param mIndex 空間の番号
//	 */
//	private final void searchRightStopPosition(double[] stopPos, int[] mIndex) {
//		stopPos[0] = fLens.getStopPosition();
//		for (int i = 1; i < fLens.getNoOfSurfaces(); ++i) {
//			if (fLens.getConfig().isGlass(i)) {
//				TSurface s1 = fLens.surface(i - 1);
//				TSurface s2 = fLens.surface(i);
//				double x1 = s1.getPosition() < s1.getEdgePosition() ? s1.getPosition() : s1.getEdgePosition();
//				double x2 = s2.getPosition() > s2.getEdgePosition() ? s2.getPosition() : s2.getEdgePosition();
//				/* レンズの幅に入るとき */
//				if (x1 <= stopPos[0] && stopPos[0] <= x2) {
//					stopPos[0] = x2;
//					mIndex[0] = i + 1;
//				}
//			}
//		}
//	}

	/**
	 * ガラスの幅に絞りが入る箇所を探し, あれば, 左端代入しをその空間番号に変更
	 * @param stopPos 探索結果
	 * @param mIndex 空間の番号
	 */
	private final void searchRightStopPosition(TMyPtrDouble stopPos, TMyPtrInt mIndex) {
		stopPos.setValue(fLens.getStopPosition());
		for (int i = 1; i < fLens.getNoOfSurfaces(); ++i) {
			if (fLens.getConfig().isGlass(i)) {
				TSurface s1 = fLens.surface(i - 1);
				TSurface s2 = fLens.surface(i);
				double x1 = s1.getPosition() < s1.getEdgePosition() ? s1.getPosition() : s1.getEdgePosition();
				double x2 = s2.getPosition() > s2.getEdgePosition() ? s2.getPosition() : s2.getEdgePosition();
				/* レンズの幅に入るとき */
				if (x1 <= stopPos.getValue() && stopPos.getValue() <= x2) {
					stopPos.setValue(x2);
					mIndex.setValue(i + 1);
				}
			}
		}
	}

	/**
	 * 評価値の歪曲および解像度の部分を計算
	 */
	private final void calculateDistortionAndResolution() {
		TVector2D d = TVector2D.newInstance();
		TVector2D sub = TVector2D.newInstance(0.0, 0.0);
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			double y = fLens.getFocusLength() * Math.tan(fW[w]);
			d.copy(fSpots[TWavelength.REF_D][w].getVector2D(0));
			sub.setData(1, y);
			d.subtract(sub);
			double distortion = d.getData(0) * d.getData(0) + d.getData(1) * d.getData(1);
			fLens.setDistortion(w, distortion);
			double resolution = 0.0;
			for (int i = 1; i < TRayConstant.NO_OF_RAYS; ++i) {
				d.putSubtract(fSpots[TWavelength.REF_D][w].getVector2D(i),
					            fSpots[TWavelength.REF_D][w].getVector2D(0));
				resolution += d.getData(0) * d.getData(0) + d.getData(1) * d.getData(1);
			}
			fLens.setResolution(w, resolution);
		}
		TVector2D.deleteInstance(d);
		TVector2D.deleteInstance(sub);
	}

	/**
	 * 各波長，各画角の色収差の計算を行う．
	 */
	private final void calculateChromaticAbberations() {
		TVector2D d = TVector2D.newInstance();
		for (int wavelength = 0; wavelength < TWavelength.NO_OF_WAVELENGTHS; ++wavelength) {
			for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
				double chromaticAbberation = 0.0;
				for (int i = 0; i < TRayConstant.NO_OF_RAYS; ++i) {
					d.putSubtract(fSpots[TWavelength.REF_D][w].getVector2D(i),
							          fSpots[wavelength][w].getVector2D(i));
					chromaticAbberation += (d.getData(0) * d.getData(0) + d.getData(1) * d.getData(1));
				}
				fLens.setChromaticAbberation(w, wavelength, chromaticAbberation);
			}
		}
		TVector2D.deleteInstance(d);
	}

	/**
	 * 色収差を考慮するか？
	 * @return 考慮する：true, 考慮しない：false
	 */
	public boolean isChromatic() {
		return fChromatic;
	}

	/**
	 * 絞りの位置の強制(ガラス内にあるときの移動)を行うか？
	 * @return 行う：true, 行わない：false
	 */
	public boolean isEnforceStopPosition() {
		return fEnforceStopPosition;
	}

	/**
	 * オリジナルの絞りの位置を使うか？
	 * @return 使う：true, 使わない：false
	 */
	public boolean isUseOriginalStopPosition() {
		return fUseOriginalStopPosition;
	}

	/**
	 * 色収差を考慮するか？ を設定する．
	 * @param b 考慮する：true, 考慮しない：false
	 */
	public void setChromatic(boolean b) {
		fChromatic = b;
	}

	/**
	 * 絞りの位置の強制(ガラス内にあるときの移動)を行うか？ を設定する．
	 * @param b 行う：true, 行わない：false
	 */
	public void setEnforceStopPosition(boolean b) {
		fEnforceStopPosition = b;
	}

	/**
	 * オリジナルの絞りの位置を使うか？ を設定する．
	 * @param b 使う：true, 使わない：false
	 */
	public void setUseOriginalStopPosition(boolean b) {
		fUseOriginalStopPosition = b;
	}

	/**
	 * 光線を表示に使うかどうか？
	 * @return 使う：true, 使わない：false
	 */
	public boolean isUseRaysForDisplay() {
		return fUseRaysForDisplay;
	}

	/**
	 * 光線を表示に使うかどうか？ を設定する．
	 * @param b 使う：true, 使わない：false
	 */
	public void setUseRaysForDisplay(boolean b) {
		fUseRaysForDisplay = b;
	}

}
