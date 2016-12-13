
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a = 0;
		UserInterface ui = UserInterface.getInstance();
		Runnable[] freds = {ui};
		
		for(Runnable r : freds)
		{
			r.run();
		}
	}

}
