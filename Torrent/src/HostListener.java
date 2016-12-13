import java.io.*;
import java.net.*;

public class HostListener implements Runnable{

	private int port;
	private ServerSocket serverSocket = null;
	private HostListener instance = null;
	
	private HostListener(int port)
	{
		this.port = port;
		try 
		{
			serverSocket = new ServerSocket(port);
			UserInterface.portEstablished = true;
			System.out.println("Host Listens on port number: "+port);
		} 
		catch (IOException e)
		{
			System.out.println("Can't create socket on a given port");
			UserInterface.portEstablished = false;
		}
	}
	
	public HostListener getInstance(int port)
	{
		if(instance == null)
			instance = new HostListener(port);
		
		return instance;
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			try 
			{
				serverSocket.accept();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

}
