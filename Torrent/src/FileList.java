import java.io.*;
import java.security.*;
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
		{
			list += fileList.get(i).getName()+" "+getFileSize(fileList.get(i))+" "+checkSum(fileList.get(i))+"\n";
		}
		return list;
	}
	
	public static String getFileSize(File file)
	{
		double size = file.length();
		if(size < 1024)
			return size+" B";
		
		size = size/1024;
		if(size < 1024 && size > 1)
			return (new BigDecimal(size).setScale(2, RoundingMode.HALF_UP)).toString()+" KB";
		
		else
		{
			size = size/1024;
			return (new BigDecimal(size).setScale(2, RoundingMode.HALF_UP)).toString()+" MB";
		}
	}
	
	public static String checkSum(File file) 
	{
		String checksum = "";
		
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] dataBytes= new byte[1024];
			
			try
			{
				FileInputStream fis = new FileInputStream(file);
				 
				int nread = 0;
		        try 
		        {
					while ((nread = fis.read(dataBytes)) != -1) 
					  md.update(dataBytes, 0, nread);
				} 
		        catch (IOException e) { System.out.println("Error while generating checksum."); return "Checksum err.";}
		        
		        byte[] mdbytes = md.digest();

		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < mdbytes.length; i++) {
		          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		        }

		        checksum = sb.toString();
			}
			catch (FileNotFoundException e1) { System.out.println("No such file"); return "Checksum err.";}
		} 
		catch (NoSuchAlgorithmException e1) { e1.printStackTrace(); return "Checksum err.";}
		
		return checksum;		
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
