
public class Connection {
	
	
	private static String ip;
	private static int port;
	private static String protocol;
	private static Connection instance = null;
	// FILETRANSFER LIST
	
	
	protected Connection(String ip, int port, String protocol)
	{
		this.ip = ip;
		this.port = port;
		this.protocol = protocol;
	}
	
	public Connection getInstance(String ip, int port, String protocol)
	{
		if(instance == null)
		{
			this.ip = ip;
			this.port = port;
			this.protocol = protocol;
			instance = new Connection(ip, port, protocol);
		}
		return instance;
	}
	
	public String getIp()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getProtocol()
	{
		return protocol;
	}
	
	
}
