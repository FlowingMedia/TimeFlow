package timeflow.vis.timeline;

import java.awt.*;
import java.util.*;

import timeflow.data.time.Interval;
import timeflow.data.time.TimeUtils;
import timeflow.model.*;
import timeflow.vis.Mouseover;
import timeflow.vis.TimeScale;

public class AxisRenderer {
	
	TimelineVisuals visuals;
	
	public AxisRenderer(TimelineVisuals visuals)
	{
		this.visuals=visuals;
	}
	
	public void render(Graphics2D g, Collection<Mouseover> objectLocations)
	{		
		TFModel model=visuals.getModel();
		g.setColor(model.getDisplay().getColor("chart.background"));
		Rectangle bounds=visuals.getBounds();
		
		TimeScale scale=visuals.getTimeScale();
		java.util.List<AxisTicMarks> t=AxisTicMarks.allRelevant(scale.getInterval());
		
		int dateLabelH=model.getDisplay().getInt("timeline.datelabel.height");
		int y=bounds.y+bounds.height-dateLabelH;	
		
		// draw in reverse order so bigger granularity at top.
		int n=t.size();
		for (int i=0; i<n; i++)
		{
			render(t.get(i), g, bounds.x, y, dateLabelH-1, bounds.y, i==0, objectLocations);
			y-=dateLabelH;
		}
	}
	
	void render(AxisTicMarks t, Graphics2D g, int x, int y, int h, int top, boolean full, Collection<Mouseover> objectLocations)
	{
		TFModel model=visuals.getModel();

		int n=t.tics.size();
		for (int i=0; i<n-1; i++)
		{
			
			long start=t.tics.get(i);
			long end=t.tics.get(i+1);
			
			int x0=Math.max(x,visuals.getTimeScale().toInt(start));			
			int x1=visuals.getTimeScale().toInt(end);
			
			int dayOfWeek=TimeUtils.cal(start).get(Calendar.DAY_OF_WEEK);
			
			g.setColor(t.unit.isDayOrLess() && (dayOfWeek==1 || dayOfWeek==7) ? 
					new Color(245,245,245) : new Color(240,240,240));

			g.fillRect(x0, y, x1-x0-1, h);
			g.setColor(Color.white);
			g.drawLine(x1-1, y, x1-1, y+h);
			g.drawLine(x0,y+h,x1,y+h);
			objectLocations.add(new Mouseover(new Interval(start,end), x0, y, x1-x0-1, h));
			
			g.setFont(model.getDisplay().timeLabel());
			String label=full? t.unit.formatFull(start) : t.unit.format(new Date(start));
			int tx=x0+3;
			int ty=y+h-5;
			g.setColor(full ? Color.darkGray : Color.gray);
			int sw=model.getDisplay().timeLabelFontMetrics().stringWidth(label);
			if (sw<x1-tx-3)
				g.drawString(label, tx,ty);
			else
			{
				int c=label.indexOf(':');
				if (c>0)
				{
					label=label.substring(0,c);
					sw=model.getDisplay().timeLabelFontMetrics().stringWidth(label);
					if (sw<x1-tx-3)
						g.drawString(label, tx,ty);
				}
			}
		}
	}
}
