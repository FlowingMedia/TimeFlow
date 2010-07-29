package timeflow.app.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import timeflow.app.TimeflowApp;
import timeflow.app.ui.ReorderFieldsPanel;
import timeflow.model.Display;

public class WebDocAction extends TimeflowAction {
	public WebDocAction(TimeflowApp app)
	{
		super(app, "Documentation & License Info...", null, "Read web documentation.");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Display.launchBrowser("http://wiki.github.com/FlowingMedia/TimeFlow/");
	}

}
