package rexjgg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ベクトルクラス
 *
 */
public class TVector {

	/** 実数の配列 */
	private double[] fData;

	/** 十分小さな値 */
	public static double EPS = 1e-10;

	/**
	 * コンストラクタ
	 */
	public TVector() {
		fData = new double[0];
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src
	 *              コピー元
	 */
	public TVector(TVector src) {
		fData = new double[src.fData.length];
		for (int i = 0; i < fData.length; ++i) {
			fData[i] = src.fData[i];
		}
	}

	/**
	 * 自分自身へコピーする．
	 * 
	 * @param src
	 *              コピー元
	 * @return 自分自身
	 */
	public TVector copyFrom(TVector src) {
		if (fData.length != src.fData.length) {
			fData = new double[src.fData.length];
		}
		for (int i = 0; i < fData.length; ++i) {
			fData[i] = src.fData[i];
		}
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
	 *             出力ストリーム
	 */
	public void writeTo(PrintWriter pw) {
		pw.println(fData.length);
		for (int i = 0; i < fData.length; ++i) {
			pw.print(fData[i] + " ");
		}
		pw.println();
	}

	/**
	 * ストリームから読み込む．
	 * 
	 * @param br
	 *             入力ストリーム
	 * @throws IOException
	 */
	public void readFrom(BufferedReader br) throws IOException {
		int dimension = Integer.parseInt(br.readLine());
		setDimension(dimension);
		String[] tokens = br.readLine().split(" ");
		for (int i = 0; i < dimension; ++i) {
			fData[i] = Double.parseDouble(tokens[i]);
		}
	}

	/**
	 * 次元数を設定する．
	 * 
	 * @param dimension
	 *                    次元数
	 */
	public void setDimension(int dimension) {
		if (fData.length != dimension) {
			fData = new double[dimension];
		}
	}

	/**
	 * 次元数を返す．
	 * 
	 * @return 次元数
	 */
	public int getDimension() {
		return fData.length;
	}

	/**
	 * 要素を返す．
	 * 
	 * @param index
	 *                インデックス
	 * @return 要素
	 */
	public double getElement(int index) {
		return fData[index];
	}

	/**
	 * 要素を設定する．
	 * 
	 * @param index
	 *                インデックス
	 * @param e
	 *                要素の値
	 */
	public void setElement(int index, double e) {
		fData[index] = e;
	}

	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < fData.length; ++i) {
			str += fData[i] + " ";
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
		assert fData.length == v.fData.length;
		for (int i = 0; i < fData.length; ++i) {
			if (Math.abs(fData[i] - v.fData[i]) > EPS) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ベクトルを足す．
	 * 
	 * @param v
	 *            ベクトル
	 * @return 自分自身
	 */
	public TVector add(TVector v) {
		assert fData.length == v.fData.length;
		for (int i = 0; i < fData.length; ++i) {
			fData[i] += v.fData[i];
		}
		return this;
	}

	/**
	 * ベクトルを引く．
	 * 
	 * @param v
	 *            ベクトル
	 * @return 自分自身
	 */
	public TVector subtract(TVector v) {
		assert fData.length == v.fData.length;
		for (int i = 0; i < fData.length; ++i) {
			fData[i] -= v.fData[i];
		}
		return this;
	}

	/**
	 * スカラー倍する．
	 * 
	 * @param a
	 *            スカラー値
	 * @return 自分自身
	 */
	public TVector scalarProduct(double a) {
		for (int i = 0; i < fData.length; ++i) {
			fData[i] *= a;
		}
		return this;

	}

	/**
	 * 要素ごとに割る．
	 * 
	 * @param v
	 *            ベクトル
	 * @return 自分自身
	 */
	public TVector elementwiseDevide(TVector v) {
		assert fData.length == v.fData.length;
		for (int i = 0; i < fData.length; ++i) {
			fData[i] /= v.fData[i];
		}
		return this;

	}

	/**
	 * 要素ごとに掛ける．
	 * 
	 * @param v
	 *            ベクトル
	 * @return 自分自身
	 */
	public TVector elementwiseProduct(TVector v) {
		assert fData.length == v.fData.length;
		for (int i = 0; i < fData.length; ++i) {
			fData[i] *= v.fData[i];
		}
		return this;

	}

	/**
	 * 内積値を返す．
	 * 
	 * @param v
	 *            ベクトル
	 * @return 内積値
	 */
	public double innerProduct(TVector v) {
		assert fData.length == v.fData.length;
		double sum = 0;
		for (int i = 0; i < fData.length; ++i) {
			sum += fData[i] * v.fData[i];
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
		for (int i = 0; i < fData.length; ++i) {
			sum += fData[i] * fData[i];
		}
		return Math.sqrt(sum);
	}

	/**
	 * 正規化する．
	 * 
	 * @return 自分自身
	 */
	public TVector Normalize() {
		this.scalarProduct(1 / this.calculateL2Norm());
		return this;
	}

}
