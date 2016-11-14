package client.game;

import gameObjects.Pawn;
import gameObjects.WaterTile;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class GameController {

	private GameView view;
	private GameModel model;

	public GameController(GameView gameView, GameModel gameModel) {
		this.view = gameView;
		this.model = gameModel;
		// for (StackPane s: view.getBase()){
		// s.setOnMouseClicked(e-> handleBase(e));

		// }
		view.btnPlayCard.setOnAction(e -> handlePlayCard());
		view.btnBuyCard.setOnAction(e -> handleBuyCard());
		view.btnPayWithTreasure.setOnAction(e -> handlePayWithTreasure());
	
	}

	private void handlePayWithTreasure() {
		// TODO Auto-generated method stub
	}

	private void handleBuyCard() {
		model.tryBuyCards();
		
	}

	private void handlePlayCard() {
		model.tryPlayCard();

	}

	private void handleBase(MouseEvent e) {
		if (!(((StackPane) e.getSource()).getChildren()
				.get(((StackPane) e.getSource()).getChildren().size() - 1) instanceof WaterTile))
			((StackPane) e.getSource()).getChildren().remove(((StackPane) e.getSource()).getChildren().size() - 1);

	}

}