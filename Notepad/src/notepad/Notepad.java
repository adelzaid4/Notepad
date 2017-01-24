/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.stage.Stage;
import javax.swing.Action;
import javax.swing.plaf.OptionPaneUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import javax.swing.undo.UndoManager;

/**
 *
 * @author Adel
 */
public class Notepad extends Application {

    TextArea ta = new TextArea();
    File file = null;
    FileChooser fc = new FileChooser();
    UndoManager mgr = new UndoManager();
    static String contentTA = "";

    @Override
    public void start(Stage primaryStage) {
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT (*.txt)", "*.txt"));
        primaryStage.getIcons().add(new Image(Notepad.class.getResourceAsStream("icon.png")));
        MenuBar bar = new MenuBar();
        //Defining Menues and its items
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");

        MenuItem fileItem1 = new MenuItem("New");
        MenuItem fileItem2 = new MenuItem("Save");
        MenuItem fileItem3 = new MenuItem("Open");
        MenuItem fileItem4 = new MenuItem("Exit");

        MenuItem editItem1 = new MenuItem("Undo");
        MenuItem editItem2 = new MenuItem("Cut");
        MenuItem editItem3 = new MenuItem("Copy");
        MenuItem editItem4 = new MenuItem("Paste");
        MenuItem editItem5 = new MenuItem("Delete");
        MenuItem editItem6 = new MenuItem("Select All");

        MenuItem helpItem1 = new MenuItem("About Notepad");
        //Adding Events to items
        fileItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (ta.getText().length() > 0) {
                    if (!contentTA.equals(ta.getText())) {
                        Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to save?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.YES) {
                            saveFile(primaryStage);
                            ta.setText("");
                            primaryStage.setTitle("NotePad");
                            file=null;
                        } else if (alert.getResult() == ButtonType.NO) {
                            ta.setText("");
                            file=null;
                        }
                    } else {
                        ta.setText("");
                        primaryStage.setTitle("NotePad");
                        file=null;
                    }
                }
            }
        });
        fileItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveFile(primaryStage);
                
            }
        });
        fileItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ta.getText().length() > 0) {
                    if (!contentTA.equals(ta.getText())) {
                        Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to save?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.YES) {
                            saveFile(primaryStage);
                            openFile(primaryStage);
                        } else if (alert.getResult() == ButtonType.NO) {
                            openFile(primaryStage);
                        }
                    } else {
                        openFile(primaryStage);
                    }
                } else {
                    openFile(primaryStage);
                }

            }
        });
        fileItem4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleExit(primaryStage);
            }
        });

        editItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.undo();
            }
        });
        editItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.cut();
            }
        });
        editItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.copy();
            }
        });
        editItem4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.paste();
            }
        });
        editItem5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!(ta.getSelectedText()).equals("")) {
                    ta.setText(ta.getText().replace(ta.getSelectedText(), ""));
                } else {
                    ta.deleteNextChar();
                }
            }
        });
        editItem6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.selectAll();
            }
        });
        helpItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(AlertType.INFORMATION, "By/Adel Zaid");
                alert.initStyle(StageStyle.UTILITY);
                alert.setHeaderText(null);
                alert.setTitle("About Notepad");
                alert.setContentText("Verion: 1.0.0 \nDate: 5-Jan-2017 \nDeveloped by: Adel Zaid");
                alert.showAndWait();
            }
        });
        fileMenu.getItems().addAll(fileItem1, fileItem2, fileItem3, new SeparatorMenuItem(), fileItem4);
        editMenu.getItems().addAll(editItem1, new SeparatorMenuItem(), editItem2, editItem3, editItem4, editItem5, new SeparatorMenuItem(), editItem6);
        helpMenu.getItems().addAll(helpItem1);

        bar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        BorderPane pane = new BorderPane();
        pane.setTop(bar);
        pane.setCenter(ta);
        Scene scene = new Scene(pane, 800, 500);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                handleExit(primaryStage);
            }
        });
        primaryStage.setTitle("Notepad");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openFile(Stage primaryStage) {

        if ((file = fc.showOpenDialog(primaryStage)) != null) {
            primaryStage.setTitle(file.getName() + " - Notepad");
            BufferedReader bufferedReader = null;
            try {
                ta.setText("");
                String currentLine;
                bufferedReader = new BufferedReader(new FileReader(file));
                while ((currentLine = bufferedReader.readLine()) != null) {
                    ta.appendText(currentLine + "\n");
                }
                contentTA = ta.getText();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(Stage primaryStage) {
        String content = ta.getText();

        if (file != null) {

            try {
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                }
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(content);
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if ((file = fc.showSaveDialog(primaryStage)) != null) {
                primaryStage.setTitle(file.getName() + " - Notepad");
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    contentTA = ta.getText();
                    FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(content);
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleExit(Stage primaryStage) {
        if (ta.getText().length() > 0) {
            if (!contentTA.equals(ta.getText())) {
                Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to save?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    saveFile(primaryStage);
                    Platform.exit();
                } else if (alert.getResult() == ButtonType.NO) {
                    Platform.exit();

                }
            }
        } else {
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
