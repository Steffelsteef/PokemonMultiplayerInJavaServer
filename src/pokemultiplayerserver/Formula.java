/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

/**
 *
 * @author Gebruiker
 */
public class Formula {
    /**
     * Choose between:
     * 
     * hp
     * attack
     * defense
     * speed
     * spatt
     * spdef
     * speed
     * 
     * @param type
     * @param nature
     * @return 
     */
    public static int calcStat(String type, int nature, int iv, int base, int ev, int level)
    {
        float IV, Base, EV, Level, Nature;
        float E;
        float honderd;
        
        IV = (float) iv;
        Base = (float) base;
        EV = (float) ev;
        Level = (float) level;
        Nature = 1f;
        
        if(type.equals("hp"))
        {
            E = 10f;
            honderd = 100f;
        }
        else
        {
            E = 5f;
            honderd = 0f;
        }
        
        if(type.equals("attack"))
        {
            if(nature == 2 || nature == 3 || nature == 4 || nature == 5)
            {
                Nature = 1.1f;
            }
            else if(nature == 6 || nature == 11 || nature == 16 || nature == 21)
            {
                Nature = 0.9f;
            }
        }
        else if(type.equals("defense"))
        {
            if(nature == 6 || nature == 8 || nature == 9 || nature == 10)
            {
                Nature = 1.1f;
            }
            else if(nature == 2 || nature == 12 || nature == 17 || nature == 22)
            {
                Nature = 0.9f;
            }
        }
        else if(type.equals("speed"))
        {
            if(nature == 11 || nature == 12 || nature == 14 || nature == 15)
            {
                Nature = 1.1f;
            }
            else if(nature == 3 || nature == 8 || nature == 18 || nature == 23)
            {
                Nature = 0.9f;
            }
        }
        else if(type.equals("spatt"))
        {
            if(nature == 16 || nature == 17 || nature == 18 || nature == 20)
            {
                Nature = 1.1f;
            }
            else if(nature == 4 || nature == 9 || nature == 14 || nature == 24)
            {
                Nature = 0.9f;
            }
        }
        else if(type.equals("spdef"))
        {
            if(nature == 21 || nature == 22 || nature == 23 || nature == 24)
            {
                Nature = 1.1f;
            }
            else if(nature == 5 || nature == 10 || nature == 15 || nature == 20)
            {
                Nature = 0.9f;
            }
        }
        
        int stat = Math.round( Math.round( ( ((IV + (2f * Base) + (EV / 4f) + honderd) * Level) / 100f ) + E ) * Nature );
        return stat;
    }
    
    public static int randomNature()
    {
        int nature = (int) (Math.round(Math.random() * 24) + 1);
        return nature;
    }
    
    public static int randomIV()
    {
        int iv = (int) (Math.round(Math.random() * 32));
        return iv;
    }
    
    public static int calcExpReceived(boolean wild, boolean OT, int baseexp, boolean lucky_egg, int level, int participants)
    {
        float a = 1.5f, t = 1f, b = 0, e = 1f, L = 5f, s = 1f;
        if(wild) a = 1f;
        if(!OT) t = 1.5f;
        b = (float) baseexp;
        if(lucky_egg) e = 1.5f;
        L = (float) level;
        s = (float) participants;
        
        
        System.out.println("( ( " + a + " * " + t + " * " + b + " * " + e + " * " + L + " )   /   ( 7 * " + s + " ) )");
        
        return Math.round((a*t*b*e*L)/(7f * s));
        
        
        
    }
    
    public static int calcNeededExp(int currentLevel, int expCurve)
    {
        int returned = 0;
        int n = currentLevel;
        
        if(expCurve == 1)
        {
            if(n < 50)
            {
                returned = (int) Math.round(((Math.pow(n,3))*(100-n))/50);
            }
            else if(n >= 50 && n < 68)
            {
                returned = (int) Math.round((Math.pow(n,3)*(150-n))/100);
            }
            else if(n >= 68 && n < 98)
            {
                returned = (int) Math.round((Math.pow(n,3)*((1911-(10*n))/3))/500);
            }
            else if(n >= 98 && n < 100)
            {
                returned = (int) Math.round((Math.pow(n,3)*(160-n))/100);
            }
        }
        else if(expCurve == 2)
        {
            returned = (int) Math.round((4*Math.pow(n,3))/5);
        }
        else if(expCurve == 3)
        {
            returned = (int) Math.round(Math.pow(n,3));
        }
        else if(expCurve == 4)
        {
            returned = (int) Math.round(((6f/5f)*Math.pow(n,3)) - (15f*(Math.pow(n,2))) + (100f*n) - 140f);
        }
        else if(expCurve == 5)
        {
            returned = (int) Math.round((5*Math.pow(n,3))/4);
        }
        else if(expCurve == 6)
        {
            if(n < 15)
            {
                returned = (int) Math.round(Math.pow(n,3) * ((((n+1)/3)+24)/50));
            }
            else if(n >= 15 && n < 36)
            {
                returned = (int) Math.round(Math.pow(n,3) * ((n+14)/50));
            }
            else if(n >= 36 && n < 100)
            {
                returned = (int) Math.round(Math.pow(n,3) * (((n/2)+32)/50));
            }
        }
        
        if(n == 100)
        {
            returned = -1;
        }
        
        System.out.println("calcNeededExp(" + currentLevel + ", " + expCurve + ") = " + returned);
        
        return returned;
    }
}
