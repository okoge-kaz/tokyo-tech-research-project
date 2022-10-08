package jgoal.ga.reproductionSelection;


import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;
import jssf.di.ACParam;
import jssf.random.ICRandom;

/**
 * �W�c���烉���_���ɐe�̂�񕜌����o����D
 * 
 * @author uemura
 *
 * @param <X>
 */
public class TCRandomSelectionWithoutReplacement<
	X extends ICSolution
> implements ICReproductionSelection<X> {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	private ICRandom fRandom;

	public TCRandomSelectionWithoutReplacement(
			@ACParam(key="Random",defaultValue="$Random") ICRandom random
	) {
		fRandom = random;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jgoal.ga.reproductionSelection.ICReproductionSelection#doIt(jgoal.solution.TCSolutionSet, int, jgoal.solution.TCSolutionSet)
	 */
	@Override
	public void doIt(TCSolutionSet<X> pop, int noOfParents, TCSolutionSet<X> parents) {
		parents.clear();
		//�e�̂��W�c���烉���_����noOfParents�̐������񕜌����o����D
		for(int i=0; i<noOfParents; i++) {
			int index = fRandom.nextInt(0, pop.size()-1);
			parents.add(pop.remove(index));
		}
	}

}
