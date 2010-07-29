package timeflow.app.ui;

import timeflow.data.time.*;
import timeflow.data.db.*;
import timeflow.format.field.*;
import timeflow.format.file.DelimitedFormat;
import timeflow.model.*;

import timeflow.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ImportDelimitedPanel extends JFrame 
{
	String fileName;
	SchemaPanel schemaPanel;
	boolean exitOnClose=false; // for testing!
	JScrollPane scroller;
	TFModel model;
	JLabel numLinesLabel=new JLabel();
	
	// for testing:
	public static void main(String[] args) throws Exception
	{
		System.out.println("Starting test of ImportEditor");
		String file="data/probate.tsv";
		String[][] data=DelimitedFormat.readArrayGuessDelim(file, System.out);
		ImportDelimitedPanel editor=new ImportDelimitedPanel(new TFModel());
		editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editor.setFileName(file);
		editor.setData(data);
		editor.setBounds(50,50,900,800);
		editor.setVisible(true);
		editor.exitOnClose=true;
	}
	
	public ImportDelimitedPanel(final TFModel model)
	{
		super("Import File");
		this.model=model;
		setBackground(Color.white);
		
		setLayout(new BorderLayout());
		JPanel top=new JPanel();
		add(top, BorderLayout.NORTH);
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		top.setBackground(Color.lightGray);
		top.add(numLinesLabel);
		final JTextField source=new JTextField(12);
		
		JButton done=new JButton("Import This");
		top.add(done);
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setDB(schemaPanel.makeDB(source.getText()), fileName, true, this);
				setVisible(false);
				if (exitOnClose)
					System.exit(0);
			}});
		
		JButton cancel=new JButton("Cancel");
		top.add(cancel);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				if (exitOnClose)
					System.exit(0);
			}});
		
		top.add(new JLabel("        Enter A Source:"));		
		top.add(source);
		schemaPanel=new SchemaPanel();
		schemaPanel.setBackground(Color.white);
		scroller=new JScrollPane(schemaPanel);
		add(scroller, BorderLayout.CENTER);
	}

	public void scrollToTop()
	{
		scroller.getViewport().setViewPosition(new Point(0,0));  
	}
	
	public void setFileName(String fileName)
	{
		this.fileName=fileName;
	}
	
	public void setData(String[][] data)
	{
		numLinesLabel.setText((data.length-1)+" records read.  ");
		schemaPanel.display(data);
	}

	class SchemaPanel extends JPanel
	{
		int numFields, rows;
		String[][] data;
		ArrayList<FieldPanel> panels=new ArrayList<FieldPanel>();
		
		ActDB makeDB(String source)
		{
			// count number of fields that are not ignored.
			int n=0;
			for (FieldPanel fp: panels)
				if (!fp.ignore.isSelected())
					n++;
			
			Class[] types=new Class[n];
			String[] fieldNames=new String[n];
			int[] index=new int[n];
			if (source.trim().length()==0)
				source="[source unspecified]";
			int i=0, j=0;
			for (FieldPanel fp: panels)
			{			
				if (!fp.ignore.isSelected())
				{
					fieldNames[i]=fp.fieldName;
					String typeChoice=(String)fp.typeChoices.getSelectedItem();
					Class type=FieldFormatCatalog.javaClass(typeChoice);
					System.out.println("Type: "+type+" for: "+typeChoice+" from "+fp.fieldName);
					types[i]=type;
					index[i]=j;
					i++;
				}
				j++;
			}
			
			ActDB db= new ArrayDB(fieldNames, types, source);
			HashMap<Integer, StringBuffer> errors=new HashMap<Integer, StringBuffer>();
			for (i=1; i<data.length; i++)
			{
				Act act=db.createAct();				
				for (int k=0; k<n; k++)
				{
					j=index[k];
					String s=data[i][j];
					Field f=db.getField(fieldNames[k]);
					FieldFormat format=FieldFormatCatalog.getFormat(types[k]);
					try
					{
						Object o=format.parse(s);
						act.set(f,o);
					}
					catch (Exception e)
					{
						StringBuffer b=errors.get(i-1);
						if (b==null)
						{
							b=new StringBuffer();
							errors.put(i-1,b);
						}
						else
							b.append("; ");
						b.append(f.getName()+":"+s);
					}
				}
			}
			
			if (errors.size()>0)
			{
				Field error=db.addField("UNPARSED FIELDS", String.class);
				for (int row:errors.keySet())
				{
					db.get(row).set(error, errors.get(row).toString());
				}
			}
			
			for (j=0; j<n; j++)
			{
				System.out.println(db.getField(fieldNames[j]));
			}
			
			return db;
		}
		
		void display(String[][] data)
		{
			removeAll();
			
			this.data=data;
			
			// analyze data.
			Class[] guesses=FieldFormatGuesser.analyze(data, 1, 100);
			
			// go through first row, which is headers.
			String[] headers=data[0];
			
			// if there are duplicate headers, add indicators.
			HashSet<String> h=new HashSet<String>();
			for (int i=0; i<headers.length; i++)
			{
				String base=headers[i];
				int j=2;
				String name=base;
				while (h.contains(name))
				{
					name=base+" "+j;
					j++;
				}
				headers[i]=name;
				h.add(name);
			}
			
			numFields=headers.length;
			int cols=2;
			rows=(int)Math.ceil(numFields/2.0);
			setLayout(new GridLayout(rows, cols));
			for (int i=0; i<numFields; i++)
			{
				Bag<String> vals=new Bag<String>();
				
				for (int j=1; j<data.length; j++)
				{
					vals.add(data[j][i]);
				}
				java.util.List<String> top=vals.listTop(5);
				int n=top.size();
				String[] samples=new String[n];
				for (int j=0; j<n; j++)
				{
					String s=top.get(j);
					samples[j]=(s.length()==0 ? "*MISSING*" : s)+"    ("+vals.num(s)+" times)";
				}
				
				JPanel p=new JPanel();
				add(p);
				p.setLayout(new BorderLayout());
				FieldPanel f=new FieldPanel(headers[i], samples, guesses[i]);
				panels.add(f);
				p.add(f, BorderLayout.CENTER);
				JPanel hr=new JPanel();
				hr.setPreferredSize(new Dimension(20,20));
				p.add(hr, BorderLayout.SOUTH);
			}
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(400,150*rows);
		}
	}
	
	class FieldPanel extends JPanel
	{
		JComboBox typeChoices;
		String fieldName;
		JCheckBox ignore;
		JLabel fieldLabel;
		int x1=5, y1=20, y3=150,x2=150, x3=150, x4=375, dh=2;

		
		FieldPanel(String fieldName, String[] sampleValues, Class typeGuess)
		{
			// just going with a null layout here, because it's a lot simpler!
			
			setLayout(null);
			setBackground(Color.white);
			this.fieldName=fieldName;
			
			fieldLabel=new JLabel(" \""+fieldName+'"');	
			fieldLabel.setFont(model.getDisplay().big());
			add(fieldLabel);
			fieldLabel.setBounds(x1,y1,fieldLabel.getPreferredSize().width, fieldLabel.getPreferredSize().height);
			
			typeChoices=new JComboBox();
			for (String choice: FieldFormatCatalog.classNames())
				typeChoices.addItem(choice);
			typeChoices.setSelectedItem(FieldFormatCatalog.humanName(typeGuess));
			add(typeChoices);
			int y2=fieldLabel.getY()+fieldLabel.getHeight()+dh+5;
			typeChoices.setBounds(x1,y2,
					typeChoices.getPreferredSize().width, typeChoices.getPreferredSize().height);
				
			ignore=new JCheckBox("Ignore Field");			
			add(ignore);
			ignore.setBounds(x1,typeChoices.getY()+typeChoices.getHeight()+dh,
					ignore.getPreferredSize().width, ignore.getPreferredSize().height);
			ignore.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Color c=ignore.isSelected() ? Color.gray : Color.black;
					fieldLabel.setForeground(c);
					typeChoices.setForeground(c);
				}});
			
			JTextArea values=new JTextArea();
			values.setForeground(Color.gray);
			for (int i=0; i<sampleValues.length; i++)
				values.append(sampleValues[i]+"\n");
			add(values);
			values.setBounds(x3,y2,x4-x3,y3-y2);
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(500,150);
		}
	}
}
