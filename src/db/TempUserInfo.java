/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Gebruiker
 */
public class TempUserInfo {
    private int uid;
    private String showed_username;
    private int chartype;
    
    public TempUserInfo(int uid, String showed_username, int chartype)
    {
        this.uid = uid;
        this.showed_username = showed_username;
        this.chartype = chartype;
    }

    public int getUid() {
        return uid;
    }

    public String getShowed_username() {
        return showed_username;
    }

    public int getChartype() {
        return chartype;
    }
    
    
}
