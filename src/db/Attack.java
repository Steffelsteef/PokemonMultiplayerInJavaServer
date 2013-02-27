/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Gebruiker
 */
public class Attack {
    private int id;
    private int pp, accuracy, speed, damage;
    private int type, effecttype;
    private boolean physical;
    private String name;
    
    public Attack(int aid, int pp, int accuracy, int speed, int damage, int type, boolean physical, String name, int effecttype)
    {
        this.id = aid;
        this.pp = pp;
        this.accuracy = accuracy;
        this.speed = speed;
        this.damage = damage;
        this.type = type;
        this.effecttype = effecttype;
        this.physical = physical;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getPp() {
        return pp;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public int getType() {
        return type;
    }

    public int getEffecttype() {
        return effecttype;
    }

    public boolean isPhysical() {
        return physical;
    }

    public String getName() {
        return name;
    }
    
    
}
