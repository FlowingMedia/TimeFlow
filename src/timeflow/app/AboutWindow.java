package timeflow.app;

import timeflow.model.*;

import java.awt.*;
import java.io.File;
import java.util.*;
import javax.imageio.*;

public class AboutWindow extends Window {
	
	Image image;
	Display display;
	int w=640, h=380;
	
	public AboutWindow(Frame owner, Display display) {
		super(owner);
		this.display=display;
		
		try
		{
			image=ImageIO.read(new File("images/splash.jpg"));
			w=image.getWidth(null);
			h=image.getHeight(null);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((size.width-w)/2, (size.height-h)/2, w,h);
		
	}
	
	public void paint(Graphics g)
	{
		if (image!=null)
		{
			g.drawImage(image,0,0,null);
			return;
		}
		int lx=15;
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(display.getColor("splash.background"));
		g.fillRect(0,0,w,h);
		g.setFont(display.huge());
		g.setColor(display.getColor("splash.text"));
		g.drawString(Display.version(), lx, 35);
		g.setFont(display.plain());
		g.drawString("Prototype version",lx,60);
	}
}
