/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gebruiker
 */
public class NewNewServer implements Runnable{
    private final int port = 2406;
    private DatagramSocket socket;
    
    Map<SocketAddress, NewAcceptedClient> list_clients;
    
    public NewNewServer()
    {
        list_clients = new HashMap<SocketAddress, NewAcceptedClient>();
    }
    
    
    
    @Override
    public void run()
    {
        try
        {
            socket = new DatagramSocket(2406);
        }
        catch (SocketException ex)
        {
            System.err.println("Unable to setup server.");
        }
        
        byte[] buffer = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buffer, 1024);
    
        while(true)
        {
            try
            {
                this.socket.receive(dp);
                String data = new String(dp.getData(), 0, dp.getLength()).trim();
                System.out.println("   < " + data);
                
                String[] args = data.split(":");
                handleCommand(args, dp.getSocketAddress());
                
                
                
            } catch (Exception ex)
            {
                System.err.println("Receive failed.");
            }
        }
    }
    
    public void handleCommand(String[] args, SocketAddress addr)
    {
        String command = args[0];        
        
        if(command.equals("j")) // j : username : charType
        {
            try {
                int size = list_clients.size();
                
                NewAcceptedClient nc = new NewAcceptedClient();
                nc.id = size + 1;
                nc.username = args[1];
                nc.charType = Integer.parseInt(args[2]);
                
                list_clients.put( addr, nc );
                
                System.out.println( "New client joined. ID(" + (nc.id) + ") - " + nc.username );
                
                String reply = "w:" + (size + 1);
                DatagramPacket dp = new DatagramPacket(reply.getBytes(), reply.length(), addr);
                
                socket.send(dp);
                System.out.println("   > (" + size + ") " + reply);
                
                String broadcast = "sn:" + nc.id + ":" + nc.username + ":" + nc.charType;
                sendAll(broadcast);
                
                for(NewAcceptedClient send : list_clients.values())
                {
                    String sendIDs = "sn:" + send.id + ":" + send.username + ":" + send.charType;
                    dp = new DatagramPacket(sendIDs.getBytes(), sendIDs.length(), addr);
                    socket.send(dp);
                }
                
            } catch (Exception ex) {
                System.err.println("Error sending welcome message.");
                ex.printStackTrace();
            }
        }
        else if (command.equals("c"))
        {
            NewAcceptedClient nc = list_clients.get(addr);
            nc.x = Integer.parseInt(args[1]);
            nc.y = Integer.parseInt(args[2]);
            String broadcast = "sc:" + nc.id + ":" + args [1] + ":" + args [2];
            sendAll(broadcast);
        }
        
    }
    
    public void sendAll(String data)
    {
        DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length());
        for( SocketAddress addr : list_clients.keySet() )
        {
            dp.setSocketAddress(addr);
            try {
                socket.send(dp);
            } catch (IOException ex) {
                Logger.getLogger(NewNewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("   > (all) " + data);
    }
    
    
    public static void main(String[] args)
    {
        new Thread(new NewNewServer()).start();
    }
    
}
