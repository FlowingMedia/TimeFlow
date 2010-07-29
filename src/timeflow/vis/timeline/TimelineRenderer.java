package timeflow.vis.timeline;


import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.model.*;
import timeflow.vis.Mouseover;
import timeflow.vis.MouseoverLabel;
import timeflow.vis.VisualAct;

import timeflow.util.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

public class TimelineRenderer {

	private TimelineVisuals visuals;
	private int dy;
	
	public TimelineRenderer(TimelineVisuals visuals)
	{
		this.visuals=visuals;
	}
	
	public void setDY(int dy)
	{
		this.dy=dy;
	}

	public void render(Graphics2D g, Collection<Mouseover> objectLocations)
	{
		AffineTransform old=g.getTransform();
		g.setTransform(AffineTransform.getTranslateInstance(0, -dy));
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		TFModel model=visuals.getModel();
		Display display=model.getDisplay();
		ActDB db=model.getDB();

		if (display.emptyMessage(g, model))
			return;
		
		// need to check this, because resize events don't (and shouldn't) register with central TFModel.
		visuals.layoutIfChanged();
		
		java.util.List<VisualAct> visualActs=visuals.getVisualActs();
		
		if (visualActs==null || visualActs.size()==0)
		{
			g.drawString("No data",10,30);
			return;
		}
		
		Rectangle bounds=visuals.getBounds();
		boolean colorTrackLabels=db.getField(VirtualField.COLOR)==null || db.getField(VirtualField.COLOR).equals(db.getField(VirtualField.TRACK));

		// draw tracks, if more than 1.
		if (visuals.trackList.size()>1)
		{
			boolean zebra=false;

			for (TimelineTrack t: visuals.trackList)
			{
				if (zebra)
				{
					g.setColor(display.getColor("timeline.zebra"));
					g.fillRect(bounds.x,t.y0,bounds.width,t.y1-t.y0);
				}
				zebra=!zebra;
				g.setColor(display.getColor("timeline.grid"));
				g.drawLine(bounds.x,t.y0,bounds.x+bounds.width,t.y0);
			}
		}
		
		Interval screenInterval=visuals.getViewInterval().subinterval(-.5,1.5);
		AxisTicMarks tics=AxisTicMarks.histoTics(screenInterval.start, screenInterval.end);
		for (TimelineTrack t: visuals.trackList)
		{
			// now... if not in graph mode, just draw items
			if (visuals.getLayoutStyle()!=TimelineVisuals.Layout.GRAPH)//max<(t.y1-t.y0)/20)
			{
				for (VisualAct v: t.visualActs)
				{
					Mouseover o=v.draw(g, null, bounds, display, true, true);
					if (o!=null)
					{
						o.y-=dy;
						objectLocations.add(o);	
					}
				}
				continue;
			}

			// draw bars. to do so, we make a histogram of visible items.
			t.histogram=new DoubleBag<Long>();
			for (VisualAct v: t.visualActs)
			{
				long time=v.getStart().getTime();
				if (screenInterval.contains(time))
				{
					t.histogram.add(tics.unit.roundDown(v.getStart().getTime()).getTime(), 1);//v.getSize());
				}
			}
			
			// get max of items.
			double max=t.histogram.getMax();
			
			// now draw bars on screen.
			Color fg=colorTrackLabels ? model.getDisplay().makeColor(t.label) : Color.gray;
			if (visuals.trackList.size()<2)
				fg=Color.gray;
			
			List<Long> keys=t.histogram.unordered();
			Collections.sort(keys);
			for (Long r: keys)
			{
				double num=t.histogram.num(r);
				int x1=visuals.getTimeScale().toInt(r);
				int x2=visuals.getTimeScale().toInt(tics.unit.roundUp(r+1).getTime());
				int barY=t.y1-(int)(.9*((t.y1-t.y0)*num)/max);
				g.setColor(new Color(230,230,230));
				int m=12;
				g.fillRoundRect(x1+3,barY+3,x2-x1-1,t.y1-barY, m, m);
				
				g.setColor(fg);
				g.fillRoundRect(x1,barY,x2-x1-1,t.y1-barY, m, m);
				
				MouseoverLabel mouse=new MouseoverLabel(""+Math.round(num), "items",x1,barY,x2-x1-1,t.y1-barY);
				objectLocations.add(mouse);
			}
		}
		
		// finally label the tracks. we do this last so that the labels go on top of the data.
		
		if (visuals.trackList.size()>1)
		{
			boolean zebra=false;
			FontMetrics hugeFm=display.hugeFontMetrics();
			for (TimelineTrack t: visuals.trackList)
			{				
				
				// now label the track.
				if (t.y1-t.y0>23)
				{				
					Color fg=colorTrackLabels ? model.getDisplay().makeColor(t.label) : Color.darkGray;
					Color bg=zebra ? display.getColor("timeline.zebra") : Color.white;
					
					String label=t.label;
					if (label.equals(Display.MISC_CODE))
					{
						label=display.getMiscLabel();
					}
					else if (label.length()==0)
					{
						label=display.getNullLabel();
					}
					else
						label=display.format(label, 20, false);
					
					// draw background.
					g.setColor(bg);
					int sw=hugeFm.stringWidth(label);
					g.fillRect(0,t.y1-20,sw+8,19);
					
					// draw foreground (actual label)	
					g.setFont(display.huge());
					g.setColor(fg);
					g.drawString(label, 2, t.y1-5);
				}
				zebra=!zebra;
			}
		}
		g.setTransform(old);
	}
}
