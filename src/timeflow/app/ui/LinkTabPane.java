package timeflow.app.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

// custom JTabbedPane-like thing.
public class LinkTabPane extends JPanel {
	
	ArrayList<String> tabNames=new ArrayList<String>();
	HashMap<String, JComponent> tabMap=new HashMap<String, JComponent>();
	String currentName;
	CardLayout cards=new CardLayout();
	JPanel center=new JPanel();
	LinkTop top=new LinkTop();
	
	public LinkTabPane()
	{
		setBackground(Color.white);
		setLayout(new BorderLayout());
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		center.setLayout(cards);
		top.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String s=top.getName(e.getX());
				if (s!=null)
				{
					String old=currentName;
					setCurrentName(s);
					firePropertyChange("tab", old, s);
				}
			}});
	}
	
	public String getTitleAt(int i)
	{
		return tabNames.get(i);
	}
	
	public void setSelectedIndex(int i)
	{
		setCurrentName(getTitleAt(i));
	}
	
	public void addTab(JComponent component, String name, boolean left)
	{
		tabNames.add(name);
		tabMap.put(name, component);
		center.add(component, name);
		top.addName(name, left);
		repaint();
		if (currentName==null)
			currentName=name;
	}
	
	public String getCurrentName()
	{
		return currentName;
	}
	
	public void setCurrentName(final String currentName)
	{
		this.currentName=currentName;
		top.repaint();
		SwingUtilities.invokeLater(new Runnable() {public void run() {cards.show(center, currentName);}});

	}
	
	class LinkTop extends JPanel
	{
		int left, right;
		ArrayList<HotLink> leftHots=new ArrayList<HotLink>();
		ArrayList<HotLink> rightHots=new ArrayList<HotLink>();
		Font font=new Font("Verdana", Font.PLAIN, 14);
		FontMetrics fm=getFontMetrics(font);
		
		LinkTop()
		{
			setLayout(new FlowLayout(FlowLayout.LEFT));
			setBackground(new Color(220,220,220));
		}
		
		public void paintComponent(Graphics g1)
		{
			int w=getSize().width, h=getSize().height;
			Graphics2D g=(Graphics2D)g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(getBackground());
			g.fillRect(0,0,w,h);
			g.setColor(Color.gray);
			for (int i=0; i<2; i++)
			{
				g.drawLine(0,i,w,i);
				g.drawLine(0,h-1-i,w,h-1-i);
			}
			
			for (HotLink hot: leftHots)
			{
				draw(g, hot, h, 0);
			}
			
			for (HotLink hot: rightHots)
			{
				draw(g, hot, h, w);
			}
			
			for (int i=0; i<leftHots.size(); i++)
			{
				
				if (i<leftHots.size()-1)
				{
					HotLink hot=leftHots.get(i);
					for (int j=0; j<1; j++)
						g.drawLine(hot.x+hot.width-1-j, 7, hot.x+hot.width-1-j, h-7);
				}
			}
			
			for (int i=0; i<rightHots.size(); i++)
			{
				
				if (i<rightHots.size()-1)
				{
					HotLink hot=rightHots.get(i);
					for (int j=0; j<1; j++)
						g.drawLine(hot.x+w-1-j, 7, hot.x+w-1-j, h-7);
				}
			}
		}
		
		void draw(Graphics g, HotLink hot, int h, int dx)
		{
			int x=hot.x+dx;
			if (hot.s.equals(currentName))
			{
				g.setColor(Color.lightGray);
				g.fillRect(x,2,hot.width,h-4);
				g.setColor(Color.gray);
				g.drawLine(x-1, 0, x-1, h);
				g.drawLine(x+hot.width-1, 0, x+hot.width-1, h);
			}
			g.setColor(Color.darkGray);
			g.setFont(font);
			int sw=fm.stringWidth(hot.s);
			g.drawString(hot.s, x+(hot.width-sw)/2, h-10);
			
		}
		
		String getName(int x)
		{
			for (HotLink h: leftHots)
			{
				if (h.x<=x && h.x+h.width>x)
					return h.s;
			}
			for (HotLink h: rightHots)
			{
				int w=getSize().width;
				if (h.x+w<=x && h.x+h.width+w>x)
					return h.s;
			}

			if (leftHots.size()>0)
				return leftHots.get(leftHots.size()-1).s;
			if (rightHots.size()>0)
				return rightHots.get(0).s;
			return null;
		}
		
	    void addName(String name, boolean leftward)
		{
	    	if (leftward)
	    	{
	    		int x=right;
		    	int w=fm.stringWidth(name)+24;
		    	leftHots.add(new HotLink(name, x, 0, w, 30));
		    	right+=w;
	    	}
	    	else
	    	{
	    		int x=left;
		    	int w=fm.stringWidth(name)+24;
		    	rightHots.add(new HotLink(name, x-w, 0, w, 30));
		    	left-=w;		
	    	}
		}
	    
	    class HotLink extends Rectangle
	    {
	    	String s;
	    	HotLink(String s, int x, int y, int w, int h)
	    	{
	    		super(x,y,w,h);
	    		this.s=s;
	    	}
	    }
		
		public Dimension getPreferredSize()
		{
			return new Dimension(30,30);
		}
	}
	
}
