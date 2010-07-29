package timeflow.model;

public class TFEvent {
	public enum Type {DATABASE_CHANGE, ACT_ADD, ACT_DELETE, ACT_CHANGE, ERROR, SOURCE_CHANGE, DESCRIPTION_CHANGE,
					 FIELD_ADD, FIELD_DELETE, FIELD_CHANGE, SELECTION_CHANGE, FILTER_CHANGE, VIEW_CHANGE};
	public Type type;
	public String message="[]";
	public Object info;
	public Object origin;
	
	public TFEvent(Type type, Object origin)
	{
		this.type=type;
		this.origin=origin;
	}

	public String toString()
	{
		return "[TimelineEvent: type="+type+", info="+info+", message="+message+", origin="+origin+"]";
	}
	
	public boolean affectsSchema()
	{
		switch (type){
			case DATABASE_CHANGE: 
			case FIELD_ADD:
			case FIELD_DELETE:
			case FIELD_CHANGE: return true;
		}
		return false;
	}
	
	public boolean affectsRowSet()
	{
		return affectsSchema() || type==Type.ACT_CHANGE || type== Type.ACT_ADD || type== Type.ACT_DELETE
		|| type==Type.FILTER_CHANGE;
	}
	
	public boolean affectsData()
	{
		return type!=Type.SELECTION_CHANGE && type!=Type.VIEW_CHANGE && type!=Type.ERROR;
	}
}
