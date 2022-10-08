package report06;

import java.io.IOException;

import jgoal.ga.TSArexJgg;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.ICSolution.Status;
import jssf.log.TCTable;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * AREX/JGG��3���s���s���邽�߂̃v���O�����D
 * �e���s�ɂ����āC�W�c���̍ŗǕ]���l�̐��ڂ̃��M���O���s���Ă���D
 * ���O�t�@�C����CSV�t�H�[�}�b�g�ŏo�͂����D
 * �����ݒ�͈ȉ��̒ʂ�F
 * �x���`�}�[�N�֐��FDouble-Sphere (UV)�C
 * �������Fn=20�C
 * �������̈�F[-5,+5]^n�C
 * �W�c�T�C�Y�F14n�C
 * �q�̐������F5n�C
 * �ł��؂�]���񐔁F4n �~ 1e4�C
 * �ł��؂�]���l�F1.0 �~ 1e-7�D
 * ���O�t�@�C�����FArexJggDoubleSphereUVP14K5.csv
 * 
 * @author isao
 *
 */
public class TSArexJggM {

	/**
	 * �����W�c�̏��������s���D
	 * @param population �����W�c
	 * @param min �������̈�̍ŏ��l
	 * @param max �������̈�̍ő�l
	 * @param random ����������
	 */
	private static void initializePopulation(TCSolutionSet<TSRealSolution> population, double min, double max, ICRandom random) {
		for (TSRealSolution s: population) {
			s.getVector().rand(random).times(max - min).add(min); //�̂̍��W��͈�[min, max]^n�̗����ŏ������D
		}
	}

	/**
	 * �W�c���̑S�Ă̌̂̕]�����s���D
	 * @param population �W�c
	 */
	private static void evaluate(TCSolutionSet<TSRealSolution> population) {
		for (TSRealSolution s: population) {
			double eval = doubleSphereUV(s.getVector()); //doubleSphereUV�֐��̒l�𓾂�D
			s.setEvaluationValue(eval); //�̂ɕ]���l��ݒ�D
			s.setStatus(Status.FEASIBLE); //�̂̏�Ԃ��u���s�\�v�ɐݒ�D
		}
	}
	
	/**
	 * k-tablet�֐� (k=n/4)
	 * @param s ��
	 */
	private static double doubleSphereUV(TCMatrix x) {
		double eval1 = 0.0, eval2 = 0.0;
		for(int i = 0; i < x.getDimension(); i++) {
			eval1 += 2.0 * (x.getValue(i) + 2.0) * (x.getValue(i) + 2.0);
			eval2 += 1.0 * (x.getValue(i) - 2.0) * (x.getValue(i) - 2.0);
		}
		return Math.min(eval1, eval2 + 0.1);
	}
	
	/**
	 * �ŗǕ]���l�����O�e�[�u���ɋL�^����D
	 * @param log ���O�e�[�u��
	 * @param trialName ���s���D���O�e�[�u���̃��x���Ɏg����D
	 * @param trialNo ���s�ԍ��D���O�e�[�u���̃��x���Ɏg����D
	 * @param index �s���̓Y��
	 * @param noOfEvals �]����
	 * @param bestEvaluationValue �ŗǕ]���l
	 */
	private static void putLogData(TCTable log, String trialName, int trialNo, int index, long noOfEvals, double bestEvaluationValue) {
		log.putData(index, "NoOfEvals", noOfEvals);
		log.putData(index, trialName + "_" + trialNo, bestEvaluationValue);		
	}

	/**
	 * 1���s�����s����D
	 * @param ga GA
	 * @param maxEvals �ł��؂�]����
	 * @param log ���O�e�[�u��
	 * @param trialName ���s���D���O�e�[�u���̃��x���Ɏg����D
	 * @param trialNo ���s�ԍ��D���O�e�[�u���̃��x���Ɏg����D
	 */
	private static void executeOneTrial(TSArexJgg ga, long maxEvals, TCTable log, String trialName, int trialNo) {
		long noOfEvals = 0; //�]���񐔂��������D
		double best = ga.getBestEvaluationValue(); //�W�c�̍ŗǕ]���l���擾�D
		int logIndex = 0; //���O�e�[�u���̍s�̓Y�����������D
		putLogData(log, trialName, trialNo, logIndex, noOfEvals, best); //�����W�c�̏������O�ɕۑ��D
		++logIndex; //���O�e�[�u���̍s���P�i�߂�D
		int loopCount = 0; //���[�v�J�E���^������������D
		while (best > 1e-7 && noOfEvals < maxEvals) { //�I�������D�ŗǒl��10^-7�ȉ��C�������́C�]���񐔂��ł��؂�]���񐔂𒴂����Ƃ��D
			TCSolutionSet<TSRealSolution> offspring = ga.makeOffspring(); //�q�̏W�c�𐶐��D
			evaluate(offspring); //�q�̏W�c��]��
			noOfEvals += offspring.size(); //�]���񐔂��X�V
			ga.nextGeneration(); //GA�̐�����P����i�߂�D
			best = ga.getBestEvaluationValue(); //�W�c���̍ŗǕ]���l���擾�D
			if (loopCount % 10 == 0) { //���[�v�J�E���^���P�O�̔{���̂Ƃ��Ƀ��O���Ƃ�D
				putLogData(log, trialName, trialNo, logIndex, noOfEvals, best);			
				++logIndex; //���O�e�[�u���̍s�̓Y�����P�i�߂�D
			}
			++loopCount; //���[�v�J�E���g���P�i�߂�D
		}
		putLogData(log, trialName, trialNo, logIndex, noOfEvals, best); //�ŏI����̃��O���Ƃ�D	
		System.out.println("TrialNo:" + trialNo + ", NoOfEvals:" + noOfEvals + ", Best:" + best); //��ʂɎ��s���C�]���񐔁C�ŗǕ]���l��\���D
	}
	
	/**
	 * ���C�����\�b�h�D
	 * @param args �Ȃ�
	 */
	public static void main(String[] args) throws IOException {
		boolean minimization = true; //�ŏ���
		int dimension = 20; //������
		int populationSize = 14 * dimension; //�W�c�T�C�Y
		int noOfKids = 5 * dimension; //�q�̐�����
		double min = -5.00; //�������̈�̍ŏ��l
		double max = +5.00; //�������̈�̍ő�l
		long maxEvals = (long)(4 * dimension * 1e4); //�ł��؂�]����
		int maxTrials = 3; //���s��
		String trialName = "ArexJggDoubleSphereUVP14K5"; //���s��
		String logFilename = trialName + ".csv"; //���O�t�@�C����
		
		ICRandom random = new TCJava48BitLcg(); //����������
		TSArexJgg ga = new TSArexJgg(minimization, dimension, populationSize, noOfKids, random); //AREX/JGG
		TCTable log = new TCTable(); //���O�e�[�u��
		for (int trial = 0; trial < maxTrials; ++trial) {
			TCSolutionSet<TSRealSolution> population = ga.initialize(); //�����W�c���擾�D
			initializePopulation(population, min, max, random); //�����W�c��������
			evaluate(population); //�����W�c��]��
			executeOneTrial(ga, maxEvals, log, trialName, trial); //1���s���s
		}
		log.writeTo(logFilename); //3���s���̃��O���t�@�C���ɏo�́D
	}

}
