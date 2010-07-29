package timeflow.vis;

import java.awt.Graphics2D;
import java.util.ArrayList;

import timeflow.data.db.Act;
import timeflow.data.db.ActDB;
import timeflow.data.db.Field;
import timeflow.model.Display;

public class MouseoverLabel extends Mouseover {

	String label1, label2;
	
	public MouseoverLabel(String label1, String label2, int x, int y, int w, int h) {
		super(label1, x, y, w, h);
		this.label1=label1;
		this.label2=label2;
	}


	public void draw(Graphics2D g, int maxW, int maxH, Display display)
	{
		super.draw(g, maxW, maxH, display);
		ArrayList labels=new ArrayList();
		labels.add(label1);
		labels.add(label2);
		int numLines=1;
		draw(g, maxW, maxH, display, labels, numLines);
	}
}

