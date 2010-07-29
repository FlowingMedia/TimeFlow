package timeflow.vis.timeline;

import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.model.*;
import timeflow.vis.TimeScale;
import timeflow.vis.VisualAct;
import timeflow.vis.timeline.*;

import timeflow.util.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class TimelineSlider extends ModelPanel {
	
	TimelineVisuals visuals;
	Interval original;
	long minRange;
	int ew=10;
	int eventRadius=2;
	TimeScale scale;
	
	Point mouseHit=new Point();
	Point mouse=new Point(-1,0);
	enum Modify {START, END, POSITION, NONE};
	Modify change=Modify.NONE;
	Rectangle startRect=new Rectangle(-1,-1,0,0);
	Rectangle endRect=new Rectangle(-1,-1,0,0);
	Rectangle positionRect=new Rectangle(-1,-1,0,0);
	Color sidePlain=Color.orange;
	Color sideMouse=new Color(230,100,0);

	
	public TimelineSlider(final TimelineVisuals visuals, final long minRange, final Runnable action)
	{
		super(visuals.getModel());
		
		this.minRange=minRange;
		this.visuals=visuals;
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				int mx=e.getX();
				int my=e.getY();
				if (positionRect.contains(mx,my))
					change=Modify.POSITION;
				else if (startRect.contains(mx, my))
					change=Modify.START;
				else if (endRect.contains(mx,my))
					change=Modify.END;
				else
					change=Modify.NONE;
				mouseHit.setLocation(mx,my);
				original=window().copy();
				mouse.setLocation(mx,my);
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				change=Modify.NONE;
				repaint();
			}});
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (change==Modify.NONE)
					return;
				mouse.setLocation(e.getX(), e.getY());
				int mouseDiff=mouse.x-mouseHit.x;
				Interval limits=visuals.getGlobalInterval();
				long timeDiff=scale.spaceToTime(mouseDiff);
				
				switch (change)
				{
					case POSITION: 						
							window().translateTo(original.start+timeDiff); 
							window().clampInside(limits);
						break;
					case START: window().start=Math.min(original.start+timeDiff, original.end-minRange); 
								window().start=Math.max(window().start, limits.start);
						break;
					case END: window().end=Math.max(original.end+timeDiff, original.start+minRange);
							  window().end=Math.min(window().end, limits.end);
				}
				getModel().setViewInterval(window());
				action.run();
				repaint();
			}
		});
	}
	
	private Interval window()
	{
		return visuals.getViewInterval();
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(600,30);
	}
	
	public void setMinRange(long minRange)
	{
		this.minRange=minRange;
	}

	@Override
	public void note(TFEvent e) {
		repaint();
	}
	
	void setTimeInterval(Interval interval)
	{
		window().setTo(interval);
		repaint();
	}
	
	public void paintComponent(Graphics g1)
	{
		int w=getSize().width, h=getSize().height;
		Graphics2D g=(Graphics2D)g1;
		
		long start=System.currentTimeMillis();
		
		// draw main backdrop.
		g.setColor(Color.white);
		g.fillRect(0,0,w,h);
		
		if (visuals.getModel()==null || visuals.getModel().getActs()==null)
		{
			g.setColor(Color.darkGray);
			g.drawString("No data for timeline.", 5, 20);
			return;
		}
		
		scale=new TimeScale();
		scale.setDateRange(visuals.getGlobalInterval());
		scale.setNumberRange(ew, w-ew);
		
		
		// draw the area for the central "thumb".
		int lx=scale.toInt(window().start);
		int rx=scale.toInt(window().end);
		g.setColor(change==Modify.POSITION ? new Color(255,255,120) : new Color(255,245,200));
		positionRect.setBounds(lx,0,rx-lx,h);
		g.fill(positionRect);		

		// Figure out how best to draw events.
		// If there are too many, we just draw a kind of histogram of frequency,
		// rather than using the timeline layout.
		int slotW=2*eventRadius;
		int slotNum=w/slotW+1;
		int[] slots=new int[slotNum];
		int mostInSlot=0;
		for (VisualAct v: visuals.getVisualActs())
		{
			if (!v.isVisible())
				continue;
			int x=scale.toInt(v.getStart().getTime());
			int s=x/slotW;
			if (s>=0 && s<slotNum)
			{
				slots[s]++;
				mostInSlot=Math.max(mostInSlot, slots[s]);
			}
		}
		if (mostInSlot>30)
		{
			g.setColor(Color.gray);
			for (int i=0; i<slots.length; i++)
			{
				int sh=(h*slots[i])/mostInSlot;
				g.fillRect(slotW*i, h-sh, slotW, sh);
			}
		}
		else
		{
			// draw individual events.
			for (VisualAct v: visuals.getVisualActs())
			{
				if (!v.isVisible())
					continue;
				g.setColor(v.getColor());
				int x=scale.toInt(v.getStart().getTime());
	
				int y=eventRadius+(int)(v.getY()*h)/(visuals.getBounds().height-2*eventRadius);
				g.fillRect(x-1,y-eventRadius,2*eventRadius,3);
				if (v.getEnd()!=null)
				{
					int endX=scale.toInt(v.getEnd().getTime());
					g.drawLine(x,y,endX,y);
				}
			}
		}
		
		g.setColor(Color.gray);
		g.drawLine(0,0,w,0);
		g.drawLine(0,h-1,w,h-1);
		
		// draw "expansion" areas on sides of thumb.
		startRect.setBounds(positionRect.x-ew,1,ew,h-2);
		g.setColor(change==Modify.START ? sideMouse : sidePlain);
		g.fill(startRect);
		endRect.setBounds(positionRect.x+positionRect.width,1,ew,h-2);
		g.setColor(change==Modify.END ? sideMouse : sidePlain);
		g.fill(endRect);
	}
}
