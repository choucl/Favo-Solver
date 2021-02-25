package org.chouchou;

public class Block {
    //Every blocks on the board belongs to the class

    private boolean validity = true;
    private char blockType = 'N';
    private int value = 0;
    private boolean hasExplored = false;
    //Type and value before being modified
    private char typeBuffer[] = new char[4];
    private int valueBuffer[] = new int[4];
    /*
    Block types:
    N: no exsiting block, R: Red block
    B: Blue block, G: Green block
    */

    public Block(boolean v, char bT, int val){
        this.validity = v;
        this.blockType = bT;
        this.value = val;
    }

    public void setValidity(boolean v){
        this.validity = v;
    }

    public boolean getValidity(){
        return validity;
    }

    public void setBlockType(char i){
        this.blockType = i;
    }

    public char getBlockType(){
        return blockType;
    }

    public void setExplored(boolean b){
        this.hasExplored = b;
    }

    public boolean getExplored(){
        return hasExplored;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int i){
        this.value = i;
    }

    public void setBlockBuffer(int i, Block b){
        this.typeBuffer[i] = b.getBlockType();
        this.valueBuffer[i] = b.getValue();
    }

    public void undoBlockBuffer(int i){
        this.blockType = typeBuffer[i];
        this.value = valueBuffer[i];

        typeBuffer[i] = 0;
        valueBuffer[i] = 0;
    }

    public boolean getLayerExistence(int layer){
        if(typeBuffer[layer] != 0){
            return true;
        }else return false;
    }

    public void printLayer(int layer){
        System.out.println(typeBuffer[layer] + " " + valueBuffer[layer]);
    }
    
    public void changeFeature(char c, int i){
        this.blockType = c;
        this.value = i;
    }
}