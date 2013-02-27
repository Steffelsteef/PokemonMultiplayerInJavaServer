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
public class PokeDataSer implements Serializable {
    public int pokenr;
    public String nickname;
    public int atk1;
    public int atk2;
    public int atk3;
    public int atk4;
    public int lvl;
    public int maxhp;
    public int hp;
    
    public String username;
    public int loc;
    
    public int strength = 0, defense = 0, special_strength = 0, special_defense = 0, speed = 0;
    
    public PokeDataSer(String username, int pokenr, String nickname, int atk1, int atk2, int atk3, int atk4,int lvl, int maxhp, int hp, int loc)
    {
        this.username = username;
        
        this.pokenr = pokenr;
        this.nickname = nickname;
        this.atk1 = atk1;
        this.atk2 = atk2;
        this.atk3 = atk3;
        this.atk4 = atk4;
        this.lvl = lvl;
        this.maxhp = maxhp;
        this.hp = hp;
        
        this.loc = loc;
    }
    
    public PokeDataSer(String username, int pokenr, String nickname, int atk1, int atk2, int atk3, int atk4,int lvl, int maxhp, int hp, int loc, int strength, int defense, int special_strength, int special_defense, int speed)
    {
        this.username = username;
        
        this.pokenr = pokenr;
        this.nickname = nickname;
        this.atk1 = atk1;
        this.atk2 = atk2;
        this.atk3 = atk3;
        this.atk4 = atk4;
        this.lvl = lvl;
        this.maxhp = maxhp;
        this.hp = hp;
        
        this.loc = loc;
        
        this.strength = strength;
        this.defense = defense;
        this.special_strength = special_strength;
        this.special_defense = special_defense;
        this.speed = speed;
    }
    
    
    
}
