package timeflow.data.db;


public class Field {
	private String name;
	private Class type;
	int index;
	private int recommendedSize=-1;
	
	public Field(String name, Class type)
	{
		this.name=name;
		this.type=type;
	}
	
	public Field(String name, Class type, int recommendedSize)
	{
		this.name=name;
		this.type=type;
		this.recommendedSize=recommendedSize;
	}
	
	public int getRecommendedSize() {
		return recommendedSize;
	}

	public void setRecommendedSize(int recommendedSize) {
		this.recommendedSize = recommendedSize;
	}
	
	void setName(String name)
	{
		this.name=name;
	}

	public String getName()
	{
		return name;
	}
	
	public Class getType()
	{
		return type;
	}

	public String toString()
	{
		return "[Field: name='"+name+"', type="+type+", index="+index+"]";
	}
}
