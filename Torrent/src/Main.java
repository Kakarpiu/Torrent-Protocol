
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		UserInterface ui = UserInterface.getInstance();
		HostListener hl = HostListener.getInstance();
		Runnable[] freds = {ui, hl};
		
		for(Runnable r : freds)
		{
			r.run();
		}
	}

}
