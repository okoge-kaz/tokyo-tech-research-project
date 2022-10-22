package lensDesignProblem.simulator;

/* use
 *  TVector3D
 *  TRayError
 */

/*
レンズ工学 p.81-88
*/

/**
 * 光線追跡に利用するオペレータを扱う クラス<BR>
 * 
 * @author Kenta Hirano
 */
public class TRayTraceOperator {
	private TVector3D fM;
	private TVector3D fE;
	private TVector3D fBuf1;
	private TVector3D fBuf2;

	public TRayTraceOperator() {
		fM = TVector3D.newInstance();
		fE = TVector3D.newInstance();
		fBuf1 = TVector3D.newInstance();
		fBuf2 = TVector3D.newInstance();
	}

	/**
	 * 次の面の位置ベクトルTを求めるオペレータ
	 * (レンズ工学 p.81-85)
	 * 
	 * @param t     今の面の位置ベクトルT
	 * @param q     今の光線の方向ベクトルQ
	 * @param d     次の面までの距離d
	 * @param r     面の曲率半径r
	 * @param nextT 次の面の位置ベクトルT
	 * @return エラー番号(TRayError)
	 */
	public final int getNextT(final TVector3D t, final TVector3D q,
			final double d, final double r,
			TVector3D nextT) {
		/* (3.1.3) */
		double p = -((t.getData(0) - d) * q.getData(0)
				+ t.getData(1) * q.getData(1)
				+ t.getData(2) * q.getData(2));
		/* (3.1.5) */
		fM.copy(t.getData(0) - d + p * q.getData(0),
				t.getData(1) + p * q.getData(1),
				t.getData(2) + p * q.getData(2));

		/* (3.1.12) */
		double m2 = fM.getData(0) * fM.getData(0)
				+ fM.getData(1) * fM.getData(1)
				+ fM.getData(2) * fM.getData(2);
		/* (3.1.14) aはルートの中 */
		double a = q.getData(0) * q.getData(0)
				- (m2 / r - 2.0 * fM.getData(0)) / r;
		/* a < 0 でレンズと光線の交点なし */
		if (a < 0.0)
			return TRayError.NO_INTERSECTION;

		/* カメラレンズでは光線は必ず左から右へ進む (3.1.17,18) */
		double s = q.getData(0) > 0.0 ? 1.0 : -1.0; /* s:sign */
		/* assert ( s > 0.0 ); */

		/* (3.1.19) (3.1.11) */
		double e; /* ε */
		e = (m2 / r - 2.0 * fM.getData(0)) / (q.getData(0) + s * Math.sqrt(a));
		/* p + e < 0.0 で光線が逆走 */
		if (p + e < 0.0)
			return TRayError.INVERSE_DIRECTION;
		nextT.setData(0, t.getData(0) - d + (p + e) * q.getData(0));

		if (r >= 0.0 && nextT.getData(0) > r)
			return TRayError.NO_INTERSECTION2;
		if (r < 0.0 && nextT.getData(0) < r)
			return TRayError.NO_INTERSECTION3;
		nextT.setData(1, t.getData(1) + (p + e) * q.getData(1));
		nextT.setData(2, t.getData(2) + (p + e) * q.getData(2));
		return TRayError.NO_ERROR;
	}

	/**
	 * 次の光線の方向ベクトルQを求めるオペレータ
	 * (レンズ工学 p.86-88)
	 * 
	 * @param t     今の面の位置ベクトルT
	 * @param q     今の光線の方向ベクトルQ
	 * @param r     面の曲率半径r
	 * @param n1    前の空間の屈折率N
	 * @param n2    次の空間の屈折率N'
	 * @param nextQ 次の光線の方向ベクトルQ
	 * @return エラー番号(TRayError)
	 */
	public final int getNextQ(final TVector3D t, final TVector3D q,
			final double r,
			final double n1, final double n2,
			TVector3D nextQ) {
		/* 球面の法線 中心向き */
		fE.copy(1.0 - t.getData(0) / r,
				-t.getData(1) / r,
				-t.getData(2) / r);

		double cosi1 = q.returnInnerProduct(fE);

		/* (3.1.30) aはルートの中 */
		double a = 1.0 - (n1 * n1 * (1.0 - cosi1 * cosi1)) / (n2 * n2);
		/* a < 0 で全反射 */
		if (a < 0.0)
			return TRayError.FULL_REFLECTION;
		double s = cosi1 > 0.0 ? 1.0 : -1.0; /* 正で屈折、負で反射 */
		if (s < 0.0)
			return TRayError.FULL_REFLECTION2;
		double cosi2 = s * Math.sqrt(a);

		fBuf1.putScalerProduct((n1 / n2), q);
		fBuf2.putScalerProduct(fE, (cosi2 - n1 * cosi1 / n2));
		nextQ.putAdd(fBuf1, fBuf2);

		/* nextQ.fArray(0) < 0.0 で光線が逆走 */
		if (nextQ.getData(0) < 0.0)
			return TRayError.INVERSE_DIRECTION2;
		return TRayError.NO_ERROR;
	}
}
