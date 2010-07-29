package timeflow.views;

import timeflow.app.ui.ComponentCluster;
import timeflow.data.db.*;
import timeflow.data.time.Interval;
import timeflow.data.time.RoughTime;
import timeflow.model.*;
import timeflow.vis.*;
import timeflow.vis.calendar.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

public class CalendarView extends AbstractView {

	CalendarPanel calendarPanel;
	ScrollingCalendar scroller;
	CalendarVisuals visuals;
	ActDB lastDB;
	JPanel controls;
	
	@Override
	public JComponent _getControls()
	{
		return controls;
	}
	
	public CalendarView(TFModel model)
	{
		super(model);
		calendarPanel=new CalendarPanel(model);
		scroller=new ScrollingCalendar();
		setLayout(new GridLayout(1,1));
		add(scroller);
		
		controls=new JPanel();
		controls.setLayout(new BorderLayout());

		ComponentCluster units=new ComponentCluster("Grid");
		controls.add(units, BorderLayout.NORTH);
		
		ButtonGroup unitGroup=new ButtonGroup();
		
		JRadioButton days=new JRadioButton(new ImageIcon("images/button_days.gif"),true);
		days.setSelectedIcon(new ImageIcon("images/button_days_selected.gif"));
		units.addContent(days);
		days.addActionListener(new LayoutSetter(CalendarVisuals.Layout.DAY));
		unitGroup.add(days);

		JRadioButton months=new JRadioButton(new ImageIcon("images/button_months.gif"),false);
		months.setSelectedIcon(new ImageIcon("images/button_months_selected.gif"));
		units.addContent(months);
		months.addActionListener(new LayoutSetter(CalendarVisuals.Layout.MONTH));
		unitGroup.add(months);
		
		JRadioButton years=new JRadioButton(new ImageIcon("images/button_years.gif"),false);
		years.setSelectedIcon(new ImageIcon("images/button_years_selected.gif"));
		units.addContent(years);
		years.addActionListener(new LayoutSetter(CalendarVisuals.Layout.YEAR));	
		unitGroup.add(years);
		
		
		ComponentCluster showCluster=new ComponentCluster("Show");
		controls.add(showCluster, BorderLayout.CENTER);
		
		ButtonGroup group=new ButtonGroup();
		JRadioButton icon=new JRadioButton(new ImageIcon("images/button_dots.gif"),true);
		icon.setSelectedIcon(new ImageIcon("images/button_dots_selected.gif"));
		showCluster.addContent(icon);
		icon.addActionListener(new DrawStyleSetter(CalendarVisuals.DrawStyle.ICON));
		group.add(icon);
		
		JRadioButton label=new JRadioButton(new ImageIcon("images/button_labels.gif"),false);
		label.setSelectedIcon(new ImageIcon("images/button_labels_selected.gif"));
		showCluster.addContent(label);
		label.addActionListener(new DrawStyleSetter(CalendarVisuals.DrawStyle.LABEL));						
		group.add(label);

		ComponentCluster layout=new ComponentCluster("Layout");
		controls.add(layout, BorderLayout.SOUTH);
		
		ButtonGroup layoutGroup=new ButtonGroup();
		JRadioButton loose=new JRadioButton(new ImageIcon("images/button_expanded.gif"),true);
		loose.setSelectedIcon(new ImageIcon("images/button_expanded_selected.gif"));
		layout.addContent(loose);
		loose.addActionListener(new FitStyleSetter(CalendarVisuals.FitStyle.LOOSE));
		layoutGroup.add(loose);
		
		JRadioButton tight=new JRadioButton(new ImageIcon("images/button_compressed.gif"),false);
		tight.setSelectedIcon(new ImageIcon("images/button_compressed_selected.gif"));
		layout.addContent(tight);
		tight.addActionListener(new FitStyleSetter(CalendarVisuals.FitStyle.TIGHT));
		layoutGroup.add(tight);
	}
	
	class LayoutSetter implements ActionListener
	{
		CalendarVisuals.Layout layout;
		LayoutSetter(CalendarVisuals.Layout layout)
		{
			this.layout=layout;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			setLayoutStyle(layout);
		}		
	}
	
	class DrawStyleSetter implements ActionListener
	{
		CalendarVisuals.DrawStyle style;
		DrawStyleSetter(CalendarVisuals.DrawStyle style)
		{
			this.style=style;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			setDrawStyle(style);
		}		
	}
	
	class FitStyleSetter implements ActionListener
	{
		CalendarVisuals.FitStyle style;
		FitStyleSetter(CalendarVisuals.FitStyle style)
		{
			this.style=style;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			setFitStyle(style);
		}		
	}
	
	@Override
	public String getName() {
		return "Calendar";
	}

	private void redraw(boolean fresh)
	{
		visuals.makeGrid(fresh);
		calendarPanel.drawVisualization();
		repaint();
	}

	void setLayoutStyle(CalendarVisuals.Layout layout)
	{
		visuals.setLayoutStyle(layout);
		calendarPanel.drawVisualization();
		revalidate();
		repaint();
	}

	void setDrawStyle(CalendarVisuals.DrawStyle style)
	{
		visuals.setDrawStyle(style);
		calendarPanel.drawVisualization();
		revalidate();
		repaint();
	}

	void setFitStyle(CalendarVisuals.FitStyle style)
	{
		visuals.setFitStyle(style);
		calendarPanel.drawVisualization();
		revalidate();
		repaint();
	}
	
	@Override
	protected void onscreen(boolean majorChange)
	{
		visuals.initAllButGrid();
		scroller.calibrate(true);
		revalidate();
		ActDB db=getModel().getDB();
		redraw(majorChange);
		scroller.calibrate(majorChange);
		lastDB=db;
	}
	
	@Override
	protected void _note(TFEvent e) {
		int oldHeight=calendarPanel.getPreferredSize().height;
		visuals.note(e);
		
		calendarPanel.drawVisualization();
		calendarPanel.repaint();
		if (e.affectsData() || oldHeight!=calendarPanel.getPreferredSize().height)
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {scroller.calibrate(false); revalidate();}
			});
		}
		revalidate();
	}
	
	public void setBounds(int x, int y, int w, int h)
	{
		super.setBounds(x,y,w,h);
		if (visuals==null || visuals.grid==null)
			return;
		calendarPanel.drawVisualization();
		calendarPanel.repaint();
	}
	
	class ScrollingCalendar extends JPanel
	{
		JScrollBar bar;
		public ScrollingCalendar()
		{
			setLayout(new BorderLayout());
			add(calendarPanel, BorderLayout.CENTER);
			bar=new JScrollBar(JScrollBar.VERTICAL);
			add(bar, BorderLayout.EAST);
			bar.addAdjustmentListener(new AdjustmentListener() {			
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					visuals.grid.setDY(bar.getValue());
					
					// set time in model.
					RoughTime startTime=visuals.grid.getFirstDrawnTime();
					Interval viewInterval=getModel().getViewInterval();
					if (viewInterval!=null)
					{
						viewInterval.translateTo(startTime.getTime());
					}
					
					calendarPanel.drawVisualization();
					calendarPanel.repaint();
				}
			});
		}
		
		public void setBounds(int x, int y, int w, int h)
		{
			if (x==getX() && y==getY() && w==getWidth() && h==getHeight())
				return;
			super.setBounds(x,y,w,h);
			calibrate(false);
		}
		
		void calibrate(boolean forceValue)
		{
			if (visuals==null || visuals.grid==null)
				return;
			int height=getSize().height;
			int desired=visuals.grid.getCalendarHeight();
			bar.setVisible(desired>height);
			if (desired>height)
			{
				bar.setMinimum(0);
				bar.setMaximum(desired);
				bar.setVisibleAmount(height);
				Interval view=getModel().getViewInterval();
				if (view!=null && forceValue)
				{					
					double s=visuals.grid.getScrollFraction();
					double maxFraction=(desired-height)/(double)desired;
					int value=(int)((s/maxFraction)*desired);
					bar.setValue(value);
				}
				
			}
		}
	}
	
	class CalendarPanel extends AbstractVisualizationView
	{		

		CalendarPanel(TFModel model)
		{
			super(model);
			setBackground(Color.white);
			visuals=new CalendarVisuals(getModel());
		}	

		public RoughTime getTime(Point p)
		{
			return visuals.grid.getTime(p.x, p.y); 
		}
		
		protected void drawVisualization(Graphics2D g)
		{
			g.setBackground(Color.white);
			g.fillRect(0,0,getSize().width, getSize().height);

			getModel().getDisplay().emptyMessage(g, model);
			if (model.getDB()==null)
				return;
			visuals.setBounds(0,0,getSize().width,getSize().height);	
			objectLocations=new ArrayList<Mouseover>();
			visuals.render(g, objectLocations);
		}
	}
}
