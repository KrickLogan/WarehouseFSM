import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class ManagerState extends WareState implements ActionListener {
  private static ManagerState managerState = new ManagerState();
  private static Warehouse warehouse;

  private JFrame frame;
  private AbstractButton addSupplierButton, addProductsButton, showSuppliersButton, showSuppliersForProductButton,
    showProductsForSupplierButton, becomeClientButton, becomeClerkButton, exitButton;

  private ManagerState() {
    warehouse = Warehouse.instance();
  }

  public static ManagerState instance() {
      return managerState;
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource().equals(this.addSupplierButton))
      this.addSupplier();
    else if (event.getSource().equals(this.addProductsButton)) 
      this.addProducts();
    else if (event.getSource().equals(this.showSuppliersButton)) 
      this.showSuppliers();
    else if (event.getSource().equals(this.showSuppliersForProductButton)) 
      this.showSuppliersForProduct();
    else if (event.getSource().equals(this.showProductsForSupplierButton)) 
      this.showProductsForSupplier();
    else if (event.getSource().equals(this.becomeClientButton)) 
      this.becomeClient();
    else if (event.getSource().equals(this.becomeClerkButton)) 
      this.becomeClerk();
    else if (event.getSource().equals(this.exitButton)) 
      this.logout(); 
  }

  public void addSupplier() {
    String name = JOptionPane.showInputDialog(frame,"Enter supplier name:");

    Supplier result = warehouse.addSupplier(name);

    if (result == null) {
      JOptionPane.showMessageDialog(frame,"There was a problem adding the supplier.", "ERROR", JOptionPane.WARNING_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(frame,"Supplier added successfully.\n" + result.toString(), "Add Supplier", JOptionPane.PLAIN_MESSAGE);
    }
  }

  public void addProducts() {
    Product result;
    do {
      String name = JOptionPane.showInputDialog(frame,"Enter product name:");
      String sale = JOptionPane.showInputDialog(frame,"Enter Sale Price:");
      String supply = JOptionPane.showInputDialog(frame,"Enter Supply Price:");
      String supplierId = JOptionPane.showInputDialog(frame,"Enter Supplier Id:");

      // convert strings to doubles
      double salePrice = Double.valueOf(sale);
      double supplyPrice = Double.valueOf(supply);

      result = warehouse.addProduct(name, 1, salePrice, supplyPrice, supplierId);
      if (result != null) {
        JOptionPane.showMessageDialog(frame,"Product added successfully.\n" + result.toString(), "Add Product", JOptionPane.PLAIN_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(frame,"There was a problem adding the product.", "ERROR", JOptionPane.WARNING_MESSAGE);
      }
      // add another product? y/n
      int yesNoResult = JOptionPane.showConfirmDialog(frame,
      "Add another product?",
      "Add Product",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    
      if (yesNoResult == JOptionPane.NO_OPTION) {
        break;
      }
    } while (true);
  }

  public void showSuppliers() {
    Iterator<Supplier> iter = warehouse.getSuppliers();

    // if list is empty notify user
    if (!iter.hasNext()) {
      JOptionPane.showMessageDialog(frame, "No suppliers were found.");
    } else { // else display table
      
      Object[][] data = new Object[SupplierList.instance().size()][2];
      int rowCounter = 0;

      iter = warehouse.getSuppliers();
      while(iter.hasNext()) {
        Supplier next = iter.next();
        data[rowCounter][0] = next.getId();
        data[rowCounter][1] = next.getName();
        rowCounter++;
      }

      String[] columnNames = {"Supplier ID", "Supplier Name"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Suppliers");
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

  public void showSuppliersForProduct() {
    String productId = JOptionPane.showInputDialog(frame,"Please input the Product ID\nto view the product's suppliers:");

    if(warehouse.getProductById(productId) != null) {
      Object[][] data = new Object[ProductList.instance().size()][3];
      int rowCounter = 0;
      
      Iterator<Product> iter = warehouse.getProducts();
      while (iter.hasNext()) {
        Product next = iter.next();
        if (next.equals(productId)) {
          Supplier supplier = warehouse.getSupplierById(next.getSupplierId());
          data[rowCounter][0] = next.getId();
          data[rowCounter][1] = supplier.getId();
          data[rowCounter][2] = supplier.getName();
          rowCounter++;
        }
      }
      String[] columnNames = {"Product ID", "Supplier Id", "Supplier Name"};

      // create & display new JFrame for table
      JFrame f = new JFrame("Suppliers");
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
    } else {
      JOptionPane.showMessageDialog(frame,"Invalid Product ID.", "ERROR", JOptionPane.WARNING_MESSAGE);
    }
  }

  public void showProductsForSupplier() {
      // String targetSupplierId = InputUtils.getToken("Enter supplierId");
      // Iterator<Product> allProducts = warehouse.getProducts();

      // System.out.println("Products");
      // while (allProducts.hasNext()){
      //   Product product = allProducts.next();
      //   if (product.equalsSupplierId(targetSupplierId)) {
      //     System.out.println(product.toString());
      //   }
      // }
    String supplierId = JOptionPane.showInputDialog(frame,"Please input the Supplier ID\nto view the supplier's products:");
    Supplier supplier = warehouse.getSupplierById(supplierId);
    if(supplier != null) {

      Object[][] data = new Object[ProductList.instance().size()][5];
      int rowCounter = 0;
      
      Iterator<Product> iter = warehouse.getProducts();
      while (iter.hasNext()) {
        Product next = iter.next();
        if (supplier.equals(next.getSupplierId())) {
          data[rowCounter][0] = next.getId();
          data[rowCounter][1] = next.getName();
          data[rowCounter][2] = next.getSalePrice();
          data[rowCounter][3] = supplier.getId();
          data[rowCounter][4] = next.getSupplyPrice();
          rowCounter++;
        }
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
    } else {
      JOptionPane.showMessageDialog(frame,"Invalid Product ID.", "ERROR", JOptionPane.WARNING_MESSAGE);
    }
  }

  private void becomeClerk() {
    (WareContext.instance()).changeState(2);
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

  public void run() {
    frame = WareContext.instance().getFrame();
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(new FlowLayout());
    addSupplierButton = new JButton("Add Supplier");
    addProductsButton = new JButton("Add Products");
    showSuppliersButton = new JButton("View Suppliers");
    showSuppliersForProductButton = new JButton("View Suppliers for a Product");
    showProductsForSupplierButton = new JButton("View Products for a Supplier");
    becomeClientButton = new JButton("Become a Client");
    becomeClerkButton = new JButton("Become a Clerk");
    exitButton = new JButton("Logout");  
    addSupplierButton.addActionListener(this);
    addProductsButton.addActionListener(this);
    showSuppliersButton.addActionListener(this);
    showSuppliersForProductButton.addActionListener(this);
    showProductsForSupplierButton.addActionListener(this);
    becomeClientButton.addActionListener(this);
    becomeClerkButton.addActionListener(this);
    exitButton.addActionListener(this);
    frame.getContentPane().add(this.addSupplierButton);
    frame.getContentPane().add(this.addProductsButton);
    frame.getContentPane().add(this.showSuppliersButton);
    frame.getContentPane().add(this.showSuppliersForProductButton);
    frame.getContentPane().add(this.showProductsForSupplierButton);
    frame.getContentPane().add(this.becomeClientButton);
    frame.getContentPane().add(this.becomeClerkButton);
    frame.getContentPane().add(this.exitButton);
    frame.setTitle("Manager Menu");
    frame.setVisible(true);
    frame.paint(frame.getGraphics()); 
    frame.toFront();
    frame.requestFocus();
  }

  public void logout()
  {
    if (WareContext.instance().getLogin() == WareContext.IsManager)
       {  //system.out.println(" going to login \n");
        (WareContext.instance()).changeState(0); // exit to login with a code 0
       }
    else
       (WareContext.instance()).changeState(3); // exit code 3, indicates error
  }
}
