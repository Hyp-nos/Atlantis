package client.game;

import java.util.ArrayList;
import java.util.Optional;

import gameObjects.Card;
import gameObjects.Pawn;
import gameObjects.WaterTile;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;

public class GameController {

	private GameView view;
	private GameModel model;

	public GameController(GameView gameView, GameModel gameModel) {
		this.view = gameView;
		this.model = gameModel;
		view.btnRevert.setDisable(true);
		view.btnPlayCard.setOnAction(e -> handlePlayCard());
		view.btnPlayCard.setOnMouseEntered(e-> view.rotate.play());
		view.btnPlayCard.setOnMouseExited(e-> view.rotate.stop());
		view.btnBuyCards.setOnAction(e -> handleBuyCard());
		view.btnPay4cards.setOnAction(e-> handlePay4cards());
		view.btnPay4Water.setOnAction(e-> handlePay4Water());
		view.btnCalc.setOnAction(e-> handleCalc());
		view.btnEndMyTurn.setOnAction(e->handleEndMyTurn());
		view.btnRevert.setOnAction(e-> handleRevert());
		view.btnNotEnough.setOnAction(e-> handleNotEnough());	
		view.stage.setOnCloseRequest(e ->handleClose(e));
	}


	
	private void handleClose(WindowEvent e) {
		model.playerLeft(e);

	}



	private void handleNotEnough() {
		model.handleNotEnough();
		
	}

	private void handleRevert() {
		model.handleRevert();
	}



	private void handleEndMyTurn() {
		model.handleEndMyTurn();
	}



	private void handleCalc() {
		model.handleCalc();
	}



	private void handlePay4Water() {
		model.pay4Water();
	}



	private void handlePay4cards() {
		model.pay4Cards();
	
	}



	private void handleBuyCard() {
		model.tryBuyCards();
		
	}

	private void handlePlayCard() {
		model.tryPlayCard();

	}
/*
	private void handleBase(MouseEvent e) {
		if (!(((StackPane) e.getSource()).getChildren()
				.get(((StackPane) e.getSource()).getChildren().size() - 1) instanceof WaterTile))
			((StackPane) e.getSource()).getChildren().remove(((StackPane) e.getSource()).getChildren().size() - 1);

	}*/

}