package org.chouchou;

import java.util.List;
import java.util.ArrayList;

public class Brick implements CRIndex{
    //Integration of blocks, minimal playing unit of the game.

    private String type;
    private int blockCost = 0;
    private String[] rotate = new String[3];
    private boolean isMerge = false;
    private List<Integer> blockPosition = new ArrayList<Integer>(); // Colored block position in the brick code.

    public void setType(String s){
        this.type = s.substring(0,7);
        int bitCount = 0;

        for(int i = 0; i < 7; i++){
            if(s.charAt(i) != '0'){
                bitCount++;
                blockPosition.add(i);
            }
        }
        blockCost = bitCount;

        //Store the rotated brick code in the rotate array.
        rotate[0] = type;
        if(blockCost > 1){
            rotate[1] = rotateBrick(type);
            if(blockCost > 2)
                rotate[2] = rotateBrick(rotate[1]);
        }

        if(blockCost == 1 && s.length() == 8){
            this.setMerge(true);
        }else this.setMerge(false);
    }

    public boolean searchVacancy(Block[][] board, Status rootStatus){
        //Search vacancy space for the brick, call from main function
        
        boolean hasVacancy = false;
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                if(board[i][j].getBlockType() == 'N' && board[i][j].getValidity()){

                    int allOneChecked = 0;
                    for(int k = 1; k < blockPosition.size(); k++){
                        int index = blockPosition.get(k);
                        if(i + cIndex[index] < 7 && i + cIndex[index] >= 0 && j + rIndex[index] < 7 && j + rIndex[index] >= 0)
                            if(board[i + cIndex[index]][j + rIndex[index]].getBlockType() == 'N' && board[i + cIndex[index]][j + rIndex[index]].getValidity())
                                allOneChecked++;
                    }

                    if(allOneChecked == blockCost - 1){
                        for(int k = 0; k < blockCost; k++){
                            Status status = new Status(i, j, rotate[k], rotate[0], isMerge);
                            status.setEvaluation(evaluate(status, board));
                            rootStatus.pushStatus(status);
                            
                            // All blocks set to unexplored.
                            for(int m = 0; m < 7; m++){
                                for(int n = 0; n < 7; n++){
                                    board[m][n].setExplored(false);
                                }
                            }
                        }
                        hasVacancy = true;
                    }

                }
            }
        }

        return hasVacancy;
    }

    private float evaluate(Status status, Block[][] board) {

        int blockGain[] = new int[blockCost];
        //int colRowEstimate = 0; // Estimate the distance to the middle of the board.
        for(int i = 0; i < blockCost; i++){
            int currentColumn = status.getColumn();
            int currentRow = status.getRow();

            currentColumn += cIndex[blockPosition.get(i)];
            currentRow += rIndex[blockPosition.get(i)];
            
            Block fakeBlock = new Block(true, status.getBrick().charAt(blockPosition.get(i)), 1);
            if(!status.getMerge()) blockGain[i] = blockEvaluate(fakeBlock, currentColumn, currentRow, board, 0);
            //colRowEstimate += Math.abs(currentColumn - 3) + Math.abs(currentRow - 3);
        }
        float sum = 0;
        int totalCount = 0;

        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                if(board[i][j].getValidity() && board[i][j].getBlockType() == 'N'){
                    sum += 0.5;
                }
            }
        }

        for(int i = 0; i < blockCost; i++){
            if(blockGain[i] > 1){
                totalCount++;
                sum += blockGain[i];
            }
        }

        if(totalCount == 2) sum *= 2;
        else if(totalCount == 3) sum *= 4;

        //sum -= (float)colRowEstimate / ((float)blockCost * 6);
        return sum;
        
    }

    private int blockEvaluate(Block block,int column, int row, Block board[][], int gain) {

        board[column][row].setExplored(true);
        int tmpGain = gain;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                int x = column + i;
                int y = row + j;
                if(x < 7 && y < 7 && x >= 0 && y >= 0 && i + j != 0){
                    
                    if(!board[x][y].getExplored() && block.getBlockType() == board[x][y].getBlockType()){
                        //Calculate if the block hasn't been explored & the block type is same as the original block type.
                        tmpGain += blockEvaluate(board[x][y], x, y, board, gain);
                    }
                }
            }
        }
        return tmpGain + block.getValue();

    }

    private String rotateBrick(String str) {

        StringBuilder sb = new StringBuilder(str);

        for(int i = 0; i < blockCost; i++){
            if(i != blockCost - 1)
                sb.setCharAt(blockPosition.get(i), str.charAt(blockPosition.get(i + 1)));
            else
                sb.setCharAt(blockPosition.get(blockCost - 1), str.charAt(0));
        }

        return sb.toString();
    }

    public String getRotate(int i){
        return rotate[i];
    }

    
    public String getType(){
        return type;
    }

    public boolean getIsMerge(){
        return isMerge;
    }

    public int getBlockCost(){
        return blockCost;
    }

    public void setMerge(boolean b){
        this.isMerge = b;
    }

    public String getRotateCode(int i){
        return rotate[i];
    }


}