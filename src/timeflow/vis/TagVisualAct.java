package timeflow.vis;

import timeflow.data.db.*;

import java.awt.*;
import java.util.*;

public class TagVisualAct extends VisualAct {
	
	Color[] colors;
	private static Color[] nullColors={new Color(230,230,230)};

	public TagVisualAct(Act act) {
		super(act);
	}
	
	public void setColors(Color[] colors)
	{
		this.colors=colors;
		this.color=colors.length>0 ? colors[0] : Color.gray;
	}

	public void draw(Graphics2D g, int ox, int oy, int r, Rectangle maxFill, boolean showDuration)
	{
		if (colors==null)
		{
			super.draw(g, ox, oy, r, maxFill, showDuration);
			return;
		}

		Color[] c= colors==null || colors.length==0 ? nullColors : colors;
		int tx=ox-r;
		int side=2*r;
		for (int i=0; i<c.length; i++)
		{
			g.setColor(c[i]);
			int y0=-5+(oy-r)+(2*side*i)/c.length;
			int y1=-5+(oy-r)+(2*side*(i+1))/c.length;
			if (size>=0)
				g.fillRect(tx,y0,side+2,y1-y0);
			else
				g.drawRect(tx,y0,side+2,y1-y0);
		}
		
		if (end!=null && showDuration)
		{
			int lineY=y+6;
			g.fillRect(getX(), lineY, getEndX()-getX(), 2);
			g.drawLine(getX(), lineY, getX(), lineY-4);
		}
	}
}
