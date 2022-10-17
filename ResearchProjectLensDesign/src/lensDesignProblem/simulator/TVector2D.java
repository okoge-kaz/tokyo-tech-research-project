package lensDesignProblem.simulator;

import java.io.*;
import java.util.*;


/** 2次元ベクトル を扱うクラス和,差,スカラー倍,内積が扱える<BR>
    @author Kenta Hirano */
public class TVector2D {
    /** ベクトルの要素を格納する. 2次元double型配列.  */
    private double[] fArray;


    /** このクラスで実装するガーベジコレクションのサイズ */
    private static final int MAX_GARBAGE_SIZE = 1000;

    /** このクラスで実装するガベコレ用のオブジェクトの格納場所 */
    private static final TVector2D fGarbage[] = new TVector2D [MAX_GARBAGE_SIZE];

    /** このクラスで実装するガベコレのオブジェクトの現在の格納個数 */
    private static int fGarbageSize = 0;

    /** 2次元零ベクトルを作成.  */
    private TVector2D(){
        fArray = new double[2];
        fArray[0] = 0.0;
        fArray[1] = 0.0;
    }

    /** 2次元ベクトルを作成. 
        @param x  fArray[0] の要素
        @param y  fArray[1] の要素    */
    private TVector2D( final double x, final double y ){
        fArray = new double[2];
        fArray[0] = x;
        fArray[1] = y;
    }

    /** コピーコンストラクタ. 2次元ベクトルを作成. 
        @param src コピー元    */
    private TVector2D( final TVector2D src ){
        fArray = new double[2];
        fArray[0] = src.fArray[0];
        fArray[1] = src.fArray[1];
    }

    /** インスタンスを返す. (再利用できなければ新規)
	コンストラクタ の役割を果たす.
	@return インスタンス    */
    public static TVector2D newInstance(){
	TVector2D v = null;
	if( fGarbageSize == 0 ) {
	    v = new TVector2D();
	}
	else {
	    --fGarbageSize;
	    v = fGarbage[fGarbageSize];
	    v.fArray[0] = v.fArray[1] = 0.0;
	}
	return v;
    }

    /** インスタンスを返す. (再利用できなければ新規)
	コンストタクタ の役割を果たす. 
	@param x  コピー元の fArray[0] の要素
	@param y  コピー元の fArray[1] の要素
	@return インスタンス    */
    public static TVector2D newInstance( double x, double y ){
	TVector2D v = null;
	if( fGarbageSize == 0 ) {
	    v = new TVector2D( x, y );
	}
	else {
	    --fGarbageSize;
	    v = fGarbage[fGarbageSize];
	    v.fArray[0] = x;
	    v.fArray[1] = y;
	}
	return v;
    }

    /** インスタンスを返す. (再利用できなければ新規)
        @param src コピー元
	コピーコンストラクタ の役割を果たす.    */
    public static TVector2D newInstance( TVector2D src ){
	TVector2D v = null;
	if( fGarbageSize == 0 ) {
	    v = new TVector2D( src );
	}
	else {
	    --fGarbageSize;
	    v = fGarbage[fGarbageSize];
	    v.fArray[0] = src.fArray[0];
	    v.fArray[1] = src.fArray[1];
	}
	return v;
    }

    /** 使わなくなったインスタンスの保持 */
    public static void deleteInstance( TVector2D v ){
	if( fGarbageSize < MAX_GARBAGE_SIZE ) {
	    fGarbage[fGarbageSize] = v;
	    ++fGarbageSize;
	    v = null;
	}
	else {
	    System.err.print("Warning:The garbage is ful ");
	    System.err.println("in TVector2D.deleteInstance");
	}
    }


    /** コピーする. 
        @param src コピー元    */
    public final void copy( final TVector2D src ){
        this.fArray[0] = src.fArray[0];
        this.fArray[1] = src.fArray[1];
    }

    /** コピーする. 
        @param x  コピー元の fArray[0] の要素
        @param y  コピー元の fArray[1] の要素    */
    public final void copy( final double x, final double y ){
        this.fArray[0] = x;
        this.fArray[1] = y;
    }

    /** 標準出力に出力する. 
        一行に2個の要素を表示.  */
    public final void writeTo(){
        System.out.print(this.fArray[0] + " ");
        System.out.println(this.fArray[1] + " ");
    }

    /** ファイルに出力する. 
        一行に2個の要素を表示. 
        @param file 出力ストリーム  */
    public final void writeTo( PrintWriter pw ){
        pw.print(this.fArray[0] + " ");
	pw.println(this.fArray[1] + " ");
    }

    /** ファイルに出力する. 
        一行に2個の要素を表示. 
        @param file 出力ストリーム  */
    public final void writeTo( BufferedWriter file ) throws IOException{
        try{
            /* データを書き込む */
            file.write( this.fArray[0] + " " );
            file.write( this.fArray[1] + " " );
            file.write("\n");
        }
        catch( IOException e ){
            System.out.println("TVector2D writeTo: " + e );
            throw e;
        }
    }

    /** ファイルから読み込む. 
        一行から2個の要素を読み取る. 
        @param file 入力ストリーム  */
    public final void readFrom( BufferedReader file ) throws IOException{
        try{
            /* 一行読みこんでスペースごとに区切る */
            StringTokenizer st = new StringTokenizer( file.readLine()," ");
            String s;
            for( int i = 0; i < 2; ++i ){
                /* 後で 修正する */
                if( !st.hasMoreTokens()){
                    System.err.println("TVector2D readFrom: Read Err");
                    System.exit( 1 );
                }
                s = st.nextToken();
                this.fArray[i] = Double.parseDouble( s );
            }
            if( st.hasMoreTokens()){
                System.err.println("TVector2D readFrom: Read Err");
                System.exit( 1 );
            }   
        }
        catch( IOException e ){
            System.out.println("TVector2D readForm: " + e );
            throw e;
        }
    }

    /** 要素を返す.
        @param index 要素番号
        @return index番目の要素    */
    public final double getData( final int index ){
        return fArray[index];
    }

    /** 要素を代入する. 
        @param index 要素番号
        @param data 代入値    */
    public final void setData( final int index , final double data ){
        fArray[index] = data;
    }

    /** fArray[0] を返す. 
        @return 0番目の要素     */
    public final double getX(){
        return fArray[0];
    }

    /** fArray[1] を返す. 
        @return 1番目の要素     */
    public final double getY(){
        return fArray[1];
    }

    /** fArray[0]に代入. 
        @param data 代入値    */
    public final void setX( final double data ){
        fArray[0] = data;
    }

    /** fArray[1]に代入. 
        @param data 代入値    */
    public final void setY( final double data ){
        fArray[1] = data;
    }

    /** 2次元ベクトルが等しければ true を返す. 
        @param src 比較するベクトル     */
    public final boolean isEqual( final TVector2D src ){
        for ( int i = 0; i < 2; ++i ) {
            if ( fArray[i] != src.fArray[i] )
                return false;
        }
        return true;
    }

    /** 等しくなければ true を返す. 
        @param src 比較するベクトル     */
    public final boolean isNotEqual( final TVector2D src ){
        for ( int i = 0; i < 2; ++i ) {
            if ( fArray[i] == src.fArray[i] )
                return false;
        }
        return true;
    }

    /** 絶対値を返す. 
        @return 絶対値    */
     public final double getLength(){
        double result = 0.0;
        result += fArray[0] * fArray[0];
        result += fArray[1] * fArray[1];
        return Math.sqrt( result );
    }

    /** 規範的なベクトル(単位ベクトル)を返す. (注意 new して返している)
	@return 単位ベクトル     */
    public final TVector2D getCanonical(){
        double l = this.getLength();
        if ( l == 0.0 )
            return new TVector2D( 0.0, 0.0 );
        return new TVector2D( fArray[0] / l, fArray[1] / l );
    }

    /** 零ベクトルにリセットする */
    public final void reset(){
        fArray[0] = fArray[1] = 0.0;
    }

    /** 2次元ベクトルを加える. 
        @param src 加えるベクトル    */
    public final void add( final TVector2D src ){
        this.fArray[0] += src.fArray[0];
        this.fArray[1] += src.fArray[1];
    }

    /** 2次元ベクトルを引く. 
        @param src 引くベクトル    */
    public final void subtract( final TVector2D src ){
        this.fArray[0] -= src.fArray[0];
        this.fArray[1] -= src.fArray[1];
    }

    /** スカラー倍する. 
        @param a スカラー倍する実数     */
    public final void scalerProduct( final double a ){
        this.fArray[0] *= a;
        this.fArray[1] *= a;
    }

    /** スカラーで割る. 
        @param a 割り算する実数     */
    public final void scalerQuotient( final double a ){
        this.fArray[0] /= a;
        this.fArray[1] /= a;
    }

    /** 和ベクトルを格納する. 
        (this = src1 + src2)
        @param src1 2次元ベクトル
        @param src2 2次元ベクトル     */
    public final void putAdd( final TVector2D src1, final TVector2D src2 ){
        this.fArray[0] = src1.fArray[0] + src2.fArray[0];
        this.fArray[1] = src1.fArray[1] + src2.fArray[1];
    }

    /** 差ベクトルを格納する. 
        (this = src1 - src2)
        @param src1 2次元ベクトル
        @param src2 2次元ベクトル     */
    public final void putSubtract( final TVector2D src1, final TVector2D src2 ){
        this.fArray[0] = src1.fArray[0] - src2.fArray[0];
        this.fArray[1] = src1.fArray[1] - src2.fArray[1];
    }

    /** スカラー倍を格納する. 
        @param a スカラー
        @param src 2次元ベクトル     */
    public final void putScalerProduct( final double a, final TVector2D src ){
        this.fArray[0] = a * src.fArray[0];
        this.fArray[1] = a * src.fArray[1];
    }

    /** スカラー倍を格納する. 
        @param src 2次元ベクトル
        @param a スカラー     */
    public final void putScalerProduct( final TVector2D src, final double a ){
        this.fArray[0] = a * src.fArray[0];
        this.fArray[1] = a * src.fArray[1];
    }

    /** スカラーで割ったものを格納する. 
        @param src 2次元ベクトル
        @param a スカラー     */
    public final void putScalerQuotient( final TVector2D src, final double a ){
        this.fArray[0] = src.fArray[0] / a;
        this.fArray[1] = src.fArray[1] / a;
    }

    /** 内積を返す. 
        @param src1 2次元ベクトル
        @param src2 2次元ベクトル
        @return 内積(src1 * src2)    */
    public static final double returnInnerProduct( final TVector2D src1,
					     final TVector2D src2 ){
        double result = 0.0;
        result += src1.fArray[0] * src2.fArray[0];
        result += src1.fArray[1] * src2.fArray[1];
        return result;
    }

    /** 内積を返す. 
        @param src 2次元ベクトル
        @return 内積(this * src)    */
    public final double returnInnerProduct( final TVector2D src2 ){
        double result = 0.0;
        result += this.fArray[0] * src2.fArray[0];
        result += this.fArray[1] * src2.fArray[1];
        return result;
    }
}
