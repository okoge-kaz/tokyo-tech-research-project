package lensDesignProblem.simulator;

/**
 * オブジェクト付きDouble型ポインタクラス<BR>
 * 
 * @author Kenta Hirano
 */
public class TMyPtrDouble {
	/** doubleの値 */
	private double fValue;

	/** このクラスで実装するガーベジコレクションのサイズ */
	private static final int MAX_GARBAGE_SIZE = 1000;

	/** このクラスで実装するガベコレ用のオブジェクトの格納場所 */
	private static final TMyPtrDouble[] fGarbage = new TMyPtrDouble[MAX_GARBAGE_SIZE];

	/** このクラスで実装するガベコレのオブジェクトの現在の格納個数 */
	private static int fGarbageSize = 0;

	/** デフォルトコンストラクタ */
	private TMyPtrDouble() {
		fValue = 0.0;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param d 値
	 */
	private TMyPtrDouble(double d) {
		fValue = d;
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src コピー元
	 */
	private TMyPtrDouble(TMyPtrDouble src) {
		fValue = src.fValue;
	}

	/**
	 * インスタンスを返す. (再利用できなければ新規)
	 * コンストラクタの役割を果たす.
	 * 
	 * @return インスタンス
	 */
	public static TMyPtrDouble newInstance() {
		TMyPtrDouble p = null;
		if (fGarbageSize == 0) {
			p = new TMyPtrDouble();
		} else {
			--fGarbageSize;
			p = fGarbage[fGarbageSize];
			p.fValue = 0.0;
		}
		return p;
	}

	/**
	 * インスタンスを返す. (再利用できなければ新規)
	 * コンストラクタの役割を果たす.
	 * 
	 * @param d 値
	 * @return インスタンス
	 */
	public static TMyPtrDouble newInstance(double d) {
		TMyPtrDouble p = null;
		if (fGarbageSize == 0) {
			p = new TMyPtrDouble(d);
		} else {
			--fGarbageSize;
			p = fGarbage[fGarbageSize];
			p.fValue = d;
		}
		return p;
	}

	/**
	 * インスタンスを返す. (再利用できなければ新規)
	 * コピーコンストラクタの役割を果たす.
	 * 
	 * @param src コピー元
	 * @return インスタンス
	 */
	public static TMyPtrDouble newInstance(TMyPtrDouble src) {
		TMyPtrDouble p = null;
		if (fGarbageSize == 0) {
			p = new TMyPtrDouble(src);
		} else {
			--fGarbageSize;
			p = fGarbage[fGarbageSize];
			p.fValue = src.fValue;
		}
		return p;
	}

	/** 使わなくなったインスタンスの保持 */
	public static void deleteInstance(TMyPtrDouble p) {
		if (fGarbageSize < MAX_GARBAGE_SIZE) {
			fGarbage[fGarbageSize] = p;
			++fGarbageSize;
			p = null;
		} else {
			System.err.print("Warning:The garbage is ful");
			System.err.println("in TMyPtrDouble.deleteInstance");
		}
	}

	/**
	 * コピーする.
	 * 
	 * @param src コピー元
	 */
	public final void copy(final TMyPtrDouble src) {
		this.fValue = src.fValue;
	}

	/**
	 * 値を返す.
	 * 
	 * @return 値
	 */
	public final double getValue() {
		return this.fValue;
	}

	/**
	 * 代入する.
	 * 
	 * @param d 代入値
	 */
	public final void setValue(double d) {
		this.fValue = d;
	}
}
