package me.i2000c.newalb.utils2;

import java.util.Random;

public class Range{
    private final Random random;
    private int min;
    private int max;
    
    public Range(){
        this(0);
    }
    
    public Range(int a){
        this(a, a);
    }
    
    public Range(int a, int b){
        random = new Random();
        if(a <= b){
            this.min = a;
            this.max = b;
        }else{
            this.min = b;
            this.max = a;
        }
    }
    
    public boolean setMin(int min){
        if(min <= this.max){
            this.min = min;
            return true;
        }else{
            return false;
        }
    }
    public int getMin(){
        return this.min;
    }
    
    public boolean setMax(int max){
        if(max >= this.min){
            this.max = max;
            return true;
        }else{
            return false;
        }
    }
    public int getMax(){
        return this.max;
    }
    
    public int getRandomInt(){
        if(min == max){
            return min;
        }else{
            return random.nextInt((max - min) + 1) + min;
        }
    }
    
    /**
     *
     * @param precision is 1, 10, 100, etc
     * @return
     */
    public double getRandomDouble(int precision){
        if(min == max){
            return min;
        }else{
            Range aux = new Range(min*precision, max*precision);
            return aux.getRandomInt() / (double) precision;
        }
    }
    
    public static Range fromString(String str) throws IllegalArgumentException{
        try{
            String[] strSplit = str.split(",");
            int a = Integer.parseInt(strSplit[0]);
            int b;
            try{
                b = Integer.parseInt(strSplit[1]);
            }catch(ArrayIndexOutOfBoundsException ex){
                b = a;
            }
            return new Range(a, b);
        }catch(Exception ex){
            throw new IllegalArgumentException("Incorrect range format: " + str);
        }
    }
    
    @Override
    public String toString(){
        if(min == max){
            return min + "";
        }else{
            return min + "," + max;
        }
    }

    @Override
    public boolean equals(Object object){
        if(object == null || !(object instanceof Range)){
            return false;
        }else{
            Range range = (Range) object;
            return this.min == range.min && this.max == range.max;
        }
    }
    
    public Range cloneRange(){
        return new Range(this.min, this.max);
    }
    
}
