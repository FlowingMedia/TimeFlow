package timeflow.util;

import java.io.*;
import java.util.*;

public class IO {
	
	public static ArrayList<String> lines(String fileName) throws IOException
	{
		ArrayList<String> a=new ArrayList<String>();
		String line=null;
		FileReader fr=new FileReader(fileName);
		BufferedReader in=new BufferedReader(fr);
		while (null != (line=in.readLine()))
			a.add(line);
		in.close();
		fr.close();
		return a;
	}
	
	public static String[] lineArray(String fileName) throws IOException
	{
		ArrayList<String> a=lines(fileName);
		return (String[])a.toArray(new String[0]);
	}
	
	public static String read(File file) throws IOException
	{
		char[] buffer = new char[1024];
		int n = 0;
		StringBuilder builder = new StringBuilder();
		FileReader reader = new FileReader(file);
		BufferedReader b = new BufferedReader(reader);
		while ((n = b.read(buffer, 0, buffer.length)) != -1) 
			builder.append(buffer, 0, n);
		b.close();
		reader.close();
		return builder.toString();
	}
	
	public static String read(String fileName) throws IOException
	{
		char[] buffer = new char[1024];
		int n = 0;
		StringBuilder builder = new StringBuilder();
		FileReader reader = new FileReader(fileName);
		BufferedReader b = new BufferedReader(reader);
		while ((n = b.read(buffer, 0, buffer.length)) != -1) 
			builder.append(buffer, 0, n);
		b.close();
		reader.close();
		return builder.toString();
	}
}
