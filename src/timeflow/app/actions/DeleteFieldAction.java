package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class DeleteFieldAction extends TimeflowAction {

	public DeleteFieldAction(TimeflowApp app)
	{
		super(app, "Delete Field...", null, "Delete a field from this database");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ArrayList<String> options=new ArrayList<String>();
		for (Field f: getModel().getDB().getFields())
			options.add(f.getName());
		String[] o=(String[])options.toArray(new String[0]);
		String fieldToDelete = (String)JOptionPane.showInputDialog(
		                    app,
		                    "Field to delete:",
		                    "Delete Field",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    o,
		                    o[0]);

		if (fieldToDelete!=null)
		{
			TFModel model=getModel();
			Field f=model.getDB().getField(fieldToDelete);
			model.getDB().deleteField(f);			
			model.noteSchemaChange(this);
			return;
		}	
	}
}
