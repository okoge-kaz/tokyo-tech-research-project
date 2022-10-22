package lensDesignProblem.simulator;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * 光線追跡のエラーを扱うクラス<BR>
 * 
 * @author Kenta Hirano
 */
public class TRayTraceError {
    private int fRayNo;
    private int fSurfaceNo;
    private int fErrorNo;
    private double fHeight;

    public TRayTraceError() {
        fRayNo = -1;
        fSurfaceNo = -1;
        fErrorNo = TRayError.NO_ERROR;
        fHeight = 0.0;
    }

    public TRayTraceError(int rayNo, int surfaceNo, int errorNo,
            double height) {
        fRayNo = rayNo;
        fSurfaceNo = surfaceNo;
        fErrorNo = errorNo;
        fHeight = height;
    }

    /* コピーコンストラクタ */
    public TRayTraceError(TRayTraceError src) {
        fRayNo = src.fRayNo;
        fSurfaceNo = src.fSurfaceNo;
        fErrorNo = src.fErrorNo;
        fHeight = src.fHeight;
    }

    /* コピー */
    public final void copy(TRayTraceError src) {
        this.fRayNo = src.fRayNo;
        this.fSurfaceNo = src.fSurfaceNo;
        this.fErrorNo = src.fErrorNo;
        this.fHeight = src.fHeight;
    }

    public final int getRayNo() {
        return fRayNo;
    }

    public final void setRayNo(int no) {
        fRayNo = no;
    }

    public final int getSurfaceNo() {
        return fSurfaceNo;
    }

    public final void setSurfaceNo(int no) {
        fSurfaceNo = no;
    }

    public final int getErrorNo() {
        return fErrorNo;
    }

    public final void setErrorNo(int no) {
        fErrorNo = no;
    }

    public final double getRayHeight() {
        return fHeight;
    }

    public final void setRayHeight(double h) {
        fHeight = h;
    }

    /** エラーメッセージを返す */
    public final String getErrorMessage() {
        return TRayError.fMessage[fErrorNo];
    }

    /**
     * 標準出力に出力する.
     * 一行に3個の要素を表示.
     */
    public final void writeTo() {
        System.out.print("Ray:" + fRayNo + ", ");
        System.out.print("Surface:" + fSurfaceNo + ", ");
        System.out.print(TRayError.fMessage[fErrorNo] + ", ");
        System.out.println("h=" + fHeight);
    }

    /**
     * ファイルに出力する.
     * 
     * @param file 出力ストリーム
     */
    public final void writeTo(BufferedWriter file) throws IOException {
        try {
            /* データを書き込む */
            file.write("Ray:" + fRayNo + ", ");
            file.write("Surface:" + fSurfaceNo + ", ");
            file.write(TRayError.fMessage[fErrorNo] + ", ");
            file.write("h=" + fHeight);
            file.write("\n");
        } catch (IOException e) {
            System.out.println("TRayTraceError writeTo: " + e);
            throw e;
        }
    }
}
