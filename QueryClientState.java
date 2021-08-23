import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class QueryClientState extends WareState implements ActionListener {
  private static QueryClientState queryClientState = new QueryClientState();
  private static Warehouse warehouse;

  private JFrame frame;
  private AbstractButton showClientsButton, showOutstandingBalancesButton,
    showNoTransactionsButton, exitButton;

  private QueryClientState() {
    warehouse = Warehouse.instance();
  }

  public static QueryClientState instance() {
      return queryClientState;
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource().equals(this.showClientsButton))
      this.showClients();
    else if (event.getSource().equals(this.showOutstandingBalancesButton)) 
      this.showOutstandingBalances();
    else if (event.getSource().equals(this.showNoTransactionsButton)) 
      this.showNoTransactions();
    else if (event.getSource().equals(this.exitButton)) 
      this.logout(); 
  }

  public void showClients() {
    Iterator<Client> iter = warehouse.getClients();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No clients were found.");
    } else { // else display table
      
      Object[][] data = new Object[ClientList.instance().size()][5];
      int rowCounter = 0;

      iter = warehouse.getClients();
      while(iter.hasNext()) {
        Client next = iter.next();
        data[rowCounter][0] = next.getClientId();
        data[rowCounter][1] = next.getFirstName();
        data[rowCounter][2] = next.getLastName();
        data[rowCounter][3] = next.getAddress();
        data[rowCounter][4] = next.getBalance();
        rowCounter++;
      }

      String[] columnNames = {"Client ID", "First Name", "Last Name", "Address", "Balance"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Clients");
      f.setSize(750,500);
      f.setLocation(400, 400);
      JTable table = new JTable(data, columnNames) {
        private static final long serialVersionUID = 1L;
        public boolean isCellEditable(int row, int column) {                
                return false;               
        }
      };
      JScrollPane scrollPane = new JScrollPane(table);
      table.setFillsViewportHeight(true);
      f.getContentPane().add(scrollPane);
      f.setVisible(true);
      f.paint(f.getGraphics()); 
      f.toFront();
      f.requestFocus();
    }
  }

  public void showOutstandingBalances() {
    Iterator<Client> iter = warehouse.getClients();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No clients were found.");
    } else { // else display table
      
      Object[][] data = new Object[ClientList.instance().size()][5];
      int rowCounter = 0;

      iter = warehouse.getClients();
      while(iter.hasNext()) {
        Client next = iter.next();
        if(next.getBalance() < 0) {
          data[rowCounter][0] = next.getClientId();
          data[rowCounter][1] = next.getFirstName();
          data[rowCounter][2] = next.getLastName();
          data[rowCounter][3] = next.getAddress();
          data[rowCounter][4] = next.getBalance();
          rowCounter++;
        }
      }

      String[] columnNames = {"Client ID", "First Name", "Last Name", "Address", "Balance"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Clients with Outstanding Balance");
      f.setSize(750,500);
      f.setLocation(400, 400);
      JTable table = new JTable(data, columnNames) {
        private static final long serialVersionUID = 1L;
        public boolean isCellEditable(int row, int column) {                
                return false;               
        }
      };
      JScrollPane scrollPane = new JScrollPane(table);
      table.setFillsViewportHeight(true);
      f.getContentPane().add(scrollPane);
      f.setVisible(true);
      f.paint(f.getGraphics()); 
      f.toFront();
      f.requestFocus();
    }
  }

  public void showNoTransactions() {
    // Iterator<Client> allClients = warehouse.getClients();
    // System.out.println("\n  List of Clients with no transactions: \n");
    // while (allClients.hasNext()){
    //   Client client = allClients.next();
    //   Iterator<Transaction> trans = client.getTransactionList().getTransactions();
    //   if(!trans.hasNext()) {
    //     System.out.println(client.toString());
    //   }
    // }
    // System.out.println("\n  End of Clients with no transactions list. \n");
    Iterator<Client> iter = warehouse.getClients();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No clients were found.");
    } else { // else display table
      
      Object[][] data = new Object[ClientList.instance().size()][5];
      int rowCounter = 0;

      iter = warehouse.getClients();
      while(iter.hasNext()) {
        Client next = iter.next();
        Iterator<Transaction> trans = next.getTransactionList().getTransactions();
        if(!trans.hasNext()) {
          data[rowCounter][0] = next.getClientId();
          data[rowCounter][1] = next.getFirstName();
          data[rowCounter][2] = next.getLastName();
          data[rowCounter][3] = next.getAddress();
          data[rowCounter][4] = next.getBalance();
          rowCounter++;
        }
      }

      String[] columnNames = {"Client ID", "First Name", "Last Name", "Address", "Balance"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Clients with No Transactions");
      f.setSize(750,500);
      f.setLocation(400, 400);
      JTable table = new JTable(data, columnNames) {
        private static final long serialVersionUID = 1L;
        public boolean isCellEditable(int row, int column) {                
                return false;               
        }
      };
      JScrollPane scrollPane = new JScrollPane(table);
      table.setFillsViewportHeight(true);
      f.getContentPane().add(scrollPane);
      f.setVisible(true);
      f.paint(f.getGraphics()); 
      f.toFront();
      f.requestFocus();
    }
  }

  public void run() {
    frame = WareContext.instance().getFrame();
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(new FlowLayout());
    showClientsButton = new JButton("View Clients");
    showOutstandingBalancesButton = new JButton("View Clients with Outstanding Balances");
    showNoTransactionsButton = new JButton("View Clients with No Transactions");
    exitButton = new JButton("Return to Clerk Menu");  
    showClientsButton.addActionListener(this);
    showOutstandingBalancesButton.addActionListener(this);
    showNoTransactionsButton.addActionListener(this);
    exitButton.addActionListener(this);
    frame.getContentPane().add(this.showClientsButton);
    frame.getContentPane().add(this.showOutstandingBalancesButton);
    frame.getContentPane().add(this.showNoTransactionsButton);
    frame.getContentPane().add(this.exitButton);
    frame.setTitle("Query Clients Menu");
    frame.setVisible(true);
    frame.paint(frame.getGraphics()); 
    frame.toFront();
    frame.requestFocus();
  }

  public void logout()
  {
    (WareContext.instance()).changeState(0); // exit to ClerkState with a code 0
  }
 
}
