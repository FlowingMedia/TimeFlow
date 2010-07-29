package timeflow.app.ui;

import timeflow.format.field.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;

public class EditValuePanel extends JPanel
{
	FieldFormat parser;
	boolean longField;
	
	JLabel feedback=new JLabel()
	{
		public Dimension getPreferredSize()
		{
			Dimension d=super.getPreferredSize();
			return new Dimension(200, d.height);
		}
	};
	
	static final String space="   ";
	JTextComponent input;
	
	public EditValuePanel(String name, Object startValue, Class type, boolean longField)
	{
		parser=FieldFormatCatalog.getFormat(type);
		
		if (longField)
		{
			setLayout(new BorderLayout());
			JPanel top=new JPanel();
			top.setLayout(new GridLayout(2,2));
			top.add(new JPanel());
			top.add(new JPanel());			
			JLabel fieldLabel=new JLabel(space+name+"   (long)");
			top.add(fieldLabel);
			top.add(feedback);
			add(top, BorderLayout.NORTH);
			input=new JTextArea(5,60);
			((JTextArea)input).setLineWrap(true);
			((JTextArea)input).setWrapStyleWord(true);
			JScrollPane scroller=new JScrollPane(input);
			scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			add(scroller, BorderLayout.CENTER);
			add(new JPanel(), BorderLayout.WEST);
			add(new JPanel(), BorderLayout.SOUTH);
		}
		else
		{
			setLayout(new GridLayout(1,4));	
			JLabel fieldLabel=new JLabel(space+name);
			add(fieldLabel);
			JLabel typeLabel=new JLabel(FieldFormatCatalog.humanName(type));
			add(typeLabel);
			typeLabel.setForeground(Color.gray);
			input=new JTextField(8);
			add(input);			
			// enough room for "couldn't understand"
			add(feedback);
		}
		input.setText(startValue==null ? "" : parser.format(startValue));
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				parse();
			}
		});
		parse();
	}

	void parse()
	{
		try
		{
			parser.parse(input.getText());
		}
		catch (Exception e)
		{}
		feedback.setText("  "+parser.feedback());
		feedback.setForeground(parser.isUnderstood() ? Color.gray : Color.red);
	}
	
	public Object getInputValue()
	{
		try
		{
			return parser.parse(input.getText());
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public boolean isOK()
	{
		return parser.isUnderstood();
	}
	
	public Dimension getPreferredSize()
	{
		Dimension d=super.getPreferredSize();
		int w=Math.max(300, d.width);
		return new Dimension(w, d.height);
	}
	
}
