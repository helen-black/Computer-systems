
import java.util.ArrayList;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thereptile
 */
public class Task {
    private int complexity;
    private double probability;
    private ArrayList<Integer> processors;
    private Random r;
    
    public Task(int compl, double probability, ArrayList<Integer>procs) {
        this.complexity = compl;
        this.probability = probability;
        this.processors = procs;
        r = new Random();
    }
    
    public void setComplexity(int compl) {
        this.complexity = compl;
    }
    
    public int getComplexity() {
        return complexity;
    }
    
    public boolean isAllowed() {
        double result = r.nextDouble();
        if(probability >= result)
            return true;
        return false;
    }
    
    public boolean isProcValid(Processor proc) {
        for (Integer id: processors) 
            if (proc.getId() == id)
                return true;
        return false;
    }
}
