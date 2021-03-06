package messageObjects.turnMessages;

import java.io.Serializable;

import gameObjects.Player;
import messageObjects.InGameMessage;
/**
 * <h1>message</h1> 
 * this is a class that represent an object sent from server to client or vice versa
 * they are all self explanatory 
 * 
 * 
 * @author Ali Habbabeh
 * @version 1.2
 * @since 2016-12-22
 */
public class GameStatusMessage extends InGameMessage implements Serializable {

	private boolean startGame;
	private Player currentPlayer;
	private int playerIndex;

	
	public GameStatusMessage(String gameName) {
		super(gameName);
		
	}
	
	// start game from server to client
	public GameStatusMessage(String gameName,  boolean startGame, Player currentPlayer){
		super(gameName);
		this.startGame=startGame;
		this.currentPlayer=currentPlayer;
	}
	
	// from client to server
	public GameStatusMessage(String gameName, int playerIndex){
		super(gameName);
		this.playerIndex=playerIndex;
		
	}
	
	

	
	
	public boolean isStarted() {
		return startGame;
	}
	public void setStartGame(boolean startGame) {
		this.startGame = startGame;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public int getPlayerIndex() {
		return playerIndex;
	}

	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	
	
	
	

}
