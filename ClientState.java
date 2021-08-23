import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class ClientState extends WareState implements ActionListener {
  private static ClientState clientState;
  private static Warehouse warehouse;

  private JFrame frame;
  private AbstractButton clientDetailsButton, transactionsButton, waitlistButton, productsButton,
    modifyCartButton, placeOrderButton, exitButton;

  private ClientState() {
    warehouse = Warehouse.instance();
  }

  public static ClientState instance() {
    if (clientState == null) {
      return clientState = new ClientState();
    } else {
      return clientState;
    }
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource().equals(this.clientDetailsButton))
      this.showClientDetails();
    else if (event.getSource().equals(this.transactionsButton)) 
      this.showTransactions();
    else if (event.getSource().equals(this.waitlistButton)) 
      this.showWaitlist();
    else if (event.getSource().equals(this.productsButton)) 
      this.showProducts();
    else if (event.getSource().equals(this.modifyCartButton)) 
      this.modifyCart();
    else if (event.getSource().equals(this.placeOrderButton)) 
      this.placeOrder();
    else if (event.getSource().equals(this.exitButton)) 
      this.logout(); 
  } 

  public void clear() { //clean up stuff
    frame.getContentPane().removeAll();
    frame.paint(frame.getGraphics());   
  }

  public void showClientDetails() {
    String id = WareContext.instance().getUser();
    Client client = warehouse.getClientById(id);
    String clientDetails = "Client ID: " + client.getClientId() + ", First Name: " + client.getFirstName() +
      ", Last Name: " + client.getLastName() + ", Address: " + client.getAddress() + ", Balance: $" + client.getBalance();
    
    JOptionPane.showMessageDialog(frame, clientDetails, "Client Details", JOptionPane.INFORMATION_MESSAGE);
  }

  public void showTransactions() {
    String id = WareContext.instance().getUser();
    Client client = warehouse.getClientById(id);
    Iterator<Transaction> tIterator = client.getTransactionList().getTransactions();

    // if transaction list is empty notify user
    if (!tIterator.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No transactions were found.");
    } else { // else display table of Transactions
      
      Object[][] data = new Object[client.getTransactionList().size()][3];
      int rowCounter = 0;

      tIterator = client.getTransactionList().getTransactions();
      while(tIterator.hasNext()) {
        Transaction nextTrans = tIterator.next();
        data[rowCounter][0] = nextTrans.getDescription();
        data[rowCounter][1] = nextTrans.getDate();
        data[rowCounter][2] = nextTrans.getAmount();
        rowCounter++;
      }

      String[] columnNames = {"Description", "Date", "Total"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Transactions");
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

  public void showWaitlist() {
    String id = WareContext.instance().getUser();
    Client client = warehouse.getClientById(id);
    Iterator<WaitItem> iter = warehouse.getWaitlist();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No waitlisted items were found.");
    } else { // else display table
      
      Object[][] data = new Object[Waitlist.instance().size()][3];
      int rowCounter = 0;

      iter = Warehouse.instance().getWaitlist();
      while(iter.hasNext()) {
        WaitItem next = iter.next();
        if(client.equals(next.getClient().getClientId())) {
          data[rowCounter][0] = next.getOrderFilled();
          data[rowCounter][1] = next.getProduct().getId();
          data[rowCounter][2] = next.getQuantity();
          rowCounter++;
        }
      }

      String[] columnNames = {"Order Filled Status", "Product ID", "Quantity"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Waitlisted Orders");
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

  public void showProducts() {
    Iterator<Product> iter = warehouse.getProducts();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No products were found.");
    } else { // else display table
      
      Object[][] data = new Object[ProductList.instance().size()][3];
      int rowCounter = 0;

      iter = warehouse.getProducts();
      while(iter.hasNext()) {
        Product next = iter.next();
        data[rowCounter][0] = next.getId();
        data[rowCounter][1] = next.getName();
        data[rowCounter][2] = next.getSalePrice();
        rowCounter++;
      }

      String[] columnNames = {"Product ID", "Product Name", "Sale Price"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Products");
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

  public void modifyCart() {
    (WareContext.instance()).changeState(4); // transition to ShoppingCartState with code 4
  }

  public void placeOrder() {
    String clientId = WareContext.instance().getUser();
    Client client = warehouse.getClientById(clientId);

    Iterator<ShoppingCartItem> cartIterator = client.getShoppingCart().getShoppingCartProducts();
    if (!cartIterator.hasNext()) {
      JOptionPane.showMessageDialog(frame, "Shopping Cart is empty.");
    } else {
      int result = JOptionPane.showConfirmDialog(frame,
      "Shopping Cart Total: $" + client.getShoppingCart().getTotalPrice() +
      "\nPlace Order?",
      "Place Order",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    
      if(result == JOptionPane.YES_OPTION) {
        if (warehouse.placeOrder(clientId)) {
          JOptionPane.showMessageDialog(frame,"Order placed successfully.", "Place Order", JOptionPane.INFORMATION_MESSAGE);
        } else {
          JOptionPane.showMessageDialog(frame,"There was a problem placing your order.", "ERROR", JOptionPane.WARNING_MESSAGE);
        }
      } else if (result == JOptionPane.NO_OPTION) {
        JOptionPane.showMessageDialog(frame,"Canceled, order was not placed.", "Place Order", JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  public void run() {
    frame = WareContext.instance().getFrame();
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(new FlowLayout());
    clientDetailsButton = new JButton("View Client Details");
    transactionsButton = new JButton("View Transactions");
    waitlistButton = new JButton("View Waitlist");
    productsButton = new JButton("View Product List");
    modifyCartButton = new JButton("Shopping Cart Menu");
    placeOrderButton = new JButton("Place Order");
    exitButton = new JButton("Logout");  
    clientDetailsButton.addActionListener(this);
    transactionsButton.addActionListener(this);
    waitlistButton.addActionListener(this);
    productsButton.addActionListener(this);
    modifyCartButton.addActionListener(this);
    placeOrderButton.addActionListener(this);
    exitButton.addActionListener(this);
    frame.getContentPane().add(this.clientDetailsButton);
    frame.getContentPane().add(this.transactionsButton);
    frame.getContentPane().add(this.waitlistButton);
    frame.getContentPane().add(this.productsButton);
    frame.getContentPane().add(this.modifyCartButton);
    frame.getContentPane().add(this.placeOrderButton);
    frame.getContentPane().add(this.exitButton);
    frame.setTitle("Client Menu");
    frame.setVisible(true);
    frame.paint(frame.getGraphics()); 
    frame.toFront();
    frame.requestFocus();
  }

  public void logout()
  {
    if ((WareContext.instance()).getLogin() == WareContext.IsClient)
       { //system.out.println(" going to login \n ");
         (WareContext.instance()).changeState(0); // exit to login with a code 0
        }
    else if (WareContext.instance().getLogin() == WareContext.IsClerk)
       {  //system.out.println(" going to clerk \n");
        (WareContext.instance()).changeState(1); // exit to clerk with a code 0
       }
    else if (WareContext.instance().getLogin() == WareContext.IsManager)
       {  //system.out.println(" going to manager \n");
        (WareContext.instance()).changeState(2); // exit to manager with a code 0
       }
    else 
       (WareContext.instance()).changeState(3); // exit code 3, indicates error
  }
 
}
