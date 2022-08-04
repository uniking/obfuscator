package me.superblaubeere27.jobf.processors.cfg;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Keys {
    private static Keys self = new Keys();
    private static Random random = new Random();

    Set<Integer> hs = new HashSet<>();

    private Keys(){

    }

    public static Keys getInstance(){
        return self;
    }

    /*
不能重复
 */
    public int getKey(){
        int key;
        while (true){
            int one = random.nextInt() % 9999;
            if(hs.contains(one)){
                continue;
            }else{
                key = one;
                hs.add(key);
                break;
            }
        }
        return key;
    }
}
