package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityExcpetion;
import gui.listeners.DataChangeInterface;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeInterface {

	/*
	 * Declarando a dependencia a serem injetaas na class Evitando um aclopamento
	 * forte new SellerService (conteudo de interfaces )
	 */
	private SellerService service;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEdit;

	@FXML
	private TableColumn<Seller, Seller> tableColumRemove;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumId;

	@FXML
	private TableColumn<Seller, String> tableColumName;
	
	@FXML
	private TableColumn<Seller, String> tableColumEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumBaseSalary;

	@FXML
	private Button btNew;

	private ObservableList<Seller> obsList; // Carregar os departamentos nessa lista do JAVAFEX

	@FXML
	public void onBtNewAction(ActionEvent event) { // acessar meu stage primario usando

		Stage parentStage = Utils.currentStage(event); // Passo Referencia do meu stage atual como argumento e passo
														// para a nova janela creatDialog
		Seller obj = new Seller(); // Instancia um departm vazio pq é novo form
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	public void setSellerService(SellerService service) {
		this.service = service; // Injentando a dependencia
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // Metodo auxiliar iniciar um evento
	}

	private void initializeNodes() {
		/**
		 * Padrão JFx para iniciar o comportamento das colunas
		 */
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumBirthDate, "dd/mm/yyyy");
		tableColumBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalaru"));
		Utils.formatTableColumnDouble(tableColumBaseSalary, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // Pega referencia do palco. Window é uma super classe do
																// stage
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); // Macete tableview acompanhar altura da
																				// janela
	}

	public void updateTableView() {
		if (service == null) { // injeção de depencia invalida//Tratamento
			throw new IllegalStateException("Service was Null");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list); // instanciar obslista com os dados da list
		tableViewSeller.setItems(obsList);
		initEditButtons(); // Adicionar um novo botão text edit em cada linha da tabela e abrir o
							// formulário de edição
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) { // passar o stage que criou
																							// a janela de dialogo
		try { // Absolutoname é o caminho da tela
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // Carregar minah viewer
			// Carrgear uma janela na frente de outra (Palco na frent do outro)

			SellerFormController controller = loader.getController();
			// Injeção de dependencias manual
			controller.setSeller(obj);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjetc();
			controller.updateFormData();

			controller.subscribeDataChangeListener(this); // me inscrever pra recebr aquele evento e exeucatar o metodo
															// onDataChange

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane)); // Pane vai ser o elemento da nova scene
			dialogStage.setResizable(false); // metodo que não permite janela ser redimencionada
			dialogStage.initOwner(parentStage); // Método que quem que é o stage pai dessa nova stage janela
			dialogStage.initModality(Modality.WINDOW_MODAL); // Vai falar se a janela vai modal ou outro comportamento
																// (Nesse caso ela via ficar travada)
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace(); //Aparece no console emnsgaem de erro
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChaned() { // Quando recber uma objeto que altera os dados, executa um operação
		updateTableView();

	}

	// Criar um um objeto setCellFactory que instancia os botões

	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			// Criar o evento do botap
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event))); // Cria uma tebala com o formulário ja preenchido
			}
		});
	}

	private void initRemoveButtons() {
		tableColumRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove"); //CRIAR UM BOTÃO com nome remove em cada linha da minha nova coluna

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmátion", "Are you sure to delete"); //Retornar um botão clicavel
		// Optional carrega o objeto dentro dele podem sem presente ou não
		if (result.get() == ButtonType.OK) {
			if (service == null) { //Esqueceu de injeta a referencia
				throw new IllegalStateException("Service was null ");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityExcpetion e) {
				Alerts.showAlert("Error Removing object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}

}
