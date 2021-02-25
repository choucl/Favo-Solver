package org.chouchou;

import java.util.ArrayList;
import java.util.List;

public class Status{
    //Each brick(rotated or not) placed in every possible vacancy of the board is an object of Status.

    private int row;
    private int column;
    private String brick;
    private String original;
    private float evaluation = 0;
    private List<Status> statusBuff = new ArrayList<Status>();
    private boolean isMerge;

    public Status(int c, int r, String b, String O, boolean m){
        this.column = c;
        this.row = r;
        this.brick = b;
        this.original = O;
        this.isMerge = m;
    }

    public int getRow(){
        return row;
    }

    public int getColumn(){
        return column;
    }

    public String getBrick(){
        return brick;
    }

    public String getOriginal(){
        return original;
    }

    public void setEvaluation(float e){
        this.evaluation = e;
    }

    public float getEvaluation(){
        return evaluation;
    }

    public boolean getMerge(){
        return isMerge;
    }

    public void setMerge(boolean m){
        this.isMerge = m;
    }

    private float popMaxEvaluation(){
        float max = 0;
        for(Status s: statusBuff){
            if(s.getEvaluation() > max) max = s.getEvaluation();
        }
        return max;
    }

    public void getFinalEvaluate(){
        for(Status s: statusBuff){
            if(s.getBuffLength() != 0) s.getFinalEvaluate();
            else break;
        }
        evaluation += popMaxEvaluation();
    }

    public Status popMaxStatus(){
        float max = 0;
        Status maxStatus = new Status(0,0,"0000000","000000", false);
        for(Status s : statusBuff){
            if(s.getEvaluation() > max){
                max = s.getEvaluation();
                maxStatus = s;
            }
        }
        return maxStatus;
    }

    public void pushStatus(Status status){
        statusBuff.add(status);
    }

    public Status popStatus(int i){
        return statusBuff.get(i);
    }

    public int getBuffLength(){
        return statusBuff.size();
    }

    public String outputString() {

        System.out.printf("--\nThe current best position is [%d, %d]. \n", column, row);
        System.out.printf("With the brick %s rotated x times.\n( %s )\n", original, brick);

        return "";
        
    }

    public String toString(){
        return String.format("Brick code: %s, col: %d, row: %d, eva: %f", brick, column, row, evaluation - popMaxEvaluation());
    }

}