module Workspace {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.sql;
	
	exports gui;
	exports model.entities;

	opens gui to javafx.fxml;
	opens model.entities to javafx.fxml;

	opens application to javafx.graphics, javafx.fxml;
}
