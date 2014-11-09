package lozano.com.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

/**
 * 
 * @author Jose Manuel Lozano Serrano ID 11033743
 * 
 * This class build the chat GUI and functionalities.
 *
 */


public class Client  implements ActionListener
{

	private  static String clientName;
	private static Socket connectionSocket;
	private static String serverIP;
	private static int port=1980;
	private static PrintWriter output;
	private static Scanner input;
	//ArrayList that stores the name of the users online
	private static ArrayList<String> usersOnline = new ArrayList<String>();
	String message;
    
	private JFrame window;
	private JPanel lowerPanel;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JSplitPane centralSplitPanel;
	private static JTextArea chatTxtArea;
    private JTextField messageTxtField;
    private JButton sendBtn;
    private JButton butPrivado;
    private static JLabel userNameLabel;
    private static JList usersOnlineJList;
    
    private static ClientPrivateChat privateChat;
    private static Client client;
    //ArrayList that stores the names of the users that a users is speaking in private chats.  
    private static ArrayList<String> friendsChattingWith = new ArrayList<String>();
    //ArrayList that stores the client private chat windows.
    private static ArrayList<ClientPrivateChat> privateChats = new ArrayList<ClientPrivateChat>();
   
 
    
    
    /**
     *  Constructor 
     */
    public Client() 
    {
    	buildGUI();
           
    }//end constructor

    
    /**
     * This method builds the Graphical User Interface 
     */
    
    public void buildGUI()
    {
    	 window = new JFrame("Chat Client");
         
    	 //This add functionality to me closing button of the GUI
         window.addWindowListener(new WindowListener()
         {         
            public void windowClosing(WindowEvent e)
            {
               output.println("1");
         	   try
         	   {	
					connectionSocket.close();
					System.out.println("Connection closed");
					System.exit(0);
         	   } 
         	   catch (IOException e1)
         	   {
					e1.printStackTrace();
         	   }
            }

				@Override
				public void windowActivated(WindowEvent e) {}
				@Override
				public void windowClosed(WindowEvent e) {}
				@Override
				public void windowDeactivated(WindowEvent e) {}
				@Override
				public void windowDeiconified(WindowEvent e) {}
				@Override
				public void windowIconified(WindowEvent e) {}
				@Override
				public void windowOpened(WindowEvent e) {}
         });
         
         messageTxtField = new JTextField(30);
         userNameLabel = new JLabel("User <<  >>");
         userNameLabel.setHorizontalAlignment(JLabel.CENTER);
         chatTxtArea = new JTextArea();             
         chatTxtArea.setColumns(25);
         DefaultCaret caret = (DefaultCaret)chatTxtArea.getCaret();//autosrcoll
         caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
         messageTxtField.addActionListener(this);
         sendBtn = new JButton("Send");
         sendBtn.addActionListener(this);
         usersOnlineJList=new JList();             
         butPrivado=new JButton("Private");
         butPrivado.addActionListener(this);     
                 
         chatTxtArea.setEditable(false);            
         chatTxtArea.setForeground(Color.BLUE);
         chatTxtArea.setBorder(javax.swing.BorderFactory.createMatteBorder(3,3,3,3,new Color(25,10,80)));	
 	     chatTxtArea.setLineWrap(true);
 	     chatTxtArea.setWrapStyleWord(true);

         lowerPanel = new JPanel();
         lowerPanel.setLayout(new BorderLayout());
            lowerPanel.add(new JLabel("  Type the message:"),BorderLayout.NORTH);
            lowerPanel.add(messageTxtField, BorderLayout.CENTER);
            lowerPanel.add(sendBtn, BorderLayout.EAST);
         
         rightPanel = new JPanel();
         rightPanel.setLayout(new BorderLayout());
            rightPanel.add(userNameLabel, BorderLayout.NORTH);
            rightPanel.add(new JScrollPane(chatTxtArea), BorderLayout.CENTER);
            rightPanel.add(lowerPanel,BorderLayout.SOUTH);
         
         leftPanel=new JPanel();
         leftPanel.setLayout(new BorderLayout());
           leftPanel.add(new JScrollPane(this.usersOnlineJList),BorderLayout.CENTER);
           leftPanel.add(this.butPrivado,BorderLayout.NORTH);
           
         centralSplitPanel=new JSplitPane();  
         centralSplitPanel.setDividerLocation(100);
         centralSplitPanel.setDividerSize(7);
         centralSplitPanel.setOneTouchExpandable(true);
           centralSplitPanel.setLeftComponent(leftPanel);
           centralSplitPanel.setRightComponent(rightPanel);
         
         
         window.setLayout(new BorderLayout());
         window.add(centralSplitPanel, BorderLayout.CENTER);   
                    
         messageTxtField.requestFocus();//request focus on txtField	
         
                     
         window.setSize(450, 430);
         window.setLocation(120, 90);
         window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
         window.setVisible(true);
    	
    }//end method buildGUI
        
 /**
  * Main method, creates and input from a JOptionPane into an String that  holds the clientName.
  * Then ,generates a new instance of the class Client by calling its constructor.
  * Makes and infinite loop where will be calling the method listen(waiting responses from the server)
  * @param args
  * @throws IOException
  */
    
   public static void main(String args[]) throws IOException
   {
        
	   client = new Client();
    	chatNickName();
    	
    	connection();
    	
    	while (true)
    	{
    		listen();
    		
    	}
    	  
    }//end main method
   
   
   
   /**
    * This method ensure that the nick name is not empty
    */
   
   private static void chatNickName()
   {
	  
	   clientName = JOptionPane.showInputDialog("Insert your Chat nickname\n Nickname must contain between 1 and 10 characters","");
	   //cancel button is pressed
	   if(clientName == null)
	    {
		   System.out.println("Cancel is pressed");
		   System.exit(0);

		}
	    
	    
	   setUserName(clientName);
	  //while nick name is empty or longer than 10 characters
	   while(clientName.trim().equals("") || clientName.length()>10)
	   {	
		    System.out.println("The name is empty");
			JOptionPane.showMessageDialog(null, "Sorry your Nick Name must contain between 1 and 10 characters","NickName Empty "
					+ "or longer than 10 characteres",JOptionPane.ERROR_MESSAGE);
			clientName= JOptionPane.showInputDialog("Insert your Chat nickname :","");
			setUserName(clientName);
			

	   }
	   
   }//end method chatNickName
      
   
   
   /**
     *This method listens the responses from the server 
     */  
   private static void listen()
   {

	   if (input.hasNext())
	   {
		   int option = Integer.parseInt(input.nextLine());
		   
		   switch (option)
		   {	 
		   		case 1: //Update the online list
		   		
		   			String userNames = input.nextLine();
		   			if (!usersOnline.contains(userNames))
		   			{
		   				usersOnline.add(userNames);
		   				updateOnlineList();
		   				
		   			} 
		   			break;
		   				   			
		   		case 2://receiving and display message 
		   			
		   			String message = input.nextLine();
		   			System.out.println(message);
		   			chatTxtArea.append(message + "\n");
		   			break;
		   			
		   		case 3:////deleting one user and updating one the online list
		   			
		   			String userToDelete = input.nextLine();
		   			usersOnline.remove(usersOnline.indexOf(userToDelete));
		   			chatTxtArea.append(  userToDelete + " >has left the chat \n");
		   			updateOnlineList();
		   			break;
		   	
		   		case 4: //Display private message
		   		
		   			String userWhoSentTheMessage = input.nextLine();
		   			String privateMessage = input.nextLine();
		   		
		   			if (friendsChattingWith.contains(userWhoSentTheMessage))
		   			{
		   				privateChats.get(friendsChattingWith.indexOf(userWhoSentTheMessage)).setVisible(true);
		   			
		   			}
		   			else
		   			{
		   				showPrivateChatWindow(userWhoSentTheMessage);
		   			}
		   			privateChats.get(friendsChattingWith.indexOf(userWhoSentTheMessage)).showTextInPrivateWindow(userWhoSentTheMessage + ">" + privateMessage);
		  			break;
		  			
		   		case 5://Server not Running
		   			
		   			JOptionPane.showMessageDialog(null, "ERROR:SERVER IS NOT RUNNING TRY TO CONNECT LATER \n The GUI will close "
		   					+ "after pressing OK. ","Connection Error",JOptionPane.ERROR_MESSAGE);
		    		System.exit(0);
		    		break;
		    	
		   		case 6://Duplicate name
						try
						{
							connectionSocket.close();
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
		   			JOptionPane.showMessageDialog(null, "ERROR: Duplicate name","Duplicate Name",JOptionPane.ERROR_MESSAGE);
		    		chatNickName();
		    		connection();
		   			break;
		   			
		   			
		   }//end swicth
		   
	   }//end if
	   
   }//end method listen
   
   
   
	/**
	 * This method update the users online list
	 */
	private static void updateOnlineList()
	{
		   for (int i = 0; i < usersOnline.size(); i++)
		   {
			   //make the onlineList safe without errors
			   SwingUtilities.invokeLater(new Runnable () 
			   {
				  // @Override
				   public void run()
				   {	
					   	usersOnlineJList.setModel(new AbstractListModel()
					   	{            
					   		@Override
					   		public int getSize() { return usersOnline.size(); }
					   		@Override
					   		public Object getElementAt(int i) { return usersOnline.get(i);}
					   	});
				   }
				   
			   });
			  
		   	}//end for loop
		   
	}//end method updateOnlineList
    
	
	/**
	 * This method connects the User the Server by using the Socket (name host and port number)
	 * @throws UnknownHostException
	 * @throws IOException
	 */
    private static void connection() 
    {	try
    	{
    		serverIP=InetAddress.getLocalHost().getHostName();
	    	connectionSocket = new Socket(serverIP, port);	
	    	output = new PrintWriter(connectionSocket.getOutputStream(),true);
	    	input = new Scanner(connectionSocket.getInputStream());
	    	output.println(clientName);
    	}
    	catch(UnknownHostException u)
    	{
    		System.out.println("Error: Problem with the name of the host");
    		
    		u.printStackTrace();
    	}
    	catch(IOException i)
    	{
			JOptionPane.showMessageDialog(null, "ERROR:THE SERVER IS NOT RUNNING","Connection Error",JOptionPane.ERROR_MESSAGE);
    		System.out.println("Error: with the connection");
    		i.printStackTrace();
    		System.exit(0);//close the gui if the server if not running
    	}
    
    }//end method connection
       
    
    
    /**
     * This method sets userNameLabel (chat's label) with the name of the user
     * @param String user
     */
    public  static void setUserName(String user)
    {
       userNameLabel.setText("User " +"<< " +user+ " >>");
       
    }//end method userNameLabel
    
    
    
    /**
     * This method shows in the client console the messages send in the chat
     * @param String message
     */ 
    public void showMessage(String message)
    {
       this.chatTxtArea.append(message+"\n");
       
    }//end method showMessage
    
    
    
    /**
     * This method gives functionality to the JButtons 
     *    
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        
      String comand=(String)evt.getActionCommand();
       
      	// If you press the button send or press enter after typing in the txtField
      if(evt.getSource()==this.sendBtn || evt.getSource()==this.messageTxtField)
       {	
          			sendTOALL( message);
          
       }
       //if you select a user to chat in private and press the button private
       else if(evt.getSource()==this.butPrivado)
       {
    	   
    	   privateChat();

       }//end else if
       
    }//end method actionPerformed
      
    
    
    /**
     * This method helps the client to create a private room session with a selected client   
     */
    private  void privateChat()
    {
    	   int pos=this.usersOnlineJList.getSelectedIndex();
           String selectedUser = (String) usersOnlineJList.getSelectedValue();
           System.out.println("pos: " + pos + " name of user: " + selectedUser);
           //user not chatting privately with himself
           if(pos>=0 && selectedUser != clientName)              
           {
         	 if (friendsChattingWith.contains(selectedUser))
         	 {
         		  privateChats.get(friendsChattingWith.indexOf(selectedUser)).setVisible(true);
         		  //privateChat.setVisible(true);
          	 } 
         	 else
         	 {
             	  showPrivateChatWindow(selectedUser);
              }

           }//end if
    	   
     }//end method privateChat
       
    
    
    /**
      * This method shows a private window when a user request to chat with the client in private.
      * It adds the gui and the friend chatting with to 2 ArrayList 
      * @param friend
      */
      private static void showPrivateChatWindow(String friend) 
      {
    	  privateChat = new ClientPrivateChat(client, clientName);
    	  privateChat.setFriend(friend);
    	  privateChat.setVisible(true);
    	  friendsChattingWith.add(friend);
    	  privateChats.add(privateChat);
    	  
      }//end method showPrivateChatWindow
      
      
     /**
      * This method  send a message to everybody connected 
      * @param message
      */
      public void sendTOALL(String message)
      {
            message = messageTxtField.getText();
            this.message=message;
             if(message.trim().equals(""))
             {
           	  JOptionPane.showMessageDialog(null, "Sorry, the message can not be  null","Empty Message",JOptionPane.ERROR_MESSAGE);

             }
             else
             {
           	  messageTxtField.setText("");
   	          output.println("2");
   	          output.println(clientName);
   	          output.println(message);
           	  
             }
    	  
      }//end method sendToAll
      
      
       /**
        * This method sends a message in a private room
        * @param friend
        * @param message
        */
       public void sendToOne(String friend, String message)
       {
    	   //System.out.println("Hello from sendToOne");
    	   output.println("3");
    	   output.println(friend);
    	   output.println(message);
    	   
       }//end method sentToOne
       
    
}//end class
