package timeflow.data.db;

import java.util.*;

import timeflow.data.db.filter.ActFilter;

public interface ActDB extends Iterable<Act> {
	
	public String getSource();
	public String getDescription();
	public void setSource(String source);
	public void setDescription(String description);
	
	public List<String> getFieldKeys();
	public List<Field> getFields();
	public List<Field> getFields(Class type);
	public Field addField(String name, Class type);
	public Field getField(String name);
	public void deleteField(Field field);
	public void setAlias(Field field, String name);
	public void setNewFieldOrder(List<Field> newOrder);
	public void renameField(Field field, String name);

	public void delete(Act act);
	public Act createAct();
	public ActList select(ActFilter filter);
	public ActList all();
	public int size();
	public Act get(int i);
}
