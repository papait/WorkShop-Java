package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbExecption;
import gui.listeners.DataChangeInterface;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Exception.ValidationException;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;

	private DepartmentService service;
	
	private List <DataChangeInterface> dataChangeList = new ArrayList<>();

	@FXML
	private TextField txId;

	@FXML
	private TextField txName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	public void subscribeDataChangeListener (DataChangeInterface listener) {
		dataChangeList.add(listener);
	}
	
	@FXML
	public void onBtSaveAction (ActionEvent event) {
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
			Utils.currentStage(event).close();//Pegando uma referen da janela atual e fecho ela
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

	private Department getFormData() {
		Department dep = new Department();

		ValidationException exception = new ValidationException("Validation error");
		
		dep.setId(Utils.tryParseToiNT(txId.getText())); // Pegar os dados ID
														// Uso o mwetodo no utils pra converter
		if (txName.getText()==null || txName.getText().trim().equals("")) { //Possivel error de campo vazio
			exception.addError("name", "Field can´t be empty");
		}
		dep.setName(txName.getText());

		if (exception.getErros().size()>0) { //Verificar se a coleção esta vazia
			throw exception;
		}
		
		return dep;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void setDepartment(Department entity) { // Inserção de dependecia
		this.entity = entity;
	}

	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initialiazeNodes();
	}

	private void initialiazeNodes() {
		Constraints.setTextFieldInteger(txId); // Id só aceita valor inteiro
		Constraints.setTextFieldMaxLength(txName, 30); // Name só recebe no max 30
	}

	public void updateFormData() {
		if (entity == null) { // programação defensiva
			throw new IllegalStateException("Entity eas null");
		}
		txId.setText(String.valueOf(entity.getId()));// converter para string
		txName.setText(entity.getName());
	}
	
	private void setErrorMessages (Map<String, String> error) {
		Set<String> fields = error.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(error.get("name"));
		}
	}
}
