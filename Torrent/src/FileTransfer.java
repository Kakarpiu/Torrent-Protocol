import java.io.*;
import java.util.*;
import java.net.*;

public class FileTransfer extends Thread{
	
	private Socket socket = null;
	
	private PrintWriter socketOUT = null;
	private BufferedReader socketIN = null;
	private OutputStream send = null;
	
	private FileInputStream fileIn = null;
	private File file = null;
	
	private int n = 1;
	private command cmd;
	enum command{PUSH, PULL, RECEIVE};
	
	private int bufferSize = 8*1024;
	private byte[] buff = new byte[bufferSize];
	
	public FileTransfer(String ip, int port, File file, command cmd)
	{
		try 
		{
			socket.setSoTimeout(15000);
			socket.connect(new InetSocketAddress(ip, port));
			send = socket.getOutputStream();
			socketOUT = new PrintWriter(socket.getOutputStream(), true);
			socketIN = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			fileIn = new FileInputStream(file);
			this.file = file;
			this.cmd = cmd;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		if(cmd == command.PUSH)
			push();
		else if(cmd == command.PULL)
			pull();
	}
	
	public void push()
	{
		try 
		{
			socketOUT.println("Push");
			socketOUT.println(file.getName());
			socketOUT.println(file.length());
					
			if(socketIN.readLine().equals("ACK"))
			{
				while((fileIn.read(buff, bufferSize*n, bufferSize)) > -1)
				{
					send.write(buff);
					n++;
				}
			}
			else
			{
				System.out.println("Peer declined to push file");
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Disconnected");
		}
	 }
	
	public void pull()
	{
		
	}
	
	
}
