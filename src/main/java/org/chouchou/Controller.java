package org.chouchou;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller extends FAVOMain {

    @FXML
    private AnchorPane ap;

    @FXML
    private MenuItem importMenu;

    @FXML
    private MenuItem saveMenu;

    @FXML
    private MenuItem closeMenu;

    @FXML
    private MenuItem rewindMenu;

    @FXML
    private MenuItem about;

    @FXML
    private MenuItem readme;

    @FXML
    private ArrayList<Label> boardArray;

    @FXML
    private ArrayList<Label> brick1Array;

    @FXML
    private ArrayList<Label> brick2Array;

    @FXML
    private ArrayList<Label> brick3Array;

    @FXML
    private Button takeStepBTN;

    @FXML
    private Button customBTN;

    @FXML
    private Button rewindBTN;

    @FXML
    private TextField codeField;

    @FXML
    private Button commitBTN;

    @FXML
    private Button saveBTN;

    @FXML
    private Label stepLayout;

    @FXML
    private Label maxLayout;

    @FXML
    private ArrayList<GridPane> brickPaneArray;

    private int stepCount = 0;
    private static boolean brickCondition[] = new boolean[3];
    private Status maxStatus;
    private int maxSelection;
    private Solver solver = new Solver(board, brickBuffer);

    @FXML
    void close(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void commit(ActionEvent event) {
        commitAction();
    }

    private void commitAction(){

        String brickCode = codeField.getText();
        codeField.clear();

        int count = 0;
        for (int i = 0; i < 3; i++) {
            if (!brickCondition[i]) {
                brickBuffer[i] = new Brick();
                brickBuffer[i].setType(brickCode);
                brickSetup(i, brickBuffer[i].getType());
                brickCondition[i] = true;
                brickPaneArray.get(i).setBackground(
                        new Background(new BackgroundFill(Color.web("#d4d2d2"), CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            }
        }
        for (int i = 0; i < 3; i++)
            if (brickCondition[i])
                count++;

        if (count == 3) {
            solve();
        } else {
            for (int i = 0; i < 3; i++)
                if (!brickCondition[i]) {
                    codeField.setPromptText("Please Enter brick " + (i + 1));
                    break;
                }
        }

    }

    private void brickSetup(int i, String brickCode) {

        ArrayList<Label> brickArray;
        switch (i) {
            case 0:
                brickArray = brick1Array;
                break;
            case 1:
                brickArray = brick2Array;
                break;
            case 2:
                brickArray = brick3Array;
                break;
            default:
                brickArray = brick1Array;
        }

        for (int j = 0; j < 7; j++) {
            if (brickCode.charAt(j) != '0') {
                if (brickBuffer[i].getIsMerge())
                    brickArray.get(j).setText(" m ");
                else
                    brickArray.get(j).setText(" " + brickCode.charAt(j) + " ");
                colorUp(brickArray.get(j), brickCode.charAt(j));
            } else
                brickArray.get(j).setText("");
        }

    }

    private void colorUp(Label label, char color) {

        label.setTextFill(Color.web("#ffffff"));
        switch (color) {
            case 'R':
                label.setStyle("-fx-background-color: RED");
                break;
            case 'G':
                label.setStyle("-fx-background-color: GREEN");
                break;
            case 'B':
                label.setStyle("-fx-background-color: BLUE");
                break;

        }

    }

    private void solve() {

        codeField.setDisable(true);
        customBTN.setDisable(false);
        takeStepBTN.setDisable(false);
        maxStatus = solver.solve();
        for (int i = 0; i < 3; i++) {
            if (maxStatus.getOriginal().equals(brickBuffer[i].getType())) {
                maxSelection = i;
                brickSetup(i, maxStatus.getBrick());
                brickPaneArray.get(i).setBackground(
                        new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            }
        }

        maxLayout.setText("Three Step Evaluation: " + maxStatus.getEvaluation());
        stepLayout.setText("Step: " + stepCount);
        int count = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j].getValidity()) {
                    if (maxStatus.getColumn() == i && maxStatus.getRow() == j) {
                        boardArray.get(count).setStyle("-fx-background-color: DARKGRAY");
                        boardArray.get(count).setText(" E ");
                    }
                    count++;
                }
            }
        }

    }

    @FXML
    void customSetup(ActionEvent event) {
        stepCount++;
        codeField.setDisable(false);
        customBTN.setDisable(true);
        takeStepBTN.setDisable(true);

        final ChoiceDialog<Integer> brickDialog = new ChoiceDialog<Integer>(1, 2, 3);
        brickDialog.setTitle("Brick Choice");
        brickDialog.setHeaderText("");
        brickDialog.setContentText("Please select the brick you want to put: ");
        Optional<Integer> opt = brickDialog.showAndWait();

        int brickChoice;
        try {
            brickChoice = opt.get() - 1;
        } catch (final NoSuchElementException ex) {
            brickChoice = 0;
        }

        ArrayList<String> rotateBuffer = new ArrayList<String>();
        for (int i = 0; i < brickBuffer[brickChoice].getBlockCost(); i++) {
            rotateBuffer.add(brickBuffer[brickChoice].getRotate(i));
        }
        final ChoiceDialog<String> rotateDialog = new ChoiceDialog<String>(rotateBuffer.get(0), rotateBuffer);
        for (int i = 0; i < brickBuffer[brickChoice].getBlockCost(); i++) {
            rotateDialog.setSelectedItem(brickBuffer[brickChoice].getRotate(i));
        }
        rotateDialog.setTitle("Rotation Choice");
        rotateDialog.setHeaderText("");
        rotateDialog.setContentText("Please select the rotation type of the brick: ");
        Optional<String> str = rotateDialog.showAndWait();

        String rotateChoice;
        try {
            rotateChoice = str.get();
        } catch (final NoSuchElementException ex) {
            rotateChoice = "";
        }

        final ChoiceDialog<Integer> columnDialog = new ChoiceDialog<Integer>(0, 1, 2, 3, 4, 5, 6);
        columnDialog.setTitle("Column Choice");
        columnDialog.setHeaderText("");
        columnDialog.setContentText("Please select the COLUMN you want to put: ");
        opt = columnDialog.showAndWait();

        int columnChoice;
        try {
            columnChoice = opt.get();
        } catch (final NoSuchElementException ex) {
            columnChoice = 0;
        }

        final ChoiceDialog<Integer> rowDialog = new ChoiceDialog<Integer>(0, 1, 2, 3, 4, 5, 6);
        rowDialog.setTitle("Row Choice");
        rowDialog.setHeaderText("");
        rowDialog.setContentText("Please select the ROW you want to put: ");
        opt = rowDialog.showAndWait();

        int rowChoice;
        try {
            rowChoice = opt.get();
        } catch (final NoSuchElementException ex) {
            rowChoice = 0;
        }

        Status status = new Status(columnChoice, rowChoice, brickBuffer[brickChoice].getType(), rotateChoice,
                brickBuffer[brickChoice].getIsMerge());
        solver.placeBrick(brickBuffer[brickChoice], status, board, 0);
        brickCondition[brickChoice] = false;
        boardChange();
        brickPaneArray.get(maxSelection).setBackground(
                new Background(new BackgroundFill(Color.web("#d4d2d2"), CornerRadii.EMPTY, Insets.EMPTY)));
        brickPaneArray.get(brickChoice)
                .setBackground(new Background(new BackgroundFill(Color.BROWN, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    void importFile(ActionEvent event) {
        Path p = Paths.get("data/saved");
        if(!Files.exists(p)){
            try{
                Files.createDirectories(p);
            }catch(IOException e){
                System.out.println("ERROR: Directory Creation Failed.");
            }
        }
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the board data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.setInitialDirectory(new File("data/saved"));
        File file = fileChooser.showOpenDialog(stage);
        // Read in files. Inclusive of 'last.txt' and 'lastmove.txt'
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j].changeFeature('N', 0);
            }
        }
        try {
            Scanner reader = new Scanner(file);
            int count = 0;
            while (reader.hasNextLine()) {
                String str = reader.nextLine();
                if (str.charAt(0) == '(') {
                    int column = str.charAt(1) - '0';
                    int row = str.charAt(3) - '0';
                    char type = str.charAt(6);
                    int value = Integer.parseInt(str.substring(8, str.indexOf(']')));

                    board[column][row].setBlockType(type);
                    board[column][row].setValue(value);
                } else {

                    brickBuffer[count].setType(str.substring(0, 7));
                    if (str.length() == 8)
                        brickBuffer[count].setMerge(true);
                    brickSetup(count, brickBuffer[count].getType());
                    brickCondition[count] = true;

                    count++;
                }
            }
            reader.close();
            boardChange();
            solve();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: FILE NOT FOUND.");
        }
    }

    @FXML
    void rewind(ActionEvent event) {
        // TODO: NO IDEA!!!
    }

    @FXML
    void save(ActionEvent event) {
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("data/saved"));
        chooser.setTitle("Save Board");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));

        try {
            PrintWriter writer = new PrintWriter(chooser.showSaveDialog(stage).getAbsolutePath());
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    if (board[i][j].getValue() != 0)
                        writer.printf("(%d,%d)[%c,%d]\n", i, j, board[i][j].getBlockType(), board[i][j].getValue());
                }
            }
            for (int i = 0; i < 3; i++) {
                writer.print(brickBuffer[i].getType());
                if (brickBuffer[i].getIsMerge())
                    writer.print("m");
                writer.println();
            }
            writer.flush();
            writer.close();
        } catch (NullPointerException ex) {
            System.out.println("ERROR: NULL POINTER");
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: FILE NOT FOUND.");
        }

    }

    @FXML
    void showAbout(ActionEvent event) {

    }

    @FXML
    void showReadme(ActionEvent event) {

    }

    @FXML
    void takeStep(ActionEvent event) {
        stepCount++;
        codeField.setDisable(false);
        customBTN.setDisable(true);
        takeStepBTN.setDisable(true);
        for (int i = 0; i < 3; i++) {
            if (brickBuffer[i].getType().equals(maxStatus.getOriginal())) {
                solver.placeBrick(brickBuffer[i], maxStatus, board, 0);
                brickCondition[i] = false;
                break;
            }
        }
        boardChange();
        brickPaneArray.get(maxSelection).setBackground(
                new Background(new BackgroundFill(Color.web("#d4d2d2"), CornerRadii.EMPTY, Insets.EMPTY)));
        brickPaneArray.get(maxSelection)
                .setBackground(new Background(new BackgroundFill(Color.BROWN, CornerRadii.EMPTY, Insets.EMPTY)));

    }

    void boardChange() {
        int count = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j].getValidity()) {
                    boardArray.get(count).setText(" " + board[i][j].getValue() + " ");
                    boardArray.get(count).setTextFill(Color.LIGHTGRAY);
                    switch (board[i][j].getBlockType()) {
                        case 'R':
                            boardArray.get(count).setStyle("-fx-background-color: RED");
                            break;
                        case 'G':
                            boardArray.get(count).setStyle("-fx-background-color: GREEN");
                            break;
                        case 'B':
                            boardArray.get(count).setStyle("-fx-background-color: BLUE");
                            break;
                        case 'N':
                            boardArray.get(count).setStyle("-fx-background-color: WHITE");
                            boardArray.get(count).setText(i + "," + j);
                            boardArray.get(count).setTextFill(Color.LIGHTGRAY);
                            break;
                    }
                    count++;
                }
            }
        }

    }

    public void initialize() {
        boardChange();
        // add listener to the block labels
        boardArray.forEach(this::colorChange);
        codeField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
              commitAction();
            }
         });
    }

    private void colorChange(Label callLabel){

        callLabel.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY){
                //left mouse change color
                int count = 0;
                for(int i = 0; i < 7; i++){
                    for(int j = 0; j < 7; j++){
                        if(board[i][j].getValidity()){
                            if(boardArray.get(count).equals(callLabel)){
                                switch(board[i][j].getBlockType()){
                                    case 'N':
                                    callLabel.setStyle("-fx-background-color: RED");
                                    callLabel.setText(" 1 ");
                                    board[i][j].setBlockType('R');
                                    board[i][j].setValue(1);
                                    break;
                                    case 'R':
                                    callLabel.setStyle("-fx-background-color: GREEN");
                                    board[i][j].setBlockType('G');
                                    break;
                                    case 'G':
                                    callLabel.setStyle("-fx-background-color: BLUE");
                                    board[i][j].setBlockType('B');
                                    break;
                                    case 'B':
                                    callLabel.setStyle("-fx-background-color: WHITE");
                                    callLabel.setText(i + "," + j);
                                    board[i][j].setBlockType('N');
                                    board[i][j].setValue(0);
                                    break;
                                }
                            }
                            count++;
                        }
                    }
                }
            }else if(event.getButton() == MouseButton.SECONDARY){
                //right mouse to set up the value
                int count = 0;
                for(int i = 0; i < 7; i++){
                    for(int j = 0; j < 7; j++){
                        if(board[i][j].getValidity()){
                            if(boardArray.get(count).equals(callLabel) && board[i][j].getBlockType() != 'N'){
                                final TextInputDialog textInputDialog = new TextInputDialog("1"); 
                                textInputDialog.setTitle("Set Up Block Value");
                                textInputDialog.setHeaderText("Please set up the block value"); 
                                textInputDialog.setContentText("Input a positive integer :");
                                final Optional<String> opt = textInputDialog.showAndWait(); 
                                String rtn = "0";
                                try{
                                    rtn = opt.get(); 
                                }catch(final NoSuchElementException ex){
                                    System.out.println("No input");
                                }
                            
                                try{
                                    if(Integer.parseInt(rtn) < 0) throw new NumberFormatException();
                                    board[i][j].setValue(Integer.parseInt(rtn));
                                    boardArray.get(count).setText(" " + rtn + " ");
                                }catch(NumberFormatException e){
                                    System.out.println("Wrong number format, please set up one more time.");
                                }
                            }
                            count++;
                        }
                    }
                }

                
            }
        });
        
    }
}
