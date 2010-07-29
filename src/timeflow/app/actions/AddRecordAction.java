package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;
import timeflow.format.field.FieldFormatCatalog;

import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class AddRecordAction extends TimeflowAction {

	public AddRecordAction(TimeflowApp app)
	{
		super(app, "Add Record...", null, "Add a record to this database");
		accelerate('A');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		EditRecordPanel.add(getModel());
	}
}
