package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.util.PSQLException;

import classes.Student;
import database.postgreSQLHeroku;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BookSearchMenu implements AutoCloseable {

	final int WIDTH = 250;
	private Stage stage;
	private Scene scene;
	
	public BookSearchMenu(Stage stage, Scene scene) {
		this.stage = stage;
		this.scene = scene;
	}
	
	public Scene showMenu(Student stud) throws Exception {
		GridPane pane = new GridPane();
      	pane.setAlignment(Pos.CENTER);
      	pane.setHgap(2);
    	pane.setVgap(10);
      	Label heading = new Label("Choose a field to search.\n");
      	
      	Button searchBtn = new Button("Find");
      	Button backBtn = new Button("Back");
      	TextField bookField = new TextField();
      	ChoiceBox <String> choiceBox = new ChoiceBox<>();
      	
      	Platform.runLater(()->bookField.requestFocus());
      	
      	//getItems
      	choiceBox.getItems().addAll("Title","Author","Publisher","ID");
      	choiceBox.setValue("Title");

      	
      	//searchBtn.setMaxWidth(WIDTH);
      	searchBtn.setOnAction((event) -> {
          	String choiceBoxString = choiceBox.getValue();
          	String searchString = bookField.getText();
      		this.handleSearch(choiceBoxString, searchString, stud); });

      	
      	pane.setOnKeyPressed((event) -> {
      	    if(event.getCode() == KeyCode.ENTER) {
      	        // method handler
      	    	String choiceBoxString = choiceBox.getValue();
              	String searchString = bookField.getText();
          		this.handleSearch(choiceBoxString, searchString, stud); 
      	    }
      	});
				

      	
      	
      	
      	backBtn.setOnAction(e-> {
			
      		try (StudentMenu studentMenu = new StudentMenu(stage, scene)) 
    		{

    			stage.setScene(studentMenu.showMenu(stud));
    			stage.setTitle("Student Home Page");
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
		});
      	
      	searchBtn.setStyle("-fx-background-color: mediumaquamarine");
        backBtn.setStyle("-fx-background-color: coral");
      	pane.add(heading, 0, 0);
      	pane.add(choiceBox, 0, 1);
      	pane.add(bookField, 0, 2);
      	pane.add(searchBtn, 1, 2);
      	pane.add(backBtn, 0, 3);
      	
      	pane.setStyle("-fx-background-color: #B7D8D6");
      	Scene scene = new Scene(pane, 350, 450);
	    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    return scene;
	}
	
	public void handleSearch(String choiceBoxString, String searchString, Student stud)
	{
		if(choiceBoxString == null)
		{
			//System.out.println("Please enter a valid field from the dropdown menu!");
			AlertBox.display("Error!", "Please enter a valid field from the dropdown menu!");
		}
		else if (searchString == "") 
		{
			//System.out.println("Please enter something to search!");
			AlertBox.display("Error!","Please enter something to search!");
		}
		else
		{
			try(Connection connection = DriverManager.getConnection(postgreSQLHeroku.DATABASE_URL, postgreSQLHeroku.DATABASE_USERNAME, postgreSQLHeroku.DATABASE_PASSWORD)) {

				Statement statement = connection.createStatement();
				String query = "";
				
				if (choiceBoxString.equalsIgnoreCase("ID"))
				{
					query = String.format("select * from %s where %s = %s;", postgreSQLHeroku.TABLE_LIBRARY, postgreSQLHeroku.COL_ID, searchString);
					
				}
				else
				{
					query = String.format("select * from %s where upper(%s) like upper('%%%s%%');", postgreSQLHeroku.TABLE_LIBRARY, choiceBoxString, searchString);
				}
				//System.out.println(choiceBoxString);
				//System.out.println(query);
				ResultSet queryResult = statement.executeQuery(query); 
				
				
				try (BookTable bookTable = new BookTable(stage, scene)) 
				{
					stage.setScene(bookTable.showMenu(queryResult, stud));
					stage.setTitle("Search Results");
				} catch (Exception e) {
					e.printStackTrace();
				}
				

			} catch (PSQLException e2) {
				e2.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
