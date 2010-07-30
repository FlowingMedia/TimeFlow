package timeflow.app;

import timeflow.util.*;

import java.io.*;
import java.util.*;

public class AppState {
	private static final String FILE="settings/info.txt";
	private File currentFile, currentDir;
	private LinkedList<File> recentFiles=new LinkedList<File>();
	
	public AppState()
	{
		if (!new File(FILE).exists())
		{
			System.err.println("No existing settings file found.");
			return;
		}
		try
		{
			for (String line: IO.lines(FILE))
			{
				String[] t=line.split("\t");
				String command=t[0];
				String arg=t[1];
				if ("CURRENT_FILE".equals(command))
					currentFile=new File(arg);
				else if ("RECENT_FILE".equals(command))
					recentFiles.add(new File(arg).getAbsoluteFile());
				else if ("CURRENT_DIR".equals(command))
					currentDir=new File(arg);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	public List<File> getRecentFiles()
	{
		return (List<File>)recentFiles.clone();
	}
	
	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile.getAbsoluteFile();
		
		// if list is big, remove one at end.
		if (recentFiles.size()>10)
			recentFiles.removeLast();
		
		// put at front of list
		if (recentFiles.contains(this.currentFile))
			recentFiles.remove(this.currentFile);
		recentFiles.addFirst(this.currentFile);
		
		// set current dir, too.
		this.currentDir=currentDir;
	}

	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File currentDir) {
		this.currentDir = currentDir;
	}

	public void save()
	{
		try
		{
			FileOutputStream fos=new FileOutputStream(FILE);
			PrintStream out=new PrintStream(fos);
			out.println("CURRENT_FILE\t"+currentFile);
			out.println("CURRENT_DIR\t"+currentDir);
			for (File f: recentFiles)
				out.println("RECENT_FILE\t"+f);
			out.flush();
			out.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
