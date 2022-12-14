package jgoal.ga.survivalSelection;

import java.util.Collections;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.comparator.ICComparator;
import jssf.di.ACParam;

/**
 * qÂÌãÊNÂÌðWcÉß·¶¶IðíD
 * 
 * @author uemura
 *
 * @param <X>
 */
public class TCNBestSelectionFromKids<
	X extends ICSolution
> implements ICSurvivalSelection<X> {

	/**  */
	private static final long serialVersionUID = 1L;
	
	/** ÂÌärí */
	private ICComparator<X> fComparator;
	
	/** ¶¶IðÉæèIð³ê½ÂÌQ */
	private TCSolutionSet<X> fSelectedSolutions;

	public TCNBestSelectionFromKids(
			@ACParam(key="Comparator", defaultValue="$Comparator") ICComparator<X> comparator
	) {
		fComparator = comparator;
		fSelectedSolutions = null;
	}
	
	@Override
	public void doIt(TCSolutionSet<X> population, TCSolutionSet<X> parents, TCSolutionSet<X> kids) {
		if(fSelectedSolutions == null) {
			fSelectedSolutions = new TCSolutionSet<X>(parents.get(0));
		}
		fSelectedSolutions.clear();
		Collections.sort(kids, fComparator);
		int noOfParents = parents.size();
		for(int i=0; i<noOfParents; i++) {
			population.add(kids.get(i));
			fSelectedSolutions.add(kids.get(i));
		}
	}
	
	@Override
	public TCSolutionSet<X> getSelectedSolutions() {
		return fSelectedSolutions;
	}
}
