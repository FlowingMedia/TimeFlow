package timeflow.data.db;

import java.util.*;

import timeflow.data.db.*;
import timeflow.data.db.filter.ActFilter;

public class BasicDB implements ActDB {
	
	private Schema schema;
	private List<Act> data=new ArrayList<Act>();
	private String source="[unknown]";
	private String description="";	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public BasicDB(String source)
	{
		this(new Schema(), source);
	}
	
	public BasicDB(Schema schema, String source)
	{
		this.schema=schema;
		this.source=source;
	}
	
	@Override
	public Field addField(String name, Class type) {
		Field field=new Field(name, type);
		schema.add(field);
		return field;
	}
	
	public Field getField(String name)
	{
		return schema.getField(name);
	}

	@Override
	public ActList all() {
		return select(null);
	}

	@Override
	public Act createAct() {
		BasicAct act=new BasicAct(this);
		data.add(act);
		return act;
	}

	@Override
	public void delete(Act act) {
		data.remove(act);
	}

	@Override
	public void deleteField(Field field) {
		schema.delete(field);
	}

	@Override
	public List<Field> getFields(Class type) {
		return schema.getFields(type);
	}

	@Override
	public ActList select(ActFilter filter) {
		ActList set=new ActList(this);
		for (Act a: data)
			if (filter==null || filter.accept(a))
				set.add(a);
		return set;
	}

	@Override
	public List<Field> getFields() {
		return schema.getFields();
	}

	@Override
	public Act get(int i) {
		return data.get(i);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Iterator<Act> iterator() {
		return data.iterator();
	}

	@Override
	public void setAlias(Field field, String name) {
		schema.addAlias(field,name);
	}

	@Override
	public List<String> getFieldKeys() {
		return schema.getKeys();
	}

	@Override
	public void setNewFieldOrder(List<Field> newOrder) {
		schema.setNewFieldOrder(newOrder);
	}

	@Override
	public void renameField(Field field, String name) {
		schema.renameField(field, name);
	}

}
