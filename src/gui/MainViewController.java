package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {
	/**
	 * Atributos dos intens menu
	 */
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbaout;

	@FXML
	/**
	 * Métodos para tratar os eventos do menu
	 */
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService()); // Injentando a dependencia
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService()); // Injentando a dependencia
			controller.updateTableView();
		});// Função foi parametrizada
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	private synchronized <T> void loadView(String absolutName, Consumer <T> initializingAction ) { // Garantir que todo processamento da tela dentro try n seja interronpido uso synchronaizse
		try {																								//InterfaceCossumer
			// fxmlload OBJEETO que carrega uma tela
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName)); // vour passar por argumento o
																						// caminho da pagina
			VBox newVbox = loader.load();
			/**
			 * MANIPULANDO A SCENA PRINCIPAL
			 */
			Scene mainScene = Main.getMainScene();

			// Pega o primeiro elemento da Mainview (scrollPane), depois acessa conteudo do
			// scrollPane
			// Peguei uma referencia principal do meu VBOX da janela principal
			VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			// Guarda referencia do menu
			Node mainMenu = mainVbox.getChildren().get(0); // primeiro filho da janela principal
			mainVbox.getChildren().clear(); // Limpar todo o resto da janela que eu n quero

			mainVbox.getChildren().add(mainMenu);
			mainVbox.getChildren().addAll(newVbox.getChildren()); // Vou addicionar todos os filhos do novoVbox nessa
																	// tela
			
			T controller = loader.getController();//Comando ativar a função que vem por parametro e retorna um departlistcontrole
			initializingAction.accept(controller); // Executa a funçaõ
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	

	
	
	
}
