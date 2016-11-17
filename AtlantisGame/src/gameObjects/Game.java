package gameObjects;

// This class is part of the Gamer Server, should be moved from this package
import java.util.ArrayList;
import messageObjects.AtlantisMainLandMessage;
import messageObjects.InGameMessage;
import messageObjects.OpponentMessage;
import messageObjects.PlayerMessage;
import messageObjects.WaterMessage;
import messageObjects.turnMessages.BuyCardsMessage;
import messageObjects.turnMessages.CardsBoughtMessage;
import messageObjects.turnMessages.GameStatusMessage;
import messageObjects.turnMessages.PawnCardSelectedMessage;
import messageObjects.turnMessages.PaymentDoneMessage;
import messageObjects.turnMessages.PlayAnotherCardMessage;
import messageObjects.turnMessages.RefreshPlayerMessage;
import messageObjects.turnMessages.ServerMessage;
import messageObjects.turnMessages.TurnMessage;
import messageObjects.turnMessages.WaterPaidMessage;
import server.backend.Lobby;
import server.backend.LobbyInterface;
import server.backend.User;

public class Game implements GameInterface {

	private String name;
	private String password;
	// send Message trough sendMessage method of User, get player name stats etc
	// through userInfo field of User
	private ArrayList<User> users;
	private int maxPlayers;
	// Invoke lobby.addWin, lobby.addLoss & lobby.addTie methods on users at the
	// end of the game
	private LobbyInterface lobby;
	private DeckOfCards cards;
	private DeckOfLandTiles deckA;
	private DeckOfLandTiles deckB;
	private ArrayList<Player> players = new ArrayList<>();
	private AtlantisTile atlantis = new AtlantisTile();
	private MainLand mainland = new MainLand();
	// base for water
	private ArrayList<WaterTile> base;
	private int currentPlayerIndex;
	private Player currentPlayer;

	// Constructor (doesn't start game)
	public Game(String name, String password, int maxPlayers, User creator, Lobby lobby) {

		this.name = name;
		this.password = password;
		this.maxPlayers = maxPlayers;
		users = new ArrayList<User>();
		users.add(creator);
		this.lobby = lobby;
	}
 
	// Here the game starts
	public void start() {
		// Informs clients about game start
		for (User u : users) {
			u.initiateGameStart(this);
		}
		// send waterTiles to all the players
		int numberOfPlayers = getNumOfRegisteredPlayers();

		base = new ArrayList<>();
		deckA = new DeckOfLandTiles();
		deckB = new DeckOfLandTiles();
		cards = new DeckOfCards();
		// create water tiles which will hold land tiles later on
		for (int i = 1; i < 54; i++) {
			WaterTile water = new WaterTile(i);
			base.add(water);
		}
		distributeLandTiles();

		for (int i = 0; i < numberOfPlayers; i++) {
			users.get(i).sendMessage(new WaterMessage(getName(), base));

		}
		/*
		 * for (int i = 0; i < numberOfPlayers; i++) { users.get(i)
		 * .sendMessage(new DeckLandTileMessage(getName(),
		 * deckA.getDeckOfTiles(), deckB.getDeckOfTiles()));
		 * 
		 * }
		 */
		// send the atlantis and main land
		for (int i = 0; i < numberOfPlayers; i++) {
			users.get(i).sendMessage(new AtlantisMainLandMessage(getName(), atlantis, mainland));

		}
		// send Player for each user

		for (int i = 0; i < numberOfPlayers; i++) {
			Player player = new Player(users.get(i).getUserInfo().getUsername());
			setPlayerColorAndTurn(player, i);
			player.setPlayerIndex(i);
			player.getPlayerHand().setCards(cardsForPlayers(i, player));
			for (Pawn p : player.getPawns()) {
				p.setOwner(player);
				p.setPawnColor(player.getColor());
			}
			users.get(i).sendMessage(new PlayerMessage(getName(), player));
			players.add(player);
		}

		currentPlayerIndex = 0;
		currentPlayer = players.get(currentPlayerIndex);

		// send the list of players for client to set opponents
		for (int i = 0; i < numberOfPlayers; i++) {
			users.get(i).sendMessage(new OpponentMessage(getName(), players));
		}

		// check whose turn it is and inform client to start
		for (int i = 0; i < numberOfPlayers; i++) {
			users.get(i).sendMessage(new GameStatusMessage(getName(), true, currentPlayer));

		}

	}

	// Here messages from clients arrive
	public synchronized void processMessage(InGameMessage igm) {
		// message from client asking who is first player and shall we start ?
		if (igm instanceof GameStatusMessage) {
			users.get(((GameStatusMessage) igm).getPlayerIndex()).sendMessage(new TurnMessage(
					((GameStatusMessage) igm).getGameName(), checkTurn(((GameStatusMessage) igm).getPlayerIndex())));

		}
		// message from client telling server what pawn and card are selected
		// from which player
		if (igm instanceof PawnCardSelectedMessage) {
			PawnCardSelectedMessage message = (PawnCardSelectedMessage) igm;
			currentPlayer = players.get(message.getPlayerIndex());
			currentPlayerIndex = currentPlayer.getPlayerIndex();
			Card selectedCard = null;

			for (Card card : currentPlayer.getPlayerHand().getCards()) {
				if (card.getCardId() == message.getCard().getCardId()) {
					selectedCard = card;
					currentPlayer.getPlayerHand().removeCardFromHand(card);
					break;
				}
			}
			Pawn selectedPawn = null;

			for (Pawn p : currentPlayer.getPawns()) {
				if (p.getPawnId() == message.getPawn().getPawnId()) {
					selectedPawn = p;
					System.out.println("found PAWN IN SERVERRRRR");
					selectedPawn.setPawnSelected(true);
				}

			}
			System.out.println("In Server: we got a pawn from this owner: " + selectedPawn.getOwner().getPlayerName()
					+ " from this color " + selectedPawn.getPawnColor().toString());
			System.out.println("and a card from this colo " + selectedCard.getColor().toString());
			performTurn(selectedCard, selectedPawn);
		}
		if (igm instanceof BuyCardsMessage) {
			BuyCardsMessage message = ((BuyCardsMessage) igm);
			ArrayList<Card> purchase = new ArrayList<>();
			ArrayList<LandTile> sold = new ArrayList<>();
			if (message.getCurrentPlayerIndex() == currentPlayerIndex) {
				System.out.println("received buycards message in server");
				int amount = 0;
				for (LandTile t : message.getTreasuresChosen()) {
					amount += t.getLandValue();
				}
				amount = amount / 2;
				while (amount != 0) {
					Card c = cards.deal();
					c.setOwner(currentPlayer);
					currentPlayer.addCard(c);
					purchase.add(c);
					amount--;
				}
				for (LandTile gone : message.getTreasuresChosen()) {
					sold.add(gone);
					currentPlayer.getPlayerHand().getTreasures().remove(gone);
				}
				int numberOfPlayers = getNumOfRegisteredPlayers();
				for (int i = 0; i < numberOfPlayers; i++) {
					users.get(i).sendMessage(new CardsBoughtMessage(getName(), currentPlayer, purchase, sold));

				}

			}
		}
		if (igm instanceof WaterPaidMessage) {
			WaterPaidMessage message = (WaterPaidMessage) igm;
			Player player = players.get(message.getPlayerIndex());
			for (int i = 0; i < player.getPlayerHand().getNumCards(); i++) {
				Card c = player.getPlayerHand().getCards().get(i);
				for (int k = 0; k < message.getCardsChosen().size(); k++) {
					if (c.getCardId() == message.getCardsChosen().get(k).getCardId())
						player.getPlayerHand().removeCardFromHand(c);
				}
			}
			for (int i = 0; i < player.getPlayerHand().getTreasures().size(); i++) {
				LandTile t = player.getPlayerHand().getTreasures().get(i);
				for (int k = 0; k < message.getTreasuresChosen().size(); k++) {
					if (t.getTileId() == message.getTreasuresChosen().get(k).getTileId())
						player.getPlayerHand().getTreasures().remove(t);
				}
			}

			int numberOfPlayers = getNumOfRegisteredPlayers();
			for (int i = 0; i < numberOfPlayers; i++) {
				users.get(i).sendMessage(new PaymentDoneMessage(getName(), player.getPlayerIndex(),
						message.getCardsChosen(), message.getTreasuresChosen()));

			}

		}

	}

	private void performTurn(Card selectedCard, Pawn selectedPawn) {

		ArrayList<Card> newCards = null;

		boolean foundLand = false;
		boolean giveTreasure = false;
		boolean waterHasTile = false;
		boolean nextPlayer = false;
		boolean connectedWater = false;
		int waterPassedCount = 0;
		int waterBill = 0;
		LandTile treasure = null;
		LandTile selectedLand = null;

		// loop through the base and assign watertile
		for (int f = selectedPawn.getNewLocation() + 1; f < base.size() && !foundLand; f++) {
			WaterTile water = base.get(f);
			// check if water has tiles
			int topNode = 0;
			if (water.getChildren().size() > 0) {
				topNode = water.getChildren().size() - 1;
				System.out.println("amount of Water is " + base.size());
				waterHasTile = true;
				connectedWater = false;
			} else {
				if (!connectedWater) {
					connectedWater = true;
					waterPassedCount++;
					int i = payForWater(water);
					waterBill += i;
				}
			}
			// get the top tile on that water
			if (waterHasTile && water.getChildren().get(topNode) instanceof LandTile) {
				waterHasTile = false;
				LandTile land = (LandTile) water.getChildren().get(topNode);
				// does this tile has the same color ?
				if (land.getColor().equals(selectedCard.getColor()) && !land.hasPawn()) {

					land.setPawnOnTile(selectedPawn);
					selectedLand = land;
					selectedPawn.setNewLocation(base.indexOf(water));
					foundLand = true;
					giveTreasure = true;
					nextPlayer = true;
					selectedPawn.setPawnSelected(false);
					// give a new card
					newCards = dealCards(currentPlayer);
					// remove pawn stamp
					removePawnFromOldTile(selectedPawn);

				}
				// here a player played a pawn and landed on another that has a pawn, if he has no other cards, he gets 2 free
				if (land.getColor().equals(selectedCard.getColor()) && land.hasPawn() && !foundLand) {
					ArrayList<Card> extraCards = new ArrayList<>();
					selectedPawn.setNewLocation(base.indexOf(water));
					land.setTempPawn(selectedPawn);
					removePawnFromOldTile(selectedPawn);
					selectedPawn.setPawnSelected(false);
					selectedLand = land;
					foundLand = true;
					giveTreasure = false;
					if(currentPlayer.getPlayerHand().getNumCards()==0){
						Card c1 = cards.deal();
						Card c2 = cards.deal();
						c1.setOwner(currentPlayer);
						c2.setOwner(currentPlayer);
						currentPlayer.addCard(c1);
						currentPlayer.addCard(c2);
						extraCards.add(c1);
						extraCards.add(c2);
						
					}
					users.get(currentPlayer.getPlayerIndex())
							.sendMessage(new PlayAnotherCardMessage(getName(), selectedPawn,extraCards));

				}
			}
			if (!foundLand && f == base.size() - 1) {
				System.out.println("reached the end, f now is " + f);
				selectedPawn.setNewLocation(f+1);
				selectedPawn.setReachedMainLand(true);
				mainland.getPawns().add(selectedPawn);
				removePawnFromOldTile(selectedPawn);
				foundLand = true;
				selectedLand = null;
				selectedPawn.setPawnSelected(false);
				nextPlayer = true;
				giveTreasure = true;
				newCards = dealCards(currentPlayer);

			}

		}
		if (selectedPawn.getNewLocation() > 0 && foundLand && giveTreasure) {
			System.out.println("Trying to find a treasure for the player");
			treasure = giveTreasureToPlayer(selectedPawn.getNewLocation());
			if (treasure != null) {
				users.get(currentPlayerIndex)
						.sendMessage(new ServerMessage(getName(), "found Treasure: " + treasure.getColor().toString()));

			} else
				users.get(currentPlayerIndex).sendMessage(new ServerMessage(getName(), "No treasure found"));

		}
		int numberOfPlayers = getNumOfRegisteredPlayers();
		for (int i = 0; i < numberOfPlayers; i++) {
			users.get(i).sendMessage(new RefreshPlayerMessage(getName(), currentPlayer, selectedLand, selectedPawn,
					selectedCard, treasure, newCards, waterBill, waterPassedCount));
		}
		if (foundLand && nextPlayer)
			endTurn();

	}

	private int payForWater(WaterTile water) {
		int waterIndex = base.indexOf(water);
		WaterTile waterBefore;
		WaterTile waterAfter;
		LandTile landBefore;
		LandTile landAfter;
		int valueBefore = 0;
		int valueAfter = 0;
		boolean foundPreviousWaterWithTiles = false;
		boolean foundAfterWaterWithTiles = false;

		if (waterIndex != 0) {
			waterBefore = base.get(waterIndex - 1);
			if (waterBefore.getChildren() != null) {
				landBefore = (LandTile) waterBefore.getChildren().get(waterBefore.getChildren().size() - 1);
				valueBefore = landBefore.getLandValue();
			}
		} else {
			waterBefore = null;
			landBefore = null;
			valueBefore = 0;

		}

		if (waterIndex != base.size() - 1) {

			int waterAfterIndex = waterIndex + 1;
			while (!foundAfterWaterWithTiles) {
				waterAfter = base.get(waterAfterIndex);
				if (waterAfter.getChildren().size() != 0) {
					landAfter = (LandTile) waterAfter.getChildren().get(waterAfter.getChildren().size() - 1);

					valueAfter = landAfter.getLandValue();
					foundAfterWaterWithTiles = true;
				} else
					waterAfterIndex++;
			}

		} else if (waterIndex == base.size() - 1) {
			waterAfter = null;
			landAfter = null;
			valueAfter = 0;
		}
		if (valueBefore < valueAfter)
			return valueBefore;
		else
			return valueAfter;

	}

	public void endTurn() {

		// end current player turn
		System.out.println("reached a point where we change player index");
		if (currentPlayerIndex == players.size() - 1) {
			currentPlayerIndex = 0;
			currentPlayer = players.get(currentPlayerIndex);
		} else {
			currentPlayerIndex++;
			currentPlayer = players.get(currentPlayerIndex);
		}
		int numberOfPlayers = getNumOfRegisteredPlayers();
		for (int i = 0; i < numberOfPlayers; i++) {
			users.get(i).sendMessage(new GameStatusMessage(getName(), true, currentPlayer));

		}

	}

	public ArrayList<Card> dealCards(Player player) {
		ArrayList<Card> result = new ArrayList<>();
		// after each round a player gets a card anyway
		Card turnCard = cards.deal();
		turnCard.setOwner(player);
		player.getPlayerHand().addCard(turnCard);
		result.add(turnCard);
		// for each pawn in mainland he gets an extra one
		for (Pawn p : player.getPawns()) {
			if (p.ReachedMainLand()) {
				Card newCard = cards.deal();
				newCard.setOwner(player);
				player.getPlayerHand().addCard(newCard);
				result.add(newCard);
			}
		}
		return result;
	}

	private void removePawnFromOldTile(Pawn selectedPawn) {
		WaterTile w = null;
		if (selectedPawn.getOldLocation() != -1) {
			w = base.get(selectedPawn.getOldLocation());
			((LandTile) w.getChildren().get((w.getChildren().size() - 1))).removePawn(selectedPawn);

		}
	}

	private LandTile giveTreasureToPlayer(int f) {
		boolean gotIt = false;
		LandTile treasure = null;
		int waterIndex = -1;
		while (!gotIt && f + waterIndex >= 0) {
			WaterTile previousWater = base.get(f + waterIndex);
			if (previousWater.getChildren().size() != 0
					&& previousWater.getChildren().get(previousWater.getChildren().size() - 1) instanceof LandTile
					&& !((LandTile) previousWater.getChildren().get(previousWater.getChildren().size() - 1))
							.hasPawn()) {

				treasure = (LandTile) previousWater.getChildren().remove(previousWater.getChildren().size() - 1);
				currentPlayer.getPlayerHand().addTreasure(treasure);

				gotIt = true;
			}

			waterIndex -= 1;

		}
		return treasure;
	}

	private boolean checkTurn(int playerIndex) {
		if (playerIndex == currentPlayerIndex) {
			return true;
		} else
			return false;
	}

	public void setPlayerColorAndTurn(Player player, int index) {
		if (index == 0) {
			player.setColor(ColorChoice.blue);
		} else if (index == 1) {
			player.setColor(ColorChoice.red);
		} else if (index == 2) {
			player.setColor(ColorChoice.green);
		} else if (index == 3) {
			player.setColor(ColorChoice.purple);
		}

	}

	public void distributeLandTiles() {
		// Distribution of Land Tiles according to the rules the first 10 stacks
		// are doubled ..

		// DeckA
		for (int i = 0; i < 26; i++) {
			LandTile tile = deckA.getDeckOfTiles().remove(0);
			base.get(i).addLand(tile);
		}
		for (int i = 0; i < 10; i++) {
			LandTile tile = deckA.getDeckOfTiles().remove(0);
			base.get(i).addLand(tile);
		}
		for (int i = 21; i < 26; i++) {
			LandTile tile = deckA.getDeckOfTiles().remove(0);
			base.get(i).addLand(tile);
		}

		// DeckB
		for (int i = 27; i < 53; i++) {
			LandTile tile = deckB.getDeckOfTiles().remove(0);
			base.get(i).addLand(tile);
		}
		for (int i = 27; i < 33; i++) {
			LandTile tile = deckB.getDeckOfTiles().remove(0);
			base.get(i).addLand(tile);
		}
		for (int i = 43; i < 53; i++) {
			LandTile tile = deckB.getDeckOfTiles().remove(0);
			base.get(i).addLand(tile);
		}
		// now convert the arraylist to children
		for (int i = 0; i < base.size(); i++) {
			base.get(i).convertToChildren();
		}

	}

	private ArrayList<Card> cardsForPlayers(int playerIndex, Player player) {
		ArrayList<Card> result = new ArrayList<>();

		for (int i = playerIndex + 4; i > 0; i--) {
			Card card = cards.deal();
			card.setOwner(player);
			result.add(card);
			player.addCard(card);
		}
		return result;
	}

	// Getters for kevin
	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public int getNumOfRegisteredPlayers() {
		return users.size();
	}

	// adds new player to game
	public void addUser(User user) {
		users.add(user);

	}

}
