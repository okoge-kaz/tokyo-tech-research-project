package crfmnes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import crfmnes.matrix2017.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * Created by masahiro on 2016/12/27.
 * Modified by isao on 12/04/2017.
 */
public class TCrFmNes implements Serializable {

  private static final long serialVersionUID = 1L;

  private int fDim;
  private int fLambda;
  private TCMatrix fMean;
  private double fSigma;
  private TCMatrix fD;
  private TCMatrix fV;
  // learning rate
  private double fEtaM;
  private double fEtaSigma;
  // weight
  private double[] fWeight;
  private double[] fWRank;
  private double[] fWRankHat;
  private double[] fWDist;
  private double fChiN;

  private double fHInverse;
  private TCMatrix fPc;
  private TCMatrix fPs;
  private double fCs;
  private double fCc;
  private double fMueff;
  private ICRandom fRandom;
  private double fC1cma;
  private TIndividual[] fPop;
  private int fLambdaF = 0;
  private TCMatrix fWz;
  private TCMatrix fXxm;
  private TCMatrix fS;
  private TCMatrix fT;

  private TCMatrix fVBar;
  private TCMatrix fVBarBar;
  private TCMatrix fYVBar;
  private TCMatrix fWork1;
  private TCMatrix fWork2;
  private double[] fWeightsTmp;
  private TCMatrix fOnes;
  private TCMatrix fYy;
  private TCMatrix fY;
  private TCMatrix fA;
  private TCMatrix fInvAvbarbar;


  public TCrFmNes(int dim, int lambda, TCMatrix m, double sigma, TCMatrix D, TCMatrix v, ICRandom random) {
    fDim = dim;
    fLambda = lambda;
    fRandom = new TCJava48BitLcg();
    fMean = m.clone();
    fD = D.clone();
    fV = v.clone();
    fSigma = sigma;
    fRandom = random;
    // create init population
    fPop = new TIndividual[lambda];
    for (int i = 0; i < lambda; ++i) {
      fPop[i] = new TIndividual(dim);
    }
    // evolution path
    fPs = new TCMatrix(fDim);
    fPc = new TCMatrix(fDim);
    // weight
    fWeight = new double[fLambda];
    fWRankHat = new double[fLambda];
    fWRank = new double[fLambda];
    fWDist = new double[fLambda];
    // calculate weight
    double wSum = 0.;
    for (int i = 0; i < fLambda; ++i) {
      fWRankHat[i] = Math.max(0., Math.log(fLambda/2. + 1.) - Math.log(i+1));
      wSum += fWRankHat[i];
    }
    for (int i = 0; i < fLambda; ++i) {
      fWRank[i] = (fWRankHat[i]/wSum) - (1./fLambda);
    }
    double wSqSum = 0.;
    for (int i = 0; i < fLambda; ++i) {
      wSqSum += (fWRank[i] + 1./fLambda) * (fWRank[i] + 1./fLambda);
    }
    fMueff = 1. / wSqSum;
//    fCs = ((fMueff + 2.) / (fDim + fMueff + 5.)) / (2. * Math.log(fDim + 1));
    fCs = (fMueff + 2.) / (fDim + fMueff + 5.);
    fCc = (4.0 + fMueff/dim) / (dim + 4.0 + 2.0*fMueff/dim);
    fHInverse = getHInverse(fDim);
    fEtaM = 1.0;
    fEtaSigma = Double.NaN;
    fChiN = Math.sqrt(fDim) * (1. - 1./(4. * fDim) + 1./(21. * fDim * fDim));
    fC1cma = 2. / (Math.pow(dim+1.3, 2) + fMueff);
    fWz = new TCMatrix(fDim);
    fXxm = new TCMatrix(fDim);
    fS = new TCMatrix(fDim);
    fT = new TCMatrix(fDim);
    fPop = new TIndividual[fLambda];
    for (int i = 0; i < fLambda; ++i) {
      fPop[i] = new TIndividual(dim);
    }

    fVBar = new TCMatrix(fDim);
    fVBarBar = new TCMatrix(fDim);
    fYVBar = new TCMatrix(fDim);
    fWork1 = new TCMatrix(fDim);
    fWork2 = new TCMatrix(fDim);
    fOnes = new TCMatrix(fDim).fill(1.0);
    fYy = new TCMatrix(fDim);
    fY = new TCMatrix(fDim);
    fA = new TCMatrix(fDim);
    fInvAvbarbar = new TCMatrix(fDim);


    fWeightsTmp = new double[fLambda];
  }

  public TIndividual[] samplePopulation() {
  	double vNormL2 = fV.normL2();
  	fVBar.div(fV, vNormL2);
    double normv2 = vNormL2 * vNormL2;
    double gammav = 1.0 + normv2;
    // symmetry variable method
    for (int i = 0; i < fLambda / 2; ++i) {
      // z
      fPop[2 * i].getZ().randn(fRandom);
      fPop[2 * i + 1].getZ().times(fPop[2 * i].getZ(), -1.0);
      // y
      double coefVbar = (Math.sqrt(gammav) - 1.0) * fPop[2 * i].getZ().innerProduct(fVBar);
      fPop[2 * i].getY().add(fPop[2 * i].getZ(), fWork1.times(fVBar, coefVbar));
      fPop[2 * i + 1].getY().times(fPop[2 * i].getY(), -1.0);
      // x
      fPop[2 * i].getX().timesElement(fD, fPop[2 * i].getY()).times(fSigma);
      fPop[2 * i + 1].getX().times(fPop[2 * i].getX(), -1.0);
      fPop[2 * i].getX().add(fMean);
      fPop[2 * i + 1].getX().add(fMean);
    }
    return fPop;
  }

  public double getBestEvaluationValue() {
  	return fPop[0].getEvaluationValue();
  }

  public TCMatrix getV() {
  	return fV;
  }

  public TCMatrix getD() {
  	return fD;
  }

  private double f(double a, double b) {
    return ((1.0 + a * a) * Math.exp(a * a / 2.0) / 0.24) - 10.0 - (double)fDim;
  }

  private double fprime(double a) {
    return (1.0 / 0.24) * a * Math.exp(a * a / 2.0) * (3.0 + a * a);
  }

  private double getHInverse(int dim) {
    double alphaDist = 1.0;
    while (Math.abs(f(alphaDist, fDim)) > 1e-10) {
      alphaDist = alphaDist - 0.5 * (f(alphaDist, dim) / fprime(alphaDist));
    }
    return alphaDist;
  }
  private double getAlphaDist(int lambdaF) {
//    return fHInverse * Math.min(1., Math.sqrt((double)fLambda/fDim)) * Math.sqrt((double)lambdaF/fLambda);
    return fHInverse * Math.min(1.0, Math.sqrt((double)fLambda/fDim)) * Math.sqrt((double)lambdaF/fLambda);
  }

  private double wDistHat(TCMatrix z, int lambdaF) {
    return Math.exp(getAlphaDist(lambdaF) * z.normL2());
  }

  private double etaMoveSigma = 1.0;

  private double etaStagSigma(int lambdaF) {
  	return Math.tanh((0.024*lambdaF + 0.7*fDim + 20)/(fDim + 12.0));
  }

  private double etaConvSigma(int lambdaF) {
  	return 2 * Math.tanh((0.025*lambdaF + 0.75*fDim + 10)/(fDim + 4.0));
  }

  private double getC1(int lambdaF) {
  	return fC1cma * (fDim - 5.) / 6. * lambdaF / fLambda;
  }

  private double getEtaB(int lambdaF) {
  	return Math.tanh((Math.min(0.02*lambdaF, 3.0*Math.log(fDim)) + 5) / (0.23*fDim + 25));
  }

  public void sort() {
    Arrays.sort(fPop, new Comparator<TIndividual>() {
      @Override
      public int compare(TIndividual o1, TIndividual o2) {
        if (o1.getEvaluationValue() < Double.MAX_VALUE && o2.getEvaluationValue() < Double.MAX_VALUE) {
          int sgn = 0;
          if (o1.getEvaluationValue() - o2.getEvaluationValue() < 0)
            sgn = -1;
          else if (o1.getEvaluationValue() - o2.getEvaluationValue() > 0)
            sgn = 1;
          return sgn;
        } else if (o1.getEvaluationValue() < Double.MAX_VALUE && o2.getEvaluationValue() >= Double.MAX_VALUE) {
          return -1;
        } else if (o1.getEvaluationValue() >= Double.MAX_VALUE && o2.getEvaluationValue() < Double.MAX_VALUE) {
          return 1;
        } else if (o1.getEvaluationValue() >= Double.MAX_VALUE && o2.getEvaluationValue() >= Double.MAX_VALUE) {
          int sgn = 0;
          if (o1.getZ().normL2() - o2.getZ().normL2() < 0)
            sgn = -1;
          else if (o1.getZ().normL2() - o2.getZ().normL2() > 0)
            sgn = 1;
          return sgn;
        } else {
          throw new RuntimeException("The solutions have not been evaluated!!");
        }
      }
    });
  }


  public void nextGeneration() {
    fWz.fill(0.0);
    fXxm.fill(0.0);
    fS.fill(0.0);
    fT.fill(0.0);
    fLambdaF = 0;
    for (int i = 0; i < fLambda; ++i) {
      if (fPop[i].getEvaluationValue() < Double.MAX_VALUE) {
        fLambdaF++;
      }
      fWz.add(fWork1.times(fPop[i].getZ(), fWRank[i]));
    }
    fPs.times(1.0 - fCs).add(fWork1.times(fWz, Math.sqrt(fCs * (2.0 - fCs) * fMueff)));
    double psNormL2 = fPs.normL2();

    double tmpSum = 0.0;
    for (int i = 0; i < fLambda; ++i) {
    	double tmp = fWRankHat[i] * wDistHat(fPop[i].getZ(), fLambdaF);
      fWeightsTmp[i] = tmp;
      tmpSum += tmp;
    }
    for (int i = 0; i < fLambda; ++i) {
      fWDist[i] = fWeightsTmp[i] / tmpSum - 1.0 / fLambda;
    }
    // switching learning rate and weight
    if (psNormL2 >= fChiN) {
      fWeight = fWDist;
    } else {
      fWeight = fWRank;
    }

    if (psNormL2 >= fChiN) {
      fEtaSigma = etaMoveSigma;
    } else if (psNormL2 > 0.1 * fChiN) {
      fEtaSigma = etaStagSigma(fLambdaF);
    } else {
      fEtaSigma = etaConvSigma(fLambdaF);
    }

    // wxm
    for (int i = 0; i < fLambdaF; ++i) {
      fXxm.add(fWork1.sub(fPop[i].getX(), fMean).times(fWeight[i]));
    }
    fPc.times(1.0 - fCc).add(fWork1.times(fXxm, Math.sqrt(fCc * (2.0 - fCc) * fMueff) / fSigma));
    fMean.add(fWork1.times(fXxm, fEtaM));
    double vNormL2 = fV.normL2();
    // vd-cma update
    fVBar.div(fV, vNormL2);
    //TCMatrix vbarbar = fVBar.clone().timesElement(fVBar);
    fVBarBar.timesElement(fVBar, fVBar);
    double normv2 = vNormL2 * vNormL2;
    double gammav = 1.0 + normv2;
    // brief computes the sample wise first two steps of S and T of theorem 3.6 in the paper
    // rank-mu update
    for (int i = 0; i < fLambda; ++i) {
      double w = fWeight[i] * getEtaB(fLambdaF);
      double yvbarInner = fPop[i].getY().innerProduct(fVBar);

      // step 1
      double coefYvbar = normv2 * yvbarInner / gammav ;
      fYVBar.timesElement(fPop[i].getY(), fVBar);
      fYy.timesElement(fPop[i].getY(), fPop[i].getY());
      //s.add((fWork2.sub(fYy, fWork1.times(fYVBar, coefYvbar)).sub(fOnes)).times(w));
      fS.add(fWork2.sub(fYy, fWork1.times(fYVBar, coefYvbar)).sub(fOnes).times(w));

      // step 2
      double coefVbar = (yvbarInner * yvbarInner + gammav) / 2.;
      fT.add(fWork1.times(fPop[i].getY(), yvbarInner).sub(fWork2.times(fVBar, coefVbar)).times(w));
    }

    // rank-one update
    double lc = -1.0;
    if (psNormL2 >= fChiN) {
      lc = 1.0;
    } else {
      lc = 0.0;
    }
    double w = lc * getC1(fLambdaF);


    fY.divElement(fPc, fD);
    double yvbarInner = fY.innerProduct(fVBar);
    // step 1
    double coefYvbar = normv2 / gammav * yvbarInner;
    fS.add((fWork2.timesElement(fY, fY).sub(fWork1.timesElement(fY, fVBar).times(coefYvbar)).sub(fOnes)).times(w));

    // step 2
    double coefVbar = (yvbarInner * yvbarInner + gammav) / 2.0;
    fT.add(fWork1.times(fY, yvbarInner).sub(fWork2.times(fVBar, coefVbar)).times(w));


    // brief computes the last three steps of S and of theorem 3.6 in the paper
    // alpha of 3.5
    double maxVbarbar = -1.0;
    for (int i = 0; i < fDim; ++i) {
      if (maxVbarbar < fVBarBar.getValue(i)) {
        maxVbarbar = fVBarBar.getValue(i);
      }
    }
    double tmp = normv2 * normv2 + (2.0 * gammav - Math.sqrt(gammav)) / maxVbarbar;
    tmp = Math.sqrt(tmp);
    tmp /= 2.0 + normv2;
    double alpha = Math.min(1.0, tmp);
    // constants (b, A) of 3.4
    double alpha2 = alpha * alpha;
    double b = -(1.0 - alpha2) * normv2 * normv2 / gammav + 2.0 * alpha2;
    fA.fill(2.0).sub(fWork1.times(fVBarBar, b + 2.0 * alpha2));
    fInvAvbarbar.divElement(fVBarBar, fA);
    // step 3
    fWork1.timesElement(fVBar, fT).times(2.0 + vNormL2 * vNormL2);
    fWork2.times(fVBarBar, vNormL2 * vNormL2 * fVBar.innerProduct(fT));
    fS.sub((fWork1.sub(fWork2)).times(alpha / gammav));

   // step 4
    double coefInvAvbarbar = b / (1.0 + b * (fVBarBar.innerProduct(fInvAvbarbar))) * fS.innerProduct(fInvAvbarbar);
    fS.sub(fWork1.divElement(fS, fA), fWork2.times(fInvAvbarbar, coefInvAvbarbar));

    // step 5
    fWork1.timesElement(fVBar, fS).times(2.0 + vNormL2 * vNormL2);
    fWork2.times(fVBar, fS.innerProduct(fVBarBar));
    fT.sub((fWork1.sub(fWork2)).times(alpha));

    // update v and d
    double sumLogD = 0.0;
    for (int i = 0; i < fDim; ++i) {
      sumLogD += Math.log(fD.getValue(i));
    }
    fD.add(fWork1.timesElement(fD, fS));
    fV.add(fWork1.div(fT, fV.normL2()));
    double nVNormL2 = fV.normL2();

    fWork1.fill(0.0);
    for (int i = 0; i < fLambda; ++i) {
      for (int j = 0; j < fDim; ++j) {
        fWork2.setValue(j, fPop[i].getZ().getValue(j) * fPop[i].getZ().getValue(j) - 1.0);
      }
      fWork1.add(fWork2.times(fWeight[i]));
    }

    double sumForGsigma = 0.0;
    for (int i = 0; i < fDim; ++i) {
      sumForGsigma += fWork1.getValue(i);
    }
    double Gsigma = sumForGsigma / (double)fDim;

    double lsig = -1.0;
    if (Gsigma < 0.0 && psNormL2 >= fChiN) {
      lsig = 1.0;
    } else {
      lsig = 0.0;
    }

    double sumLogND = 0.0;
    for (int i = 0; i < fDim; ++i) {
      sumLogND += Math.log(fD.getValue(i));
    }

    double Cstep = Math.exp(1.0 / (double)fDim * sumLogD + 1.0 / (2.0 * (double)fDim) * Math.log(1.0 + vNormL2 * vNormL2));
    double nCstep = Math.exp(1.0 / (double)fDim * sumLogND + 1.0 / (2.0 * (double)fDim) * Math.log(1.0 + nVNormL2 * nVNormL2));
    double coef = nCstep / Cstep;
    fSigma = fSigma * Math.exp((1.0 - lsig) * (fEtaSigma / 2.0) * Gsigma) * coef;
    fD.div(nCstep);
  }

}
