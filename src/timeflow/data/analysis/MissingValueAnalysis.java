package timeflow.data.analysis;

import timeflow.data.analysis.DBAnalysis.*;
import timeflow.data.db.*;
import timeflow.data.db.filter.*;

public class MissingValueAnalysis implements FieldAnalysis {

	int numNull;
	int percent;
	
	@Override
	public String getName() {
		return "Missing/Blank Values";
	}

	@Override
	public String[] getResultDescription() {
		String s;
		if (numNull==0)
			s="No missing values";
		else if (numNull==1)
			s= "One missing value";
		else
			s=numNull+" missing values:  "+percent+"%";
		return new String[] {s};
	}

	@Override
	public InterestLevel perform(ActList acts, Field field) {
		numNull=DBUtils.count(acts, new MissingValueFilter(field));
		percent=(int)Math.round(100*numNull/(double)acts.size());
		if (numNull==0)
			return InterestLevel.IGNORE;
		if (numNull<5)
			return InterestLevel.VERY_INTERESTING;
		return InterestLevel.INTERESTING;
	}

	@Override
	public boolean canHandleType(Class type) {
		return true;
	}
}
