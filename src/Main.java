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

	public static void main(String[] args) {
		GameDriver driver = new GameDriver(IOType.COMMAND_LINE);
		Thread driverThread = new Thread(driver);
		driverThread.start();
	}

}
