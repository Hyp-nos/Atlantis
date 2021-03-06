package gameObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
/**
* <h1>Deck of land tiles</h1>
* this class contains the landtiles to distrupte on clients
* 
* @author  Ali Habbabeh
* @version 1.2
* @since   2016-12-22
*/
public class DeckOfLandTiles implements Serializable {

	private ArrayList<LandTile> deckOfTiles = new ArrayList<>();
	
	int k = 0;
	// random object to remove one land tile from each color 
	Random r = new Random();

	public DeckOfLandTiles() {
		shuffleTiles();
	}

	private void shuffleTiles() {
		// first we remove any tiles in the deck
		deckOfTiles.clear();

		// Add all possible land tiles
			for (ColorChoice c : ColorChoice.values()) { // used nested loop to multiply # of colors with #of landtiles
				for (int i = 1; i < 8; i++) {										
				deckOfTiles.add(new LandTile(k, c, i));
				k++;
			
			}
		}
		// the rules says that there are a value removed from each color 
			// since the tiles are not shuffled yet we can just remove one card from each color
		
		for (int i=0; i<7;i++){
			int f=	r.nextInt(6);
			deckOfTiles.remove(i+6*f);	
		}
		
		// now since we added them on a row we shuffle them
		Collections.shuffle(deckOfTiles);
	}

	public int getNumberOfLandTiles() {
		return deckOfTiles.size(); 
	}

	public LandTile dealTile() {
		return (deckOfTiles.size() > 0) ? deckOfTiles.remove(deckOfTiles.size() - 1) : null; // first semester
	}
	public LandTile getTileDetails(int i){
		return deckOfTiles.get(i);
	}

	public ArrayList<LandTile> getDeckOfTiles() {
		return deckOfTiles;
	}



	public void setDeckOfTiles(ArrayList<LandTile> deckOfTiles) {
		this.deckOfTiles = deckOfTiles;
	}
}
