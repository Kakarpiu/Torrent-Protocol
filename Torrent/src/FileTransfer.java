import java.io.*;
import java.util.*;
import java.net.*;

public class FileTransfer extends Thread{
	
	// Sockets 
	private Socket socket = null;
	private ServerSocket receiveSock = null;
	private int transferport = 0;
	
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
	
	public FileTransfer(Socket socket, File file, command cmd)
	{
		this.socket = socket;
		this.file = file;
		this.cmd = cmd;
	}
	
	public FileTransfer(int port, File file, command cmd)
	{
		transferport = port;
		this.file = file;
		this.cmd = cmd;
	}
	
	public void run()
	{
		if(cmd == command.PUSH)
			push();
		else if(cmd == command.RECEIVE)
		{
			try 
			{
				ServerSocket receiveSock = new ServerSocket(transferport) ;
				socket = receiveSock.accept();
				receiveSock.close();
				receive();
			}
			catch (IOException e) { System.out.println("Error while creating socket."); }
		}	
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
				while((count = fileIN.read(buffer)) > -1)
				{
					send.write(buffer, 0, count);
					n++;
				}
				System.out.println(file.getName()+" pushed");
				socket.close();
			}
			catch (IOException e) { System.out.println("Error while sending file. Sent "+n*bufferSizeKB+" KB"); reconnect(); }
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
				while((count = receive.read(buffer)) > -1)
				{
					fileOUT.write(buffer, 0, count);
					n++;
				}
				System.out.println(file.getName()+" received");
				socket.close();
			}
			catch (IOException e) { System.out.println("Error while receiving file. Received "+n*bufferSizeKB+" KB"); }
		}
		catch (IOException e) { System.out.println("Error while creating streams for files."); }
	}
	
	public void reconnect()
	{
		System.out.println("Trying to restart transmission");
	}
}
