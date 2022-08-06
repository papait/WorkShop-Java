package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
// essa class operaçõe static para instanciar os daos
// Macete a minha class um metodo de retorna o tipo da interface e internamente  retornar uma implementação
	// macete pra não expor a implementação
	//Meu programa só vai conhecer a interface e n a implementação
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartamentDao () {
		return new DepartmentDaoJDBC(DB.getConnection());
	}
	
}
