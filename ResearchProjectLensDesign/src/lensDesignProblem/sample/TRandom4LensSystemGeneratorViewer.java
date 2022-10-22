package lensDesignProblem.sample;

import java.util.Random;

import javax.swing.JFrame;

import lensDesignProblem.evaluator.TSMonochromeLensProblem;
import lensDesignProblem.plot.TLensPlot;

public class TRandom4LensSystemGeneratorViewer {

	/**
	 * 実行可能なものが得られるまで，レンズ系をランダムに生成する．
	 * 実行可能なレンズ系が得られたら，それまでに生成したレンズ系の数を標準出力に出力し，
	 * その形状と光線，および，評価値をウィンドウに表示する．
	 * レンズ系の設計仕様は，4枚組，F値2.0, 焦点距離50mm, 最大半画角23度である．
	 * 探索範囲は，レンズ厚0mm～5mm, 空気厚0mm～20mm, 曲率半径10mm～1,000mmである．
	 * 歪曲の重み1.0，解像度の重み1.0である．
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Random rand = new Random();
		TSMonochromeLensProblem problem = new TSMonochromeLensProblem("a g a g a g a g a", 2.0, 50.0, 23.0,
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
		TLensPlot plot = new TLensPlot(problem.getLens(), "Eval:" + eval);
		JFrame frame = new JFrame("TGSample Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 300);
		frame.getContentPane().add(plot);
		frame.setVisible(true);
	}

}
