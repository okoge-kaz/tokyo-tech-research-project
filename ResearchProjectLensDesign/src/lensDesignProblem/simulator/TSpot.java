package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * スポット(光線が当たってできる斑点)クラス<BR>
 * 
 * @author Kenta Hirano
 */
public class TSpot {
	/** 光線の数 */
	private int fNoOfRays;
	/** 斑点の座標 */
	private TVector2D[] fArray;

	/** スポットを作成する. */
	public TSpot() {
		fNoOfRays = 1;
		fArray = new TVector2D[fNoOfRays];
		for (int i = 0; i < fNoOfRays; ++i) {
			fArray[i] = TVector2D.newInstance();
		}
	}

	/**
	 * スポットを作成する.
	 * 
	 * @param noOfRays 光線の数
	 */
	public TSpot(int noOfRays) {
		fNoOfRays = noOfRays;
		fArray = new TVector2D[fNoOfRays];
		for (int i = 0; i < fNoOfRays; ++i) {
			fArray[i] = TVector2D.newInstance();
		}
	}

	/**
	 * コピーコンストラクタ. スポットを作成する.
	 * 
	 * @param src コピー元
	 */
	public TSpot(final TSpot src) {
		this.fNoOfRays = src.fNoOfRays;
		fArray = new TVector2D[fNoOfRays];
		for (int i = 0; i < fNoOfRays; ++i) {
			fArray[i] = TVector2D.newInstance();
		}
		for (int i = 0; i < fNoOfRays; ++i)
			this.setVector2D(i, src.fArray[i]);
	}

	/**
	 * コピーする.
	 * 
	 * @param src コピー元
	 */
	public final void copy(final TSpot src) {
		this.setNoOfRays(src.fNoOfRays);
		for (int i = 0; i < fNoOfRays; ++i)
			this.setVector2D(i, src.fArray[i]);
	}

	/**
	 * 斑点の座標を返す.
	 * 
	 * @param index 要素番号
	 * @return 斑点の座標
	 */
	public final TVector2D getVector2D(int index) {
		return fArray[index];
	}

	/**
	 * 斑点の座標を設定する.
	 * 
	 * @param index 要素番号
	 * @param src   斑点の座標
	 */
	public final void setVector2D(int index, TVector2D src) {
		fArray[index].copy(src);
	}

	/** 光線の数を返す. */
	public final int getNoOfRays() {
		return fNoOfRays;
	}

	/**
	 * 光線の数を設定する.
	 * もとのデータは消える.
	 * 
	 * @param 光線の数
	 */
	public final void setNoOfRays(int noOfRays) {
		if (fNoOfRays == noOfRays)
			return;
		fNoOfRays = noOfRays;
		fArray = new TVector2D[fNoOfRays];
		for (int i = 0; i < fNoOfRays; ++i) {
			fArray[i] = TVector2D.newInstance();
		}
	}

	/** 標準出力に出力する. */
	public final void writeTo() {
		System.out.println(fNoOfRays);
		for (int i = 0; i < fNoOfRays; ++i) {
			System.out.print(fArray[i].getData(0) + " ");
			System.out.println(fArray[i].getData(1));
		}
	}

	/**
	 * ファイルに出力する.
	 * 
	 * @param file 出力ストリーム
	 */
	public final void writeTo(PrintWriter pw) {
		pw.println(fNoOfRays);
		for (int i = 0; i < fNoOfRays; ++i) {
			pw.print(fArray[i].getData(0) + " ");
			pw.println(fArray[i].getData(1));
		}
	}

	/**
	 * ファイルに出力する.
	 * 
	 * @param file 出力ストリーム
	 */
	public final void writeTo(BufferedWriter file) throws IOException {
		try {
			file.write(fNoOfRays + "\n");
			for (int i = 0; i < fNoOfRays; ++i) {
				file.write(fArray[i].getData(0) + " ");
				file.write(fArray[i].getData(1) + "\n");
			}
		} catch (IOException e) {
			System.out.println("TSpot writeTo: " + e);
			throw e;
		}
	}

	/**
	 * ファイルから読み込む.
	 * 
	 * @param file 入力ストリーム
	 */
	public final void readFrom(BufferedReader file) throws IOException {
		try {
			String s;
			int noOfRays;
			s = file.readLine();
			noOfRays = Integer.parseInt(s);
			this.setNoOfRays(noOfRays);

			StringTokenizer st;
			TVector2D v2d = TVector2D.newInstance();
			for (int i = 0; i < fNoOfRays; ++i) {
				st = new StringTokenizer(file.readLine(), " ");
				if (st.countTokens() != 2) {
					System.out.println("TSpot readFrom :Read Error");
					System.exit(1);
				}
				s = st.nextToken();
				v2d.setData(0, Double.parseDouble(s));
				s = st.nextToken();
				v2d.setData(1, Double.parseDouble(s));

				this.setVector2D(i, v2d);
			}
		} catch (IOException e) {
			System.out.println("TSpot readFrom: " + e);
			throw e;
		}
	}
}
