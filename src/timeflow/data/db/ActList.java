package timeflow.data.db;

import java.util.*;

public class ActList extends ArrayList<Act> {
	
	private ActDB db;
	
	public ActList(ActDB db)
	{
		this.db=db;
	}
	
	public ActDB getDB()
	{
		return db;
	}
	
	public ActList copy()
	{
		return (ActList)clone();
	}
}
