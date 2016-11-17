package messageObjects.turnMessages;

import java.io.Serializable;
import java.util.ArrayList;

import gameObjects.Card;
import gameObjects.LandTile;
import gameObjects.Player;
import messageObjects.InGameMessage;

public class CardsBoughtMessage extends InGameMessage implements Serializable{
	
	ArrayList<Card> purchase;
	ArrayList<LandTile> sold;
	Player currentPlayer;
	
	public CardsBoughtMessage(String gameName, Player currentPlayer, ArrayList<Card> purchase, ArrayList<LandTile> sold) {
		super(gameName);
		this.purchase=purchase;
		this.currentPlayer=currentPlayer;
		this.sold=sold;
		
	}

	public ArrayList<Card> getPurchase() {
		return purchase;
	}

	public void setPurchase(ArrayList<Card> purchase) {
		this.purchase = purchase;
	}

	public ArrayList<LandTile> getSold() {
		return sold;
	}

	public void setSold(ArrayList<LandTile> sold) {
		this.sold = sold;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

}