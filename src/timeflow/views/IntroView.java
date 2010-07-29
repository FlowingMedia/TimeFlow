package timeflow.views;

import timeflow.app.ui.EditRecordPanel;
import timeflow.data.analysis.*;
import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.format.field.FieldFormatCatalog;
import timeflow.model.*;
import timeflow.views.*;
import timeflow.views.ListView.LinkIt;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import timeflow.util.*;

public class IntroView extends AbstractView {
	
	private JComponent controls;
	Image image;
	Image repeat;
	
	public IntroView(TFModel model)
	{
		super(model);
		setBackground(Color.white);
		try
		{
			image=ImageIO.read(new File("images/intro.gif"));
			repeat=ImageIO.read(new File("images/repeat.gif"));
		}
		catch (Exception e)
		{
			System.out.println("Couldn't load images.");
			e.printStackTrace(System.out);
		}
		makeHtml();
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.white);
		int w=getSize().width, h=getSize().height;
		g.fillRect(0,0,w,h);
		// draw image and extensible background, so it looks cool on a big screen.
		if (image!=null && repeat!=null)
		{
			int ih=image.getHeight(null);
			int iw=image.getWidth(null);
			int rw=repeat.getWidth(null);
			g.drawImage(image,0,0,null);
			for (int x=iw; x<w; x+=rw)
				g.drawImage(repeat,x,0,null);
		}
	}
	
	void makeHtml()
	{
		try
		{
			String sidebar=IO.read("settings/sidebar.html");
			controls=new HtmlControls(sidebar);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	@Override
	public JComponent _getControls()
	{
		return controls;
	}

	@Override
	protected void onscreen(boolean majorChange)
	{
		
	}

	protected void _note(TFEvent e) {
		// do nothing.
	}
	
	@Override
	public String getName() {
		return "About";
	}

}
