import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class ClerkState extends WareState implements ActionListener {
  private static ClerkState clerkState = new ClerkState();
  private static Warehouse warehouse;

  private JFrame frame;
  private AbstractButton addClientButton, showProductsButton, becomeClientButton, queryClientsButton,
    showWaitlistButton, receiveShipmentButton, exitButton;

  private ClerkState() {
    warehouse = Warehouse.instance();
  }

  public static ClerkState instance() {
      return clerkState;
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource().equals(this.addClientButton))
      this.addClient();
    else if (event.getSource().equals(this.showProductsButton)) 
      this.showProducts();
    else if (event.getSource().equals(this.becomeClientButton)) 
      this.becomeClient();
    else if (event.getSource().equals(this.queryClientsButton)) 
      this.QueryClients();
    else if (event.getSource().equals(this.showWaitlistButton)) 
      this.showProductsWaitlist();
    else if (event.getSource().equals(this.receiveShipmentButton)) 
      this.recieveShipment();
    else if (event.getSource().equals(this.exitButton)) 
      this.logout(); 
  } 

  public void addClient() {
    String firstName = JOptionPane.showInputDialog(frame,"Please input the Client's first name: ");
    String lastName = JOptionPane.showInputDialog(frame,"Please input the Client's last name: ");
    String address = JOptionPane.showInputDialog(frame,"Please input the Client's address: ");

    Client result = warehouse.addClient(firstName, lastName, address);

    if (result == null) {
      JOptionPane.showMessageDialog(frame,"There was a problem adding the client.", "ERROR", JOptionPane.WARNING_MESSAGE);
    }
    JOptionPane.showMessageDialog(frame,"Client added successfully.\n" + result.toString(), "Add Client", JOptionPane.PLAIN_MESSAGE);
  }

  public void showProducts() {
    Iterator<Product> iter = warehouse.getProducts();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No products were found.");
    } else { // else display table
      
      Object[][] data = new Object[ProductList.instance().size()][5];
      int rowCounter = 0;

      iter = warehouse.getProducts();
      while(iter.hasNext()) {
        Product next = iter.next();
        data[rowCounter][0] = next.getId();
        data[rowCounter][1] = next.getName();
        data[rowCounter][2] = next.getSalePrice();
        data[rowCounter][3] = next.getSupplierId();
        data[rowCounter][4] = next.getSupplyPrice();
        rowCounter++;
      }

      String[] columnNames = {"Product ID", "Product Name", "Sale Price", "Supplier ID", "Supply Price"};

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

  public void showProductsWaitlist() {
    String productId = JOptionPane.showInputDialog(frame,"Please input the Product ID\nto view the product's waitlist: ");
    Product product = warehouse.getProductById(productId);
    Iterator<WaitItem> iter = warehouse.getWaitlist();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No waitlisted items were found.");
    } else { // else display table
      
      Object[][] data = new Object[Waitlist.instance().size()][4];
      int rowCounter = 0;

      iter = Warehouse.instance().getWaitlist();
      while(iter.hasNext()) {
        WaitItem next = iter.next();
        if(product.equals(next.getProduct().getId())) {
          data[rowCounter][0] = next.getOrderFilled();
          data[rowCounter][1] = next.getClient().getClientId();
          data[rowCounter][2] = next.getProduct().getId();
          data[rowCounter][3] = next.getQuantity();
          rowCounter++;
        }
      }

      String[] columnNames = {"Order Filled Status", "Client ID", "Product ID", "Quantity"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Waitlisted Product");
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

  public void recieveShipment() {
    Product p;
    do {
      String productId = JOptionPane.showInputDialog(frame,"Please input the Product ID:");
      p = warehouse.getProductById(productId);
      if(p != null) {
        String q = JOptionPane.showInputDialog(frame,"Enter quantity");
        int quantity = Integer.valueOf(q);

        //check for waitlisted orders
        List<WaitItem> waitlistedOrders = warehouse.getWaitItemsByProductId(productId);
        Iterator<WaitItem> waitlistedOrdersIterator = waitlistedOrders.iterator();

        while(waitlistedOrdersIterator.hasNext()) {
          WaitItem waitItem = waitlistedOrdersIterator.next();

          int result = JOptionPane.showConfirmDialog(frame,
          "Waitlisted Order found for provided product:\n\n" + waitItem.toString() +
          "\n\nFill waitlisted order?",
          "Receive Shipment",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
        
          if(result == JOptionPane.YES_OPTION) {
              quantity -= waitItem.getQuantity();
              waitItem.setOrderFilled(true);
              JOptionPane.showMessageDialog(frame,"Order filled.", "Receive Shipment", JOptionPane.INFORMATION_MESSAGE);
          } else if (result == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(frame,"Order was not filled.", "Receive Shipment", JOptionPane.INFORMATION_MESSAGE);
          }
        }
        warehouse.addToInventory(productId, quantity); // add remaining product to inventory
      } else {
        JOptionPane.showMessageDialog(frame,"Invalid Product ID.", "ERROR", JOptionPane.WARNING_MESSAGE);
      }

      // Receive another product? y/n
      int result = JOptionPane.showConfirmDialog(frame,
      "Receive another product?",
      "Receive Shipment",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    
      if (result == JOptionPane.NO_OPTION) {
        break;
      }
    } while(true);
  }

  private void becomeClient() {
    String user = JOptionPane.showInputDialog(frame,"Please input the client id: ");
    if (Warehouse.instance().getClientById(user) != null){
      (WareContext.instance()).setUser(user.toString());      
      (WareContext.instance()).changeState(1);
    } else {
      JOptionPane.showMessageDialog(frame,"Invalid Client ID.", "ERROR", JOptionPane.WARNING_MESSAGE);
    }
  }

  private void QueryClients() {
    (WareContext.instance()).changeState(4); // transition to QueryClientState with code 4
  }

  public void run() {
    frame = WareContext.instance().getFrame();
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(new FlowLayout());
    addClientButton = new JButton("Add Client");
    showProductsButton = new JButton("View Products");
    becomeClientButton = new JButton("Become a Client");
    queryClientsButton = new JButton("Query Clients Menu");
    showWaitlistButton = new JButton("View Waitlist");
    receiveShipmentButton = new JButton("Receive a Shipment");
    exitButton = new JButton("Logout");  
    addClientButton.addActionListener(this);
    showProductsButton.addActionListener(this);
    becomeClientButton.addActionListener(this);
    queryClientsButton.addActionListener(this);
    showWaitlistButton.addActionListener(this);
    receiveShipmentButton.addActionListener(this);
    exitButton.addActionListener(this);
    frame.getContentPane().add(this.addClientButton);
    frame.getContentPane().add(this.showProductsButton);
    frame.getContentPane().add(this.becomeClientButton);
    frame.getContentPane().add(this.queryClientsButton);
    frame.getContentPane().add(this.showWaitlistButton);
    frame.getContentPane().add(this.receiveShipmentButton);
    frame.getContentPane().add(this.exitButton);
    frame.setTitle("Clerk Menu");
    frame.setVisible(true);
    frame.paint(frame.getGraphics()); 
    frame.toFront();
    frame.requestFocus();
  }

  public void logout()
  {
    if (WareContext.instance().getLogin() == WareContext.IsClerk)
       {  //system.out.println(" going to login \n");
        (WareContext.instance()).changeState(0); // exit to login with a code 0
       }
    else if (WareContext.instance().getLogin() == WareContext.IsManager)
       {  //system.out.println(" going to manager \n");
        (WareContext.instance()).changeState(2); // exit to manager with a code 0
       }
    else 
       (WareContext.instance()).changeState(3); // exit code 3, indicates error
  }
 
}
