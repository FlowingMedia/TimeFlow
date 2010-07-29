package timeflow.app;

import timeflow.app.ui.*;
import timeflow.app.actions.*;
import timeflow.app.ui.filter.*;
import timeflow.data.db.*;
import timeflow.data.time.RoughTime;
import timeflow.format.field.*;
import timeflow.format.file.*;
import timeflow.model.*;
import timeflow.views.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import timeflow.util.Pad;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;

public class TimeflowApp extends JFrame
{
	public TFModel model=new TFModel();
	public JFileChooser fileChooser;
	
	AboutWindow splash;
	String[][] examples;
	String[] templates;

	AppState state=new AppState();
	JMenu openRecent=new JMenu("Open Recent");
	public JMenu filterMenu;
	JMenuItem save=new JMenuItem("Save");
	FilterControlPanel filterControlPanel;
	LinkTabPane leftPanel;

	TFListener filterMenuMaker=new TFListener()
	{
		@Override
		public void note(TFEvent e) {
			if (e.affectsSchema())
			{
				filterMenu.removeAll();
				for (Field f: model.getDB().getFields())
				{
					if (f.getType()==String.class || f.getType()==String[].class || 
							f.getType()==Double.class || f.getType()==RoughTime.class)
					{
						final JCheckBoxMenuItem item=new JCheckBoxMenuItem(f.getName());
						final Field field=f;
						filterMenu.add(item);
						item.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								filterControlPanel.setFacet(field, item.getState());
								leftPanel.setSelectedIndex(1);
							}});
					}
				}
			}
		}		
	};

	void splash(boolean visible)
	{
		splash.setVisible(visible);
	}
	
	public void init() throws Exception
	{
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0,0,Math.min(d.width, 1200), Math.min(d.height, 900));
		setTitle(Display.version());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		final QuitAction quitAction=new QuitAction(this, model);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quitAction.quit();
			}
			public void windowStateChanged(WindowEvent e) {
				repaint();
			}
		});		
		Image icon = Toolkit.getDefaultToolkit().getImage("images/icon.gif");
		setIconImage(icon);
		
		// read example directory
		String[] ex=getVisibleFiles("settings/examples");
		int n=ex.length;
		examples=new String[n][2];
		for (int i=0; i<n; i++)
		{
			String s=ex[i];
			int dot=s.lastIndexOf('.');
			if (dot>=0 && dot<s.length()-1);
				s=s.substring(0,dot);
			examples[i][0]=s;
			examples[i][1]="settings/examples/"+ex[i];
		}
		templates=getVisibleFiles("settings/templates");
		fileChooser=new JFileChooser(state.getCurrentFile());
		
		getContentPane().setLayout(new BorderLayout());	
		
		// left tab area, with vertical gray divider.
		JPanel leftHolder=new JPanel();
		getContentPane().add(leftHolder, BorderLayout.WEST);
		
		leftHolder.setLayout(new BorderLayout());
		JPanel pad=new Pad(3,3);
		pad.setBackground(Color.gray);
		leftHolder.add(pad, BorderLayout.EAST);
		
		leftPanel=new LinkTabPane();//JTabbedPane();
		leftHolder.add(leftPanel, BorderLayout.CENTER);
		
		JPanel configPanel=new JPanel();
		configPanel.setLayout(new BorderLayout());		
		filterMenu=new JMenu("Filters");
		filterControlPanel=new FilterControlPanel(model, filterMenu);
		final GlobalDisplayPanel displayPanel=new GlobalDisplayPanel(model, filterControlPanel);
		configPanel.add(displayPanel, BorderLayout.NORTH);
		
		JPanel legend=new JPanel();
		legend.setLayout(new BorderLayout());
		configPanel.add(legend, BorderLayout.CENTER);	
		legend.add(new SizeLegendPanel(model), BorderLayout.NORTH);
		legend.add(new ColorLegendPanel(model), BorderLayout.CENTER);		
		leftPanel.addTab(configPanel, "Display", true);

		leftPanel.addTab(filterControlPanel, "Filter", true);
		
		// center tab area
		
		final LinkTabPane center=new LinkTabPane();
		getContentPane().add(center, BorderLayout.CENTER);
		
		center.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				displayPanel.showLocalControl(center.getCurrentName());
			}
		});		
			
		final IntroView intro=new IntroView(model); // we refer to this a bit later.
		final TimelineView timeline=new TimelineView(model);
		AbstractView[] views={
				timeline,
				new CalendarView(model),
				new ListView(model),
				new TableView(model),
				new BarGraphView(model),
				intro,
				new DescriptionView(model),
				new SummaryView(model),
		};

		for (int i=0; i<views.length; i++)
		{
			center.addTab(views[i], views[i].getName(), i<5);
			displayPanel.addLocalControl(views[i].getName(), views[i].getControls());
		}
		
		// start off with intro screen
		center.setCurrentName(intro.getName());
		displayPanel.showLocalControl(intro.getName());
		
		// but then, once data is loaded, switch directly to the timeline view.
		model.addListener(new TFListener() {
			@Override
			public void note(TFEvent e) {
				if (e.type==e.type.DATABASE_CHANGE)
				{
					if (center.getCurrentName().equals(intro.getName()))
					{
						center.setCurrentName(timeline.getName());
						displayPanel.showLocalControl(timeline.getName());
					}
				}
			}});

		JMenuBar menubar=new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu fileMenu=new JMenu("File");
		menubar.add(fileMenu);
			
		fileMenu.add(new NewDataAction(this));
		fileMenu.add(new CopySchemaAction(this));
		
		JMenu templateMenu=new JMenu("New From Template");
		fileMenu.add(templateMenu);
		for (int i=0; i<templates.length; i++)
		{
			JMenuItem t=new JMenuItem(templates[i]);
			final String fileName="settings/templates/"+templates[i];
			templateMenu.add(t);
			t.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					load(fileName, FileExtensionCatalog.get(fileName), true);
				}});
		}
		
		fileMenu.addSeparator();

		
		JMenuItem open=new JMenuItem("Open...");
		fileMenu.add(open);
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load(new TimeflowFormat(), false);
			}});
		
		
		fileMenu.add(openRecent);
		makeRecentFileMenu();		
		fileMenu.addSeparator();		
		fileMenu.add(new ImportFromPasteAction(this));
		
		JMenuItem impDel=new JMenuItem("Import CSV/TSV...");
		fileMenu.add(impDel);
		impDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkSaveStatus())
					importDelimited();
			}});

		fileMenu.addSeparator();
		
		fileMenu.add(save);
		save.setAccelerator(KeyStroke.getKeyStroke('S',
			    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		save.setEnabled(false);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(model.getDbFile());
				
			}});
		model.addListener(new TFListener() {
			@Override
			public void note(TFEvent e) {
				save.setEnabled(!model.getReadOnly());
			}});
		
		JMenuItem saveAs=new JMenuItem("Save As...");
		fileMenu.add(saveAs);
		saveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}});	
		
		fileMenu.addSeparator();
		
		JMenuItem exportTSV=new JMenuItem("Export TSV...");
		fileMenu.add(exportTSV);
		exportTSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportDelimited('\t');
			}});			
		JMenuItem exportCSV=new JMenuItem("Export CSV...");
		fileMenu.add(exportCSV);
		exportCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportDelimited(',');
			}});	
		JMenuItem exportHTML=new JMenuItem("Export HTML...");
		fileMenu.add(exportHTML);
		exportHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportHtml();
			}});	
		fileMenu.addSeparator();		
		fileMenu.add(quitAction);
		
		JMenu editMenu=new JMenu("Edit");
		menubar.add(editMenu);
		editMenu.add(new AddRecordAction(this));
		editMenu.addSeparator();
		editMenu.add(new DateFieldAction(this));
		editMenu.add(new AddFieldAction(this));
		editMenu.add(new RenameFieldAction(this));
		editMenu.add(new DeleteFieldAction(this));
		editMenu.add(new ReorderFieldsAction(this));
		editMenu.addSeparator();
		editMenu.add(new EditSourceAction(this));		
		editMenu.addSeparator();		
		editMenu.add(new DeleteSelectedAction(this));
		editMenu.add(new DeleteUnselectedAction(this));

		menubar.add(filterMenu);
		model.addListener(filterMenuMaker);

	
		JMenu exampleMenu=new JMenu("Examples");
		menubar.add(exampleMenu);
		
		for (int i=0; i<examples.length; i++)
		{
			JMenuItem example=new JMenuItem(examples[i][0]);
			exampleMenu.add(example);
			final String file=examples[i][1];
			example.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					load(file, FileExtensionCatalog.get(file), true);
				}});
		}
		
		JMenu helpMenu=new JMenu("Help");
		menubar.add(helpMenu);
		
		helpMenu.add(new WebDocAction(this));
		
		JMenuItem about=new JMenuItem("About TimeFlow");
		helpMenu.add(about);
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				splash(true);
			}});
		
		model.addListener(new TFListener() {

			@Override
			public void note(TFEvent e) {
				if (e.type==TFEvent.Type.DATABASE_CHANGE)
				{
					String name=model.getDbFile();
					int n=Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
					if (n>0)
						name=name.substring(n+1);
					setTitle(name);
				}
			}});
	}
	
	void makeRecentFileMenu()
	{
		openRecent.removeAll();
		try
		{
			for (File f:state.getRecentFiles())
			{
				final String file=f.getAbsolutePath();
				JMenuItem m=new JMenuItem(f.getName());
				openRecent.add(m);
				m.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						load(file, FileExtensionCatalog.get(file), false);
					}});
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	void exportHtml()
	{
		int retval = fileChooser.showSaveDialog(this);
	    if (retval == fileChooser.APPROVE_OPTION)
	    {   	    	
	    	String fileName=fileChooser.getSelectedFile().getAbsolutePath();
	    	try
			{
	    		FileWriter fw=new FileWriter(fileName);
	    		BufferedWriter out=new BufferedWriter(fw);
				new HtmlFormat().export(model, out);
				out.close();
				fw.close();
			}
			catch (Exception e)
			{
				System.out.println(e);
				showUserError("Couldn't save file: "+e);
			}
	    }
	}

	void exportDelimited(char delimiter)
	{
		int retval = fileChooser.showSaveDialog(this);
	    if (retval == fileChooser.APPROVE_OPTION)
	    {   	    	
	    	String fileName=fileChooser.getSelectedFile().getAbsolutePath();
	    	try
			{
				new DelimitedFormat(delimiter).write(model.getDB(), new File(fileName));
			}
			catch (Exception e)
			{
				System.out.println(e);
				showUserError("Couldn't save file: "+e);
			}
	    }
 	}

	void load(Import importer, boolean readOnly)
	{
		if (!checkSaveStatus())
			return;
        try {
    	    int retval = fileChooser.showOpenDialog(this);
    	    if (retval == fileChooser.APPROVE_OPTION)
    	    {
    	    	load(fileChooser.getSelectedFile().getAbsolutePath(), importer, readOnly);
    	    	noteFileUse(fileChooser.getSelectedFile().getAbsolutePath());
     	    }
         } catch (Exception e) {
        	showUserError("Couldn't read file.");
            System.out.println(e);
        }
	}	
	
	public void showImportEditor(String fileName, String[][] data)
	{
		final ImportDelimitedPanel editor=new ImportDelimitedPanel(model);
		editor.setFileName(fileName);
		editor.setData(data);
		editor.setBounds(0,0,1024,768);
		editor.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() 
   	    {
   	    	public void run() {
   	    		editor.scrollToTop();
   	    	}
   	    });

	}
	
	void importDelimited()
	{
		if (!checkSaveStatus())
			return;
		try
		{
			int result = fileChooser.showOpenDialog(this);

	        if (result == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            String fileName=file.getAbsolutePath();
	            noteFileUse(fileName);
	    		String[][] data=DelimitedFormat.readArrayGuessDelim(fileName, System.out);
	    		showImportEditor(fileName, data);
	    		
	        } else {
	            System.out.println("OK, canceling import.");
	        }
		}
		catch (Exception e) 
		{
			showUserError("Couldn't read file format.");
			e.printStackTrace(System.out);
		}
	}
	
	void load(final String fileName, final Import importer, boolean readOnly)
	{
		if (!checkSaveStatus())
			return;
		try
		{
			final File f=new File(fileName);
			ActDB db=importer.importFile(f);	
			model.setDB(db, fileName, readOnly, TimeflowApp.this);
			if (!readOnly)
				noteFileUse(fileName);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			showUserError("Couldn't read file.");
			model.noteError(this);
		}
	}
	
	public boolean save(String fileName)
	{
		try
		{
			FileWriter fw=new FileWriter(fileName);
			BufferedWriter out=new BufferedWriter(fw);
			new TimeflowFormat().export(model, out);
			out.close();
			fw.close();
			noteFileUse(fileName);
			if (!fileName.equals(model.getDbFile()))
				model.setDbFile(fileName, false, this);
			model.setChangedSinceSave(false);
			model.setReadOnly(false);
			save.setEnabled(true);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			showUserError("Couldn't save file: "+e);
			return false;
		}
	}
    
	public boolean checkSaveStatus()
	{
		boolean needSave=model.isChangedSinceSave();
		if (!needSave)
			return true;

		Object[] options=null;
		if (model.isReadOnly())
			options= new Object[] {"Save As", "Discard Changes", "Cancel"};
		else
			options= new Object[] {"Save", "Save As", "Discard Changes", "Cancel"};
		int n = JOptionPane.showOptionDialog(
				this, 
		    "The current data set has unsaved changes that will be lost.\n"+
		    "Would you like to save them before continuing?",
		    "Save Before Closing?",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    model.isReadOnly() ? "Save As" : "Save");
		Object result=options[n];
		if ("Discard Changes".equals(result))
			return true;
		if ("Cancel".equals(result))
			return false;
		if ("Save".equals(result))
		{
			return save(model.getDbFile());
		}
		
		// we are now at "save as..."
		return saveAs();
	}
	
	public boolean saveAs()
	{
		File current=fileChooser.getSelectedFile();
		if (current!=null)
			fileChooser.setSelectedFile(new File(current.getAbsolutePath()+" (copy)"));
		int retval = fileChooser.showSaveDialog(this);
	    if (retval == fileChooser.APPROVE_OPTION)
	    {   	    	
	    	String fileName=fileChooser.getSelectedFile().getAbsolutePath();
	    	model.setReadOnly(false);
	    	save.setEnabled(true);
	    	return save(fileName);
	    }
	    else
	    	return false;
 	}
	
	public void showUserError(Object o)
	{
		JOptionPane.showMessageDialog(this,
			    o,
			    "A problem occurred",
			    JOptionPane.ERROR_MESSAGE);
		if (o instanceof Exception)
		{
			((Exception)o).printStackTrace(System.out);
		}
	}
    
	public void noteFileUse(String file)
	{

		state.setCurrentFile(new File(file));
		state.save();
		makeRecentFileMenu();

	}
	
	public void clearFilters()
	{
		filterControlPanel.clearFilters();
	}

	static String[] getVisibleFiles(String dir)
	{
		String[] s=new File(dir).list();
		ArrayList<String> real=new ArrayList<String>();
		for (int i=0; i<s.length; i++)
			if (!s[i].startsWith("."))
				real.add(s[i]);
		return (String[])real.toArray(new String[0]);
	}
}
