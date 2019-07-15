import fxmlgui.GameDriver;
import player.Player;

public class GameRunner extends Thread{

	private Player winner;
	private boolean done = false;
	private final int timeout;
	private GameDriver driver;
	
	private boolean stop = false;

	public GameRunner(int timeout) {
		super();
		this.timeout = timeout;
	}

	@Override
	public void run() {

		while(!stop) {
			done = false;
			
			//get a game
			driver = TournamentGame.getGameDriver();
//			System.out.println(driver);
			if(driver == null) {
				continue;
			}

			while(!done) {
				Thread runner = new Thread(new Runnable() {

					@Override
					public void run() {
						winner = driver.playGame();
//						System.out.println("done");
						done = true;
					}
				});
				
				runner.start();

				try {
					runner.join(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(!done) {
					driver.resetGame();
				}
			}

			TournamentGame.addWin(winner);
		}
	}
	
	public void stopRunner() {
		stop = true;
	}
}
