package timeflow.vis.calendar;

import timeflow.data.time.*;
import timeflow.model.Display;
import timeflow.vis.*;

import java.util.*;
import java.awt.*;
import java.text.*;

public class Grid {
	TimeUnit rowUnit, columnUnit;
	RoughTime startRow, endRow;
	Interval interval;
	HashMap<Long, GridCell> cells;
	int[] screenGridX;
	
	int cellHeight=80, cellWidth, numCols, numRows;
	Rectangle bounds=new Rectangle();
	int dy;
	
	static final DateFormat dayOfWeek=new SimpleDateFormat("EEE");
	static final DateFormat month=new SimpleDateFormat("MMM d");
	static final String[] day={"SUN", "MON", "TUES", "WED", "THURS", "FRI", "SAT"};
	
	Grid(TimeUnit rowUnit, TimeUnit columnUnit, Interval interval)
	{
		this.rowUnit=rowUnit;
		this.columnUnit=columnUnit;
		numCols=columnUnit.numUnitsIn(rowUnit);
		setInterval(interval);
	}
	
	public void setDY(int dy)
	{
		this.dy=dy;
	}
	
	public int getCalendarHeight()
	{
		return bounds.height+bounds.y+20;
	}
	
	public RoughTime getTime(int x, int y)
	{
		y+=dy;
		if (!bounds.contains(x,y))
			return null;
		
		// find grid coordinates.
		int gridX=(x-bounds.x)/cellWidth;
		int gridY=(y-bounds.y)/cellHeight;
		
		return startRow.plus(rowUnit, gridY).plus(columnUnit, gridX);
	}
	
	public double getScrollFraction()
	{
		double x= (getFirstDrawnTime().getTime()-startRow.getTime())/(double)
				(endRow.getTime()-startRow.getTime());
		if (x<0)
			return 0;
		if (x>1)
			return 1;
		return x;
	}
	
	public RoughTime getFirstDrawnTime()
	{
		int gridY=(dy-bounds.y)/cellHeight;
		
		return startRow.plus(rowUnit, gridY);
	}
	
	private Point getGridCorner(long timestamp)
	{
		int diff=(int)columnUnit.difference(timestamp, startRow.getTime());
		int gridX=diff%numCols;
		int gridY=diff/numCols;
		return new Point(gridX, gridY);
	}
	
	public Rectangle getCell(long timestamp)
	{
		Point p=getGridCorner(timestamp);
		return new Rectangle(bounds.x+p.x*cellWidth, bounds.y+p.y*cellHeight-dy,
				cellWidth, cellHeight);
	}
	
	void setInterval(Interval interval)
	{
		this.interval=interval;
		startRow=rowUnit.roundDown(interval.start);
		endRow=rowUnit.roundDown(interval.end);
		numRows=1+(int)(rowUnit.difference(endRow.getTime(), startRow.getTime()));
		
		// the next line fixes a problem with multi-century data sets.
		// it works, but there's probably a better way to do this :-)
		if (numRows>50 && rowUnit.getRoughSize()>=TimeUnit.YEAR.getRoughSize())
			numRows++;
	}
	
	void makeCells(java.util.List<VisualAct> visualActs)
	{
		cells=new HashMap<Long, GridCell>();
		for (VisualAct v: visualActs)
		{
			if (v.getStart()==null)
				continue;
			long timestamp=v.getStart().getTime();
			RoughTime timeKey=columnUnit.roundDown(timestamp);
			GridCell cell=cells.get(timeKey.getTime());
			if (cell==null)
			{
				cell=new GridCell(timeKey);
				cells.put(timeKey.getTime(), cell);
				Point p=getGridCorner(timestamp);
				cell.gridX=p.x;
				cell.gridY=p.y;
			}
			cell.visualActs.add(v);
		}
	}

	void render(Graphics2D g, Display display, Rectangle screenBounds, CalendarVisuals visuals, 
			Collection<Mouseover> objectLocations)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int left=110, right=20;
		int padY=50;
		boolean shouldLabel=visuals.drawStyle==CalendarVisuals.DrawStyle.LABEL;
		
		cellWidth=(screenBounds.width-left-right)/numCols;	
		
		boolean fitTight=visuals.fitStyle==CalendarVisuals.FitStyle.TIGHT;
		int idealHeight= fitTight ? 12 : Display.CALENDAR_CELL_HEIGHT;
		cellHeight=Math.max(idealHeight, (screenBounds.height-padY-10)/numRows);
		this.bounds.setBounds(left, padY, numCols*cellWidth, numRows*cellHeight);
		
		g.setColor(new Color(240,240,240));//Color.white);
		g.fill(screenBounds);
		g.setColor(new Color(245,245,245));
		g.drawRect(bounds.x, bounds.y-dy, bounds.width, bounds.height);
		g.setFont(display.bold());
		
		// draw vertical grid lines.
		Color gridColor=new Color(220,220,220);
		
		for (int i=0; i<=numCols; i++)
		{
			int x=bounds.x+i*cellWidth;
			g.setColor(gridColor);
			g.drawLine(x,bounds.y-dy,x,bounds.y+bounds.height-dy);
			if (rowUnit==TimeUnit.WEEK && i<7)
			{
				g.setColor(Color.gray);
				g.drawString(day[i], x, bounds.y-dy-6);
			}
		}
		
		// horizontal grid lines.
		RoughTime labelTime=startRow.copy();
		int lastLabelY=-100;
		int lastYear=-1000000;
		FontMetrics fm=display.boldFontMetrics();
		int skipped=0;
		for (int i=0; i<numRows; i++)
		{
			int y=bounds.y+i*cellHeight;
			if (y-dy>-50)
			{
				if (skipped>0)
				{
					rowUnit.addTo(labelTime, skipped);
					skipped=0;
				}
				g.setColor(gridColor);
				g.drawLine(bounds.x,y-dy,bounds.x+bounds.width,y-dy);
				if (y-lastLabelY>30 || lastLabelY<0)
				{
					String label=null;
					if (rowUnit==TimeUnit.WEEK)
					{
						int year=TimeUtils.cal(labelTime.getTime()).get(Calendar.YEAR);
						if (year!=lastYear)
							label=labelTime.format();
						else 
							label=month.format(labelTime.toDate());
						lastYear=year;
					}
					else
						label=labelTime.format();
					g.setColor(Color.gray);
					g.drawString(label, bounds.x-fm.stringWidth(label)-15, y+15-dy);
					lastLabelY=y;
				}
				if (y-dy>screenBounds.height)
					break;
				
				// now draw, in gray, the labels for each of the boxes.
				if (!fitTight)
				{
					RoughTime gridLabel=labelTime.copy();
					int labelH=13;
					for (int j=0; j<numCols; j++)
					{
						
						g.setColor(Color.gray);
						g.setFont(display.bold());
						String label=columnUnit.format(gridLabel.toDate());
						g.drawString(label, bounds.x+j*cellWidth+3, y-dy+labelH);
						columnUnit.addTo(gridLabel);
					}
				}
				rowUnit.addTo(labelTime);
			}
			else 
				skipped++;
		}
		
		// draw a frame around the whole thing.
		g.setColor(Color.darkGray);
		g.drawRect(bounds.x, bounds.y-dy, bounds.width, bounds.height);
		
		// draw backgrounds
		for (GridCell cell: cells.values())
		{
			// are any visible?
			boolean visible=false;
			for (VisualAct v: cell.visualActs)
			{
				if (v.isVisible())
				{
					visible=true;
					break;
				}
			}
			int cx=bounds.x+cell.gridX*cellWidth;
			int cy=bounds.y+cell.gridY*cellHeight-dy;
			
			if (cy<screenBounds.y-50 || cy>screenBounds.y+screenBounds.height+50)
				continue;
			
			// label top of cell.
			int labelH=0;
			g.setColor(new Color(240,240,240));
			
			if (visible)
			{
				g.setColor(Color.white); 
				g.fillRect(cx+1,cy+1,cellWidth-1,cellHeight-1);
			}
			if (cellHeight>42)
			{
				labelH=13;
				g.setColor(Color.darkGray);
				g.setFont(display.bold());
				String label=columnUnit.format(cell.time.toDate());
				g.drawString(label, cx+3, cy+labelH);
			}
		
		}
		
		
		
		// draw items.
		int mx=10, my=shouldLabel ? 18 : 10;
		for (GridCell cell: cells.values())
		{
			
			int cx=bounds.x+cell.gridX*cellWidth;
			int cy=bounds.y+cell.gridY*cellHeight-dy;
			
			if (cy<screenBounds.y-50 || cy>screenBounds.y+screenBounds.height+50)
				continue;
			
			// label top of cell.
			int labelH=cellHeight>42 ? 13 : 0;
			
			// now draw the items in the cell.
			// old, non-aggregation code:
			
			// START AGGREGATION CODE
			
			ArrayList<VisualAct> visibleActs=new ArrayList<VisualAct>();
			for (VisualAct v: cell.visualActs)
				if (v.isVisible())
					visibleActs.add(v);
			Iterator<VisualAct> vacts=
				VisualActFactory.makeEmFit(visuals.model, visibleActs, new Rectangle(cx, cy, cellWidth, cellHeight)).iterator();
			
			// END AGGREGATION CODE
			
			int leftX=6;
			int cdx=leftX;
			int topDotY=Math.min(labelH+16,cellHeight/2);
			int cdy=topDotY;
			while (vacts.hasNext())
			{
				VisualAct v=vacts.next();
				if (!v.isVisible())
					continue;
				
				// set x,y, room to right.
				int x=cx+cdx;				
				int y=cy+cdy;
				
				int space=cellWidth-20;
				v.setX(x);
				v.setY(y);
				v.setSpaceToRight(space);
				Mouseover o=v.draw(g, new Rectangle(cx+1,cy+labelH+1,cellWidth-2, cellHeight-2-labelH), 
						bounds, display, shouldLabel, false);
				if (o!=null)
					objectLocations.add(o);
				
				// go to next location. if we're labeling, we do this vertically.
				// otherwise, left-to-right, then top-to-bottom.
				
				if (shouldLabel)
				{
					cdy+=my;
					if (cdy>cellHeight-2-my)
					{	
						g.drawString("...", x,y+my);
						break;
					}
				}
				else
				{
					cdx+=mx;
					if (cdx>cellWidth-mx/2-2 && vacts.hasNext())
					{
						cdx=leftX;
						cdy+=my;
						if (cdy>cellHeight-my/2)
						{
							break;
						}
					}					
				}				
			}
		}		
	}
}
