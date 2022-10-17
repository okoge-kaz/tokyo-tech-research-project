package rexjgg;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 個体クラス
 *
 */
public class TIndividual {

	/** 評価値 */
	private double fEvaluationvalue;

	/** ベクトル */
	private TVector fVector;

	/**
	 * コンストラクタ
	 */
	public TIndividual() {
		fEvaluationvalue = Double.NaN;
		fVector = new TVector();
	}

	/**
	 * コピーコンストラクタ
	 * @param src コピー元
	 */
	public TIndividual(TIndividual src) {
		fEvaluationvalue = src.fEvaluationvalue;
		fVector = new TVector(src.fVector);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TIndividual clone() {
		return new TIndividual(this);
	}

	/**
	 * コピーする．
	 * @param src コピー元
	 * @return 自分自身
	 */
	public TIndividual copyFrom(TIndividual src) {
		setEvaluationValue(src.getEvaluationValue());
		fVector.copyFrom(src.fVector);
		return this;
	}

	/**
	 * ストリームへ書き出す．
	 * @param pw 出力ストリーム
	 */
	public void writeTo(PrintWriter pw) {
		pw.println(fEvaluationvalue);
		fVector.writeTo(pw);
	}

	/**
	 * ストリームから読み込む
	 * @param br 入力ストリーム
	 * @throws IOException
	 */
	public void readFrom(BufferedReader br) throws IOException {
		fEvaluationvalue = Double.parseDouble(br.readLine());
		fVector.readFrom(br);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = fEvaluationvalue + "\n";
		str += fVector.toString();
		return str;
	}

	/**
	 * 評価値を返す．
	 * @return 評価値
	 */
	public double getEvaluationValue() {
		return fEvaluationvalue;
	}

	/**
	 * 評価値を設定する．
	 * @param eval 評価値
	 */
	public void setEvaluationValue(double eval) {
		fEvaluationvalue = eval;
	}

	/**
	 * ベクトルを返す．
	 * @return ベクトル
	 */
	public TVector getVector() {
		return fVector;
	}

	/**
	 * ベクトルを設定する．
	 * 
	 * @param vec
	 *              ベクトル
	 */
	public void setVector(TVector vec) {
		fVector = vec;
	}

}
