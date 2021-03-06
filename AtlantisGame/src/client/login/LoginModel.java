package client.login;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.lobby.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import messageObjects.ServerInfoMessage;
import messageObjects.userMessages.CreateUserMessage;
import messageObjects.userMessages.LoginMessage;
import messageObjects.userMessages.UserInfoMessage;
/**
* <h1>Logic for login screen</h1>
* Client side logic of login and creation of users. Sends messages to server.
* @author  Kevin Neuschwander
* @version 1.0
* @since   2016-12-16
*/
public class LoginModel {
	private Socket socket;
	private LoginView view;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public LoginModel(Socket socket, LoginView view)
	{
		this.socket = socket;
		this.view = view;
		
		try{
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			

		} catch(IOException e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unexpected error");
			alert.setContentText("An unexpected error ocurred. Please try to restart the program");
			alert.showAndWait();
				
		}
		
		
	}
	
	//Sends entered login data to server and processes the answer it gets about it
	protected void processLogin()
	{
		try {
			oos.writeObject(new LoginMessage(view.loginusernametxt.getText(), view.loginpasswordtxt.getText()));
			oos.flush();
			Object reply = ois.readObject();
			if(reply instanceof ServerInfoMessage)
			{
				ServerInfoMessage em = (ServerInfoMessage)reply;
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Atlantis server notification");
				alert.setContentText(em.getMessage());
				alert.showAndWait();
				view.loginButton.setDisable(false);
			}
			else if(reply instanceof UserInfoMessage)
			{
				
				UserInfoMessage nowLoggedInAs = (UserInfoMessage)reply;
				LobbyView lobbyView = new LobbyView();
				LobbyModel lobbyModel = new LobbyModel(lobbyView, nowLoggedInAs, oos, ois);
				LobbyController lobbyController = new LobbyController(lobbyView, lobbyModel);
				lobbyView.start();
				lobbyModel.startListener();
				view.close();
			}
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unexpected error");
			alert.setContentText("An unexpected error ocurred. Please try to restart the program");
			alert.showAndWait();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Sends 'create new user' data to server and processes the answer about it
	protected void processNewUser()
	{
		try {
			oos.writeObject(new CreateUserMessage(view.createusernametxt.getText(), view.createpasswordtxt.getText()));
			oos.flush();
			
			Object reply = ois.readObject();
			
			if(reply instanceof ServerInfoMessage)
			{
				ServerInfoMessage em = (ServerInfoMessage)reply;
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Atlantis server notification");
				alert.setContentText(em.getMessage());
				alert.showAndWait();
				view.createButton.setDisable(false);
			}
			else if(reply instanceof UserInfoMessage)
			{
				UserInfoMessage nowLoggedInAs = (UserInfoMessage)reply;
				
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Atlantis server notification");
				alert.setContentText("Account '" + nowLoggedInAs.getUsername() + "' successfully created.");
				alert.showAndWait();
				
				
				LobbyView lobbyView = new LobbyView();
				LobbyModel lobbyModel = new LobbyModel(lobbyView, nowLoggedInAs, oos, ois);
				LobbyController lobbyController = new LobbyController(lobbyView, lobbyModel);
				lobbyView.start();
				lobbyModel.startListener();
				view.close();
				
			}
			
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unexpected error");
			alert.setContentText("An unexpected error ocurred. Please try to restart the program");
			alert.showAndWait();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
