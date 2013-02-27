/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Gebruiker
 */
public class TempUserLocation {
    private int id;
    
    int x, y;
    int walking;
    int zone, subzone;
    
    public TempUserLocation(int id, int x, int y, int walking, int zone, int subzone)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.walking = walking;
        this.zone = zone;
        this.subzone = subzone;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getWalking() {
        return walking;
    }
    
    public void setWalking(int walking) {
        this.walking = walking;
    }
    
    public int getZone() {
        return zone;
    }
    
    public void setZone(int zone) {
        this.zone = zone;
    }
    
    public int getSubzone() {
        return subzone;
    }
    
    public void setSubzone(int subzone) {
        this.subzone = subzone;
    }
    
    public int getID() {
        return id;
    }
}
