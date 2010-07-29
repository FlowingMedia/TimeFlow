package timeflow.vis;

import timeflow.data.db.*;
import timeflow.model.*;

import java.awt.Color;
import java.util.*;

public class VisualEncoder {
	
	private TFModel model;
	private java.util.List<VisualAct> visualActs=new ArrayList<VisualAct>();
	private double maxSize=0;
	
	public VisualEncoder(TFModel model)
	{
		this.model=model;
	}	
	
	public java.util.List<VisualAct> getVisualActs()
	{
		return visualActs;
	}
	
	public void createVisualActs() {
		Field colorField=model.getDB().getField(VirtualField.COLOR);
		Field trackField=model.getDB().getField(VirtualField.TRACK);
		boolean multipleColors=colorField!=null && colorField.getType()==String[].class && colorField!=trackField;
		visualActs=VisualActFactory.create(model.getDB().all(), trackField, multipleColors);
		Collections.sort(visualActs);
	}

	public List<VisualAct> apply()
	{
		
		ActList visibleActs=model.getActs();
		Field start=model.getDB().getField(VirtualField.START);
		Field end=model.getDB().getField(VirtualField.END);
		Field size=model.getDB().getField(VirtualField.SIZE);
		if (size!=null)
		{
			double[] minmax=DBUtils.minmax(model.getActs(), size);
			maxSize=Math.max(Math.abs(minmax[0]), Math.abs(minmax[1]));
		}
		
		// apply color, label, visibility, etc.
		for (VisualAct v: visualActs)
		{
			Act a=v.getAct();
			v.setStart(start==null ? null : a.getTime(start));
			v.setEnd(end==null ? null : a.getTime(end));
			v.setVisible(visibleActs.contains(a) && v.getStart()!=null && v.getStart().isDefined());
			apply(v);
		}	
		
		return visualActs;
	}

	public void apply(VisualAct v)
	{
		Display display=model.getDisplay();
		ActDB db=model.getDB();
		Act a=v.getAct();
		Field label=db.getField(VirtualField.LABEL);
		Field track=db.getField(VirtualField.TRACK);
		Field color=db.getField(VirtualField.COLOR);
		Field size=db.getField(VirtualField.SIZE);
		
		if (label==null)
			v.setLabel("");
		else
			v.setLabel(a.getString(label));
				
		double s=Display.MAX_DOT_SIZE;
		if (size==null || maxSize==0)
			v.setSize(s/3);
		else
		{
			double z=s*Math.sqrt(Math.abs(a.getValue(size))/maxSize);
			if (a.getValue(size)<0)
				z=-z;
			v.setSize(z);		
		}
		
		// For setting the track:
		// This is a little complicated, but if the track is set to
		// tags (that is, track.getType()==String[].class) then
		// the track string was set earlier so it doesn't need to be set now.
		if (track==null)
			v.setTrackString("");
		else if (track.getType()==String.class) 
			v.setTrackString(a.getString(track));
		
		if (color==null || color==track)
		{
			if (track==null)
				v.setColor(display.getColor("timeline.unspecified.color"));
			else
				v.setColor(display.makeColor(v.getTrackString()));
		}
		else
		{
			if (color.getType()==String[].class)
			{
				String[] tags=a.getTextList(color);
				if (tags==null || tags.length==0)
					((TagVisualAct)v).setColors(new Color[0]);
				else
				{
					int n=tags.length;
					Color[] c=new Color[n];
					for (int i=0; i<n; i++)
						c[i]=display.makeColor(tags[i]);
					((TagVisualAct)v).setColors(c);
				}
			}
			else
				v.setColor(display.makeColor(a.getString(color)));
		}
	}
}