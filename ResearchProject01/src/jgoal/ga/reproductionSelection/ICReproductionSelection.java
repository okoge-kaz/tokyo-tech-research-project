package jgoal.ga.reproductionSelection;

import java.io.Serializable;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;

/**
 * GA�̕����I����D
 * �������o�C����є񕜌����o�̗����ŗp����D
 * 
 * @author uemura
 *
 * @param <X>
 */
public interface ICReproductionSelection<
	X extends ICSolution
> extends Serializable {

	/**
	 * �e�̂̑I�����s���D
	 * �������o�̏ꍇ�́Cpopulation���̌̂̃R�s�[��parents�ɓo�^����D
	 * �񕜌����o�̏ꍇ�́Cpopulation����폜�����̂�parents�ɓo�^����D
	 * {@code parents} �ɒ��o���ꂽ�e�W�c���i�[�����D
	 * {@code parents} �͎n�߂�{@code clear()} ����邱�ƁD
	 * 
	 * @param population �W�c
	 * @param noOfParents �K�v�e�̐�
	 * @param parents �e�̂̏W�����i�[�����̏W���D�K�v�ȃT�C�Y�ɗ\�߃��T�C�Y���Ă������ƁD
	 */
	public void doIt(TCSolutionSet<X> population, int noOfParents, TCSolutionSet<X> parents);

}
