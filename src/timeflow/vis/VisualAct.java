package timeflow.vis;

import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.model.*;
import timeflow.vis.timeline.TimelineTrack;

import java.awt.*;
import java.util.*;

import timeflow.util.*;

public class VisualAct implements Comparable 
{
	Color color;
	String label;
	String mouseOver;
	double size=1;
	String trackString;
	TimelineTrack track;
	boolean visible;
	Act act;
	int x,y;
	int spaceToRight;
	RoughTime start, end;
    int endX;
	
	public VisualAct(Act act)
	{
		this.act=act;
	}

	public int getX() {
		return x;
	}
	
	public int getDotR()
	{
		return Math.max(1, (int)Math.abs(size));
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	

	public void draw(Graphics2D g, int ox, int oy, int r, Rectangle maxFill, boolean showDuration)
	{
		g.setColor(getColor());		
		if (size>=0)
			g.fillOval(ox,y-r,2*r,2*r);
		else
			g.drawOval(ox,y-r,2*r,2*r);
		if (end!=null && showDuration)
		{
			int lineY=y+6;
			g.fillRect(getX(), lineY, getEndX()-getX(), 2);
			g.drawLine(getX(), lineY, getX(), lineY-4);
		}

	}
	
	public Mouseover draw(Graphics2D g, Rectangle maxFill, Rectangle bounds, 
			                   Display display, boolean text, boolean showDuration)
	{
		if (!isVisible())
			return null;

		if (x>bounds.x+bounds.width && (end==null || endX>bounds.x+bounds.width) || 
			x<bounds.x-200 && (end==null || endX<bounds.x-200)) 
			return null;
		
		g.setFont(display.plain());
		
		int r=getDotR();
		if (r<=0)
			r=1;
		if (r>30)
			r=30;
		int ox=text ? x-2*r : x;

		draw(g,ox,y-2,r,maxFill,showDuration);

		
		if (!text)
		{
			return new VisualActMouseover(ox-2, y-r-4, 4+2*r, 4+2*r);
		}
					
		int labelSpace=getSpaceToRight()-12;
		int sw=0;
		if (labelSpace>50)
		{
			String s=display.format(getLabel(), labelSpace/8, true);
			int n=s.indexOf(' ');
			int tx=x+5;
			int ty=y+4;
			if (n<1)
			{
				g.drawString(s,tx,ty);
			}
			else
			{
				String first=s.substring(0,n);
				g.drawString(first,tx,ty);
				Color c=ColorUtils.interpolate(g.getColor(), Color.white, .33);
				g.setColor(c);
				g.drawString(s.substring(n),tx+display.plainFontMetrics().stringWidth(first),ty);
			}
			sw=display.plainFontMetrics().stringWidth(s);
		}
		
		return new VisualActMouseover(x-3-2*r, y-r-8, 14+sw, r+13+2*r);
	}
	
	
	public Act getAct()
	{
		return act;
	}
	
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public RoughTime getStart() {
		return start;
	}
	public void setStart(RoughTime start) {
		this.start = start;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getMouseOver() {
		return mouseOver;
	}
	public void setMouseOver(String mouseOver) {
		this.mouseOver = mouseOver;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	
	public String getTrackString() {
		return trackString==null ? "" : trackString;
	}
	public void setTrackString(String track) {
		this.trackString = track;
	}
	
	public TimelineTrack getTrack() {
		return track;
	}

	public void setTrack(TimelineTrack track) {
		this.track = track;
	}

	public void setSpaceToRight(int spaceToRight) {
		this.spaceToRight = spaceToRight;
	}

	public int getSpaceToRight() {
		return spaceToRight;
	}
	
	
	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public RoughTime getEnd() {
		return end;
	}

	public void setEnd(RoughTime end) {
		this.end = end;
	}



	@Override
	public int compareTo(Object o) {
		return RoughTime.compare(start, ((VisualAct)o).start);
		//start.compareTo(((VisualAct)o).start);
	}
	
	class VisualActMouseover extends Mouseover
	{
		
		public VisualActMouseover(int x, int y, int w, int h) {
			super(VisualAct.this, x, y, w, h);
		}

		public void draw(Graphics2D g, int maxW, int maxH, Display display)
		{
			super.draw(g, maxW, maxH, display);
			Act a=getAct();
			ActDB db=a.getDB();
			java.util.List<Field> fields=db.getFields();
			ArrayList labels=new ArrayList();
			int charWidth=40;
			int numLines=1;
			if (VisualAct.this instanceof GroupVisualAct)
			{
				GroupVisualAct gv=(GroupVisualAct)VisualAct.this;
				labels.add(gv.getNumActs()+"");
				labels.add("items");
				Field sizeField=db.getField(VirtualField.SIZE);
				if (sizeField!=null)
				{
					labels.add("Total "+sizeField.getName());
					double t=((GroupVisualAct)(VisualAct.this)).total;
					labels.add(Display.format(t));
					numLines++;
				}
			}
			else
			{
				for (Field f: fields)
				{
					labels.add(f.getName());
					Object val=a.get(f);
					String valString=display.toString(val);
					if (f.getName().length()+valString.length()+2>charWidth)
					{
						ArrayList<String> lines=Display.breakLines(valString, charWidth, 2+f.getName().length());
						labels.add(lines);
						numLines+=lines.size()+1;
					}
					else
					{
						labels.add(valString);
						numLines++;
					}
				}		
			}
			draw(g, maxW, maxH, display, labels, numLines);
		}
	}
}
