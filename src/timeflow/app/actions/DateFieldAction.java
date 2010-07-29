package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;
import timeflow.format.field.FieldFormatCatalog;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class DateFieldAction extends TimeflowAction {

	public DateFieldAction(TimeflowApp app)
	{
		super(app, "Set Date Fields...", null, "Set date fields corresponding to start, end.");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DateFieldPanel.popWindow(app.model);
	}
}
