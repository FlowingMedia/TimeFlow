package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.ui.*;
import timeflow.app.*;
import timeflow.data.db.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;

public class RenameFieldAction extends TimeflowAction {

	public RenameFieldAction(TimeflowApp app)
	{
		super(app, "Rename Field...", null, "Rename a field from this database");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JPanel panel=new JPanel();
		panel.setLayout(new GridLayout(4,1));
		panel.add(new JLabel("Choose a field and type a new name."));
		final JComboBox fieldChoices=new JComboBox();
		panel.add(fieldChoices);
		ArrayList<String> options=new ArrayList<String>();
		for (Field f: getModel().getDB().getFields())
			fieldChoices.addItem(f.getName());
		JPanel inputPanel=new JPanel();
		inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(new JLabel("New Name:"));
		final JTextField nameField=new JTextField(20);
		inputPanel.add(nameField);
		nameField.requestFocus();
		final JLabel feedback=new JLabel("(No name entered)");

		nameField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String name=nameField.getText();
				Field other=getModel().getDB().getField(name);
				//System.out.println("name="+name);
				if (name.trim().length()==0)
				{
					feedback.setText("(No name entered)");
				} else if (other!=null && !other.getName().equals(fieldChoices.getSelectedItem()))
				{
					feedback.setText("A field named '"+name+"' already exists.");
				} else
					feedback.setText("");
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}});
		
		panel.add(inputPanel);
		feedback.setForeground(Color.gray);
		panel.add(feedback);
		
		String[] o={"OK", "Cancel"};
		int n = JOptionPane.showOptionDialog(
				app,
				panel,
				"Rename Field",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null,
				o,
				o[0]);

		if (n==0)
		{
			Field old=getModel().getDB().getField((String)fieldChoices.getSelectedItem());
			String newName=nameField.getText();
			Field conflict=getModel().getDB().getField(newName);
			boolean tooSpacey=newName.trim().length()==0;
			if (tooSpacey)
				app.showUserError("Can't change the field name to be empty.");
			else if (conflict!=null && conflict!=old)
				app.showUserError("A field named '"+newName+"' already exists.");
			else
			{
				getModel().getDB().renameField(old, nameField.getText());
				getModel().noteSchemaChange(this);
			}
		}
	}
}
