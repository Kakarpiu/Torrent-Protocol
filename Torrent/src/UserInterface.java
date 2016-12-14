import java.io.*;
import java.net.*;


public class UserInterface extends Thread{

	private static UserInterface instance = null; 
	static BufferedReader console;
	private static Connection peer;
	protected static boolean portEstablished = false;
	private String argument;

	private UserInterface()
	{
		console = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static UserInterface getInstance()
	{
		if(instance == null)
		{
			instance = new UserInterface();
		}
		return instance;
	}
	
	public void run() {	
		while(true)
		{
			try
			{
				if((argument = console.readLine()) != null)
				{
					commandCenter(argument);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void commandCenter(String command)
	{
		System.out.println("commandCenter");
		String[] arguments = command.split("\\s");
		int argsCount = arguments.length;
		String commandName = arguments[0];
		
		switch(commandName)
		{	
			case "con" : CONN(arguments, argsCount); break;
		}
	}
	
	public void CONN(String[] arguments, int argsCount)
	{
		System.out.println("in CONN");
		if(Connection.lock == true)
		{
			System.out.println("Connection already established to "+peer.getIp()+":"+peer.getPort()+". Disconnect this connection to establish new");
			return;
		}
		else
		{
			if (argsCount != 3)
			{
				System.out.println("Wrong number of arguments. This command takes 2 arguments in form of. \nIP PORT ");
				return;
			}
			
			String ip = arguments[1];
			int port;
			
			try
			{
				port = Integer.parseInt(arguments[2]);
				System.out.println("beefore get instance");
				peer = Connection.getInstance(ip, port);
				System.out.println("after get instance");
				peer.connect();
			}
			catch (NumberFormatException e ) { System.out.println("Port number is not Integer!"); return;}
		}
	}
	
}
