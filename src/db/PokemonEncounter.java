/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Gebruiker
 */
public class PokemonEncounter {
    private int zone;
    private int pnr;
    private int lvl;
    private int rate;
    private int atk1, atk2, atk3, atk4;
    
    public PokemonEncounter(int zone, int pnr, int lvl, int rate, int atk1, int atk2, int atk3, int atk4)
    {
        this.zone = zone;
        this.pnr = pnr;
        this.lvl = lvl;
        this.rate = rate;
        this.atk1 = atk1;
        this.atk2 = atk2;
        this.atk3 = atk3;
        this.atk4 = atk4;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    public int getPnr() {
        return pnr;
    }

    public void setPnr(int pnr) {
        this.pnr = pnr;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getAtk1() {
        return atk1;
    }

    public void setAtk1(int atk1) {
        this.atk1 = atk1;
    }

    public int getAtk2() {
        return atk2;
    }

    public void setAtk2(int atk2) {
        this.atk2 = atk2;
    }

    public int getAtk3() {
        return atk3;
    }

    public void setAtk3(int atk3) {
        this.atk3 = atk3;
    }

    public int getAtk4() {
        return atk4;
    }

    public void setAtk4(int atk4) {
        this.atk4 = atk4;
    }
    
    
    
    
}
