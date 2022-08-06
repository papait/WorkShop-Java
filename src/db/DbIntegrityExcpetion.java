package db;

public class DbIntegrityExcpetion extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public DbIntegrityExcpetion (String msg) {
		
		super(msg);
	}
}
