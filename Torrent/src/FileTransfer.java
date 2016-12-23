import java.io.*;
import java.util.*;
import java.net.*;

public class FileTransfer extends Thread{
	
	private Socket socket = null;
	
	private PrintWriter socketOUT = null;
	private BufferedReader socketIN = null;
	
	private OutputStream send = null;
	private InputStream receive = null;
	
	private FileInputStream fileIN = null;
	private FileOutputStream fileOUT = null;
	private File file = null;
	
	private int n = 0;
	private command cmd;
	enum command{PUSH, PULL, RECEIVE};
	
	private byte[] buffer = new byte[8*1024];
	
	public FileTransfer(Socket socket, File file, command cmd)
	{
		this.socket = socket;
		this.file = file;
		this.cmd = cmd;
	}
	
	public void run()
	{
		if(cmd == command.PUSH)
			push();
		else if(cmd == command.PULL)
			pull();
		else if(cmd == command.RECEIVE)
			receive();
	}
	
	public void push()
	{
		try 
		{
			fileIN = new FileInputStream(file);
			send = socket.getOutputStream();
				
			int count;
			System.out.println("Before sending");
			while((count = fileIN.read(buffer)) > 0)
			{
				send.write(buffer, 0, count);
				n++;
			}
			System.out.println("File pushed");
			
			socket.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	 }
	
	public void receive()
	{
		try 
		{
			receive = socket.getInputStream();
			fileOUT = new FileOutputStream(file);
			System.out.println("Before receiving");
			int count;
			while((count = receive.read(buffer)) > 0)
			{
				fileOUT.write(buffer, 0, count);
				n++;
			}
			System.out.println("File received");
			
			receive.close();
			fileOUT.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void pull()
	{
		
	}
	
	
}
