/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gebruiker
 */
public class TempUserPokedata {
    private int id;
    int pnr;
    String nickname;
    int patk1, patk2, patk3, patk4;
    int lvl, atk, def, special_atk, special_def, spd, maxhp, hp;
    int exp;
    int location;
    
    int partysize;
    
    
    Map<Integer, PokeData> list_pokedata;

    public TempUserPokedata(int id)
    {
        this.id = id;
        list_pokedata = new HashMap<>();
    }
    
//    public void putPokedata(int id, int pnr, String nickname, int patk1, int patk2, int patk3, int patk4, int lvl, int atk, int def, int special_atk, int special_def, int spd, int maxhp, int hp, int exp, int nature, int location)
//    {
//        this.id = id;
//        list_pokedata.put(location, new PokeData(pnr, nickname, patk1, patk2, patk3, patk4, lvl, atk, def, special_atk, special_def, spd, maxhp, hp, exp, nature)); 
//    }
    
    public void putPokedata(int location, PokeData pd)
    {
        list_pokedata.put(location, pd);
    }
    
    public PokeData getPokeData(int location)
    {
        return list_pokedata.get(location);
    }
    
    public Map<Integer, PokeData> getList_pokedata()
    {
        return list_pokedata;
    }
    
    public boolean containsKey(int key)
    {
        return list_pokedata.containsKey(key);
    }
    
    public int getID()
    {
        return id;
    }
    
    
    
}

