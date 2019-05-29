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
		
		GameDriver driver;
		if(args.length > 0 && args[0].equalsIgnoreCase("-ai")) {
			driver = new GameDriver(IOType.COMMAND_LINE, true, names);
		} else {
			driver = new GameDriver(IOType.COMMAND_LINE, false, names);
		}
		
		Thread driverThread = new Thread(driver);
		driverThread.start();
	}

}
