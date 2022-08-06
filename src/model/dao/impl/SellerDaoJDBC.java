package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbExecption;
import db.DbIntegrityExcpetion;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

// Classe de implementa os Dao especificos para o JDBC
public class SellerDaoJDBC implements SellerDao {

// dao dependendia com a conexão disponivel em qualquer metodo da minha classe

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES " + "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalaru());
			st.setInt(5, obj.getDepartment().getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbExecption("Unexpected error! No rows affected!");
			}
		} catch (SQLException e) {
			throw new DbExecption(e.getMessage());
		} finally {
			DB.closeStatemant(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + "WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalaru());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbExecption(e.getMessage());
		} finally {
			DB.closeStatemant(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbExecption(e.getMessage());
		} finally {
			DB.closeStatemant(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + " FROM seller INNER JOIN department"
							+ " ON seller.DepartmentId = department.Id " + " WHERE seller.Id = ?");

			st.setInt(1, id);
			// comando sql do st vai ser executado e o reusltado vai ser armazenado no
			// resultset
			rs = st.executeQuery();
			// rs.next teste de venho um resultado pq rs esta na prosição 0 (que n tem
			// objetop) e quando vem o reusltado vai pra posição 1 (que tem objeto)
			if (rs.next()) { // true entra no if
				// preciso ter os objetos instaciandos em memoria
				// Reutilização de iinstanciação com metodos auxiliadres
				Department dep = indtsnDepartment(rs);
				Seller sel = instSeller(rs, dep);
				return sel;
			}
			return null;

		} catch (SQLException e) {
			throw new DbExecption(e.getMessage());
		} finally {
			// nÃO PRECISA CONECTAR A CONEXÃO JA QUE O OBJETO DAO PODE FAZER MAIS operações
			DB.closeStatemant(st);
			DB.closeResultSet(rs);
		}

	}

	private Seller instSeller(ResultSet rs, Department dep) throws SQLException { //Java sql date n funciona, tem que fazer esse maceta pra transforma para utils
		Seller sel = new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), (new java.util.Date(rs.getDate("BirthDate").getTime())),
				rs.getDouble("BaseSalary"), dep);
		return sel;
	}

	// propagar a exessão
	private Department indtsnDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " 
							+ "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "ORDER BY Name");
			rs = st.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = indtsnDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Seller obj = instSeller(rs, dep);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbExecption(e.getMessage());
		} finally {
			DB.closeStatemant(st);
			DB.closeResultSet(rs);
		}

	}

	

	@Override
	public List<Seller> findByDepartment(Department dep) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name ");
			// passar o ID do departamento
			st.setInt(1, dep.getId());
			rs = st.executeQuery();
			// percoreer o meu result set enquanto tiver um proximo
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			// incluir controle pra n reptir o depart
			while (rs.next()) {

				// Vou buscar um departamento existente no map, se não tiver a variavel derpt
				// recebe valor de null
				Department depart = map.get(rs.getInt("DepartmentId"));
				// Opa esse depart n exit, então eu instancio e guardo no map
				if (depart == null) {
					depart = indtsnDepartment(rs);
					map.put(rs.getInt("DepartmentId"), depart);
				}

				Seller sel = instSeller(rs, depart);
				list.add(sel);
			}
			return list;

		} catch (SQLException e) {
			throw new DbExecption(e.getMessage());
		}
	}

}
