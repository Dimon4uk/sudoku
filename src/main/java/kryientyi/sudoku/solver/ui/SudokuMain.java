package kryientyi.sudoku.solver.ui;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import kryientyi.sudoku.solver.core.Solver;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by ִלטענטי on 27.08.2015.
 */
public class SudokuMain extends Application {
    private Stage window;
    private StackPane center;
    private static Pane board;
    private HBox hBox ;
    private VBox vBox;
    private Button next;
    private Button solveButton;
    private Button clear;
    private GridPane keypad;
    private Button numButton;
    private Cell cell;
    private Solver solver;
    private static String tmp = "";
    private static int cellSize = 45;
    private static int sudokuSize = 9;
    private static int blockSize = 3;
    private static HashSet<String> checkList;
    private static ArrayList<String> cellValues;
    private static ArrayList<String> cellsBefore;

    private int nextClicks = 0;

    public static ArrayList<String> getCellValues() {
        for (int i = 0; i < sudokuSize*sudokuSize; i++) {
            tmp = ((Cell) board.getChildren().get(i)).getCellText().getText();
            if(tmp.isEmpty())
                tmp = "0";
            cellValues.set(i,tmp);
        }
        return cellValues;
    }


    public static void setCellValues(ArrayList<String> cellValues) {
        for (int i = 0; i < sudokuSize*sudokuSize; i++) {
            ((Cell) board.getChildren().get(i)).getCellText().setText(cellValues.get(i));
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        hBox = new HBox();
        center = new StackPane();
        board = new Pane();
        vBox = new VBox();
        cellValues = new ArrayList<String>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Sudoku Solver");
        window.setScene(new Scene(createContent(), 580,420));
        window.setResizable(false);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Parent createContent() {
        center.getChildren().add(createBoard());
        configureBorder(center);
        vBox.setAlignment(Pos.BASELINE_CENTER);
        vBox.setSpacing(20);
        vBox.getChildren().addAll(new Label("Default mode"),
                new Separator(Orientation.HORIZONTAL),creteSolveButton(),
                new Separator(Orientation.HORIZONTAL),createClearButton(),
                new Separator(Orientation.HORIZONTAL),createNextButton(),
                new Separator(Orientation.HORIZONTAL));
        configureBorder(vBox);
        hBox.setSpacing(10);
        hBox.getChildren().addAll(new Separator(Orientation.VERTICAL),center,
                new Separator(Orientation.VERTICAL),vBox);
        return hBox;
    }

    private Pane createBoard() {
        board.setPrefSize(sudokuSize*cellSize, sudokuSize*cellSize);
        for (int i = 0; i < sudokuSize; i++) {
            for (int j = 0; j < sudokuSize; j++) {
                cell = new Cell();
                cell.setTranslateX(j*cellSize);
                cell.setTranslateY(i*cellSize);

                final int row = i;
                final int col = j;
                cell.setOnMouseClicked(event -> {
                    if (nextClicks == 0)
                    showKeypad(window, row , col);
                });
                cellValues.add(cell.getCellText().getText());
                board.getChildren().add(cell);
            }
        }
        drawSudokuBlocks();
        return board;
    }

    private Button creteSolveButton() {
        solveButton = new Button("Solve");
        solveButton.setPrefSize(80, 20);
        solveButton.setOnAction(event -> {
            getSolution();
            setCellValues(cellValues);
        });
        return solveButton;
    }

    private ArrayList<String> getSolution() {
        solver = new Solver(getCellValues());
        cellValues = solver.getResults();
        return cellValues;
    }

    private Button createClearButton() {
        clear = new Button("Clear");
        clear.setPrefSize(80, 20);
        clear.setOnAction(event -> {
            clear();
            nextClicks = 0;
        });

        return clear;
    }

    private void clear() {
        for (int i = 0; i < sudokuSize * sudokuSize; i++) {
            cell = ((Cell) board.getChildren().get(i));
            cell.getCellText().setText("");
            cell.getCellText().setFont(Font.font("Times New Roman", FontWeight.EXTRA_LIGHT, FontPosture.REGULAR, 20));
            board.getChildren().set(i,cell);
        }
    }

    private Button createNextButton() {
        next = new Button("Next");
        next.setPrefSize(80, 20);
        nextClicks = 0;
        next.setOnAction(event -> {
            getResultsByStepMode(nextClicks++);
        });
        return next;
    }

    private void getResultsByStepMode(int nextClicks) {
        if(nextClicks == 0) {
            cellsBefore = getCellValues(); // getting not-zero elements on board before solution
            cellValues = getSolution();  //get all results
            if(cellsBefore.get(nextClicks) != cellValues.get(nextClicks))
                setCellByStepMode(nextClicks,cellValues.get(nextClicks));
        } else if((nextClicks<sudokuSize*sudokuSize)&&(cellsBefore.get(nextClicks) != cellValues.get(nextClicks))) {
            setCellByStepMode(nextClicks,cellValues.get(nextClicks));
        }

    }

    private void setCellByStepMode(int cellIndex, String num) {
        cell = ((Cell) board.getChildren().get(cellIndex));
        cell.getCellText().setText(num);
        board.getChildren().set(cellIndex, cell);
    }

    private static void configureBorder(final Region region) {
        region.setStyle("-fx-border-width: 1;"
                        + "-fx-border-radius: 20;"
                        + "-fx-padding: 9;"
//                        + "-fx-background-color: blueviolet;"
                        + "-fx-border-color: darkblue;"
        );
    }

    private Pane drawSudokuBlocks() {
        for (int i = 0; i < sudokuSize; i=i+blockSize) {
            for (int j = 0; j < sudokuSize; j=j+blockSize) {
                if(((i+j)/blockSize)%2 != 0) {
                    for (int y = i; y < i+blockSize; y++) {
                        for (int x = j; x < j + blockSize; x++) {
                            cell = ((Cell) board.getChildren().get(y * sudokuSize + x));
                            cell.getElement().setFill(Color.BLANCHEDALMOND);
                            board.getChildren().set(y * sudokuSize + x,cell);
                        }
                    }
                }
            }
        }
        return board;
    }

    private void showKeypad(Window owner, int rowIndex, int columnIndex) {
        // Create a Stage with specified owner and modality
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        keypad = new GridPane();
        keypad.setVgap(5);
        keypad.setHgap(5);
        for (int i = 0; i < sudokuSize; i++) {
            final int num = i + 1;
            numButton = new Button("" + num);
            numButton.setFont(Font.font("Times New Roman",FontWeight.BOLD, FontPosture.ITALIC,16));
            numButton.setPrefSize(40, 40);
            keypad.add(numButton,i%blockSize,i/blockSize);
        }

        checkForInput(rowIndex, columnIndex);
        for (int i = 0; i < sudokuSize; i++) {
            final int num = i + 1;
            ((Button) keypad.getChildren().get(i)).setOnAction(event -> {
                setCellByUser(rowIndex * sudokuSize + columnIndex, "" + num);
                stage.close();
            });
        }

        numButton = new Button("clear");
        numButton.setPrefSize(130,40);
        numButton.setOnAction(event -> {
            setCellByUser(rowIndex * sudokuSize + columnIndex, "");
            stage.close();
        });

        VBox root = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        root.getChildren().addAll(numButton,keypad);
        Scene scene = new Scene(root, 130, 160);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Cell");
        stage.show();
    }

    private  void checkForInput(int rowIndex, int columnIndex) {
        checkList = new HashSet<String>();

        //check row and column
        for (int i = 0; i < sudokuSize; i++) {
            tmp = ((Cell) board.getChildren().get(rowIndex * sudokuSize + i)).getCellText().getText();
            if (!tmp.isEmpty())
                checkList.add(tmp);
            tmp = ((Cell) board.getChildren().get(i * sudokuSize + columnIndex)).getCellText().getText();
            if(!tmp.isEmpty())
                checkList.add(tmp);
        }

        // get row and column indexes for check block
        int i,j;
        if(rowIndex<blockSize) {                    //i=0
            if(columnIndex<blockSize) {             //j=0
                i=0;    j=0;
            } else if(columnIndex<blockSize*2) {    //j=3
                i=0;    j=3;
            } else {                                //j=6
                i=0;    j=6;
            }
        } else if(rowIndex<blockSize*2){            //i=3
            if(columnIndex<blockSize) {             //j=0
                i=3;    j=0;
            } else if(columnIndex<blockSize*2) {    //j=3
                i=3;    j=3;
            } else {                                //j=6
                i=3;    j=6;
            }
        } else {                                    //i=6
            if(columnIndex<blockSize) {             //j=0
                i=6;    j=0;
            } else if(columnIndex<blockSize*2) {    //j=3
                i=6;    j=3;
            } else {                                //j=6
                i=6;    j=6;
            }
        }
        // check block
        for (int y = i; y < i + blockSize; y++) {
            for (int x = j; x < j + blockSize; x++) {
                tmp = ((Cell) board.getChildren().get(y * sudokuSize + x)).getCellText().getText();
                if(!tmp.isEmpty())
                    checkList.add(tmp);
            }
        }
        for (int k = 0; k < sudokuSize; k++) {
            if (checkList.contains(""+(k+1))) {
                ((Button) keypad.getChildren().get(k)).setDisable(true);
            } else {
                ((Button) keypad.getChildren().get(k)).setDisable(false);
            }

        }

        checkList.clear();
    }

    private void setCellByUser(int cellIndex, String num) {
        cell = ((Cell) board.getChildren().get(cellIndex));
        cell.getCellText().setText(num);
        cell.getCellText().setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 20));
        board.getChildren().set(cellIndex, cell);
    }
}
