package lensDesignProblem.simulator;

/** クラス<BR>
    @author Kenta Hirano */
public class TRayConstant{
    /** スポットダイアグラム(11本の光線の結ぶ像)の個数 */
    public static final int NO_OF_WS = 3;
    /** スポットダイアグラムの光線の角度その1 0度 */
    public static final int W_0 = 0;
    /** スポットダイアグラムの光線の角度その2 0.65*画角W 度 */
    public static final int W_065 = 1;
    /** スポットダイアグラムの光線の角度その3 画角W 度 */
    public static final int W_MAX = 2;

    /* 評価を行う光線の数 */
    public static final int NO_OF_RAYS = 11;
    public static final int NO_OF_RAYS_FOR_DISPLAY = 3;
}
