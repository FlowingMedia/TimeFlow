package timeflow.app.ui;

import javax.swing.*;
import java.awt.*;

public class ComponentCluster extends JPanel
{
	int numComps=0;
	int x1=80;
	int width=200;
	int compH=30;
	DottedLine line=new DottedLine();
	
	public ComponentCluster(String name)
	{
		setBackground(Color.white);
		setLayout(null);
		JLabel label=new JLabel(name);
		add(label);
		label.setBounds(3,3,50,30);
		add(line);
	}
	
	public void addContent(JComponent c)
	{
		add(c);
		c.setBorder(null);
		c.setBounds(x1,10+numComps*compH, c.getPreferredSize().width, c.getPreferredSize().height);
		numComps++;
		line.setBounds(x1-10,10,1,numComps*compH-5);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(width, 20+compH*numComps);
	}
}
