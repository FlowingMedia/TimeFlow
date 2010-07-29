package timeflow.app.ui;

import timeflow.app.*;
import timeflow.format.field.FieldFormatCatalog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class AddFieldPanel extends JPanel {
	public JTextField name=new JTextField(12);
	public JComboBox typeChoices=new JComboBox();
	public AddFieldPanel()
	{
		for (String choice: FieldFormatCatalog.classNames())
			typeChoices.addItem(choice);
		setLayout(new GridLayout(2,2));
		add(new JLabel("Field Name"));
		add(name);
		add(new JLabel("Field Type"));
		add(typeChoices);
	}
}
