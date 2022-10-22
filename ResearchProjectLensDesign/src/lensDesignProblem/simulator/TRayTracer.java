package lensDesignProblem.simulator;

/**
 * 光線追跡を行うクラス<BR>
 * 
 * @author Kenta Hirano, isao
 */
public class TRayTracer {

	/** 光線追跡オペレータ */
	private TRayTraceOperator fOp;

	private TVector3D fNextT;

	private TVector3D fNextQ;

	private TVector3D fT;

	private TVector3D fQ;

	private TVector3D fP1;

	private TVector3D fP2;

	private TVector3D fV;

	/**
	 * コンストラクタ
	 */
	public TRayTracer() {
		fOp = new TRayTraceOperator();

		fNextT = TVector3D.newInstance();
		fNextQ = TVector3D.newInstance();
		fT = TVector3D.newInstance();
		fQ = TVector3D.newInstance();
		fP1 = TVector3D.newInstance();
		fP2 = TVector3D.newInstance();
		fV = TVector3D.newInstance();
	}

	/**
	 * 光線追跡を行う.
	 * 
	 * @param initD           間隔 d の初期値
	 * @param initT           位置ベクトル T の初期値
	 * @param initQ           光線の方向ベクトル T の初期値
	 * @param lens            追跡を行うレンズ系
	 * @param wavelength      波長番号
	 * @param ray             追跡光線. これに登録する.
	 * @param checkLensHeight 高さのチェックするかどうか
	 * @param checkStopR      絞りに光線が当たらないかチェックするかどうか
	 */
	public final boolean doIt(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength,
			TRay ray, boolean checkLensHeight, boolean checkStopR) {
		ray.reset();
		addToRay(initT, -initD, ray);
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			double r = lens.surface(i).getR();
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			fT.copy(fNextT);
			if (checkLensHeight) {
				if (Math.sqrt(fT.getData(1) * fT.getData(1) + fT.getData(2) * fT.getData(2)) > lens.surface(i).getHeight()) {
					return false;
				}
			}
			addToRay(fT, lens.surface(i).getPosition(), ray);
			if (checkStopR) {
				if (!this.checkStopR(lens, ray, i)) {
					return false;
				}
			}
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		calculateLastVertex(fQ, lens.getFilmPosition(), ray);
		if (checkStopR) {
			if (!checkStopR(lens, ray, ray.getCurrentSize() - 2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 光線追跡を行う.
	 * 
	 * @param initD           間隔 d の初期値
	 * @param initT           位置ベクトル T の初期値
	 * @param initQ           光線の方向ベクトル T の初期値
	 * @param lens            追跡を行うレンズ系
	 * @param wavelength      波長番号
	 * @param ray             追跡光線. これに登録する.
	 * @param checkLensHeight 高さのチェックするかどうか
	 */
	public final boolean doIt(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, TRay ray, boolean checkLensHeight) {
		ray.reset();
		addToRay(initT, -initD, ray);
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			double r = lens.surface(i).getR();
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			fT.copy(fNextT);
			if (checkLensHeight) {
				if (Math.sqrt(fT.getData(1) * fT.getData(1) + fT.getData(2) * fT.getData(2)) > lens.surface(i).getHeight()) {
					return false;
				}
			}
			addToRay(fT, lens.surface(i).getPosition(), ray);
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		calculateLastVertex(fQ, lens.getFilmPosition(), ray);
		return true;
	}

	/**
	 * 光線追跡を行う.
	 * 
	 * @param initD      間隔 d の初期値
	 * @param initT      位置ベクトル T の初期値
	 * @param initQ      光線の方向ベクトル T の初期値
	 * @param lens       追跡を行うレンズ系
	 * @param wavelength 波長番号
	 * @param ray        追跡光線. これに登録する.
	 */
	public final boolean doIt(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, TRay ray) {
		ray.reset();
		addToRay(initT, -initD, ray);
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			double r = lens.surface(i).getR();
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			fT.copy(fNextT);
			addToRay(fT, lens.surface(i).getPosition(), ray);
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		calculateLastVertex(fQ, lens.getFilmPosition(), ray);
		return true;
	}

	public final int doOneStep(int index, final TVector3D curT, final TVector3D curQ,
			TVector3D nextT, TVector3D nextQ, final TLens lens, int wavelength, TRay ray) {
		int err = TRayError.NO_ERROR;
		double d;
		double r = lens.surface(index).getR();
		if (index == 0) {
			ray.clear();
			d = r < -10.0 ? Math.abs(r - 10.0) : 10.0;
			addToRay(curT, -d, ray);
		} else {
			d = lens.getD(index - 1);
		}
		err = fOp.getNextT(curT, curQ, d, r, nextT);
		if (err != TRayError.NO_ERROR) {
			return err;
		}
		addToRay(nextT, lens.surface(index).getPosition(), ray);
		double n1 = lens.getGlass(index).getN(wavelength);
		double n2 = lens.getGlass(index + 1).getN(wavelength);
		err = fOp.getNextQ(nextT, curQ, r, n1, n2, nextQ);
		if (err != TRayError.NO_ERROR) {
			return err;
		}
		if (index == lens.getNoOfSurfaces() - 1) {
			this.calculateLastVertex(nextQ, lens.getFilmPosition(), ray);
		}
		return TRayError.NO_ERROR;
	}

	public final int doOneStep(int index, final TVector3D curT, final TVector3D curQ,
			TVector3D nextT, TVector3D nextQ, final TLens lens, int wavelength) {
		int err = TRayError.NO_ERROR;
		double d;
		double r = lens.surface(index).getR();
		if (index == 0) {
			d = r < -10.0 ? Math.abs(r - 10.0) : 10.0;
		} else {
			d = lens.getD(index - 1);
		}
		err = fOp.getNextT(curT, curQ, d, r, nextT);
		if (err != TRayError.NO_ERROR) {
			return err;
		}
		double n1 = lens.getGlass(index).getN(wavelength);
		double n2 = lens.getGlass(index + 1).getN(wavelength);
		err = fOp.getNextQ(nextT, curQ, r, n1, n2, nextQ);
		/* 無駄っぽい 下 */
		if (err != TRayError.NO_ERROR) {
			return err;
		}
		return TRayError.NO_ERROR;
	}

	/**
	 * 光線の高さがyになっているポジションを探す
	 * 
	 * @param initD           間隔 d の初期値
	 * @param initT           位置ベクトル T の初期値
	 * @param initQ           光線の方向ベクトル T の初期値
	 * @param lens            追跡を行うレンズ系
	 * @param wavelength      波長番号
	 * @param y               探す高さy
	 * @param pos             見つかったポジションを代入する
	 *                        ポインタが使えないため大きさ1の配列
	 * @param reachFilm       最後まで追跡できたが見つからなかったとき, trueを代入
	 *                        ポインタが使えないため大きさ1の配列
	 * @param checkLensHeight 高さのチェックするかどうか
	 * @return 見つかったとき, true
	 */
	public final boolean searchPointWithY(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, double y, double[] pos, boolean[] reachFilm, boolean checkLensHeight) {
		reachFilm[0] = false;
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			double r = lens.surface(i).getR();
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			if ((fNextT.getData(1) - y) * (fT.getData(1) - y) <= 0.0) {
				double x = lens.surface(i).getPosition();
				fP1.copy(x - d, 0.0, 0.0);
				fP1.add(fT);
				fP2.copy(x, 0.0, 0.0);
				fP2.add(fNextT);
				double a = (y - fP1.getData(1)) / (fP2.getData(1) - fP1.getData(1));
				pos[0] = fP1.getData(0) + a * (fP2.getData(0) - fP1.getData(0));
				return true;
			}
			fT = fNextT;
			if (checkLensHeight) {
				if (Math.sqrt(fT.getData(1) * fT.getData(1) + fT.getData(2) * fT.getData(2)) > lens.surface(i).getHeight()) {
					return false;
				}
			}
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		double x1 = lens.surface(lens.getNoOfSurfaces() - 1).getPosition();
		fP1.copy(x1, 0.0, 0.0);
		fP1.add(fT);
		double x2 = lens.getFilmPosition();
		fP2.copy(x2, fQ.getData(1) * (x2 - fP1.getData(0)) / fQ.getData(0), 0.0);
		if ((fP1.getData(1) - y) * (fP2.getData(1) - y) <= 0.0) {
			double a = (y - fP1.getData(1)) / (fP2.getData(1) - fP1.getData(1));
			pos[0] = fP1.getData(0) + a * (fP2.getData(0) - fP1.getData(0));
			return true;
		}
		reachFilm[0] = true;
		return false;
	}

	/**
	 * 光線の高さがyになっているポジションを探す
	 * 
	 * @param initD      間隔 d の初期値
	 * @param initT      位置ベクトル T の初期値
	 * @param initQ      光線の方向ベクトル T の初期値
	 * @param lens       追跡を行うレンズ系
	 * @param wavelength 波長番号
	 * @param y          探す高さy
	 * @param pos        見つかったポジションを代入する
	 *                   ポインタが使えないため大きさ1の配列
	 * @param reachFilm  最後まで追跡できたが見つからなかったとき, trueを代入
	 *                   ポインタが使えないため大きさ1の配列
	 * @return 見つかったとき, true
	 */
	public final boolean searchPointWithY(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, double y, double[] pos, boolean[] reachFilm) {
		reachFilm[0] = false;
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			double r = lens.surface(i).getR();
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			if ((fNextT.getData(1) - y) * (fT.getData(1) - y) <= 0.0) {
				double x = lens.surface(i).getPosition();
				fP1.copy(x - d, 0.0, 0.0);
				fP1.add(fT);
				fP2.copy(x, 0.0, 0.0);
				fP2.add(fNextT);
				double a = (y - fP1.getData(1)) / (fP2.getData(1) - fP1.getData(1));
				pos[0] = fP1.getData(0) + a * (fP2.getData(0) - fP1.getData(0));
				return true;
			}
			fT.copy(fNextT);
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		double x1 = lens.surface(lens.getNoOfSurfaces() - 1).getPosition();
		fP1.copy(x1, 0.0, 0.0);
		fP1.add(fT);
		double x2 = lens.getFilmPosition();
		fP2.copy(x2, fQ.getData(1) * (x2 - fP1.getData(0)) / fQ.getData(0), 0.0);
		if ((fP1.getData(1) - y) * (fP2.getData(1) - y) <= 0.0) {
			double a = (y - fP1.getData(1)) / (fP2.getData(1) - fP1.getData(1));
			pos[0] = fP1.getData(0) + a * (fP2.getData(0) - fP1.getData(0));
			return true;
		}
		reachFilm[0] = true;
		return false;
	}

	/**
	 * 光線のポジションがxになっている高さyを探す
	 * 
	 * @param initD           間隔 d の初期値
	 * @param initT           位置ベクトル T の初期値
	 * @param initQ           光線の方向ベクトル T の初期値
	 * @param lens            追跡を行うレンズ系
	 * @param wavelength      波長番号
	 * @param 探すポジションx
	 * @param y               見つかった高さyを代入する
	 *                        ポインタが使えないため大きさ1の配列
	 * @param checkLensHeight 高さのチェックするかどうか
	 * @return 見つかったとき, true
	 */
	public final boolean searchPointWithX(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, double x, double[] y, boolean checkLensHeight) {
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			double r = lens.surface(i).getR();
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			{
				double pos = lens.surface(i).getPosition();
				fP1.copy(pos - d, 0.0, 0.0);
				fP1.add(fT);
				fP2.copy(pos, 0.0, 0.0);
				fP2.add(fNextT);
				if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
					double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
					y[0] = fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1));
					return true;
				}
			}
			fT.copy(fNextT);
			if (checkLensHeight) {
				if (Math.sqrt(fT.getData(1) * fT.getData(1) + fT.getData(2) * fT.getData(2)) > lens.surface(i).getHeight()) {
					return false;
				}
			}
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		{
			double pos1 = lens.surface(lens.getNoOfSurfaces() - 1).getPosition();
			fP1.copy(pos1, 0.0, 0.0);
			fP1.add(fT);
			double pos2 = lens.getFilmPosition();
			fP2.copy(pos2, fQ.getData(1) * (pos2 - fP1.getData(0)) / fQ.getData(0), 0.0);
			if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
				double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
				y[0] = fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1));
				return true;
			}
		}
		return false;
	}

	/**
	 * 光線のポジションがxになっている高さyを探す
	 * 
	 * @param initD      間隔 d の初期値
	 * @param initT      位置ベクトル T の初期値
	 * @param initQ      光線の方向ベクトル T の初期値
	 * @param lens       追跡を行うレンズ系
	 * @param wavelength 波長番号
	 * @param 探すポジションx
	 * @param y          見つかった高さyを代入する
	 *                   ポインタが使えないため大きさ1の配列
	 * @return 見つかったとき, true
	 */
	public final boolean searchPointWithX(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, double x, double[] y) {
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			double r = lens.surface(i).getR();
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			{
				double pos = lens.surface(i).getPosition();
				fP1.copy(pos - d, 0.0, 0.0);
				fP1.add(fT);
				fP2.copy(pos, 0.0, 0.0);
				fP2.add(fNextT);
				if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
					double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
					y[0] = fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1));
					return true;
				}
			}
			fT.copy(fNextT);
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		{
			double pos1 = lens.surface(lens.getNoOfSurfaces() - 1).getPosition();
			fP1.copy(pos1, 0.0, 0.0);
			fP1.add(fT);
			double pos2 = lens.getFilmPosition();
			fP2.copy(pos2, fQ.getData(1) * (pos2 - fP1.getData(0)) / fQ.getData(0), 0.0);
			if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
				double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
				y[0] = fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1));
				return true;
			}
		}
		return false;
	}

	/**
	 * 光線のポジションがxになっている高さyを探す
	 * 
	 * @param initD           間隔 d の初期値
	 * @param initT           位置ベクトル T の初期値
	 * @param initQ           光線の方向ベクトル T の初期値
	 * @param lens            追跡を行うレンズ系
	 * @param wavelength      波長番号
	 * @param 探すポジションx
	 * @param y               見つかった高さyを代入する
	 *                        ポインタが使えないため大きさ1の配列
	 * @param checkLensHeight 高さのチェックするかどうか
	 * @return 見つかったとき, true
	 */
	public final boolean searchPointWithX(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, double x, TMyPtrDouble y, boolean checkLensHeight) {
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			double r = lens.surface(i).getR();
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			{
				double pos = lens.surface(i).getPosition();
				fP1.copy(pos - d, 0.0, 0.0);
				fP1.add(fT);
				fP2.copy(pos, 0.0, 0.0);
				fP2.add(fNextT);
				if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
					double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
					y.setValue(fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1)));
					return true;
				}
			}
			fT.copy(fNextT);
			if (checkLensHeight) {
				if (Math.sqrt(fT.getData(1) * fT.getData(1) + fT.getData(2) * fT.getData(2)) > lens.surface(i).getHeight()) {
					return false;
				}
			}
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		{
			double pos1 = lens.surface(lens.getNoOfSurfaces() - 1).getPosition();
			fP1.copy(pos1, 0.0, 0.0);
			fP1.add(fT);
			double pos2 = lens.getFilmPosition();
			fP2.copy(pos2, fQ.getData(1) * (pos2 - fP1.getData(0)) / fQ.getData(0), 0.0);
			if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
				double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
				y.setValue(fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1)));
				return true;
			}
		}
		return false;
	}

	/**
	 * 光線のポジションがxになっている高さyを探す
	 * 
	 * @param initD      間隔 d の初期値
	 * @param initT      位置ベクトル T の初期値
	 * @param initQ      光線の方向ベクトル T の初期値
	 * @param lens       追跡を行うレンズ系
	 * @param wavelength 波長番号
	 * @param 探すポジションx
	 * @param y          見つかった高さyを代入する
	 *                   ポインタが使えないため大きさ1の配列
	 * @return 見つかったとき, true
	 */
	public final boolean searchPointWithX(double initD, final TVector3D initT, final TVector3D initQ, final TLens lens,
			int wavelength, double x, TMyPtrDouble y) {
		fT.copy(initT);
		fQ.copy(initQ);
		double d = initD;
		for (int i = 0; i < lens.getNoOfSurfaces(); ++i) {
			if (i != 0) {
				d = lens.getD(i - 1);
			}
			double r = lens.surface(i).getR();
			if (fOp.getNextT(fT, fQ, d, r, fNextT) != TRayError.NO_ERROR) {
				return false;
			}
			{
				double pos = lens.surface(i).getPosition();
				fP1.copy(pos - d, 0.0, 0.0);
				fP1.add(fT);
				fP2.copy(pos, 0.0, 0.0);
				fP2.add(fNextT);
				if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
					double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
					y.setValue(fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1)));
					return true;
				}
			}
			fT.copy(fNextT);
			double n1 = lens.getGlass(i).getN(wavelength);
			double n2 = lens.getGlass(i + 1).getN(wavelength);
			if (fOp.getNextQ(fT, fQ, r, n1, n2, fNextQ) != TRayError.NO_ERROR) {
				return false;
			}
			fQ.copy(fNextQ);
		}
		{
			double pos1 = lens.surface(lens.getNoOfSurfaces() - 1).getPosition();
			fP1.copy(pos1, 0.0, 0.0);
			fP1.add(fT);
			double pos2 = lens.getFilmPosition();
			fP2.copy(pos2, fQ.getData(1) * (pos2 - fP1.getData(0)) / fQ.getData(0), 0.0);
			if ((fP1.getData(0) - x) * (fP2.getData(0) - x) <= 0.0) {
				double a = (x - fP1.getData(0)) / (fP2.getData(0) - fP1.getData(0));
				y.setValue(fP1.getData(1) + a * (fP2.getData(1) - fP1.getData(1)));
				return true;
			}
		}
		return false;
	}

	/**
	 * 光線に付け加える.
	 * 
	 * @param t      位置ベクトル T
	 * @param offset (絶対位置にするための)差分
	 * @param ray    登録先の追跡光線
	 */
	private final void addToRay(final TVector3D t, double offset, TRay ray) {
		fV.copy(t.getData(0) + offset, t.getData(1), t.getData(2));
		ray.appendVertex(fV);
	}

	/**
	 * 最後の頂点(像面上)を計算して, 光線に登録する.
	 * 
	 * @param q            方向ベクトル Q
	 * @param filmPosition 像面のポジション
	 * @param ray          登録先の追跡光線
	 */
	private final void calculateLastVertex(final TVector3D q, double filmPosition, TRay ray) {
		int last = ray.getCurrentSize();
		double factor = (filmPosition - ray.getVector3D(last - 1).getData(0)) / q.getData(0);
		double y = ray.getVector3D(last - 1).getData(1) + factor * q.getData(1);
		double z = ray.getVector3D(last - 1).getData(2) + factor * q.getData(2);
		fV.copy(filmPosition, y, z);
		ray.appendVertex(fV);
	}

	/**
	 * 光線が絞りに当たるかしらべる.
	 * 当たったとき true 当たらなかったとき falseを返す.
	 * 
	 * @param lens 光線追跡中のレンズ系
	 * @param ray  光線追跡中の光線
	 * @param i    追跡箇所
	 * @return 当たったとき false 当たらなかったとき true
	 */
	private final boolean checkStopR(final TLens lens, TRay ray, int i) {
		double stopPos = lens.getStopPosition();
		double x1 = ray.getVector3D(i).getData(0);
		double x2 = ray.getVector3D(i + 1).getData(0);
		if (x1 <= stopPos && stopPos <= x2) {
			double t = (stopPos - x1) / (x2 - x1);
			double y = ray.getVector3D(i).getData(1)
					+ t * (ray.getVector3D(i + 1).getData(1) - ray.getVector3D(i).getData(1));
			double z = ray.getVector3D(i).getData(2)
					+ t * (ray.getVector3D(i + 1).getData(2) - ray.getVector3D(i).getData(2));
			if (y * y + z * z > lens.getStopR() * lens.getStopR()) {
				return false;
			}
		}
		return true;
	}
}
