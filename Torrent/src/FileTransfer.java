import java.io.*;
import java.util.*;
import java.net.*;

public class FileTransfer extends Thread{
	
	private Connection mymaster = null;
	
	// Sockets 
	private Socket socket = null;
	private InetAddress ip = null;
	private int transferport = 60001;
	
	// Transfer streams
	private OutputStream send = null;
	private InputStream receive = null;
	
	// File streams
	private FileInputStream fileIN = null;
	private FileOutputStream fileOUT = null;
	private File file = null;

	private int n = 0;
	private command cmd;
	enum command{PUSH, RECEIVE};
	
	private int bufferSizeKB = 8;
	private byte[] buffer = new byte[bufferSizeKB*1024];
	
	public FileTransfer(Socket socket, File file, command cmd, Connection c)
	{
		this.socket = socket;
		ip = socket.getInetAddress();
		this.file = file;
		this.cmd = cmd;
		mymaster = c;
	}
	
	public FileTransfer(File file, command cmd, Connection c)
	{
		this.file = file;
		this.cmd = cmd;
		mymaster = c;
	}
	
	public void run()
	{
		if(cmd == command.PUSH)
			push();
		else if(cmd == command.RECEIVE)
		{
			
				getSocket();
				receive();
		}	
	}
	
	public void getSocket()
	{
		try 
		{
			ServerSocket receiveSock = new ServerSocket(transferport);
			socket = receiveSock.accept();
			ip = socket.getInetAddress();
			receiveSock.close();
		}
		catch (IOException e) { System.out.println("Error while creating socket."); }
	}
	
	public void push()
	{
		try 
		{
			fileIN = new FileInputStream(file);
			send = socket.getOutputStream();
			
			int count;
			System.out.println("Pushing file: "+file.getName());
			try
			{
				while((count = fileIN.read(buffer)) > 0)
				{
					send.write(buffer, 0, count);
					n++;
				}
				System.out.println(file.getName()+" pushed");
				socket.close();
			}
			catch (IOException | NullPointerException e) { System.out.println("Error while sending file. Sent "+n*bufferSizeKB+" KB "+e.toString()); reconnect("push"); }
		}
		catch (IOException e) { System.out.println("Error while creating streams for files."); }
	 }
	
	public void receive()
	{
		try 
		{
			receive = socket.getInputStream();
			fileOUT = new FileOutputStream(file);
			
			int count;
			System.out.println("Receiving file "+file.getName());
			try
			{
				while((count = fileIN.read(buffer)) > 0)
				{
					fileOUT.write(buffer, 0, count);
					n++;
				}
				System.out.println(file.getName()+" received");
				socket.close();
			}
			catch (IOException | NullPointerException e) { System.out.println("Error while receiving file. Received "+n*bufferSizeKB+" KB "+e.toString()); reconnect("recv"); }
		}
		catch (IOException e) { System.out.println("Error while creating streams for files."); }
	}
	
	public void reconnect(String method)
	{
		System.out.println("Trying to restart transfer");
//		if(!mymaster.isConnected())
//			mymaster.reconnect();
//		
		if(method.equals("push"))
		{
			try 
			{
				socket.close();
				do 
				{
					try
					{
						socket = new Socket(ip, transferport);
						push();
					}
					catch (IOException e) { e.toString(); }
				} while(!socket.isConnected());
			} 
			catch (IOException e) { }
		}
		
		else if(method.equals("recv"))
		{
			try 
			{
				socket.close();
				getSocket();
				receive();
			} 
			catch (IOException e) { System.out.println("Error while closing socket."); }
		}
	}
}
