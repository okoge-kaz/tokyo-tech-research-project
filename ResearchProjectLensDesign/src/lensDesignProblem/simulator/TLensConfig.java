package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.StringTokenizer;

/*
  memo int[] ではなくて String で実現できそう
  暇なとき, リファクタリングする
 */
/**
 * レンズ要素の並びを扱うクラス<BR>
 * 
 * @author Kenta Hirano
 */
public class TLensConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 空気を表す */
	public static final int AIR = 0;

	/** ガラスを表す */
	public static final int GLASS = 1;

	/** 曲面の数を表す */
	private int fNoOfSurfaces;

	/** 空気とガラスの並び */
	private int[] fArray;

	/** ガラス層の数 */
	private int fNoOfGlasses;

	/** レンズ要素の並びを作成する. (データ未登録) */
	public TLensConfig() {
		fNoOfSurfaces = 0;
		fArray = null;
		fNoOfGlasses = 0;
	}

	/**
	 * レンズ要素の並びを作成する. (引数データ)
	 * 
	 * @param src コピー元
	 */
	public TLensConfig(String config) {
		StringTokenizer st = new StringTokenizer(config, " ");
		fArray = new int[st.countTokens()];
		for (int i = 0; i < fArray.length; ++i) {
			String s = st.nextToken();
			if (s.equals("a") || s.equals("A")) {
				fArray[i] = AIR;
			} else if (s.equals("g") || s.equals("G")) {
				fArray[i] = GLASS;
				++fNoOfGlasses;
			} else {
				throw new RuntimeException("Error: Invalid Data in TLensConfig::TLensConfig");
			}
		}
		fNoOfSurfaces = fArray.length - 1;
		if (!isValidSequence()) {
			throw new RuntimeException("Error: Invalid Data in TLensConfig::TLensConfig");
		}
	}

	/**
	 * レンズ要素の並びを作成する. (コピーコンストラクタ)
	 * 
	 * @param src コピー元
	 */
	public TLensConfig(TLensConfig src) {
		fNoOfSurfaces = src.fNoOfSurfaces;
		fArray = new int[src.fArray.length];
		for (int i = 0; i < fArray.length; ++i)
			fArray[i] = src.fArray[i];
		fNoOfGlasses = src.fNoOfGlasses;
	}

	/**
	 * コピーする．
	 * 
	 * @param src コピー元
	 */
	public void copyFrom(TLensConfig src) {
		fNoOfSurfaces = src.fNoOfSurfaces;
		fArray = new int[src.fArray.length];
		for (int i = 0; i < fArray.length; ++i)
			fArray[i] = src.fArray[i];
		fNoOfGlasses = src.fNoOfGlasses;
	}

	/**
	 * ファイルから読み込む.
	 * 
	 * @param file 入力ストリーム
	 */
	public final void readFrom(BufferedReader file) throws IOException {
		try {
			int noOfSurfaces;
			noOfSurfaces = Integer.parseInt(file.readLine());
			fNoOfSurfaces = noOfSurfaces;
			fArray = new int[fNoOfSurfaces + 1];
			StringTokenizer st = new StringTokenizer(file.readLine(), " ");
			for (int i = 0; i < fArray.length; ++i) {
				String s = st.nextToken();
				if (s.equals("a") || s.equals("A")) {
					fArray[i] = AIR;
				} else if (s.equals("g") || s.equals("G")) {
					fArray[i] = GLASS;
					++fNoOfGlasses;
				} else {
					System.err.println(
							"Error: Invalid Data in TLensConfig::readFrom");
					System.exit(5);
				}
			}
		} catch (IOException e) {
			System.out.println("TLensConfig readFrom:" + e);
			throw e;
		}
		if (!isValidSequence()) {
			System.err.println("Error: Invalid Data in TLensConfig::readFrom");
			System.exit(5);
		}
	}

	/** 標準出力に出力する. */
	public final void writeTo() {
		System.out.println(fNoOfSurfaces);
		for (int i = 0; i < fArray.length; ++i) {
			if (fArray[i] == GLASS)
				System.out.print("g ");
			else
				System.out.print("a ");
		}
		System.out.println();
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < fArray.length; ++i) {
			if (fArray[i] == GLASS) {
				result += "g ";
			} else {
				result += "a ";
			}
		}
		return result;
	}

	/**
	 * ファイルに出力する.
	 * 
	 * @param file 出力ストリーム
	 */
	public final void writeTo(PrintWriter pw) {
		pw.println(fNoOfSurfaces);
		for (int i = 0; i < fArray.length; ++i) {
			if (fArray[i] == GLASS)
				pw.print("g ");
			else
				pw.print("a ");
		}
		pw.println();
	}

	/**
	 * ファイルに出力する.
	 * 
	 * @param file 出力ストリーム
	 */
	public final void writeTo(BufferedWriter file) throws IOException {
		try {
			file.write(fNoOfSurfaces + "\n");
			for (int i = 0; i < fArray.length; ++i) {
				if (fArray[i] == GLASS)
					file.write("g ");
				else
					file.write("a ");
			}
			file.write("\n");
		} catch (IOException e) {
			System.out.println("TLensConfig writeTo:" + e);
			throw e;
		}
	}

	/** 曲面の数を返す */
	public final int getNoOfSurfaces() {
		return fNoOfSurfaces;
	}

	/** AIR ならば true を返す */
	public final boolean isAir(int index) {
		if (fArray[index] == AIR)
			return true;
		else
			return false;
	}

	/** GLASS ならば true を返す */
	public final boolean isGlass(int index) {
		if (fArray[index] == GLASS)
			return true;
		else
			return false;
	}

	/**
	 * ガラス層の数を返す．
	 * 
	 * @return ガラス層
	 */
	public int getNoOfGlasses() {
		return fNoOfGlasses;
	}

	/** 配列が妥当かどうか(AIR層が続いていないか)調べる */
	private final boolean isValidSequence() {
		for (int i = 1; i < fArray.length; ++i) {
			if (fArray[i - 1] == 0 && fArray[i] == 0)
				return false;
		}
		return true;
	}
}
