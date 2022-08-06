package model.services;

import java.util.ArrayList;
import java.util.List;

import db.DB;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerService {

	private SellerDao daoSeller =  DaoFactory.createSellerDao(); //Fiz a dependencia e injetei ela suando padrão dao
																		//Bom de fazer projeto em camadas é a facilidade de implementar
																		// Nesse caso foi aproveitado a camada de dados do outro projeto
	public List<Seller> findAll(){
		return daoSeller.findAll();
	}
	
	public void saveOrUpdate (Seller obj) {
		if (obj.getId()== null) { //Objeto tem id=null é um novo objeto
			daoSeller.insert(obj);
		}
		else {
			daoSeller.update(obj); // Se if existe é um update
		}
	}
	
	public void remove (Seller obj) {
		daoSeller.deleteById(obj.getId());
	}
}
