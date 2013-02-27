/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Gebruiker
 */
public class TypeChart {
    static float[][] typechart = new float[18][18];
    
    public static void add(int attack, int defender, float outcome)
    {
        typechart[attack][defender] = (float) outcome;
    }
    
    public static float get(int attack, int defender)
    {
        return typechart[attack][defender];
    }
}
