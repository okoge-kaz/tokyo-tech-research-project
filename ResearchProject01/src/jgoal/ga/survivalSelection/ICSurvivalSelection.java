package jgoal.ga.survivalSelection;

import java.io.Serializable;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;

/**
 * �����I����D
 * @author uemura
 *
 * @param <X>
 */
public interface ICSurvivalSelection<
	X extends ICSolution
> extends Serializable {

	/**
	 * �����I�����s���D
	 * @param population �W�c
	 * @param parents �e�̏W��
	 * @param kids �����q�̏W��
	 */
	void doIt(TCSolutionSet<X> population, TCSolutionSet<X> parents, TCSolutionSet<X> kids);
	
	/**
	 * �����I���ɂ��I�΂ꂽ�̌Q��Ԃ��D
	 * @return �����I���ɂ��I�΂ꂽ�̌Q
	 */
	TCSolutionSet<X> getSelectedSolutions();

}
