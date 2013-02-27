/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

import db.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ser.PokeDataSer;
import ser.PokeDataPackage;

/**
 *
 * @author Gebruiker
 */
public class BattleWild{
    Map<Integer, Float> atkdef_modifier_table;
    Map<Integer, Double> acc_modifier_table;
    
    PokeMultiplayerServer pms;
    int id_trainer;
    Socket so_trainer;
    
    String trainerData = "";
    String wildData = "";
    int trainerSpeed = 0;
    int wildSpeed = 0;
    
    private boolean trainerActioned = false;
    private boolean wildActioned = false;
    
    // 0=atk, 1=def, 2=spcatk, 3=spcdef, 4=spd, 5=acc, 6=evasion
    int[] trainerModValues = new int[7];
    int[] wildModValues = new int[7];
    
    db.PokeData wildPokemon;
    
    public BattleWild(PokeMultiplayerServer pms, int id_challenger, Socket so_challenger, int zone)
    {
        atkdef_modifier_table = new HashMap<Integer, Float>();
        acc_modifier_table = new HashMap<Integer, Double>();
        setModifierTables();
        
        int[] encounter = Encounters.getEncounter(zone);
        int nature = Formula.randomNature();
        int iv_hp = Formula.randomIV();
        int iv_a = Formula.randomIV();
        int iv_d = Formula.randomIV();
        int iv_sa = Formula.randomIV();
        int iv_sd = Formula.randomIV();
        int iv_sp = Formula.randomIV();
        Basestat temp = pms.getBasestatFromDatabase(encounter[0]);
        
        int hp = Formula.calcStat("hp", nature, iv_hp, temp.getHp(), 0, encounter[1]);
        
        wildPokemon = new db.PokeData(1, encounter[0], "", encounter[2], encounter[3], encounter[4], encounter[5], encounter[1],
                Formula.calcStat("attack", nature, iv_a, temp.getAttack(), 0, encounter[1]), 
                Formula.calcStat("defense", nature, iv_d, temp.getDefense(), 0, encounter[1]), 
                Formula.calcStat("spatt", nature, iv_sa, temp.getSpecial_attack(), 0, encounter[1]), 
                Formula.calcStat("spdef", nature, iv_sd, temp.getSpecial_defense(), 0, encounter[1]), 
                Formula.calcStat("speed", nature, iv_sp, temp.getSpeed(), 0, encounter[1]), 
                hp, hp, 0, nature);
        wildPokemon.setIVs(iv_hp, iv_a, iv_d, iv_sa, iv_sd, iv_sp);
        
        this.pms = pms;
        this.id_trainer = id_challenger;
        this.so_trainer = so_challenger;
        
        resetChallengerModValues();
        resetVictimModValues();
        
        sendToChallenger();
        
    }
    
    private void setModifierTables()
    {
        atkdef_modifier_table.put(-6, 0.25f);
        atkdef_modifier_table.put(-5, 0.28f);
        atkdef_modifier_table.put(-4, 0.33f);
        atkdef_modifier_table.put(-3, 0.40f);
        atkdef_modifier_table.put(-2, 0.50f);
        atkdef_modifier_table.put(-1, 0.66f);
        atkdef_modifier_table.put(0, 1f);
        atkdef_modifier_table.put(1, 1.5f);
        atkdef_modifier_table.put(2, 2f);
        atkdef_modifier_table.put(3, 2.5f);
        atkdef_modifier_table.put(4, 3f);
        atkdef_modifier_table.put(5, 3.5f);
        atkdef_modifier_table.put(6, 4f);
        
        acc_modifier_table.put(-6, (double) (3.0f/9.0f));
        acc_modifier_table.put(-5, (double) (3.0f/8.0f));
        acc_modifier_table.put(-4, (double) (3.0f/7.0f));
        acc_modifier_table.put(-3, (double) (3.0f/6.0f));
        acc_modifier_table.put(-2, (double) (3.0f/5.0f));
        acc_modifier_table.put(-1, (double) (3.0f/4.0f));
        acc_modifier_table.put(0, (double) (3.0f/3.0f));
        acc_modifier_table.put(1, (double) (4.0f/3.0f));
        acc_modifier_table.put(2, (double) (5.0f/3.0f));
        acc_modifier_table.put(3, (double) (6.0f/3.0f));
        acc_modifier_table.put(4, (double) (7.0f/3.0f));
        acc_modifier_table.put(5, (double) (8.0f/3.0f));
        acc_modifier_table.put(6, (double) (9.0f/3.0f));
        
    }

    private void resetChallengerModValues()
    {
        trainerModValues[0] = 0;
        trainerModValues[1] = 0;
        trainerModValues[2] = 0;
        trainerModValues[3] = 0;
        trainerModValues[4] = 0;
        trainerModValues[5] = 0;
    }
    
    private void resetVictimModValues()
    {
        wildModValues[0] = 0;
        wildModValues[1] = 0;
        wildModValues[2] = 0;
        wildModValues[3] = 0;
        wildModValues[4] = 0;
        wildModValues[5] = 0;
    }
    
    private void sendToChallenger()
    {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(so_trainer.getOutputStream());
            
            PokeDataPackage pdp = new PokeDataPackage(1, -1);

            ser.PokeDataSer pd = new PokeDataSer("", wildPokemon.getPnr(), "", wildPokemon.getPatk1(), wildPokemon.getPatk2(), wildPokemon.getPatk3(), wildPokemon.getPatk4(), wildPokemon.getLvl(), wildPokemon.getMaxhp(), wildPokemon.getHp(), 1, wildPokemon.getAtk(), wildPokemon.getDef(), wildPokemon.getSpecial_atk(), wildPokemon.getSpecial_def(), wildPokemon.getSpd());
            pdp.addPokemon(pd, 0);
            
            System.out.println("Goign to send a pdp...");
            oos.writeObject(pdp);
            System.out.println("Sent a pdp!");
            
            
        } catch (IOException ex) {
            Logger.getLogger(BattleWild.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setChallengerActioned(boolean action)
    {
        this.trainerActioned = action;
    }
    private void setVictimActioned(boolean action)
    {
        this.wildActioned = action;
    }
    
    public void setChallengerAction(String data, int speed)
    {
        trainerData = data;
        trainerSpeed = speed;
        setChallengerActioned(true);
        
        if(wildActioned)
        {
            sendActionData(trainerSpeed, wildSpeed);
            
            trainerActioned = false;
            wildActioned = false;
            
            trainerData = "";
            wildData = "";
            trainerSpeed = 0;
            wildSpeed = 0;
        }
    }
    
    public void setVictimAction(String data, int speed)
    {
        wildData = data;
        wildSpeed = speed;
        setVictimActioned(true);
        
        if(trainerActioned)
        {
            sendActionData(trainerSpeed, wildSpeed);
            
            trainerActioned = false;
            wildActioned = false;
            
            trainerData = "";
            wildData = "";
            trainerSpeed = 0;
            wildSpeed = 0;
        }
    }

    private void sendActionData(int challengerspeed, int victimspeed)
    {
        System.out.println("Battle debug: challengerspeed = " + challengerspeed + " and victimspeed = " + victimspeed + ".");
        if(challengerspeed == victimspeed)
        {
            if(Math.random() > 0.5) challengerspeed += 100;
            else victimspeed += 100;
        }
        pms.sendTCP(id_trainer, "sad:" + trainerData + ":" + challengerspeed);
        pms.sendTCP(id_trainer, "sad:" + wildData + ":" + victimspeed);
    }
    
    /**
     * 
     * @param towho true = self, false = other
     * @param user 1 = challenger, 2 = victim
     * @param stat 0 = atk, 1 = def, 2 = spcatk, 3 = spcdef, 4 = spd, 5 = accuracy, 6 = evasion
     * @param modifier how much the stat must be modified
     */
    public void setModifier(boolean towho, int user, int stat, int modifier)
    {
        if(user == 2)
        {
            if(towho == true) towho = false;
            else towho = true;
        }
            
        if(towho)
        {
            trainerModValues[stat] += modifier;
        }
        else if(!towho)
        {
            wildModValues[stat] += modifier;
        }
        
    }
    
    public boolean calcHit(int user, int effect, int acc)
    {
        double chance = 0;
        if(user == 1) chance = (double) acc_modifier_table.get(trainerModValues[5]) * acc;
        if(user == 2) chance = (double) acc_modifier_table.get(wildModValues[5]) * acc;
        
        
        double random = Math.random() * 100;
        System.out.println("Chance of hit for user " + user + ": " + chance + ". Testing with random " + random);
        System.out.println("ch: " + acc_modifier_table.get(trainerModValues[5]) + ", v: " + acc_modifier_table.get(wildModValues[5]));
        System.out.println("---");
        if(random < chance) return true;
        else return false;
    }
    
    public void setEmpty()
    {
        id_trainer = -1;
        so_trainer = null;

        trainerData = "";
        wildData = "";
        trainerSpeed = 0;
        wildSpeed = 0;

        trainerActioned = false;
        wildActioned = false;
        
        resetChallengerModValues();
        resetVictimModValues();
    }
}

