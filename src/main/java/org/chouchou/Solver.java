package org.chouchou;

public class Solver implements CRIndex{

    private Block[][] board;
    private Brick[] brickBuffer;

    public Solver(Block[][] board, Brick[] brickBuffer){
        this.board = board;
        this.brickBuffer = brickBuffer;
    }

    public Status solve(){

        Status rootStatus = new Status(0, 0, "0000000", "0000000", false);
            
            //Search vacancy and fill the status list
            searchEvaluate(board, brickBuffer, rootStatus, 1);

            //Find evaluation
            rootStatus.getFinalEvaluate();
 
            System.out.println("\n" + rootStatus.popMaxStatus());
            System.out.println(rootStatus.popMaxStatus().popMaxStatus());
            System.out.println(rootStatus.popMaxStatus().popMaxStatus().popMaxStatus());
            //Get largest status
            return rootStatus.popMaxStatus();

    }

    private void searchEvaluate(Block[][] board, Brick[] brickBuffer, Status rootStatus, int layer){
        int noVacancyCount = 0;
        int emptyBrickCount = 0;
        for(int i = 0; i < 3; i++){
            Brick curBrick = brickBuffer[i];
            if(brickBuffer[i] != null){
                if(!curBrick.searchVacancy(board, rootStatus)){
                    noVacancyCount++;
                }
            }else emptyBrickCount++;
        }

        if(layer == 0 && noVacancyCount == 3){
            System.out.println("No Vacancy Found. Game Over...");
            System.out.println("System exiting...");
            System.exit(0);
        }

        //Recursive function, comment this area to not take order into consideration
        
        if(emptyBrickCount == 3) return;

        for(int i = 0; i < rootStatus.getBuffLength(); i++){
            for(int j = 0; j < 3; j++){
                if(brickBuffer[j] != null){
                    if(brickBuffer[j].getType().equals(rootStatus.popStatus(i).getOriginal())){
                        //Refresh the board and buffer with the status and call function again
                        placeBrick(brickBuffer[j], rootStatus.popStatus(i), board, layer);
                        //Calculate the evaluation from empty block
                        for(int col = 0; col < 7; col++){
                            for(int row = 0; row < 7; row++){
                                if(board[col][row].getValidity() && board[col][row].getBlockType() == 'N'){
                                    emptyBlockEvaluation(board, rootStatus.popStatus(i), col, row);
                                }
                            }
                        }
                        Brick tmpBrick = brickBuffer[j];
                        brickBuffer[j] = null;

                        searchEvaluate(board, brickBuffer, rootStatus.popStatus(i), layer + 1);

                        //Undo
                        brickBuffer[j] = tmpBrick;
                        undoBoard(board, layer);

                        break;
                    }
                }
            }
        }
        
    }

    private void emptyBlockEvaluation(Block[][] board, Status status, int col, int row) {

        int eva = 0;
        char color = 'R';
        for(int i = 0; i < 3; i++){
            eva += blockEvaluate(color, col, row, board, 0, 0, false);
            for(int m = 0; m < 7; m++){
                for(int n = 0; n < 7; n++){
                    board[m][n].setExplored(false);
                }
            }
            if(color == 'R') color = 'G';
            else if(color == 'G') color = 'B';
        }

        status.setEvaluation(status.getEvaluation() + eva);

    }

    private void undoBoard(Block[][] board, int layer) {
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                if(board[i][j].getLayerExistence(layer)){
                    board[i][j].undoBlockBuffer(layer);
                }
            }
        }
    }

    public void placeBrick(Brick brick, Status status, Block[][] board, int layer) {

        String brickCode = status.getBrick();
        int column = status.getColumn();
        int row = status.getRow();

        if(brick.getIsMerge()){
            
            if(layer != 0) board[column][row].setBlockBuffer(layer, board[column][row]);
            board[column][row].setBlockType(brickCode.charAt(0));
            board[column][row].setValue(blockEvaluate(board[column][row].getBlockType(), column, row, board, 0, layer, true) + 1);                            
            for(int i = 0; i < 7; i++){
                for(int j = 0; j < 7; j++){
                    board[i][j].setExplored(false);
                }
            }
            
        }else{
            for(int i = 0; i < 7; i++){
                if(brickCode.charAt(i) != '0'){
                    board[column + cIndex[i]][row + rIndex[i]].setBlockBuffer(layer, board[column + cIndex[i]][row + rIndex[i]]);
                    board[column + cIndex[i]][row + rIndex[i]].changeFeature(brickCode.charAt(i), 1);
                }
            }
        }

    }

    private int blockEvaluate(char blockType, int column, int row, Block[][] board, int gain, int layer, boolean mergeType) {
        //Used for merge brick. Calculate the neiboring blocks' value.

        board[column][row].setExplored(true);
        int tmpGain = gain;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                int x = column + i;
                int y = row + j;
                if(x < 7 && y < 7 && x >= 0 && y >= 0 && i + j != 0){
                    if(!board[x][y].getExplored() && blockType == board[x][y].getBlockType()){
                        if(mergeType) board[x][y].setBlockBuffer(layer, board[x][y]);
                        tmpGain += blockEvaluate(blockType, x, y, board, gain, layer, mergeType);
                        if(mergeType){
                            board[x][y].setBlockType('N');
                            board[x][y].setValue(0);
                        }

                    }
                }
            }
        }
        
        return tmpGain + board[column][row].getValue();

    }

    

}