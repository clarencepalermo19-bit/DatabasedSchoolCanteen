package com.mycompany.Supplier;

import javax.swing.DefaultComboBoxModel;
import com.mycompany.Supplier.SupplierDAO;
import com.mycompany.Model.Supplier;
import com.mycompany.Product.ComboItem;
import com.mycompany.canteeninventory.DBConnection;
import java.sql.*;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class SupplierDAO {

    // CREATE
    public static boolean save(Supplier s) {

        String sql = "INSERT INTO suppliers "
                + "(supplier_name, categories, contact_number) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, s.getSupplierName());
            pst.setString(2, s.getCategories());
            pst.setString(3, s.getContactNumber());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Save Error: " + e.getMessage());
            return false;
        }
    }

    // READ BY ID
    public static Supplier getByID(String supplierID) {

        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(supplierID));

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    Supplier s = new Supplier();

                    s.setSupplierID(rs.getString("supplier_id"));
                    s.setSupplierName(rs.getString("supplier_name"));
                    s.setCategories(rs.getString("categories"));
                    s.setContactNumber(rs.getString("contact_number"));

                    return s;
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Read Error: " + e.getMessage());
        }

        return null;
    }

    // UPDATE
    public static boolean update(Supplier s) {

        String sql = "UPDATE suppliers SET "
                + "supplier_name = ?, categories = ?, contact_number = ? "
                + "WHERE supplier_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, s.getSupplierName());
            pst.setString(2, s.getCategories());
            pst.setString(3, s.getContactNumber());
            pst.setInt(4, Integer.parseInt(s.getSupplierID()));

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Update Error: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public static boolean delete(String supplierID) {

        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(supplierID));

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Delete Error: " + e.getMessage());
            return false;
        }
    }

    // DISPLAY TABLE DATA
    public static TableModel getTableData() {

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Supplier ID");
        model.addColumn("Supplier Name");
        model.addColumn("Categories");
        model.addColumn("Contact Number");

        String sql = "SELECT * FROM suppliers ORDER BY supplier_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("supplier_id"),
                    rs.getString("supplier_name"),
                    rs.getString("categories"),
                    rs.getString("contact_number")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Table Error: " + e.getMessage());
        }

        return model;
    }

    // SEARCH BY SUPPLIER NAME
    public static TableModel searchByName(String keyword) {

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Supplier ID");
        model.addColumn("Supplier Name");
        model.addColumn("Categories");
        model.addColumn("Contact Number");

        String sql = "SELECT * FROM suppliers "
                + "WHERE supplier_name LIKE ? ORDER BY supplier_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("categories"),
                        rs.getString("contact_number")
                    });
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Search Error: " + e.getMessage());
        }

        return model;
    }
    
public static void loadCategories(JComboBox<ComboItem> cmbCategories) {

    cmbCategories.removeAllItems();

    // Add default text
    cmbCategories.addItem(new ComboItem(0, "Select Categories"));

    String sql = "SELECT category_id, category_name FROM categories";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            cmbCategories.addItem(new ComboItem(
                rs.getString("categories")
            ));
        }

        // Set "Select Categories" as default selection
        cmbCategories.setSelectedIndex(0);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null,
                "Category Error: " + e.getMessage());
    }
}
}