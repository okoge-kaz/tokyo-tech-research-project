package jssf.util;

import java.io.Serializable;
import jssf.random.ICRandom;
import jssf.di.ACParam;

/**
 * ���[���b�g
 * @since 2
 * @author isao
 */
public class TCRoulette implements Serializable {

	private static final long serialVersionUID = 1L;

	/** �X���b�g�̐� */
	private int fNoOfSlots;

	/** ���݂̃X���b�g�̈ʒu */
	private int fCurrentSlotIndex;

	/** �X���b�g */
	private double[] fSlots;

	private ICRandom fRandom;

	/**
	 * �R���X�g���N�^
	 * @param noOfSlots the number of slots in the roulette wheel
	 * @param random a random number generator
	 * @since 2 isao
	 */
	public TCRoulette(
			@ACParam(key = "NoOfSlots") int noOfSlots,
			@ACParam(key = "Random") ICRandom random
	) {
		fRandom = random;
		fCurrentSlotIndex = 0;
		setNoOfSlots(0);
	}

	/**
	 * �R���X�g���N�^
	 * @param random a random number generator
	 * @since 2 isao
	 */
	public TCRoulette(ICRandom random) {
		this(0, random);
	}

	/**
	 * ���[���b�gsrc�̃p�����[�^���R�s�[����D
	 * @param src �R�s�[���̃��[���b�g
	 * @since 2 isao
	 */
	public void copyFrom(TCRoulette src) {
		setNoOfSlots(src.fNoOfSlots);
		fCurrentSlotIndex = src.fCurrentSlotIndex;
		for(int i = 0; i < fNoOfSlots; i++)
			fSlots[i] = src.fSlots[i];
	}

	/**
	 * �X���b�g�����Z�b�g����D
	 * @param noOfSlots �X���b�g��
	 * @since 2 isao
	 */
	public void setNoOfSlots(int noOfSlots) {
		resetCurrentSlotIndex();
		if(fNoOfSlots >= noOfSlots) {
			return;
		}
		fSlots = new double[noOfSlots];
		fNoOfSlots = noOfSlots;
	}

	/**
	 * �X���b�g����Ԃ��D
	 * @return �X���b�g��
	 * @since 2 isao
	 */
	public int getNoOfSlots() {
		return fNoOfSlots;
	}

	/**
	 * ���݂̃X���b�g�̈ʒu�����Z�b�g����D<BR>
	 * �J�����g�X���b�g��0�ɂ���D
	 * @since 2 isao
	 */
	public void resetCurrentSlotIndex() {
		fCurrentSlotIndex = 0;
	}

	/**
	 * ���݂̃X���b�g�̈ʒu��Ԃ��D
	 * @return ���݂̃X���b�g�̈ʒu
	 * @since 2 isao
	 */
	public int getCurrentSlotIndex() {
		return fCurrentSlotIndex;
	}

	/**
	 * �X���b�g�ɒl���Z�b�g����D
	 * @param value �Z�b�g����l
	 * @since 2 isao
	 */
	public void setValueToSlot(double value) {
		if(fCurrentSlotIndex == 0) {
			fSlots[fCurrentSlotIndex] = value;
		} else{
			fSlots[fCurrentSlotIndex] = fSlots[fCurrentSlotIndex-1] + value;
		}
		fCurrentSlotIndex++;
	}

	/**
	 * index�Ԗڂ̃X���b�g�̒l��Ԃ��D
	 * @param index �l�𓾂����X���b�g�̈ʒu
	 * @return �X���b�g�̒l
	 * @since 2 isao
	 */
	public double getSlotValue(int index) {
		return fSlots[index];
	}

	/**
	 * ���[���b�g���񂵂āC�����_���ɔԍ���Ԃ�
	 * @return �I�΂ꂽ�ԍ�
	 * @since 2 isao
	 */
	public int doIt() {
		int  selectedIndex;
		double r = fRandom.nextDouble(0.0, fSlots[fCurrentSlotIndex-1]);
		for (selectedIndex = 0; selectedIndex < fNoOfSlots; selectedIndex++) {
			if (fSlots[selectedIndex] > r) {
				return selectedIndex;
			}
		}
		return selectedIndex;
	}
}
