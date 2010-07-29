package timeflow.views;

import timeflow.app.ui.HtmlDisplay;
import timeflow.data.analysis.*;
import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.format.field.FieldFormatCatalog;
import timeflow.model.*;


import java.awt.*;
import java.util.Date;

import javax.swing.*;


public class SummaryView extends AbstractView {
	
	private JEditorPane analysisDisplay;
	private FieldAnalysis[] fieldAnalyzers=new FieldAnalysis[]
    {
			new MissingValueAnalysis(), new RangeDateAnalysis(), 
			new RangeNumberAnalysis(), new FrequencyAnalysis()
    };
	private int numItems;
	private int numFiltered;
	private Interval range;
	private JComponent controls;
	
	public SummaryView(TFModel model)
	{
		super(model);
		analysisDisplay=HtmlDisplay.create();
		JScrollPane scrollPane = new JScrollPane(analysisDisplay);
		setLayout(new GridLayout(1,1));
		add(scrollPane);
		
		controls=new HtmlControls("This report gives a <br> statistical breakdown<br> "+
				"of your data. <p> Reading the summary often helps<br> you find "+
				"data errors.");
	}
	
	@Override
	public JComponent _getControls()
	{
		return controls;
	}
	
	void makeHtml()
	{
		Display d=getModel().getDisplay();
		ActDB db=getModel().getDB();
		ActList acts=getModel().getActs();
		StringBuffer page=new StringBuffer();
		page.append("<blockquote>");
		if (getModel().getDB()==null)
		{
			page.append("<h1><font color=#003399>No data loaded.</font></h1>");
		}
		else
		{
			page.append("<BR><BR><BR>File: "+getModel().getDbFile()+"<br>");
			page.append("Source: "+getModel().getDB().getSource()+"<br><br>");
			page.append("Description: "+getModel().getDB().getDescription()+"<br><br>");
			page.append("<br><br>");
			page.append("<table border=0>");
			
			
			page.append("<tr><td valign=top align=left width=100><b>");
			page.append("<font size=+1>Data</font><br>");
			
			page.append("</td><td align=top>");
			append(page, "Total events", ""+numItems);
			
			if (numItems>0)
			{
				append(page, "Total selected", ""+numFiltered);
				if (numFiltered>0)
				{
					append(page, "Earliest",  new Date(range.start).toString());
					append(page, "Latest",  new Date(range.end).toString());
				}
			}
			page.append("<br></td></tr>");
			
			page.append("<tr><td valign=top align=left width=100><b>");
			page.append("<font size=+1>Fields</font><br>");
			
			page.append("</td><td align=top>");
			for (Field f: getModel().getDB().getFields())
			{
					append(page, f.getName(),FieldFormatCatalog.humanName(f.getType())+fieldLabel(f));
			}
			page.append("<br></td></tr>");
			
				
			page.append("</table>");

			if (numFiltered>0)
			{
				page.append("<h1>Statistics (for "+acts.size()+" items)</h1>");
				for (Field field: db.getFields())
				{
					page.append("<h2>"+field.getName()+"</h2>");
					page.append("<ul>");
					for (int i=0; i<fieldAnalyzers.length; i++)
					{
						FieldAnalysis fa=fieldAnalyzers[i];
						if (fa.canHandleType(field.getType()))
						{
							page.append("<li>");
							page.append("<b><font color=#808080>"+fa.getName()+"</font></b><br>");
							fa.perform(acts, field);
							String[] s=fa.getResultDescription();
							for (int j=0; j<s.length; j++)
							{
								page.append(s[j]);
								page.append("<br>");
							}
							page.append("</li>");
						}
					}
					page.append("</ul>");
				}
			}
		}
		page.append("</blockquote>");
		analysisDisplay.setText(page.toString());
		analysisDisplay.setCaretPosition(0);
	}
	
	static void append(StringBuffer page, String label, String value)
	{
		page.append("<b><font color=#808080>"+label+"</font></b>&nbsp;&nbsp;&nbsp;&nbsp;");
		page.append(value);
		page.append("<br>");
	}
	
	@Override
	protected void onscreen(boolean majorChange)
	{
		_note(null);
	}

	protected void _note(TFEvent e) {
		recalculate();
		makeHtml();
		repaint();
	}
	
	String fieldLabel(Field f)
	{
		StringBuffer b=new StringBuffer("<b>");
		ActDB db=getModel().getDB();
		for (String v: VirtualField.list())
		{
			if (db.getField(v)!=null && db.getField(v).getName().equals(f.getName()))
				b.append(" (Shown in visualization as "+VirtualField.humanName(v)+")");
		}
		b.append("</b>");
		return b.toString();
	}
	
	void recalculate()
	{
		ActList acts=getModel().getActs();
		if (acts==null)
		{
			numItems=0;
			return;
		}
		numFiltered=acts.size();
		numItems=getModel().getDB().size();
		range=DBUtils.range(acts, VirtualField.START);
	}

	@Override
	public String getName() {
		return "Summary";
	}
}
