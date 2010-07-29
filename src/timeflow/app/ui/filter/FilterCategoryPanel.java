package timeflow.app.ui.filter;

import timeflow.util.*;

import javax.swing.*;

import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.model.ModelPanel;

import java.awt.*;
import java.awt.event.*;

public class FilterCategoryPanel extends FilterDefinitionPanel 
{
	public JList dataList=new JList();
	Field field;
	
	public FilterCategoryPanel(final Field field, final ModelPanel parent)
	{
		this(field.getName(), field, parent);
	}
	
	public FilterCategoryPanel(String title, final Field field, final ModelPanel parent)
	{
		this.field=field;
		setLayout(new BorderLayout());
		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

		add(new FilterTitle(title, field, parent, true), BorderLayout.NORTH);
		
		
		JScrollPane scroller=new JScrollPane(dataList);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(null);
		add(scroller, BorderLayout.CENTER);
		dataList.setForeground(Color.darkGray);
		dataList.setSelectionForeground(Color.black);
		dataList.setSelectionBackground(new Color(220,235,255));
		dataList.setFont(parent.getModel().getDisplay().small());
		scroller.getVerticalScrollBar().setBackground(Color.white);
		
		
		// ok, the following is ugly code to insert a new mouselistener
		// that lets the user deselect items when they are clicked.
		// i tried a bunch of stuff but this is all that would work--
		// and searching the web yielded only solutions similar to this.
		// also, there's a weird dance with consuming/not consuming events
		// that is designed to allow a certain kind of multi-selection behavior
		// with the mouse, while letting you scroll through items one at a time
		// with the keyboard. this was the product of a long series of
		// conversations with target users.
		MouseListener[] old = dataList.getMouseListeners();
		for (MouseListener m: old)
		   dataList.removeMouseListener(m);
		
		dataList.addMouseListener(new MouseAdapter()
		{
		   public void mousePressed(MouseEvent e)
		   {
			  if (e.isControlDown() || e.isMetaDown() || e.isShiftDown())
				  return;
		      final int index = dataList.locationToIndex(e.getPoint());
		      if (dataList.isSelectedIndex(index))
		      {
		         SwingUtilities.invokeLater(new Runnable()
		         {
		            public void run()
		            {
		               dataList.removeSelectionInterval(index, index);
		              
		            }
		         });
		         e.consume();
		      }
		      else
		      {
		    	  SwingUtilities.invokeLater(new Runnable()
			         {
			            public void run()
			            {
			            	dataList.addSelectionInterval(index, index);
			              
			            }
			         });		    	  
		    	  e.consume();
		      }
		   }
		});

		for (MouseListener m: old)
		   dataList.addMouseListener(m);
		
		dataList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component c=super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (field==parent.getModel().getColorField())
				{
					String text=value.toString();
					int n=text.lastIndexOf('-');
					if (n>1)
						text=text.substring(0,n-1);
					c.setForeground(parent.getModel().getDisplay().makeColor(text));
				}
				return c;
			}});
			
	}

	public void setData(Bag<String> data)
	{
		dataList.removeAll();
		java.util.List<String> items=data.list();
		String[] s=(String[])items.toArray(new String[0]);
		for (int i=0; i<s.length; i++)
		{
			int num=data.num(s[i]);
			if (s[i]==null || s[i].length()==0)
				s[i]="(missing)";
			s[i]+=" - "+num;
		}
		dataList.setListData(s);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200,200);
	}

	@Override
	public ActFilter defineFilter() {
		Object[] o=dataList.getSelectedValues();	
		if (o==null || o.length==0)
			return null;
		
		int n=o.length;
		String[] s=new String[n];
		for (int i=0; i<n; i++)
		{
			String w=(String)o[i];
			int m=w.lastIndexOf('-');
			s[i]=w.substring(0, m-1);
			if ("(missing)".equals(s[i]))
				s[i]="";
		}
		
		if (s.length==1)
			return new FieldValueFilter(field, s[0]);
		FieldValueSetFilter f=new FieldValueSetFilter(field);
		for (int i=0; i<s.length; i++)
			f.addValue(s[i]);
		return f;
	}

	@Override
	public void clearFilter() {
		dataList.clearSelection();
	}
	
}
