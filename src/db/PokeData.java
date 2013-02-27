/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Gebruiker
 */
public class PokeData {

    int pnr;
    String nickname, name;
    int patk1, patk2, patk3, patk4;
    int lvl, atk, def, special_atk, special_def, spd, maxhp, hp;
    int exp;
    private int nature, location;
    
    private int hpIV, attackIV, defenseIV, spattIV, spdefIV, speedIV;
    private int hpEV, attackEV, defenseEV, spattEV, spdefEV, speedEV;

    public PokeData(int location, int pnr, String name, int patk1, int patk2, int patk3, int patk4, int lvl, int atk, int def, int special_atk, int special_def, int spd, int maxhp, int hp, int exp, int nature)
    {
        this.location = location;
        this.pnr = pnr;
        this.name = name;
        this.patk1 = patk1;
        this.patk2 = patk2;
        this.patk3 = patk3;
        this.patk4 = patk4;
        this.lvl = lvl;
        this.atk = atk;
        this.def = def;
        this.special_atk = special_atk;
        this.special_def = special_def;
        this.spd = spd;
        this.maxhp = maxhp;
        this.hp = hp;
        this.exp = exp;
        this.nature = nature;
    }
    
    public void setIVs(int hpIV, int attackIV, int defenseIV, int spattIV, int spdefIV, int speedIV)
    {
        this.hpIV = hpIV;
        this.attackIV = attackIV;
        this.defenseIV = defenseIV;
        this.spattIV = spattIV;
        this.spdefIV = spdefIV;
        this.speedIV = speedIV;
    }
    
    /**
     * choices: <br />
     * 1 = hp<br />
     * 2 = attack<br />
     * 3 = defense<br />
     * 4 = special attack<br />
     * 5 = special defense<br />
     * 6 = speed<br />
     * 
     * @param choice
     * @param addby 
     */
    public void setEV(int choice, int addby)
    {
        if(choice == 1) hpEV += addby;
        else if(choice == 2) attackEV += addby;
        else if(choice == 3) defenseEV += addby;
        else if(choice == 4) spattEV += addby;
        else if(choice == 5) spdefEV += addby;
        else if(choice == 6) speedEV += addby;
            
    }

    public int getHpIV() {
        return hpIV;
    }

    public int getAttackIV() {
        return attackIV;
    }

    public int getDefenseIV() {
        return defenseIV;
    }

    public int getSpattIV() {
        return spattIV;
    }

    public int getSpdefIV() {
        return spdefIV;
    }

    public int getSpeedIV() {
        return speedIV;
    }

    public int getHpEV() {
        return hpEV;
    }

    public int getAttackEV() {
        return attackEV;
    }

    public int getDefenseEV() {
        return defenseEV;
    }

    public int getSpattEV() {
        return spattEV;
    }

    public int getSpdefEV() {
        return spdefEV;
    }

    public int getSpeedEV() {
        return speedEV;
    }

    public int getNature() {
        return nature;
    }
    
    public int getPnr() {
        return pnr;
    }

    public void setPnr(int pnr) {
        this.pnr = pnr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPatk1() {
        return patk1;
    }
    
    public int getPatk(int id) {
        int attack;
        attack = 0;
        if(id == 1) attack = getPatk1();
        if(id == 2) attack = getPatk2();
        if(id == 3) attack = getPatk3();
        if(id == 4) attack = getPatk4();
        return attack;
    }

    public void setPatk1(int patk1) {
        this.patk1 = patk1;
    }

    public int getPatk2() {
        return patk2;
    }

    public void setPatk2(int patk2) {
        this.patk2 = patk2;
    }

    public int getPatk3() {
        return patk3;
    }

    public void setPatk3(int patk3) {
        this.patk3 = patk3;
    }

    public int getPatk4() {
        return patk4;
    }

    public void setPatk4(int patk4) {
        this.patk4 = patk4;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getSpecial_atk() {
        return special_atk;
    }

    public void setSpecial_atk(int special_atk) {
        this.special_atk = special_atk;
    }

    public int getSpecial_def() {
        return special_def;
    }

    public void setSpecial_def(int special_def) {
        this.special_def = special_def;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public int getMaxhp() {
        return maxhp;
    }

    public void setMaxhp(int maxhp) {
        this.maxhp = maxhp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
    
    public void setLocation(int location)
    {
        this.location = location;
    }
    
    public int getLocation()
    {
        return location;
    }

    
}
