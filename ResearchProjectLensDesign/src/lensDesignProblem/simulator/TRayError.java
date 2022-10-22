package lensDesignProblem.simulator;

/**
 * 光線追跡時に起こるエラー<BR>
 * 
 * @author Kenta Hirano
 */
public class TRayError {

    /** エラーがないこと状態 */
    public static final int NO_ERROR = 0;

    /** レンズとの交点が無い */
    public static final int NO_INTERSECTION = 1;
    /** レンズとの交点が無い */
    public static final int NO_INTERSECTION2 = 2;
    /** レンズとの交点が無い */
    public static final int NO_INTERSECTION3 = 3;

    /** 光線が逆走することを示す */
    public static final int INVERSE_DIRECTION = 4;
    /** 光線が逆走することを示す */
    public static final int INVERSE_DIRECTION2 = 5;

    /** 光線が屈折せずに全反射してしまう */
    public static final int FULL_REFLECTION = 6;
    /** 光線が屈折せずに全反射してしまう */
    public static final int FULL_REFLECTION2 = 7;

    public static final int HEIGHT_OVERFLOW = 8;
    public static final int HEIGHT_UNDERFLOW = 9;
    public static final int W_MAX_RAY_OVERFLOW = 10;
    public static final int CANT_ENFORCE = 11;

    /** エラーに対するメッセージ */
    public final static String fMessage[] = { "No Error",
            "No Intersection",
            "No Intersection2",
            "No Intersection3",
            "Inverse Direction",
            "Inverse Direction2",
            "Full Reflection",
            "Full Reflection2",
            "Height Overflow",
            "Height Underflow",
            "Can't Enforce" };
}
