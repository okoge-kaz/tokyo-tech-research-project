package jgoal.solution;

import java.io.Serializable;

/**
 * ���e���v���[�g�𗘗p����t�@�N�g���D
 * clone���\�b�h�𐶂ŗ��p�����ۂɐ�����^�ϊ����B�����邽�߂̃��[�e�B���e�B�D
 * @author isao
 *
 * @param <X>
 */
public class TCSolutionFactory<X extends ICSolution> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** ���e���v���[�g */
	private X fSolutionTemplate;
	
	/**
	 * �R���X�g���N�^
	 * @param solutionTemplate ���e���v���[�g
	 */
	public TCSolutionFactory(X solutionTemplate) {
		fSolutionTemplate = solutionTemplate;
	}
	
	/**
	 * ���𐶐�����D
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public X create() {
		return (X)fSolutionTemplate.clone();
	}
	
	/**
	 * �����̉��̃R�s�[�𐶐�����D
	 * @param src �R�s�[��
	 * @return �R�s�[
	 */
	@SuppressWarnings("unchecked")
	public X createClone(X src) {
		return (X)src.clone();
	}

}
