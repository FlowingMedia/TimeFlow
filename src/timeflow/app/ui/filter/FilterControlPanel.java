package timeflow.app.ui.filter;

import timeflow.model.*;
import timeflow.app.ui.StatusPanel;
import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.data.time.RoughTime;

import timeflow.util.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;

public class FilterControlPanel extends ModelPanel
{
	FacetSubpanel inside=new FacetSubpanel();
	SearchPanel searchPanel;
	boolean inverted=false;
	JMenu menuToSyncWith;
	
	
	public FilterControlPanel(TFModel model, JMenu menuToSyncWith)
	{
		super(model);
		this.menuToSyncWith=menuToSyncWith;
		searchPanel=new SearchPanel(model, this);
		setLayout(new BorderLayout());
		setBackground(Color.white);
		JPanel top=new JPanel();
		top.setBackground(Color.white);
		top.setLayout(new BorderLayout());
		top.setBackground(Color.white);
		
		top.add(new StatusPanel(model, this), BorderLayout.NORTH);
		top.add(searchPanel, BorderLayout.CENTER);
	
		add(top, BorderLayout.NORTH);
		add(inside, BorderLayout.CENTER);
	}
	
	void setInverted(boolean inverted)
	{
		this.inverted=inverted;
		makeFilter();
	}
	
	void makeFilter()
	{
		AndFilter filter=new AndFilter();
		String s=searchPanel.entry.getText();
		if (s.length()>0)
			filter.and(new StringMatchFilter(getModel().getDB(), s, true));
		for (FilterDefinitionPanel f: inside.facetTable.values())
			filter.and(f.defineFilter());
		getModel().setFilter(inverted ? new NotFilter(filter) : filter, this);
	}
	
	public void clearFilters()
	{

		searchPanel.entry.setText("");
		for (FilterDefinitionPanel d: inside.facetTable.values())
			d.clearFilter();
		inverted=false;
		searchPanel.invert.setSelected(false);
		for (Field f:getModel().getDB().getFields())
		{
			inside.setFacet(f, false);
		}
		makeFilter();
	}

	@Override
	public void note(TFEvent e) {
		if (e.affectsSchema())
		{
			inside.clearFacets();
			searchPanel.entry.setText("");
		}
	}
	
	public void setFacet(Field field, boolean on)
	{
		inside.setFacet(field, on);
		makeFilter();
	}
	
	class FacetSubpanel extends JPanel
	{	
		
		ArrayList<Field> facets=new ArrayList<Field>();
		HashMap<Field, FilterDefinitionPanel> facetTable=new HashMap<Field, FilterDefinitionPanel>();		
		
		FacetSubpanel()
		{
			setLayout(null);
			setBackground(Color.white);
		}
		
		FilterDefinitionPanel makePanel(Field field)
		{
			if (field.getType()==Double.class)
			{
				final FilterNumberPanel num=new FilterNumberPanel(field, new Runnable() {
					@Override
					public void run() {
						makeFilter();
					}}, FilterControlPanel.this);
				num.setData(DBUtils.getValues(getModel().getDB(), field));
				return num;
			}
			
			if (field.getType()==RoughTime.class)
			{
				final FilterDatePanel date=new FilterDatePanel(field, new Runnable() {
					@Override
					public void run() {
						makeFilter();
					}}, FilterControlPanel.this);
				date.setData(DBUtils.getValues(getModel().getDB(), field));
				return date;
			}
			
			final FilterCategoryPanel p= new FilterCategoryPanel(field, FilterControlPanel.this);
			p.dataList.addListSelectionListener(new ListSelectionListener() {				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					makeFilter();	
				}
			});
			Bag<String> data=DBUtils.countValues(getModel().getDB().all(), field);
			p.setData(data);
			return p;
		}
		
		public void clearFacets()
		{
			removeAll();
			facets.clear();
			facetTable.clear();
			revalidate();
			repaint();
		}
		
		public void setFacet(Field field, boolean on)
		{
			FilterDefinitionPanel panel=facetTable.get(field);
			if (on == (panel!=null))
				return;
			if (on)
			{
				panel=makePanel(field);
				add(panel);
				facets.add(field);
				facetTable.put(field,panel);
			}
			else
			{
				remove(panel);
				facets.remove(field);
				facetTable.remove(field);
			}
			
			doFacetLayout();
			if (menuToSyncWith!=null)
				for (int i=0; i<menuToSyncWith.getItemCount(); i++)
				{
					JCheckBoxMenuItem item=(JCheckBoxMenuItem)menuToSyncWith.getItem(i);
					if (item.getText().equals(field.getName()))
					{
						item.setSelected(on);
					}
				}
			revalidate();
			repaint();
		}
		
		public void setBounds(int x, int y, int w, int h)
		{
			super.setBounds(x,y,w,h);
			doFacetLayout();
		}
		
		void doFacetLayout()
		{
			int w=getSize().width, h=getSize().height;
			int goodSize=0;
			for (Field f: facets)
			{
				FilterDefinitionPanel p=facetTable.get(f);
				goodSize+=p.getPreferredSize().height;
			}
			int top=0;
			for (Field f: facets)
			{
				FilterDefinitionPanel p=facetTable.get(f);
				int pref=p.getPreferredSize().height;
				int panelHeight=(goodSize<= h ? pref : (pref*h)/goodSize);
				p.setBounds(0,top,w,panelHeight);
				top+=panelHeight;
			}
		}
	}
}
