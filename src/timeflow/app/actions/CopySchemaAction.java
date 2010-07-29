package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;
import timeflow.format.field.FieldFormatCatalog;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class CopySchemaAction extends TimeflowAction {

	public CopySchemaAction(TimeflowApp app)
	{
		super(app, "New With Same Fields", null, 
				"Create a new, blank database with same fields as the current one.");
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		java.util.List<Field> fields=getModel().getDB().getFields();
		ActDB db=new BasicDB("Unspecified");
		for (Field f: fields)
			db.addField(f.getName(), f.getType());
		getModel().setDB(db, "[new data]", true, this);
	}
}
