package lensDesignProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import lensDesignProblem.evaluator.TSMonochromeLensProblem;

public class RexJgg {
  /*
   * レンズ系設計問題を REX/JGG で解く
   * ResearchProject02 のコードを参考にしている。
   */

  /*
   * 実行可能な TSMonochromeLensProblem が 所定の個数存在するか判定する
   */
  private static boolean isEnoughSize(ArrayList<TVector> population, int size) {
    return population.size() >= size;
  }

  /*
   * TSMonochromeLensProblem が実行可能かを判定する
   */
  private static boolean isRunnable(TSMonochromeLensProblem problem, TVector individual) {
    double evaluationValue = problem.evaluate(individual.getVector());

    assert (evaluationValue == individual.getEvaluationValue());

    if (evaluationValue == Double.MAX_VALUE) {
      return false;
    }
    return true;
  }

  /*
   * 平均ベクトルを計算する
   * <y> = (1/n) * Σy
   */
  private static double[] calcAverageVector(final ArrayList<TVector> parents) {
    final int dimension = parents.get(0).getVector().length;
    double[] averageVector = new double[dimension];

    for (TVector individual : parents) {
      for (int i = 0; i < dimension; i++) {
        averageVector[i] += individual.getElement(i);
      }
    }
    for (int i = 0; i < dimension; i++) {
      averageVector[i] /= parents.size();
    }

    return averageVector;
  }

  /*
   * TSMonochromeLensProblem の次元に合わせた vector を作成する
   * 実行可能かどうかは判定していない
   */
  private static TVector generateIndividual(Random random, TSMonochromeLensProblem problem) {
    TVector individual = new TVector(problem.getDimension());
    for (int i = 0; i < individual.getDimension(); i++) {
      individual.setElement(i, random.nextDouble() * (TSMonochromeLensProblem.MAX - TSMonochromeLensProblem.MIN)
          + TSMonochromeLensProblem.MIN);
    }
    individual.setEvaluationValue(problem.evaluate(individual.getVector()));
    return individual;
  }

  /*
   * 初期集団を生成するメソッド
   * populationSize 個の 実行可能な解を生成する。
   */
  private static ArrayList<TVector> generateInitialPopulation(int populationSize, TSMonochromeLensProblem problem) {
    ArrayList<TVector> population = new ArrayList<TVector>();
    Random random = new Random();

    // 要求されている 初期集団 の個数に達するまでループ
    while (true) {
      // 生成
      TVector individual = generateIndividual(random, problem);
      // 実行可能かどうかを判定
      if (isRunnable(problem, individual)) {
        // 実行可能ならば population に追加
        population.add(individual);
      }
      // 終了判定
      if (isEnoughSize(population, populationSize)) {
        break;
      }
    }

    return population;
  }

  /*
   * 集団から親を抽出するメソッド
   * 集団の順番自体も破壊的に入れ替える
   */
  private static ArrayList<TVector> selectParents(ArrayList<TVector> population, TSMonochromeLensProblem problem) {
    Random random = new Random();

    final int dimension = problem.getDimension();
    for (int i = 0; i < dimension + 1; i++) {
      int index = random.nextInt(population.size());
      Collections.swap(population, i, index);
    }
    ArrayList<TVector> parents = new ArrayList<TVector>(population.subList(0, dimension + 1));
    return parents;
  }

  /*
   * 親から子を生成するメソッド
   */
  private static ArrayList<TVector> generateChildren(ArrayList<TVector> parents, TSMonochromeLensProblem problem,
      final int childrenSize) {
    Random random = new Random();
    final int n = problem.getDimension();

    ArrayList<TVector> children = new ArrayList<TVector>();
    for (int i = 0; i < childrenSize; i++) {
      TVector child = new TVector(n);
      // 計算処理 start
      double[] averageVector = calcAverageVector(parents);
      child.add(averageVector, problem);// <y>
      for (int j = 0; j < n + 1; j++) {
        double sigma = random.nextGaussian() * Math.sqrt(1.0 / n);
        child = child.add((parents.get(j).subtract(averageVector, problem)).scalarProduct(sigma, problem), problem);
        // <y> + (Σ(y - <y>) * sigma)

        // assert check
        assert (child.getDimension() == n);
        assert (child.getVector().length == n);
        assert (child.getEvaluationValue() == problem.evaluate(child.getVector()));
      }
      // end
      children.add(child);
    }
    assert (children.size() == childrenSize);
    return children;
  }

  public static void main(String[] args) {
    // レンズ系設計問題のインスタンスを生成する
    TSMonochromeLensProblem problem = source.clone();
    // レンズ系設計問題を解く
    problem.solve();
    // レンズ系設計問題の解を表示する
    problem.showSolution();
  }
}
