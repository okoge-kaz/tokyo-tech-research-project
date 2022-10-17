package samples;

import jgoal.ga.TSUndxMgg;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.ICSolution.Status;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * UNDX+MGG���P���s���s���邽�߂̃v���O�����D�ݒ�͈ȉ��̒ʂ�F
 * �x���`�}�[�N�֐��Fk-tablet (k=n/4)�C
 * �������Fn=20�C
 * �������̈�F[-5,+5]^n�C
 * �W�c�T�C�Y�F12n�C
 * �q�̐������F6n�C
 * �ł��؂�]���񐔁Fn �~ 1e5�C
 * �ł��؂�]���l�F1.0 �~ 1e-7�D
 * 
 * @author isao
 *
 */
public class TSUndxMggS {
	
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
			double eval = ktablet(s.getVector()); //k-tablet�֐��̒l�𓾂�D
			s.setEvaluationValue(eval); //�̂ɕ]���l��ݒ�D
			s.setStatus(Status.FEASIBLE); //�̂̏�Ԃ��u���s�\�v�ɐݒ�D
		}
	}
	
	/**
	 * k-tablet�֐� (k=n/4)
	 * @param s ��
	 */
	private static double ktablet(TCMatrix x) {
		int k = (int)((double)x.getDimension() /4.0); //k=n/4
		double result = 0.0; //�]���l��������
		for (int i = 0; i < x.getDimension(); ++i) {
			double xi = x.getValue(i); //i�Ԗڂ̎����̗v�f
			if (i < k) {
				result += xi * xi;				
			} else {
				result += 10000.0 * xi * xi;				
			}
		}
		return result;
	}
	
	/**
	 * ���C�����\�b�h�D
	 * @param args �Ȃ�
	 */
	public static void main(String[] args) {
		boolean minimization = true;
		int dimension = 20; //������
		int populationSize = 12 * dimension; //�W�c�T�C�Y
		int noOfKids = 6 * dimension; //�q�̐�����
		double min = -5.0; //�������̈�̍ŏ��l
		double max = +5.0; //�������̈�̍ő�l
		long maxEvals = (long)(dimension * 1e5); //�ł��؂�]����
		ICRandom random = new TCJava48BitLcg(); //����������
		TSUndxMgg ga = new TSUndxMgg(minimization, dimension, populationSize, noOfKids, random); //UNDX+MGG
		
		TCSolutionSet<TSRealSolution> population = ga.initialize(); //�����W�c�𐶐�
		initializePopulation(population, min, max, random); //�����W�c��������
		evaluate(population); //�����W�c��]��
		
		int noOfEvals = 0; //�]����
		double best = ga.getBestEvaluationValue(); //�W�c���̍ŗǕ]���l�𓾂�D
		System.out.println(noOfEvals + " " + best); //�W�c���̍ŗǕ]���l����ʂɏo�́D
		while (best > 1e-7 && noOfEvals < maxEvals) { //�I�������D�ŗǒl���ł��؂�]���l�ȉ��ɂȂ����Ƃ��C�������́C�]���񐔂��C�ł��؂�]���񐔂𒴂����Ƃ��D
			TCSolutionSet<TSRealSolution> offspring = ga.makeOffspring(); //�q�̏W���𐶐��D
			evaluate(offspring); //�q�̏W����]��
			noOfEvals += offspring.size(); //�]���񐔂��X�V
			ga.nextGeneration(); //������ɐi�߂�D
			best = ga.getBestEvaluationValue(); //�W�c���̍ŗǕ]���l�𓾂�D
			System.out.println(noOfEvals + " " + best); //�W�c���̍ŗǕ]���l����ʂɏo�́D 
		}
		
	}

}
