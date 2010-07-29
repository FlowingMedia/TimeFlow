package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;
import timeflow.format.field.FieldFormatCatalog;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class NewDataAction extends TimeflowAction {

	public NewDataAction(TimeflowApp app)
	{
		super(app, "New", null, "Create a new, blank database");
		accelerate('N');

	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if (app.checkSaveStatus())
			getModel().setDB(new BasicDB("Unspecified"), "[new data]", true, this);
	}
}
