package lozano.com.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
/**
 * @author Jose Manuel Lozano Serrano ID 11033743
 * This class is needed to create a private chat room
 * 
 */
public class ClientPrivateChat extends JFrame  implements ActionListener
{
	//Global Variables
	private JPanel lowerPanel;
	private JTextArea chatPrivateTxtArea; //chatTxtArea messageTxtField
	private JTextField messagePrivateTxtField;
	private JButton sendBtn;
	private String friend;
	private String clientName;
	Client client;
   
	/**
    * Constructor
    * @param cliente
    * @param clientName
    */
	public ClientPrivateChat(Client cliente, String clientName)
	{
		
	    super("Private Window");
		this.client=cliente;
	    this.clientName = clientName;
	    messagePrivateTxtField = new JTextField(30);
	    sendBtn = new JButton("Send");
	    chatPrivateTxtArea = new JTextArea(); 
	    chatPrivateTxtArea.setEditable(false);
	    chatPrivateTxtArea.setLineWrap(true);
	    chatPrivateTxtArea.setWrapStyleWord(true);
	    //autosrcoll
	    DefaultCaret caret = (DefaultCaret)chatPrivateTxtArea.getCaret();//autosrcoll
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    messagePrivateTxtField.requestFocus();
	    messagePrivateTxtField.addActionListener(this);
	    sendBtn.addActionListener(this);
      
	    lowerPanel = new JPanel();
           lowerPanel.setLayout(new BorderLayout());
           lowerPanel.add(new JLabel("  Type your message::"),BorderLayout.NORTH);
           lowerPanel.add(messagePrivateTxtField, BorderLayout.CENTER);
           lowerPanel.add(sendBtn, BorderLayout.EAST);
      
	    setLayout(new BorderLayout());
	    add(new JScrollPane(chatPrivateTxtArea),BorderLayout.CENTER);
	    add(lowerPanel,BorderLayout.SOUTH);
       
	    friend="";
	    
	    //This add functionality to me closing button of the GUI
	    addWindowListener(new WindowListener()
	    {         
	       public void windowClosing(WindowEvent e)
	       {
	          closePrivateChatWindow();
	       }
	       public void windowClosed(WindowEvent e) {}         
	       public void windowOpened(WindowEvent e) {}
	       public void windowIconified(WindowEvent e) {}
	       public void windowDeiconified(WindowEvent e) {}
	       public void windowActivated(WindowEvent e) {}
	       public void windowDeactivated(WindowEvent e) {}
	        
	    });
	      
	    setSize(300,300);
	    setLocation(570,90); 
	    
     
	}//end constructor
	
	
	
	/**
	 * This method sets the label of the GUI with the name of the client chatting with
	 * @param String friend
	 */
    public void setFriend(String friend)
    {
    	this.friend=friend;
        setTitle(clientName + " chatting with " +friend);
        
    }//end method setFriend
    
    
    
    /**
     * This method hides the private chat window
     */
    private void closePrivateChatWindow() 
    {       
    	setVisible(false);      
    
    }//end method closePrivateChatWindow
    
    
    
    /**
     * This method shows the messages in the private chat window
     * @param msg
     */
    public void showTextInPrivateWindow(String msg)
    {
       this.chatPrivateTxtArea.append(msg+"\n");
        
    }//end method showTextInPrivateWindow
    
    
    
    /**
     * This method gives functionality to the send button (send a private message)
     */
   @Override
   public void actionPerformed(ActionEvent e) 
   {
	   String message = messagePrivateTxtField.getText(); 
	   if(message.trim().equals(""))
       {
     	  JOptionPane.showMessageDialog(null, "Sorry, the message can not be  null","Empty Message",JOptionPane.ERROR_MESSAGE);

       }
       else
       {	// if(The user open a private room with him/herself)
    	   if(clientName.equals(friend))
    	   {
    		   client.sendToOne(friend,message);
    		   messagePrivateTxtField.setText("");
    	   }
    	   else
    	   {
    		   showTextInPrivateWindow(clientName+">"+message);
    		   client.sendToOne(friend,message);
    		   messagePrivateTxtField.setText("");
    	   }
       }
      
   }//end method actionPerformed
   
   
}//end class
