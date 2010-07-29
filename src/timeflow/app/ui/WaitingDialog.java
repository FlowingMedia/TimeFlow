package timeflow.app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WaitingDialog extends JFrame {
	
	Timer timer;
	
	public static void main(String[] args)
	{
		new WaitingDialog("Testing", "Hello, world!");
	}
		
	public WaitingDialog(String title, String message)
	{
		super(title);
		Throbber throbber=new Throbber();
		timer=new Timer(50, throbber);		
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(throbber);
		getContentPane().add(new JLabel(message));
		setBounds(400,400,300,150);
		setVisible(true);
		timer.start();
	}
	
	public void stop()
	{
		timer.stop();
		setVisible(false);
	}
	
	class Throbber extends JPanel implements ActionListener
	{
		int count=0;
		public void paintComponent(Graphics g)
		{
			int w=getSize().width, h=getSize().height;
			int c=count%256;
			g.setColor(new Color(c,c,c));
			g.fillRect(0,0,w,h);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
			count+=10;
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(30,100);
		}
	}
}
