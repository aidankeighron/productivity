import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class gui extends Application {
	
	ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();
	static String currentDir = System.getProperty("user.dir");
	static File checkListFile = new File(currentDir + "\\list.TXT");
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 450, 250);
		VBox vbCenter = new VBox();
		TextField input = new TextField();
		input.setOnKeyPressed( event -> {
			if( event.getCode() == KeyCode.ENTER ) {
				addCheckBox(input.getText(), vbCenter);
				input.setText("");
				saveCheckBoxes();
			}
		});
		vbCenter.getChildren().add(input);
		
		HBox hbButtons = new HBox();
		Button reset = new Button("Reset");
		reset.setOnAction( event  -> {
			removeCheckBoxes(vbCenter);
		});
		hbButtons.getChildren().add(reset);
		hbButtons.setAlignment(Pos.CENTER_LEFT);
		
		loadCheckBoxes(vbCenter);
		
		root.setPadding(new Insets(20));
		root.setCenter(vbCenter);
		root.setBottom(hbButtons);
		primaryStage.setTitle("Checklist");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void addCheckBox(String name, VBox vbCenter) {
		CheckBox checkBox = new CheckBox(name);
		checkBoxes.add(checkBox);
		vbCenter.getChildren().add(checkBox);
	}
	
	public void removeCheckBoxes(VBox vbCenter) {
		for (CheckBox checkBox : checkBoxes) {
			vbCenter.getChildren().remove(checkBox);			
		}
		checkBoxes = new ArrayList<CheckBox>();
		try {
			clearTheFile(checkListFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void clearTheFile(File file) throws IOException {
        FileWriter fwOb = new FileWriter(file); 
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
	
	public void saveCheckBoxes() {
		String[] name = new String[checkBoxes.size()];
		for (int i = 0; i < checkBoxes.size(); i++) {
			name[i] = checkBoxes.get(i).getText();
		}
		writeData(name, checkListFile);
	}
	
	public void loadCheckBoxes(VBox vb) {
		String[] data = readData(checkListFile);
		for (String s : data) {
			CheckBox checkBox = new CheckBox(s);
			checkBoxes.add(checkBox);
			vb.getChildren().add(checkBox);
		}
		
	}
	
	public String[] readData(File file) {
		String[] result = new String[0];
		try {
			result = new String[(int)Files.lines(file.toPath()).count()];
			Scanner scanner = new Scanner(file);
			int index = 0;
			while (scanner.hasNextLine()) {
				result[index] = scanner.nextLine();
				index++;
			}
			scanner.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(String[] dataArr, File file) {
		String data = "";
		for (int i = 0; i < dataArr.length; i++) {
			data += (dataArr[i] + "\n");
		}
		writeData(data, file);
	}
}