package timeflow.vis;

import timeflow.model.*;

import java.awt.*;
import java.util.ArrayList;

public class Mouseover extends Rectangle {
	public Object thing;
	public Mouseover(Object thing, int x, int y, int w, int h)
	{
		super(x,y,w,h);
		this.thing=thing;
	}
	
	public void draw(Graphics2D g, int maxW, int maxH, Display display)
	{
		g.setColor(new Color(0,53,153));
		g.setColor(new Color(255,255,0,100));
		g.fill(this);
	}
	
	protected void draw(Graphics2D g, int maxW, int maxH, Display display, ArrayList labels, int numLines)
	{
		if (labels==null || labels.size()==0)
			return;
		
		// draw a background box.
		
		// find max number of chars, very very roughly!
		int boxW=50;
		for (int i=0; i<labels.size(); i+=2)
		{
			if (labels.get(i) instanceof String[])
				boxW=300;
			else if (labels.get(i) instanceof String)
			{
				boxW=Math.max(boxW, 50+50*((String)labels.get(i)).length());
			}
		}	
		
		
		boxW=Math.min(350, boxW);
		int boxH=18*numLines+10;
		int mx=this.x+this.width+5;
		int my=this.y+this.height+35;		
		
		// put box in a place where it does not obscure the data
		// or go off screen.
		if (my+boxH>maxH-10)
		{
			my=Math.max(10,this.y-boxH-5);
		}
		if (mx+boxW>maxW-10)
		{
			mx=Math.max(10,this.x-boxW-10);
		}
		int ty=my;
		g.setColor(new Color(0,0,0,70));
		g.fillRoundRect(mx-11, my-16, boxW, boxH,12,12);
		g.setColor(Color.white);
		g.fillRoundRect(mx-15, my-20, boxW, boxH,12,12);
		g.setColor(Color.darkGray);
		g.drawRoundRect(mx-15, my-20, boxW, boxH,12,12);
		
		// finally, draw the darn labels.
		for (int i=0; i<labels.size(); i+=2)
		{
			g.setFont(display.bold());
			String field=(String)labels.get(i);
			g.drawString(field,mx,ty);
			int sw=display.boldFontMetrics().stringWidth(field);
			g.setFont(display.plain());
			Object o=labels.get(i+1);
			if (o instanceof String)
			{
				g.drawString((String)o,mx+sw+9,ty);
				ty+=18;
			}
			else
			{
				ArrayList<String> lines=(ArrayList<String>)o;
				int dx=sw+9;
				for (String line: lines)
				{
					g.drawString((String)line,mx+dx,ty);
					ty+=18;
					dx=0;
				}
				ty+=5;
			}
		}		
	}
}
