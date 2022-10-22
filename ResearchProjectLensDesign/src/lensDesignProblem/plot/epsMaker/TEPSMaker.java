package lensDesignProblem.plot.epsMaker;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * EPSファイルを生成し，諸々の描画を行うクラス<br>
 *
 * 2012 uemura
 * ヘッダとトレイラをファイルで読み込むのをやめた．
 * また，そのほかいろいろ変更
 *
 * 2013 isao
 * ヘッダのバグ修正．座標系の変更．
 *
 * @author kinoshita, uemura, isao
 *
 */
public class TEPSMaker {
	PrintWriter fPrintWriter;

	private double fLineWidth = 1; // 線の太さ
	private int fLineCapStyle = 0; // 線の端の形状．0はカット，1は半円，2は正方形．setlinecapコマンド参照
	private int fFillArcMode = 1; // ArcPieSlice
	private double fGrayScale = 0.0; // 0.0は黒．1.0で白
	private String fFontName = "Helvetica";
	private int fStringOriginX = 0; // 文字列書き込み時のx座標原点．0:left, 1:center, 2:right
	private int fStringOriginY = 2; // 文字列書き込み時のy座標原点．0:bottom, 1:center, 2:top

	/**
	 * コンストラクタ
	 * ファイルfileNameをオープンして，headerを書き込む
	 *
	 * @param x1 BoundingBoxの左
	 * @param y1 BoundingBoxの上
	 * @param x2 BoundingBoxの右
	 * @param y2 BoundingBoxの下
	 */
	public TEPSMaker(String fileName, double x1, double y1, double x2, double y2) {
		try {
			fPrintWriter = new PrintWriter(new FileWriter(fileName + ".eps"), true);
			fPrintWriter.print(TEPSConstants.getHeader(fileName, x1, y1, x2, y2));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * trailerを書き込んで，ファイルを閉じる．
	 */
	public void close() {
		try {
			fPrintWriter.print(TEPSConstants.getTrailer());
			fPrintWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 座標(x,y)に点を打つ
	 *
	 * @param x
	 * @param y
	 */
	public void point(double x, double y) {
		fPrintWriter.print(x + " " + y + " ");
		fPrintWriter.print("1 1 0 360 ");
		fPrintWriter.print(fGrayScale + " " + fFillArcMode + " ");
		fPrintWriter.print("myfillarc");
		fPrintWriter.println();
	}

	/**
	 * 座標(x1,y1)から(x2,y2)に直線を引く
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void line(double x1, double y1, double x2, double y2) {
		fPrintWriter.print(x1 + " " + y1 + " " + x2 + " " + y2 + " ");
		fPrintWriter.print(fLineWidth + " " + fLineCapStyle + " ");
		fPrintWriter.print("[] 0 "); // 破線に関する設定．破線にしない．
		fPrintWriter.print(fGrayScale + " ");
		fPrintWriter.print("myline");
		fPrintWriter.println();
	}

	/**
	 * 長方形を描く
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void frameRectangle(double x1, double y1, double x2, double y2) {
		fPrintWriter.print(x1 + " " + y1 + " " + x2 + " " + y2 + " ");
		fPrintWriter.print(fLineWidth + " " + fLineCapStyle + " ");
		fPrintWriter.print("[] 0 ");
		fPrintWriter.print(fGrayScale + " ");
		fPrintWriter.print("myframerect");
		fPrintWriter.println();
	}

	/**
	 * 長方形を塗りつぶす
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void fillRectangle(double x1, double y1, double x2, double y2) {
		fPrintWriter.print(x1 + " " + y1 + " " + x2 + " " + y2 + " ");
		fPrintWriter.print(fGrayScale + " ");
		fPrintWriter.print("myfillrect");
		fPrintWriter.println();
	}

	/**
	 * 弧を描く<br>
	 * (x1,y1)-(x2,y2)で指定される矩形に内接する楕円のうち，
	 * startAngle(degree)からdeltaAngle(degree)だけ弧を描く．
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param startAngle
	 * @param deltaAngle
	 */
	public void frameArc(double x1, double y1, double x2, double y2, double startAngle, double deltaAngle) {
		double x = (x1 + x2) / 2.0;
		double y = (y1 + y2) / 2.0;
		double rx = Math.abs(x1 - x2) / 2.0;
		double ry = Math.abs(y1 - y2) / 2.0;
		double endAngle = startAngle + deltaAngle;
		if (deltaAngle < 0.0) {
			double tmp = endAngle;
			endAngle = startAngle;
			startAngle = tmp;
		}
		fPrintWriter.print(x + " " + y + " " + rx + " " + ry + " ");
		fPrintWriter.print(startAngle + " " + endAngle + " ");
		fPrintWriter.print(fLineWidth + " " + fLineCapStyle + " ");
		fPrintWriter.print("[] 0 ");
		fPrintWriter.print(fGrayScale + " ");
		fPrintWriter.print("myframearc");
		fPrintWriter.println();
	}

	/**
	 * 楕円を描く
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void frameOval(double x1, double y1, double x2, double y2) {
		frameArc(x1, y1, x2, y2, 0, 360);
	}

	/**
	 * 文字列(1バイト文字)を書き込む
	 *
	 * @param str 文字列．2バイト文字は禁止です．
	 * @param x   基点x座標
	 * @param y   基点y座標
	 */
	public void string(String str, double x, double y, int size) {
		fPrintWriter.print("(" + str + ") ");
		fPrintWriter.print(x + " " + y + " ");
		fPrintWriter.print(fStringOriginX + " " + fStringOriginY + " ");
		fPrintWriter.print(fGrayScale + " ");
		fPrintWriter.print(size + " (" + fFontName + ") ");
		fPrintWriter.print("mystring");
		fPrintWriter.println();
	}

	// /**
	// * 直接EPSファイルに文字列を書き込む．
	// * コメントとか，一時的な利用を推奨．
	// * 変な記述をするとEPSが壊れるので注意．
	// * @param s
	// */
	// public void println(String s) {
	// fPrintWriter.println(s);
	// }

	/**
	 * EPSファイルに1行コメントを書き込む．
	 * 1バイト文字にすること．
	 * また，改行文字は含まないこと．
	 *
	 * @param s
	 */
	public void comment(String s) {
		String s1 = s.replaceAll("\\r", "");
		s = s1.replaceAll("\\n", "");
		fPrintWriter.print("%" + s);
		fPrintWriter.println();
	}

	public void setLineWidth(double lw) {
		fLineWidth = lw;
	}

	public static void main(String[] args) {
		TEPSMaker epsMaker = new TEPSMaker("test/hoge", 0, 0, 120, 200);
		epsMaker.point(0, 0);
		epsMaker.line(10, 10, 20, 20);
		epsMaker.frameRectangle(0, 100, 50, 200);
		epsMaker.fillRectangle(25, 10, 35, 20);
		epsMaker.frameArc(0, 100, 50, 200, 0, 180);
		epsMaker.frameOval(100, 0, 120, 20);
		epsMaker.frameRectangle(0, 0, 100, 100);
		epsMaker.string("hogehoge", 0, 0, 10);
		epsMaker.close();
	}

}
