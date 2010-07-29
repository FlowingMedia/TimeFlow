package timeflow.views;

import timeflow.app.ui.EditRecordPanel;
import timeflow.app.ui.HtmlDisplay;
import timeflow.data.db.*;
import timeflow.format.file.HtmlFormat;
import timeflow.model.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.html.*;

import timeflow.util.*;

import java.net.URI;
import java.net.URL;
import java.util.*;

public class ListView extends AbstractView {

	private JEditorPane listDisplay;
	private JComboBox sortMenu=new JComboBox();
	private ActComparator sort;//=ActComparator.byTime();
	private int maxPerPage=50;
	private int pageStart=0;
	private int lastSize=0;
	private ActList acts;
	private Field sortField;
	
	private JLabel pageLabel=new JLabel("Page", JLabel.LEFT);
	private JComboBox pageMenu=new JComboBox();
	private boolean changing=false;
	
	private JPanel controls;
	
	public ListView(TFModel model)
	{
		super(model);
		
		listDisplay=HtmlDisplay.create();
		listDisplay.addHyperlinkListener(new LinkIt());		
		JScrollPane scrollPane = new JScrollPane(listDisplay);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
		
		controls=new JPanel();
		controls.setLayout(null);
		controls.setBackground(Color.white);
		
		int x=10, y=10;
		int ch=25, pad=5, cw=160;
		JLabel sortLabel=new JLabel("Sort Order", JLabel.LEFT);
		controls.add(sortLabel);
		sortLabel.setBounds(x,y,cw,ch);
		y+=ch+pad;
		
		controls.add(sortMenu);	
		sortMenu.setBounds(x,y,cw,ch);
		y+=ch+3*pad;
		
		controls.add(pageLabel);
		pageLabel.setBounds(x,y,cw,ch);
		y+=ch+pad;
		controls.add(pageMenu);
		pageMenu.setBounds(x,y,cw,ch);
		
		showPageMenu(false);
		pageMenu.addActionListener(pageListener);				
		sortMenu.addActionListener(sortListener);
	}
	
	protected JComponent _getControls()
	{
		return controls;
	}
	
	ActionListener sortListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (changing || sortMenu.getItemCount()<=0) // this means the action was fired after all items removed.
				return;
			sortField=getModel().getDB().getField((String)sortMenu.getSelectedItem());
			sort=sortField==null ? null : ActComparator.by(sortField);
			setToFirstPage();
			makeList();
		}};
	
	ActionListener pageListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (changing)
				return;
			pageStart=maxPerPage*pageMenu.getSelectedIndex();
			System.out.println(e.getActionCommand());
			makeList();
		}};
	
	@Override
	protected void onscreen(boolean majorChange)
	{
		_note(null);
	}
	
	public void _note(TFEvent e) {
		changing=true;
		if (e==null || e.affectsSchema() || e.affectsRowSet())
		{
			sortMenu.removeActionListener(sortListener);
			sortMenu.removeAllItems();
			pageStart=0;
			java.util.List<Field> fields=getModel().getDB().getFields();
			Field firstField=null;
			if (fields.size()>0)
				firstField=fields.get(0);
			for (Field f: fields)
			{
				sortMenu.addItem(f.getName());
			}
			sortField=getModel().getDB().getField(VirtualField.START);
			if (sortField!=null)
				sortMenu.setSelectedItem(sortField.getName());
			else
				sortField=firstField;
			sortMenu.addActionListener(sortListener);
			sort=null;
		}
		if (e!=null && e.affectsData())
		{
			setToFirstPage();
		}
		changing=false;
		makeList();
	}
	
	private void setToFirstPage()
	{
		pageStart=0;
		if (pageMenu.isVisible())
		{
			pageMenu.removeActionListener(pageListener);
			pageMenu.setSelectedIndex(0);
			pageMenu.addActionListener(pageListener);
		}
	}
	
	void showPageMenu(boolean visible)
	{
		pageLabel.setVisible(visible);
		pageMenu.setVisible(visible);
		if (visible)
		{
			pageMenu.removeActionListener(pageListener);
			pageMenu.setSelectedIndex(pageStart/maxPerPage);
			pageMenu.addActionListener(pageListener);
		}
	}
	
	
	void makeList()
	{
		HtmlFormat html=new HtmlFormat();
		html.setModel(getModel());
		StringBuffer page=new StringBuffer();
		
		page.append(html.makeHeader());
		
		
		ActList as=getModel().getActs();
		if (as==null || as.size()==0 && getModel().getDB().size()==0)
		{
			page.append("<tr><td><h1><font color=#003399>Empty Database</font></h1></td></tr>");
			showPageMenu(false);
		}
		else
		{
			
			if (sort==null)
			{
				Field timeField=getModel().getDB().getField(VirtualField.START);
				if (timeField!=null)			
					sort=ActComparator.by(timeField);
			}

			acts=as.copy();
			if (sort!=null)
				Collections.sort(acts, sort);
			
			boolean pages=acts.size()>maxPerPage;
			int last=Math.min(acts.size(), pageStart+maxPerPage);
			if (pages)
			{
				int n=acts.size();
				if (lastSize!=n)
				{
					pageMenu.removeActionListener(pageListener);
					pageMenu.removeAllItems();
					for (int i=0; i*maxPerPage<n;i++)
					{
						pageMenu.addItem("Items "+((i*maxPerPage)+1)+" to "+
								Math.min(n, (i+1)*maxPerPage));
					}
					pageMenu.addActionListener(pageListener);
					lastSize=n;
				}
			}
			showPageMenu(pages);
			
			page.append("<tr><td><h1><font color=#003399>"+(pages? (pageStart+1)+"-"+(last) +" of ": "")+acts.size()+" Events</font></h1>");
			page.append("<br><br></td></tr>");

			for (int i=pageStart; i<last; i++)
			{
				Act a=acts.get(i);
				page.append(html.makeItem(a,i));
			}
		}
		page.append(html.makeFooter());
		listDisplay.setText(page.toString());
		listDisplay.setCaretPosition(0);
		repaint();
	}
	
	
	
	
	@Override
	public String getName() {
		return "List";
	}
	
	static class ArrayRenderer extends DefaultTableCellRenderer {
	    public void setValue(Object value) {
	    	setText(Display.arrayToString((Object[])value));
	    }
	}
	
	public class LinkIt implements HyperlinkListener 
	{
		public void hyperlinkUpdate(HyperlinkEvent e) 
		{
			if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) 
				return;
			
			String s=e.getDescription();
			System.out.println(s);
			if (s.length()>0)
			{
				char c=s.charAt(0);
				if (c=='e') // code for "edit"
				{
					int i=Integer.parseInt(s.substring(1));
					EditRecordPanel.edit(getModel(), acts.get(i));
					return;
				}
				
			}
			Display.launchBrowser(e.getURL().toString());
			
		} 
	} 
}
