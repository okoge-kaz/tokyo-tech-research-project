package jgoal.ga.reproduction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import jgoal.solution.ICRealSolution;
import jgoal.solution.TCSolutionFactory;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.comparator.ICComparator;
import jssf.di.ACParam;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;

/**
 * AREX�̃N���X�D
 * �ڍׂ�[�H�{ 09]���Q�ƁD
 * 
 * @author uemura, isao
 */
public class TCArex<X extends ICRealSolution> implements Serializable {

	private class KidAndEpsilon {

		double[] fEpsilon;
		
		X fKid;
		
		public KidAndEpsilon(TCSolutionFactory<X> factory, int noOfParents) {
			fEpsilon = new double[noOfParents];
			fKid = factory.create();
		}
	}
	
	private class KidAndEpsilonSet extends ArrayList<KidAndEpsilon> {
		
		private static final long serialVersionUID = 1L;
		
		TCSolutionFactory<X> fFactory;
		
		int fNoOfParents;
		
		public KidAndEpsilonSet(X template, int noOfParents) {
			fFactory = new TCSolutionFactory<X>(template);
			fNoOfParents = noOfParents;
		}

		public void resize(int size) {
			assert size >= 0;
			int diff = size - size();
			if(diff > 0) {
				do {
					add(new KidAndEpsilon(fFactory, fNoOfParents));
					diff--;
				} while (diff > 0);
			} else {
				do {
					remove(size() - 1);
					diff++;
				} while (diff < 0);
			}
			assert size == size();
		}
	}
	
	/** for serialization */
	public static final long serialVersionUID = 1L;
	
	/** �����ɗp����e�� */
	private int fMu;
		
	/** �����̕W���΍� */
	private double fSigma;
	
	/** �g���� */
	private double fAlpha;
	
	/** �w�K�� */
	private double fCa;
	
	/** ���p����q�̐� */
	private int fMuA;
	
	/** �����p�����[�^Lcdp */
	private double fLcdp;
	
	/** �����p�����[�^Lavg */
	private double fLavg;
	
	/** �q�̒��S�~���x�N�g�� */
	private TCMatrix fDescentVector;
	
	/** �e�̂̒��S�x�N�g�� */
	private TCMatrix fMeanOfParents;
	
	/** ��Ɨp�x�N�g�� */
	private TCMatrix fTmp;
	
	/** �q�̂Ƃ��̎q�̐����ɗp���������̃Z�b�g */
	private KidAndEpsilonSet fKidAndEpsilonSet;

	/** �̔�r�� */
	private final ICComparator<X> fComparator;
	
	/** �q��+�����̔�r�� */
	private Comparator<KidAndEpsilon> fKidsSetComparator;
	
	/** ���������� */
	private ICRandom fRandom;
	
	/**
	 * �R���X�g���N�^
	 * �����p�����[�^�ŏ�����
	 * @param dim ��莟��
	 * @param comparator �̔�r��
	 * @param random ����������
	 */
	public TCArex(
			@ACParam(key="SolutionTemplate") X solutionTemplate,
			@ACParam(key="ProblemDimension") int dim,
			@ACParam(key="Comparator", defaultValue="$Comparator") ICComparator<X> comparator,
			@ACParam(key="Random", defaultValue="$Random") ICRandom random
	) {
		this(solutionTemplate, dim, dim + 1, 1.0 / (double)dim, 1.0, 1.0 / (5.0 * dim), dim + 1, comparator, random);
	}
	
	/**
	 * �R���X�g���N�^
	 * @param solutionTemplate ���e���v���[�g
	 * @param dim ��莟��
	 * @param mu �e�̐�
	 * @param variance REX�ŗp���镪�U
	 * @param initAlpha �����g����
	 * @param ca �w�K��
	 * @param muAlpha �g�����̌v�Z�ɗ��p����q�̐�
	 * @param comparator �̔�r��
	 * @param random ����������
	 */
	public TCArex(
			@ACParam(key="SolutionTemplate") X solutionTemplate,
			@ACParam(key="ProblemDimension")int dim,
			@ACParam(key="NoOfParents")int mu,
			@ACParam(key="Variance")double variance,
			@ACParam(key="InitialExpansionRate")double initAlpha,
			@ACParam(key="LearningRate")double ca,
			@ACParam(key="SelectionMass")int muAlpha,
			@ACParam(key="Comparator", defaultValue="$Comparator") ICComparator<X> comparator,
			@ACParam(key="Random", defaultValue="$Random")ICRandom random
	) {
		fKidAndEpsilonSet = new KidAndEpsilonSet(solutionTemplate, mu);
		fMu = mu;
		fSigma = Math.sqrt(variance);
		fAlpha = initAlpha;
		fCa = ca;
		fMuA = muAlpha;
		fDescentVector = new TCMatrix(dim, 1);
		fMeanOfParents = new TCMatrix(dim, 1);
		fTmp = new TCMatrix(dim, 1);
		fComparator = comparator;
		fRandom = random;
		fKidsSetComparator = new Comparator<KidAndEpsilon>() {
			@Override
			public int compare(KidAndEpsilon a, KidAndEpsilon b) {
				return fComparator.compare(a.fKid, b.fKid);
			}
		};
	}

	/**
	 * 
	 * �Ȃ��C[�H�{ 09]�ɂ��ƁC
	 * AREX+JGG�ɂ����鐶���q�̐��̐����l�͎��� <i>n</i> �ɑ΂��� <tt>noOfKids</tt> = <i>4n</i> �ł���D
	 */
	public void makeOffspring(TCSolutionSet<X> parents, int noOfkids, TCSolutionSet<X> kids) {
		assert parents.size() == fMu;
		calcDecentDirectionAndMean(parents);
		fKidAndEpsilonSet.clear();
		fKidAndEpsilonSet.resize(noOfkids);
		generateKids(parents);
		kids.clear();
		for(KidAndEpsilon ke : fKidAndEpsilonSet) {
			kids.add(ke.fKid);
		}
	}

	/**
	 * �g�����K�������s����D
	 * doIt���Ă�ŁCkids�𐶐����āCkids��]��������ɌĂяo�����ƁD
	 */
	public void updateExpansionRate() {
		Collections.sort(fKidAndEpsilonSet, fKidsSetComparator);
		calcExpansionRate();				
	}

	public int getNoOfParents() {
		return fMu;
	}

	/**
	 * �g�������v�Z
	 */
	private void calcExpansionRate() {
		if(fKidAndEpsilonSet.size() == 0) return;
		calcLavg();
		calcLcdp();
		fAlpha *= Math.sqrt((1.0 - fCa) + fCa * fLcdp / fLavg);
		fAlpha = Math.max(fAlpha, 1.0);
	}
	
	/**
	 * �����p�����[�^Lavg���v�Z
	 */
	private void calcLavg() {
		fLavg = fAlpha * fSigma * (fMu - 1.0);
		fLavg = fLavg * fLavg / (double)fMuA;
	}

	/**
	 * �����p�����[�^Lcdp���v�Z
	 */
	private void calcLcdp(){
		fLcdp = fAlpha * fAlpha * (fMu - 1.0);
		double e1 = 0.0;
		double e2 = 0.0;
		for(int j=0; j<fMu; j++) {
			double ej = 0.0;
			for(int i=0; i<fMuA; i++) {
				ej += fKidAndEpsilonSet.get(i).fEpsilon[j];
			}
			ej /= (double)fMuA;
			e1 += ej*ej;
			e2 += ej;
		}
		fLcdp *= (e1 - e2 * e2 / (double)fMu);
	}
	
	/**
	 * ���S�~���x�N�g������ѐe�̂̒��S�x�N�g�����v�Z
	 * @param parents
	 */
	private void calcDecentDirectionAndMean(TCSolutionSet<X> parents) {
		assert parents.size() > 0;
		Collections.sort(parents, fComparator);
		fDescentVector.fill(0.0);
		fMeanOfParents.fill(0.0);
		
		double w = 0.0;
		for(int j=0; j<fMu; j++) {
			w = 2.0 * (fMu + 1.0 - (j + 1.0)) / (double)(fMu * (fMu + 1.0));
			fTmp.copyFrom(parents.get(j).getVector());
			fMeanOfParents.add(fTmp);
			fTmp.times(w);
			fDescentVector.add(fTmp);
		}
		fMeanOfParents.times(1.0 / (double)fMu);
	}
	
	/**
	 * �q�̂̐����Ƃ��̕]��
	 * @param parents �e�W�c
	 */
	private void generateKids(TCSolutionSet<X> parents) {
		for(KidAndEpsilon ke : fKidAndEpsilonSet) {
			TCMatrix x = ke.fKid.getVector();
			x.copyFrom(fDescentVector);
			for(int j=0; j<fMu; j++) {
				fTmp.copyFrom(parents.get(j).getVector());
				fTmp.sub(fMeanOfParents);
				double ep = fRandom.nextGaussian(0.0, fSigma);
				fTmp.times(ep * fAlpha);
				ke.fEpsilon[j] = ep;
				x.add(fTmp);
			}
		}
	}
	
}
