package timeflow.vis.calendar;


import timeflow.vis.*;
import timeflow.data.time.*;

import java.util.*;
import java.awt.*;

public class GridCell {
	ArrayList<VisualAct> visualActs=new ArrayList<VisualAct>();
	Rectangle bounds;
	RoughTime time;
	int gridX, gridY;
	
	GridCell(RoughTime time)
	{
		this.time=time;
	}
}
