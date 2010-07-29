package timeflow.app.ui.filter;

import javax.swing.*;

import timeflow.data.time.Interval;
import timeflow.model.Display;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.*;


public class BabyHistogram extends JPanel {
	private int[] buckets;
	private double[] x;
	private double min, max;
	private int numDefined;
	private int maxBucket;
	private double value;
	private static final DecimalFormat df=new DecimalFormat("###,###,###,###.##");
	
	
	Point mouseHit=new Point();
	Point mouse=new Point(-1,0);
	enum Modify {START, END, POSITION, NONE};
	Modify change=Modify.NONE;
	Rectangle startRect=new Rectangle(-1,-1,0,0);
	Rectangle endRect=new Rectangle(-1,-1,0,0);
	Rectangle positionRect=new Rectangle(-1,-1,0,0);
	Color sidePlain=Color.orange;
	Color sideMouse=new Color(230,100,0);
	double relLow=0, relHigh=1, originalLow, originalHigh;
	
	public void setRelRange(double relLow, double relHigh)
	{
		this.relLow=Math.max(0,relLow);
		this.relHigh=Math.min(1,relHigh);
		repaint();
	}

	public void setTrueRange(double low, double high)
	{
		double span=max-min;
		if (span<=0) // nothing much to do...
			return;
		
		setRelRange((low-min)/span, (high-min)/span);	
	}
	
	public boolean isEverything()
	{
		return relLow==0 && relHigh==1;
	}
	
	public double getLow()
	{
		return abs(relLow);
	}
	
	public double getHigh()
	{
		return abs(relHigh);
	}
	
	private double abs(double x)
	{
		return x*max+(1-x)*min;
	}

	public BabyHistogram(final Runnable changeAction)
	{
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				int mx=e.getX();
				int my=e.getY();
				int lx=lowX(), hx=highX();
			
				int ox=0;
				if (Math.abs(mx-lx)<Math.abs(mx-hx))
				{
					change=Modify.START;
					ox=lx;
				}
				else
				{
					change=Modify.END;
					ox=hx;
				}
				mouseHit.setLocation(ox,my);
				mouse.setLocation(mx,my);
				originalLow=relLow;
				originalHigh=relHigh;
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
				double relDiff=mouseDiff/(double)getSize().width;
				switch (change)
				{
					case POSITION: 						
							relLow=originalLow+relDiff;
							relHigh=originalHigh+relDiff;

						break;
					case START: relLow=originalLow+relDiff;
						break;
					case END: relHigh=originalHigh+relDiff;
				}
				relLow=Math.max(0, relLow);
				relHigh=Math.min(1, relHigh);
				changeAction.run();
				repaint();
			}
		});

	}
	
	public void setData(double[] x)
	{
		relLow=0;
		relHigh=1;
		
		this.x=x;
		int n=x.length;
		
		// do some quick checks on the data.
		boolean positive=true;
		min=Double.NaN;
		max=Double.NaN;
		numDefined=0;
		for (int i=0; i<n; i++)
		{
			double m=x[i];
			if (!Double.isNaN(m))
			{
				numDefined++;
				positive &= m>0;
				if (Double.isNaN(min))
				{
					min=m;
					max=m;
					value=m;
				}
				else
				{
					min=Math.min(m, min);
					max=Math.max(m, max);
				}
			}
		}
		
		if (numDefined==0)
			return;
		if (min==max)
		{
			buckets=new int[1];
			buckets[0]=numDefined;
			maxBucket=numDefined;
			return;
		}
		int numBuckets=(int)Math.min(50, 2*Math.sqrt(numDefined));
		buckets=new int[numBuckets];
		maxBucket=0;
		for (int i=0; i<n; i++)
		{
			if (!Double.isNaN(x[i]))
			{
				int b=(int)((numBuckets-1)*(x[i]-min)/(max-min));
				buckets[b]++;
				maxBucket=Math.max(maxBucket, buckets[b]);
			}
		}
	}
	
	public void paintComponent(Graphics g)
	{
		int w=getSize().width;
		int h=getSize().height;
		g.setColor(Color.white);
		g.fillRect(0,0,w,h);
		
		if (x==null)
		{			
			say(g, "No data");
			return;
		}
		
		if (x.length==0)
		{
			say(g, "No values");
			return;
		}
		
		if (numDefined==0)
		{
			say(g, "No defined values");
			return;
		}
		
		int n=buckets.length;
		if (n==1)
		{
			say(g, "All defined vals = "+df.format(value));
			return;
		}
		
		// wow, if we got here we really have a histogram and not a degenerate mess!
		
		Color bar=Display.barColor;
		g.setColor(bar);
		for (int i=0; i<n; i++)
		{
			int x1=(i*w)/n;
			int x2=((i+1)*w)/n;
			int y1=h-(buckets[i]*h)/maxBucket;
			if (buckets[i]>0 && y1>h-2)
				y1=h-2;
			g.fillRect(x1,y1,x2-x1-1,h-y1);
		}
		
		// now draw thumb.
		
		int thumb1=lowX();
		int thumb2=highX();
		g.setColor(Color.black);
		g.drawLine(thumb1,0,thumb1,h);
		g.drawLine(thumb2,0,thumb2,h);
		g.setColor(new Color(235,235,235,160));
		g.fillRect(0,0,thumb1,h);
		g.fillRect(thumb2+1,0,w-thumb2-1,h);
		g.setColor(Color.lightGray);
		g.drawRect(0,0,w-1,h-1);
	}
	
	int lowX()
	{
		return (int)((getSize().width-1)*relLow);
	}
	
	int highX()
	{
		return (int)((getSize().width-1)*relHigh);
	}
	
	void say(Graphics g, String s)
	{
		g.setColor(Color.gray);
		g.drawString(s,5,getSize().height-5);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200,60);
	}
}
