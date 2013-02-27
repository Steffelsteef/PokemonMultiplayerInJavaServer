/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ser;

import java.io.Serializable;

/**
 *
 * @author Gebruiker
 */
public class ChatLine implements Serializable{
    String username = "";
    String line = "";
    
    public ChatLine(String username, String line)
    {
        this.username = username;
        this.line = line;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getLine()
    {
        return line;
    }
}
