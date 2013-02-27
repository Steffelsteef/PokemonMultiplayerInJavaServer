/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

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
public class Battle{
    int[] typechart;
    
    Map<Integer, Float> atkdef_modifier_table;
    Map<Integer, Double> acc_modifier_table;
    
    PokeMultiplayerServer pms;
    int id_challenger, id_victim;
    Socket so_challenger, so_victim;
    
    String challengerData = "";
    String victimData = "";
    int challengerSpeed = 0;
    int victimSpeed = 0;
    
    private boolean challengerActioned = false;
    private boolean victimActioned = false;
    
    // 0=atk, 1=def, 2=spcatk, 3=spcdef, 4=spd, 5=acc, 6=evasion
    int[] challengerModValues = new int[7];
    int[] victimModValues = new int[7];
    
    public Battle(PokeMultiplayerServer pms, int id_challenger, int id_victim, Socket so_challenger, Socket so_victim)
    {
        atkdef_modifier_table = new HashMap<Integer, Float>();
        acc_modifier_table = new HashMap<Integer, Double>();
        setModifierTables();
        System.out.println("new Battle(pms, " + id_challenger + ", " + id_victim + ", " + so_challenger + ", " + so_victim + ")");
        this.pms = pms;
        this.id_challenger = id_challenger;
        this.id_victim = id_victim;
        this.so_challenger = so_challenger;
        this.so_victim = so_victim;
        
        
        resetChallengerModValues();
        resetVictimModValues();
        
        sendToChallenger();
        sendToVictim();
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
        challengerModValues[0] = 0;
        challengerModValues[1] = 0;
        challengerModValues[2] = 0;
        challengerModValues[3] = 0;
        challengerModValues[4] = 0;
        challengerModValues[5] = 0;
    }
    
    private void resetVictimModValues()
    {
        victimModValues[0] = 0;
        victimModValues[1] = 0;
        victimModValues[2] = 0;
        victimModValues[3] = 0;
        victimModValues[4] = 0;
        victimModValues[5] = 0;
    }
    
    private void sendToChallenger()
    {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(so_challenger.getOutputStream());
            
            int size;
            
            size = pms.getPartySize(id_victim);
            PokeDataPackage pdp = new PokeDataPackage(size, id_victim);

            for(int i = 0; i < size; i++)
            {
                db.PokeData dpd = pms.getPokeDataPackageFromDatabase( id_victim, (i + 1) );
                ser.PokeDataSer pd = new PokeDataSer(dpd.getName(), dpd.getPnr(), dpd.getName(), dpd.getPatk1(), dpd.getPatk2(), dpd.getPatk3(), dpd.getPatk4(), dpd.getLvl(), dpd.getMaxhp(), dpd.getHp(), (i + 1));
                pdp.addPokemon(pd, (i));

            }
            
            
            
            oos.writeObject(pdp);
            
            
        } catch (IOException ex) {
            Logger.getLogger(Battle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendToVictim()
    {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(so_victim.getOutputStream());
            
            int size = pms.getPartySize(id_challenger);
            
            PokeDataPackage pdp = new PokeDataPackage(size, id_challenger);
            
            for(int i = 0; i < size; i++)
            {
                
                db.PokeData dpd = pms.getPokeDataPackageFromDatabase( id_challenger, (i + 1) );
                ser.PokeDataSer pd = new PokeDataSer(dpd.getName(), dpd.getPnr(), dpd.getName(), dpd.getPatk1(), dpd.getPatk2(), dpd.getPatk3(), dpd.getPatk4(), dpd.getLvl(), dpd.getMaxhp(), dpd.getHp(), (i + 1));
                pdp.addPokemon(pd, (i));
                
            }
            
            oos.writeObject(pdp);
            
        } catch (IOException ex) {
            Logger.getLogger(Battle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setChallengerActioned(boolean action)
    {
        this.challengerActioned = action;
    }
    private void setVictimActioned(boolean action)
    {
        this.victimActioned = action;
    }
    
    public void setChallengerAction(String data, int speed)
    {
        challengerData = data;
        challengerSpeed = speed;
        setChallengerActioned(true);
        
        if(victimActioned)
        {
            sendActionData(challengerSpeed, victimSpeed);
            
            challengerActioned = false;
            victimActioned = false;
            
            challengerData = "";
            victimData = "";
            challengerSpeed = 0;
            victimSpeed = 0;
        }
    }
    
    public void setVictimAction(String data, int speed)
    {
        victimData = data;
        victimSpeed = speed;
        setVictimActioned(true);
        
        if(challengerActioned)
        {
            sendActionData(challengerSpeed, victimSpeed);
            
            challengerActioned = false;
            victimActioned = false;
            
            challengerData = "";
            victimData = "";
            challengerSpeed = 0;
            victimSpeed = 0;
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
        pms.sendTCP(id_challenger, "sad:" + challengerData + ":" + challengerspeed);
        pms.sendTCP(id_victim, "sad:" + challengerData + ":" + challengerspeed);
        pms.sendTCP(id_challenger, "sad:" + victimData + ":" + victimspeed);
        pms.sendTCP(id_victim, "sad:" + victimData + ":" + victimspeed);
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
            challengerModValues[stat] += modifier;
        }
        else if(!towho)
        {
            victimModValues[stat] += modifier;
        }
        
    }
    
    public boolean calcHit(int user, int effect, int acc)
    {
        double chance = 0;
        if(user == 1) chance = (double) acc_modifier_table.get(challengerModValues[5]) * acc;
        if(user == 2) chance = (double) acc_modifier_table.get(victimModValues[5]) * acc;
        
        
        double random = Math.random() * 100;
        System.out.println("Chance of hit for user " + user + ": " + chance + ". Testing with random " + random);
        System.out.println("ch: " + acc_modifier_table.get(challengerModValues[5]) + ", v: " + acc_modifier_table.get(victimModValues[5]));
        System.out.println("---");
        if(random < chance) return true;
        else return false;
    }
    
    public void setEmpty()
    {
        id_challenger = -1;
        id_victim = -1;
        so_challenger = null;
        so_victim = null;

        challengerData = "";
        victimData = "";
        challengerSpeed = 0;
        victimSpeed = 0;

        challengerActioned = false;
        victimActioned = false;
        
        resetChallengerModValues();
        resetVictimModValues();
    }
    
    
}

