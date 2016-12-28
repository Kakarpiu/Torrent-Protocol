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
	enum command{PUSH, PULL, RECEIVE};
	
	private byte[] buffer = new byte[8*1024];
	
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
		else if(cmd == command.PULL)
			pull();
		else if(cmd == command.RECEIVE)
		{
			try {
				ServerSocket receiveSock = new ServerSocket(transferport) ;
				socket = receiveSock.accept();
				receive();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
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
			while((count = fileIN.read(buffer)) > 0)
			{
				send.write(buffer, 0, count);
				n++;
			}
			System.out.println(file.getName()+" pushed");
			
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
			System.out.println("Receiving file "+file.getName());
			int count;
			while((count = receive.read(buffer)) > 0)
			{
				fileOUT.write(buffer, 0, count);
				n++;
			}
			System.out.println(file.getName()+" received");
			
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
