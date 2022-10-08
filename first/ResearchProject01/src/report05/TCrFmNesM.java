package report05;

import java.io.IOException;

import crfmnes.TCrFmNes;
import crfmnes.TIndividual;
import crfmnes.matrix2017.TCMatrix;
import jssf.log.TCTable;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * CR-FM-NES��3���s���s���邽�߂̃v���O�����D
 * �e���s�ɂ����āC�W�c���̍ŗǕ]���l�̐��ڂ̃��M���O���s���Ă���D
 * ���O�t�@�C����CSV�t�H�[�}�b�g�ŏo�͂����D
 * �����ݒ�͈ȉ��̒ʂ�F
 * �x���`�}�[�N�֐��Fk-tablet (k=n/4)�C
 * �������Fn=20�C
 * �������̈�F[+1,+5]^n�C
 * => m = [3,...,3]^T, sigma = 1
 * �T���v���T�C�Y�Fn�C
 * �ł��؂�]���񐔁Fn �~ 1e5�C
 * �ł��؂�]���l�F1.0 �~ 1e-7�D
 * ���O�t�@�C�����FRexJggOffsetKTabletP14K5.csv
 *
 * @author isao
 *
 */
public class TCrFmNesM {

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

	private static void evaluate(TIndividual[] pop) {
		for (int i = 0; i < pop.length; ++i) {
			double eval = ktablet(pop[i].getX());
			pop[i].setEvaluationValue(eval);
		}
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
	private static void executeOneTrial(TCrFmNes crfmnes, long maxEvals, TCTable log, String trialName, int trialNo) {
		long noOfEvals = 0; //�]���񐔂��������D
		double best = Double.MAX_VALUE; //�ŗǕ]���l
		int logIndex = 0; //���O�e�[�u���̍s�̓Y�����������D
		int loopCount = 0; //���[�v�J�E���^������������D
    double start = System.currentTimeMillis();
		while (best > 1e-7 && noOfEvals < maxEvals) { //�I�������D�ŗǒl��10^-7�ȉ��C�������́C�]���񐔂��ł��؂�]���񐔂𒴂����Ƃ��D
      TIndividual[] pop = crfmnes.samplePopulation();
      evaluate(pop);
      noOfEvals += pop.length;
      crfmnes.sort();
      best = crfmnes.getBestEvaluationValue();
      crfmnes.nextGeneration();
			if (loopCount % 10 == 0) { //���[�v�J�E���^��10�̔{���̂Ƃ��Ƀ��O���Ƃ�D
				putLogData(log, trialName, trialNo, logIndex, noOfEvals, best);
				++logIndex; //���O�e�[�u���̍s�̓Y�����P�i�߂�D
			}
			++loopCount; //���[�v�J�E���g���P�i�߂�D
		}
    double time = System.currentTimeMillis() - start;
		System.out.println("TrialNo:" + trialNo + ", NoOfEvals:" + noOfEvals + ", Best:" + best + ", Time:" + time + "[msec]"); //��ʂɎ��s���C�]���񐔁C�ŗǕ]���l�C���s���Ԃ�\���D
		putLogData(log, trialName, trialNo, logIndex, noOfEvals, best); //�ŏI����̃��O���Ƃ�D
	}


	/**
	 * ���C�����\�b�h
	 * @param args
	 * @throws IOException
	 */
  public static void main(String[] args) throws IOException {
    int dim = 20; //������
		String trialName = "CrFmNesOffsetKTabletS1"; //���s��
		String logFilename = trialName + ".csv"; //���O�t�@�C����
    int sampleSize = dim; //�T���v���T�C�Y
		int maxTrials = 3; //���s��
		long maxEvals = (long)(4 * dim * 1e4); //�ł��؂�]����
		ICRandom random = new TCJava48BitLcg();
    TCMatrix m = new TCMatrix(dim).fill(3.0); //���σx�N�g���̏����l
    double sigma = 1.0; //�W���΍��̏����l
    TCMatrix D = new TCMatrix(dim).fill(1.0); //�Ίp�s��̏����l
    TCMatrix v = new TCMatrix(dim);
    for (int i = 0; i < dim; ++i) {
      v.setValue(i, random.nextGaussian() / dim);
    }
		TCTable log = new TCTable(); //���O�e�[�u��
		for (int trial = 0; trial < maxTrials; ++trial) {
	    TCrFmNes crfmnes = new TCrFmNes(dim, sampleSize, m, sigma, D, v, random);
			executeOneTrial(crfmnes, maxEvals, log, trialName, trial); //1���s���s
		}
		log.writeTo(logFilename); //3���s���̃��O���t�@�C���ɏo�́D
  }
}
