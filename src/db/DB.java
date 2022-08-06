package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {

	//Metodos conexeção com o banco de dados
	
	private static Connection conn = null;
	//Metodo gerar uma conexãp BD
	public static Connection getConnection ()  {
		if (conn == null) {
			try {
			Properties props = loadProperties(); // variavel propes pegar as minhas propriedades metodo
			String url = props.getProperty("dburl"); // esse valor e do arquivo db
			conn = DriverManager.getConnection(url, props);// Essa class JDBC realizar a conexeção
			// Conectar com o BD no jdbc e instanciar um objeto com o Drivermanager
			} 
			catch (SQLException e) {
				throw new DbExecption(e.getMessage());
			}
		}
		return conn;
	}
	// Metodo fechar uma coneção
	public static void closeConnection () {
		try {
		if (conn != null) {
			conn.close();
		}
		}
		catch (SQLException e) {
			throw new DbExecption(e.getMessage());
		}
	}
	
	//Metodo para carregar as propriedades do arquivo db.properties
	private static Properties loadProperties () {
		try (FileInputStream fs = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fs); //load leitura do arquivo properties apontado pelo inputstream e guarda o dados no objeto
			return props;
		} catch (IOException e) {
			throw new DbExecption(e.getMessage());
		}
	}
	
	public static void closeStatemant (Statement st) {
		if (st != null) { //st diferente de null ele ta instanciado
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbExecption(e.getMessage()); //expetion runtime não precisa tratar toda hr com tyr cacth
			}
		}
	}
	public static void closeResultSet (ResultSet rt) {
		if (rt != null) { //st diferente de null ele ta instanciado
			try {
				rt.close();
			} catch (SQLException e) {
				throw new DbExecption(e.getMessage()); //expetion runtime não precisa tratar toda hr com tyr cacth
			}
		}
	}
}
