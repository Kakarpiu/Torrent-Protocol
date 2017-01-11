import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	static String LOGPATH = "C:/Torrent/InstanceLog.txt";
	static String DIRPATH = "C:/Torrent/TORrent_";
	static int PORT = 0;
	Scanner console = new Scanner(System.in);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int instance = getInstanceNumber();
		String ip = "127.0.0.1"; //args[0];
		int port;
		
		try
		{
			port = Integer.parseInt("10000"); //args[1]
			try 
			{
				Socket sock = new Socket(ip, port);
				ClientConnection connection = new ClientConnection(sock);
				connection.start();
			}
			catch (UnknownHostException e) { System.out.println("No server with these address"); destroyInstance();}
			catch (IOException e1) { System.out.println("Could not connect"); destroyInstance(); }
		} 
		catch(NumberFormatException e) { System.out.println("Port needs to be an integer number."); destroyInstance();}
	}
	
	private static int getInstanceNumber()
	{

		int number = 0;
		try 
		{
			File instanceLog = new File(LOGPATH);
			if(!instanceLog.exists())
			{
				instanceLog.createNewFile();
				instanceLog.deleteOnExit();
			}
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(instanceLog));
				String tmp;
				
				while((tmp = reader.readLine()) != null)
					number = Integer.parseInt(tmp);
				try
				{
					number++;
					BufferedWriter writer = new BufferedWriter(new FileWriter(instanceLog));
					writer.write(Integer.toString(number));
					try
					{
						DIRPATH = DIRPATH+number;
						File directory = new File(DIRPATH);
						directory.mkdir();
						
						reader.close();
						writer.close();
					}
					catch (IOException e) { System.out.println("Error while closing streams.");}
				}
				catch (IOException e) { System.out.println("Error while overwriting instanceLog file"); System.exit(0);}
			}
			catch (IOException e) { System.out.println("Error whiloe reading from instanceLog file."); System.exit(0);}
		} 
		catch (IOException e) { System.out.println("Error while creating instanceLog file"); System.exit(0);}
		return number;
	}
	
	public static void destroyInstance()
	{
		File instanceLog = new File(LOGPATH);
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(instanceLog));
			try
			{
				int number = Integer.parseInt(reader.readLine());
				
				File directory = new File(DIRPATH);
				directory.delete();
				number--;
				try
				{
					BufferedWriter writer = new BufferedWriter(new FileWriter(instanceLog));
					writer.write(Integer.toString(number));
					System.exit(0);
				}
				catch (IOException e) { System.out.println("Couldn't overwrite file"); System.exit(0); }
			}
			catch (NumberFormatException | IOException e)  { System.out.println("InstanceLog file has been manipulated. Cannot correctly delet directory and update instance number."); System.exit(0); } 
		} 
		catch (FileNotFoundException e) { System.exit(0); } 
	}

}
