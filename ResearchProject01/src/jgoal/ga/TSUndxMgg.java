package jgoal.ga;

import java.io.Serializable;
import java.util.Collections;

import jgoal.ga.reproduction.TCUndx;
import jgoal.ga.reproductionSelection.ICReproductionSelection;
import jgoal.ga.reproductionSelection.TCRandomSelectionWithoutReplacement;
import jgoal.ga.survivalSelection.ICSurvivalSelection;
import jgoal.ga.survivalSelection.TCBestAndRankBasedRouletteSelectionFromFamily;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.comparator.ICComparator;
import jgoal.solution.comparator.TSEvaluationValueComparator;
import jssf.di.ACParam;
import jssf.random.ICRandom;

/**
 * UNDX+MGG
 * 
 * @author isao
 * @param <X>
 */
public class TSUndxMgg implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Dimension */
	private int fDimension;

	/** The population */
	private TCSolutionSet<TSRealSolution> fPopulation;

	/** The population size */
	private int fPopulationSize;

	/** The parent set */
	private TCSolutionSet<TSRealSolution> fParents;

	/** The offspring set */
	private TCSolutionSet<TSRealSolution> fKids;

	/** The number of offspring */
	private int fNoOfKids;

	/** The mating selection operator */
	private ICReproductionSelection<TSRealSolution> fReproductionSelection;

	/** UNDX */
	private TCUndx<TSRealSolution> fUndx;

	/** The survival selection operator */
	private ICSurvivalSelection<TSRealSolution> fSurvivalSelection;

	/** The random number generator */
	private ICRandom fRandom;

	/** The comparator of individuals */
	private ICComparator<TSRealSolution> fComparator;

	/** The solution template */
	private TSRealSolution fSolutionTemplate;

	/**
	 * Constructor
	 * 
	 * @param solution              ���̃e���v���[�g
	 * @param noOfParents           �e�̐�
	 * @param noOfKids              �����q�̐�
	 * @param reproductionSelection �����I����
	 * @param reproduction          �q�̐�����i�����j
	 * @param survivalSelection     �����I����
	 */
	public TSUndxMgg(
			@ACParam(key = "Minimization") boolean minimization,
			@ACParam(key = "Dimension") int dimension,
			@ACParam(key = "PopulationSize") int populationSize,
			@ACParam(key = "NoOfKids") int noOfKids,
			@ACParam(key = "random") ICRandom random) {
		fDimension = dimension;
		fPopulationSize = populationSize;
		fRandom = random;
		fNoOfKids = noOfKids;
		fSolutionTemplate = new TSRealSolution(fDimension);
		fComparator = new TSEvaluationValueComparator<TSRealSolution>(minimization);
	}

	/**
	 * ����������D
	 * 
	 * @return �����W�c
	 */
	public TCSolutionSet<TSRealSolution> initialize() {
		fParents = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fKids = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fReproductionSelection = new TCRandomSelectionWithoutReplacement<TSRealSolution>(fRandom);
		fUndx = new TCUndx<TSRealSolution>(fRandom);
		fSurvivalSelection = new TCBestAndRankBasedRouletteSelectionFromFamily<TSRealSolution>(fComparator, fRandom);
		fPopulation = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fPopulation.resize(fPopulationSize);
		return fPopulation;
	}

	/**
	 * �W�c��Ԃ��D
	 * 
	 * @return �W�c
	 */
	public TCSolutionSet<TSRealSolution> getPopulation() {
		return fPopulation;
	}

	/**
	 * �q�̏W���𐶐����ĕԂ��D
	 * 
	 * @return �q�̏W��
	 */
	public TCSolutionSet<TSRealSolution> makeOffspring() {
		fParents.clear(); // �e�̏W�����N���A����D
		fKids.clear(); // �q�̏W�����N���A����D
		fReproductionSelection.doIt(fPopulation, fUndx.getNoOfParents(), fParents); // �����I�����s���D
		fUndx.makeOffspring(fParents, fNoOfKids, fKids); // AREX�ɂ��q�̏W���𐶐�����D
		return fKids;
	}

	/**
	 * �����i�߂�D
	 */
	public void nextGeneration() {
		fSurvivalSelection.doIt(fPopulation, fParents, fKids); // �����I�����s���D
	}

	/**
	 * �W�c���̍ŗǌ̂�Ԃ��D
	 * 
	 * @return �W�c���̍ŗǌ�
	 */
	public TSRealSolution getBestIndividual() {
		Collections.sort(fPopulation, fComparator);
		return fPopulation.get(0);
	}

	/**
	 * �W�c���̍ŗǌ̂̕]���l��Ԃ��D
	 * 
	 * @return �W�c���̍ŗǌ̂̕]���l
	 */
	public double getBestEvaluationValue() {
		return getBestIndividual().getEvaluationValue();
	}

}
