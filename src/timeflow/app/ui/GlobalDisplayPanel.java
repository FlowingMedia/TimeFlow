package timeflow.app.ui;


import timeflow.app.ui.filter.FilterControlPanel;
import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.model.*;

import javax.swing.*;

import timeflow.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GlobalDisplayPanel extends ModelPanel {
	JPanel encodings=new JPanel();
	JPanel localControls=new JPanel();
	JPanel globalControls=new JPanel();
	CardLayout localCards=new CardLayout();
	
	public GlobalDisplayPanel(TFModel model, FilterControlPanel filterControls)
	{
		super(model);
		setBackground(Color.white);
		setLayout(new BorderLayout());
		 
		add(localControls, BorderLayout.CENTER);
		localControls.setBackground(Color.white);
		localControls.setLayout(localCards);
		
		JPanel p=new JPanel();
		p.setBackground(Color.white);
		p.setLayout(new BorderLayout());
		
		JPanel globalLabel=new JPanel();
		globalLabel.setLayout(new BorderLayout());
		
		JPanel topLine=new Pad(2,3);
		topLine.setBackground(Color.gray);
		globalLabel.add(topLine, BorderLayout.NORTH);
		
		JPanel bottomLine=new Pad(2,3);
		bottomLine.setBackground(Color.gray);
		globalLabel.add(bottomLine, BorderLayout.SOUTH);
		
		JLabel label=new JLabel(" Global Controls", JLabel.LEFT)
		{
			public Dimension getPreferredSize()
			{
				return new Dimension(30,30);
			}
		};
		label.setBackground(Color.lightGray);
		label.setForeground(Color.darkGray);
		globalLabel.add(label, BorderLayout.CENTER);
		p.add(globalLabel, BorderLayout.NORTH);
		
		JPanel global=new JPanel();
		global.setLayout(new BorderLayout());
		global.add(new StatusPanel(model, filterControls), BorderLayout.NORTH);
		
		encodings.setLayout(new GridLayout(4,1));
		encodings.setBackground(Color.white);
		global.add(encodings, BorderLayout.CENTER);
		p.add(global, BorderLayout.CENTER);
		add(p, BorderLayout.SOUTH);
		
		makeEncodingPanel();
	}
	
	public void showLocalControl(String name)
	{
		localCards.show(localControls, name);
	}
	
	public void addLocalControl(String name, JComponent control)
	{
		localControls.add(control, name);
	}
	
	void makeEncodingPanel()
	{
		encodings.removeAll();
		ActDB db=getModel().getDB();
		if (db==null)
			return;

		java.util.List<Field> dimensions=DBUtils.categoryFields(db);
		java.util.List<Field> measures=db.getFields(Double.class);
		
		makeChooser(VirtualField.LABEL, "Label", "None", db.getFields(String.class));		
		makeChooser(VirtualField.TRACK, "Groups", "None", dimensions);
		makeChooser(VirtualField.COLOR, "Color", "Same As Groups", dimensions);
		
		makeChooser(VirtualField.SIZE, "Dot Size", "None", measures);
	}
	
	private JComboBox makeChooser(final String alias, String title, String nothingLabel, List<Field> fields) {
		if (fields.size()==0)
			return null;
		JPanel panel=new JPanel();
		panel.setBackground(Color.white);
		panel.setLayout(new BorderLayout());
		JPanel topPad=new Pad(10,7);
		topPad.setBackground(Color.white);
		panel.add(topPad, BorderLayout.NORTH);

		JPanel rightPad=new Pad(10,10);
		panel.add(rightPad, BorderLayout.EAST);
		rightPad.setBackground(Color.white);
		
		panel.add(new JLabel(" "+title) {public Dimension getPreferredSize() {return new Dimension(60,25);}}, 
				BorderLayout.WEST);
		final JComboBox c=new JComboBox();
		
		if (nothingLabel!=null)
			c.addItem(nothingLabel);
		for (Field f: fields)
		{
			c.addItem(f.getName());
		}
		
		Field current=getModel().getDB().getField(alias);
		if (current!=null)
			c.setSelectedItem(current.getName());
		c.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Field realField=c.getSelectedIndex()==0 ? 
						null : getModel().getDB().getField((String)c.getSelectedItem());
				getModel().setFieldAlias(realField, alias, GlobalDisplayPanel.this);
			}});
		c.setBackground(Color.white);
		c.setBorder(null);
		panel.add(c, BorderLayout.CENTER);
		encodings.add(panel);
		c.setBorder(null);
		return c;
	}

	@Override
	public void note(TFEvent e) {
		if (e.affectsSchema())
			makeEncodingPanel();
	}
}
