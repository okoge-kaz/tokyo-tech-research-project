package jgoal.solution;

/**
 * �P�ړI�œK���̂��߂̌̃C���^�[�t�F�[�X
 *
 * @since 2
 * @author isao
 */
public interface ISSolution extends ICSolution {

	/**
	 * �]���l��Ԃ��D
	 * @return �]���l
	 * @since 2 isao
	 */
	double getEvaluationValue();

	/**
	 * �]���l��ݒ肷��D
	 * @param value �]���l
	 * @since 2 isao
	 */
	void setEvaluationValue(double value);

}
