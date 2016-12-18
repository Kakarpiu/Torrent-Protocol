import java.io.*;

public class Main {

	static String LOGPATH = "C:/Torrent/InstanceLog.txt";
	static String DIRPATH = "C:/Torrent/TORrent_";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int instance = getInstanceNumber();;
		int port;
		
		UserInterface ui = UserInterface.getInstance();
		HostListener hl = null;
	
		
		
		while(!UserInterface.portEstablished)
		{
			System.out.println("What port do you want to use for listening? Choose between 10 000 and 60 000");
			try 
			{
				port = Integer.parseInt(ui.console.readLine());
				if(port >= 10000 && port <=60000 )
				{
					hl = HostListener.getInstance(port);
					UserInterface.portEstablished = true;
				}
				else
					System.out.println("Choose port number between 10 000 and 60 000");
			} 
			catch (NumberFormatException e) { System.out.println("Number Format Exception."); }
			catch (IOException e) { System.out.println("I/O Exception"); }
		}
	
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
			BufferedReader reader = new BufferedReader(new FileReader(instanceLog));
			String tmp;
			
			while((tmp = reader.readLine()) != null)
				number = Integer.parseInt(tmp);
			
			number++;
			BufferedWriter writer = new BufferedWriter(new FileWriter(instanceLog));
			writer.write(Integer.toString(number));
	
			DIRPATH = DIRPATH+number;
			File directory = new File(DIRPATH);
			directory.mkdir();
			
			reader.close();
			writer.close();
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return number;
	}

}
