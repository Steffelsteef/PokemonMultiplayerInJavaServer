/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pokemultiplayerserver;

import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author Gebruiker
 */
public class ScreenDatabaseHandler extends JFrame{
    ScreenDatabaseHandler sdh = null;
    
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    
    JScrollPane scrollPane;
    JList list_users;
    DefaultListModel list_model;
    Insets insets = null;
    
    JButton but_remove, but_rename, but_empty;
    
    
    public void setConnection()
    {
        String url = "jdbc:postgresql://localhost/pokemon";
        String user = "postgres";
        String password = "kl3hjJfa#@ad";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            //resultSet = statement.executeQuery("SELECT * FROM users");
            resultSet = statement.executeQuery("SELECT UID, chartype FROM users WHERE username = 'steffelsteef'");
            
            resultSet.next();
            System.out.println("Testing database... User 1 = (" + resultSet.getString(1) + ") " + resultSet.getString(2) + "!");
                //System.out.println(Integer.parseInt(resultSet.getString(1)) + ", " + resultSet.getString(2) + " - type " + Integer.parseInt(resultSet.getString(3)));
        }
        catch(SQLException ex){
            Logger.getLogger(PokeMultiplayerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setFrame()
    {
        list_model = new DefaultListModel();
        list_users = new JList(list_model);
        
        scrollPane = new JScrollPane(list_users);
        scrollPane.setBounds(50,50,400,700);
        
        but_remove = new JButton();
        but_remove.setBounds(50,10,100,30);
        but_remove.setText("Remove");
        but_remove.addActionListener(al_remove);
        
        but_rename = new JButton();
        but_rename.setBounds(200,10,100,30);
        but_rename.setText("Rename");
        
        but_empty = new JButton();
        but_empty.setBounds(350,10,100,30);
        but_empty.setText("Empty yet");
        
        add(scrollPane);
        add(but_remove);
        add(but_rename);
        add(but_empty);
        
        setLayout(null);
        pack();
        insets = getInsets();
        setSize(500 + insets.left + insets.right, 800 + insets.top + insets.bottom);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        requestFocusInWindow();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public void addInfo()
    {
        String query = "";
        try {
            query = "SELECT uid, showed_username FROM users ORDER BY uid ASC";
            resultSet = statement.executeQuery(query);
            
            
            while(resultSet.next())
            {
                list_model.addElement(resultSet.getInt(1) + ", " + resultSet.getString(2));
            }
            
            
        } catch (SQLException ex) {
            System.err.println("Error at query \"" + query + "\".");
            Logger.getLogger(ScreenDatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public ScreenDatabaseHandler()
    {
        sdh = this;
        
        setConnection();
        setFrame();
        addInfo();
    }
    
    public static void main(String[] args)
    {
        new ScreenDatabaseHandler();
    }
    
    ActionListener al_remove = new ActionListener() {

        
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String selected = (String) list_model.getElementAt(list_users.getSelectedIndex());
            String[] args = selected.split(", ");
            
            int hoi = JOptionPane.showConfirmDialog(sdh, "Are you sure to delete " + args[1] + "?", "That's a big thing to do!", 0, JOptionPane.YES_NO_OPTION);
            
            if(hoi == 0)
            {
                System.out.println("============");
                try {
                    System.out.println("Going to remove user '" + args[1] + "'...");
                    statement.executeUpdate("DELETE FROM users WHERE showed_username = '" + args[1] + "'");
                    System.out.println("Removed!");
                } catch (SQLException ex) {
                    System.out.println("Could not remove! :(");
                    Logger.getLogger(ScreenDatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("=---=");

                try {
                    System.out.println("Trying to remove pokedata from '" + args[1] + "'...");
                    statement.executeUpdate("DELETE FROM pokedata WHERE uid = " + args[0]);
                    System.out.println("Removed!");
                } catch (SQLException ex) {
                    System.out.println("Could not remove! :(");
                    Logger.getLogger(ScreenDatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                System.out.println("=---=");

                try {
                    System.out.println("Trying to remove locations from '" + args[1] + "'...");
                    statement.executeUpdate("DELETE FROM locations WHERE uid = " + args[0]);
                    System.out.println("Removed!");
                } catch (SQLException ex) {
                    System.out.println("Could not remove! :(");
                    Logger.getLogger(ScreenDatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                list_model.removeElementAt(list_users.getSelectedIndex());
                System.out.println("============");
                System.out.println("");
            }
           
        }
    };
}
