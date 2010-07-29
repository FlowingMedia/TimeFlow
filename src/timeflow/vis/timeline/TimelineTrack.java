package timeflow.vis.timeline;

import timeflow.vis.VisualAct;
import timeflow.data.time.*;
import timeflow.util.*;

import java.util.*;

public class TimelineTrack implements Comparable
{
	String label;
	List<VisualAct> visualActs=new ArrayList<VisualAct>();
	int y0, y1;
	DoubleBag<Long> histogram;
	
	TimelineTrack(String label)
	{
		this.label=label;		
	}
	
	void add(VisualAct v)
	{
		visualActs.add(v);
	}
	
	int size()
	{
		return visualActs.size();
	}
	
	// assumes a>b>0
	int gcd(int a, int b)
	{
		int mod=a%b;
		if (a%b==0)
			return b;
		return gcd(b, mod);
	}
	
	int nearAndRelPrime(int target, int modulus)
	{
		if (target<2)
			return 1;
		while (gcd(modulus, target)>1)
			target--;
		return target;
	}
	
	// top and height are in proportion of total height of frame.
	void layout(double top, double height, TimelineVisuals visuals)
	{
		int n=visualActs.size();
		if (n==0)
			return;
		int labelHeight=80;
		int fh=visuals.getBounds().height-labelHeight;
		int fy=visuals.getBounds().y;
		int cellH=visuals.getModel().getDisplay().getInt("timeline.item.height.min");
		
		y0=fy+(int)(fh*top);
		y1=fy+(int)(fh*(top+height));
		int mid=(y0+y1)/2;
		int iy0=Math.min(y0+5, mid);
		int iy1=Math.max(y1-15,mid);
		
		int numCells=Math.max(1,(iy1-iy0)/cellH);
		
		VisualAct[] rights=new VisualAct[numCells];
		
		int step=nearAndRelPrime((int)(.61803399*numCells), numCells);
		int i=0;
		VisualAct last=null;
		for (VisualAct v: visualActs)
		{
			if (!v.isVisible() || !v.getTrack().equals(this))
				continue;
			v.setSpaceToRight(1000);

			double num=visuals.getTimeScale().toNum(v.getStart().getTime());
			int x=(int)num;
			
			int cell=numCells<2 ? 0 : (i%numCells);
			int y=iy0 +  (iy1-iy0<12 ? 0 : cell*cellH);
			v.setX(x);
			v.setY(y+11);
			
			if (v.getEnd()!=null)
				v.setEndX((int)visuals.getTimeScale().toNum(v.getEnd().getTime()));
			
			if (rights[cell]!=null)
			{
				int space=x-rights[cell].getX();
				rights[cell].setSpaceToRight(space);
			}
			rights[cell]=v;
			if ((last!=null && v.getStart().getTime()==last.getStart().getTime())
				|| visuals.getLayoutStyle()==TimelineVisuals.Layout.TIGHT)
				i++;
			else 
				i+=step;
			last=v;
		}
	}

	@Override
	public int compareTo(Object o) {
		return ((TimelineTrack)o).size()-size();
	}
}
