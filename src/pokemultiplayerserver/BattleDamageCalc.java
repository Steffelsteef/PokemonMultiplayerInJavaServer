/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

import db.TypeChart;

/**
 *
 * @author Gebruiker
 */
public class BattleDamageCalc {
    
    /**
     * The mathematical calculation for battle damage.<br />
     * ((((((((2A/5+2)*B*C)/D)/50)+2)*X)*Y/10)*Z)/255<br />
     *  A = attacker's Level<br />
     *  B = attacker's Attack or Special<br />
     *  C = attack Power<br />
     *  D = defender's Defense or Special<br />
     *  X = same-Type attack bonus (1 or 1.5)<br />
     *  Y = Type modifiers (40, 20, 10, 5, 2.5, or 0)<br />
     *  Z = a random number between 217 and 255 <br />
     * @param att_lvl
     *       The level of the attacker.
     * @param att_str
     *       The attack power of the attacker.
     * @param att_spcstr
     *       The special attack power of the attaacker.
     * @param attackpower
     *       The attack's power.
     * @param def_def
     *       The defense of the defender.
     * @param def_spcdef
     *       The special defense of the defender.
     * @param sametype
     *       Whether or not the attacker has the same damage type of the attack. (boolean)
     * @param def_type1 
     *       Type 1 of the defender.
     * @param def_type2
     *       Type 2 of the defender.
     * @param attackdamagetype
     *       The damage type of the attack.
     * @param effecttype
     *       The type of effect (damage, statchange, etc.)
     * @param attacktype
     *       The type of attack (fire, electric, etc.)
     */
    public static int calcDamage(int att_lvl, float att_str, float att_spcstr, int attackpower, float def_def, float def_spcdef, boolean sametype, int def_type1, int def_type2, int effecttype, boolean physical, int attacktype)
    {
        
        float sametypebonus = 1;
        if(sametype == true) sametypebonus = (float) 1.5;
        
        float typemodifier1 = TypeChart.get(attacktype, def_type1);
        float typemodifier2 = 1;
        if(def_type2 != 0) typemodifier2 = TypeChart.get(attacktype, def_type2);
        float typemodifier = typemodifier1 * typemodifier2;
        
        
        double random = Math.random() * 38;
        int randomsolution = (int) Math.round(random);
        
        float attack = 0f, defense = 0f;
        if(physical)
        {
            attack = att_str;
            defense = def_def;
        }
        else{
            attack = att_spcstr;
            defense = def_spcdef;
        }
        
        return Math.round((((((((((2*att_lvl)/5+2)*attack*attackpower)/defense)/50)+2)*sametypebonus)*typemodifier)*(randomsolution+217))/255);
    }
    
}

/*
 *  ((2A/5+2)*B*C)/D)/50)+2)*X)*Y/10)*Z)/255
 *  A = attacker's Level
 *  B = attacker's Attack or Special
 *  C = attack Power
 *  D = defender's Defense or Special
 *  X = same-Type attack bonus (1 or 1.5)
 *  Y = Type modifiers (40, 20, 10, 5, 2.5, or 0)
 *  Z = a random number between 217 and 255 
 */