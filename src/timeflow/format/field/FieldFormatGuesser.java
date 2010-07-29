package timeflow.format.field;

import timeflow.util.*;

public class FieldFormatGuesser {
	
	FieldFormat[] scores;
	
	private FieldFormatGuesser()
	{
		scores=FieldFormatCatalog.listFormats();
	}
	public static Class[] analyze(String[][] data, int startRow, int numRows)
	{
		int n=data[0].length;
		FieldFormatGuesser[] g=new FieldFormatGuesser[n];
		for (int i=0; i<n; i++)
			g[i]=new FieldFormatGuesser();
		for (int i=startRow; i<startRow+numRows && i<data.length; i++)
		{
			for (int j=0; j<n; j++)
				g[j].add(data[i][j]);
		}
		Class[] c=new Class[n];
		for (int i=0; i<n; i++)
			c[i]=g[i].best();
		return c;
	}	
	
	private void add(String s)
	{
		for (int i=0; i<scores.length; i++)
			scores[i].note(s);
	}
	
	private Class best()
	{
		double max=scores[0].value;
		Class best=scores[0].getType();
		for (int i=1; i<scores.length; i++)
		{
			if (scores[i].value>max)
			{
				max=scores[i].value;
				best=scores[i].getType();
			}
		}
		return best;
	}
}
