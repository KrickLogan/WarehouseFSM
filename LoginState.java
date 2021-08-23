import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class LoginState extends WareState implements ActionListener {
  private JFrame frame;
  private AbstractButton clientButton, clerkButton, managerButton, exitButton;
  
  private static LoginState instance;
  private LoginState() {
      super();
  }

  public static LoginState instance() {
    if (instance == null) {
      instance = new LoginState();
    }
    return instance;
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource().equals(this.clientButton))
      this.client();
    else if (event.getSource().equals(this.clerkButton)) 
      this.clerk();
    else if (event.getSource().equals(this.managerButton)) 
      this.manager();
    else if (event.getSource().equals(this.exitButton)) 
      (WareContext.instance()).changeState(3); // exit successfully with transition code 3
  } 

  public void clear() { //clean up stuff
    frame.getContentPane().removeAll();
    frame.paint(frame.getGraphics());   
  }

  private void client(){
    SecuritySystem ss = new SecuritySystem();
    String user = JOptionPane.showInputDialog(frame,"Please input the client username: ");
    if (Warehouse.instance().getClientById(user) != null){
      String pass = JOptionPane.showInputDialog(frame,"Please input the client password: ");
      if (ss.verifyPassword(user, pass)) {
        (WareContext.instance()).setLogin(WareContext.IsClient);
        (WareContext.instance()).setUser(user.toString());
        clear();    
        (WareContext.instance()).changeState(0);
      } else {
        JOptionPane.showMessageDialog(frame,"Invalid client password.");
      }      
    } else {
      JOptionPane.showMessageDialog(frame,"Invalid client username.");
    }
  }

  private void clerk(){
    SecuritySystem ss = new SecuritySystem();
    String clerk = JOptionPane.showInputDialog(frame,"Please input the clerk username: ");
    if (clerk.equals("clerk")) { 
      String pass = JOptionPane.showInputDialog(frame,"Please input the clerk password: ");
      if (ss.verifyPassword(clerk, pass)){
        (WareContext.instance()).setLogin(WareContext.IsClerk);
        (WareContext.instance()).setUser("clerk");
        clear();     
        (WareContext.instance()).changeState(1);
      } else {
        JOptionPane.showMessageDialog(frame,"Invalid clerk password.");
      }
    } else {
      JOptionPane.showMessageDialog(frame,"Invalid clerk username.");
    }
  }

  private void manager(){
    SecuritySystem ss = new SecuritySystem();
    String manager = JOptionPane.showInputDialog("Please input the manager username: ");
    if (manager.equals("manager")) { 
      String pass = JOptionPane.showInputDialog("Please input the manager password: ");
      if (ss.verifyPassword(manager, pass)){
        (WareContext.instance()).setLogin(WareContext.IsManager);
        (WareContext.instance()).setUser("manager");
        clear();   
        (WareContext.instance()).changeState(2);
      } else {
        JOptionPane.showMessageDialog(frame,"Invalid manager password.");
      }
    } else {
      JOptionPane.showMessageDialog(frame,"Invalid manager username.");
    }
  } 

  public void run() {
    frame = WareContext.instance().getFrame();
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(new FlowLayout());
    clientButton = new JButton("Client");
    clerkButton =  new JButton("Clerk");
    managerButton =  new JButton("Manager");
    exitButton = new JButton("Exit");  
    clientButton.addActionListener(this);
    clerkButton.addActionListener(this);
    managerButton.addActionListener(this);
    exitButton.addActionListener(this);
    frame.getContentPane().add(this.clientButton);
    frame.getContentPane().add(this.clerkButton);
    frame.getContentPane().add(this.managerButton);
    frame.getContentPane().add(this.exitButton);
    frame.setTitle("Warehouse Login");
    frame.setVisible(true);
    frame.paint(frame.getGraphics()); 
    frame.toFront();
    frame.requestFocus();
  }
}
