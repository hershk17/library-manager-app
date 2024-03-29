package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import classes.BorrowedBooksTableLine;
import classes.Student;
import database.postgreSQLHeroku;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BorrowedLibTable  implements AutoCloseable {

	final int WIDTH = 250;
	final int PADDING = 10;
	private Stage stage;
	private Scene scene;
	private String lines = postgreSQLHeroku.COL_STUD_NO + ", "
  			+ postgreSQLHeroku.COL_STUD_FNAME + ", " 
  			+ postgreSQLHeroku.COL_STUD_LNAME  + ", "
  			+ postgreSQLHeroku.COL_ID  + ", "
  			+ postgreSQLHeroku.COL_TITLE  + ", "
  			+ postgreSQLHeroku.COL_AUTHOR + ", "
  			+ postgreSQLHeroku.COL_PUBLISHER + ", "
  			+ postgreSQLHeroku.COL_MEDIA_TYPE + "\n";
	
	private ObservableList<BorrowedBooksTableLine> lo =  FXCollections.observableArrayList();
	TableView<BorrowedBooksTableLine> table = new TableView<>();
	
	public BorrowedLibTable(Stage stage, Scene scene) {
		this.stage = stage;
		this.scene = scene;
	}
	
	
	
	public Scene showMenu(ResultSet rs, Student stud)  {

    	VBox vbox = new VBox();
    	HBox hbox = new HBox();
    	hbox.setPadding(new Insets(PADDING,PADDING,PADDING,PADDING));
    	hbox.setSpacing(PADDING);
    	
      	//Label heading = new Label("Here are your results.\n");
      	

      	Button backBtn = new Button("Back");
      	Button saveFileBtn = new Button("Save to File");
      	//
      	hbox.getChildren().addAll(backBtn, saveFileBtn);
      	backBtn.setMaxWidth(WIDTH);
      	

      	
      	//try and create the table
      	try
      	{
      		while (rs.next())
          	{
      			
          		lo.add(new BorrowedBooksTableLine(rs.getInt(postgreSQLHeroku.COL_STUD_NO),
          				rs.getString(postgreSQLHeroku.COL_STUD_FNAME),
          				rs.getString(postgreSQLHeroku.COL_STUD_LNAME),
          				rs.getInt(postgreSQLHeroku.COL_ID),
          				rs.getString(postgreSQLHeroku.COL_TITLE),
          				rs.getString(postgreSQLHeroku.COL_AUTHOR),
          				rs.getString(postgreSQLHeroku.COL_PUBLISHER),
          				rs.getString(postgreSQLHeroku.COL_MEDIA_TYPE)));
          		
          		lines += rs.getString(postgreSQLHeroku.COL_STUD_NO) + ", "
          				+ rs.getString(postgreSQLHeroku.COL_STUD_FNAME) + ", "
          				+ rs.getString(postgreSQLHeroku.COL_STUD_LNAME) + ", "
          				+ rs.getString(postgreSQLHeroku.COL_ID) + ", "
          				+ rs.getString(postgreSQLHeroku.COL_TITLE) + ", "
          				+ rs.getString(postgreSQLHeroku.COL_AUTHOR) + ", "
          				+ rs.getString(postgreSQLHeroku.COL_PUBLISHER) + ", "
          				+ rs.getString(postgreSQLHeroku.COL_MEDIA_TYPE) + "\n";
      				
          	}
      	}
      	catch (SQLException e)
      	{
      		e.printStackTrace();
      	}
      	
      	//Column Student No
      	TableColumn<BorrowedBooksTableLine,String> studnoCol = new TableColumn<>("Student No");
      	studnoCol.setMinWidth(120);
      	studnoCol.setCellValueFactory(new PropertyValueFactory<>("studentno"));
      	
      	//Column First Name
      	TableColumn<BorrowedBooksTableLine,String> fnameCol = new TableColumn<>("First Name");
      	fnameCol.setMinWidth(120);
      	fnameCol.setCellValueFactory(new PropertyValueFactory<>("fname"));
      	
      	//Column Last Name
      	TableColumn<BorrowedBooksTableLine,String> lnameCol = new TableColumn<>("Last Name");
      	lnameCol.setMinWidth(120);
      	lnameCol.setCellValueFactory(new PropertyValueFactory<>("lname"));
      	
      	//Column Title
      	TableColumn<BorrowedBooksTableLine,String> titleCol = new TableColumn<>("Title");
      	titleCol.setMinWidth(200);
      	titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
      	
      	//Column Author
      	TableColumn<BorrowedBooksTableLine,String> authCol = new TableColumn<>("Author");
      	authCol.setMinWidth(150);
      	authCol.setCellValueFactory(new PropertyValueFactory<>("author"));
      	
      	//Column Publisher
      	TableColumn<BorrowedBooksTableLine,String> pubCol = new TableColumn<>("Publisher");
      	pubCol.setMinWidth(150);
      	pubCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
      	
      	//Column MediaType
      	TableColumn<BorrowedBooksTableLine,String> typeCol = new TableColumn<>("Type");
      	typeCol.setMinWidth(100);
      	typeCol.setCellValueFactory(new PropertyValueFactory<>("media_type"));
      	
      	//Column ID
      	TableColumn<BorrowedBooksTableLine,String> idCol = new TableColumn<>("BookID");
      	idCol.setMinWidth(80);
      	idCol.setCellValueFactory(new PropertyValueFactory<>("libid"));
      	
      	
      	table.setItems(lo);
      	table.getColumns().addAll(idCol, titleCol, authCol, pubCol, typeCol);
      	//table.getColumns().addAll(studnoCol, lnameCol, fnameCol, idCol, titleCol, authCol, pubCol, typeCol);
      	
      	
		

      	
      	backBtn.setOnAction(e-> {
			
      		try (StudentMenu studentMenu = new StudentMenu(stage, scene)) 
			{

				stage.setScene(studentMenu.showMenu(stud));
				stage.setTitle("Student Home Page");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}); 
      	
      	saveFileBtn.setOnAction(e-> {

      		//create the file
      		try {
      	      File myObj = new File("Borrowed Items.csv");
      	      if (myObj.createNewFile()) {
      	        //System.out.println("File created: " + myObj.getName());
      	        AlertBox.display("File Created", "File created: " + myObj.getName());
      	      } else {
      	        //System.out.println("File already exists.");
      	    	AlertBox.display("Error!", "File already exists!");
      	      }
      	    } catch (IOException e2) {
      	      //System.out.println("An error occurred.");
      	      AlertBox.display("Error!", "An error occurred!");
      	      e2.printStackTrace();
      	    }
      		
      		try {
      	      FileWriter myWriter = new FileWriter("Borrowed Items.csv");
      	      myWriter.write("");
      	      myWriter.write(lines);
      	      myWriter.close();
      	      //System.out.println("Successfully wrote to the file.");
      	      AlertBox.display("Success!", "Successfully saved to file!");
      	    } catch (IOException e2) {
      	      //System.out.println("An error occurred.");
      	      AlertBox.display("Error!", "Could not save to the file!");
      	      e2.printStackTrace();
      	    }
      		

      	});
      	

      	
      	vbox.getChildren().addAll(table,hbox);
      	
      	//Scene scene = new Scene(vbox, 350, 450);
      	Scene scene = new Scene(vbox);
	    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    return scene;
	}
	

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}





}
