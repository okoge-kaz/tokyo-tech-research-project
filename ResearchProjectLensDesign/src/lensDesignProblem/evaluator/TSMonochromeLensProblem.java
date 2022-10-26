package lensDesignProblem.evaluator;

import java.io.Serializable;
import java.util.Random;

import javax.swing.JFrame;

import lensDesignProblem.plot.TLensPlot;
import lensDesignProblem.simulator.TChromaticLensEvaluator;
import lensDesignProblem.simulator.TEnforcementOperator;
import lensDesignProblem.simulator.TLens;
import lensDesignProblem.simulator.TLensConfig;
import lensDesignProblem.simulator.TRayConstant;
import lensDesignProblem.simulator.TTransformer;

/**
 * 固定焦点単色レンズ設計問題．
 * 歪曲と解像度の線形加重和を評価値として返す．
 *
 * @author isao
 *
 */
public class TSMonochromeLensProblem implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/** 探索空間の定義域の最小値．-1,000に正規化されている． */
	public static double MIN = TTransformer.GA_MIN;

	/** 探索空間の定義域の最大値． +1,000に正規化されている． */
	public static double MAX = TTransformer.GA_MAX;

	/** 光線の入射角のID（平行） */
	public static int W0 = 0;

	/** 光線の入射角のID（最大画角の0.65倍） */
	public static int W065 = 1;

	/** 光線の入射角のID（最大画角） */
	public static int W1 = 2;

	/** 評価対象のレンズ系 */
	private TLens fLens;

	/** 焦点距離を満たすように，最終面の曲率，最終面と像面との距離を強制するオペレータ */
	private TEnforcementOperator fEnforcementOperator;

	/** レンズ系の評価器 */
	private TChromaticLensEvaluator fEvaluator;

	/** 決定変数ベクトルとレンズ系の変換器 */
	private TCArrayMonochromeLensConverter fConverter;

	/** 歪曲の重み */
	private double fWeightForDistortion;

	/** 解像度の重み */
	private double fWeightForResolution;

	/**
	 * コンストラクタ
	 *
	 * @param config              レンズ系の構成．"a g a g a"は「空気 ガラス 空気 ガラス
	 *                            空気」を表す．必ず，最初と最後は「空気」とすること．
	 * @param fNumber             F値
	 * @param focalLength         焦点距離
	 * @param wMax                最大半画角
	 * @param lensGapMin          レンズ厚の最小値
	 * @param lensGapMax          レンズ厚の最大値
	 * @param airGapMin           空気厚の最小値
	 * @param airGapMax           空気厚の最大値
	 * @param radiusMin           曲率半径の最小値
	 * @param radiusMax           曲率半径の最大値
	 * @param weightForDistortion 歪曲の重み
	 * @param weightForResolution 解像度の重み
	 */
	public TSMonochromeLensProblem(String config,
			double fNumber, double focalLength, double wMax,
			double lensGapMin, double lensGapMax,
			double airGapMin, double airGapMax,
			double radiusMin, double radiusMax,
			double weightForDistortion, double weightForResolution) {
		fLens = new TLens(new TLensConfig(config), fNumber, focalLength, wMax);
		fEnforcementOperator = new TEnforcementOperator();
		fEvaluator = new TChromaticLensEvaluator(false, true, false, false);
		fConverter = new TCArrayMonochromeLensConverter(lensGapMin, lensGapMax, airGapMin, airGapMax, radiusMin, radiusMax);
		fWeightForDistortion = weightForDistortion;
		fWeightForResolution = weightForResolution;
	}

	public TSMonochromeLensProblem clone() {
		String confiString = fLens.getConfig().toString();
		double fNumber = fLens.getFNumber();
		double focalLength = fLens.getFocusLength();
		double wMax = fLens.getWMax();
		double lensGapMin = fConverter.getLensGapMin();
		double lensGapMax = fConverter.getLensGapMax();
		double airGapMin = fConverter.getAirGapMin();
		double airGapMax = fConverter.getAirGapMax();
		double radiusMin = fConverter.getRadiusMin();
		double radiusMax = fConverter.getRadiusMax();
		return new TSMonochromeLensProblem(confiString, fNumber, focalLength, wMax,
				lensGapMin, lensGapMax,
				airGapMin, airGapMax,
				radiusMin, radiusMax,
				fWeightForDistortion, fWeightForResolution);
	}

	/**
	 * 決定変数の数を返す．
	 *
	 * @return 決定変数の数
	 */
	public int getDimension() {
		return fLens.getNoOfSurfaces() * 2 - 2;
	}

	/**
	 * 決定変数ベクトルxの評価値を計算して返す．
	 *
	 * @param x 決定変数ベクトル．要素の定義域は[-1000, +1000]．次元数は，getDimensionメソッドで返されたもの．
	 * @return 評価値．実行不可能の場合はDouble.MAX_VALUEが返る．
	 */
	public double evaluate(double[] x) {
		if (!fConverter.convertVectorToLens(x, fLens)) {
			// System.err.print("1");
			return Double.MAX_VALUE;
		}
		if (!fEnforcementOperator.doIt(fLens)) {
			// System.err.print("2");
			return Double.MAX_VALUE;
		}
		if (!fEvaluator.doIt(fLens)) {
			// System.err.print("3");
			return Double.MAX_VALUE;
		}
		double result = 0.0;
		for (int w = 0; w < TRayConstant.NO_OF_WS; ++w) {
			result += fWeightForDistortion * fLens.getDistortion(w);
			result += fWeightForResolution * fLens.getResolution(w);
		}
		return Math.sqrt(result);
	}

	/**
	 * 直近に評価されたレンズ系を返す．
	 *
	 * @return レンズ系
	 */
	public TLens getLens() {
		return fLens;
	}

	/**
	 * 決定変数ベクトルとレンズ系の変換器を返す．
	 *
	 * @return 決定変数ベクトルとレンズ系の変換器
	 */
	public TCArrayMonochromeLensConverter getConverter() {
		return fConverter;
	}

	/**
	 * 歪曲の重みを返す．
	 *
	 * @return 歪曲の重み
	 */
	public double getWeightForDistortion() {
		return fWeightForDistortion;
	}

	/**
	 * 解像度の重み
	 *
	 * @return 解像度の重み
	 */
	public double getWeightForResolution() {
		return fWeightForResolution;
	}

	/**
	 * 実行可能なものが得られるまで，レンズ系をランダムに生成する．
	 * 実行可能なレンズ系が得られたら，それまでに生成したレンズ系の数を標準出力に出力し，
	 * その形状と光線，および，評価値をウィンドウに表示する．
	 * レンズ系の設計仕様は，3枚組，F値3.0, 焦点距離100mm, 最大半画角19度である．
	 * 探索範囲は，レンズ厚0mm～5mm, 空気厚0mm～20mm, 曲率半径10mm～1,000mmである．
	 * 歪曲の重み1.0，解像度の重み1.0である．
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Random rand = new Random();
		TSMonochromeLensProblem problem = new TSMonochromeLensProblem("a g a g a g a", 3.0, 100.0, 19.0,
				0.0, 5.0, 0.0, 20.0, 10.0, 1000.0,
				1.0, 1.0); // 固定焦点単色レンズ設計問題を生成している．
		double[] x = new double[problem.getDimension()]; // 決定変数ベクトルを生成している．必要な次元数を固定焦点単色レンズ設計問題から得ていることに注意．
		int counter = 0; // 実行可能解が得られるまでに生成される解の数を数えるためのカウンタ．
		double eval = Double.MAX_VALUE; // 解の評価値
		while (true) {
			++counter;
			// 決定変数ベクトルの各要素を[-1000, +1000]で一様ランダムに初期化している．
			for (int i = 0; i < x.length; ++i) {
				x[i] = rand.nextDouble() * (TSMonochromeLensProblem.MAX - TSMonochromeLensProblem.MIN)
						+ TSMonochromeLensProblem.MIN;
			}
			eval = problem.evaluate(x); // 評価値を計算している．
			if (eval < Double.MAX_VALUE) { // 実行可能だったら，レンズ系のパラメータとカウンタを表示してループを抜ける．
				System.out.println(problem.getLens());
				System.out.println("Counter:" + counter);
				break;
			}
		}
		// 以下，レンズ系の表示．
		TLensPlot plot = new TLensPlot(problem.getLens(), "Eval:" + 1.3155973460742414);
		JFrame frame = new JFrame("TGSample Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 300);
		frame.getContentPane().add(plot);
		frame.setVisible(true);
	}
}
