package jgoal.ga.survivalSelection;

import java.util.Collections;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.comparator.ICComparator;
import jssf.di.ACParam;

/**
 * Æ°àãÊNÂÌðWcÉß·¶¶IðíD
 * 
 * @author uemura
 *
 * @param <X>
 */
public class TCNBestSelectionFromFamily<
	X extends ICSolution
> implements ICSurvivalSelection<X> {

	/**  */
	private static final long serialVersionUID = 1L;
	
	/** ÂÌärí */
	private ICComparator<X> fComparator;
	
	/** Æ°eÂÌQ+qÂÌQ */
	private TCSolutionSet<X> fFamily;
	
	/** ¶¶IðÉæèIð³ê½ÂÌQ */
	private TCSolutionSet<X> fSelectedSolutions;

	public TCNBestSelectionFromFamily(
			@ACParam(key="Comparator", defaultValue="$Comparator") ICComparator<X> comparator
	) {
		fComparator = comparator;
		fFamily = null;
		fSelectedSolutions = null;
	}
	
	@Override
	public void doIt(TCSolutionSet<X> population, TCSolutionSet<X> parents, TCSolutionSet<X> kids) {
		if(fFamily == null) {
			fFamily = new TCSolutionSet<X>(parents.get(0));
			fSelectedSolutions = new TCSolutionSet<X>(parents.get(0));
		}
		fSelectedSolutions.clear();
		fFamily.clear();
		fFamily.addAll(parents);
		fFamily.addAll(kids);
		Collections.sort(fFamily, fComparator);
		int noOfParents = parents.size();
		for(int i=0; i<noOfParents; i++) {
			population.add(fFamily.get(i));
			fSelectedSolutions.add(fFamily.get(i));
		}
	}

	@Override
	public TCSolutionSet<X> getSelectedSolutions() {
		return fSelectedSolutions;
	}
}
