package server.frontend;

import javafx.application.Application;
import javafx.stage.Stage;
/**
* <h1>Class that runs server instance</h1>
* Server of atlantis is started with this class.
* @author  Kevin Neuschwander
* @version 1.0
* @since   2016-12-16
*/
public class AtlantisServer extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
	 @Override
	 public void start(Stage primaryStage) {
	
		 ServerView view = new ServerView(primaryStage);
		 ServerModel model = new ServerModel(view);
		 ServerController controller = new ServerController(view, model);
		 view.start();
		 
		 
	}

}
