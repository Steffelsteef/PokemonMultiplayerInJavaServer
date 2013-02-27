/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Gebruiker
 */
public class Manager extends JFrame{
    JButton btn_message, btn_disconnect;
    
    JTextField server_message;
    
    DefaultListModel list_clients_model;
    
    JPanel panel1, panel2, panel3, panel4;
    
    JList list_clients;
    JScrollPane list_clients_scrollpane;
    
    PokeMultiplayerServer server;
    
    Map<Integer, ManagerClient> list_clients_physical;
    
    public Manager(PokeMultiplayerServer pms, String ip){
        this.server = pms;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Sorry, this server is not runnable on your system :(", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        
        btn_message = new JButton();
        btn_message.setText("Send");
        btn_message.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
//                int selected = list_clients.getSelectedIndex();
//
//                if (list_sockets.size() > 0)
//                {
//                    try
//                    {
//                        mes++;
//                        chatLine.username = null;
//                        chatLine.line = server_message.getText();
//
//
//                        if(chatLine_queue.size() > 0){
//                            chatLine_queue.add(chatLine);
//                        } else{
//                            chatLine_queue.add(chatLine);
//                            chatLine_sent  = new ArrayList();
//                            for (int i = 0; i < list_sockets.size(); i++){
//                                chatLine_sent.add(i, false);
//                            }
//                        }
//
//                        server_message.setText("");
//                    }
//                    catch (Exception ex)
//                    {
//                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
//                        System.err.println(ex);
//                    }
//                }
            }
        });
        
        
        btn_disconnect = new JButton();
        btn_disconnect.setText("kick");
        btn_disconnect.setBounds(132, 0, 128, 32);
        btn_disconnect.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                server.saveWorld();
                //int selected = list_clients.getSelectedIndex();
                //System.out.println("selected = " + selected);
                //server.kick(selected);
                
                
//                int selected = list_clients.getSelectedIndex();
//                int kick = 1;
//                while (server.list_client_states.get(selected) != 1){
//                    try{
//                        server.list_client_states_new.set(selected, kick);
//                    }
//                    catch (Exception ex) {
//                        System.err.println("Error while disconnecting.");
//                        ex.printStackTrace();
//                    }
//                }
            }
        });
            
        server_message = new JTextField(1);
        server_message.setLayout(null);
        server_message.setBounds(0, 4, 260, 24);

        
        list_clients_model = new DefaultListModel();
        list_clients_physical = new HashMap<>();
        list_clients = new JList(list_clients_model);
        
        panel1 = new JPanel(); // kick-knop
        panel1.setLayout(null);
        panel1.add(btn_disconnect);

        add(panel1);



        panel2 = new JPanel(); // ip-adres
        panel2.setLayout(null);
        JLabel ip_label = new JLabel(ip);
        panel2.add(ip_label);

        add(panel2);



        panel3 = new JPanel(); // clientlijst
        panel3.setLayout(null);
        list_clients_scrollpane = new JScrollPane(list_clients);
        panel3.add(list_clients_scrollpane);

        add(panel3);

        

        panel4 = new JPanel();
        panel4.setLayout(null);
        panel4.add(server_message);
        panel4.add(btn_message);

        add(panel4);

        panel1.setBounds(4, 4, 392, 32); //1 kick
        panel3.setBounds(4, panel1.getHeight() + panel1.getY() + 4, 392, 600 - 108); //3 clientlijst
        panel4.setBounds(4, panel3.getHeight() + panel3.getY() + 4, 392, 32); //4 server message
        panel2.setBounds(4, panel4.getHeight() + panel4.getY() + 4, 392, 24); //2 ip adres


        

        list_clients_scrollpane.setBounds(0, 0, 392, panel3.getHeight());

        btn_message.setBounds(264, 4, 128, 24);

        ip_label.setSize(panel2.getWidth(), panel2.getHeight());
        ip_label.setHorizontalAlignment(JLabel.CENTER);
        setLayout(null);
        setTitle("Server - " + ip);
        
        URL url;
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(getClass().getResource("/img/masterball.png"));
        setIconImage(img);
        

        
        pack();
        Insets insets = getInsets();
        setSize(400 + insets.left + insets.right, 600 + insets.top + insets.bottom);
        setLocationRelativeTo(null);
        setVisible(true);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
    
    public void drawElements()
    {
        list_clients_model.removeAllElements();
        for( ManagerClient mc : list_clients_physical.values() )
        {
            list_clients_model.addElement(mc.id + ": " + mc.getUsername() + " - " + mc.getIP() + " - zone (" + mc.getZone() + ", " + mc.getSubzone() + ")");
        }
    }
    
    @Deprecated
    public void addUser(int id, String username, String ip, short zone, short subzone){
        list_clients_model.addElement(id +": " + username + " - " + ip + " - zone " + zone + "," + subzone);
        System.out.println("Added: " + list_clients_model.get(0));
    }
    
    public void addUser(int id, ManagerClient mc)
    {
        list_clients_physical.put(id, mc);
        list_clients_model.addElement(mc.id + ": " + mc.getUsername() + " - " + mc.getIP() + " - zone (" + mc.getZone() + ", " + mc.getSubzone() + ")");        
    }
    
    public void setUserZone(int id, int zone, int subzone)
    {
        ManagerClient mc = list_clients_physical.get(id);
        mc.setZone(zone);
        mc.setSubzone(subzone);
        
        list_clients_physical.put(id, mc);
        drawElements();

    }
    
    public void removeUser(int index){
        ManagerClient mc = list_clients_physical.get(index);
        list_clients_model.removeElement(mc.id + ": " + mc.getUsername() + " - " + mc.getIP() + " - zone (" + mc.getZone() + ", " + mc.getSubzone() + ")");
        list_clients_physical.remove(index);
        
    }
		
    
}

