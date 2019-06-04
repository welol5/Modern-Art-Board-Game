import core.GameDriver;
import io.IOType;

/**
 * 
 * @author William Elliman
 * 
 * This class is just here for the name. It just contains the method for starting the program.
 *
 */
public class Main {
	
	private static String[] names = {"Random1","Random2","AIPlayer"};

	public static void main(String[] args) {
		
		GameDriver driver = new GameDriver(IOType.COMMAND_LINE, true, names);
		Thread driverThread = new Thread(driver);
		driverThread.start();
	}

}
