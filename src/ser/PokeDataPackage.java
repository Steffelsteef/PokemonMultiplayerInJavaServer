/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ser;

import java.io.Serializable;

/**
 *
 * @author Gebruiker
 */
public class PokeDataPackage implements Serializable {
    public int[] pokenr;
    public String [] nickname;
    public int[] atk1;
    public int[] atk2;
    public int[] atk3;
    public int[] atk4;
    public int[] lvl;
    public int[] maxhp;
    public int[] hp;
    
    public int[] strength;
    public int[] defense;
    public int[] special_strength;
    public int[] special_defense;
    public int[] speed;
    
    public int size;
    public int id;
    
    public PokeDataPackage(int size, int id)
    {
        this.size = size;
        this.id = id;
        
        pokenr = new int[size];
        nickname = new String[size];
        atk1 = new int[size];
        atk2 = new int[size];
        atk3 = new int[size];
        atk4 = new int[size];
        lvl = new int[size];
        maxhp = new int[size];
        hp = new int[size];
        
        strength = new int[size];
        defense = new int[size];
        special_strength = new int[size];
        special_defense = new int[size];
        speed = new int[size];
        
    }
    
//    public void addPokemon(int nr, String nickname, int atk1, int atk2, int atk3, int atk4, int lvl, int maxhp, int hp, int loc)
//    {
//        this.pokenr[loc] = nr;
//        this.nickname[loc] = nickname;
//        this.atk1[loc] = atk1;
//        this.atk2[loc] = atk2;
//        this.atk3[loc] = atk3;
//        this.atk4[loc] = atk4;
//        this.lvl[loc] = lvl;
//        this.maxhp[loc] = maxhp;
//        this.hp[loc] = hp;
//    }
    
    public void addPokemon(PokeDataSer pd, int loc)
    {
        this.pokenr[loc] = pd.pokenr;
        this.nickname[loc] = pd.nickname;
        this.atk1[loc] = pd.atk1;
        this.atk2[loc] = pd.atk2;
        this.atk3[loc] = pd.atk3;
        this.atk4[loc] = pd.atk4;
        this.lvl[loc] = pd.lvl;
        this.maxhp[loc] = pd.maxhp;
        this.hp[loc] = pd.hp;
        this.strength[loc] = pd.strength;
        this.defense[loc] = pd.defense;
        this.special_strength[loc] = pd.special_strength;
        this.special_defense[loc] = pd.special_defense;
    }
    
    
    
}
