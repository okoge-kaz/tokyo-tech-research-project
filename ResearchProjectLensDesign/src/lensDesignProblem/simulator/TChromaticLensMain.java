package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 色ありレンズ系を評価するサンプルプログラム．
 * @author isao
 *
 */
public class TChromaticLensMain {

	public static void main(String[] args) throws IOException {
		//ダブルガウスレンズのデータをファイルから読み込んで，レンズ系を表示する．
		TLens lens = new TLens();
		BufferedReader br = new BufferedReader(new FileReader("GaussLens.txt"));
		lens.readFrom(br);
		br.close();
		System.out.println(lens);
		
		//焦点距離を満たすように，最終面の曲率半径，最終面と像面の距離を強制する．
		TEnforcementOperator enfOp = new TEnforcementOperator();
		enfOp.doIt(lens);
		
		//レンズ系を評価して，結果を表示する．
		TChromaticLensEvaluator eval = new TChromaticLensEvaluator(true, true, false, true);
		boolean result = eval.doIt(lens);
		System.out.println(result);
		System.out.println(lens);
	}

}
