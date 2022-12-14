package rexjgg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jssf.log.TCTable;

public class RexJgg {

  /**
   * ベンチマーク関数
   *
   * @param individual
   *                     個体
   *
   * @param k
   *                     k-tabletのk
   */
  private static double benchmark(TIndividual individual, final int k) {
    TVector vector = individual.getVector();
    double evaluationValue = 0.0;

    for (int i = 0; i < vector.getDimension(); ++i) {
      if (i + 1 <= k) {
        evaluationValue += Math.pow(vector.getElement(i), 2);
      } else {
        evaluationValue += Math.pow(100 * vector.getElement(i), 2);
      }
    }

    return evaluationValue;
  }

  /*
   * 初期集団の生成
   *
   * [-5, 5]の範囲でランダムに個体を生成する
   *
   * 個体個数は 14n (= populationSize)
   *
   * 次元は n (= dimension)
   *
   * 個体ごとの評価値計算までする
   */
  private static ArrayList<TIndividual> createInitialPopulation(final int populationSize, final int dimension) {
    ArrayList<TIndividual> population = new ArrayList<TIndividual>();

    final double vectorMin = -5.0;
    final double vectorMax = 5.0;

    for (int i = 0; i < populationSize; ++i) {
      TVector vector = new TVector(dimension);
      for (int j = 0; j < dimension; ++j) {
        // 一様乱数に基づいてベクトルの要素を生成
        vector.setElement(j, vectorMin + (vectorMax - vectorMin) * Math.random());
      }

      TIndividual individual = new TIndividual(vector);
      population.add(individual);

      // 個体の評価値を設定
      double evaluationValue = benchmark(individual, dimension / 4);
      population.get(i).setEvaluationValue(evaluationValue);
    }

    return population;
  }

  /**
   * 親個体からランダムにn+1個体を非復元抽出する
   *
   * @param population
   *                     親個体
   *
   * @param n
   */
  private static ArrayList<TIndividual> selectParents(ArrayList<TIndividual> population, final int n) {
    // population自体の順番が変わるようにする
    assert (population.size() == 14 * n);

    for (int i = 0; i < n + 1; ++i) {
      Random random = new Random();
      int index = (int) (random.nextDouble() * (population.size() - 1));

      assert (0 <= index && index <= n);

      Collections.swap(population, i, index);
    }
    ArrayList<TIndividual> parents = new ArrayList<TIndividual>(population.subList(0, n + 1));

    // [0, n+1)の範囲を抽出
    return parents;
  }

  /*
   * 親個体の平均値を求める
   */
  private static TVector calcMeanVector(ArrayList<TIndividual> parents) {
    TVector meanVector = new TVector(parents.get(0).getVector().getDimension());
    for (int i = 0; i < parents.size(); ++i) {
      meanVector = meanVector.add(parents.get(i).clone().getVector());
    }
    meanVector = meanVector.scalarProduct(1.0 / parents.size());
    return meanVector;
  }

  /**
   * 親個体の差分ベクトルを求める
   *
   * @param selectedParents
   *                          親個体
   *
   * @param meanVector
   *                          親個体の平均値
   *
   * @param n
   *                          次元数(ベクトル)
   */
  private static TVector calcDiffVector(ArrayList<TIndividual> parents, TVector meanVector, int n) {
    TVector diffVector = new TVector(parents.get(0).getVector().getDimension());
    // 0初期化済み

    for (int i = 0; i < parents.size(); ++i) {
      Random random = new Random();
      double sigma = random.nextGaussian() * Math.sqrt(1.0 / n);
      // nextGaussian()は標準正規分布に従う乱数を生成する
      // Returns the next pseudorandom, Gaussian ("normally") distributed double value
      // with mean 0.0 and standard deviation 1.0 from this random number generator's
      // sequence.
      // https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextGaussian--
      diffVector = diffVector.add(parents.get(i).clone().getVector().subtract(meanVector).scalarProduct(sigma));
    }
    return diffVector;
  }

  /*
   * 親個体から、 5n 個の子個体を生成する
   */
  private static ArrayList<TIndividual> createChildren(ArrayList<TIndividual> parents, final int populationSize,
      final int dimension) {
    // 手順3, 4
    ArrayList<TIndividual> children = new ArrayList<TIndividual>();

    for (int i = 0; i < 5 * dimension; ++i) {// 子個体の数は 5n (= 5 * dimension)

      // 親個体の情報から子個体のベクトルを生成する
      TVector childVector = new TVector(dimension);
      // xi = <y> + sum(1, n+1, (yj - <y>))
      TVector meanVector = calcMeanVector(parents);
      childVector = meanVector.add(calcDiffVector(parents, meanVector, dimension));// dimension = n

      TIndividual child = new TIndividual(childVector);

      // 子個体の評価値を設定
      // 手順 4
      double evaluationValue = benchmark(child, dimension / 4);
      child.setEvaluationValue(evaluationValue);
      // push to array
      children.add(child);
    }

    return children;
  }

  private static double getBestEvaluationValue(ArrayList<TIndividual> population) {
    // deep copy
    ArrayList<TIndividual> sortedPopulation = new ArrayList<TIndividual>();
    for (TIndividual individual : population) {
      sortedPopulation.add(individual.clone());
    }

    Collections.sort(sortedPopulation);

    // 評価値順になっているならばこれでよい
    double bestEvaluationValue = sortedPopulation.get(0).getEvaluationValue();

    return bestEvaluationValue;
  }

  /*
   * best evaluation value <= 1.0 * 10^(-7)
   */
  private static boolean isTerminated(ArrayList<TIndividual> population, int evaluationCount) {
    double bestEvaluationValue = getBestEvaluationValue(population);
    // for debug
    if (evaluationCount % 10000 == 0) {
      System.out.println("Number Of Evaluation:" + evaluationCount + ", Best:" + bestEvaluationValue); // 画面に評価回数，最良評価値を表示．
    }

    if (bestEvaluationValue <= 1.0 * Math.pow(10, -7)) {
      return true;
    }
    return false;
  }

  /**
   * 最良評価値をログテーブルに記録する．
   * 
   * @param log
   *                              ログテーブル
   * @param trialName
   *                              試行名．ログテーブルのラベルに使われる．
   * @param trialNo
   *                              試行番号．ログテーブルのラベルに使われる．
   * @param index
   *                              行数の添字
   * @param noOfEvals
   *                              評価回数
   * @param bestEvaluationValue
   *                              最良評価値
   */
  private static void putLogData(TCTable log, String trialName, int trialNo, int index, long noOfEvals,
      double bestEvaluationValue) {
    log.putData(index, "NoOfEvals", noOfEvals);
    log.putData(index, trialName + "_" + trialNo, bestEvaluationValue);
  }

  private static void executeOneTrial(final int populationSize, final int n, TCTable log, String trialName,
      int trialNo) {
    ArrayList<TIndividual> population = createInitialPopulation(populationSize, n);

    // log
    int logIndex = 0;
    long noOfEvals = 0; // 評価回数を初期化
    double bestEvaluationValue = getBestEvaluationValue(population);

    putLogData(log, trialName, trialNo, logIndex, noOfEvals, bestEvaluationValue);
    logIndex++;

    for (int evaluationCount = 0; evaluationCount < 4 * n * 10000; evaluationCount += 5 * n) {
      // step 2
      ArrayList<TIndividual> parents = selectParents(population, n + 1);

      // step 3, step 4
      ArrayList<TIndividual> children = createChildren(parents, populationSize, n);
      Collections.sort(children);

      // step 5
      // parentsとして取り出した 最初 n + 1 個と子供を入れ替える
      for (int i = 0; i < n + 1; ++i) {
        population.set(i, children.get(i));
      }

      // log
      if (evaluationCount % (10 * 5 * n) == 0) {
        noOfEvals = evaluationCount;
        bestEvaluationValue = getBestEvaluationValue(population);
        putLogData(log, trialName, trialNo, logIndex, noOfEvals, bestEvaluationValue);
        logIndex++;
      }

      // step 6
      if (isTerminated(population, evaluationCount)) {
        System.out.println(evaluationCount + "回の評価で終了");
        break;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    // step 1
    final int n = 20;
    final int populationSize = 14 * n;// 14n
    final int maxTrial = 3;
    final String trialName = "RexJggKTabletP14K5"; // 試行名
    final String logFileName = "data/" + trialName + ".csv"; // ログファイル名

    // log
    TCTable log = new TCTable(); // ログテーブル

    for (int trial = 0; trial < maxTrial; ++trial) {
      System.out.println("Trial:" + trial);
      executeOneTrial(populationSize, n, log, trialName, trial);
    }

    log.writeTo(logFileName); // 3試行分のログをファイルに出力．
  }
}
