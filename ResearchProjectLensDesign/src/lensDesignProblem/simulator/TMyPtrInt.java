package lensDesignProblem.simulator;

/**
 * オブジェクト付きint型ポインタクラス<BR>
 * 
 * @author Kenta Hirano
 */
public class TMyPtrInt {
	/** intの値 */
	private int fValue;

	/** このクラスで実装するガーベジコレクションのサイズ */
	private static final int MAX_GARBAGE_SIZE = 1000;

	/** このクラスで実装するガベコレ用のオブジェクトの格納場所 */
	private static final TMyPtrInt[] fGarbage = new TMyPtrInt[MAX_GARBAGE_SIZE];

	/** このクラスで実装するガベコレのオブジェクトの現在の格納個数 */
	private static int fGarbageSize = 0;

	/** デフォルトコンストラクタ */
	private TMyPtrInt() {
		fValue = 0;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param i 値
	 */
	private TMyPtrInt(int i) {
		fValue = i;
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src コピー元
	 */
	private TMyPtrInt(TMyPtrInt src) {
		fValue = src.fValue;
	}

	/**
	 * インスタンスを返す. (再利用できなければ新規)
	 * コンストラクタの役割を果たす.
	 * 
	 * @return インスタンス
	 */
	public static TMyPtrInt newInstance() {
		TMyPtrInt p = null;
		if (fGarbageSize == 0) {
			p = new TMyPtrInt();
		} else {
			--fGarbageSize;
			p = fGarbage[fGarbageSize];
			p.fValue = 0;
		}
		return p;
	}

	/**
	 * インスタンスを返す. (再利用できなければ新規)
	 * コンストラクタの役割を果たす.
	 * 
	 * @param i 値
	 * @return インスタンス
	 */
	public static TMyPtrInt newInstance(int i) {
		TMyPtrInt p = null;
		if (fGarbageSize == 0) {
			p = new TMyPtrInt(i);
		} else {
			--fGarbageSize;
			p = fGarbage[fGarbageSize];
			p.fValue = i;
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
	public static TMyPtrInt newInstance(TMyPtrInt src) {
		TMyPtrInt p = null;
		if (fGarbageSize == 0) {
			p = new TMyPtrInt(src);
		} else {
			--fGarbageSize;
			p = fGarbage[fGarbageSize];
			p.fValue = src.fValue;
		}
		return p;
	}

	/** 使わなくなったインスタンスの保持 */
	public static void deleteInstance(TMyPtrInt p) {
		if (fGarbageSize < MAX_GARBAGE_SIZE) {
			fGarbage[fGarbageSize] = p;
			++fGarbageSize;
			p = null;
		} else {
			System.err.print("Warning:The garbage is ful");
			System.err.println("in TMyPtrInt.deleteInstance");
		}
	}

	/**
	 * コピーする.
	 * 
	 * @param src コピー元
	 */
	public final void copy(final TMyPtrInt src) {
		this.fValue = src.fValue;
	}

	/**
	 * 値を返す.
	 * 
	 * @return 値
	 */
	public final int getValue() {
		return this.fValue;
	}

	/**
	 * 代入する.
	 * 
	 * @param i 代入値
	 */
	public final void setValue(int i) {
		this.fValue = i;
	}
}
