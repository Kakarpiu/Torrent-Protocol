import java.io.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int instance;
		int port;
		UserInterface ui = UserInterface.getInstance();
		HostListener hl = null;
		
		instance = getInstanceNumber();
		System.out.println(instance);
		
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
		File instanceLog = new File("C:/Users/Files/InstanceLog.txt");
		int number = 0;
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(instanceLog));
			String tmp;
			
			while((tmp = reader.readLine()) != null)
				number = Integer.parseInt(tmp);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(instanceLog));
			writer.write(Integer.toString(number+1));
	
			reader.close();
			writer.close();
			
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return number;
	}

}
