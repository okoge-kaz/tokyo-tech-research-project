package lensDesignProblem.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 レンズ設計法(松居) p.17
 */
/**
 * @author Kenta Hirano
 */
public class TEnforcementOperator {

	public TEnforcementOperator() {
	}

	public final boolean doIt(TLens x) {
		double a = 0.0;
		double h = 1.0;
		int ns = x.getNoOfSurfaces();
		for (int i = 0; i < ns - 1; ++i) {
			double n1 = x.getGlass(i).getN(TWavelength.REF_D);
			double n2 = x.getGlass(i + 1).getN(TWavelength.REF_D);
			/*
			 * a = a + h * (x.GetN( i + 1 ) - x.GetN( i )) / x.Surface( i ).GetR();
			 */
			/*
			 * h = h - x.GetD( i ) * a / x.GetN( i + 1 );
			 */
			a = a + h * (n2 - n1) / x.surface(i).getR();
			h = h - x.getD(i) * a / n2;
		}
		double f = x.getFocusLength();
		double ns1 = x.getGlass(ns - 1).getN(TWavelength.REF_D);
		double ns2 = x.getGlass(ns).getN(TWavelength.REF_D);
		double lastR = f * h * (ns2 - ns1) / (1.0 - f * a);
		double lastD = f * h * ns2;
		if (lastD < 0.0) {
			return false;
		}
		x.surface(ns - 1).setR(lastR);
		x.setD(ns - 1, lastD);
		x.updateSurfacePositions();
		return true;
	}

	public static void main(String[] args) throws IOException {
		TLens lens = new TLens();
		BufferedReader br = new BufferedReader(new FileReader("GaussLens.txt"));
		lens.readFrom(br);
		br.close();
		System.out.println(lens);
		TEnforcementOperator enOp = new TEnforcementOperator();
		enOp.doIt(lens);
		System.out.println(lens);
	}

}
