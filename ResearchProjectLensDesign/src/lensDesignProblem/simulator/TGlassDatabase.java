package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TGlassDatabase {

	/** ガラスの配列,1種類ごとにデータを格納 */
	private ArrayList<TGlass> fArray;

	/** 共通かつ唯一のデータベースインスタンスを差す */
	static TGlassDatabase uniqueInstance;

	/** 屈折率の最小 */
	public static final double ND_MIN = 1.4;

	/** 屈折率の最大 */
	public static final double ND_MAX = 2.1;

	/** アッベ数の最小 */
	public static final double ABBE_MIN = 20.0;

	/** アッベ数の最大 */
	public static final double ABBE_MAX = 100.0;

	/**
	 * コンストラクタ
	 */
	public TGlassDatabase() {
		fArray = new ArrayList<TGlass>();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param filename ガラスデータベースのファイル名
	 * @throws IOException
	 */
	public TGlassDatabase(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		readFrom(br);
		br.close();
	}

	/**
	 * ファイル入力
	 * 
	 * @param file 入力ストリーム
	 */
	public final void readFrom(BufferedReader file) throws IOException {
		int size = Integer.parseInt(file.readLine());
		fArray = new ArrayList<TGlass>();
		for (int i = 0; i < size; ++i) {
			TGlass glass = new TGlass(file);
			fArray.add(glass);
		}
	}

	/**
	 * ファイル出力する.
	 * 
	 * @param file 出力ストリーム
	 */
	public final void writeTo(PrintWriter pw) {
		pw.println(fArray.size());
		for (int i = 0; i < fArray.size(); ++i) {
			TGlass glass = (TGlass) fArray.get(i);
			glass.writeTo(pw);
		}
	}

	/**
	 * ガラスのデータを返す
	 * 
	 * @param index 要素番号
	 * @return ガラスのデータ
	 */
	public final TGlass getGlass(int index) {
		return (TGlass) fArray.get(index);
	}

	/**
	 * ガラスを加える
	 * 
	 * @param glass ガラス
	 */
	public void addGlass(TGlass glass) {
		fArray.add(glass);
	}

	/**
	 * ガラスを全て削除する．
	 *
	 */
	public void clearGlasses() {
		fArray.clear();
	}

	/**
	 * タイプによる検索
	 * 
	 * @param type タイプ
	 * @return 格納場所, みつからなかったときは -1
	 */
	public final int getIndex(String type) {
		/* 未テスト */
		for (int i = 0; i < fArray.size(); ++i) {
			if (type.compareTo(((TGlass) fArray.get(i)).getType()) == 0)
				return i;
		}
		System.err.println(type + " : No found!!");
		System.exit(5);
		return -1;
	}

	/**
	 * 最も近いものを探す.
	 * 
	 * @param nd   屈折率
	 * @param abbe アッベ数
	 */
	public final int getNearestNeighbor(double nd, double abbe) {
		int result = 0;
		double nd2 = getGlass(0).getN(TWavelength.REF_D);
		double abbe2 = getGlass(0).getAbbe();
		double min = this.calcDistance(nd, abbe, nd2, abbe2);
		double d;
		for (int i = 1; i < fArray.size(); ++i) {
			nd2 = getGlass(i).getN(TWavelength.REF_D);
			abbe2 = getGlass(i).getAbbe();
			d = this.calcDistance(nd, abbe, nd2, abbe2);
			if (d < min) {
				min = d;
				result = i;
			}
		}
		return result;
	}

	/**
	 * ガラスの数を返す.
	 * 
	 * @return ガラス数
	 */
	public final int getNoOfGlasses() {
		return fArray.size();
	}

	/**
	 * 妥当な範囲内か調べる.
	 * 
	 * @param nd   屈折率
	 * @param abbe アッベ数
	 */
	public final boolean inValidRegion(double nd, double abbe) {
		if (nd < ND_MIN || nd > ND_MAX) {
			return false;
		}
		if (abbe < ABBE_MIN || abbe > ABBE_MAX) {
			return false;
		}
		return true;
	}

	/**
	 * 距離を求める
	 * 
	 * @param nd1   屈折率
	 * @param abbe1 アッベ数
	 * @param nd2   屈折率
	 * @param abbe2 アッベ数
	 */
	private final double calcDistance(double nd1, double abbe1, double nd2, double abbe2) {
		double result;
		result = (100.0 * nd1 - 100.0 * nd2) * (100.0 * nd1 - 100.0 * nd2);
		result += (abbe1 - abbe2) * (abbe1 - abbe2);
		return Math.sqrt(result);
	}

	@Override
	public String toString() {
		String result = "";
		for (TGlass g : fArray) {
			result += g.toString();
			result += "\n";
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		TGlassDatabase glassDatabase = new TGlassDatabase("gauss-mod.glass");
		System.out.println(glassDatabase);
	}

}
