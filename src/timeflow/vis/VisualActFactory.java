package timeflow.vis;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import timeflow.data.db.Act;
import timeflow.data.db.ActDB;
import timeflow.data.db.ActList;
import timeflow.data.db.Field;
import timeflow.model.TFModel;
import timeflow.model.VirtualField;

public class VisualActFactory {
	// create one VisualAct per Act
	private static java.util.List<VisualAct> create(ActList acts)
	{
		java.util.List<VisualAct> list=new ArrayList<VisualAct>();
		for (Act a: acts)
		{
			VisualAct v=new TagVisualAct(a);
			list.add(v);
		}
		return list;
	}

	// create one VisualAct per Act/tag combo.
	public static java.util.List<VisualAct> create(ActList acts, Field tagField, boolean multipleColors)
	{
		if (tagField==null || tagField.getType()==String.class)
			return create(acts);
		java.util.List<VisualAct> list=new ArrayList<VisualAct>();
		for (Act a: acts)
		{
			String[] tags=a.getTextList(tagField);
			if (tags==null || tags.length<2)
			{				
				VisualAct v=new TagVisualAct(a);
				if (tags!=null && tags.length==1)
					v.setTrackString(tags[0]);
				list.add(v);
			}
			else
			{
				for (String tag: tags)
				{
					VisualAct v=multipleColors ? new TagVisualAct(a) : new VisualAct(a);
					v.setTrackString(tag);
					list.add(v);
				}
			}
		}
		return list;
	}
	
	public static Collection<VisualAct> makeEmFit(TFModel model, ArrayList<VisualAct> vacts, Rectangle bounds)
	{		
		// Does everything fit? Because, if so, we're already good to go.
		int area=bounds.width*bounds.height;
		int room=area/200;
		if (vacts.size()<=room)
			return vacts;
		
		ArrayList<VisualAct> results=new ArrayList<VisualAct>();

		// OK. If:
		//     * there's room for more than one item, and  
		//     * there's more than one color in use,
		//
		// Then let's see how many colors there are. Maybe we can do one bubble per color.
		ActDB db=model.getDB();
		if (room>1 && (db.getField(VirtualField.COLOR)!=null || db.getField(VirtualField.TRACK)!=null))
		{
			HashMap<Color, ArrayList<VisualAct>> colorGroupings=new HashMap<Color, ArrayList<VisualAct>>();
			for (VisualAct v:vacts)
			{
				Color c=v.color;
				ArrayList<VisualAct> grouping=colorGroupings.get(c);
				if (grouping==null)
				{
					grouping=new ArrayList<VisualAct>();
					colorGroupings.put(c, grouping);
				}
				grouping.add(v);
			}
		
			if (colorGroupings.size()<=room) // Great! The colors fit. We now return one group VisualAct per color.
			{
				for (Color c: colorGroupings.keySet())
				{
					ArrayList<VisualAct> grouping=colorGroupings.get(c);
					if (grouping.size()==1)
						results.add(grouping.get(0));
					else if (grouping.size()>1)
						results.add(new GroupVisualAct(grouping, false, bounds));
				}
				return results;
			}			
		}
		
		// OK, too bad, even that doesn't fit. We will just create one fat VisualAct
		// that descibes the aggregate. C'est la vie!
		
		results.add(new GroupVisualAct(vacts, true, bounds));
		return results;
	}
}
