import java.io.*;
import java.net.*;


public class UserInterface implements Runnable{

	private static UserInterface instance = null; 
	private BufferedReader console;
	private String argument;
	private Connection peer;

	protected UserInterface()
	{
		console = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static UserInterface getInstance()
	{
		if(instance == null)
			instance = new UserInterface(); 
		return instance;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("What port number do you want to use?");
		
		while(true)
		{
			try {
				if((argument = console.readLine()) != null)
				{
					commandCenter(argument);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void commandCenter(String command)
	{
		String[] arguments = command.split("\\s");
		int argsCount = arguments.length;
		String commandName = arguments[0];
		System.out.println(arguments[0]);
		
		switch(commandName){
			
			case "CONN" :  
			{
				if (argsCount != 4)
				{
					System.out.println("Wrong number of arguments. This command takes 3 arguments in form of. \nIP PORT PROTOCOL ");
					return;
				}
				String ip = arguments[1];;
				int port;
				String protocol;
				
				try
				{
					port = Integer.parseInt(arguments[2]);
				} catch (Exception e) { System.out.println("Port number is not Integer!"); return; };
				try
				{
					protocol = arguments[3];
				} catch (Exception e) { System.out.println("Protocol doesn't match. Please choose TCP or UDP. TCP is default"); return;}
				
				
				peer = new Connection(ip, port, protocol);
				
				
			}
		}
	}
	
}
