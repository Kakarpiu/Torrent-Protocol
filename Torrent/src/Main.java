import java.io.*;

public class Main {

	static String LOGPATH = "C:/Torrent/InstanceLog.txt";
	static String DIRPATH = "C:/Torrent/TORrent_";
	static int PORT = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int instance = getInstanceNumber();
		
		UserInterface ui = UserInterface.getInstance();
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
