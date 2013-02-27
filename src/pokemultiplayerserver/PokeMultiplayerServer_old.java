///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package pokemultiplayerserver;
//
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.StringReader;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//import ser.DataPackage;
//
///**
// *
// * @author Gebruiker
// */
//public class PokeMultiplayerServer_old {
//    public String ip = "";
//    public int port = 2406;
//    public ServerSocket server;
//    
//    Manager manager;
//    
//    ArrayList<Socket> list_sockets = new ArrayList();
//    ArrayList<Integer> list_client_states = new ArrayList();
//    ArrayList<Integer> list_client_states_new = new ArrayList();
//    ArrayList<Boolean> list_data_requests = new ArrayList();
//    ArrayList<DataPackage> list_data_packages = new ArrayList();
//    
//    String threadState = "free";
//        
//    public PokeMultiplayerServer_old(){
//        
//        try{
//
//                ip = InetAddress.getLocalHost().getHostAddress() + ":" + port;
//
//                System.out.println("Attempting " + ip +":"+port);
//                server = new ServerSocket(port, 0, InetAddress.getLocalHost());
//                System.out.println("Done.");
//                new Thread(accept).start();
//            }
//            catch (IOException ex)
//            {
//                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
//                System.exit(0);
//            }
//        
//        
//        manager = new Manager(this, ip);
//        manager.addWindowListener(new WindowListener()
//		{
//			public void windowActivated(WindowEvent e) {}
//			public void windowClosed(WindowEvent e) {}
//			
//			@Override
//			public void windowClosing(WindowEvent e)
//			{
//                            System.out.println("WINDOW IS CLOSING! :O");
//                            int disconnect = 2;
//				while (list_sockets.size() != 0)
//				{
//					try
//					{
//						for (int i = 0; i < list_client_states.size(); i++)
//						{
//                                                    System.out.println("Trying to set " + i + " to state 2");
//							list_client_states.set(i, disconnect);
//						}
//					}
//					catch (Exception ex) {}
//				}
//				
//				System.exit(0);
//			}
//			
//			public void windowDeactivated(WindowEvent e) {}
//			public void windowDeiconified(WindowEvent e) {}
//			public void windowIconified(WindowEvent e) {}
//			public void windowOpened(WindowEvent e) {
//                            System.out.println("Window has opened.");
//                        }
//		});
//        manager.requestFocusInWindow();
//    }
//    
//    private Runnable accept = new Runnable(){
//
//        @Override
//        public void run() {
//            ObjectInputStream ois;
//            ObjectOutputStream oos;
//            
//            
//            new Thread(send).start();
//            new Thread(receive).start();
//            while (true){
//                try{
//                    Socket socket = server.accept();
//                    
//                    ois = new ObjectInputStream(socket.getInputStream());
//                    String acceptedUsername = (String) ois.readObject(); // receiving username
//                    
//                    oos = new ObjectOutputStream(socket.getOutputStream());
//                    oos.writeObject("Welcome to the NEWER server, " +acceptedUsername+"!"); // sending welcome
//                    
//                    int connectedstate = 0;
//                    
//                    list_client_states.add(connectedstate);
//                    list_client_states_new.add( -1);
//                    list_data_requests.add(true);
//                    list_data_packages.add(new DataPackage());
//                    list_sockets.add(socket);
//                            
//                    manager.addUser(acceptedUsername, socket.getInetAddress().getHostAddress(), (short) 0, (short) 0);
//                } catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    };
//    
////    private Thread send = new Thread(){
////
////        @Override
////        public void run() {
////            ObjectOutputStream oos;
////            
////            while(true){
////                for(int i = 0; i < list_sockets.size(); i++){
////                    try {
////                        if(list_client_states_new.get(i) != -1){
////                            list_client_states.set(i, (byte) list_client_states_new.get(i));
////                        } else{
////                            list_client_states.set(i, (byte) 0);
////                        }
////                        oos = new ObjectOutputStream(list_sockets.get(i).getOutputStream());
////                        oos.writeObject((Byte) list_client_states.get(i));
////                        
////                        oos = new ObjectOutputStream(list_sockets.get(i).getOutputStream());
////                        oos.writeObject(list_data_packages);
////                        
////                        /* een lijst die checkt welke chats deze persoon heeft ontvangen, en dan een forloopje die
////                         * in een andere lijst, die verstuurd wordt, de gemiste chats stopt
////                         */
////                        String chat = null;
////                        oos = new ObjectOutputStream(list_sockets.get(i).getOutputStream());
////                        oos.writeObject(chat);
////                        
////                    } catch (Exception ex) {
////                        ex.printStackTrace();
////                    }
////                    
////                }
////                try {
////                    sleep(2);
////                } catch (InterruptedException ex) {
////                    ex.printStackTrace();
////                }
////            }
////        }
////        
////    };
//    
//    private Runnable send = new Runnable(){
//        public void run(){
//            ObjectOutputStream oos;
//            int datatype = 0;
//            while(true){
//                for(int i = 0; i < list_sockets.size(); i++){
//                    try {
//                        if(list_data_requests.get(i) == true) datatype = 1;
//                        
//                        oos = new ObjectOutputStream(list_sockets.get(i).getOutputStream());
//                        
//                        oos.writeObject(list_client_states.get(i));
//                        
//                        oos.writeObject(list_sockets.size());
//                        
//                        for(int j = 0; j < list_sockets.size(); j++){
//                            oos.writeObject(datatype);
//                            oos.writeObject(list_data_packages.get(i).x);
//                            oos.writeObject(list_data_packages.get(i).y);
//                            oos.writeObject(list_data_packages.get(i).walking);
//                            if(datatype == 1){
//                                oos.writeObject(list_data_packages.get(i).username);
//                                oos.writeObject(list_data_packages.get(i).zone);
//                                oos.writeObject(list_data_packages.get(i).subzone);
//                                
//                            }
//                        }
//                        
//                        list_data_requests.set(i, false);
//                        
//                        
//                        
//                        
//                        
//                    } catch (IOException ex) {
//                        Logger.getLogger(PokeMultiplayerServer_old.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    
//                }
//            }
//        }
//    };
//    
//    private Runnable receive = new Runnable(){
//        public void run(){
//            ObjectInputStream ois;
//            while(true){
//                for(int i = 0; i < list_sockets.size(); i++){
//                    try {
//                        ois = new ObjectInputStream(list_sockets.get(i).getInputStream());
//                        int receive_state = (int) ois.readObject();
//                        
//                        int datatype = (int) ois.readObject();
//
//                        DataPackage dp = new DataPackage();
//                        dp.x = (int) ois.readObject();
//                        dp.y = (int) ois.readObject();
//                        dp.walking = (int) ois.readObject();
//                        
//                        if(datatype == 1){
//                            dp.username = (String) ois.readObject();
//                            dp.zone = (int) ois.readObject();
//                            dp.subzone = (int) ois.readObject();
//                        } else if (datatype == 0){
//                            dp.username = list_data_packages.get(i).username;
//                            dp.zone = list_data_packages.get(i).zone;
//                            dp.subzone = list_data_packages.get(i).subzone;
//                        }
//                        
//                        
//                        
//                        list_data_packages.set(i, dp);
//                                
//                            
//                        
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        System.out.println("\n\n\n");
//                    }
//                    
//                }
//            }
//        }
//    };
//    
////    private Thread receive = new Thread(){
////
////        @Override
////        public void run() {
////            ObjectInputStream ois;
////            
////            while(true){
////                for(int i = 0; i < list_sockets.size(); i++){
////                    try {
////                        ois = new ObjectInputStream(list_sockets.get(i).getInputStream()); // naar bufferedoutpuststream
////                        byte receive_state = (byte) ois.readObject();
////                        list_client_states.set(i, receive_state);
////                        
////                        
////                        ois = new ObjectInputStream(list_sockets.get(i).getInputStream());
////                        DataPackage dp = (DataPackage) ois.readObject();
////                        list_data_packages.set(i, dp);
////                        
////                        
////                        ois = new ObjectInputStream(list_sockets.get(i).getInputStream());
////                        String receive_line = (String) ois.readObject();
////                        if(receive_line != null){
////                            System.out.println("Thanks!");
////                        }
////                        
////                        if (receive_state == 1) { // Client Disconnected by User
////                            manager.removeUser(i);
////                            list_client_states.remove(i);
////                            list_client_states_new.remove(i);
////                            list_data_packages.remove(i);
////                            list_sockets.remove(i);
////                        }
////                        
////                        
////                    } catch (Exception ex) {
////                        manager.removeUser(i);
////                        list_client_states.remove(i);
////			list_client_states_new.remove(i);
////			list_data_packages.remove(i);
////			list_sockets.remove(i);
////                    }
////                    
////
////                }
////                try {
////                    sleep(2);
////                } catch (InterruptedException ex) {
////                    ex.printStackTrace();
////                }
////            }
////        }
////        
////    };
//    
//
//    public synchronized void edit(String tryState){
//        if(tryState.equals("read")){
//            while(threadState.equals("write")){
//                try {
//                    wait();
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            
//        }
//    }
//    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        PokeMultiplayerServer_old pms = new PokeMultiplayerServer_old();
//    }
//
//}
