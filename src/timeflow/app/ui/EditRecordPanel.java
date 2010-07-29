package timeflow.app.ui;

import timeflow.model.*;
import timeflow.app.ui.ImportDelimitedPanel.SchemaPanel;
import timeflow.data.time.*;
import timeflow.data.db.*;

import javax.swing.*;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// panel with form for editing a given database entry
public class EditRecordPanel extends JPanel
{
	Act act;
	HashMap<Field, EditValuePanel> fieldUI=new HashMap<Field, EditValuePanel>();
	JButton submit, cancel;
	Dimension idealSize=new Dimension();
	TFModel model;
	
	private static void edit(final TFModel model, final Act act, final boolean isAdd)
	{
		final JFrame window=new JFrame(isAdd ? "Add Record" : "Edit Record");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		final EditRecordPanel editor=new EditRecordPanel(model, act);
		window.getContentPane().setLayout(new GridLayout(1,1));
		window.getContentPane().add(editor);
		editor.submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);
				editor.submitValues();
				model.noteAdd(this);
			}});
		editor.cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);
				if (isAdd)
				{
					model.getDB().delete(act);
				}
			}});
		window.setBounds(50,50,700,500);
		window.pack();
		window.setVisible(true);
	}
	
	public static void edit(TFModel model, Act act)
	{
		edit(model, act, false);
	}
	
	public static void add(TFModel model)
	{
		Act act=model.getDB().createAct();
		edit(model, act, true);
	}
	
	public static void add(TFModel model, RoughTime r)
	{
		Act act=model.getDB().createAct();
		act.set(act.getDB().getField(VirtualField.START), r);
		edit(model, act, true);
	}
	
	public EditRecordPanel(TFModel model, Act act)
	{
		this.model=model;
		this.act=act;
		
		setBackground(Color.white);
		setLayout(new BorderLayout());
		
		JPanel buttons=new JPanel();
		add(buttons, BorderLayout.SOUTH);
		buttons.setBackground(Color.lightGray);
		submit=new JButton("OK");
		buttons.add(submit);
		cancel=new JButton("Cancel");
		buttons.add(cancel);
		
		JPanel entryPanel=new JPanel();
		JScrollPane scroller=new JScrollPane(entryPanel);
		add(scroller, BorderLayout.CENTER);
		
		java.util.List<Field> fields=act.getDB().getFields();
		int n=fields.size();
		entryPanel.setLayout(null);
		
		DBUtils.setRecSizesFromCurrent(act.getDB());
		int top=0;
		
		for (Field f: fields)
		{
			EditValuePanel p=new EditValuePanel(f.getName(), act.get(f), 
					f.getType(), f.getRecommendedSize()>100);
			entryPanel.add(p);
			Dimension d=p.getPreferredSize();
			p.setBounds(0,top,d.width,d.height);
			top+=d.height;
			idealSize.width=Math.max(d.width+5, idealSize.width);
			idealSize.height=Math.max(top+45, idealSize.height);
			fieldUI.put(f, p);
		}
		
	}
	
	public Dimension getPreferredSize()
	{
		return idealSize;
	}

	public void submitValues()
	{
		for (Field f: fieldUI.keySet())
		{
			act.set(f, fieldUI.get(f).getInputValue());
		}
		model.noteRecordChange(this);
	}
}
