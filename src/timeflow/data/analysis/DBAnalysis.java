package timeflow.data.analysis;

import timeflow.data.db.*;

public interface DBAnalysis {
	
	public enum InterestLevel {IGNORE, BORING, INTERESTING, VERY_INTERESTING};
	
	public String getName();
	public InterestLevel perform(ActList acts);	
	public String[] getResultDescription();
	
	
}
