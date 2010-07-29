package timeflow.data.analysis;

import timeflow.data.db.*;

public interface FieldAnalysis {
		
	public String getName();
	public boolean canHandleType(Class type);
	public DBAnalysis.InterestLevel perform(ActList acts, Field field);	
	public String[] getResultDescription();
	
	
}
