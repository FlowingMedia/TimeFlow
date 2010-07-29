package timeflow.app.ui;

import timeflow.model.*;
import timeflow.data.time.*;
import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.format.field.*;
import timeflow.format.file.TimeflowFormat;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;


public class DateFieldPanel extends JPanel
{
	TFModel model;
	int numRows;
	HashMap<String, Integer> numBad=new HashMap<String, Integer>();
	private static String[] mappable={VirtualField.START, VirtualField.END};
	JLabel status=new JLabel("");
	FieldMap[] panels=new FieldMap[mappable.length];
	JButton submit, cancel;
	
	public DateFieldPanel(TFModel model, boolean hasButtons)
	{
		this.model=model;
		
		ActDB db=model.getDB();
		numRows=db.size();
		ActList all=db.all();
		
		
		// calculate stats.
		for (Field f: db.getFields(RoughTime.class))
		{
			int bad=DBUtils.count(all, new MissingValueFilter(f));
			numBad.put(f.getName(),bad);
		}
		
		setLayout(new BorderLayout());
		JPanel top=new JPanel();
		if (hasButtons)
		{
			submit=new JButton("Submit");
			top.add(submit);
			cancel=new JButton("Cancel");
			top.add(cancel);
		}
		else
		{
			JLabel about=new JLabel("Dates");
			top.add(about);
		}
		top.add(status);
		status.setForeground(Color.red);
		add(top, BorderLayout.SOUTH);
		JPanel bottom=new JPanel();
		add(bottom, BorderLayout.CENTER);
		bottom.setLayout(new GridLayout(mappable.length,1));
		
		// add panels.
		for (int i=0; i<mappable.length; i++)
		{
			panels[i]=new FieldMap(mappable[i],i==0);
			bottom.add(panels[i]);
		}
		
		
		// to do: add a status field or something
		// note inconsistencies, like:
		// * no start defined.
		// * ends after starts
	}
	
	public void map()
	{
		ActDB db=model.getDB();
		for (int i=0; i<panels.length; i++)
		{
			String choice=(String)panels[i].choices.getSelectedItem();
			db.setAlias("None".equals(choice) ? null : db.getField(choice), panels[i].name);
		} 
		model.noteSchemaChange(this);
	}
	
	class FieldMap extends JPanel
	{
		String name;
		int bad;
		JComboBox choices;
		JLabel definedLabel=new JLabel("# def goes here");
		boolean important;
		
		FieldMap(String name, boolean important)
		{
			this.name=name;
			this.important=important;
			setBackground(Color.white);
			setLayout(new GridLayout(1,3));
			
			add(new JLabel("   "+(important? "* ":"")+VirtualField.humanName(name)));//, BorderLayout.NORTH);
			
			choices=new JComboBox();
			choices.addItem("None");
			for (Field f: model.getDB().getFields(RoughTime.class))
			{				
				choices.addItem(f.getName());//+"  "+percentDef+"% defined");
			}
			add(choices);
			choices.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showDefined();
				}});
			
			add(definedLabel);
			definedLabel.setForeground(Color.gray);
			
			Field current=model.getDB().getField(name);
			if (current!=null)
				choices.setSelectedItem(current.getName());
			
			showDefined();
		}
		
		void showDefined()
		{
			String choice=(String)choices.getSelectedItem();
			String val="";
			boolean none="None".equals(choice);
			int percentDef=0;
			if (!none)
			{
				percentDef=(int)(100*(1-numBad.get(choice)/(double)numRows));
				val="  "+percentDef+"% defined";
			}
			definedLabel.setText(val);
			if (important)
			{
				if (none)
					status.setText("   Need \"Start\" for timeline/calendar.");
				else if (percentDef==0)
					status.setText("   No dates defined in "+choice+".");
				else
					status.setText("");
			}
		}
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(400,80+mappable.length*25);
	}
	
	public static void popWindow(TFModel model)
	{
		final JFrame window=new JFrame("Date Fields");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new GridLayout(1,1));
		final DateFieldPanel p=new DateFieldPanel(model, true);
		p.submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p.map();
				window.setVisible(false);
			}});
		p.cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);
			}});
		window.getContentPane().add(p);
		window.setBounds(50,50,window.getPreferredSize().width,window.getPreferredSize().height);
		window.setVisible(true);
	}
}
