package messageObjects.turnMessages;

import java.io.Serializable;
import java.util.ArrayList;

import gameObjects.Card;
import gameObjects.LandTile;
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
public class PaymentDoneMessage extends InGameMessage implements Serializable {

	int playerIndex;
	ArrayList<Card> cardsChosen;
	ArrayList<LandTile> treasuresChosen;
	int vp;
	int cardCount;
	
	
	public PaymentDoneMessage(String name, int playerIndex, ArrayList<Card> cardsChosen,
			ArrayList<LandTile> treasuresChosen, int vp, int cardCount) {
		super(name);
		this.playerIndex=playerIndex;
		this.cardsChosen=cardsChosen;
		this.treasuresChosen=treasuresChosen;
		this.vp=vp;
		this.cardCount=cardCount;
	}


	public int getPlayerIndex() {
		return playerIndex;
	}

	public ArrayList<Card> getCardsChosen() {
		return cardsChosen;
	}


	public ArrayList<LandTile> getTreasuresChosen() {
		return treasuresChosen;
	}


	public int getVp() {
		return vp;
	}


	public int getCardCount() {
		return cardCount;
	}



}
