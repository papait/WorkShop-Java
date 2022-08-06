package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;





public class Main extends Application {
	
	//Para mim trabalhar com a janela principal eu uso uma referência da scena (Para conseguir usar a scena na janela about)
	//export referencia da scena

	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		
		
		
		try {
			 FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml")); // Instanciando a tela carrega MainView com o Container
			 ScrollPane scrollPane = loader.load(); // Container
			 
			 scrollPane.setFitToHeight(true);
			 scrollPane.setFitToWidth(true); // ajuste do menu a container
			 
			 
			 mainScene = new Scene(scrollPane); //criando scene passando nó
			 primaryStage.setScene(mainScene); //Setando a scenna principal no palco
			 primaryStage.setTitle("Sample JavaFX application"); //setando um titulo
			 primaryStage.show(); // mostrando o palco
			 } catch (IOException e) {
			 e.printStackTrace();
			 } 

		
	}
	
	public static Scene getMainScene () {
		return mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
