/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Gebruiker
 */
public class Encounters {
    private static Map<Integer, LinkedList<Encounter>> list_encounters;
    
    public Encounters()
    {
        list_encounters = new HashMap<>();
    }
    
    public void add(int zone, int pnr, int level, int rate, int atk1, int atk2, int atk3, int atk4)
    {
        LinkedList<Encounter> list = list_encounters.get(zone);
        if(list == null)
        {
            list = new LinkedList<>();
        }
        list.add(new Encounter(zone, pnr, level, rate, atk1, atk2, atk3, atk4));
        list_encounters.put(zone, list);
        
    }
    
    public static int[] getEncounter(int zone)
    {
        LinkedList<Encounter> list = list_encounters.get(zone);
        
        Random random = new Random();
        
        float sum = 0;

        float[] chancelist = new float[list.size()];
        Encounter[] pkmnlist = new Encounter[list.size()];
        
        for(int i = 0; i < list.size(); i++)
        {
            sum += list.get(i).rate;
            chancelist[i] = sum;
            pkmnlist[i] = list.get(i);
        }
        
        float calc = random.nextFloat();
        //System.out.println("encounter = " + calc);
        calc *= sum;
        //System.out.println("calc *= sum -> " + calc);
        
        int[] returned = new int[6];
        
        for(int i = 0; i < list.size(); i++)
        {
            if( i == 0 )
            {
                if( calc <= chancelist[i] )
                {
                    returned[0] = pkmnlist[i].pnr;
                    returned[1] = pkmnlist[i].level;
                    returned[2] = pkmnlist[i].atk1;
                    returned[3] = pkmnlist[i].atk2;
                    returned[4] = pkmnlist[i].atk3;
                    returned[5] = pkmnlist[i].atk4;
                    
                    return returned;
                }
                    
            }
            else
            {
                if( calc > chancelist[i - 1] && calc <= chancelist[i] )
                {
                    returned[0] = pkmnlist[i].pnr;
                    returned[1] = pkmnlist[i].level;
                    returned[2] = pkmnlist[i].atk1;
                    returned[3] = pkmnlist[i].atk2;
                    returned[4] = pkmnlist[i].atk3;
                    returned[5] = pkmnlist[i].atk4;
                    
                    return returned;
                }
            }
        }
        
        returned[0] = 0;
        returned[1] = 999;
        returned[2] = 1;
        returned[3] = 60;
        returned[4] = 72;
        returned[5] = 0;

        return returned;
    }
    
    private class Encounter
    {
        int zone, pnr, level, rate, atk1, atk2, atk3, atk4;
        
        public Encounter(int zone, int pnr, int level, int rate, int atk1, int atk2, int atk3, int atk4)
        {
            this.zone = zone;
            this.pnr = pnr;
            this.level = level;
            this.rate = rate;
            this.atk1 = atk1;
            this.atk2 = atk2;
            this.atk3 = atk3;
            this.atk4 = atk4;
        }
    }
}
