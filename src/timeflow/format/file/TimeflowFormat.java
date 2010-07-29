package timeflow.format.file;

import timeflow.model.*;
import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.format.field.*;

import timeflow.util.*;

import java.io.*;
import java.net.URL;
import java.util.*;

public class TimeflowFormat implements Import, Export
{
	private static final String END_OF_SCHEMA="#TIMEFLOW\tend-metadata";
	private static final String END_OF_METADATA="#TIMEFLOW\t====== End of Header. Data below is in tab-delimited format. =====";
	public ActDB readFile(String fileName, PrintStream messages) throws Exception
	{
		return read(new File(fileName), messages);
	}
	
	public static ActDB read(File file, PrintStream out) throws Exception
	{
		String text=IO.read(file.getAbsolutePath());		
		DelimitedText quote=new DelimitedText('\t');
		Iterator<String[]> lines=quote.read(text).iterator();
		

		ActDB db=null;
		List<String> fieldNames=new ArrayList<String>();
		List<Class> fieldTypes=new ArrayList<Class>();
		List<Integer> fieldSizes=new ArrayList<Integer>();
		String source="[unknown]", description="";
		for (;;)
		{
			String[] t=lines.next();

			if (t[1].equals("field"))
			{
				fieldNames.add(t[2]);
				fieldTypes.add(FieldFormatCatalog.javaClass(t[3]));
				if (t.length>4)
				{
					fieldSizes.add(Integer.parseInt(t[4]));
				}
				else
					fieldSizes.add(-1);
			}
			else if (t[1].equals("source"))
			{
				source=t[2];
			}
			else if (t[1].equals("description"))
			{
				description=t[2];
				
			}
			else if (t[1].equals("end-metadata"))
				break;
		}
		db=new ArrayDB((String[])fieldNames.toArray(new String[0]), 
				         (Class[])fieldTypes.toArray(new Class[0]), source);
		db.setDescription(description);
		for (int i=0; i<fieldNames.size(); i++)
			if (fieldSizes.get(i)>0)
				db.getField(fieldNames.get(i)).setRecommendedSize(fieldSizes.get(i));
		for (;;)
		{
			String[] t=lines.next();
			if (t[1].startsWith("==="))
				break;		
			if (t[1].equals("alias"))
				db.setAlias(db.getField(t[3]), t[2]);
		}
		
		// note: in some cases headers may be in a different order than in
		// metadata section, so we will read these.
		String[] headers=lines.next();
		if (headers.length!=fieldNames.size())
			throw new IllegalArgumentException("Different number of headers than fields!");
		
		
		while (lines.hasNext())
		{
			String[] t=lines.next();
			Act a=db.createAct();
			for (int i=0; i<t.length; i++)
			{
				Field f=db.getField(headers[i]);
				FieldFormat format=FieldFormatCatalog.getFormat(f.getType());
				a.set(f, format.parse(t[i]));
			}
		}
		
		return db;
	}
	
	public static void write(ActList acts, BufferedWriter bw) throws IOException
	{
		ActDB db=acts.getDB();
		
		PrintWriter out=new PrintWriter(bw);
		
		DelimitedText tab=new DelimitedText('\t');
		
		// Write version
		out.println("#TIMEFLOW\tformat version\t1");
		
		// Write source of data.
		out.println("#TIMEFLOW\tsource\t"+tab.write(db.getSource()));
		
		// Write description of data.
		out.println("#TIMEFLOW\tdescription\t"+tab.write(db.getDescription()));
		
		// Write schema.
		List<Field> fields=db.getFields();
		for (Field f: fields)
		{
			String recSize=f.getRecommendedSize()<=0 ? "" : "\t"+f.getRecommendedSize();
			out.println("#TIMEFLOW\tfield\t"+tab.write(f.getName())+
					    "\t"+FieldFormatCatalog.humanName(f.getType())+recSize);
		}
		
		out.println(END_OF_SCHEMA);
		
		// Write column mappings.
		List<String> aliases=DBUtils.getFieldAliases(db);
		for (String a:aliases)
			out.println("#TIMEFLOW\talias\t"+a+"\t"+tab.write(db.getField(a).getName()));
		
		// Write end of header indicator
		out.println(END_OF_METADATA);
		
		// Write data!
		new DelimitedFormat('\t').writeDelimited(db, acts, out);
		
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Reading");
		ActDB db=read(new File("test/monet.txt"), System.out);
		System.out.println("# lines: "+db.size());
	}

	@Override
	public String getName() {
		return "TimeFlow Format";
	}

	@Override
	public ActDB importFile(File file) throws Exception {
		return read(file, System.out);
	}

	@Override
	public void export(TFModel model, BufferedWriter out) throws Exception {
		write(model.getDB().all(), out);
	}
}
