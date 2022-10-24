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
    assert (population.size() == populationSize);// check

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

  /*
   * 集団を更新するメソッド
   */
  private static void updatePopulation(ArrayList<TVector> population, ArrayList<TVector> children,
      TSMonochromeLensProblem problem) {
    // [0, n+1) が親個体である
    // 親 -> 子供 に更新する

    // 子供個体を今後使用することはないので破壊的にsortする
    Collections.sort(children);

    for (int i = 0; i < problem.getDimension() + 1; i++) {// 上位 n+1 個体で更新
      population.set(i, children.get(i).clone());// cloneしなくてもよい
    }
  }

  /*
   * 集団の中で最良の個体を返すメソッド
   */
  private static TVector getBestIndividual(ArrayList<TVector> population) {
    TVector bestIndividual = population.get(0);
    for (TVector individual : population) {
      if (individual.compareTo(bestIndividual) < 0) {
        bestIndividual = individual;
      }
    }
    return bestIndividual;
  }

  public static void main(String[] args) {
    // レンズ系設計問題のインスタンスを生成する
    TSMonochromeLensProblem problem = new TSMonochromeLensProblem("a g a g a g a", 3.0, 100.0, 19.0,
        0.0, 5.0, 0.0, 20.0, 10.0, 1000.0,
        1.0, 1.0); // 固定焦点単色レンズ設計問題を生成している．

    System.err.println("Dimension: " + problem.getDimension());

    final int n = problem.getDimension(); // 次元数
    final int populationSizeCandidates[] = { 10 * n, 14 * n, 15 * n, 20 * n, 30 * n };
    final int childrenSizeCandidates[] = { 2 * n, 3 * n, 4 * n, 5 * n, 6 * n, 7 * n, 8 * n, 9 * n, 10 * n, 15 * n };
    final int maxGeneration = 10000; // 最大世代数

    for (int populationSize : populationSizeCandidates) {
      for (int childSize : childrenSizeCandidates) {
        if (childSize > populationSize) {
          continue;
        }
        // 初期集団を生成する
        ArrayList<TVector> population = generateInitialPopulation(populationSize, problem);
        int generationCount = 0;
        for (; generationCount < maxGeneration; generationCount++) {
          // 集団から親を抽出する
          ArrayList<TVector> parents = selectParents(population, problem);
          // 親から子を生成する
          ArrayList<TVector> children = generateChildren(parents, problem, childSize);
          // 集団を更新する
          updatePopulation(population, children, problem);
          // 実行ログ
          if (generationCount % 100 == 0) {
            System.err.println(
                "Generation: " + generationCount + " Best : " + getBestIndividual(population).getEvaluationValue());
          }
        }
        System.out.println("PopulationSize: " + populationSize + " ChildSize: " + childSize + " Generation: "
            + generationCount + " Best Evaluation Score: " + getBestIndividual(population).getEvaluationValue());
      }
    }
  }
}
