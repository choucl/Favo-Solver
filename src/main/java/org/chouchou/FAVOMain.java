package org.chouchou;

import java.net.URL;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;


public class FAVOMain extends Application{

    public static Block[][] board = new Block[7][7];
    public static Brick[] brickBuffer = new Brick[3];
    public static Scanner sysin = new Scanner(System.in);

    public void start(Stage primaryStage){
        try{
            URL fxmlURL = Paths.get("D:\\Work Space\\FAVOSolver_Maven\\FAVOSolver\\favo_main.fxml").toUri().toURL();
            Parent root = FXMLLoader.load(fxmlURL);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FAVO! Solver");
            primaryStage.show();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }  
    }

    public static void main(String args[]){

        for(int i = 0; i < 7; i++)
            for(int j = 0; j < 7; j++)
                board[i][j] = new Block(true, 'N', 0);
            

        //set the redundant blocks to invalid
        for(int i = 0; i < 7; i++)
            
            if(i > 3){
                for(int j = 0; j < i - 3; j++)
                    board[i][j].setValidity(false);       
            }else{
                for(int j = i + 4; j < 7; j++)
                    board[i][j].setValidity(false);
            }
        for(int i = 0; i < 3; i++) brickBuffer[i] = new Brick();

        launch(args);
    }
}