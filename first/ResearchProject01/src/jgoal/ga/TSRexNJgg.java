package jgoal.ga;

import java.io.Serializable;
import java.util.Collections;
import jgoal.ga.reproduction.TCRex;
import jgoal.ga.reproduction.TCRex.ProbabilityDistribution;
import jgoal.ga.reproductionSelection.ICReproductionSelection;
import jgoal.ga.reproductionSelection.TCRandomSelectionWithoutReplacement;
import jgoal.ga.survivalSelection.ICSurvivalSelection;
import jgoal.ga.survivalSelection.TCNBestSelectionFromKids;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.comparator.ICComparator;
import jgoal.solution.comparator.TSEvaluationValueComparator;
import jssf.di.ACParam;
import jssf.random.ICRandom;

/**
 * REX(Normal distribution)/JGG
 * 
 * @author isao
 * @param <X>
 */
public class TSRexNJgg implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** ������ */
	private int fDimension;
	
	/** �W�c */
	private TCSolutionSet<TSRealSolution> fPopulation;
	
	/** �W�c�T�C�Y */
	private int fPopulationSize;
	
	/** �e�̏W�� */
	private TCSolutionSet<TSRealSolution> fParents;
	
	/** �q�̏W�� */
	private TCSolutionSet<TSRealSolution> fKids;
	
	/** �����q�̐� */
	private int fNoOfKids;

	/** �����I���� */
	private ICReproductionSelection<TSRealSolution> fReproductionSelection;

	/** �q�̐�����i�����j */
	private TCRex<TSRealSolution> fRex;
	
	/** �����I���� */
	private ICSurvivalSelection<TSRealSolution> fSurvivalSelection;
	
	/** ���������� */
	private ICRandom fRandom;
	
	/** �̔�r�� */
	private ICComparator<TSRealSolution> fComparator;
	
	/** �̃e���v���[�g */
	private TSRealSolution fSolutionTemplate;
	
	/**
	 * �R���X�g���N�^�D
	 * @param solution ���̃e���v���[�g
	 * @param noOfParents �e�̐�
	 * @param noOfKids �����q�̐�
	 * @param reproductionSelection �����I����
	 * @param reproduction �q�̐�����i�����j
	 * @param survivalSelection �����I����
	 */
	public TSRexNJgg(
			@ACParam(key="Minimization") boolean minimization,
			@ACParam(key="Dimension") int dimension,
			@ACParam(key="PopulationSize") int populationSize,
			@ACParam(key="NoOfKids") int noOfKids,
			@ACParam(key="random") ICRandom random
	) {
		fDimension = dimension;
		fPopulationSize = populationSize;
		fRandom = random;
		fNoOfKids = noOfKids;
		fSolutionTemplate = new TSRealSolution(fDimension);
		fComparator = new TSEvaluationValueComparator<TSRealSolution>(minimization);
	}

	/**
	 * ����������D
	 * @return �����W�c
	 */
	public TCSolutionSet<TSRealSolution> initialize() {
		fParents = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fKids = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fReproductionSelection = new TCRandomSelectionWithoutReplacement<TSRealSolution>(fRandom);
		fRex = new TCRex<TSRealSolution>(fDimension, ProbabilityDistribution.NORMAL, fRandom);
		fSurvivalSelection = new TCNBestSelectionFromKids<TSRealSolution>(fComparator);
		fPopulation = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fPopulation.resize(fPopulationSize);
		return fPopulation;
	}
	
	/**
	 * �W�c��Ԃ��D
	 * @return �W�c
	 */
	public TCSolutionSet<TSRealSolution> getPopulation() {
		return fPopulation;
	}
	
	/**
	 * �q�̏W���𐶐����ĕԂ��D
	 * @return �q�̏W��
	 */
	public TCSolutionSet<TSRealSolution> makeOffspring() {
		fParents.clear(); //�e�̏W�����N���A����D
		fKids.clear(); //�q�̏W�����N���A����D
		fReproductionSelection.doIt(fPopulation, fRex.getNoOfParents(), fParents); //�����I�����s���D
		fRex.makeOffspring(fParents, fNoOfKids, fKids); //AREX�ɂ��q�̏W���𐶐�����D
		return fKids;
	}
	
	/**
	 * �����i�߂�D
	 */
	public void nextGeneration() {
		fSurvivalSelection.doIt(fPopulation, fParents, fKids); //�����I�����s���D
	}
	
	/**
	 * �W�c���̍ŗǌ̂�Ԃ��D
	 * @return �W�c���̍ŗǌ�
	 */
	public TSRealSolution getBestIndividual() {
		Collections.sort(fPopulation, fComparator);
		return fPopulation.get(0);
	}
	
	/**
	 * �W�c���̍ŗǌ̂̕]���l��Ԃ��D
	 * @return �W�c���̍ŗǌ̂̕]���l
	 */
	public double getBestEvaluationValue() {
		return getBestIndividual().getEvaluationValue();
	}
	
}
