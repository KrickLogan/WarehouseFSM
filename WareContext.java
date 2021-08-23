import javax.swing.*;
import java.awt.event.*;
import backend.*;

public class WareContext {
  
  private static int currentState;
  private static Warehouse warehouse;
  private static WareContext context;
  private int currentUser;
  private String userID;
  private static JFrame WareFrame; 
  public static final int IsClient = 0;
  public static final int IsClerk = 1;
  public static final int IsManager = 2;
  private static WareState[] states;
  private int[][] nextState;


  private Boolean retrieve() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println("The warehouse has been successfully retrieved from the file WarehouseData \n" );
        warehouse = tempWarehouse;
        return true;
      } else {
        System.out.println("File doesn't exist; creating new warehouse" );
        warehouse = Warehouse.instance();
        return false;
      }
    } catch(Exception cnfe) {
      cnfe.printStackTrace();
    }
    return false;
  }

  public void setLogin(int code)
  {currentUser = code;}

  public void setUser(String uID)
  { userID = uID;}

  public int getLogin()
  { return currentUser;}

  public String getUser()
  { return userID;}

  public JFrame getFrame()
  { return WareFrame;}

  private WareContext() { //constructor
    // Create JFrame Window
    WareFrame = new JFrame("Warehouse GUI");
	  WareFrame.addWindowListener(new WindowAdapter()
    { public void windowClosing(WindowEvent e) {System.exit(0);} });
    WareFrame.setSize(400,300);
    WareFrame.setLocation(400, 400);

    // Ask to load saved WarehouseData file
    int result = JOptionPane.showConfirmDialog(WareFrame,
      "Look for saved data and use it?",
      "Warehouse System",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    
    if(result == JOptionPane.YES_OPTION) {
      if (retrieve()) {
        JOptionPane.showMessageDialog(WareFrame,"The warehouse has been successfully retrieved from the file WarehouseData.", "Warehouse System", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(WareFrame,"WarehouseData file doesn't exist.\nCreating new warehouse.", "Warehouse System", JOptionPane.WARNING_MESSAGE);
      }
    } else if (result == JOptionPane.NO_OPTION) {
      warehouse = Warehouse.instance();
    }

    // set up the FSM and transition table;
    states = new WareState[6];
    states[0] = ClientState.instance();
    states[1] = ClerkState.instance();
    states[2] = ManagerState.instance();
    states[3] = LoginState.instance();
    states[4] = ShoppingCartState.instance();
    states[5] = QueryClientState.instance();
    nextState = new int[6][6];
    nextState[0][0] = 3;nextState[0][1] = 1;nextState[0][2] = 2;nextState[0][3] = 3;nextState[0][4] = 4;nextState[0][5] = -2; //ClientState transitions
    nextState[1][0] = 3;nextState[1][1] = 0;nextState[1][2] = 2;nextState[1][3] = 3;nextState[1][4] = 5;nextState[1][5] = -2; //ClerkState transitions
    nextState[2][0] = 3;nextState[2][1] = 0;nextState[2][2] = 1;nextState[2][3] =-2;nextState[2][4] =-2;nextState[2][5] = -2; //ManagerState transitions
    nextState[3][0] = 0;nextState[3][1] = 1;nextState[3][2] = 2;nextState[3][3] =-1;nextState[3][4] =-2;nextState[3][5] = -2; //LoginState transitions
    nextState[4][0] = 0;nextState[4][1] =-2;nextState[4][2] =-2;nextState[4][3] =-2;nextState[4][4] =-2;nextState[4][5] = -2; //ShoppingCartState transitions
    nextState[5][0] = 1;nextState[5][1] =-2;nextState[5][2] =-2;nextState[5][3] =-2;nextState[5][4] =-2;nextState[5][5] = -2; //QueryCleintState transitions
    currentState = 3;
  }

  public void changeState(int transition)
  {
    // System.out.println("current state " + currentState + " \n \n "); //debugging, can be commented out
    currentState = nextState[currentState][transition];
    if (currentState == -2) {
      System.out.println("Error has occurred");
      terminate();
    }
    if (currentState == -1) {
      terminate();
    }
    // System.out.println("current state " + currentState + " \n \n "); //debugging, can be commented out
    states[currentState].run();
  }

  private void terminate() {
    // Ask to save data to WarehouseData file
    int result = JOptionPane.showConfirmDialog(WareFrame,
      "Save Warehouse Data?",
      "Warehouse System",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    
    if(result == JOptionPane.YES_OPTION) {
      if (Warehouse.save()) {
        JOptionPane.showMessageDialog(WareFrame,"The warehouse has been successfully saved in the file WarehouseData.\nGoodbye.", "Warehouse System", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(WareFrame,"There has been an error in saving.\nGoodbye.", "ERROR", JOptionPane.WARNING_MESSAGE);
      }
    } else if (result == JOptionPane.NO_OPTION) {
      JOptionPane.showMessageDialog(WareFrame,"Goodbye.", "Warehouse System", JOptionPane.PLAIN_MESSAGE);
      warehouse = Warehouse.instance();
    }
    System.exit(0); // exit
  }

  public static WareContext instance() {
    if (context == null) {
       System.out.println("calling constructor");
      context = new WareContext();
    }
    return context;
  }

  public void process(){
    states[currentState].run();
  }
  
  public static void main (String[] args){
    WareContext.instance().process(); 
  }
}
