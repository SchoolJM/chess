package ija.project.chess.controller;

import ija.project.chess.board.Board;
import ija.project.chess.exceptions.ChessNotationMapperException;
import ija.project.chess.exceptions.WrongChessNotationException;
import ija.project.chess.exceptions.WrongChessNotationFileException;
import ija.project.chess.factory.GameFactory;
import ija.project.chess.figure.Figure;
import ija.project.chess.game.Game;
import ija.project.chess.notation.ChessTurnNotation;
import ija.project.chess.parser.ChessNotationMapper;
import ija.project.chess.reader.ChessNotationReader;
import ija.project.chess.turn.Turn;
import ija.project.chess.writer.ChessNotationWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TabPane tabPane;

    private List<Game> gameList = new ArrayList<>();

    @FXML
    /**
    *   Vytvori novu hru
    */
    public void newGame(ActionEvent event) {

        Board board = new Board(8);
        Game game = GameFactory.createChessGame(board);
        board.setGame(game);

        gameList.add(game);

        Tab tab = new Tab();
        tab.setText("Game #" + (tabPane.getTabs().size() + 1));
        tab.setContent(game.getContent());

        tab.setOnCloseRequest(e -> {
            int gameid = tabPane.getSelectionModel().getSelectedIndex();
            gameList.remove(gameid);
        });

        tabPane.getTabs().add(tab);
    }

    @FXML
    /**
    *   Otvori hru
    */
    public void openGame(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Chess Notation File", "*.chess");

        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.getExtensionFilters().add(filter);

        File notationFile = fileChooser.showOpenDialog(new Stage());

        if(notationFile == null) return;

        Board board = new Board(8);
        Game game = GameFactory.createChessGame(board);
        board.setGame(game);

        gameList.add(game);

        ChessNotationReader reader = null;
        ChessNotationMapper mapper = new ChessNotationMapper(game);
        ChessTurnNotation turnNotation;
        List<Turn> history = new ArrayList<>();
        try {
            reader = new ChessNotationReader(notationFile);
            while((turnNotation = reader.getTurnNotation()) != null) {
                Turn turn = mapper.getTurn(turnNotation);
                history.add(turn);
            }
            game.setHistory(history);
            mapper.recoverBoard();

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Figure foundedFigure = board.getField(i, j).get();
                    if(foundedFigure != null && foundedFigure.getField() == null) System.out.println(foundedFigure.getFigureChar() + " is null");
                }
            }

            Tab tab = new Tab();
            tab.setText("Game #" + (tabPane.getTabs().size() + 1));
            tab.setContent(game.getContent());

            tab.setOnCloseRequest(e -> {
                int gameid = tabPane.getSelectionModel().getSelectedIndex();
                gameList.remove(gameid);
            });

            tabPane.getTabs().add(tab);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(ChessNotationMapperException | WrongChessNotationFileException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wrong file content");
            alert.show();
        }
    }

    @FXML
    /**
    *   Ulozi hru
    */
    public void saveGame(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Chess notation", "*.chess");
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showSaveDialog(new Stage());
        ChessNotationWriter writer = null;
        try {
            Game game = gameList.get(tabPane.getSelectionModel().getSelectedIndex());
            ChessNotationMapper mapper = new ChessNotationMapper();
            writer = new ChessNotationWriter(file);

            for(Turn turn :game.getHistory()) {
                writer.appendNotation(mapper.getNotation(turn));
            }
        } catch (IOException | WrongChessNotationException e) {
            e.printStackTrace();
        } finally {
            if(writer != null) {
                writer.close();
            }
        }

    }

    @FXML
    /**
    *   Ukonci hru
    */
    public void quitGame(ActionEvent event) {
        System.exit(0);
    }

    public void initialize(URL location, ResourceBundle resources) {

    }
}
