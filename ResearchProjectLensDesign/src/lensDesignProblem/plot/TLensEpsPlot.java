package lensDesignProblem.plot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import lensDesignProblem.plot.epsMaker.TEPSMaker;
import lensDesignProblem.simulator.TChromaticLensEvaluator;
import lensDesignProblem.simulator.TEnforcementOperator;
import lensDesignProblem.simulator.TLens;
import lensDesignProblem.simulator.TRay;
import lensDesignProblem.simulator.TRayConstant;
import lensDesignProblem.simulator.TVector3D;
import lensDesignProblem.simulator.TWavelength;

/**
 * レンズ系をEPSファイルへ出力する．
 *
 * @author ishihara, isao
 */
public class TLensEpsPlot {

	public static final double MIN_VALUE_OF_AXIS = -10.0;

	/**
	 * EPS描画クラス
	 */
	private TEPSMaker fPen;

	/** レンズ */
	private TLens fLens = null;

	/** 光線 */
	private TRay fRays[][];

	/**
	 * コンストラクタ
	 */
	public TLensEpsPlot() {
	}

	/**
	 * 描画を実行する．
	 *
	 * @param filename 出力先のファイル名
	 * @param lens     レンズ系
	 */
	public void doIt(String filename, TLens lens) {
		fLens = lens;
		calcRays(lens);
		double[] extent = new double[4];
		fLens.getExtent(extent);
		double margin = (extent[2] - extent[0]) / 10.0;
		fPen = new TEPSMaker(filename, extent[0] - margin, extent[3] - margin, extent[2] + margin, extent[1] + margin);
		fPen.setLineWidth(0.5);
		drawAxis();
		drawFilm();
		drawLenses();
		drawStop();
		drawRays();
		fPen.close();
	}

	/**
	 * 光線を設定する。
	 *
	 * @param ray 光線
	 * @param w   入射角
	 * @param i   光線番号
	 */
	private void calcRays(TLens lens) {
		TChromaticLensEvaluator evaluator = new TChromaticLensEvaluator(true, true, true, false);
		TEnforcementOperator fEnforcementOperator = new TEnforcementOperator();
		fEnforcementOperator.doIt(lens); // レンズ最終面の強制
		evaluator.doIt(lens); // レンズを評価
		fRays = new TRay[TRayConstant.NO_OF_WS][TRayConstant.NO_OF_RAYS_FOR_DISPLAY];
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			for (int i = 0; i < TRayConstant.NO_OF_RAYS_FOR_DISPLAY; ++i) {
				fRays[w][i] = evaluator.getRaysForDisplay(TWavelength.REF_D, w, i);
			}
		}
	}

	/**
	 * 光線を描く
	 */
	private void drawRays() {
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			for (int i = 0; i < TRayConstant.NO_OF_RAYS_FOR_DISPLAY; ++i) {
				TRay ray = fRays[w][i];
				for (int j = 0; j < ray.getNoOfVertexes() - 1; ++j) {
					TVector3D v1 = ray.getVector3D(j);
					TVector3D v2 = ray.getVector3D(j + 1);
					fPen.line(v1.getX(), v1.getY(), v2.getX(), v2.getY());
				}
			}
		}
	}

	/**
	 * レンズ系を描画する。
	 */
	private void drawLenses() {
		for (int i = 0; i < fLens.getNoOfSurfaces(); ++i) {
			double r = fLens.surface(i).getR();
			double x = fLens.surface(i).getPosition();
			double x1 = 0.0, x2 = 0.0, y1 = 0.0, y2 = 0.0;
			double angle = 0.0, delta = 0.0;
			if (r > 0.0) {
				x1 = x;
				x2 = x + 2.0 * r;
				angle = 180.0 - fLens.surface(i).getAngleByDegree();
				delta = 2.0 * fLens.surface(i).getAngleByDegree();
				y1 = r;
				y2 = -r;
			} else {
				x1 = x + 2.0 * r;
				x2 = x;
				angle = -fLens.surface(i).getAngleByDegree();
				delta = 2.0 * fLens.surface(i).getAngleByDegree();
				y1 = -r;
				y2 = r;
			}
			fPen.frameArc(x1, y1, x2, y2, angle, delta);
			if (fLens.getConfig().isGlass(i)) {
				double x3 = fLens.surface(i - 1).getEdgePosition();
				double x4 = fLens.surface(i).getEdgePosition();
				double y3 = fLens.surface(i - 1).getHeight();
				double y4 = fLens.surface(i).getHeight();
				fPen.line(x3, y3, x4, y4);
				fPen.line(x3, -y3, x4, -y4);
			}
		}

	}

	/**
	 * フィルム面を描画する。
	 */
	private void drawFilm() {
		double filmX = fLens.getFilmPosition();
		double filmY = fLens.getFocusLength() * Math.tan(fLens.getWMax() * Math.PI / 180.0);
		fPen.line(filmX, filmY, filmX, -filmY);
	}

	/**
	 * 光軸を描画する。
	 */
	private void drawAxis() {
		fPen.line(MIN_VALUE_OF_AXIS, 0.0, fLens.getFilmPosition(), 0.0);
	}

	/**
	 * 絞りを描画する。
	 */
	private void drawStop() {
		double stopX = fLens.getStopPosition();
		double stopY = fLens.getStopR();
		fPen.line(stopX, stopY, stopX, stopY + 10.0);
		fPen.line(stopX, -stopY, stopX, -stopY - 10.0);
	}

	/**
	 * ダブルガウスレンズのデータをファイルから読み込んでEPSファイルを作成する．
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String lensFile = "GaussLens.txt"; // レンズデータ
		// String lensFile = "3lens-F3_0-f100-w19.txt"; // レンズデータ
		TLens lens = new TLens();
		BufferedReader br = new BufferedReader(new FileReader(lensFile));
		lens.readFrom(br);
		br.close();
		TLensEpsPlot plot = new TLensEpsPlot();
		plot.doIt("gauss", lens);
	}
}
