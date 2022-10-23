package report03;

import java.io.IOException;

import jgoal.ga.TSUndxMgg;
import jgoal.solution.ICSolution.Status;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jssf.log.TCTable;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * UNDX+MGGを3試行実行するためのプログラム．
 * 各試行において，集団中の最良評価値の推移のロギングを行っている．
 * ログファイルはCSVフォーマットで出力される．
 * 実験設定は以下の通り：
 * ベンチマーク関数：k-tablet (k=n/4)，
 * 次元数：n=20，
 * 初期化領域：[-5,+5]^n，
 * 集団サイズ：14n，
 * 子個体生成数：5n，
 * 打ち切り評価回数：n × 1e5，
 * 打ち切り評価値：1.0 × 1e-7．
 * ログファイル名：UndxMggKTabletP14K5.csv
 * 
 * @author isao
 *
 */
public class TSUndxMggM {

	/**
	 * 初期集団の初期化を行う．
	 * 
	 * @param population 初期集団
	 * @param min        初期化領域の最小値
	 * @param max        初期化領域の最大値
	 * @param random     乱数生成器
	 */
	private static void initializePopulation(TCSolutionSet<TSRealSolution> population, double min, double max,
			ICRandom random) {
		for (TSRealSolution s : population) {
			s.getVector().rand(random).times(max - min).add(min); // 個体の座標を範囲[min, max]^nの乱数で初期化．
		}
	}

	/**
	 * 集団中の全ての個体の評価を行う．
	 * 
	 * @param population 集団
	 */
	private static void evaluate(TCSolutionSet<TSRealSolution> population) {
		for (TSRealSolution s : population) {
			double eval = ktablet(s.getVector()); // k-tablet関数の値を得る．
			s.setEvaluationValue(eval); // 個体に評価値を設定．
			s.setStatus(Status.FEASIBLE); // 個体の状態を「実行可能」に設定．
		}
	}

	/**
	 * k-tablet関数 (k=n/4)
	 * 
	 * @param s 個体
	 */
	private static double ktablet(TCMatrix x) {
		int k = (int) ((double) x.getDimension() / 4.0); // k=n/4
		double result = 0.0; // 評価値を初期化
		for (int i = 0; i < x.getDimension(); ++i) {
			double xi = x.getValue(i); // i番目の次元の要素
			if (i < k) {
				result += xi * xi;
			} else {
				result += 10000.0 * xi * xi;
			}
		}
		return result;
	}

	/**
	 * 最良評価値をログテーブルに記録する．
	 * 
	 * @param log                 ログテーブル
	 * @param trialName           試行名．ログテーブルのラベルに使われる．
	 * @param trialNo             試行番号．ログテーブルのラベルに使われる．
	 * @param index               行数の添字
	 * @param noOfEvals           評価回数
	 * @param bestEvaluationValue 最良評価値
	 */
	private static void putLogData(TCTable log, String trialName, int trialNo, int index, long noOfEvals,
			double bestEvaluationValue) {
		log.putData(index, "NoOfEvals", noOfEvals);
		log.putData(index, trialName + "_" + trialNo, bestEvaluationValue);
	}

	/**
	 * 1試行を実行する．
	 * 
	 * @param ga        GA
	 * @param maxEvals  打ち切り評価回数
	 * @param log       ログテーブル
	 * @param trialName 試行名．ログテーブルのラベルに使われる．
	 * @param trialNo   試行番号．ログテーブルのラベルに使われる．
	 */
	private static void executeOneTrial(TSUndxMgg ga, long maxEvals, TCTable log, String trialName, int trialNo) {
		long noOfEvals = 0; // 評価回数を初期化．
		double best = ga.getBestEvaluationValue(); // 集団の最良評価値を取得．
		int logIndex = 0; // ログテーブルの行の添字を初期化．
		putLogData(log, trialName, trialNo, logIndex, noOfEvals, best); // 初期集団の情報をログに保存．
		++logIndex; // ログテーブルの行を１進める．
		int loopCount = 0; // ループカウンタを初期化する．
		while (best > 1e-7 && noOfEvals < maxEvals) { // 終了条件．最良値が10^-7以下，もしくは，評価回数が打ち切り評価回数を超えたとき．
			TCSolutionSet<TSRealSolution> offspring = ga.makeOffspring(); // 子個体集団を生成．
			evaluate(offspring); // 子個体集団を評価
			noOfEvals += offspring.size(); // 評価回数を更新
			ga.nextGeneration(); // GAの世代を１世代進める．
			best = ga.getBestEvaluationValue(); // 集団内の最良評価値を取得．
			if (loopCount % 10 == 0) { // ループカウンタが１０の倍数のときにログをとる．
				putLogData(log, trialName, trialNo, logIndex, noOfEvals, best);
				++logIndex; // ログテーブルの行の添字を１進める．
			}
			++loopCount; // ループカウントを１進める．
		}
		putLogData(log, trialName, trialNo, logIndex, noOfEvals, best); // 最終世代のログをとる．
		System.out.println("TrialNo:" + trialNo + ", NoOfEvals:" + noOfEvals + ", Best:" + best); // 画面に試行数，評価回数，最良評価値を表示．
	}

	/**
	 * メインメソッド．
	 * 
	 * @param args なし
	 */
	public static void main(String[] args) throws IOException {
		boolean minimization = true; // 最小化
		int dimension = 20; // 次元数
		int populationSize = 14 * dimension; // 集団サイズ
		int noOfKids = 5 * dimension; // 子個体生成数
		double min = -5.00; // 初期化領域の最小値
		double max = +5.00; // 初期化領域の最大値
		long maxEvals = (long) (4 * dimension * 1e4); // 打ち切り評価回数
		int maxTrials = 3; // 試行数
		String trialName = "UndxMggKTabletP14K5"; // 試行名
		String logFilename = "data/report3" + trialName + ".csv"; // ログファイル名

		ICRandom random = new TCJava48BitLcg(); // 乱数生成器
		TSUndxMgg ga = new TSUndxMgg(minimization, dimension, populationSize, noOfKids, random); // UNDX+MGG
		TCTable log = new TCTable(); // ログテーブル
		for (int trial = 0; trial < maxTrials; ++trial) {
			TCSolutionSet<TSRealSolution> population = ga.initialize(); // 初期集団を取得．
			initializePopulation(population, min, max, random); // 初期集団を初期化
			evaluate(population); // 初期集団を評価
			executeOneTrial(ga, maxEvals, log, trialName, trial); // 1試行実行
		}
		log.writeTo(logFilename); // 3試行分のログをファイルに出力．
	}

}
