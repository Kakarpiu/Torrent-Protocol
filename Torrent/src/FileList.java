import java.io.*;
import java.math.*;
import java.util.*;

public class FileList {
	
	private String dirpath;
	private static FileList instance = null; 
	private ArrayList<File> fileList = new ArrayList<File>();
	
	private FileList(String dirpath)
	{
		this.dirpath = dirpath;
		File temp = new File(dirpath);
		for(File f : temp.listFiles())
		{
			fileList.add(f);
		}
	}
	
	private void refreshList()
	{
		fileList.clear();
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
		
	public String showFiles()
	{
		refreshList();
		String list = "";
		
		for(int i = 0; i<fileList.size(); i++)
			list += fileList.get(i).getName()+" "+getFileSize(fileList.get(i));
		
		return list;
	}
	
	public static String getFileSize(File file)
	{
		double size = file.length();
		if(size < 1024)
			return size+" B\n";
		
		size = size/1024;
		if(size < 1024 && size > 1)
			return new BigDecimal(size).setScale(2, RoundingMode.HALF_UP)+" KB\n";
		
		else
		{
			size = size/1024;
			return new BigDecimal(size).setScale(2, RoundingMode.HALF_UP)+" MB\n";
		}
	}
	
	public File getFile(String filename)
	{
		refreshList();
		for(File f : fileList)
		{
			if(f.getName().equals(filename))
				return f;
		}
		return null;
	}
}
