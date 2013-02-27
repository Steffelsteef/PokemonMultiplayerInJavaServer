/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Gebruiker
 */
public class Pokemon {
    private int number;
    private String name;
    int type1, type2;
    int baseexp;
    int ev_hp, ev_attack, ev_defense, ev_specialattack, ev_specialdefense, ev_speed;
    int expcurve;
    
    public Pokemon(int number, String name, int type1, int type2, int baseexp, int ev_hp, int ev_attack, int ev_defense, int ev_specialattack, int ev_specialdefense, int ev_speed, int expcurve)
    {
        this.number = number;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.baseexp = baseexp;
        this.ev_hp = ev_hp;
        this.ev_attack = ev_attack;
        this.ev_defense = ev_defense;
        this.ev_specialattack = ev_specialattack;
        this.ev_specialdefense = ev_specialdefense;
        this.ev_speed = ev_speed;
        this.expcurve = expcurve;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getType1() {
        return type1;
    }

    public int getType2() {
        return type2;
    }

    public int getBaseexp() {
        return baseexp;
    }

    public int getEv_hp() {
        return ev_hp;
    }

    public int getEv_attack() {
        return ev_attack;
    }

    public int getEv_defense() {
        return ev_defense;
    }

    public int getEv_specialattack() {
        return ev_specialattack;
    }

    public int getEv_specialdefense() {
        return ev_specialdefense;
    }

    public int getEv_speed() {
        return ev_speed;
    }

    public int getExpcurve() {
        return expcurve;
    }
    
    
}
