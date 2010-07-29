package timeflow.app.ui;

import timeflow.model.*;
import timeflow.data.db.*;
import timeflow.data.time.*;

import timeflow.util.*;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class SizeLegendPanel extends ModelPanel {
	Field sizeField;
	double min, max;

	public SizeLegendPanel(TFModel model)
	{
		super(model);
		setBackground(Color.white);
	}
	
	@Override
	public void note(TFEvent e) {
		Field size=getModel().getDB().getField(VirtualField.SIZE);
		
		if (size!=null && (size!=sizeField || e.affectsData()))
		{
			double[] minmax=DBUtils.minmax(getModel().getActs(), size);
			min=minmax[0];
			max=minmax[1];
		}
		sizeField=size;		
		repaint();
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200,40);
	}
	
	public void paintComponent(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int w=getSize().width;
		int h=getSize().height;
		TFModel model=getModel();
		Display display=model.getDisplay();
		g.setColor(getBackground());
		g.setFont(display.plain());
		g.fillRect(0,0,w,h);
		g.setColor(Color.gray);
		
		if (sizeField==null)
		{
			return;
		}
		else if (Double.isNaN(min))
		{
			g.drawString("All values missing.",3,20);
			return;
		}
		else
		{
			AffineTransform old=g.getTransform();
			g.setTransform(AffineTransform.getTranslateInstance(20, 0));
			if (min==max)
			{
				g.setColor(Color.gray);
				g.fillOval(3,h/2-3,6,6);
				g.setColor(Color.black);
				g.setFont(display.tiny());
				g.drawString(format(min),12,h/2+5);				
			}
			else
			{
				String leftLabel=format(min);
				String rightLabel=format(max);
				g.setFont(display.tiny());
				int lw=display.tinyFontMetrics().stringWidth(leftLabel);
				int rw=display.tinyFontMetrics().stringWidth(rightLabel);
				g.setColor(Color.black);
				int ty=h/2+5;;
				g.drawString(leftLabel,2,ty);
				g.setColor(Color.lightGray);
				double maxAbs=Math.max(Math.abs(min), Math.abs(max));
				int dx=8+lw;
				for (int i=0; i<5; i++)
				{
					double z=(i*max+(4-i)*min)/4;
					int r=(int)(Math.sqrt(Math.abs(z/maxAbs))*Display.MAX_DOT_SIZE);
					if (r<1)
						r=1;
					if (z>0)
						g.fillOval(dx,h/2-r,2*r,2*r);
					else
						g.drawOval(dx,h/2-r,2*r,2*r);
					dx+=5+2*r;
				}
				g.setColor(Color.black);
				g.drawString(rightLabel,dx+4,ty);
			}
			g.setTransform(old);
		}
	}
	
	String format(double x)
	{
		if (Math.abs(x)>10 && (max-min)>10)
			return Display.format(Math.round(x));
		return Display.format(x);
	}
}
