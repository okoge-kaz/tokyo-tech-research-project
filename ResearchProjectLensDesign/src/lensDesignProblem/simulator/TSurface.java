package lensDesignProblem.simulator;

import java.io.*;
import java.util.*;

/*
   
        No. of Surfaces : 3

         s0   s1    s2
         |    |     |       |
         r0   r1    r2
           d0    d1    d2
      nd0   nd1    nd2    nd3
     abbe0 abbe1  abbe2  abbe3
      gi0   gi1    gi2    gi3

*/

/** ガラスの曲面を扱うクラス <BR>
    @author Kenta Hirano */
public class TSurface implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** ポジション. x座標. 球曲面とx軸が交わる位置 */
	private double fPosition;
	/** 曲率半径 */
	private double fR;
	/** 高さ */
	private double fH;

	/** ガラスの曲面を作成. (データ未登録)     */
	public TSurface() {
		fPosition = fR = fH = -1.0;
	}

	/** ガラスの曲面を作成. 
	@param position ポジション
	@param r 曲率半径
	@param h 高さ     */
	public TSurface(double position, double r, double h) {
		fPosition = position;
		fR = r;
		fH = h;
	}

	/** コピーコンストラクタ. ガラスの曲面を作成. 
	    @param src コピー元    */
	public TSurface(final TSurface src) {
		fPosition = src.fPosition;
		fR = src.fR;
		fH = src.fH;
	}

	/** コピーする. 
	    @param src コピー元    */
	public final void copy(final TSurface src) {
		fPosition = src.fPosition;
		fR = src.fR;
		fH = src.fH;
	}

	/** ファイル読み込み
	    @param file 入力ストリーム  */
	public final void readFrom(BufferedReader file) throws IOException {
		try {
			/* 一行読みこんでスペースごとに区切る */
			StringTokenizer st = new StringTokenizer(file.readLine(), " ");
			String s;
			if (st.countTokens() != 2) {
				System.err.println("TLens readFrom: Read Error");
				System.exit(1);
			}
			s = st.nextToken();
			this.fR = Double.parseDouble(s);
			s = st.nextToken();
			this.fH = Double.parseDouble(s);

		} catch (IOException e) {
			System.out.println("TLens readForm: " + e);
			throw e;
		}
	}

	/** ファイル出力
	    @param file 出力ストリーム  */
	public final void writeTo(BufferedWriter file) throws IOException {
		try {
			/* データを書き込む */
			file.write(fR + " ");
			file.write(fH + " ");
			file.write("\n");
		} catch (IOException e) {
			System.out.println("TLens writeTo: " + e);
			throw e;
		}
	}

	/** ファイル出力
	    @param file 出力ストリーム  */
	public final void writeTo(PrintWriter pw) {
		pw.print(fR + " ");
		pw.println(fH + " ");
	}

	/** 標準出力へ出力     */
	public final void writeTo() {
		System.out.print(fR + " ");
		System.out.println(fH + " ");
	}

	/** ポジションを返す. 
	@return ポジション    */
	public final double getPosition() {
		return fPosition;
	}

	/** ポジションを代入する. 
	@param x ポジション     */
	public final void setPosition(double x) {
		fPosition = x;
	}

	/** 曲率半径を返す. 
	@return 曲率半径    */
	public final double getR() {
		return fR;
	}

	/** 曲率半径を代入する. 
	@param r 曲率半径     */
	public final void setR(double r) {
		fR = r;
	}

	/** 高さを返す. 
	@return 高さ     */
	public final double getHeight() {
		return fH;
	}

	/** 高さをセットする. 
	@param 高さ     */
	public final void setHeight(double h) {
		fH = h > Math.abs(fR) ? Math.abs(fR) : h;
	}

	/** 球曲面の中心座標を返す. 
	@return 中心座標     */
	public final double getCenter() {
		return fPosition + fR;
	}

	/** 球曲面がある角度をラジアンで返す. 
	@return 角度    */
	public final double getAngleByRadian() {
		if (Math.abs(fH) >= Math.abs(fR))
			return Math.PI / 2.0; /* 90度 */
		return Math.asin(Math.abs(fH) / Math.abs(fR));
	}

	/** 球曲面がある角度を度数法で返す. 
	@return 角度    */
	public final double getAngleByDegree() {
		if (Math.abs(fH) >= Math.abs(fR))
			return 90.0;
		return Math.asin(Math.abs(fH) / Math.abs(fR)) * 180.0 / Math.PI;
	}

	/** 球曲面のエッジのポジション(x座標)を返す.      */
	public final double getEdgePosition() {
		if (Math.abs(fH) >= Math.abs(fR))
			return fPosition + fR;
		double d = Math.sqrt(fR * fR - fH * fH);
		if (fR > 0)
			return fPosition + (Math.abs(fR) - d);
		else
			return fPosition - (Math.abs(fR) - d);
	}
}
