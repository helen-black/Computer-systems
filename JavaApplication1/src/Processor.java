/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thereptile
 */
public class Processor {
    private int power;
    private int id;

    private boolean isBusy;
    private int leftToCalc;
    private int generalCompleted;
    
    public Processor() {
        power = id = leftToCalc = 0;
        isBusy = true;
    }
    
    public Processor(int power, int id) {
        this.power = power;
        this.id = id;
        isBusy = false;
    }
    
    public int getGeneralCompleted() {
        return generalCompleted;
    }
    
    public void makeCalculation() {
        if (this.isBusy()) {
            leftToCalc -= power;
            generalCompleted += power;
            if(leftToCalc <= 0) {
                generalCompleted += leftToCalc;
                isBusy = false;
            }
        }
    }
    
    public void setLeftToCalc(int num) {
        leftToCalc = num;  
        isBusy = true;
    }
    
    public boolean isBusy() {
        return isBusy;
    }
    
    public int getPower() {
        return power;
    }
    
    public void setPower(int power) {
        this.power = power;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
