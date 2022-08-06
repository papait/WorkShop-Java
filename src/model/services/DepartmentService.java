package model.services;

import java.util.ArrayList;
import java.util.List;

import db.DB;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao daoDep = DaoFactory.createDepartamentDao(); //Fiz a dependencia e injetei ela suando padrão dao
																		//Bom de fazer projeto em camadas é a facilidade de implementar
																		// Nesse caso foi aproveitado a camada de dados do outro projeto
	public List<Department> findAll(){
		return daoDep.findAll();
	}
	
	public void saveOrUpdate (Department obj) {
		if (obj.getId()== null) { //Objeto tem id=null é um novo objeto
			daoDep.insert(obj);
		}
		else {
			daoDep.update(obj); // Se if existe é um update
		}
	}
	
	public void remove (Department obj) {
		daoDep.deleteById(obj.getId());
	}
}
