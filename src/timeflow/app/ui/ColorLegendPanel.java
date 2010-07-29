package timeflow.app.ui;

import timeflow.model.*;
import timeflow.app.ui.filter.FilterCategoryPanel;
import timeflow.data.db.*;
import timeflow.data.db.filter.FieldValueFilter;
import timeflow.data.db.filter.ValueFilter;
import timeflow.data.time.*;

import timeflow.util.*;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ColorLegendPanel extends ModelPanel {
	

	Field oldColor;

	public ColorLegendPanel(TFModel model)
	{
		super(model);
		setBackground(Color.white);
		setLayout(new GridLayout(1,1));
	}
	
	@Override
	public void note(TFEvent e) {
		Field color=getModel().getColorField();
		if (color!=null && color!=oldColor)
		{
				removeAll();
				final FilterCategoryPanel p=new FilterCategoryPanel("Color Legend: '"+color.getName()+"'", 
						color, this);
				add(p);
				Bag<String> data=DBUtils.countValues(getModel().getDB().all(), color);
				p.setData(data);
				p.dataList.addListSelectionListener(new ListSelectionListener() {				
					@Override
					public void valueChanged(ListSelectionEvent e) {
						ValueFilter f=(ValueFilter)p.defineFilter();	
						getModel().setGrayFilter(f, this);
					}
				});
				
				oldColor=color;
				revalidate();
				return;
		} else if (color==null)
		{
			removeAll();
		}
		repaint();
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200,400);
	}
}
