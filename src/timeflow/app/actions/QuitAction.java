package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;
import timeflow.format.field.FieldFormatCatalog;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class QuitAction extends TimeflowAction {

	public QuitAction(TimeflowApp app, TFModel model)
	{
		super(app, "Quit", null, "Quit the program");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		quit();
	}
	
	public void quit()
	{
		if (app.checkSaveStatus())
		{
			System.exit(0);
		}
	}
}
