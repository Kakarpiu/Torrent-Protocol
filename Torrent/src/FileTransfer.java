import java.io.*;
import java.util.*;
import java.net.*;

public class FileTransfer extends Thread{
	
	private Socket socket = null;
	private PrintWriter socketOUT = null;
	private OutputStream send = null;
	private FileInputStream fileIn = null;
	private File file = null;
	private int n = 1;
	
	private int bufferSize = 8*1024;
	private byte[] buff = new byte[bufferSize];
	
	public FileTransfer(String ip, int port, File file)
	{
		try 
		{
			socket = new Socket(ip, port);
			send = socket.getOutputStream();
			socketOUT = new PrintWriter(socket.getOutputStream(), true);
			fileIn = new FileInputStream(file);
			this.file = file;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void send()
	{
		try 
		{
			while((fileIn.read(buff, bufferSize*n, bufferSize)) > -1)
			{
				send.write(buff);
				n++;
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Disconnected");
		}
	 }
	
	
}
