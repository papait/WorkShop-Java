package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbExecption;
import gui.listeners.DataChangeInterface;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.Exception.ValidationException;
import model.entities.Department;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeInterface> dataChangeList = new ArrayList<>();

	@FXML
	private TextField txId;

	@FXML
	private TextField txName;

	@FXML
	private TextField txEmail;

	@FXML
	private DatePicker dpBirthDate; // Campo date é diferente

	@FXML
	private TextField txBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList; // CARREGAR A LIST COM OS DEP DO BANCO

	public void subscribeDataChangeListener(DataChangeInterface listener) {
		dataChangeList.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) { // esqueceu de injetar a dependencia
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData(); // metodo responsvael por pegar os dados e criar um objeto depart
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();// Pegando uma referen da janela atual e fecho ela
		} catch (DbExecption e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErros());
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeInterface listener : dataChangeList) {
			listener.onDataChaned();
		}
	}
	//Pega os dados preenchidos no formulario
	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToiNT(txId.getText())); // Pegar os dados ID
														// Uso o mwetodo no utils pra converter
		if (txName.getText() == null || txName.getText().trim().equals("")) { // Possivel error de campo vazio
			exception.addError("name", "Field can´t be empty");
		}
		obj.setName(txName.getText());

		if (txEmail.getText() == null || txEmail.getText().trim().equals("")) { // Possivel error de campo vazio
			exception.addError("email", "Field can´t be empty");
		}
		obj.setEmail(txEmail.getText());
		
		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can´t be empty");
		} else {
		Instant instante = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault())); // cONVERTER DATA DO PC DO USUARIO PARA UMA DATA INDEFINIDA INSTANTE
		obj.setBirthDate(Date.from(instante));
		}
		
		if (txBaseSalary.getText() == null || txBaseSalary.getText().trim().equals("")) { // Possivel error de campo vazio
			exception.addError("basesalaru", "Field can´t be empty");
		}
		obj.setBaseSalaru(Utils.tryParseToDouble(txBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		if (exception.getErros().size() > 0) { // Verificar se a coleção esta vazia de erros
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setServices(SellerService service, DepartmentService depservice) { // Injetar dependencias
		this.service = service;
		this.departmentService = depservice;
	}

	public void setSeller(Seller entity) { // Inserção de dependecia
		this.entity = entity;
	}

	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initialiazeNodes();
	}

	private void initialiazeNodes() {
		Constraints.setTextFieldInteger(txId); // Id só aceita valor inteiro
		Constraints.setTextFieldMaxLength(txName, 30); // Name só recebe no max 30
		Constraints.setTextFieldDouble(txBaseSalary);
		Constraints.setTextFieldMaxLength(txEmail, 80);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}

	// PEGA os dados dos objetos e joga nas caixinhas do formulário
	public void updateFormData() {
		if (entity == null) { // programação defensiva
			throw new IllegalStateException("Entity eas null");
		}
		txId.setText(String.valueOf(entity.getId()));// converter para string
		txName.setText(entity.getName());
		txEmail.setText(entity.getEmail());
		txBaseSalary.setText(String.format("%.2f", entity.getBaseSalaru()));
		Locale.setDefault(Locale.US);
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); // Datepicker
																													// trabalha
																													// com
																													// local.date		// Mostras data no formato local do usuario
		if (entity.getDepartment()== null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		else {	
		comboBoxDepartment.setValue(entity.getDepartment()); //Funciona se o dep n for nulo, pra novo sellers tem que ter cuidado
		}
		} 
	}

	/*
	 * Método responsavel por chamar por chamar depService e carrega os
	 * departamentos do db preechendo a lista
	 */
	public void loadAssociatedObjetc() {
		if (departmentService == null) {
			throw new IllegalAccessError("DepartmentService WAS NULL");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList); // Set a list ao combox
	}

	//TESTA CADA UM DOS POSSIVEIS ERROS E SETAR O LABEL DELES
	private void setErrorMessages(Map<String, String> error) { //Da pra usar operador ternario
		Set<String> fields = error.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(error.get("name"));
		}
		else {
			labelErrorName.setText("");
		}
		
		if (fields.contains("email")) {
			labelErrorEmail.setText(error.get("email"));
		}
		else {
			labelErrorEmail.setText("");
		}
		
		if (fields.contains("basesalaru")) {
			labelErrorBaseSalary.setText(error.get("basesalaru"));
		}
		else {
			labelErrorBaseSalary.setText("");
		}
	
		if (fields.contains("birthDate")) {
			labelErrorBirthDate.setText(error.get("birthDate"));
		}

		
	}

	//Metodo iniciar o combobox
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
