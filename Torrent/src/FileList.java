import java.io.*;
import java.util.*;

public class FileList {
	
	private static FileList instance = null; 
	private ArrayList<File> fileList = new ArrayList<File>();
	
	private FileList(String dirpath)
	{
		System.out.println(dirpath);
		File temp = new File(dirpath);
		for(File f : temp.listFiles())
		{
			fileList.add(f);
		}
	}
	
	
	
	public static FileList getInstance(String dirpath)
	{
		if(instance == null)
			instance = new FileList(dirpath);
		
		return instance;
	}
	
	public void showFiles()
	{
		for(int i = 0; i<fileList.size(); i++)
		{
			System.out.println(i+". "+fileList.get(i).getName()+" "+fileList.get(i).length()/1024/1024+" MB");
		}
	}
	
	public File getFile(int index)
	{
		return fileList.get(index);
	}
	
	public File getFile(String name)
	{
		File file = null;
		for(File f : fileList)
		{
			if(f.getName().equals(name))
				file = f;
			else
				System.out.println("No file wtih such");
		}
		return file;
	}
}
