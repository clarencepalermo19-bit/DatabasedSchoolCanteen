/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.Product;

/**
 *
 * @author Clarence
 */
import com.mycompany.Model.Product;
import javax.swing.JComboBox;
import com.mycompany.canteeninventory.DBConnection;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ProductDAO {
   

    // CREATE
    public static boolean save(Product p) {

      String sql = "INSERT INTO products "
        + "(product_name, category_id, supplier_id, unit_price, quantity) "
        + "VALUES (?, ?, ?, ?, ?)";
      
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, p.getProductName());
            pst.setInt(2, Integer.parseInt(p.getCategoryID()));

            if (p.getSupplierID() != null &&
                    !p.getSupplierID().trim().isEmpty()) {
             pst.setInt(3, Integer.parseInt(p.getSupplierID()));
            } else {
                pst.setNull(3, Types.INTEGER);
            }

            pst.setBigDecimal(4,
                    new java.math.BigDecimal(p.getUnitPrice()));

            pst.setInt(5, Integer.parseInt(p.getQuantity()));

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Save Error: " + e.getMessage());
            return false;
        }
    }

    // READ BY ID
    public static Product getByID(String productID) {

        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(productID));

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    Product p = new Product();

                    p.setProductID(rs.getString("product_id"));
                    p.setProductName(rs.getString("product_name"));
                    p.setCategoryID(rs.getString("category_id"));

                    int supplierID = rs.getInt("supplier_id");

                    if (rs.wasNull()) {
                        p.setSupplierID("");
                    } else {
                        p.setSupplierID(String.valueOf(supplierID));
                    }

                    p.setUnitPrice(rs.getString("unit_price"));
                    p.setQuantity(rs.getString("quantity"));
                    p.setStockStatus(rs.getString("stock_status"));

                    return p;
                }
            }

        } catch (Exception e) {
            System.out.println("Read Error: " + e.getMessage());
        }

        return null;
    }

    // UPDATE
    public static boolean update(Product p) {

        String sql = "UPDATE products SET "
                + "product_name = ?, category_id = ?, supplier_id = ?, "
                + "unit_price = ?, quantity = ? "
                + "WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, p.getProductName());
            pst.setInt(2, Integer.parseInt(p.getCategoryID()));

            if (p.getSupplierID() == null ||
                    p.getSupplierID().trim().isEmpty()) {
                pst.setNull(3, Types.INTEGER);
            } else {
              pst.setInt(3, Integer.parseInt(p.getSupplierID()));
            }

            pst.setBigDecimal(4,
                    new java.math.BigDecimal(p.getUnitPrice()));

            pst.setInt(5, Integer.parseInt(p.getQuantity()));
            pst.setInt(6, Integer.parseInt(p.getProductID()));

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public static boolean delete(String productID) {

        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(productID));

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
            return false;
        }
    }

    // DISPLAY TABLE DATA
      // DISPLAY TABLE DATA (shows Category ID and Supplier ID)
    public static TableModel getTableData() {

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Product ID");
        model.addColumn("Product Name");
        model.addColumn("Category ID");
        model.addColumn("Supplier ID");
        model.addColumn("Unit Price");
        model.addColumn("Quantity");
        model.addColumn("Stock Status");

        String sql = "SELECT product_id, product_name, category_id, "
                + "supplier_id, unit_price, quantity, stock_status "
                + "FROM products "
                + "ORDER BY product_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("category_id"),
                    rs.getString("supplier_id"),   // getString, so an empty supplier stays blank
                    rs.getBigDecimal("unit_price"),
                    rs.getInt("quantity"),
                    rs.getString("stock_status")
                });
            }

        } catch (Exception e) {
            System.out.println("Table Error: " + e.getMessage());
        }

        return model;
    }
    // LOAD CATEGORY NAMES AND IDS
   public static void loadCategories(JComboBox<ComboItem> combo) {

        combo.removeAllItems();
          combo.addItem(new ComboItem(0, "Select Categories"));
        String sql = "SELECT category_id, category_name FROM categories";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                combo.addItem(new ComboItem(
                    rs.getInt("category_id"),
                    rs.getString("category_name")
                ));
            }

        } catch (Exception e) {
            System.out.println("Category Error: " + e.getMessage());
        }
    } 

    // LOAD SUPPLIER NAMES AND IDS
    public static void loadSuppliers(JComboBox<ComboItem> combo) {

        combo.removeAllItems();

        combo.addItem(new ComboItem(0, "Select Supplier"));

        String sql = "SELECT supplier_id, supplier_name FROM suppliers";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                combo.addItem(new ComboItem(
                    rs.getInt("supplier_id"),
                    rs.getString("supplier_name")
                ));
            }

        } catch (Exception e) {
            System.out.println("Supplier Error: " + e.getMessage());
        }
    }

}


    

