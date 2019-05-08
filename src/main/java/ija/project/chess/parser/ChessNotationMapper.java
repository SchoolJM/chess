package ija.project.chess.parser;

import ija.project.chess.exceptions.ChessNotationMapperException;
import ija.project.chess.figure.Figure;
import ija.project.chess.game.Game;
import ija.project.chess.notation.ChessTurnNotation;
import ija.project.chess.turn.Turn;

public class ChessNotationMapper {

    private Game game;

    public ChessNotationMapper(Game game) {
        this.game = game;
    }

    public Turn getTurn(ChessTurnNotation notation) throws ChessNotationMapperException {
        Turn turn = new Turn();

        setTurnAttributes(notation, turn, true);

        setTurnAttributes(notation, turn, false);

        return turn;
    }

    public ChessTurnNotation getNotation(Turn turn) {
        ChessTurnNotation notation = new ChessTurnNotation();
        StringBuilder whiteNotation = new StringBuilder();
        StringBuilder blackNotation = new StringBuilder();

        // Figure
        if(turn.getWhiteFigure().getFigureChar() != "p") {
            whiteNotation.append(turn.getWhiteFigure().getFigureChar());
        }
        if(turn.getBlackFigure().getFigureChar() != "p") {
            blackNotation.append(turn.getBlackFigure().getFigureChar());
        }

        // Specification of figure
        if(turn.isSpecifyColumnForWhite()) {
            whiteNotation.append((char)(turn.getWhiteSourceField().getCol() + 'a'));
        } else if(turn.isSpecifyRowForWhite()) {
            whiteNotation.append(turn.getWhiteSourceField().getRow());
        }
        if(turn.isSpecifyColumnForBlack()) {
            blackNotation.append((char)(turn.getBlackSourceField().getCol() + 'a'));
        } else if(turn.isSpecifyRowForBlack()) {
            whiteNotation.append(turn.getBlackSourceField().getRow());
        }

        // Defends
        if(turn.isBlackDefends()) {
            whiteNotation.append("x");
        }
        if(turn.isWhiteDefends()) {
            blackNotation.append("x");
        }

        // Destination
        whiteNotation.append((char)(turn.getWhiteDestinationField().getCol() + 'a'));
        whiteNotation.append(turn.getWhiteDestinationField().getRow());
        blackNotation.append((char)(turn.getBlackDestinationField().getCol() + 'a'));
        blackNotation.append(turn.getBlackDestinationField().getRow());

        // Pawn upgrade
        if(turn.getWhiteFigure().getFigureChar() == "p"
            && turn.getWhitePawnUpgradesTo() != null) {
            whiteNotation.append(turn.getWhitePawnUpgradesTo().getFigureChar());
        }
        if(turn.getBlackFigure().getFigureChar() == "p"
            && turn.getBlackPawnUpgradesTo() != null) {
            blackNotation.append(turn.getBlackPawnUpgradesTo().getFigureChar());
        }

        // Check or Check Mate
        if(turn.isBlackCheck()) {
            whiteNotation.append("+");
        } else if(turn.isBlackCheckMate()) {
            whiteNotation.append("#");
        }
        if(turn.isWhiteCheck()) {
            blackNotation.append("+");
        } else if(turn.isWhiteCheckMate()) {
            blackNotation.append("#");
        }

        return notation;
    }

    private void setTurnAttributes(ChessTurnNotation notation, Turn turn, boolean isWhite) throws ChessNotationMapperException {

        // Figure type
        turn = getFigure(turn, notation, isWhite);

        // Figure move
        turn = getDestination(turn, notation, isWhite);

        //Check Check or Check mate
        turn = checkCheckOrCheckMate(turn, notation, isWhite);
    }

    private Turn getFigure(Turn turn, ChessTurnNotation notation, boolean isWhite) throws ChessNotationMapperException {
        char c;
        char nextC;
        Figure figure = null;

        if(isWhite) {
            c = notation.getWhiteTurnNotation().charAt(0);
            nextC = notation.getWhiteTurnNotation().charAt(1);
        } else {
            c = notation.getBlackTurnNotation().charAt(0);
            nextC = notation.getBlackTurnNotation().charAt(1);
        }

        switch (c) {
            case 'K': case 'D':
                figure = game.getBoard().findFigure(Character.toString(c), null, null, isWhite);
                break;

            case 'V': case 'S': case 'J':
                figure = getNonPawnFigure(Character.toString(c), nextC, figure, isWhite);
                break;

            default:
                if(c >= 'a' && c <= 'g'){
                    int column = c - 'a';

                    if(isCharacterInRangeFrom1To8(nextC)) {
                        figure = game.getBoard().findFigure("p", column, null, isWhite);

                        if(figure == null) {
                            throw new ChessNotationMapperException();
                        }
                    } else {
                        throw new ChessNotationMapperException();
                    }
                    break;
                } else {
                    throw new ChessNotationMapperException();
                }
        }
        setFigure(turn, isWhite, figure);
        return turn;
    }

    private Turn getDestination(Turn turn, ChessTurnNotation notation, boolean isWhite) throws ChessNotationMapperException {
        //TODO: vymazat 'a' a priradit rozumne mena premennych + kontrola na dlzku??
        char c = 'a';
        char nextC = 'a';
        char nextNextC = 'a';
        if(isWhite) {
            if(turn.getWhiteFigure().getFigureChar().equals("p")) {
                c = notation.getWhiteTurnNotation().charAt(0);
                nextC = notation.getWhiteTurnNotation().charAt(1);
                if(notation.getWhiteTurnNotation().length() >= 3) {
                     nextNextC = notation.getWhiteTurnNotation().charAt(2);
                }
                if(c == 'x') {
                    turn.setBlackDefends(true);
                    c = notation.getWhiteTurnNotation().charAt(1);
                    nextC = notation.getWhiteTurnNotation().charAt(2);

                    if(notation.getWhiteTurnNotation().length() >= 4) {
                         nextNextC = notation.getWhiteTurnNotation().charAt(3);
                    }
                }
            } else {
                c = notation.getWhiteTurnNotation().charAt(1);
                nextC = notation.getWhiteTurnNotation().charAt(2);

                if(notation.getWhiteTurnNotation().length() >= 4) {
                     nextNextC = notation.getWhiteTurnNotation().charAt(3);
                }
                if(c == 'x') {
                    turn.setBlackDefends(true);
                    c = notation.getWhiteTurnNotation().charAt(2);
                    nextC = notation.getWhiteTurnNotation().charAt(3);

                    if(notation.getWhiteTurnNotation().length() >= 5) {
                         nextNextC = notation.getWhiteTurnNotation().charAt(4);
                    }
                }
            }

        } else {
            if(turn.getBlackFigure().getFigureChar().equals("p")) {
                c = notation.getBlackTurnNotation().charAt(0);
                nextC = notation.getBlackTurnNotation().charAt(1);

                if(notation.getBlackTurnNotation().length() >= 3) {
                    nextNextC = notation.getBlackTurnNotation().charAt(2);
                }
                if(c == 'x') {
                    turn.setWhiteDefends(true);
                    c = notation.getBlackTurnNotation().charAt(1);
                    nextC = notation.getBlackTurnNotation().charAt(2);

                    if(notation.getBlackTurnNotation().length() >= 4) {
                        nextNextC = notation.getBlackTurnNotation().charAt(3);
                    }
                }
            } else {
                c = notation.getBlackTurnNotation().charAt(1);
                nextC = notation.getBlackTurnNotation().charAt(2);

                if(notation.getBlackTurnNotation().length() >= 4) {
                    nextNextC = notation.getBlackTurnNotation().charAt(3);
                }
                if(c == 'x') {
                    turn.setWhiteDefends(true);
                    c = notation.getBlackTurnNotation().charAt(2);
                    nextC = notation.getBlackTurnNotation().charAt(3);

                    if(notation.getBlackTurnNotation().length() >= 5) {
                        nextNextC = notation.getBlackTurnNotation().charAt(4);
                    }
                }
            }
        }

        // TODO: premena pesiaka na nieco ine
        if(isCharacterInRangeFromaToh(c)) {
            if(isCharacterInRangeFromaToh(nextNextC)) {
                if(isCharacterInRangeFrom1To8(nextNextC)) {
                    setSourceAndDestination(turn, isWhite, nextC, nextNextC);

                } else {
                    throw new ChessNotationMapperException();
                }

            } else if(isCharacterInRangeFrom1To8(nextC)) {
                setSourceAndDestination(turn, isWhite, nextC, nextNextC);
            }
        } else if(isCharacterInRangeFrom1To8(c)) {
            if(isCharacterInRangeFromaToh(nextC)) {
                if(isCharacterInRangeFrom1To8(nextNextC)) {
                    setSourceAndDestination(turn, isWhite, nextC, nextNextC);
                } else {
                    throw new ChessNotationMapperException();
                }

            } else {
                throw new ChessNotationMapperException();
            }
        }

        return turn;
    }



    private Turn checkCheckOrCheckMate(Turn turn, ChessTurnNotation notation, boolean isWhite) {
        if(isWhite) {
            if(notation.getWhiteTurnNotation().endsWith("#")) {
                turn.setBlackCheckMate(true);
            } else if(notation.getWhiteTurnNotation().endsWith("+")) {
                turn.setBlackCheck(true);
            }
        } else {
            if(notation.getBlackTurnNotation().endsWith("#")) {
                turn.setWhiteCheckMate(true);
            } else if(notation.getBlackTurnNotation().endsWith("+")) {
                turn.setWhiteCheck(true);
            }
        }

        return turn;
    }

    private boolean isCharacterInRangeFromaToh(char c) {
        return c >= 'a' && c <= 'g';
    }

    private boolean isCharacterInRangeFrom1To8(char c) {
        return c > '0' && c< '9';
    }

    private void setFigure(Turn turn, boolean isWhite, Figure figure) {
        if(isWhite) {
            turn.setWhiteFigure(figure);
        } else {
            turn.setBlackFigure(figure);
        }
    }

    private void setSourceAndDestination(Turn turn, boolean isWhite, char column, char row) {
        if (isWhite) {
            turn.setWhiteSourceField(turn.getWhiteFigure().getField());
            // TODO: skontrolovat
            turn.setWhiteDestinationField(game.getBoard().getField(column - 'a', row - '1'));
        } else {
            turn.setBlackSourceField(turn.getWhiteFigure().getField());
            // TODO: skontrolovat
            turn.setBlackDestinationField(game.getBoard().getField(column - 'a', row - '1'));
        }
    }

    private Figure getNonPawnFigure(String figureType, char nextC, Figure figure, boolean isWhite) throws ChessNotationMapperException {
        if (isCharacterInRangeFromaToh(nextC)) {
            int column = nextC - 'a';
            figure = game.getBoard().findFigure(figureType, column, null, isWhite);
        } else if (isCharacterInRangeFrom1To8(nextC)) {
            int row = nextC - '1';
            figure = game.getBoard().findFigure(figureType, null, row, isWhite);
        } else {
            throw new ChessNotationMapperException();
        }

        if (figure == null || !figure.getFigureChar().equals(figureType)) {
            throw new ChessNotationMapperException();
        }
        return figure;
    }
}