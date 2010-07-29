package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.TimeflowApp;
import timeflow.app.ui.*;
import timeflow.data.db.*;
import timeflow.format.field.FieldFormatCatalog;
import timeflow.format.file.DelimitedFormat;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class ImportFromPasteAction extends TimeflowAction {

	public ImportFromPasteAction(TimeflowApp app)
	{
		super(app, "Paste From Spreadsheet / HTML...", null, "Import from copy-and-pasted data.");
	}
	
	public void actionPerformed(ActionEvent event) 
	{
		if (!app.checkSaveStatus())
			return;
		JTextArea text=new JTextArea(10,40);
		JScrollPane scroll=new JScrollPane(text);
		text.setText("Paste here! (replacing this :-)");
		text.setSelectionStart(0);
		text.setSelectionEnd(text.getText().length());
		Object[] options = {"Cancel", "Import"};
		int n = JOptionPane.showOptionDialog(app,
					scroll,
					"Import From Paste",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					"Import");
		if (n==1)
		{
			try
			{
				String pasted=text.getText();
				String[][] data=DelimitedFormat.readArrayFromString(pasted, System.out);
				app.showImportEditor("Paste", data);
			}
			catch (Exception e)
			{
				app.showUserError(e);
			}
		}
	}
}
