package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * ガラスごとのデータを扱うクラス
 * 
 * @author Kenta Hirano, isao
 */
public class TGlass implements Serializable {

	private static final long serialVersionUID = 1L;

	/** ガラスのタイプ. 名前. */
	private String fType;

	/** 屈折率. 波長ごとの屈折率 */
	private double fN[];

	/** アッベ数 */
	private double fAbbe;

	/** 空気 */
	public static final TGlass AIR = new TGlass("Air", new double[] { 1.000000, 1.000000, 1.000000 }, 0.0);

	/**
	 * ガラスを作成する.
	 * 
	 * @param type ガラスのタイプ名
	 * @param n    波長ごとの屈折率
	 * @param abbe
	 */
	public TGlass(String type, double n[], double abbe) {
		fType = new String(type);
		fN = new double[TWavelength.NO_OF_WAVELENGTHS];
		for (int i = 0; i < TWavelength.NO_OF_WAVELENGTHS; ++i) {
			fN[i] = n[i];
		}
		fAbbe = abbe;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param br 入力ストリーム
	 * @throws IOException
	 */
	public TGlass(BufferedReader br) throws IOException {
		String[] tokens = br.readLine().split(" ");
		fN = new double[TWavelength.NO_OF_WAVELENGTHS];
		fType = tokens[0];
		fN[TWavelength.REF_D] = Double.parseDouble(tokens[1]);
		fAbbe = Double.parseDouble(tokens[2]);
		fN[TWavelength.REF_C] = Double.parseDouble(tokens[3]);
		fN[TWavelength.REF_G] = Double.parseDouble(tokens[4]);
	}

	/**
	 * ガラスが等しければ true を返す.
	 * 
	 * @param src 比較するガラス
	 */
	public final boolean isEqual(TGlass src) {
		return (fType.compareTo(src.fType) == 0);
	}

	/**
	 * ガラスが等しくなければ true を返す.
	 * 
	 * @param src 比較するガラス
	 */
	public final boolean isNotEqual(TGlass src) {
		return (fType.compareTo(src.fType) != 0);
	}

	@Override
	public String toString() {
		return fType + " " + fN[TWavelength.REF_D] + " " + fAbbe + " " + fN[TWavelength.REF_C] + " "
				+ fN[TWavelength.REF_G];
	}

	/**
	 * ファイルに出力する.
	 * 
	 * @param file 出力ストリーム
	 */
	public final void writeTo(PrintWriter pw) {
		pw.print(fType + " ");
		pw.print(fN[TWavelength.REF_D] + " ");
		pw.print(fAbbe + " ");
		pw.print(fN[TWavelength.REF_C] + " ");
		pw.println(fN[TWavelength.REF_G]);
	}

	/** タイプを返す. */
	public final String getType() {
		return fType;
	}

	/**
	 * 屈折率を返す
	 * 
	 * @param 波長を表す番号 (TWavelengthの定数)
	 */
	public final double getN(int wl) {
		return fN[wl];
	}

	/** アッベ数を返す */
	public final double getAbbe() {
		/* 未テスト */
		return fAbbe;
	}
}
