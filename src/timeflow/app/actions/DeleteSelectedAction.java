package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.ui.*;
import timeflow.app.*;
import timeflow.data.db.*;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class DeleteSelectedAction extends TimeflowAction {

	public DeleteSelectedAction(TimeflowApp app)
	{
		super(app, "Delete Selected Items...", null, "Delete the currently visible events");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		HashSet<Act> keepers=new HashSet<Act>(); // switching between sets and lists
		keepers.addAll(getModel().getDB().all()); // for efficiency. maybe silly?
		ActList selected=getModel().getActs();
		for (Act a: selected)
			keepers.remove(a);
		ActList keepList=new ActList(getModel().getDB());
		keepList.addAll(keepers);
		
		MassDeletePanel panel=new MassDeletePanel(getModel(), keepList, 
				"Delete all selected items.");
		Object[] options = {"Cancel", "Proceed"};
		int n = JOptionPane.showOptionDialog(app,
					panel,
					"Delete Selected",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE,
					null,
					options,
					"Proceed");
		panel.detachFromModel();
		if (n==1)
		{
			panel.applyAction();
			app.clearFilters();
			getModel().noteSchemaChange(this);
		}
	}

}
