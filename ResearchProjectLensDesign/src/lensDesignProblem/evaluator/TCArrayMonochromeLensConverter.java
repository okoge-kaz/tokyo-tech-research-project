package lensDesignProblem.evaluator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import lensDesignProblem.simulator.TChromaticLensEvaluator;
import lensDesignProblem.simulator.TEnforcementOperator;
import lensDesignProblem.simulator.TGlass;
import lensDesignProblem.simulator.TLens;
import lensDesignProblem.simulator.TMyPtrDouble;
import lensDesignProblem.simulator.TTransformer;

public class TCArrayMonochromeLensConverter {

	public static final TGlass G1 = new TGlass("G1", new double[] { 1.613000, 1.613000, 1.613000 }, 0.00);

	public static final TGlass G2 = new TGlass("G2", new double[] { 1.636000, 1.636000, 1.636000 }, 0.00);

	public static final TGlass AIR = TGlass.AIR;

	private TTransformer fConverter;

	/**
	 * コンストラクタ
	 */
	public TCArrayMonochromeLensConverter(double lensGapMin, double lensGapMax,
			double airGapMin, double airGapMax,
			double radiusMin, double radiusMax) {
		fConverter = new TTransformer(lensGapMin, lensGapMax, airGapMin, airGapMax, radiusMin, radiusMax);
	}

	public boolean convertVectorToLens(double[] v, TLens lens) {
		int index = 0;
		TMyPtrDouble r = TMyPtrDouble.newInstance();
		for (int i = 0; i < lens.getNoOfSurfaces() - 1; ++i) {
			if (!fConverter.radius_GaToLens(v[index], r)) {
				return false;
			}
			lens.surface(i).setR(r.getValue());
			++index;
		}
		TMyPtrDouble.deleteInstance(r);
		TMyPtrDouble d = TMyPtrDouble.newInstance();
		for (int i = 0; i < lens.getNoOfSurfaces() - 1; ++i) {
			if (lens.getConfig().isGlass(i + 1)) {
				if (!fConverter.lensGap_GaToLens(v[index], d)) {
					return false;
				}
			} else {
				if (!fConverter.airGap_GaToLens(v[index], d)) {
					return false;
				}
			}
			lens.setD(i, d.getValue());
			++index;
		}
		TMyPtrDouble.deleteInstance(d);
		for (int i = 0; i < lens.getNoOfSurfaces() + 1; ++i) {
			if (lens.getConfig().isAir(i)) {
				lens.setGlass(i, AIR);
			} else {
				if (i == 1 || i == lens.getNoOfSurfaces() - 1) {
					lens.setGlass(i, G1);
				} else {
					lens.setGlass(i, G2);
				}
			}
		}
		return true;
	}

	public boolean convertLensToVector(TLens lens, double[] v) {
		TMyPtrDouble x = TMyPtrDouble.newInstance();
		assert v.length == lens.getNoOfSurfaces() * 2 - 2;
		int index = 0;
		for (int i = 0; i < lens.getNoOfSurfaces() - 1; ++i) {
			if (!fConverter.radius_LensToGa(lens.surface(i).getR(), x)) {
				return false;
			}
			v[index] = x.getValue();
			++index;
		}
		for (int i = 0; i < lens.getNoOfSurfaces() - 1; ++i) {
			if (lens.getConfig().isGlass(i + 1)) {
				if (!fConverter.lensGap_LensToGa(lens.getD(i), x)) {
					return false;
				}
			} else {
				if (!fConverter.airGap_LensToGa(lens.getD(i), x)) {
					return false;
				}
			}
			v[index] = x.getValue();
			++index;
		}
		TMyPtrDouble.deleteInstance(x);
		return true;
	}

	public double getLensGapMin() {
		return fConverter.getLensGapMin();
	}

	public double getLensGapMax() {
		return fConverter.getLensGapMax();
	}

	public double getAirGapMin() {
		return fConverter.getAirGapMin();
	}

	public double getAirGapMax() {
		return fConverter.getAirGapMax();
	}

	public double getRadiusMin() {
		return fConverter.getRadiumMin();
	}

	public double getRadiusMax() {
		return fConverter.getRadiumMax();
	}

	public static void main(String[] args) throws IOException {
		TLens lens = new TLens();
		BufferedReader br = new BufferedReader(new FileReader("3lens-F3_0-f100-w19.txt"));
		lens.readFrom(br);
		br.close();
		TEnforcementOperator enfOp = new TEnforcementOperator();
		TChromaticLensEvaluator eval = new TChromaticLensEvaluator(false, true, false, false);
		enfOp.doIt(lens);
		eval.doIt(lens);
		System.out.println(lens);
		System.out.println("*******************");
		double[] v = new double[lens.getNoOfSurfaces() * 2 - 2];
		TCArrayMonochromeLensConverter conv = new TCArrayMonochromeLensConverter(0.0, 5.0, 0.0, 20.0, 10.0, 1000.0);
		conv.convertLensToVector(lens, v);
		TLens lens2 = new TLens(lens.getConfig(), lens.getFNumber(), lens.getFocusLength(), lens.getWMax());
		conv.convertVectorToLens(v, lens2);
		enfOp.doIt(lens2);
		eval.doIt(lens2);
		System.out.println(lens2);
	}

}
