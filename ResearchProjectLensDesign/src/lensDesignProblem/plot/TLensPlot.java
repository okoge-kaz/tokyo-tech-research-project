package lensDesignProblem.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lensDesignProblem.simulator.TChromaticLensEvaluator;
import lensDesignProblem.simulator.TEnforcementOperator;
import lensDesignProblem.simulator.TLens;
import lensDesignProblem.simulator.TRay;
import lensDesignProblem.simulator.TRayConstant;
import lensDesignProblem.simulator.TVector3D;
import lensDesignProblem.simulator.TWavelength;

/**
 * レンズビューア
 *
 * @author ishihara, isao
 */
public class TLensPlot extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final double MIN_VALUE_OF_AXIS = -10.0;

	/** 描画用のペン */
	private TPen fPen = new TPen();

	/** レンズ */
	private TLens fLens = null;

	/** 光線 */
	private TRay fRays[][];

	/** 表示文字列 */
	private String fMessage;

	/**
	 * コンストラクタ
	 *
	 */
	public TLensPlot() {
		setBackground(Color.WHITE);
		fMessage = "";
	}

	/**
	 * コンストラクタ
	 *
	 * @param lens レンズ系
	 * @param msg  レンズ系の説明文字列．評価値などを指定するとよい．表示したくない場合は""を与えること．
	 */
	public TLensPlot(TLens lens, String msg) {
		setBackground(Color.WHITE);
		setLens(lens, msg);
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
	 * レンズ系を設定する。
	 *
	 * @param lens レンズ系
	 * @param msg  レンズ系の説明文字列．評価値などを指定するとよい．表示したくない場合は""を与えること．
	 */
	public void setLens(TLens lens, String msg) {
		fLens = lens;
		fMessage = msg;
		calcRays(lens);
		repaint();
	}

	/**
	 * レンズ系を返す．
	 *
	 * @return レンズ系
	 */
	public TLens getLens() {
		return fLens;
	}

	/**
	 * 光線を描く
	 *
	 */
	private void drawRays() {
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			for (int i = 0; i < TRayConstant.NO_OF_RAYS_FOR_DISPLAY; ++i) {
				TRay ray = fRays[w][i];
				for (int j = 0; j < ray.getNoOfVertexes() - 1; ++j) {
					fPen.setColor(Color.yellow.darker());
					TVector3D v1 = ray.getVector3D(j);
					TVector3D v2 = ray.getVector3D(j + 1);
					fPen.drawLine(v1.getX(), v1.getY(), v2.getX(), v2.getY());
				}
			}
		}
	}

	/**
	 * レンズ系を描画する。
	 *
	 */
	private void drawLenses() {
		for (int i = 0; i < fLens.getNoOfSurfaces(); ++i) {
			fPen.setColor(Color.blue.darker());
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
			fPen.drawArc(x1, y1, x2, y2, angle, delta);
			if (fLens.getConfig().isGlass(i)) {
				double x3 = fLens.surface(i - 1).getEdgePosition();
				double x4 = fLens.surface(i).getEdgePosition();
				double y3 = fLens.surface(i - 1).getHeight();
				double y4 = fLens.surface(i).getHeight();
				fPen.drawLine(x3, y3, x4, y4);
				fPen.drawLine(x3, -y3, x4, -y4);
			}
		}

	}

	/**
	 * フィルム面を描画する。
	 *
	 */
	private void drawFilm() {
		fPen.setColor(Color.green.darker());
		double filmX = fLens.getFilmPosition();
		double filmY = fLens.getFocusLength() * Math.tan(fLens.getWMax() * Math.PI / 180.0);
		fPen.drawLine(filmX, filmY, filmX, -filmY);
	}

	/**
	 * 光軸を描画する。
	 *
	 */
	private void drawAxis() {
		fPen.setColor(Color.red.darker());
		fPen.drawLine(MIN_VALUE_OF_AXIS, 0.0, fLens.getFilmPosition(), 0.0);
	}

	/**
	 * 絞りを描画する。
	 *
	 */
	private void drawStop() {
		fPen.setColor(Color.green.darker());
		double stopX = fLens.getStopPosition();
		double stopY = fLens.getStopR();
		fPen.drawLine(stopX, stopY, stopX, stopY + 10.0);
		fPen.drawLine(stopX, -stopY, stopX, -stopY - 10.0);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		fPen.setGraphics(g);
		int width = getWidth();
		int height = getHeight();
		if (fLens == null)
			return;
		double[] extent = new double[4];
		fLens.getExtent(extent);
		fPen.setScaling(extent[0] - 10.0, extent[1] + 10.0, extent[2] + 10.0, extent[3] - 10.0, 0, 0, width, height);
		drawAxis();
		drawFilm();
		drawLenses();
		drawStop();
		drawRays();
		fPen.setDefaultScaling();
		fPen.setColor(Color.BLACK);
		fPen.drawString(fMessage, 200, height - 20);
	}

	public Dimension getPreferredSize() {
		return new Dimension(500, 400);
	}

	/**
	 * ダブルガウスレンズのデータをファイルから読み込んで表示する．
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
		TLensPlot plot = new TLensPlot(lens, "");
		JFrame frame = new JFrame("TGSample Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 300);
		frame.getContentPane().add(plot);
		frame.setVisible(true);
	}
}
