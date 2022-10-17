package lensDesignProblem.simulator;

import java.io.*;
import java.util.*;

/** 光線クラス<BR>
    @author Kenta Hirano */
public class TRay {
    /** 頂点の数 */
    private int fNoOfVertexes;      /* fArray:vertexs の数 */
    /** 頂点の座標を格納する3次元ベクトル型配列 */
    private TVector3D fArray[];
    /** 現在行われている箇所 */
    private int fCurSize;

    /** 光線クラスを作成
	@param noOfSegments 区切り(曲面)の数    */
    public TRay( int noOfSegments ){
	fNoOfVertexes = noOfSegments + 1;
	fArray = new TVector3D [fNoOfVertexes];
	for( int i=0; i<fNoOfVertexes; ++i ){
	    fArray[i] = TVector3D.newInstance();
	}
	fCurSize = 0;
    }

    /** 光線クラスを作成 */
    public TRay(){
	fNoOfVertexes = 2;
	fArray = new TVector3D [fNoOfVertexes];
	for( int i=0; i<fNoOfVertexes; ++i ){
	    fArray[i] = TVector3D.newInstance();
	}
	fCurSize = 0;
    }

    /** コピーコンストラクタ. 
        @param src コピー元    */
    public TRay( TRay src ){
	fNoOfVertexes = src.fNoOfVertexes;
	fArray = new TVector3D [fNoOfVertexes];
	for ( int i = 0; i < fNoOfVertexes; ++i )
	    fArray[i] = src.fArray[i];
	fCurSize = src.fCurSize;
    }

    /** コピーする. 
        @param src コピー元    */
    public final void copy( final TRay src ){
	if ( fNoOfVertexes != src.fNoOfVertexes ) {
	    fNoOfVertexes = src.fNoOfVertexes;
	    fArray = new TVector3D [fNoOfVertexes];
	    for ( int i = 0; i<fNoOfVertexes; ++i ){
		fArray[i] = TVector3D.newInstance( src.fArray[i] );
	    }
	}
	else {
	    for ( int i = 0; i < fNoOfVertexes; ++i )
		fArray[i].copy( src.fArray[i] );
	    fCurSize = src.fCurSize;
	}
    }

    /** 頂点座標を返す. 
	@param index 要素番号     */
    public final TVector3D getVector3D( int index ){
	return fArray[index];
    }

    /** 頂点座標を代入する. 
	@param index 要素番号
	@param src 頂点座標     */
    public final void setVector3D( int index , TVector3D src ){
	fArray[index].copy( src );
    }

    /** 頂点の数を返す. 
	@return 頂点の数    */
    public final int getNoOfVertexes(){
	return fNoOfVertexes;
    }

    /** 区切りの数を返す. 
	@return 区切りの数     */
    public final int getNoOfSegments(){
	return fNoOfVertexes - 1;
    }

    /** 区切りの数をセットする. (元のデータは無くなる)
	@param noOfSegments 区切りの数     */
    public final void setNoOfSegments( int noOfSegments ){
	if ( noOfSegments + 1 == fNoOfVertexes )
	    return;
	fArray = null;
	fNoOfVertexes = noOfSegments + 1;
	fArray = new TVector3D[fNoOfVertexes];
	for( int i = 0; i < fNoOfVertexes; ++i ){
	    fArray[i] = TVector3D.newInstance();
	}
	fCurSize = 0;
    }

    /** 光線上で x の3次元位置を探索する. 
	@param x 探すx座標
	@param p 探索結果の位置ベクトル     */
    public final void searchPointWithX( double x, TVector3D p ){
	/*
	assert( fNoOfVertexes > 1 );
	assert( fArray[0][0] <= x && x <= fArray[fNoOfVertexes - 1][0] );
	*/
	int ptr0 = 0;
	int ptr1 = fNoOfVertexes - 1;
	while ( true ) {
	    int ptr = (ptr1 + ptr0) / 2;
	    if ( fArray[ptr].getData(0) <= x 
		 && x <= fArray[ptr + 1].getData(0) ) {
		double t = (x - fArray[ptr].getData(0)) 
		    / (fArray[ptr + 1].getData(0)
		       - fArray[ptr].getData(0) );
		p.setData( 0 , x );
		p.setData( 1 , fArray[ptr].getData(1) 
			   + t * (fArray[ptr + 1].getData(1)
				  - fArray[ptr].getData(1)) );
		p.setData( 2 , fArray[ptr].getData(2)
			   + t * (fArray[ptr + 1].getData(2)
				  - fArray[ptr].getData(2)) );
		return;
	    } else if ( fArray[ptr].getData(0) > x ) {
		ptr1 = ptr;
	    } else if ( fArray[ptr + 1].getData(0) < x ) {
		ptr0 = ptr + 1;
	    } else {
		System.err.println("Error in TRay::SearchPointWithX");
		System.exit( 5 );
	    }
	}
    }

    /** 光線の高さが y の位置を size 個探す(前から順に)
	@param y 高さy
	@param array 見つかった位置を格納する(前から順に)
	@param size 探索個数
	@raturn 発見個数     */
    public final int searchPointsWithY( double y, TVector3D array[], int size ){
	/*
	assert( fNoOfVertexes > 1 );
	*/
	int result = 0;
	for ( int i = 0 ; i < fNoOfVertexes - 1; ++i ) {
	    double y1 = fArray[i].getData(1) - y;
	    double y2 = fArray[i + 1].getData(1) - y;
	    if ( y1 * y2 < 0 ) {
		double t = (y - fArray[i].getData(1)) 
		    / (fArray[i + 1].getData(1) 
		       - fArray[i].getData(1));
		array[result].setData( 0 , fArray[i].getData(0)
				       + t * (fArray[i + 1].getData(0)
					      - fArray[i].getData(0)) );
		array[result].setData( 1 , y );
		array[result].setData( 2 , fArray[i].getData(2)
				       + t * (fArray[i + 1].getData(2)
					      - fArray[i].getData(2)) );
		++result;
	    } else if ( y1 == 0.0 ) {
		array[result].copy( fArray[i] );
		++result;
	    } else if ( y2 == 0.0 && i + 1 == fNoOfVertexes - 1 ) {
		array[result].copy( fArray[i + 1] );
		++result;
	    }
	    if ( size == result )
		return result;
	}
	return result;
    }

    /** 最後の頂点を返す */
    public final TVector3D getLastVertex(){
	/*
	assert( fNoOfVertexes == fCurSize );
	*/
	return fArray[fNoOfVertexes - 1];
    }

    /** 標準出力に出力する.      */
    public final void printOn(){
	System.out.println( fNoOfVertexes );
	for ( int i = 0; i < fNoOfVertexes; ++i )
	    System.out.println( fArray[i].getData(0) + " " +
				fArray[i].getData(1) + " " +
				fArray[i].getData(2) );
    }

    /** 標準出力に出力する. (区切りの数であることに注意)      */
    public final void writeTo() {
	System.out.println( this.getNoOfSegments() );
	for ( int i = 0; i < fNoOfVertexes; ++i ) {
	    System.out.print( fArray[i].getData(0) + " " );
	    System.out.print( fArray[i].getData(1) + " " );
	    System.out.println( fArray[i].getData(2) );
	}
    }

    /** ファイルに出力する. (区切りの数であることに注意)
        @param file 出力ストリーム  */
    public final void writeTo( PrintWriter pw ) {
	pw.println( this.getNoOfSegments() );
	for ( int i = 0; i < fNoOfVertexes; ++i ) {
	    pw.print( fArray[i].getData(0) + " " );
	    pw.print( fArray[i].getData(1) + " " );
	    pw.println( fArray[i].getData(2) );
	}
    }

    /** ファイルに出力する. (区切りの数であることに注意)
        @param file 出力ストリーム  */
    public final void writeTo( BufferedWriter file ) throws IOException{
        try{
            file.write( this.getNoOfSegments() + "\n" );
	    for ( int i = 0; i < fNoOfVertexes; ++i ) {
		file.write( fArray[i].getData(0) + " " );
		file.write( fArray[i].getData(1) + " " );
		file.write( fArray[i].getData(2) + "\n" );
	    }
        }
        catch( IOException e ){
            System.out.println("TRay writeTo: " + e );
            throw e;
        }
    }

    /** ファイルから読み込む. 
        @param file 入力ストリーム  */
    public final void readFrom( BufferedReader file ) throws IOException{
	try{
	    int noOfSegments;
	    noOfSegments = Integer.parseInt(file.readLine());
	    this.setNoOfSegments( noOfSegments );

	    double x, y, z;
	    StringTokenizer st;
	    String s;
	    for ( int i = 0; i < fNoOfVertexes; ++i ) {
		st = new StringTokenizer( file.readLine(), " ");
		s = st.nextToken();
		x = Double.parseDouble( s );
		s = st.nextToken();
		y = Double.parseDouble( s );
		s = st.nextToken();
		z = Double.parseDouble( s );

		fArray[i].setData( 0 , x );
		fArray[i].setData( 1 , y );
		fArray[i].setData( 2 , z );
	    }
        }
        catch( IOException e ){
            System.out.println("TRay readForm: " + e );
            throw e;
        }
    }

    /** リセットする */
    public final void reset(){
	fCurSize = 0;
    }

    /** 引数の番号以降の頂点をクリアする.
	@param index 要素番号     */
    public final void clear( int index ){
	for ( int i = index;  i < fNoOfVertexes; ++i )
	    fArray[i].copy( 0.0, 0.0, 0.0 );
	fCurSize = index;
    }

    /** 頂点をクリアする.      */
    public final void clear(){
	for ( int i = 0;  i < fNoOfVertexes; ++i )
	    fArray[i]. copy( 0.0, 0.0, 0.0 );
	fCurSize = 0;
    }

    /** 頂点を追加する */
    public final void appendVertex( TVector3D p ){
	fArray[fCurSize++].copy( p );
    }

    /** 現在の箇所(Currentsize)を返す */
    public final int getCurrentSize(){
	return fCurSize;
    }
}
