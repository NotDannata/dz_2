package ru.guu.dz_2;

import javafx.application.Application;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.guu.dz_2.Board.Cell;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javafx.scene.media.Media;


public class BattleShips extends Application {
    private MediaPlayer winSound;
    java.time.LocalDate currentDate = java.time.LocalDate.now();
    private boolean ships_placed = false;
    private Board enemyBoard, playerBoard;
    private int shipsToPlace = 5;
    private boolean enemyTurn = false;
    private Random random = new Random();
    BorderPane root = new BorderPane();
    private Parent createContent() {

        root.setPrefSize(600, 800);

        Text welcomeText = new Text("Добро пожаловать в морской бой!");
        welcomeText.setFont(Font.font("Roboto", 40));
        welcomeText.setFill(Color.BLACK);
        root.setTop(welcomeText);
        BorderPane.setAlignment(welcomeText, Pos.CENTER);


        enemyBoard = new Board(true, event -> {
            if (!ships_placed)
                return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot)
                return;

            enemyTurn = !cell.shoot();

            if (enemyBoard.ships == 0) {
                Text win = new Text("Победа!");
                win.setFont(Font.font("Roboto", 40));
                win.setFill(Color.BLACK);
                root.setTop(win);
                BorderPane.setAlignment(win, Pos.CENTER);
                System.out.println("YOU WIN");
                playWinSound();
                writeResultsToFile("Игра окончена. Победа, " + currentDate);

            }

            if (enemyTurn)
                enemyMove();
        });

        playerBoard = new Board(false, event -> {
            if (ships_placed)
                return;

            Cell cell = (Cell) event.getSource();
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });

        VBox vbox = new VBox(50, enemyBoard, playerBoard);
        vbox.setAlignment(Pos.CENTER);

        root.setCenter(vbox);

        return root;
    }

    public void enemyMove() {
        while (enemyTurn) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);


            Cell cell = playerBoard.getCell(x, y);

            enemyTurn = cell.shoot();
            if (cell.wasShot) {
                continue;
            }


            if (playerBoard.ships == 0) {
                System.out.println("YOU LOSE");
                Text lose = new Text("Проигрыш :(");
                lose.setFont(Font.font("Roboto", 40));
                lose.setFill(Color.BLACK);
                root.setTop(lose);
                BorderPane.setAlignment(lose, Pos.CENTER);
                enemyTurn = false;
                writeResultsToFile("Игра окончена. Проигрыш"+ currentDate);

            }
        }
    }

    private void startGame() {
        int type = 5;

        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
                type--;
            }
        }

        ships_placed = true;
    }
    private void playWinSound() {
        try {
            Media media = new Media(getClass().getResource("/W.mp3").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            if (winSound != null && winSound.getStatus() == MediaPlayer.Status.PLAYING) {
                winSound.stop();
            }

            winSound = mediaPlayer;
            winSound.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class InitialHit {
        int x, y;
        int shipsOnBoard;


        InitialHit(int x, int y, int shipsOnBoard) {
            this.x = x;
            this.y = y;
            this.shipsOnBoard = shipsOnBoard;
        }
    }

    private void writeResultsToFile(String result) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("results.txt", true))) {
            writer.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
