package ija.project.chess.figure;

import ija.project.chess.field.Field;

public class Queen extends AbstractFigure {
	public Queen() {
        super("D", true);
    }

    public Queen(boolean white) {
        super("D", white);
    }

    @Override
    public boolean canMove(Field moveTo) {
        return canDoTowerMove(moveTo) || canDoBishopMove(moveTo);
    }

    /**
    *   Skontroluje, ci sa figurka moze pohnut
    *   @param moveTo Policko kde sa ma pohnut
    */
    private boolean canDoTowerMove (Field moveTo) {
        int fieldCol = field.getCol();
        int fieldRow = field.getRow();

        int moveToCol = moveTo.getCol();
        int moveToRow = moveTo.getRow();

        if( fieldCol == moveToCol && fieldRow == moveToRow) return false;

        if( fieldCol == moveToCol) {
            if(fieldRow < moveToRow) {
                for(int row = fieldRow + 1; row <= moveToRow; row++) {
                    //vrati figuru ktora stoji vezi v ceste
                    if (field.getBoard().getField(moveToCol, row).get() != null ){
                        //ak naslo figuru az na konci cesty
                        if(row == moveToRow) {
                            //ak je to enmy figura
                            if(checkIfFieldContainsEnemyFigure(moveToCol, row)) {
                                //vzhodi a spravi presuun
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                for(int row = fieldRow - 1; row >= moveToRow; row--) {
                    //vrati figuru ktora stoji vezi v ceste
                    if (field.getBoard().getField(moveToCol, row).get() != null ){
                        //ak naslo figuru az na konci cesty
                        if(row == moveToRow) {
                            //ak je to enmy figura
                            if(checkIfFieldContainsEnemyFigure(moveToCol, row)) {
                                //vzhodi a spravi presuun
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }

            //nic nestoji v ceste srpavi len presun
            return true;
        }else if(fieldRow == moveToRow) {
            if(fieldCol < moveToCol) {
                for(int col = fieldCol+1; col <= moveToCol; col++) {
                    if (field.getBoard().getField(col, moveToRow).get() != null) {
                        if(col == moveToCol) {
                            if(checkIfFieldContainsEnemyFigure(col, moveToRow)) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                for(int col = fieldCol-1; col >= moveToCol; col--) {
                    if (field.getBoard().getField(col, moveToRow).get() != null) {
                        if(col == moveToCol) {
                            if(checkIfFieldContainsEnemyFigure(col, moveToRow)) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
    *   Skontroluje, ci sa figurka moze pohnut
    *   @param moveTo Policko kde sa ma pohnut
    */
    private boolean canDoBishopMove(Field moveTo) {
        int fieldCol = field.getCol();
        int fieldRow = field.getRow();

        int moveToCol = moveTo.getCol();
        int moveToRow = moveTo.getRow();

        if( fieldCol == moveToCol && fieldRow == moveToRow) return false;

        int checkCol = Math.abs(fieldCol - moveToCol);
        int checkRow = Math.abs(fieldRow - moveToRow);

        //nemam to overene ale nenasiel som priklad kde by to neplatilo
        if(checkCol != checkRow) return false;

        int x = fieldRow;//x ako os X
        int y = fieldCol;//y ako os Y
        while(true) {
            if(fieldRow < moveToRow) x++;
            else x--;

            if(fieldCol < moveToCol) y++;
            else y--;

            //vrati figuru ktora stoji vezi v ceste
            if (field.getBoard().getField(y, x).get() != null ){
                //ak naslo figuru az na konci cesty
                if( y == moveToCol && x == moveToRow) {
                    //ak je to enmy figura
                    if(checkIfFieldContainsEnemyFigure(y, x)) {
                        //vzhodi a spravi presuun
                        return true;
                        //chceme vyhodit vlastnu figuru
                    } else {
                        return false;
                    }
                    //nejaka figura zavadzia v ceste
                } else {
                    return false;
                }
            }
            //nic nezavadzia
            if( y == moveToCol && x == moveToRow)
                return true;
        }
    }

}
