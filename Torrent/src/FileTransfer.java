import java.io.*;
import java.util.*;
import java.net.*;

public class FileTransfer {
	
	private Socket socket = null;
	private OutputStream fileOut = null;
	private InputStream fileIn = null;
	
	public FileTransfer(String ip, int port)
	{
		try 
		{
			socket = new Socket(ip, port);
			fileOut = socket.getOutputStream();
			fileIn = socket.getInputStream();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
}
