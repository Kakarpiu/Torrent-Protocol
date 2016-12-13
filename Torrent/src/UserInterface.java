import java.io.*;
import java.net.*;


public class UserInterface implements Runnable{

	private static UserInterface instance = null; 
	private static BufferedReader console;
	private String argument;
	private Connection peer;
	private HostListener listen;
	protected static boolean portEstablished = false;

	private UserInterface()
	{
		console = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static UserInterface getInstance()
	{
		if(instance == null)
		{
			instance = new UserInterface();
			while(!portEstablished)
			{
				System.out.println("What port do you want to use for listening? Choose between 10 000 and 60 000");
				try 
				{
					int port = Integer.parseInt(console.readLine());
					if(port >= 10000 && port <=60000 )
					{
						portEstablished = true;
					}
					else
						System.out.println("Choose port number between 10 000 and 60 000");
				} 
				catch (NumberFormatException e) { System.out.println("Number Format Exception."); }
				catch (IOException e) { System.out.println("I/O Exception"); }
			}
		}
		return instance;
	}
	
	public void run() {	
		while(true)
		{
			try {
				if((argument = console.readLine()) != null)
				{
					commandCenter(argument);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void commandCenter(String command)
	{
		String[] arguments = command.split("\\s");
		int argsCount = arguments.length;
		String commandName = arguments[0];
		
		switch(commandName)
		{	
			case "CONNECT" :  CONN(arguments, argsCount); break;
		}
	}
	
	public String CONN(String[] arguments, int argsCount)
	{
		if (argsCount != 4)
		{
			return "Wrong number of arguments. This command takes 3 arguments in form of. \nIP PORT PROTOCOL ";
		}
		String ip = arguments[1];;
		int port;
		String protocol;
		
		try
		{
			port = Integer.parseInt(arguments[2]);
		} catch (Exception e) { return "Port number is not Integer!"; };
		try
		{
			protocol = arguments[3];
		} catch (Exception e) { return "Protocol doesn't match. Please choose TCP or UDP. TCP is default";}
		
		peer = Connection.getInstance(ip, port, protocol);
		peer.connect();
		
		return "Connected.";
	}
	
}
