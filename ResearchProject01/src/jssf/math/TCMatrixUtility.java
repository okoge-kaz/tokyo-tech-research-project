package jssf.math;

import java.util.ArrayList;

/**
 * ��ɍs�񉉎Z�n�̃��[�e�B���e�B�D
 * 
 * @author uemura
 *
 */
public class TCMatrixUtility {
	
	/**
	 * n-by-1�s��i�x�N�g���j�̕W�����ς̌v�Z
	 * @param v1 n-by-1 matrix
	 * @param v2 n-by-1 matrix
	 * @return 
	 */
	public static double innerProduct(TCMatrix v1, TCMatrix v2) {
		if(v1.getColumnDimension() != 1 || v2.getColumnDimension() != 1 || v1.getRowDimension() != v2.getRowDimension()) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		int d = v1.getRowDimension();
		double p = 0.0;
		for(int i=0; i<d; i++) {
			p += v1.getValue(i, 0) * v2.getValue(i, 0);
		}
		return p;
	}
	
	/**
	 * ���[min, max]��n�����������W��v�f�Ƃ���n�����̍s�x�N�g�� (1�~n�s��j�𐶐����ĕԂ��D
	 * @param min �ŏ��l
	 * @param max �ő�l
	 * @param n ������
	 * @return n�����̍s�x�N�g�� (1�~n�s��j
	 * 	 
	 * @author isao
	 */
	public static TCMatrix linspace(double min, double max, int n) {
		double delta = (max - min) / (double)(n - 1);
		TCMatrix result = new TCMatrix(1, n);
		for (int i = 0; i < n - 1; ++i) {
			result.setValue(0, i, min + delta * (double)i);
		}
		result.setValue(0, n - 1, max);
		return result;
	}
	
	/**
	 * X�����̃��b�V���O���b�h�s��𐶐����ĕԂ��D���3�����f�[�^�v���b�g�ɗ��p�����D
	 * @param x ���b�V���̌�_��X���W��v�f�Ƃ���Nx�����̍s�x�N�g��
	 * @param y ���b�V���̌�_��Y���W��v�f�Ƃ���Ny�����̍s�x�N�g��
	 * @return Ny�sNx���X�������b�V���O���b�h�s��
	 * 
	 * @author isao
	 */
	public static TCMatrix meshGridX(TCMatrix x, TCMatrix y) {
		TCMatrix xx = new TCMatrix(y.getColumnDimension(), x.getColumnDimension());
		for (int i = 0; i < xx.getRowDimension(); ++i) {
			for (int j = 0; j < xx.getColumnDimension(); ++j) {
				xx.setValue(i, j, x.getValue(j));
			}
		}
		return xx;
	}

	/**
	 * Y�����̃��b�V���O���b�h�s��𐶐����ĕԂ��D���3�����f�[�^�v���b�g�ɗ��p�����D
	 * @param x ���b�V���̌�_��X���W��v�f�Ƃ���Nx�����̍s�x�N�g��
	 * @param y ���b�V���̌�_��Y���W��v�f�Ƃ���Ny�����̍s�x�N�g��
	 * @return Ny�sNx���Y�������b�V���O���b�h�s��
	 * 
	 * @author isao
	 */
	public static TCMatrix meshGridY(TCMatrix x, TCMatrix y) {
		TCMatrix yy = new TCMatrix(y.getColumnDimension(), x.getColumnDimension());
		for (int i = 0; i < yy.getRowDimension(); ++i) {
			for (int j = 0; j < yy.getColumnDimension(); ++j) {
				yy.setValue(i, j, y.getValue(i));
			}
		}
		return yy;
	}
	
	/**
	 * �T���v���_�i��x�N�g���j�̃��X�g�ƃT���v���_�̏d�S�x�N�g�����番�U�����U�s����v�Z���ĕԂ��D
	 * @param samples �T���v���_�i��x�N�g���j�̃��X�g
	 * @return ���U�����U�s��
	 */
	public static TCMatrix calculateCovarianceMatrix(ArrayList<TCMatrix> samples) {
		int dim = samples.get(0).getDimension();
		TCMatrix mean = calculateMeanVector(samples);
		TCMatrix cov = new TCMatrix(dim, dim);
		for (TCMatrix x: samples) {
			for (int i = 0; i < dim; ++i) {
				for (int j = 0; j < dim; ++j) {
					double newValue = cov.getValue(i, j) + (x.getValue(i) - mean.getValue(i)) * (x.getValue(j) - mean.getValue(i));
					cov.setValue(i, j, newValue);
				}
			}
		}
		cov.div((double)samples.size());
		return cov;
	}
	
	/**
	 * �T���v���_�i��x�N�g���j�̃��X�g����d�S�x�N�g�����v�Z���ĕԂ��D
	 * @param samples �T���v���_�i��x�N�g���j�̃��X�g
	 * @return �d�S�x�N�g��
	 */
	public static TCMatrix calculateMeanVector(ArrayList<TCMatrix> samples) {
		int dim = samples.get(0).getDimension();
		TCMatrix mean = new TCMatrix(dim);
		for (TCMatrix x: samples) {
			mean.add(x);
		}
		mean.div((double)samples.size());
		return mean;
	}

}

