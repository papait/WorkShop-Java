package model.services;

import java.util.ArrayList;
import java.util.List;

import db.DB;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao daoDep = DaoFactory.createDepartamentDao(); //Fiz a dependencia e injetei ela suando padr�o dao
																		//Bom de fazer projeto em camadas � a facilidade de implementar
																		// Nesse caso foi aproveitado a camada de dados do outro projeto
	public List<Department> findAll(){
		return daoDep.findAll();
	}
	
	public void saveOrUpdate (Department obj) {
		if (obj.getId()== null) { //Objeto tem id=null � um novo objeto
			daoDep.insert(obj);
		}
		else {
			daoDep.update(obj); // Se if existe � um update
		}
	}
	
	public void remove (Department obj) {
		daoDep.deleteById(obj.getId());
	}
}
