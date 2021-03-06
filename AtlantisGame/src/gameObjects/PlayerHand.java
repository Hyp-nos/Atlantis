package gameObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.layout.HBox;

/**
* <h1>Player Hand</h1>
*  each player has a hand, obviously, the hand contains the cards and treasures that a 
* player collect on his way to redemption, sorry mainland 
* @author  Ali Habbabeh
* @version 1.2
* @since   2016-12-22
*/
public class PlayerHand implements Serializable {

	private String playerName;
	private ArrayList<Card> cards = new ArrayList<>();
	private ArrayList<LandTile> treasures = new ArrayList<>();

	public PlayerHand(String name) {
		playerName = name;
	}

	public ArrayList<LandTile> getTreasures() {
		return treasures;
	}

	public int getTreasuresValue(ArrayList<LandTile> list) {
		int result = 0;
		for (LandTile tile : list)
			result += tile.getLandValue();

		return result;
	}

	public void addTreasure(LandTile treasure) {
		treasures.add(treasure);
	}

	public String getPlayerName() {
		return playerName;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public void addCard(Card card) {
		cards.add(card);
	}

	public void clear() {
		cards.clear();

	}
	

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}

	public int getNumCards() {
		return cards.size();
	}

	public void removeCardFromHand(Card card) {
		cards.remove(card);

	}

}
