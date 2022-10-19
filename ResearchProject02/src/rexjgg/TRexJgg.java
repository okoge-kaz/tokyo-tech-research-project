package rexjgg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * REX/JGGのプログラム
 * @author isao
 *
 */
public class TRexJgg {

	/**
	 * ランダムな個体を１つ作る．
	 * @param dimension 次元数
	 * @param min 最小値
	 * @param max 最大値
	 * @param rand 乱数生成器
	 * @return ランダムな個体
	 */
	private static TIndividual makeRandomIndividual(int dimension, double min, double max, Random rand) {
		TIndividual ind = new TIndividual();
		TVector v = ind.getVector();
		v.setDimension(dimension);
		for (int i = 0;i < dimension; ++i) {
			double x = rand.nextDouble() * (max - min) + min;
			v.setElement(i, x);
		}
		return ind;
	}

	/**
	 * 初期集団を生成する．初期集団中の個体は評価済み．
	 * @param populationSize 集団サイズ
	 * @param dimension 次元数
	 * @param min 最小値
	 * @param max 最大値
	 * @param rand 乱数生成器
	 * @return 初期集団
	 */
	private static ArrayList<TIndividual> makeInitialPopulation(int populationSize, int dimension, double min, double max, Random rand) {
		ArrayList<TIndividual> population = new ArrayList<>();
		for (int i = 0; i < populationSize; ++i) {
			TIndividual ind = makeRandomIndividual(dimension, min, max, rand);
			double eval = ktablet(ind.getVector());
			ind.setEvaluationValue(eval);
			population.add(ind);
		}
		return population;
	}

	/**
	 * 目的関数(k-tablet関数)
	 * @param x ベクトル
	 * @return 評価値
	 */
	private static double ktablet(TVector x) {
		int n = x.getDimension();
		int k = n / 4;
		double sum = 0.0;
		for (int i = 0; i < k; i++) {
			sum += Math.pow(x.getElement(i), 2);
		}
		for (int i = k; i < n; ++i) {
			sum += Math.pow(100. * x.getElement(i), 2);
		}
		return sum;
	}

	/**
	 * ランダムに並び替えられたインデックスの配列(0～indexes.length-1の数字がランダムな順番で並んでいる）を返す．
	 * @param indexes インデックスの配列
	 * @param rand 乱数生成器
	 */
	private static void randomizeIndexes(int[] indexes, Random rand) {
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i;
		}
		for (int i = 0; i < indexes.length; ++i) {
			int targetIndex = rand.nextInt(indexes.length - i) + i;
			int tmp = indexes[targetIndex];
			indexes[targetIndex] = indexes[i];
			indexes[i] = tmp;
		}
	}

	/**
	 * 平均ベクトルを求める．
	 * @param vectors ベクトルの配列リスト
	 * @return 平均ベクトル
	 */
	private static TVector calculateMean(ArrayList<TVector> vectors) {
		int dimension = vectors.get(0).getDimension();
		TVector mean = new TVector();
		mean.setDimension(dimension);
		for (int i = 0; i < dimension; ++i) {
			mean.setElement(i, 0.0);
		}
		for (TVector p: vectors) {
			mean.add(p);
		}
		mean.scalarProduct(1.0 / (double)vectors.size());
		return mean;
	}

	/**
	 * REXにより１つの子個体を生成する．
	 * @param parentArray 親個体の配列リスト
	 * @return 子個体
	 */
	private static TIndividual makeOffspring(ArrayList<TIndividual> parentArray, Random rand) {
		double sigma = Math.sqrt(1.0 / (double)(parentArray.size() - 1));
		ArrayList<TVector> parentVectorArray = new ArrayList<>(); //親子体のベクトルの配列リスト
		for (TIndividual parent: parentArray) {
			parentVectorArray.add(parent.getVector());
		}
		int dimension = parentVectorArray.get(0).getDimension(); //次元数
		TVector mean = calculateMean(parentVectorArray); //親の平均ベクトル
		TIndividual offspring = new TIndividual(); //子個体
		TVector offspringVector = offspring.getVector(); //子個体のベクトル
		offspringVector.copyFrom(mean);
		for (int j = 0; j < dimension; ++j) {
			// parentVectors.get(j)が変更されないようにcloneを使用
			offspringVector.add(parentVectorArray.get(j).clone().subtract(mean).scalarProduct(rand.nextGaussian() * sigma));
		}
		return offspring;
	}

	/**
	 * 集団中の最良個体を返す．
	 * @param population 集団
	 * @return 最良個体
	 */
	private static TIndividual getBestIndividual(ArrayList<TIndividual> population) {
		TIndividual best = population.get(0);
		for (int i = 1; i < population.size(); ++i) {
			if (population.get(i).getEvaluationValue() < best.getEvaluationValue()) {
				best = population.get(i);
			}
		}
		return best;
	}

	/**
	 * 集団を表示する．デバッグ用．
	 * @param pop 集団
	 */
	private static void printPopulation(ArrayList<TIndividual> pop) {
		for (TIndividual ind: pop) {
			System.out.println(ind);
		}
	}

	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		double min = -5.0; //定義域の最小値
		double max = 5.0; //定義域の最大値
		int dimension = 20; //次元数
		int populationSize = 14 * dimension; //集団サイズ
		int noOfKids = 5 * dimension; //子個体生成数
		int maxEvals = 4 * dimension * 10000; //最大評価回数

		int noOfParents = dimension + 1; //親子体数
		Random rand = new Random(); //乱数生成器

		int evals = 0; //評価回数

		//Step 1:初期集団の生成
		ArrayList<TIndividual> population = makeInitialPopulation(populationSize, dimension, min, max, rand);
		evals += populationSize;

		while (true) {

			//Step 2：複製選択
			int[] parentIndexes = new int [populationSize]; //最初のn+1個だけ利用する
			randomizeIndexes(parentIndexes, rand);
			ArrayList<TIndividual> parentArray = new ArrayList<>();
			for (int i = 0; i < noOfParents; ++i) {
				parentArray.add(population.get(parentIndexes[i]));
			}

			//Step 3 & 4：子個体の生成と評価
			ArrayList<TIndividual> offspringArray = new ArrayList<>();
			for (int i = 0; i < noOfKids; ++i) {
				TIndividual offspring = makeOffspring(parentArray, rand);
				double eval = ktablet(offspring.getVector());
				offspring.setEvaluationValue(eval);
				++evals;
				offspringArray.add(offspring);
			}

			//Step 5：複製選択
			Collections.sort(offspringArray, Comparator.comparing(TIndividual::getEvaluationValue));
			for (int i = 0; i < noOfParents; ++i) {
				population.get(parentIndexes[i]).copyFrom(offspringArray.get(i));
			}

			//Step 6：終了判定
			TIndividual best = getBestIndividual(population);
			if (evals > maxEvals || best.getEvaluationValue() < 1.0e-7) {
				System.out.println(evals + " " + best.getEvaluationValue());
				break;
			}
		}
	}

}
