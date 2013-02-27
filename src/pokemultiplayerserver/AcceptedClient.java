/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

import java.net.Socket;

/**
 *
 * @author Koen Bollen, 2012
 */
public class AcceptedClient
{
    public final int id;
    
    //private SocketAddress udpaddress;
    //private SocketAddress tcpaddress;
    
    private int x;
    private int y;
    private String username;
    
    private int charType;
    private int walking;
    
    private int zone;
    private int subzone;
    
    private boolean battling;
    
    private Socket socket_chat;
    
     public AcceptedClient(/*SocketAddress udp, SocketAddress tcp, */int id, String username, int charType, int zone, int subzone)
    {
        this.id = id;
        //this.udpaddress = udp;
        //this.tcpaddress = tcp;
        this.username = username;
        this.charType = charType;
        this.zone = zone;
        this.subzone = subzone;
        this.battling = true;
    }

     public void setState(int x, int y, int w, int z, int sz)
     {
         setX(x);
         setY(y);
         setWalking(w);
         setZone(z);
         setSubzone(sz);
     }
     
    public void setSocket(Socket socket)
    {
        this.socket_chat = socket;
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

    public int getWalking() {
        return walking;
    }

    public void setWalking(int walking) {
        this.walking = walking;
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

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    public boolean getBattling(){
        return battling;
    }
    
    public void setBattling(boolean battling){
        this.battling = battling;
    }
    
//    public void setUDPAddress(SocketAddress udp)
//    {
//        this.udpaddress = udp;
//    }
//    
//    public void setTCPAddress(SocketAddress tcp)
//    {
//        this.tcpaddress = tcp;
//    }
//    
//    public SocketAddress getUDPAddress()
//    {
//        return tcpaddress;
//    }
//    
//    public SocketAddress getTCPAddress()
//    {
//        return tcpaddress;
//    }

}
