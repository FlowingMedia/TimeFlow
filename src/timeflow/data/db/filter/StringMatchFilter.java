package timeflow.data.db.filter;

import timeflow.data.db.*;
import timeflow.data.time.*;

import java.util.regex.*;

public class StringMatchFilter extends ActFilter {

	private Field[] textFields;
	private Field[] listFields;
	private String query="";
	private boolean isRegex=false;
	private Pattern pattern;
	
	public StringMatchFilter(ActDB db, boolean isRegex)
	{
		this(db,"", isRegex);
	}
	
	public StringMatchFilter(ActDB db, String query, boolean isRegex)
	{
		textFields=(Field[])db.getFields(String.class).toArray(new Field[0]);
		listFields=(Field[])db.getFields(String[].class).toArray(new Field[0]);
		this.isRegex=isRegex;
		setQuery(query);
	}
	
	public String getQuery()
	{
		return query;
	}
	
	public void setQuery(String query)
	{
		this.query=query;
		if (isRegex)
		{
			pattern=Pattern.compile(query, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE+Pattern.DOTALL);
		}
		else
			this.query=query.toLowerCase();
	}
	
	@Override
	public boolean accept(Act act) {
		// check text fields
		for (int i=0; i<textFields.length; i++)
		{
			String s=act.getString(textFields[i]);
			if (s==null) continue;
			if (isRegex ? pattern.matcher(s).find() : s.toLowerCase().contains(query))
				return true;
		}
		// check list fields
		for (int j=0; j<listFields.length; j++)
		{
			String[] m=act.getTextList(listFields[j]);
			if (m!=null)
				for (int i=0; i<m.length; i++)
				{
					String s=m[i];
					if (isRegex ? pattern.matcher(s).find() : s.toLowerCase().contains(query))
						return true;
				}
		}
		return false;
	}
}
