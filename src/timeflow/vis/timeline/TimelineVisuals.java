package timeflow.vis.timeline;

import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.data.time.*;
import timeflow.model.*;
import timeflow.vis.*;

import java.util.*;
import java.awt.*;

/*
 * A VisualEncoding takes the info about which fields to translate to
 * which visual aspects, and applies that to particular Acts.
 */
public class TimelineVisuals {
	
	private Map<String, TimelineTrack> trackTable=new HashMap<String, TimelineTrack>();
	ArrayList<TimelineTrack> trackList=new ArrayList<TimelineTrack>();
	private TimeScale timeScale=new TimeScale();
	private Rectangle bounds=new Rectangle();
	private boolean frameChanged;
	private int numShown=0;

	private Interval globalInterval;
	
	public enum Layout {TIGHT, LOOSE, GRAPH};
	private Layout layoutStyle=Layout.LOOSE;
	
	private VisualEncoder encoder;
	private TFModel model;
	private int fullHeight;

	public int getFullHeight()
	{
		return fullHeight;
	}
	
	public TimelineVisuals(TFModel model)
	{
		this.model=model;
		encoder=new VisualEncoder(model);
	}
	
	public TimeScale getTimeScale()
	{
		return timeScale;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(int x, int y, int w, int h) {
		bounds.setBounds(x,y,w,h);
		timeScale.setLow(x);
		timeScale.setHigh(x+w);
		frameChanged=true;
	}

	public Layout getLayoutStyle()
	{
		return layoutStyle;
	}
	
	public void setLayoutStyle(Layout style)
	{
		layoutStyle=style;
		layout();
	}
	
	public Interval getFitToVisibleRange()
	{
		ActList acts=model.getActs();

		// add a little bit to the right so we can see labels...
		ActDB db=getModel().getDB();
		Field endField=db.getField(VirtualField.END);
		Interval i=null;
		if (endField==null)
			i=DBUtils.range(acts, VirtualField.START);
		else 
			i=DBUtils.range(acts, new Field[] {db.getField(VirtualField.START), endField});
		if (i.length()==0)
		{
			i.expand(globalInterval.length()/20);
		}
		i=i.subinterval(-.05,1.1);
		i.intersection(globalInterval);
		return i;
	}
	
	public void fitToVisible()
	{
		Interval i=getFitToVisibleRange();
		setTimeBounds(i.start, i.end);
	}
	
	public void zoomOut()
	{
		setTimeBounds(globalInterval.start, globalInterval.end);
	}
	
	public void setTimeBounds(long first, long last)
	{
		timeScale.setDateRange(first, last);
		frameChanged=true;
		model.setViewInterval(new Interval(first, last));
	}
	
	public Interval getGlobalInterval()
	{
		if (globalInterval==null && model!=null && model.getDB()!=null)
		{
			createGlobalInterval();
		}
		return globalInterval;
	}
	
	public void createGlobalInterval()
	{
		globalInterval=DBUtils.range(model.getDB().all(), VirtualField.START).subinterval(-.05,1.1);
	}
	
	public Interval getViewInterval()
	{
		return timeScale.getInterval();
	}
	
	
	public java.util.List<VisualAct> getVisualActs()
	{
		return encoder.getVisualActs();
	}
	
	public void layoutIfChanged()
	{
		if (frameChanged)
			layout();
	}
	
	public void init(boolean majorChange)
	{
		note(new TFEvent(majorChange ? TFEvent.Type.DATABASE_CHANGE : TFEvent.Type.ACT_CHANGE,null));
	}
	
	public void note(TFEvent e) {
		ActList all=null;
		if (e.type==TFEvent.Type.DATABASE_CHANGE)
		{
			all=model.getDB().all();
			createGlobalInterval();
			Interval i=guessInitialViewInterval(all, globalInterval);
			setTimeBounds(i.start, i.end);
		}
		if (e.affectsRowSet())
		{
			all=model.getDB().all();
			encoder.createVisualActs();
			createGlobalInterval();
		}
		else
		{
			encoder.createVisualActs();
		}
		Interval v=model.getViewInterval();
		if (v!=null && v.start!=timeScale.getInterval().start)
		{
			timeScale.getInterval().translateTo(v.start);
		}
		updateVisuals();
	}
	
	private Interval guessInitialViewInterval(ActList acts, Interval fullRange)
	{
		if (acts.size()<50)
			return fullRange.copy();
		
		Interval best=null;
		int most=-1;
		double d=Math.max(.1, 50./acts.size());
		d=Math.min(1./3,d);
		for (double x=0; x<1-d; x+=d/4)
		{
			Interval i= fullRange.subinterval(x,x+d);
			TimeIntervalFilter f=new TimeIntervalFilter(i, getModel().getDB().getField(VirtualField.START));
			int num=0;
			for (Act a: acts)
				if (f.accept(a))
					num++;
			if (num>most)
			{
				most=num;
				best=i;
			}
		}
		return best;
	}
	
	public void updateVisuals()
	{
		updateVisualEncoding();
		layout();
	}
	
	public TFModel getModel()
	{
		return model;
	}
	
	public int getNumTracks()
	{
		return trackList.size();
	}
	
	public void layout()
	{
		ActList acts=model.getActs();
		if (acts==null)
			return;

		double min= bounds.height==0 ? 0 : 30./bounds.height;
		double top=0;
		for (TimelineTrack t: trackList)
		{
			double height=Math.max(min, t.size()/(double)numShown);
			t.layout(top, height, this);
			top+=height;
		}
		fullHeight=(int)(top*bounds.height);
		
		Collections.sort(trackList);
		frameChanged=false;
	}

	private void updateVisualEncoding()
	{
		java.util.List<VisualAct> acts=encoder.apply();
		
		// now arrange on tracks
		trackTable=new HashMap<String, TimelineTrack>();
		trackList=new ArrayList<TimelineTrack>();
		numShown=0;
		for (VisualAct v: acts)
		{
			if (!v.isVisible())
				continue;
			numShown++;
			String s=v.getTrackString();
			TimelineTrack t=trackTable.get(s);
			if (t==null)
			{
				t=new TimelineTrack(s);
				trackTable.put(s, t);
				trackList.add(t);
			}
			t.add(v);
			v.setTrack(t);
		}
		
		/*
	   // the following code is no longer used, but could come in handy again one day...
		
		// If there is more than one "small" track, then we will coalesce them into
		// one bigger "miscellaneous" track.
		int minSize=numShown/30;//Math.max(3,numShown/30);
		ArrayList<TimelineTrack> small=new ArrayList<TimelineTrack>();
		for (TimelineTrack t: trackList)
		{
			if (t.size()<minSize)
				small.add(t);
		}
		if (small.size()>1)
		{
			// create a new Track for "miscellaneous."
			TimelineTrack misc=new TimelineTrack(Display.MISC_CODE);
			trackList.add(misc);
			trackTable.put(misc.label, misc);
			
			// remove the old tracks.
			for (TimelineTrack t:small)
			{
				trackList.remove(t);
				trackTable.remove(t.label);
				for (VisualAct v: t.visualActs)
				{
					v.setTrack(misc);
					misc.add(v);
				}
			}
			// sort miscellaneous items in time order.
			//Collections.sort(misc.visualActs);
		}
		*/
		
		for (TimelineTrack t: trackList)
		{
			Collections.sort(t.visualActs);
		}
	}
}

