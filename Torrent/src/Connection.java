import java.util.*;
import java.io.*;
import java.net.*;


public class Connection extends Thread{
	
	private InetAddress ip;
	private int connectionPort;
	private int idnumber;
	
	private Socket connectionSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	// FILETRANSFER LIST
	private ArrayList<FileTransfer> transfers = new ArrayList<FileTransfer>();
	
	public Connection(Socket s) // When connecting
	{
		connectionSocket = s;
		ip = connectionSocket.getInetAddress();
		connectionPort = connectionSocket.getPort();
		try
		{
			out = new PrintWriter(connectionSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			idnumber = Integer.parseInt(in.readLine());
			out.println("ACK");
			System.out.println("Connection with IP: "+ip.toString()+" established. ID number: "+idnumber);
			UserInterface.peers.add(this);
			this.start();
		}	
		catch (IOException e){ System.out.println("Could not create streams."); }
		catch (NumberFormatException e) { System.out.println("Could not establish connection"); }
	}
	
	public Connection(Socket s, int id) // When receiving
	{
		connectionSocket = s;
		idnumber = id;	
		try
		{
			ip = connectionSocket.getInetAddress();
			out = new PrintWriter(connectionSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			out.println(id);
			if(in.readLine().equals("ACK"))
			{
				System.out.println("Connection with IP: "+ip.toString()+" established. ID number: "+idnumber);
				UserInterface.peers.add(this);
				this.start();
			}
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
				receive(command);
			} catch (IOException | NullPointerException e) { System.out.println("Host "+idnumber+" disconnected"); break;}			
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
					
					FileTransfer ft = new FileTransfer(new File(Main.DIRPATH+"/"+filename), FileTransfer.command.RECEIVE, this);
					transfers.add(ft);
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
							
					FileTransfer ft = new FileTransfer(new Socket(connectionSocket.getInetAddress(), transferport), file, FileTransfer.command.PUSH, this);
					transfers.add(ft);
					ft.start();
					}
					else
					{
						out.println("Nofile");
					}
				}
				catch (IOException e) { System.out.println(); }
				break;
			}
			
			case "Nofile" :
			{
				System.out.println("No such file.");
			}
			
			case "GetList" :
			{
				String list = UserInterface.fileList.showFiles();
				out.println("SendList");
				out.println("Host with id: "+idnumber);
				out.println(list);
				out.println("END");
				break;
			}
			
			case "SendList" :
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
	
	public InetAddress getIp()
	{
		return ip;
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
	
	public void getFileList()
	{
		out.println("GetList");
	}
	
	
	public void push(File file)
	{
		try 
		{
			out.println("Push");
			out.println(file.getName());
			out.println(FileList.getFileSize(file));
			out.println(FileList.checkSum(file));
			
			int transferport = 60001;
				
			FileTransfer ft = new FileTransfer(new Socket(ip, transferport), file, FileTransfer.command.PUSH, this);
			transfers.add(ft);
			ft.start();
			
		} 
		catch (IOException e) { System.out.println("Stream error"); }
	}
	
	public void pull(String file)
	{
		out.println("Pull");
		out.println(file);
		
		FileTransfer ft = new FileTransfer(new File(Main.DIRPATH+"/"+file), FileTransfer.command.RECEIVE, this);
		transfers.add(ft);
		ft.start();
	}
	
//	public void reconnect()
//	{
//		connectionSocket = new Socket();
//			try 
//			{
//				connectionSocket.setSoTimeout(60000);
//				do
//				{
//					try 
//					{
//						connectionSocket.connect(new InetSocketAddress(ip, connectionPort));
//						
//						
//					} 
//					catch (IOException e) {  }
//				} while (connectionSocket.isConnected());
//			} 
//			catch (SocketException e) { System.out.println("Reconnect timeout"); }
//	}
}









