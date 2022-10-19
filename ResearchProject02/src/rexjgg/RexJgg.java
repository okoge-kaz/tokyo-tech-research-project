package rexjgg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
  private static double benchmark(TIndividual individual, int k) {
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
  private static ArrayList<TIndividual> createInitialPopulation(int populationSize, int dimension) {
    ArrayList<TIndividual> population = new ArrayList<TIndividual>();

    double vectorMin = -5.0;
    double vectorMax = 5.0;

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
  private static ArrayList<TIndividual> selectParents(ArrayList<TIndividual> population, int n) {
    ArrayList<TIndividual> randomizedPopulation = new ArrayList<TIndividual>(population);
    for (int i = 0; i < n + 1; ++i) {
      int index = (int) (Math.random() * (randomizedPopulation.size() - 1));
      Collections.swap(randomizedPopulation, i, index);
    }
    ArrayList<TIndividual> parents = new ArrayList<TIndividual>(randomizedPopulation.subList(0, n + 1));
    // [0, n+1)の範囲を抽出
    return parents;
  }

  /*
   * 親個体の平均値を求める
   */
  private static TVector calcMeanVector(ArrayList<TIndividual> parents) {
    TVector meanVector = new TVector(parents.get(0).getVector().getDimension());
    for (int i = 0; i < parents.size(); ++i) {
      meanVector = meanVector.add(parents.get(i).getVector());
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
  private static TVector calcDiffVector(ArrayList<TIndividual> selectedParents, TVector meanVector, int n) {
    TVector diffVector = new TVector(selectedParents.get(0).getVector().getDimension());
    // 0初期化済み

    for (int i = 0; i < selectedParents.size(); ++i) {
      Random random = new Random();
      double sigma = random.nextGaussian() * Math.sqrt(1.0 / n);
      // nextGaussian()は標準正規分布に従う乱数を生成する
      // Returns the next pseudorandom, Gaussian ("normally") distributed double value with mean 0.0 and standard deviation 1.0 from this random number generator's sequence.
      // https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextGaussian--
      diffVector = diffVector.add(selectedParents.get(i).getVector().subtract(meanVector).scalarProduct(sigma));
    }
    return diffVector;
  }

  /*
   * 親個体から、 5n 個の子個体を生成する
   */
  private static ArrayList<TIndividual> createChildren(ArrayList<TIndividual> parents, int populationSize,
      int dimension) {
    // 手順3, 4
    ArrayList<TIndividual> children = new ArrayList<TIndividual>();

    for (int i = 0; i < 5 * dimension; ++i) {// 子個体の数は 5n (= 5 * dimension)
      // 親個体からランダムにn+1個体を非復元抽出する
      ArrayList<TIndividual> selectedParents = selectParents(parents, dimension);// dimension = n

      // 親個体の情報から子個体のベクトルを生成する
      TVector childVector = new TVector(dimension);
      // xi = <y> + sum(1, n+1, (yj - <y>))
      TVector meanVector = calcMeanVector(selectedParents);
      childVector = meanVector.add(calcDiffVector(selectedParents, meanVector, dimension));// dimension = n

      TIndividual child = new TIndividual(childVector);
      children.add(child);

      // 子個体の評価値を設定
      // 手順 4
      double evaluationValue = benchmark(child, dimension / 4);
      children.get(i).setEvaluationValue(evaluationValue);
    }

    return children;
  }

  /*
   * best evaluation value <= 1.0 * 10^(-7)
   */
  private static boolean isTerminated(ArrayList<TIndividual> population, Double bestScore) {
    // TODO ここの無駄な処理はどうにかしたい
    ArrayList<TIndividual> sortedPopulation = new ArrayList<TIndividual>(population);
    Collections.sort(sortedPopulation);

    // 評価値順になっているならばこれでよい
    double bestEvaluationValue = sortedPopulation.get(0).getEvaluationValue();
    bestScore = bestEvaluationValue;
    if (bestEvaluationValue <= 1.0 * Math.pow(10, -7)) {
      return true;
    }
    return false;
  }

  public static void main(String[] args) {
    System.out.println("Hello World!");

    // step 1
    int n = 20;
    int populationSize = 14 * n;// 14n
    ArrayList<TIndividual> population = createInitialPopulation(populationSize, n);

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
      Double bestScore = 0.0;

      // step 6
      if (isTerminated(population, bestScore)) {
        System.out.println("best score: " + bestScore);
        break;
      }
      if (evaluationCount % 10000 == 0) {
        System.out.println("Number Of Evaluation:" + evaluationCount + ", Best:" + bestScore.doubleValue()); // 画面に評価回数，最良評価値を表示．
      }
    }

  }
}
