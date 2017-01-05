import java.io.*;

public class Main {

	static String LOGPATH = "C:/Torrent/InstanceLog.txt";
	static String DIRPATH = "C:/Torrent/TORrent_";
	static int PORT = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int instance = getInstanceNumber();
		UserInterface ui = UserInterface.getInstance();
		
		System.out.println("What port do you want to listen for connections on? Choose between 10000 and 20000");
		while(true)
		{
			try 
			{
				int port = Integer.parseInt(ui.console.readLine());
				if((port >= 10000) && (port <= 20000))
				{
					Main.PORT = port;
					break;
				}
				else
				{
					System.out.println("Choose between 10000 and 20000.");
				}
			}
			catch (NumberFormatException e) { System.out.println("Input needs to be an Integer number beetwen 10000 and 20000"); }
			catch (IOException e) { System.out.println("Error while reading from console. Restart program"); System.exit(0); }
		}

		HostListener hl = HostListener.getInstance(Main.PORT);
	
		ui.start();
		hl.start();
		
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
					reader.close();
					writer.close();
					System.exit(0);
				}
				catch (IOException e) { System.out.println("Couldn't overwrite file"); System.exit(0); }
			}
			catch (NumberFormatException | IOException e)  { System.out.println("InstanceLog file has been manipulated. Cannot correctly delet directory and update instance number."); System.exit(0); } 
		} 
		catch (FileNotFoundException e) { System.exit(0); } 
	}

}
