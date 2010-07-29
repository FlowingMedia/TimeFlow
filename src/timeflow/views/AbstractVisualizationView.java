package timeflow.views;

import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.model.*;
import timeflow.vis.*;
import timeflow.app.ui.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;

import javax.swing.*;



public abstract class AbstractVisualizationView extends JPanel
{
	Image buffer;
	Graphics2D graphics;
	Point mouse=new Point(-10000,0), firstMouse=new Point();
	boolean mouseIsDown;
	ArrayList<Mouseover> objectLocations=new ArrayList<Mouseover>();
	TFModel model;
	Act selectedAct;
	RoughTime selectedTime;
	Set<JMenuItem> urlItems=new HashSet<JMenuItem>();
	
	public AbstractVisualizationView(TFModel model)
	{
		this.model=model;
		
		// deal with mouseovers.
		addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				mouse.setLocation(e.getX(), e.getY());
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouse.setLocation(e.getX(), e.getY());
				repaint();
			}});
		
		
		final JPopupMenu popup = new JPopupMenu();
	    final JMenuItem edit = new JMenuItem("Edit");
	    edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditRecordPanel.edit(getModel(), selectedAct);
			}});
	    popup.add(edit);
	    
	    final JMenuItem delete = new JMenuItem("Delete");
	    popup.add(delete);
	    delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getModel().getDB().delete(selectedAct);
				getModel().noteDelete(this);
			}});
	    
	    final JMenuItem add = new JMenuItem("New...");
	    popup.add(add);
	    add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditRecordPanel.add(getModel(), selectedTime);
			}});
		
		// deal with right-click.
		addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        pop(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		        pop(e);
		    }

		    private void pop(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		        	Point p=new Point(e.getX(), e.getY());
		        	Mouseover o=find(p);
		        	boolean onAct= o!=null && o.thing instanceof VisualAct;
		        	if (onAct)
		        	{
			        	VisualAct v=(VisualAct)o.thing;
			        	selectedAct=v.getAct();
			        	String name=" '"+v.getLabel()+"'";
			        	edit.setText("Edit"+name+"...");
			        	delete.setText("Delete"+name);
			        	edit.setEnabled(true);
		        		delete.setEnabled(true);
		        	}
		        	else
		        	{
		        		edit.setEnabled(false);
		        		edit.setText("Edit Event");
		        		delete.setEnabled(false);
		        		delete.setText("Delete Event");
		        	}
		        	selectedTime=getTime(p);
		        	if (selectedTime!=null || onAct)
		        	{
			        	add.setEnabled(selectedTime!=null);
			        	add.setText(selectedTime==null ? "Add" : "Add Event At "+selectedTime.format()+"...");
			            
			        	java.util.List<Field> urlFields=getModel().getDB().getFields(URL.class);
			        	if (urlFields.size()>0)
			        	{
			        		// remove any old items.
			        		for (JMenuItem m: urlItems)
			        			popup.remove(m);
			        		urlItems.clear();
			        		
			        		if (onAct)
			        		{
				        		Act a=((VisualAct)o.thing).getAct();
				        		for (Field f: urlFields)
				        		{
				        			final URL url=a.getURL(f);
				        			JMenuItem go=new JMenuItem("Go to "+url);
				        			go.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											Display.launchBrowser(url.toString());
										}});
				        			popup.add(go);
				        			urlItems.add(go);
				        		}
			        		}
			        	}
			        	
			        	popup.show(e.getComponent(), p.x, p.y);
		        	}
		        }
		    }
		});
	}
	
	public RoughTime getTime(Point p)
	{
		return null; 
	}
	
	public TFModel getModel()
	{
		return model;
	}
	
	@Override
	public void setBounds(int x, int y, int w, int h)
	{
		super.setBounds(x,y,w,h);
		if (w>0 && h>0)
		{
			if (graphics!=null)
				graphics.dispose();
			buffer=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
			graphics=(Graphics2D)buffer.getGraphics();
			drawVisualization();
			repaint();
		}
		
	}
	
	void drawVisualization()
	{
		drawVisualization(graphics);
	}
	
	protected abstract void drawVisualization(Graphics2D g);
	protected boolean paintOnTop(Graphics2D g, int w, int h)
	{
		return false;
	}
	
	protected Mouseover find(Point p)
	{
		for (Mouseover o: objectLocations)
			if (o.contains(mouse))
				return o;
		return null;
	}
	
	public final void paintComponent(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(buffer,0,0,null);
		int w=getSize().width, h=getSize().height;
		if (paintOnTop(g,w,h))
			return;
		Mouseover highlight=find(mouse);
		if (highlight!=null)
			highlight.draw(g, w, h, getModel().getDisplay());
	}				
}		