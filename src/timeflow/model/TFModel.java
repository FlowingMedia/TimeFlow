package timeflow.model;

import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.data.time.*;


import java.util.*;

// encapsulates all properties of a timeline model:
// data, display properties, etc.
// also does listening, etc.

public class TFModel {
	
	private ActDB db;
	private ActList acts;
	private ActFilter filter=new ConstFilter(true);
	private ArrayList<TFListener> listeners=new ArrayList<TFListener>();
	private Display display=new Display();

	private String[] labelGuesses={"label", "LABEL", "Label", "title", "TITLE", "Title",
			"name", "Name", "NAME"};
	private String[] startGuesses={"start", "Start", "START"};
	
	private String dbFile="[unknown source]";
	private boolean changedSinceSave;
	private boolean readOnly;
	private double minSize, maxSize;
	private Interval viewInterval;
	
	public ValueFilter getGrayFilter() {
		return display.grayFilter;
	}

	public void setGrayFilter(ValueFilter grayFilter, Object origin) {
		display.grayFilter = grayFilter;
		fireEvent(new TFEvent(TFEvent.Type.FILTER_CHANGE, origin));
	}

	public ActFilter getFilter()
	{
		return filter;
	}
	
	public Interval getViewInterval()
	{
		return viewInterval;
	}
	
	public void setViewInterval(Interval viewInterval)
	{
		this.viewInterval=viewInterval;
	}
	
	public double getMinSize() {
		return minSize;
	}

	public void setMinSize(double minSize) {
		this.minSize = minSize;
	}

	public double getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(double maxSize) {
		this.maxSize = maxSize;
	}

	public String getDbFile() {
		return dbFile;
	}
	
	public boolean getReadOnly()
	{
		return readOnly;
	}

	public void setDbFile(String dbFile, boolean readOnly, Object origin) {
		this.dbFile = dbFile;
		fireEvent(new TFEvent(TFEvent.Type.SOURCE_CHANGE, origin));
	}

	public boolean isChangedSinceSave() {
		return changedSinceSave;
	}

	public void setChangedSinceSave(boolean changedSinceSave) {
		this.changedSinceSave = changedSinceSave;
	}
	
	public Display getDisplay()
	{
		return display;
	}
	
	public ActDB getDB()
	{
		return db;
	}
	
	public ActList getActs()
	{
		return acts;
	}
	
	public void addListener(TFListener t)
	{
		listeners.add(t);
	}


	public void removeListener(TFListener t)
	{
		listeners.remove(t);
	}
	
	public void noteNewDescription(Object origin)
	{
		setChangedSinceSave(true);
		fireEvent(new TFEvent(TFEvent.Type.DESCRIPTION_CHANGE, origin));
	}
	
	public void noteNewSource(Object origin)
	{
		setChangedSinceSave(true);
		fireEvent(new TFEvent(TFEvent.Type.SOURCE_CHANGE, origin));
	}
	
	public void noteRecordChange(Object origin)
	{
		setChangedSinceSave(true);
		fireEvent(new TFEvent(TFEvent.Type.ACT_CHANGE, origin));
	}
	
	public void noteAddField(Object origin)
	{
		setChangedSinceSave(true);
		fireEvent(new TFEvent(TFEvent.Type.FIELD_ADD, origin));
	}
	
	public void noteError(Object origin)
	{
		fireEvent(new TFEvent(TFEvent.Type.ERROR, origin));
	}
	
	public void noteDelete(Object origin)
	{
		setChangedSinceSave(true);
		updateActs();
		fireEvent(new TFEvent(TFEvent.Type.ACT_DELETE, origin));
	}
	
	public void noteSchemaChange(Object origin)
	{
		setChangedSinceSave(true);
		updateActs();
		fireEvent(new TFEvent(TFEvent.Type.DATABASE_CHANGE, origin)); // @TODO: make schema change? 
	}
	
	public void noteAdd(Object origin)
	{
		setChangedSinceSave(true);
		updateActs();
		fireEvent(new TFEvent(TFEvent.Type.ACT_ADD, origin));
	}
	
	public void setFilter(ActFilter filter, Object origin)
	{
		this.filter=filter;
		updateActs();
		fireEvent(new TFEvent(TFEvent.Type.FILTER_CHANGE, origin));
	}
	
	private void updateActs()
	{
		acts=db.select(filter);
	}
	
	public void setDB(ActDB db, String dbFile, boolean readOnly, Object origin)
	{
		this.db=db;
		this.dbFile=dbFile;
		this.readOnly=readOnly;
		setChangedSinceSave(false);
		this.filter=new ConstFilter(true);
		this.acts=db.all();
		initVisualEncodings();
		refreshColors();
		fireEvent(new TFEvent(TFEvent.Type.DATABASE_CHANGE, origin));
	}
	
	private void initVisualEncodings()
	{
		guessField(VirtualField.LABEL, labelGuesses, String.class);
		guessField(VirtualField.START, startGuesses, RoughTime.class);
		viewInterval=null;
	}
	
	public void refreshColors()
	{
		display.grayFilter=null;
		Field colorField=getColorField();
		if (colorField==null)
			return;
		List<String> top25=DBUtils.countValues(db, colorField).listTop(25);
		display.refreshColors(top25);
	}
	
	private void guessField(String name, String[] guesses, Class type)
	{
		Field field=db.getField(name);
		if (field==null)
		{
			for (int i=0; i<guesses.length; i++)
			{
				Field f=db.getField(guesses[i]);
				if (f!=null && f.getType()==type)
				{
					field=f;
					break;
				}
			}
			if (field==null)
			{
				List<Field> f=db.getFields(type);
				if (f.size()>0)
					field=f.get(0);
			}
			if (field!=null)
				db.setAlias(field, name);
		}
	}
	
	private void fireEvent(TFEvent e)
	{
		// clone list before going through it, because some events can cause
		// listeners to be added or removed.
		
		for (TFListener t: (List<TFListener>)listeners.clone())
			if (t!=e.origin)
				t.note(e);				
	}

	public void setFieldAlias(Field field, String alias, Object origin)
	{
		db.setAlias(field, alias);
		if (db.size()>0 && field==getColorField())
			refreshColors();
		fireEvent(new TFEvent(TFEvent.Type.VIEW_CHANGE, origin));
	}
	
	public Field getColorField()
	{
		if (db==null)
			return null;
		Field f=db.getField(VirtualField.COLOR);
		if (f!=null)
			return f;
		return db.getField(VirtualField.TRACK);
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
}
