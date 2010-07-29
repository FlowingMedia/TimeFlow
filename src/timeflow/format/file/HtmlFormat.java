package timeflow.format.file;

import java.awt.Color;
import java.io.BufferedWriter;
import java.net.URL;

import timeflow.model.*;
import timeflow.data.db.*;

public class HtmlFormat implements Export
{
	TFModel model;
	java.util.List<Field> fields;
	Field title;
	
	public HtmlFormat() {}
	
	public HtmlFormat(TFModel model)
	{
		setModel(model);
	}
	
	public void setModel(TFModel model)
	{
		this.model=model;
		fields=model.getDB().getFields();
		title=model.getDB().getField(VirtualField.LABEL);
	}
	
	@Override
	public void export(TFModel model, BufferedWriter out) throws Exception {
		setModel(model);
		out.write(makeHeader());
		for (Act a: model.getDB())
			out.write(makeItem(a));
		out.write(makeFooter());
		out.flush();
	}
	
	public void append(ActList acts, int start, int end, StringBuffer b)
	{
		for (int i=start; i<end; i++)
		{
			Act a=acts.get(i);
			b.append(makeItem(a,i));
		}
	}
	
	private String makeItem(Act act)
	{
		return makeItem(act, -1);
	}
	
	public String makeItem(Act act, int id)
	{
		StringBuffer page=new StringBuffer();
		
		
		page.append("<tr><td valign=top align=left width=200><b>");
		if (title!=null)
		{
			Field f=model.getColorField();
			Color c=Color.black;
			if (f!=null)
			{
				if (f.getType()==String.class)
					c=model.getDisplay().makeColor(act.getString(f));
				else
				{
					String[] tags=act.getTextList(f);
					if (tags.length==0)
						c=Color.gray;
					else
						c=model.getDisplay().makeColor(tags[0]);
				}
			}
			
			page.append("<font size=+1 color="+htmlColor(c)+">"+act.getString(title)+"</font><br>");
		}
		
		Field startField=model.getDB().getField(VirtualField.START);
		
		if (startField!=null)
		{
			page.append("<font color=#999999>"+model.getDisplay().format(
				act.getTime(startField))+"</font>");
		}
		page.append("</b><br>");
		if (id>=0)
			page.append("<a href=\"e"+id+"\">EDIT</a>");
		page.append("<br></td><td valign=top>");
		for (Field f: fields)
		{
			page.append("<b><font color=#003399>"+f.getName()+"</font></b>&nbsp;&nbsp;");
			Object val=act.get(f);
			if (val instanceof URL)
			{
				page.append("<a href=\""+val+"\">"+val+"</a>");
			}
			else
				page.append(model.getDisplay().toString(val));
			page.append("<br>");

		}
		page.append("<br></td></tr>");

		return page.toString();
	}
	
	public String makeHeader()
	{
		StringBuffer page=new StringBuffer();
		page.append("<html><body><blockquote>");
		page.append("<br>File: "+model.getDbFile()+"<br>");
		page.append("Source: "+model.getDB().getSource()+"<br><br>");
		page.append("<br><br>");
		page.append("<table border=0>");

		return page.toString();
	}
	
	public String makeFooter()
	{
		return "</table></blockquote></body></html>";
	}
	
	
	static String htmlColor(Color c)
	{
		return '#'+hex2(c.getRed())+hex2(c.getGreen())+hex2(c.getBlue());
	}
	
	private static final String hexDigits="0123456789ABCDEF";
	private static String hex2(int n)
	{
		return hexDigits.charAt((n/16)%16)+""+hexDigits.charAt(n%16);
	}
	@Override
	public String getName() {
		return "HTML List";
	}

}
