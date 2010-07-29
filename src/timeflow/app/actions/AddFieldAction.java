package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;
import timeflow.format.field.FieldFormatCatalog;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class AddFieldAction extends TimeflowAction {

	public AddFieldAction(TimeflowApp app)
	{
		super(app, "Add Field...", null, "Add a field to this database");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		AddFieldPanel p=new AddFieldPanel();
		Object[] options = {"Cancel", "Add Field"};
		int n = JOptionPane.showOptionDialog(app,
				p,
				"Add New Field To Database",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				"Add Field");
		if (n==1)
		{
			String fieldName=p.name.getText();
			TFModel model=getModel();
			if (fieldName.trim().length()==0)
				app.showUserError("Field names can't be all spaces!");
			else if (model.getDB().getField(fieldName)!=null)
				app.showUserError("That name is already taken!");
			else
			{
				model.getDB().addField(fieldName, FieldFormatCatalog.javaClass((String)p.typeChoices.getSelectedItem()));
				model.noteAddField(this);
			}
		}
		else
			System.out.println("Canceled!");
	}
}
