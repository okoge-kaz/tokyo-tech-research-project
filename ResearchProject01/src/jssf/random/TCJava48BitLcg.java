package jssf.random;

import java.util.Random;

import jssf.di.ACParam;

/**
 * Java SDK��java.util.Random�N���X���g��������������D
 *
 * @since 2
 * @author hmkz
 */
public class TCJava48BitLcg extends TCAbstractRandom {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	private Random fRandom;

	/**
	 * �f�t�H���g�R���X�g���N�^�D
	 * �����N���X�Ƃ��ĕW�����C�u������Random�C�����V�[�h�Ƃ��ăV�X�e��������p����D
	 */
	public TCJava48BitLcg() {
		fRandom = new Random();
	}

	/**
	 * �R���X�g���N�^
	 * @param seed �����V�[�h
	 */
	public TCJava48BitLcg(
			@ACParam(key = "Seed") long seed
	) {
		fRandom = new Random(seed);
	}

	@Override
	protected int next(int bits) {
		assert 0 < bits && bits <= 32;
		return fRandom.nextInt() >>> (32 - bits);
	}

	@Override
	protected void resetSeed(long seed) {
		fRandom.setSeed(seed);
	}

}
