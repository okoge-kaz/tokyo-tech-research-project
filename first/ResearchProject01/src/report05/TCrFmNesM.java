package report05;

import java.io.IOException;

import crfmnes.TCrFmNes;
import crfmnes.TIndividual;
import crfmnes.matrix2017.TCMatrix;
import jssf.log.TCTable;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * CR-FM-NESを3試行実行するためのプログラム．
 * 各試行において，集団中の最良評価値の推移のロギングを行っている．
 * ログファイルはCSVフォーマットで出力される．
 * 実験設定は以下の通り：
 * ベンチマーク関数：k-tablet (k=n/4)，
 * 次元数：n=20，
 * 初期化領域：[+1,+5]^n，
 * => m = [3,...,3]^T, sigma = 1
 * サンプルサイズ：n，
 * 打ち切り評価回数：n × 1e5，
 * 打ち切り評価値：1.0 × 1e-7．
 * ログファイル名：RexJggOffsetKTabletP14K5.csv
 *
 * @author isao
 *
 */
public class TCrFmNesM {

	/**
	 * k-tablet関数 (k=n/4)
	 * @param s 個体
	 */
	private static double ktablet(TCMatrix x) {
		int k = (int)((double)x.getDimension() /4.0); //k=n/4
		double result = 0.0; //評価値を初期化
		for (int i = 0; i < x.getDimension(); ++i) {
			double xi = x.getValue(i); //i番目の次元の要素
			if (i < k) {
				result += xi * xi;
			} else {
				result += 10000.0 * xi * xi;
			}
		}
		return result;
	}

	private static void evaluate(TIndividual[] pop) {
		for (int i = 0; i < pop.length; ++i) {
			double eval = ktablet(pop[i].getX());
			pop[i].setEvaluationValue(eval);
		}
	}

	/**
	 * 最良評価値をログテーブルに記録する．
	 * @param log ログテーブル
	 * @param trialName 試行名．ログテーブルのラベルに使われる．
	 * @param trialNo 試行番号．ログテーブルのラベルに使われる．
	 * @param index 行数の添字
	 * @param noOfEvals 評価回数
	 * @param bestEvaluationValue 最良評価値
	 */
	private static void putLogData(TCTable log, String trialName, int trialNo, int index, long noOfEvals, double bestEvaluationValue) {
		log.putData(index, "NoOfEvals", noOfEvals);
		log.putData(index, trialName + "_" + trialNo, bestEvaluationValue);
	}

	/**
	 * 1試行を実行する．
	 * @param ga GA
	 * @param maxEvals 打ち切り評価回数
	 * @param log ログテーブル
	 * @param trialName 試行名．ログテーブルのラベルに使われる．
	 * @param trialNo 試行番号．ログテーブルのラベルに使われる．
	 */
	private static void executeOneTrial(TCrFmNes crfmnes, long maxEvals, TCTable log, String trialName, int trialNo) {
		long noOfEvals = 0; //評価回数を初期化．
		double best = Double.MAX_VALUE; //最良評価値
		int logIndex = 0; //ログテーブルの行の添字を初期化．
		int loopCount = 0; //ループカウンタを初期化する．
    double start = System.currentTimeMillis();
		while (best > 1e-7 && noOfEvals < maxEvals) { //終了条件．最良値が10^-7以下，もしくは，評価回数が打ち切り評価回数を超えたとき．
      TIndividual[] pop = crfmnes.samplePopulation();
      evaluate(pop);
      noOfEvals += pop.length;
      crfmnes.sort();
      best = crfmnes.getBestEvaluationValue();
      crfmnes.nextGeneration();
			if (loopCount % 10 == 0) { //ループカウンタが10の倍数のときにログをとる．
				putLogData(log, trialName, trialNo, logIndex, noOfEvals, best);
				++logIndex; //ログテーブルの行の添字を１進める．
			}
			++loopCount; //ループカウントを１進める．
		}
    double time = System.currentTimeMillis() - start;
		System.out.println("TrialNo:" + trialNo + ", NoOfEvals:" + noOfEvals + ", Best:" + best + ", Time:" + time + "[msec]"); //画面に試行数，評価回数，最良評価値，実行時間を表示．
		putLogData(log, trialName, trialNo, logIndex, noOfEvals, best); //最終世代のログをとる．
	}


	/**
	 * メインメソッド
	 * @param args
	 * @throws IOException
	 */
  public static void main(String[] args) throws IOException {
    int dim = 20; //次元数
		String trialName = "CrFmNesOffsetKTabletS1"; //試行名
		String logFilename = trialName + ".csv"; //ログファイル名
    int sampleSize = dim; //サンプルサイズ
		int maxTrials = 3; //試行数
		long maxEvals = (long)(4 * dim * 1e4); //打ち切り評価回数
		ICRandom random = new TCJava48BitLcg();
    TCMatrix m = new TCMatrix(dim).fill(3.0); //平均ベクトルの初期値
    double sigma = 1.0; //標準偏差の初期値
    TCMatrix D = new TCMatrix(dim).fill(1.0); //対角行列の初期値
    TCMatrix v = new TCMatrix(dim);
    for (int i = 0; i < dim; ++i) {
      v.setValue(i, random.nextGaussian() / dim);
    }
		TCTable log = new TCTable(); //ログテーブル
		for (int trial = 0; trial < maxTrials; ++trial) {
	    TCrFmNes crfmnes = new TCrFmNes(dim, sampleSize, m, sigma, D, v, random);
			executeOneTrial(crfmnes, maxEvals, log, trialName, trial); //1試行実行
		}
		log.writeTo(logFilename); //3試行分のログをファイルに出力．
  }
}
