import java.io.*;
import java.math.*;
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
			double size = fileList.get(i).length();
			if(size < 1024)
			{
				System.out.println(i+". "+fileList.get(i).getName()+" "+size+" B");
				continue;
			}
			size = size/1024;
			if(size < 1024 && size > 1)
			{
				System.out.println(i+". "+fileList.get(i).getName()+" "+
						(new BigDecimal(size).setScale(2, RoundingMode.HALF_UP))+" KB");
				continue;
			}
			size = size/1024;
			if(size > 1)
			{
				System.out.println(i+". "+fileList.get(i).getName()+" "+
						(new BigDecimal(size).setScale(2, RoundingMode.HALF_UP))+" MB");
				continue;
			}				
		}	
	}
	
	public String sendFiles()
	{
		String list = "File list from host: "+Connection.getIp()+"\n";
		for(int i = 0; i<fileList.size(); i++)
		{
			double size = fileList.get(i).length();
			if(size < 1024)
			{
				list += (i+". "+fileList.get(i).getName()+" "+size+" B\n");
				continue;
			}
			size = size/1024;
			if(size < 1024 && size > 1)
			{
				list += (i+". "+fileList.get(i).getName()+" "+
						(new BigDecimal(size).setScale(2, RoundingMode.HALF_UP))+" KB\n");
				continue;
			}
			size = size/1024;
			if(size > 1)
			{
				list += (i+". "+fileList.get(i).getName()+" "+
						(new BigDecimal(size).setScale(2, RoundingMode.HALF_UP))+" MB\n");
				continue;
			}		
		}
		return list;
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
