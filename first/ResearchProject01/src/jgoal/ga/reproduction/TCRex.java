package jgoal.ga.reproduction;

import java.io.Serializable;
import jgoal.solution.ICRealSolution;
import jgoal.solution.TCSolutionSet;
import jssf.di.ACParam;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;

/**
 * ����Rex
 * @author uemura, isao
 *
 * @param <X>
 */
public class TCRex<X extends ICRealSolution> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** ���p����m�����z�̌`�� */
	public enum ProbabilityDistribution {
		NORMAL,
		UNIFORM,
	}
	
	private ProbabilityDistribution fProbabilityDistribution;
	
	/** ���p����m�����z�̊g�����D���v�ʈ�`�𖞂����悤�ɐݒ肳���D */
	private double fExpansionRatio;
	
	/** �e�̂̏d�S�x�N�g�� */
	private TCMatrix fXg;
	
	private TCMatrix fWork;
	
	private int fNoOfParents;

	private ICRandom fRandom;
	
	/**
	 * �R���X�g���N�^
	 * @param dim ���̎�����
	 * @param noOfParents �e�̐�
	 * @param pd ���p����m�����z�DNORMAL��������UNIFORM��I���D
	 * @param random ����������
	 */
	public TCRex(
			@ACParam(key="ProblemDimension") int dim,
			@ACParam(key="NoOfParents") int noOfParents,
			@ACParam(key="ProbabilityDistribution") ProbabilityDistribution pd,
			@ACParam(key="Random", defaultValue="$Random") ICRandom random
	) {
		fNoOfParents = noOfParents;
		fProbabilityDistribution = pd;
		fRandom = random;
		switch(fProbabilityDistribution) {
		case NORMAL:
			fExpansionRatio = Math.sqrt(1.0 / (double)fNoOfParents);
			break;
		case UNIFORM:
			fExpansionRatio = Math.sqrt(3.0 / (double)fNoOfParents);
			break;
		}
		fXg = new TCMatrix(dim, 1);
		fWork = new TCMatrix(dim, 1);
	}
	
	/**
	 * �e�̐���(n+1)�ŏ������D
	 * 
	 * @param dim ���̎�����
	 * @param pd ���p����m�����z�DNORMAL��������UNIFORM��I���D
	 * @param random ����������
	 */
	public TCRex(
			@ACParam(key="ProblemDimension") int dim,
			@ACParam(key="ProbabilityDistribution") ProbabilityDistribution pd,
			@ACParam(key="Random", defaultValue="$Random") ICRandom random
	) {
		this(dim, dim + 1, pd, random);
	}

	public int getNoOfParents() {
		return fNoOfParents;
	}

	public void makeOffspring(TCSolutionSet<X> parents, int noOfKids, TCSolutionSet<X> kids) {
		assert parents.size()  == fNoOfParents;
		calcXg(parents);
		kids.clear();
		kids.resize(noOfKids);
		for(X kid : kids) {
			TCMatrix kVec = kid.getVector();
			kVec.copyFrom(fXg); //�d�S�x�N�g�����R�s�[
			for(X parent : parents) {
				fWork.copyFrom(parent.getVector());
				fWork.sub(fXg);
				double r = 0;
				switch (fProbabilityDistribution) {
				case UNIFORM:
					r = fRandom.nextDouble(-fExpansionRatio, fExpansionRatio);
					break;
				case NORMAL:
					r = fRandom.nextGaussian(0.0, fExpansionRatio);
					break;
				}
				fWork.times(r);
				kVec.add(fWork);
			}
		}
	}
	
	/**
	 * �e�̂̏d�S�̌v�Z�D
	 * @param parents
	 */
	private void calcXg(TCSolutionSet<X> parents) {
		fXg.setDimensions(parents.get(0).getVector().getRowDimension(), 1);
		fXg.fill(0.0);
		for(X parent : parents) {
			fXg.add(parent.getVector());
		}
		fXg.times(1.0 / (double)parents.size());
	}
	

}
