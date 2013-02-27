/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 *
 * @author Koen Bollen, 2012
 */
public class ManagerClient
{
    public final int id;
    
    private String username;
    private String ip;
    
    private int charType;
    
    private int zone;
    private int subzone;
    
     public ManagerClient(SocketAddress address, int id, String username, int charType, int zone, int subzone)
    {
        this.id = id;
        this.username = username;
        this.ip = address.toString();
        this.charType = charType;
        this.zone = zone;
        this.subzone = subzone;
    }

    public String getIP() {
        return ip;
    }
     
    public void setIP(String ip) {
        this.ip = ip;
    }
    
    public int getCharType() {
        return charType;
    }

    public void setCharType(int charType) {
        this.charType = charType;
    }

    public int getSubzone() {
        return subzone;
    }

    public void setSubzone(int subzone) {
        this.subzone = subzone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

}
