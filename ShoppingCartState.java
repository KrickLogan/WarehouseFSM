import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class ShoppingCartState extends WareState implements ActionListener {
  private static ShoppingCartState shoppingCartState;
  private static Warehouse warehouse;

  private JFrame frame;
  private AbstractButton viewCartButton, addToCartButton,
    modifyCartButton, exitButton;

  private ShoppingCartState() {
    warehouse = Warehouse.instance();
  }

  public static ShoppingCartState instance() {
    if (shoppingCartState == null) {
      return shoppingCartState = new ShoppingCartState();
    } else {
      return shoppingCartState;
    }
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource().equals(this.viewCartButton))
      this.viewCart();
    else if (event.getSource().equals(this.addToCartButton)) 
      this.addToCart();
    else if (event.getSource().equals(this.modifyCartButton)) 
      this.modifyCart();
    else if (event.getSource().equals(this.exitButton)) 
      this.logout(); 
  }

  public void viewCart() {
    String id = WareContext.instance().getUser();
    Client client = warehouse.getClientById(id);
    Iterator<ShoppingCartItem> iter = client.getShoppingCart().getShoppingCartProducts();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "Shopping Cart is empty.");
    } else { // else display table
      
      Object[][] data = new Object[client.getShoppingCart().size()][4];
      int rowCounter = 0;

      iter = client.getShoppingCart().getShoppingCartProducts();
      while(iter.hasNext()) {
        ShoppingCartItem next = iter.next();
        data[rowCounter][0] = next.getProduct().getId();
        data[rowCounter][1] = next.getProduct().getName();
        data[rowCounter][2] = next.getProduct().getSalePrice();
        data[rowCounter][3] = next.getQuantity();
        rowCounter++;
      }

      String[] columnNames = {"Product ID", "Product Name", "Sale Price", "Quantity in Cart"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Shopping Cart Contents");
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
  
  public void addToCart() {
    String clientId = WareContext.instance().getUser();
    do {
      String productId = JOptionPane.showInputDialog(frame,"Enter Product ID:");
      
      Product product = warehouse.getProductById(productId);
      if(product != null) {
        String q = JOptionPane.showInputDialog(frame,"Enter quantity");
        int quantity = Integer.valueOf(q);
        warehouse.addToCart(clientId, product, quantity);
        JOptionPane.showMessageDialog(frame,"Successfully added to your shopping cart.", "Add Products to Shopping Cart", JOptionPane.PLAIN_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(frame,"There was a problem adding the product to the shopping cart.", "ERROR", JOptionPane.WARNING_MESSAGE);
      }
      // add another product to cart? y/n
      int yesNoResult = JOptionPane.showConfirmDialog(frame,
      "Add another product to the shopping cart?",
      "Add Products to Shopping Cart",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    
      if (yesNoResult == JOptionPane.NO_OPTION) {
        break;
      }
    } while (true);
  }

  public void modifyCart() {
    String clientId = WareContext.instance().getUser();
    Client client = warehouse.getClientById(clientId);
    ShoppingCart cart = client.getShoppingCart();
    Boolean doneEditing = false;

    while (!doneEditing) {
      String productId = JOptionPane.showInputDialog(frame,"Enter a Product ID from the shopping cart to edit:");

      // find the product in the shopping cart
      ShoppingCartItem item = null;
      Iterator<ShoppingCartItem> cartIter = cart.getShoppingCartProducts();
      while ( cartIter.hasNext() ) {
        ShoppingCartItem next = cartIter.next();
        if (next.getProduct().getId().equals(productId)) {
          item = next;
          break;
        }
      }

      if ( item != null ) {
        String newQ = JOptionPane.showInputDialog(frame,"Enter the desired quantity,\nor '0' to remove the product from you cart.");
        int newQuantity = Integer.valueOf(newQ);
        if(newQuantity == 0) {
          if(cart.removeProductFromCart(warehouse.getProductById(productId))) {
            JOptionPane.showMessageDialog(frame,"Product has been removed from your shopping cart", "Modify Shopping Cart", JOptionPane.PLAIN_MESSAGE);
          } else {
            JOptionPane.showMessageDialog(frame,"There was a problem removing the product from your shopping cart.", "ERROR", JOptionPane.WARNING_MESSAGE);
          }
        } else if (newQuantity > 0) {
          item.setQuantity(newQuantity);
        } else {
          JOptionPane.showMessageDialog(frame,"Invalid input. Please enter a number greater than or equal to 0.", "ERROR", JOptionPane.WARNING_MESSAGE);
        }
      } else {
        JOptionPane.showMessageDialog(frame,"Could not find that product in your shopping cart.", "ERROR", JOptionPane.WARNING_MESSAGE);
      }
      // edit another product? y/n
      int yesNoResult = JOptionPane.showConfirmDialog(frame,
      "Edit another product in your shopping cart?",
      "Modify Shopping Cart",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    
      if (yesNoResult == JOptionPane.NO_OPTION) {
        doneEditing = true;;
      }
    }
  }

  public void run() {
    frame = WareContext.instance().getFrame();
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(new FlowLayout());
    viewCartButton = new JButton("View Shopping Cart");
    addToCartButton = new JButton("Add to Shopping Cart");
    modifyCartButton = new JButton("Modify Shopping Cart");
    exitButton = new JButton("Return to Client Menu");  
    viewCartButton.addActionListener(this);
    addToCartButton.addActionListener(this);
    modifyCartButton.addActionListener(this);
    exitButton.addActionListener(this);
    frame.getContentPane().add(this.viewCartButton);
    frame.getContentPane().add(this.addToCartButton);
    frame.getContentPane().add(this.modifyCartButton);
    frame.getContentPane().add(this.exitButton);
    frame.setTitle("Shopping Cart Menu");
    frame.setVisible(true);
    frame.paint(frame.getGraphics()); 
    frame.toFront();
    frame.requestFocus();
  }

  public void logout() {
    (WareContext.instance()).changeState(0); // exit to ClientState with a code 0
  }
  
}
