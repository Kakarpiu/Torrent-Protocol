import java.util.*;
import java.io.*;
import java.net.*;


public class ServerConnection extends Thread{
	
	private int idnumber;
	
	private Socket connectionSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	// FILETRANSFER LIST
	private String list;
	
	public ServerConnection(Socket s, PrintWriter output, BufferedReader input, int id)
	{
		connectionSocket = s;
		idnumber = id;	
		try
		{
			out = output;
			in = input;
			
			String r = "";
			StringBuffer sb = new StringBuffer();
			
			while(!(r = in.readLine()).equals("END"))
				sb.append(r);
			
			list = sb.toString();
			System.out.println("Connection with IP: "+s.getInetAddress().toString()+" established. ID number: "+idnumber);
			this.start();
		}
		catch (IOException e) { System.out.println("Couldn't create streams."); }
	}
	
	public void run()
	{
		while(true)
		{
			try 
			{
				String command = in.readLine();
				System.out.println(command);
				receive(command);
			} 
			catch (IOException | NullPointerException e) 
			{
				System.out.println("Host "+idnumber+" disconnected"); 
				ServerHostListener.peers.remove(this); 
				this.interrupt(); 
			}			
		}
	}	
	
	public void receive(String command)
	{
		switch (command)
		{
			case "Push" :
			{
				try 
				{
					String filename = in.readLine();
					String filesize = in.readLine();
					System.out.println("Peer with ID: "+idnumber+" is pushing "+filename+" "+filesize);
					
					FileTransfer ft = new FileTransfer(new File(Main.DIRPATH+"/"+filename), FileTransfer.command.RECEIVE);
					ft.start();
				}
				catch (IOException e) { System.out.println("Stream exception."); }
				break;
			}
			
			case "Pull" :
			{
				try
				{
					String filename = in.readLine();
					File file = UserInterface.fileList.getFile(filename);
					if(file != null)
					{
					int transferport = 60001;
							
					FileTransfer ft = new FileTransfer(new Socket(connectionSocket.getInetAddress(), transferport), file, FileTransfer.command.PUSH);
					ft.start();
					}
					else
						out.println("NAK");
				}
				catch (IOException e) { System.out.println(); }
				break;
			}
			
			case "GetList" :
			{
				out.println(ServerHostListener.sendList());
				out.println("END");
				break;
			}
			
			case "SendingList" :
			{
				String s;
				try 
				{
					while(!(s = in.readLine()).equals("END"))
						System.out.println(s);
				} catch (IOException e) { System.out.println("Error while sending list"); }
				break;
			}
			
			case "Disconnect" :
			{
				try 
				{
					connectionSocket.close();
					System.out.println("Peer disconnected.");
				} catch (IOException e) { System.out.println("Error while disconnecting."); }
				break;
			}
		}
	}
	public int getID()
	{
		return idnumber;
	}
	
	public boolean isConnected()
	{
		return connectionSocket.isConnected();
	}
	
	public void disconnect()
	{
		out.println("Disconnect");
	}
	
	public void sendFileList()
	{
		out.println(list);
		out.println("END");
	}

	public String getList()
	{
		return list;
	}
//	public void getFileList()
//	{
//		try 
//		{
//			out.println("GetList");
//			String response = in.readLine();
//			
//			if(response != null)
//				System.out.println(response);
//			else
//				System.out.println("List is empty.");
//
//		} 
//		catch (IOException e) { System.out.println("Couldn't get list."); }
//	}
//	
//	public void push(File file)
//	{
//		try 
//		{
//			out.println("Push");
//			out.println(file.getName());
//			out.println(FileList.getFileSize(file));
//			
//			int transferport = 60001;
//				
//			FileTransfer ft = new FileTransfer(new Socket(ip, transferport), file, FileTransfer.command.PUSH);
//			ft.start();
//			
//		} 
//		catch (IOException e) { System.out.println("Stream error"); }
//	}
//	
//	public void pull(String file)
//	{
//		out.println("Pull");
//		out.println(file);
//		
//		FileTransfer ft = new FileTransfer(new File(Main.DIRPATH+"/"+file), FileTransfer.command.RECEIVE);
//		transfers.add(ft);
//		ft.start();
//	}
	
}









