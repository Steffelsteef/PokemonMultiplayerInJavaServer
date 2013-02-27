/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokemultiplayerserver;

import db.Attack;
import db.Basestat;
import db.Encounters;
import db.PokeData;
import db.Pokemon;
import db.TempUserLocation;
import db.TempUserPokedata;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ser.ChatLine;

/**
 *
 * @author Gebruiker
 * 
 * Version 0.01 "K@#H@DS*SDHK%"
 */
public class PokeMultiplayerServer implements Runnable{
    private final int port;
    private DatagramSocket socket; // 2406 = UDP, de rest TCP
    private ServerSocket serversocket_states;
    private ServerSocket serversocket_other;    
    private ServerSocket serversocket_chat;
    private ServerSocket serversocket_pdp;
    
    private ArrayList<Battle> list_running_battles;
    private Map<Integer, BattleWild> list_running_wildbattles;
    
    private Map<Integer, AcceptedClient> list_clients;
    private Map<Integer, SocketAddress> list_clients_udp;
    private Map<Integer, Socket> list_clients_tcp;
    private Map<Integer, Socket> list_clients_chat;
    private Map<Integer, Socket> list_clients_pdp;
    
    private Map<Integer, TempUserLocation> list_clientlocations;
    private Map<Integer, TempUserPokedata> list_clientpokedata;
    
    private Map<Socket, String> list_chats;
    private Map<Socket, AcceptedClient> list_states;
        
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
    private Map<Integer, Attack> db_attackdex;
    private Map<Integer, Pokemon> db_pokedex;
    private Map<Integer, Basestat> db_basestats;
    
    
    Manager manager;
    
    public PokeMultiplayerServer(){
        this.port = 2406;
        this.list_clients = new HashMap<>();
        this.list_clients_udp = new HashMap<>();  
        this.list_clients_tcp = new HashMap<>();  
        this.list_clients_chat = new HashMap<>();
        this.list_clients_pdp = new HashMap<>();
        
        this.list_clientlocations = new HashMap<>();
        this.list_clientpokedata = new HashMap<>();
        
        this.list_chats = new HashMap<>();
        this.list_states = new HashMap<>();
        try {
            serversocket_states = new ServerSocket(port + 1);
            serversocket_other = new ServerSocket(port + 2);
            serversocket_chat = new ServerSocket(port + 3);
            serversocket_pdp = new ServerSocket(port + 4);
        } catch (BindException ex) {
            JOptionPane.showMessageDialog(new JPanel(), "Server already/still running!", "Oh my!", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        new Thread(state_accept).start();
        new Thread(TCP_other).start();
        
        
        
        try {
            socket = new DatagramSocket(port, InetAddress.getLocalHost());
            new Thread(udp_receive).start();
            System.out.println("Opened a socket at " + InetAddress.getLocalHost() + " --- " + port);
        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection = null;
        statement = null;
        resultSet = null;
        
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        setConnection();
        try {
            manager = new Manager(this, InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setConnection()
    {
        String url = "jdbc:postgresql://localhost/pokemon";
        String user = "postgres";
        String password = "kl3hjJfa#@ad";
        
        
        
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT UID, showed_username, chartype FROM users WHERE uid = 0");
            
            resultSet.next();
            System.out.println("Testing database... User 0 = (" + resultSet.getString("uid") + ") " + resultSet.getString("showed_username") + ", type " + resultSet.getString("chartype") + "!");
                //System.out.println(Integer.parseInt(resultSet.getString(1)) + ", " + resultSet.getString(2) + " - type " + Integer.parseInt(resultSet.getString(3)));
        
            setLocalDatabase();
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(new JPanel(), "Database test failed!", "Oh my!", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }
    
    @Override
    public void run()
    {
        
    }
    
    private void setLocalDatabase()
    {
        db_attackdex = new HashMap<>();
        db_pokedex = new HashMap<>();
        db_basestats = new HashMap<>();
        
        String query = "SELECT * FROM attacks";
        try {
            ResultSet attackResultSet = statement.executeQuery(query);
            while(attackResultSet.next())
            {
                boolean physical = true;
                if (attackResultSet.getInt(7) == 0)
                {
                    physical = false;
                }
                db_attackdex.put(attackResultSet.getInt(1), new Attack(attackResultSet.getInt(1), attackResultSet.getInt(2), attackResultSet.getInt(3), attackResultSet.getInt(4), attackResultSet.getInt(5), attackResultSet.getInt(6), physical, attackResultSet.getString(8), attackResultSet.getInt(9)));
            }
        } catch (SQLException ex) {
            System.err.println("Query failed: \"" + query + "\"");
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        query = "SELECT * FROM pokedex";
        try {
            ResultSet rs = statement.executeQuery(query);
            while(rs.next())
            {
                db_pokedex.put(rs.getInt(1), new Pokemon(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getInt(11), rs.getInt(12)));
            }
        } catch (SQLException ex) {
            System.err.println("Query failed: \"" + query + "\"");
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        query = "SELECT * FROM basestats";
        try {
            ResultSet rs = statement.executeQuery(query);
            while(rs.next())
            {
                db_basestats.put(rs.getInt(1), new Basestat(rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7)));
            }
        } catch (SQLException ex) {
            System.err.println("Query failed: \"" + query + "\"");
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        query = "SELECT * FROM encounters";
        try {
            ResultSet rs = statement.executeQuery(query);
            Encounters encounters = new Encounters();
            while(rs.next())
            {
                System.out.println("Adding encounter to zone " + rs.getInt(1));
                encounters.add(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8));
            }
        } catch (SQLException ex) {
            System.err.println("Query failed: \"" + query + "\"");
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        query = "SELECT * FROM typechart ORDER BY pk ASC";
        try {
            ResultSet rs = statement.executeQuery(query);
            System.out.println("=======================");
            while(rs.next())
            {
                for(int i = 0; i < 17; i++)
                {
                    System.out.println("typechart[" + rs.getInt(18) + "][" + (i+1) + "] = " + rs.getFloat(i+1) + "f;");
                }
                
            }
            System.out.println("=======================");
        } catch (SQLException ex) {
            System.err.println("Query failed: \"" + query + "\"");
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Database loaded!");
        
    }
    
    PokeMultiplayerServer pms = this;
    private Runnable state_accept = new Runnable() 
    {
        @Override
        public void run() {
            while(true)
            {
                try {
                    Socket accepted_socket = serversocket_states.accept();
                    
                    Socket accepted_objectssocket = serversocket_chat.accept();
                    
                    Socket accepted_pdpsocket = serversocket_pdp.accept();
                    

                    DataInputStream dis = new DataInputStream(accepted_socket.getInputStream());
                    String uc = dis.readUTF(); // username, password

                    String[] upa = uc.split(":"); // username & password array
                    String received_username = upa[0];
                    String received_password = upa[1];
                    
                    String database_password = "", showed_username = "";
                    boolean username = true;
                    boolean password;
                    try {
                        ResultSet passwordcheck = statement.executeQuery("SELECT showed_username, password FROM users WHERE username = '" + received_username.toLowerCase() + "'");
                        passwordcheck.next();
                        database_password = passwordcheck.getString("password");
                        showed_username = passwordcheck.getString("showed_username");
                    } catch (SQLException ex) {
                        username = false;
                    }
                    
                    
                    
                    password = database_password.equals(received_password);
                    int user_id;
                    
                    
                    System.out.println(database_password + " and " +received_password);
                    if(username && password)
                    {
                        user_id = 0;
                        int user_x = 960;
                        int user_y = 640 - 16;
                        int user_w = 0;
                        int user_z = 0;
                        int user_sz = 0;
                        int user_charType = 0;
                        String user_name = "";
                        
                        String query = "";


                        try{
                            query = "SELECT u.uid, u.chartype, u.showed_username, l.x, l.y, l.walking, l.zone, l.subzone FROM users AS u LEFT JOIN locations AS l ON u.uid = l.uid WHERE username = '" + received_username.toLowerCase() + "'";
                            ResultSet resultSet = statement.executeQuery(query);
                            resultSet.next();
                            System.out.println("ID(" + resultSet.getString(1) + ") , username " + received_username.toLowerCase() + " with charType " + resultSet.getString(2) + "!");

                            user_id = Integer.parseInt(resultSet.getString(1));
                            user_charType = Integer.parseInt(resultSet.getString(2));
                            user_name = resultSet.getString(3);
                            
                            System.out.println("x = " + resultSet.getString(4) +
                                             ", y = " + resultSet.getString(5) +
                                             ", walking = " + resultSet.getString(6) +
                                             ", zone = " + resultSet.getString(7) +
                                             ", subzone = " + resultSet.getString(8));

                            
                            
                            user_x = resultSet.getInt(4);
                            user_y = resultSet.getInt(5);
                            user_w = resultSet.getInt(6);
                            user_z = resultSet.getInt(7);
                            user_sz = resultSet.getInt(8);
                            
                            list_clientlocations.put(user_id, new TempUserLocation(user_id, user_x, user_y, user_w, user_z, user_sz));
                            list_clientpokedata.put(user_id, new TempUserPokedata(user_id));

                            System.out.println(user_id + ", (" + user_x + "," + user_y + ") in zone (" + user_z + "," + user_sz + ")");
                        }catch(SQLException ex)
                        {
                            System.err.println("Error loading in db!");
                            System.err.println("Query: /" + query);
                            ex.printStackTrace();
                        }

                        list_clients_chat.put(user_id, accepted_objectssocket);
                        new Chat_Thread(pms, user_id, accepted_objectssocket);
                        
                        list_clients_pdp.put(user_id, accepted_pdpsocket);

                        DataOutputStream dos = new DataOutputStream(accepted_socket.getOutputStream());
                        dos.writeUTF(user_id + ":" + user_charType + ":" + user_x + ":" + user_y + ":" + user_w + ":" + user_z + ":" + user_sz + ":" + user_name);
                        list_clients_tcp.put(user_id, accepted_socket);


                        state_sendall("sn:" + user_id + ":" + showed_username + ":" + user_charType);
                        state_sendall("ss:" + user_id + ":" + user_x + ":" + user_y + ":" + user_w + ":" + user_z + ":" + user_sz);

                        for(AcceptedClient ac : list_clients.values())
                        {
                            System.out.println("I'm at the for");
                            dos.writeUTF("sfn:" + ac.id + ":" + ac.getUsername() + ":" + ac.getCharType());
                            dos.writeUTF("ss:" + ac.id + ":" + ac.getX() + ":" + ac.getY() + ":" + ac.getWalking() + ":" + ac.getZone() + ":" + ac.getSubzone());
                            System.out.println("x,y = " + ac.getX() + ", " + ac.getY());
                        }

                        AcceptedClient new_ac = new AcceptedClient(/*null, accepted_socket.getLocalSocketAddress(), */ user_id, showed_username, user_charType, user_z, user_sz);
                        new_ac.setState(user_x, user_y, user_w, user_z, user_sz);
                        list_clients.put(new_ac.id, new_ac);

                        dos.writeUTF("sw:" + 1);
                        
                        manager.addUser(user_id, new ManagerClient(accepted_socket.getLocalSocketAddress(), user_id, user_name, user_charType, user_z, user_sz));
                        
                        new TCP_Thread(user_id, accepted_socket);
                        System.out.println("list_clients.size = " + list_clients.size());

                        list_running_battles = new ArrayList<>();
                        list_running_wildbattles = new HashMap<>();
                        
                        try {
                            ResultSet rs = statement.executeQuery("SELECT d.uid, d.pnr, d.nickname, d.patk1, d.patk2, d.patk3, d.patk4, d.lvl, d.atk, d.def, d.special_atk, d.special_def, d.spd, d.maxhp, d.hp, d.exp, d.location, b.expcurve, d.nature, d.iv_hp, d.iv_attack, d.iv_defense, d.iv_speed, d.iv_special_attack, d.iv_special_defense FROM pokedata d LEFT JOIN pokedex b ON d.pnr = b.number WHERE uid = " + user_id + " ORDER BY location ASC ");
                            String[] pokedata;
                            
                            String reply;
                            while(rs.next())
                            {
                                System.out.println("There is a new Pokemon! My id = " + user_id + " AND THIS IS A START QUERY");
                                pokedata = new String[17];
                                if(Integer.parseInt(rs.getString(17)) > 6)
                                {
                                    break;
                                }
                                String nickname = rs.getString(3);
                                if(rs.getObject(3) == null)
                                {
                                    nickname = "";
                                }

                                pokedata[0] = rs.getString("pnr");
                                pokedata[1] = nickname;
                                pokedata[2] = rs.getString("patk1");
                                pokedata[3] = rs.getString("patk2");
                                pokedata[4] = rs.getString("patk3");
                                pokedata[5] = rs.getString("patk4");
                                pokedata[6] = rs.getString("lvl");
                                pokedata[7] = rs.getString("atk");
                                pokedata[8] = rs.getString("def");
                                pokedata[9] = rs.getString("special_atk");
                                pokedata[10] = rs.getString("special_def");
                                pokedata[11] = rs.getString("spd");
                                pokedata[12] = rs.getString("maxhp");
                                pokedata[13] = rs.getString("hp");
                                pokedata[14] = rs.getString("exp");
                                pokedata[16] = rs.getString("location");
                                int nature = rs.getInt("nature");
                                int hpIV = rs.getInt("iv_hp");
                                int attackIV = rs.getInt("iv_attack");
                                int defenseIV = rs.getInt("iv_defense");
                                int speedIV = rs.getInt("iv_speed");
                                int spattIV = rs.getInt("iv_special_attack");
                                int spdefIV = rs.getInt("iv_special_defense");
                                
                                TempUserPokedata tup = list_clientpokedata.get(user_id);
                                PokeData pd = new PokeData(Integer.parseInt(pokedata[16]), Integer.parseInt(pokedata[0]), 
                                        nickname,
                                        Integer.parseInt(pokedata[2]),
                                        Integer.parseInt(pokedata[3]),
                                        Integer.parseInt(pokedata[4]),
                                        Integer.parseInt(pokedata[5]),
                                        Integer.parseInt(pokedata[6]),
                                        Integer.parseInt(pokedata[7]),
                                        Integer.parseInt(pokedata[8]),
                                        Integer.parseInt(pokedata[9]),
                                        Integer.parseInt(pokedata[10]),
                                        Integer.parseInt(pokedata[11]),
                                        Integer.parseInt(pokedata[12]),
                                        Integer.parseInt(pokedata[13]),
                                        Integer.parseInt(pokedata[14]),
                                        nature);
                                
                                pd.setIVs(hpIV, attackIV, defenseIV, spattIV, spdefIV, speedIV);
                                tup.putPokedata(Integer.parseInt(pokedata[16]), pd);
                                list_clientpokedata.put(user_id, tup);
                                
                                pokedata[15] = rs.getString("expcurve");
                                
                                reply = "syp:" + 
                                    pokedata[0] + ":" + //pnr
                                    pokedata[1] + ":" + //nickname
                                    pokedata[2] + ":" + //atk1
                                    pokedata[3] + ":" + //atk2
                                    pokedata[4] + ":" + //atk3
                                    pokedata[5] + ":" + //atk4
                                    pokedata[6] + ":" + //lvl
                                    pokedata[7] + ":" + //atk
                                    pokedata[8] + ":" + //def
                                    pokedata[9] + ":" + //sp atk
                                    pokedata[10] + ":" + //sp def
                                    pokedata[11] + ":" + //spd
                                    pokedata[12] + ":" + //maxhp
                                    pokedata[13] + ":" + //hp
                                    pokedata[14] + ":" + //exp
                                    pokedata[15] + ":" + // expcurve
                                    pokedata[16]; // loc

                                dos = new DataOutputStream(accepted_socket.getOutputStream());
                                dos.writeUTF(reply);
                                System.out.println("Sent the new pokemon! Checking for more...");
                            }

                        } catch (SQLException ex) {
                            System.err.println("Some query didn't go well.");
                            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(!username)
                    {
                        DataOutputStream dos = new DataOutputStream(accepted_socket.getOutputStream());
                        dos.writeUTF("-2:");
                    }
                    else if(!password)
                    {
                        DataOutputStream dos = new DataOutputStream(accepted_socket.getOutputStream());
                        dos.writeUTF("-1:");
                    }
                    
                    

                } catch (IOException ex) {
                    Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }   
    };
    
//    private Runnable state_receive = new Runnable() 
//    {
//        DataInputStream dis;
//        
//        @Override
//        public void run() {
//            
//            for(Socket so : list_clients_tcp.values())
//            {
//                so.
//            }
//        }   
//    };
    
    public void state_sendall(String data)
    {
        for(Socket so : list_clients_tcp.values())
        {
            try {
                DataOutputStream dos = new DataOutputStream(so.getOutputStream());
                dos.writeUTF(data);
            } catch (IOException ex) {
                Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Runnable udp_receive = new Runnable() 
    {
        @Override
        public void run() {
            
            while(true){
                try{
                    byte[] buffer = new byte[2048];
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    socket.receive(dp);
                    String data = new String(dp.getData(), 0, dp.getLength()).trim();
                    String[] args = data.split(":");
                    String command = args[0];
                    //System.out.println("R>" + data);

                    String reply = null;
                    
                    list_clients_udp.put(Integer.parseInt(args[1]), dp.getSocketAddress());
                    
                    if( command.equals("cc") )
                    {
                        AcceptedClient ac = list_clients.get(Integer.parseInt(args[1]));
                        ac.setX(Integer.parseInt(args[2]));
                        ac.setY(Integer.parseInt(args[3]));
                        ac.setWalking(Integer.parseInt(args[4]));
                        list_clients.put(Integer.parseInt(args[1]), ac);

                        dp = new DatagramPacket(data.getBytes(), data.length());
                        for(SocketAddress sa : list_clients_udp.values())
                        {
                            //System.out.println("s> " + data);
                            dp.setSocketAddress(sa);
                            socket.send(dp);
                        }
                        
                        if(ac.getX() % 64 == 0 && (ac.getY() + 16) % 64 == 0)
                        {
                            int id = ac.id;
                            TempUserLocation tul = list_clientlocations.get(id);
                            tul.setX(ac.getX());
                            tul.setY(ac.getY());
                            tul.setWalking(ac.getWalking());
                            list_clientlocations.put(id, tul);
                        }
                        
                    }

//                    if(reply != null){
//                        reply += "\n";
//                        DatagramPacket reply_packet = new DatagramPacket(reply.getBytes(), reply.length(), dp.getSocketAddress());
//
//                        this.socket.send(reply_packet);
//                    }

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }   
    };
    
    private synchronized void list_clientlocations_put(int id, TempUserLocation tul)
    {
        list_clientlocations.put(id, tul);
    }
    
    private class Chat_Thread extends Thread{
        PokeMultiplayerServer pms;
        int id;
        Socket socketje;
        
        String query;
        
        public Chat_Thread(PokeMultiplayerServer pms, int id, Socket so)
        {
            this.pms = pms;
            this.id = id;
            this.socketje = so;
            start();
            System.out.println("New objects thread started!");
        }
        
        @Override
        public void run()
        {
            ObjectInputStream ois;
            while(true)
            {
                try {
                    ois = new ObjectInputStream(socketje.getInputStream());
                    ChatLine chatLine = (ChatLine) ois.readObject();
                    System.out.println(">>> ChatLine(" + chatLine.getUsername() + ": " + chatLine.getLine() + ")");

                    if(chatLine.getLine().startsWith("/battle"))
                    {
                        String args[] = chatLine.getLine().split(" ");
                        System.out.println("OOH! A battle!");
                        if(args[1] != null)
                        {
                            
                            //query = "SELECT uid FROM users WHERE username = '" + args[1] + "'";
                            //ResultSet rs = statement.executeQuery(query);
                            //rs.next();
                            //int id_victim = Integer.parseInt(rs.getString(1));
                            int id_victim = Integer.parseInt(args[1]);

                            if(list_clientlocations.get(id_victim).getZone() == list_clientlocations.get(id).getZone()
                                    && list_clientlocations.get(id_victim).getSubzone() == list_clientlocations.get(id).getSubzone()
                                    && list_clientlocations.get(id_victim).getZone() != -1 )
                            {
                                list_running_battles.add(new Battle(pms, id, id_victim, list_clients_pdp.get(id), list_clients_pdp.get(id_victim)));
                            }
                            else
                            {
                                DataOutputStream dos = new DataOutputStream(list_clients_tcp.get(id).getOutputStream());
                                dos.writeUTF("sb:no");
                            }
                            
                        } else
                        {
                            System.out.println("... Nevermind.");
                        }
                        
                    }
                    else if(chatLine.getLine().startsWith("/cheat"))
                    {
                        if(chatLine.getLine().equals("/cheat 0"))
                        {
                            TempUserPokedata tup = list_clientpokedata.get(id);
                            for(PokeData pd : tup.getList_pokedata().values())
                            {
                                pd.setHp(pd.getMaxhp());
                            }
                        }
                    }
                    else {
                        for(Socket so_other : list_clients_chat.values())
                        {
                            try{
                                ObjectOutputStream oos = new ObjectOutputStream(so_other.getOutputStream());
                                oos.writeObject(chatLine);
                                oos.flush();
                            } catch( IOException ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex)
                {
                    break;
                }
            }
            System.err.println(" - Objects thread closed.");
            
        }
    }
    
    private class TCP_Thread extends Thread{
        int id = 0;
        Socket so;

        public TCP_Thread(int id, Socket so)
        {
            this.id = id;
            this.so = so;
            start();
            System.out.println("New TCP thread started!");
        }

        @Override
        public void run()
        {
            DataInputStream dis;
            DataOutputStream dos;
            String reply = "";
            while(true)
            {
                try {
                    dis = new DataInputStream(so.getInputStream());
                    String data = dis.readUTF();
                    String[] args = data.split(":");
                    String command = args[0];
                    //System.out.println(">>> received " + data);
                    switch (command) {
                        case "cs":
                            AcceptedClient ac = list_clients.get(Integer.parseInt(args[1]));
                            ac.setX(Integer.parseInt(args[2]));
                            ac.setY(Integer.parseInt(args[3]));
                            ac.setWalking(Integer.parseInt(args[4]));
                            ac.setZone(Integer.parseInt(args[5]));
                            ac.setSubzone(Integer.parseInt(args[6]));
                            list_clients.put(Integer.parseInt(args[1]), ac);
                            
                            TempUserLocation tul = list_clientlocations.get(id);
                            tul.setZone(ac.getZone());
                            tul.setSubzone(ac.getSubzone());
                            list_clientlocations.put(id, tul);
                            
                            manager.setUserZone(Integer.parseInt(args[1]), ac.getZone(), ac.getSubzone());
                            state_sendall("ss:" + args[1] + ":" + args[2] + ":" + args[3] + ":" + args[4] + ":" + args[5] + ":" + args[6]);
                            break;
//                        case "cp":
//                            try {
//                                ResultSet rs = statement.executeQuery("SELECT * FROM pokedata WHERE uid = " + id);
//                                String[] pokedata = new String[17];
//                                
//                                String replyer;
//                                while(rs.next())
//                                {
//                                    System.out.println("There is a new Pokemon!");
//                                    if(Integer.parseInt(rs.getString(17)) > 6)
//                                    {
//                                        break;
//                                    }
//                                    String nickname = rs.getString(3);
//                                    if(rs.getObject(3) == null) nickname = "";
//
//                                    pokedata[0] = rs.getString(2);
//                                    pokedata[1] = nickname;
//                                    pokedata[2] = rs.getString(4);
//                                    pokedata[3] = rs.getString(5);
//                                    pokedata[4] = rs.getString(6);
//                                    pokedata[5] = rs.getString(7);
//                                    pokedata[6] = rs.getString(8);
//                                    pokedata[7] = rs.getString(9);
//                                    pokedata[8] = rs.getString(10);
//                                    pokedata[9] = rs.getString(11);
//                                    pokedata[10] = rs.getString(12);
//                                    pokedata[11] = rs.getString(13);
//                                    pokedata[12] = rs.getString(14);
//                                    pokedata[13] = rs.getString(15);
//                                    pokedata[14] = rs.getString(16);
//                                    pokedata[16] = rs.getString(17);
//                                    
//                                    rs.close();
//                                    
//                                    rs = statement.executeQuery("SELECT expcurve FROM pokedex WHERE number = " + pokedata[0]);
//                                    rs.next();
//                                    
//                                    int expNeeded = ExpCurves.calcNeededExp(Integer.parseInt(pokedata[6]), rs.getInt(1));
//                                    pokedata[15] = "" + expNeeded;
//                                    
//                                    replyer = "syp:" + 
//                                        pokedata[0] + ":" + //pnr
//                                        pokedata[1] + ":" + //nickname
//                                        pokedata[2] + ":" + //atk1
//                                        pokedata[3] + ":" + //atk2
//                                        pokedata[4] + ":" + //atk3
//                                        pokedata[5] + ":" + //atk4
//                                        pokedata[6] + ":" + //lvl
//                                        pokedata[7] + ":" + //atk
//                                        pokedata[8] + ":" + //def
//                                        pokedata[9] + ":" + //sp atk
//                                        pokedata[10] + ":" + //sp def
//                                        pokedata[11] + ":" + //spd
//                                        pokedata[12] + ":" + //maxhp
//                                        pokedata[13] + ":" + //hp
//                                        pokedata[14] + ":" + //exp
//                                        pokedata[15] + ":" + // expneeded
//                                        pokedata[16]; // loc
//                                        
//
//                                    dos = new DataOutputStream(so.getOutputStream());
//                                    dos.writeUTF(replyer);
//
//                                }
//
//                            } catch (SQLException ex) {
//                                Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                            break;
                        case "cac":
                            int user = Integer.parseInt(args[1]);
                            int victim = Integer.parseInt(args[2]);
                            int pokemonloc = Integer.parseInt(args[3]);
                            int action = Integer.parseInt(args[4]);
                            int action_id = Integer.parseInt(args[5]);
                            if(action == 1)
                            {
                                /*
                                * attacker's lvl
                                * attacker's str
                                * attacker's spcstr
                                * attack's power
                                * defender's def
                                * defender's specdef
                                * sametype
                                * defender's type1
                                * defender's type2
                                * attack damage type
                                * 
                                */
                                
                                Attack attack = db_attackdex.get(list_clientpokedata.get(user).getPokeData(pokemonloc).getPatk(action_id));
                                

                                String attack_name = attack.getName();
                                int atk_damage = attack.getDamage();
                                int atk_type = attack.getType();
                                int atk_speed = attack.getSpeed();
                                int atk_acc = attack.getAccuracy();
                                int atk_effect = attack.getEffecttype();


                                PokeData pd_attacker = list_clientpokedata.get(user).getPokeData(pokemonloc);
                                int a_lvl = pd_attacker.getLvl();
                                int a_atk = pd_attacker.getAtk();
                                int a_spcatk = pd_attacker.getSpecial_atk();
                                int a_speed = pd_attacker.getSpd();
                                int a_type1 = db_pokedex.get(pd_attacker.getPnr()).getType1();
                                int a_type2 = db_pokedex.get(pd_attacker.getPnr()).getType2();

                                int victimloc = 1;
                                PokeData pd_victim = list_clientpokedata.get(victim).getPokeData(victimloc);
                                int v_def = pd_victim.getDef();
                                int v_spcdef = pd_victim.getSpecial_def(); 

                                boolean sametype = false;
                                if(a_type1 == atk_type || a_type2 == atk_type) sametype = true;

                                int result = 0;
                                System.out.println("sametype: " + sametype);

                                for(int i = 0; i < list_running_battles.size(); i++)
                                {
                                    Battle b = list_running_battles.get(i);
                                    int who = 0;
                                    if(b.id_challenger == user) who = 1;
                                    if(b.id_victim == user) who = 2;
                                    boolean hit = b.calcHit(who, atk_effect, atk_acc);
                                    int[] attacker, defender;
                                    
                                    if(who == 1)
                                    {
                                        attacker = b.challengerModValues;
                                        defender = b.victimModValues;
                                    }
                                    else
                                    {
                                        defender = b.challengerModValues;
                                        attacker = b.victimModValues;
                                    }
                                    
                                    if(atk_effect == 1)
                                    {
                                        //System.out.println(user + " attacked while its attack was multiplied by " + b.atkdef_modifier_table.get(b.challengerModValues[0]));
                                        if(hit) result = BattleDamageCalc.calcDamage(a_lvl, a_atk * b.atkdef_modifier_table.get(attacker[0]), a_spcatk * b.atkdef_modifier_table.get(attacker[2]), atk_damage, v_def * b.atkdef_modifier_table.get(defender[1]), v_spcdef * b.atkdef_modifier_table.get(defender[3]), sametype, 234234, 234234, atk_effect, attack.isPhysical(), atk_type);
                                        else result = -1;
                                    }
                                    else if(atk_effect == 11)
                                    {
                                        System.out.println(user + " attacked while its attack was multiplied by " + b.atkdef_modifier_table.get(b.challengerModValues[0]));
                                        if(hit) result = BattleDamageCalc.calcDamage(a_lvl, a_atk * b.atkdef_modifier_table.get(attacker[0]), a_spcatk * b.atkdef_modifier_table.get(attacker[2]), atk_damage, v_def * b.atkdef_modifier_table.get(defender[1]), v_spcdef * b.atkdef_modifier_table.get(defender[3]), sametype, 234234, 234234, atk_effect, attack.isPhysical(), atk_type);
                                        else result = -1;

                                        if(Math.random() > 0.1) atk_effect = 1; 
                                    }

                                    

                                    if(who != 0 && result != -1)
                                    {

                                        if(atk_effect == 101)
                                        {
                                            if(defender[0] == -6) result = 0;
                                            else{
                                                b.setModifier(false, who, 0, -1);
                                                result = 1;
                                            }
                                        }
                                        else if(atk_effect == 102)
                                        {
                                            if(defender[1] == -6) result = 0;
                                            else{
                                                b.setModifier(false, who, 1, -1);
                                                result = 1;
                                            }
                                        }
                                        else if(atk_effect == 103)
                                        {
                                            if(defender[1] == 6) result = 0;
                                            else{
                                                b.setModifier(false, who, 5, -1);
                                                result = 1;
                                                System.out.println("Yay!");
                                            }
                                        }
                                        else if(atk_effect == 104)
                                        {
                                            if(attacker[1] == 6) result = 0;
                                            else{
                                                b.setModifier(true, who, 1, 1);
                                                result = 1;
                                            }
                                        }

                                    }

                                    if(who == 1) b.setChallengerAction(user + ":" + result + ":" + attack_name + ":" + atk_effect + ":" + atk_type, (int) Math.round(atk_speed * a_speed * b.atkdef_modifier_table.get(b.challengerModValues[4])));
                                    else if (who == 2) b.setVictimAction(user + ":" + result + ":" + attack_name + ":" + atk_effect + ":" + atk_type, (int) Math.round(atk_speed * a_speed * b.atkdef_modifier_table.get(b.victimModValues[4])));

                                }

                        }
                        break;
                    case "chd":
                        System.out.println("OH WOW CLIENT HAS DEFEATED. " + args[1] + " and " + args[2]);
                        int winner = id;
                        int winner_location = 1;
                        int enemy = Integer.parseInt(args[1]);
                        int killed_location = Integer.parseInt(args[2]);
                        int enemytype;
                        int baseexp;
                        int level;
                        try {
                            PokeData pd;
                            boolean wild;
                            if(args[1].equals("-1"))
                            {
                                pd = list_running_wildbattles.get(id).wildPokemon;
                                wild = true;
                            }
                            else
                            {
                                pd = list_clientpokedata.get(enemy).getPokeData(killed_location);
                                wild = false;
                            }
                            
                            enemytype = pd.getPnr();
                            level = pd.getLvl();
                            baseexp = db_pokedex.get(pd.getPnr()).getBaseexp();

                            System.out.println("Base exp I got is " + baseexp + ". Level I got is " + level);

                            dos = new DataOutputStream(so.getOutputStream());
                            int exp = Formula.calcExpReceived(wild, true, baseexp, false, level, 1);
                            dos.writeUTF("sexp:" + exp);

                            TempUserPokedata tup = list_clientpokedata.get(winner);
                            pd = tup.getPokeData(winner_location);
                            level = pd.getLvl();
                            baseexp = db_pokedex.get(pd.getPnr()).getBaseexp();
                            int mylevel = pd.getLvl();
                            if(mylevel < 100)
                            {
                                int myexp = pd.getExp();
                                int expcurve = db_pokedex.get(pd.getPnr()).getExpcurve();

                                int expneeded = Formula.calcNeededExp(mylevel, expcurve);

                                if (myexp + exp < expneeded)
                                {
                                    myexp += exp;
                                    
                                    pd.setExp(myexp);
                                } else
                                {
                                    myexp = (myexp + exp) - expneeded;
                                    mylevel++;
                                    
                                    pd.setExp(myexp);
                                    pd.setLvl(mylevel);
                                }
                                
                                pd.setEV(1, db_pokedex.get(enemytype).getEv_hp());
                                pd.setEV(2, db_pokedex.get(enemytype).getEv_attack());
                                pd.setEV(3, db_pokedex.get(enemytype).getEv_defense());
                                pd.setEV(4, db_pokedex.get(enemytype).getEv_specialattack());
                                pd.setEV(5, db_pokedex.get(enemytype).getEv_specialdefense());
                                pd.setEV(6, db_pokedex.get(enemytype).getEv_speed());
                                
                                int oldMaxhp = pd.getMaxhp();
                                pd.setMaxhp(Formula.calcStat("hp", pd.getNature(), pd.getHpIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel));
                                int newMaxhp = pd.getMaxhp();
                                int newHp = (int) Math.round((newMaxhp / oldMaxhp) * pd.getHp());
                                pd.setHp(newHp);
                                
                                pd.setAtk(Formula.calcStat("attack", pd.getNature(), pd.getAttackIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel));
                                pd.setDef(Formula.calcStat("defense", pd.getNature(), pd.getDefenseIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel));
                                pd.setSpecial_atk(Formula.calcStat("spatt", pd.getNature(), pd.getSpattIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel));
                                pd.setSpecial_def(Formula.calcStat("spdef", pd.getNature(), pd.getSpdefIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel));
                                pd.setSpd(Formula.calcStat("speed", pd.getNature(), pd.getSpeedIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel));
                                
                                
                                
                                tup.putPokedata(winner_location, pd);
                                list_clientpokedata.put(winner, tup);
                                
                                dos = new DataOutputStream(so.getOutputStream());
                                //sypl:loc:lvl:hp:maxhp:atk:def:spatk:spdef+spd
                                dos.writeUTF("sypl:" + winner_location + ":" + mylevel + ":" + pd.getHp() + ":" + pd.getMaxhp() + ":" + pd.getAtk() + ":" + pd.getDef() + ":" + pd.getSpecial_atk() + ":" + pd.getSpecial_def() + ":" + pd.getSpd());
                                //hp sturen klopt niet
                                /* ===========================================================
                                String update = "UPDATE pokedata ";
                                String set = "";
                                String where = "WHERE uid = " + winner + " AND location = " + winner_location + " ";
                                String stat = "";
                                System.out.println("Level up?");
                                System.out.println(myexp + " + " + exp + " > " + expneeded);
                                if (myexp + exp < expneeded)
                                {
                                    myexp += exp;
                                    set = "SET exp = " + myexp;
                                }
                                else
                                {
                                    myexp = (myexp + exp) - expneeded;
                                    mylevel++;
                                    set = "SET exp = " + myexp + ", lvl = " + mylevel + "";
                                    stat = ", hp = " + Formula.calcStat("hp", pd.getNature(), pd.getHpIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel)
                                            + ", atk = " + Formula.calcStat("attack", pd.getNature(), pd.getAttackIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel)
                                            + ", def = " + Formula.calcStat("defense", pd.getNature(), pd.getDefenseIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel)
                                            + ", special_atk = " + Formula.calcStat("spatt", pd.getNature(), pd.getSpattIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel)
                                            + ", special_def = " + Formula.calcStat("spdef", pd.getNature(), pd.getSpdefIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel)
                                            + ", spd = " + Formula.calcStat("speed", pd.getNature(), pd.getSpeedIV(), db_basestats.get(pd.getPnr()).getHp(), pd.getHpEV(), mylevel);
                                }

                                statement.executeUpdate(update + set + where);
                                /* =========================================================== */

                           }

                           for(int i = 0; i < list_running_battles.size(); i++)
                           {
                              if (id == list_running_battles.get(i).id_challenger || id == list_running_battles.get(i).id_victim)
                              {
                                  list_running_battles.remove(i);
                                  System.out.println("Battle removed from the list -----------------------------------------------------------");
                              }
                           }

                        } catch (Exception ex) {
                            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;  
                    case "cb":
                        if(args[1].equals("r")) // request
                        {
                            list_running_wildbattles.put(id, new BattleWild(pms, id, list_clients_pdp.get(id), list_clientlocations.get(id).getZone()));
                        }
                        if(args[1].equals("c")) // catch
                        {
                            if(list_running_wildbattles.containsKey(id))
                            {
                                TempUserPokedata tup = list_clientpokedata.get(id);
                                PokeData wild = list_running_wildbattles.get(id).wildPokemon;
                                
                                int newlocation = 1;
                                while(tup.containsKey(newlocation)){
                                    newlocation++;
                                }
                                
                                int hp = Integer.parseInt(args[2]);
                                
                                wild.setLocation(newlocation);
                                tup.putPokedata(newlocation, wild);
                                list_clientpokedata.put(id, tup);
                                
                                for(PokeData pd : list_clientpokedata.get(id).getList_pokedata().values())
                                {
                                    System.out.println("===");
                                    System.out.println(pd.getPnr() + " / " + pd.getLocation());
                                    System.out.println("===");
                                }
                                
                                System.out.println("Saved " + id + "'s Pokemon on loc " + newlocation);
                                
                                String send = "syp:" + 
                                    wild.getPnr() + ":" + //pnr
                                    wild.getName() + ":" + //nickname
                                    wild.getPatk1() + ":" + //atk1
                                    wild.getPatk2() + ":" + //atk2
                                    wild.getPatk3() + ":" + //atk3
                                    wild.getPatk4() + ":" + //atk4
                                    wild.getLvl() + ":" + //lvl
                                    wild.getAtk() + ":" + //atk
                                    wild.getDef() + ":" + //def
                                    wild.getSpecial_atk() + ":" + //sp atk
                                    wild.getSpecial_def() + ":" + //sp def
                                    wild.getSpd() + ":" + //spd
                                    wild.getMaxhp() + ":" + //maxhp
                                    hp + ":" + //hp
                                    0 + ":" + //exp
                                    db_pokedex.get(wild.getPnr()).getExpcurve() + ":" + // expcurve
                                    newlocation; // loc

                                dos = new DataOutputStream(so.getOutputStream());
                                dos.writeUTF(send);
                            }
                        }
                    }
                    
                    
                } catch (IOException ex) {
                    System.err.println("User disconnected.");
                    list_clients.remove(id);
                    list_clients_tcp.remove(id);
                    list_clients_udp.remove(id);
                    
                    manager.removeUser(id);
                    
                    saveUser(list_clientlocations.get(id), list_clientpokedata.get(id));
                    state_sendall("sl:" + id);
                    
                    list_clientlocations.remove(id);
                    list_clientpokedata.remove(id);
                    
                    
                    break;
                }
            }
            System.err.println(" - TCP thread closed.");
        }
    
        
    }

    
    private Runnable TCP_other = new Runnable() {

        @Override
        public void run() {
            while(true)
            {
                try {
                    Socket so = serversocket_other.accept();
                    
                    DataInputStream dis = new DataInputStream(so.getInputStream());
                    DataOutputStream dos = new DataOutputStream(so.getOutputStream());
                    
                    String in = dis.readUTF();
                    String[] args = in.split(":");
                    String command = args[0];
                    
                    if( command.equals("cr") ) // u, p, t ----- // id,u,t,p
                    {
                        ResultSet highestID = statement.executeQuery("SELECT MAX(uid) FROM users");
                        highestID.next();
                        int newID = highestID.getInt(1) + 1;
                        System.out.println("Thanks for joining, " + args[1] + "! Your password turned into '" + args[2] + "' and your type is " + args[3] + "!");
                        statement.executeUpdate("INSERT INTO users VALUES(" + newID + ",'" + args[1].toLowerCase() + "'," + Integer.parseInt(args[3]) + ",'" + args[2] + "','" + args[1] + "')");
                        
                        int charType = Integer.parseInt(args[3]);
                        boolean charTrainer = true; // true = trainer, false = rocket
                        if(charType == 4 || charType == 5 || charType == 6) 
                        {
                            charTrainer = false;
                        }
                        
                        int x, y, walking, zone, subzone;
                        if(charTrainer)
                        {
                            x = 704;
                            y = 560;
                            walking = 0;
                            zone = 3;
                            subzone = 0;
                        }
                        else
                        {
                            x = 704;
                            y = 2608;
                            walking = 4;
                            zone = 3;
                            subzone = 0;
                        }
                        
                        
                        statement.executeUpdate("INSERT INTO locations VALUES(" + newID + "," + x + "," + y + ",'" + walking + "','" + zone + "','" + subzone + "')");
                        
                        // for testing, every player gets his own pokemon
                        int pokemon = 1;
                        int a1 = 0, a2 = 0, a3 = 0, a4 = 0;
                        
                        if(charTrainer)
                        {
                            pokemon = 7;
                            a1 = 1;
                            a2 = 5;
                        }
                        else
                        {
                            pokemon = 41;
                            a1 = 53;
                        }
                        
                        if(args[1].toLowerCase().equals("steffelsteef"))
                        {
                            pokemon = 200;
                            a1 = 90;
                        } else if(args[1].toLowerCase().equals("reneex"))
                        {
                            pokemon = 4;
                            a1 = 1;
                            a2 = 4;
                        }
                        
                        int nature = Formula.randomNature();
                        int hpIV = Formula.randomIV();
                        int attackIV = Formula.randomIV();
                        int defenseIV = Formula.randomIV();
                        int speedIV = Formula.randomIV();
                        int spatkIV = Formula.randomIV();
                        int spdefIV = Formula.randomIV();

                        System.out.println("calc(\"hp\", " + nature + ", " + hpIV + ", " + db_basestats.get(pokemon).getHp() + ", " + 0 + ", " + 5 + ")");
                        int hp = Formula.calcStat("hp", nature, hpIV, db_basestats.get(pokemon).getHp(), 0, 5);
                        statement.executeUpdate("INSERT INTO pokedata VALUES(" + newID + ", " + pokemon + ", null, " + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ", 5, "
                                + Formula.calcStat("attack", nature, attackIV, db_basestats.get(pokemon).getAttack(), 0, 5) + ", "
                                + Formula.calcStat("defense", nature, defenseIV, db_basestats.get(pokemon).getDefense(), 0, 5) + ", "
                                + Formula.calcStat("spatt", nature, spatkIV, db_basestats.get(pokemon).getSpecial_attack(), 0, 5) + ", "
                                + Formula.calcStat("spdef", nature, spdefIV, db_basestats.get(pokemon).getSpecial_defense(), 0, 5) + ", "
                                + Formula.calcStat("speed", nature, speedIV, db_basestats.get(pokemon).getSpeed(), 0, 5) + ", "
                                + hp  + ", "
                                + hp  + ", "
                                + 0 + ", "
                                + 1 + ", "
                                + hpIV + ", "
                                + attackIV  + ", "
                                + defenseIV  + ", "
                                + speedIV  + ", "
                                + spatkIV + ", "
                                + spdefIV + ", "
                                + nature + ")");
                        
                        dos.writeUTF("srf");
                        so.close();
                    }
                    
                } catch (SQLException ex) {
                    Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
    
    /**
     * Query's the desired Pokemon from the database.
     * @param id the id of the desired user
     * @param loc the location of the desired pokemon
     * @return pokenr : nickname : atk1 : atk2 : atk3 : atk4 : lv : maxhp : hp : location
     */
    public PokeData getPokeDataPackageFromDatabase(int id, int loc)
    {
        System.out.println("Starting getPokeDataPackageFromDatabase(" + id + ", " + loc + ")");
        
        return list_clientpokedata.get(id).getPokeData(loc);
    }
    
    public int getPartySize(int id)
    {
        int i;
        for (i = 0; i < 6; i++)
        {
            if(getPokeDataPackageFromDatabase(id, i+1) == null) break;
        }
        System.out.println("Partysize of " + id + " is " + i);
            
        return i;
    }
    
    public void sendTCP(int id, String data)
    {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(list_clients_tcp.get(id).getOutputStream());
            dos.writeUTF(data);
            System.out.println("sendTCP(" + id + ", " + data);
        } catch (IOException ex) {
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void saveWorld()
    {
        Map<Integer, TempUserLocation> temptul = list_clientlocations;
        Map<Integer, TempUserPokedata> temptup = list_clientpokedata;
        
        System.out.println("======");
        System.out.println("Saving world...");
        saveUsers(temptul);
        System.out.println("---");
        savePokedata(temptup);
        System.out.println("World saved!");
        System.out.println("======");
    }
    
    public void saveUser(TempUserLocation tul, TempUserPokedata tup)
    {
        // Save Locations
        System.out.println("    saving u(" + tul.getID() + ")");
        String update = "UPDATE locations ";
        String set = "SET x = " + tul.getX() + ", y = " + tul.getY() + ", walking = " + tul.getWalking() + ", zone = " + tul.getZone() + ", subzone = " + tul.getSubzone() + " ";
        String where = "WHERE uid = " + tul.getID();
        try {
            statement.executeUpdate(update + set + where);
        } catch (SQLException ex) {
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Save Pokedata
        String c = ", ";
        try {
                statement.executeUpdate("DELETE FROM pokedata WHERE uid = " + tup.getID());
                Map<Integer, PokeData> list = tup.getList_pokedata();
                for(PokeData pd : list.values())
                {
                    String name = "null";
                    if (pd.getName().trim().isEmpty() == false)
                    {
                        name = "'" + pd.getName() + "'";
                    }
                    
                    String insert = "INSERT INTO pokedata VALUES(";
                    String values = tup.getID() + c +
                            pd.getPnr() + c +
                            name + c +
                            pd.getPatk1() + c +
                            pd.getPatk2() + c +
                            pd.getPatk3() + c +
                            pd.getPatk4() + c +
                            pd.getLvl() + c +
                            pd.getAtk() + c +
                            pd.getDef() + c +
                            pd.getSpecial_atk() + c +
                            pd.getSpecial_def() + c +
                            pd.getSpd() + c +
                            pd.getMaxhp() + c +
                            pd.getHp() + c +
                            pd.getExp() + c +
                            pd.getLocation() + c +
                            pd.getHpIV() + c +
                            pd.getAttackIV() + c +
                            pd.getDefenseIV() + c +
                            pd.getSpeedIV() + c +
                            pd.getSpattIV() + c +
                            pd.getSpdefIV() + c +
                            pd.getNature() + ")";
                            statement.executeUpdate(insert + values);
                }
            } catch (SQLException ex) {
                Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
    }
    
    private void saveUsers(Map<Integer, TempUserLocation> temptul)
    {
        System.out.println("  Saving users...");
        
        for(TempUserLocation tul : temptul.values())
        {
            System.out.println("    saving u(" + tul.getID() + ")");
            String update = "UPDATE locations ";
            String set = "SET x = " + tul.getX() + ", y = " + tul.getY() + ", walking = " + tul.getWalking() + ", zone = " + tul.getZone() + ", subzone = " + tul.getSubzone() + " ";
            String where = "WHERE uid = " + tul.getID();
            try {
                statement.executeUpdate(update + set + where);
            } catch (SQLException ex) {
                Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        System.out.println("  Users saved!");
    }
    
    private void savePokedata(Map<Integer, TempUserPokedata> temptup)
    {
        System.out.println("  Saving pokedata...");
        String c = ", ";
        for(TempUserPokedata tup : temptup.values())
        {
            System.out.println("    saving u(" + tup.getID() + ")");
            try {
                statement.executeUpdate("DELETE FROM pokedata WHERE uid = " + tup.getID());
                Map<Integer, PokeData> list = tup.getList_pokedata();
                for(PokeData pd : list.values())
                {
                    String name = "null";
                    if (pd.getName().trim().isEmpty() == false)
                    {
                        name = "'" + pd.getName() + "'";
                    }
                    
                    String insert = "INSERT INTO pokedata VALUES(";
                    String values = tup.getID() + c +
                            pd.getPnr() + c +
                            name + c +
                            pd.getPatk1() + c +
                            pd.getPatk2() + c +
                            pd.getPatk3() + c +
                            pd.getPatk4() + c +
                            pd.getLvl() + c +
                            pd.getAtk() + c +
                            pd.getDef() + c +
                            pd.getSpecial_atk() + c +
                            pd.getSpecial_def() + c +
                            pd.getSpd() + c +
                            pd.getMaxhp() + c +
                            pd.getHp() + c +
                            pd.getExp() + c +
                            pd.getLocation() + c +
                            pd.getHpIV() + c +
                            pd.getAttackIV() + c +
                            pd.getDefenseIV() + c +
                            pd.getSpeedIV() + c +
                            pd.getSpattIV() + c +
                            pd.getSpdefIV() + c +
                            pd.getNature() + ")";
                    System.out.println("Going to add " + tup.getID() + "'s " + pd.getPnr() + " op locatie " + pd.getLocation());
                            statement.executeUpdate(insert + values);
                }
            } catch (SQLException ex) {
                Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
        System.out.println("  Pokedata saved!");
        
    }
    
    public Basestat getBasestatFromDatabase(int pnr)
    {
        return db_basestats.get(pnr);
    }
    
    
    
//    
//    
//    private Runnable udp_receive = new Runnable() {
//        byte[] buffer = new byte[1024];
//        DatagramPacket dp = new DatagramPacket(buffer, 1024);
//        
//        @Override
//        public void run() {
//            try{
//                socket.receive(dp);
//                
//                String data = new String(dp.getData(), 0, dp.getLength()).trim();
//                String[] args = data.split(":");
//                String command = args[0];
//                System.out.println(data);
//                
//                String reply = null;
//                try{
//                    reply = handleCommand(dp.getSocketAddress(), command, args);
//                } catch( Exception e ){
//                    System.err.println("Error while handling command: " + command);
//                    e.printStackTrace();
//                }
//                
//                if(reply != null){
//                    reply += "\n";
//                    DatagramPacket reply_packet = new DatagramPacket(reply.getBytes(), reply.length(), dp.getSocketAddress());
//                
//                    socket.send(reply_packet);
//                }
//            
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    };
//    
//    private Runnable chat_accept = new Runnable() {
//
//        @Override
//        public void run() {
//            while(true)
//            {
//                try {
//                    Socket accepted_socket = serversocket_chat.accept();
//                    
//                    DataInputStream dis = new DataInputStream(accepted_socket.getInputStream());
//                    
//                    
//                    
//                    list_chats
//                    
//                    
//                } catch (IOException ex) {
//                    Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
    
//    private Runnable state_accept = new Runnable(){
//        @Override
//        public void run()
//        {
//            DataInputStream dis;
//            DataOutputStream dos;
//            int nextId = 0;
//            
//            while( true )
//            {
//                try {
//                    final Socket socket = serversocket_states.accept();
//                    
//                    dis = new DataInputStream(socket.getInputStream());
//                    final String receiveddata = dis.readUTF(); // in username:charType:zone:subzone
//                    System.out.println("TCP in: " + receiveddata);
//                    
//                    for( AcceptedClient ac : list_clients.values() )
//                    {
//                        dos = new DataOutputStream(socket.getOutputStream());
//                        dos.writeUTF( "sn:" + ac.id + ":" + ac.getUsername() + ":" + ac.getCharType() + ":" + ac.getZone() + ":" + ac.getSubzone() ); // out everyone's info
//                    }
//                    
//                    String[] args = receiveddata.split(":");
//                    
//                    String username = args[1];
//                    int charType = Integer.parseInt(args[2]);
//                    int zone = Integer.parseInt(args[3]);
//                    int subzone = Integer.parseInt(args[4]);
//                    int id = nextId++;
//                    SocketAddress addr = socket.getRemoteSocketAddress();
//                    System.out.println("addr = " );
//                    AcceptedClient accepted = new AcceptedClient(null, addr, id, username, charType, zone, subzone);
//                    list_clients.put(id, accepted);
//                    list_states.put(socket, accepted);
//                    
//                    dos = new DataOutputStream(socket.getOutputStream());
//                    for( AcceptedClient ac : list_clients.values() )
//                    {
//                        dos.writeUTF( "sn:" + ac.id + ":" + ac.getUsername() + ":" + ac.getCharType() + ":" + ac.getZone() + ":" + ac.getSubzone() );
//                    }
//                    
//                    dos.writeUTF( "sw:0" );
//                    
//                    for( AcceptedClient ac : list_clients.values() )
//                    {
//                        try {
//                            if(ac.id != id)
//                            {
//                                String othercoord = "sc:" + ac.id + ":" + ac.getX() + ":" + ac.getY() + ":" + ac.getWalking();
//                                dos.writeUTF(othercoord);
//                                System.out.println("Just TCP-sent ID " + ac.id + " as a welcome");
//                            }
//                        } catch (IOException ex) {
//                            System.err.println("Error TCP-sending user ID's at welcome");
//                        }
//                    }
//                    
//                    
//                } catch (IOException ex) {
//                    Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                
//            }
//        }
//    };
//    
//    
//    
//    private Runnable chat_accept = new Runnable(){
//
//        @Override
//        public void run() {
//            DataInputStream dis;
//            DataOutputStream dos;
//            
//            
//            while (true){
//                try{
//                    final Socket socket = serversocket_chat.accept();
//                    
//                    dis = new DataInputStream(socket.getInputStream());
//                    final String received_username = dis.readUTF(); // receiving username
//                    
//                    for( Socket so : list_chats.keySet() )
//                    {
//                        dos = new DataOutputStream(so.getOutputStream());
//                        dos.writeUTF(received_username + " has joined the game."); // sending 
//                    }
//                    
//                    list_chats.put(socket, received_username);
//                    
//                    Runnable chat_receive = new Runnable() {
//                        @Override
//                        public void run() {
//                            Socket so = socket;
//                            String username = received_username;
//                            DataInputStream dis;
//                            DataOutputStream dos;
//                            while(true)
//                            {
//                                try {
//                                    dis = new DataInputStream(so.getInputStream());
//                                    String received = dis.readUTF();
//                                    
//                                    for( Socket soc : list_chats.keySet() )
//                                    {
//                                        dos = new DataOutputStream(soc.getOutputStream());
//                                        dos.writeUTF(received_username + ":   " + received); // sending 
//                                    }
//
//                                } catch (IOException ex) {
//                                    System.err.println("Error receiving message from " + username);
//                                    list_chats.remove(so);
//                                    break;
//                                }
//
//                            }
//                        }
//                    };
//                    
//                    new Thread(chat_receive).start();
//                    
//                } catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    };
//    
//    
//    
//    private void setup() throws IOException {
//        this.socket = new DatagramSocket(this.port[0], InetAddress.getLocalHost());
//        this.serversocket_chat = new ServerSocket(this.port[1], 0, InetAddress.getLocalHost());
//        this.serversocket_states = new ServerSocket(this.port[2], 0, InetAddress.getLocalHost());
//        
//        new Thread(state_accept).start();
//        new Thread(chat_accept).start();
//        manager = new Manager(this, InetAddress.getLocalHost().getHostAddress());
//    }
//    
//    public void run(){
//        try{
//            this.setup();
//        } catch (IOException e){
//            System.err.println("Unable to setup: " + e.getMessage());
//            return;
//        }
//    
//        byte[] buffer = new byte[1024];
//        DatagramPacket dp = new DatagramPacket(buffer, 1024);
//    
//        while(true){
//            try{
//                this.socket.receive(dp);
//                
//                String data = new String(dp.getData(), 0, dp.getLength()).trim();
//                String[] args = data.split(":");
//                String command = args[0];
//                System.out.println(data);
//                
//                String reply = null;
//                try{
//                    reply = handleCommand(dp.getSocketAddress(), command, args);
//                } catch( Exception e ){
//                    System.err.println("Error while handling command: " + command);
//                    e.printStackTrace();
//                }
//                
//                if(reply != null){
//                    reply += "\n";
//                    DatagramPacket reply_packet = new DatagramPacket(reply.getBytes(), reply.length(), dp.getSocketAddress());
//                
//                    this.socket.send(reply_packet);
//                }
//            
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }
//    
//    public void kick(int id)
//    {
//        SocketAddress sa = null;
//        for( AcceptedClient ac : list_clients.values() )
//        {
//            if( ac.id == id )
//            {
//                sa = ac.getTCPAddress();
//            }
//        }
//        if(sa != null)
//        {
//            String kickstring = "sK:0";
//            try {
//                DataOutputStream dos = new DataOutputStream(sa);
//                socket.send(kickdp);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } else {
//            System.out.println("Ja, what the.");
//        }
//        
//        
//    }
//    
//
//
//    private String handleCommand(SocketAddress saddr, String command, String[] args){
//        //System.out.println("Handlecommand received " + args[1]);
//        if(command.equals("cj")){ // client join I
//            /*
////            String username = args[1];
////            int charType = Integer.parseInt(args[2]);
////            int zone = Integer.parseInt(args[3]);
////            int subzone = Integer.parseInt(args[4]);
////            int id = this.nextId++;
////            SocketAddress addr = dp.getSocketAddress();
//            
//            //System.out.println("Player(" + id + ") " + username + " joined from " + dp.getAddress() + ", type " + charType);
//        
////            this.sendAll("sn:" + id + ":" + username + ":" + charType + ":" + zone + ":" + subzone); // server new player O
//            
////            this.list_clients.put(addr, new AcceptedClient(addr, id, username, charType, zone, subzone));
//            
////            try {
////                String yourID = "sw:" + id;
////                DatagramPacket IDdp = new DatagramPacket(yourID.getBytes(), yourID.length(), addr);
////                socket.send(IDdp); // server welcome O
////            } catch (IOException ex) {
////                System.err.println("Error sending welcome message.");
////                ex.printStackTrace();
////            }
//            
//            manager.addUser(id, new ManagerClient(addr, id, username, charType, zone, subzone));
//            
//            for( AcceptedClient ac : list_clients.values() )
//            {
//                try {
//                    if(ac.id != id)
//                    {
//                        String otherID = "sn:" + ac.id + ":" + ac.getUsername() + ":" + ac.getCharType() + ":" + ac.getZone() + ":" + ac.getSubzone();
//                        DatagramPacket IDdp = new DatagramPacket(otherID.getBytes(), otherID.length(), addr);
//                        socket.send(IDdp);
//                        String othercoord = "sc:" + ac.id + ":" + ac.getX() + ":" + ac.getY() + ":" + ac.getWalking();
//                        DatagramPacket coorddp = new DatagramPacket(othercoord.getBytes(), othercoord.length(), addr);
//                        socket.send(coorddp);
//                        System.out.println("Just sent ID " + ac.id + " as a welcome");
//                    }
//                } catch (IOException ex) {
//                    System.err.println("Error sending user ID's at welcome");
//                }
//            }
//        
//        */
//        } else if( command.equals("cX") ){ // client exited I | id : type
////            AcceptedClient c = this.list_clients.get();
////            
////            
////            sendAll("sD:" + c.id + ":" + Integer.parseInt(args[1]));
////            
////            this.list_clients.remove(dp.getSocketAddress());
////            manager.removeUser(c.id);
//        } else if(command.equals("ccTCP")){
//            AcceptedClient c = null;
//            for( AcceptedClient ac : list_clients.values() )
//            {
//                if( ac.getTCPAddress() == saddr)
//                {
//                    c = ac;
//                }
//            }
//            if(c != null)
//            {
//                //System.out.println("AcceptdClient c = (" + c.getAddress() + ", " + c.id + ", " + c.getUsername() + ", " + c.getCharType());
//                c.setX(Integer.parseInt(args[1]));
//                c.setY(Integer.parseInt(args[2]));
//                c.setWalking(Integer.parseInt(args[3]));
//
//                sendAllTCP("scTCP:" + c.id + ":" + c.getX() + ":" + c.getY() + ":" + c.getWalking()); // server coords-update O
//
//                this.list_clients.put(c.id, c);
//            } else {
//                System.err.println("Error handling command ccTCP.");
//            }
//        } else if(command.equals("cc")){ // client coords-update I | x : y : walking
//            AcceptedClient c = null;
//            for( AcceptedClient ac : list_clients.values() )
//            {
//                if( ac.getUDPAddress() == saddr )
//                {
//                    c = ac;
//                }
//            }
//            if(c != null)
//            {
//                //System.out.println("AcceptdClient c = (" + c.getAddress() + ", " + c.id + ", " + c.getUsername() + ", " + c.getCharType());
//                c.setX(Integer.parseInt(args[1]));
//                c.setY(Integer.parseInt(args[2]));
//                c.setWalking(Integer.parseInt(args[3]));
//
//                sendAllUDP("sc:" + c.id + ":" + c.getX() + ":" + c.getY() + ":" + c.getWalking()); // server coords-update O
//
//                this.list_clients.put(c.id, c);
//            } else {
//                System.err.println("Error handling command cc.");
//            }
//            
//            
//        } else if(command.equals("cz")){
//            AcceptedClient c = null;
//            for( AcceptedClient ac : list_clients.values() )
//            {
//                if( ac.getTCPAddress() == saddr )
//                {
//                    c = ac;
//                }
//            }
//            if(c != null)
//            {
//
//                c.setX(Integer.parseInt(args[1]));
//                c.setY(Integer.parseInt(args[2]));
//                c.setWalking(Integer.parseInt(args[3]));
//                c.setZone(Integer.parseInt(args[4]));
//                c.setSubzone(Integer.parseInt(args[5]));
//
//                sendAllTCP("sz:" + c.id + ":" + c.getX() + ":" + c.getY() + ":" + c.getWalking() + ":" + c.getZone() + ":" + c.getSubzone());
//
//                this.list_clients.put(c.id, c);
//
//                manager.setUserZone( c.id, c.getZone(), c.getSubzone() );
//            }
//        } else if(command.equals("cb")){
//            AcceptedClient c = null;
//            for( AcceptedClient ac : list_clients.values() )
//            {
//                if( ac.getTCPAddress() == saddr )
//                {
//                    c = ac;
//                }
//            }
//            if(c != null)
//            {
//                c.setBattling( Boolean.parseBoolean(args[1]) );
//
//                sendAllTCP( "sb:" + c.id + ":" + c.getBattling() );
//
//                this.list_clients.put( c.id, c );
//            }
//        }
//    
//        return null;
//    }   
//    
//    private void sendAllUDP(String data){
//        //System.out.println("sendAll received " + data);
//        DatagramPacket reply_packet = new DatagramPacket(data.getBytes(), data.length());
//        
//        for(AcceptedClient c: this.list_clients.values()){
//            reply_packet.setSocketAddress(c.getUDPAddress());
//            try{
//                this.socket.send(reply_packet);
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }
//    
//    private void sendAllTCP(String data){
//        //System.out.println("sendAll received " + data);
//        DatagramPacket reply_packet = new DatagramPacket(data.getBytes(), data.length());
//        
//        for(AcceptedClient c: this.list_clients.values()){
//            reply_packet.setSocketAddress(c.getTCPAddress());
//            try{
//                this.socket.send(reply_packet);
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PokeMultiplayerServer app = new PokeMultiplayerServer();
        //app.run();
    }

}
