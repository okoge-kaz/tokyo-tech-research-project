package lensDesignProblem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import lensDesignProblem.evaluator.TSMonochromeLensProblem;

/**
 * ベクトルクラス
 *
 */
public class TVector implements Comparable<TVector> {

  /** 実数の配列 */
  private double[] vector;
  private double evaluationValue;

  /** 十分小さな値 */
  public static double EPS = 1e-10;

  /**
   * コンストラクタ
   */
  public TVector() {
    this.vector = new double[0];
  }

  /**
   * コピーコンストラクタ
   * 
   * @param src
   *            コピー元
   */
  public TVector(TVector src) {
    vector = new double[src.vector.length];
    for (int i = 0; i < vector.length; ++i) {
      vector[i] = src.vector[i];
    }
    this.evaluationValue = src.evaluationValue;
  }

  /**
   * copy constructor
   * 
   * @param array
   */
  public TVector(ArrayList<Double> array, double evalValue) {
    vector = new double[array.size()];
    for (int i = 0; i < vector.length; ++i) {
      vector[i] = array.get(i);
    }
    this.evaluationValue = evalValue;
  }

  /*
   * copy constructor
   */
  public TVector(double[] array, double evalValue) {
    vector = new double[array.length];
    for (int i = 0; i < vector.length; ++i) {
      vector[i] = array[i];
    }
    this.evaluationValue = evalValue;
  }

  /**
   * コンストラクタ
   * 
   * @param size
   *             サイズ
   */
  public TVector(int size) {
    vector = new double[size];
  }

  /**
   * 自分自身へコピーする．
   * 
   * @param src
   *            コピー元
   * @return 自分自身
   */
  public TVector copyFrom(TVector src) {
    if (vector.length != src.vector.length) {
      vector = new double[src.vector.length];
    }
    for (int i = 0; i < vector.length; ++i) {
      vector[i] = src.vector[i];
    }
    this.evaluationValue = src.evaluationValue;
    return this;
  }

  /*
   * (非 Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public TVector clone() {
    return new TVector(this);
  }

  /**
   * ストリームへ書き出す．
   * 
   * @param pw
   *           出力ストリーム
   */
  public void writeTo(PrintWriter pw) {
    pw.println(vector.length);
    for (int i = 0; i < vector.length; ++i) {
      pw.print(vector[i] + " ");
    }
    pw.println();
  }

  /**
   * ストリームから読み込む．
   * 
   * @param br
   *           入力ストリーム
   * @throws IOException
   */
  public void readFrom(BufferedReader br) throws IOException {
    int dimension = Integer.parseInt(br.readLine());
    setDimension(dimension);
    String[] tokens = br.readLine().split(" ");
    for (int i = 0; i < dimension; ++i) {
      vector[i] = Double.parseDouble(tokens[i]);
    }
  }

  /**
   * 次元数を設定する．
   * 
   * @param dimension
   *                  次元数
   */
  public void setDimension(int dimension) {
    if (vector.length != dimension) {
      vector = new double[dimension];
    }
  }

  /*
   * vectorを返す
   */
  public double[] getVector() {
    return vector;
  }

  /**
   * 次元数を返す．
   * 
   * @return 次元数
   */
  public int getDimension() {
    return vector.length;
  }

  /**
   * 要素を返す．
   * 
   * @param index
   *              インデックス
   * @return 要素
   */
  public double getElement(int index) {
    return vector[index];
  }

  /**
   * 要素を設定する．
   * 
   * @param index
   *              インデックス
   * @param e
   *              要素の値
   */
  public void setElement(int index, double e) {
    vector[index] = e;
  }

  @Override
  public String toString() {
    String str = "";
    for (int i = 0; i < vector.length; ++i) {
      str += vector[i] + " ";
    }
    ;
    return str;
  }

  /*
   * (非 Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    TVector v = (TVector) o;
    assert vector.length == v.vector.length;
    for (int i = 0; i < vector.length; ++i) {
      if (Math.abs(vector[i] - v.vector[i]) > EPS) {
        return false;
      }
    }
    return true;
  }

  /**
   * ベクトルを足す．
   * 
   * @param v
   *          ベクトル
   * @return 自分自身
   */
  public TVector add(TVector v, TSMonochromeLensProblem problem) {
    assert vector.length == v.vector.length;
    for (int i = 0; i < vector.length; ++i) {
      vector[i] += v.vector[i];
    }
    this.evaluationValue = problem.evaluate(this.getVector());
    return this;
  }

  public TVector add(double[] v, TSMonochromeLensProblem problem) {
    assert vector.length == v.length;
    for (int i = 0; i < vector.length; ++i) {
      vector[i] += v[i];
    }
    this.evaluationValue = problem.evaluate(this.getVector());
    return this;
  }

  /**
   * ベクトルを引く．
   * 
   * @param v
   *          ベクトル
   * @return 自分自身
   */
  public TVector subtract(TVector v, TSMonochromeLensProblem problem) {
    assert vector.length == v.vector.length;
    for (int i = 0; i < vector.length; ++i) {
      vector[i] -= v.vector[i];
    }
    this.evaluationValue = problem.evaluate(this.getVector());
    return this;
  }

  public TVector subtract(double[] v, TSMonochromeLensProblem problem) {
    assert vector.length == v.length;
    for (int i = 0; i < vector.length; ++i) {
      vector[i] -= v[i];
    }
    this.evaluationValue = problem.evaluate(this.getVector());
    return this;
  }

  /**
   * スカラー倍する．
   * 
   * @param a
   *          スカラー値
   * @return 自分自身
   */
  public TVector scalarProduct(double a, TSMonochromeLensProblem problem) {
    for (int i = 0; i < vector.length; ++i) {
      vector[i] *= a;
    }
    this.evaluationValue = problem.evaluate(this.getVector());
    return this;
  }

  /**
   * 要素ごとに割る．
   * 
   * @param v
   *          ベクトル
   * @return 自分自身
   */
  public TVector elementWiseDevide(TVector v) {
    assert vector.length == v.vector.length;
    for (int i = 0; i < vector.length; ++i) {
      vector[i] /= v.vector[i];
    }
    return this;

  }

  /**
   * 要素ごとに掛ける．
   * 
   * @param v
   *          ベクトル
   * @return 自分自身
   */
  public TVector elementWiseProduct(TVector v) {
    assert vector.length == v.vector.length;
    for (int i = 0; i < vector.length; ++i) {
      vector[i] *= v.vector[i];
    }
    return this;

  }

  /**
   * 内積値を返す．
   * 
   * @param v
   *          ベクトル
   * @return 内積値
   */
  public double innerProduct(TVector v) {
    assert vector.length == v.vector.length;
    double sum = 0;
    for (int i = 0; i < vector.length; ++i) {
      sum += vector[i] * v.vector[i];
    }
    return sum;
  }

  /**
   * L2ノルムを返す．
   * 
   * @return L2ノルム
   */
  public double calculateL2Norm() {
    double sum = 0;
    for (int i = 0; i < vector.length; ++i) {
      sum += vector[i] * vector[i];
    }
    return Math.sqrt(sum);
  }

  /**
   * 正規化する．
   * 
   * @return 自分自身
   */
  public TVector Normalize(TSMonochromeLensProblem problem) {
    this.scalarProduct(1 / this.calculateL2Norm(), problem);
    return this;
  }

  /**
   * 評価値をセットする
   */
  public void setEvaluationValue(double evaluation) {
    this.evaluationValue = evaluation;
  }

  /**
   * 評価値を返す
   */
  public double getEvaluationValue() {
    return evaluationValue;
  }

  @Override
  public int compareTo(TVector o) {
    if (this.evaluationValue < o.evaluationValue) {
      return -1;
    } else if (this.evaluationValue > o.evaluationValue) {
      return 1;
    } else {
      return 0;
    }
  }
}
