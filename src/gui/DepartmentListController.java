package gui;

import java.io.IOException;
import java.net.URL;
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
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeInterface {

	/*
	 * Declarando a dependencia a serem injetaas na class Evitando um aclopamento
	 * forte new DepartmentService (conteudo de interfaces )
	 */
	private DepartmentService service;

	@FXML
	private TableColumn<Department, Department> tableColumnEdit;

	@FXML
	private TableColumn<Department, Department> tableColumRemove;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumId;

	@FXML
	private TableColumn<Department, String> tableColumName;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList; // Carregar os departamentos nessa lista do JAVAFEX

	@FXML
	public void onBtNewAction(ActionEvent event) { // acessar meu stage primario usando

		Stage parentStage = Utils.currentStage(event); // Passo Referencia do meu stage atual como argumento e passo
														// para a nova janela creatDialog
		Department obj = new Department(); // Instancia um departm vazio pq é novo form
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service; // Injentando a dependencia
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // Metodo auxiliar iniciar um evento
	}

	private void initializeNodes() {
		/**
		 * Padrão JX para iniciar o comportamento das colunas
		 */
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow(); // Pega referencia do palco. Window é uma super classe do
																// stage
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // Macete tableview acompanhar altura da
																				// janela
	}

	public void updateTableView() {
		if (service == null) { // injeção de depencia invalida//Tratamento
			throw new IllegalStateException("Service was Null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list); // instanciar obslista com os dados da list
		tableViewDepartment.setItems(obsList);
		initEditButtons(); // Adicionar um novo botão text edit em cada linha da tabela e abrir o
							// formulário de edição
		initRemoveButtons();
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) { // passar o stage que criou
																							// a janela de dialogo
		try { // Absolutoname é o caminho da tela
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // Carregar minah viewer
			// Carrgear uma janela na frente de outra (Palco na frent do outro)

			DepartmentFormController controller = loader.getController();
			// Injeção de dependencias manual
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.updateFormData();

			controller.subscribeDataChangeListener(this); // me inscrever pra recebr aquele evento e exeucatar o metodo
															// onDataChange

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane)); // Pane vai ser o elemento da nova scene
			dialogStage.setResizable(false); // metodo que não permite janela ser redimencionada
			dialogStage.initOwner(parentStage); // Método que quem que é o stage pai dessa nova stage janela
			dialogStage.initModality(Modality.WINDOW_MODAL); // Vai falar se a janela vai modal ou outro comportamento
																// (Nesse caso ela via ficar travada)
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@Override
	public void onDataChaned() { // Quando recber uma objeto que altera os dados, executa um operação
		updateTableView();

	}

	// Criar um um objeto setCellFactory que instancia os botões

	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			// Criar o evento do botap
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event))); // Cria uma tebala com o formulário ja preenchido
			}
		});
	}

	private void initRemoveButtons() {
		tableColumRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumRemove.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove"); //CRIAR UM BOTÃO com nome remove em cada linha da minha nova coluna

			@Override
			protected void updateItem(Department obj, boolean empty) {
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

	private void removeEntity(Department obj) {
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
