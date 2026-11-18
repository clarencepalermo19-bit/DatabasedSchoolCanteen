package com.mycompany.View;

import com.mycompany.Report.ReportDAO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class ReportDialog extends JDialog {

    private static final Color PINK = new Color(255, 102, 148);
    private static final Color LIGHT = new Color(255, 204, 255);
    private static final Color DARK = new Color(153, 0, 76);

    private final JPanel summaryPanel = new JPanel(new GridLayout(0, 5, 10, 10));
    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel dateLabel = new JLabel();

    public ReportDialog(Frame parent) {

        super(parent, "Inventory Report", false);   // false = main window stays usable

        setSize(980, 640);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(LIGHT);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        summaryPanel.setOpaque(false);
        center.add(summaryPanel, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        loadData();
    }

    // ============================================
    // PAGE PARTS
    // ============================================

    private JPanel buildHeader() {

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(PINK);
        header.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        JLabel title = new JLabel("SCHOOL CANTEEN INVENTORY REPORT",
                SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setForeground(new Color(255, 255, 204));

        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));

        header.add(title);
        header.add(dateLabel);

        return header;
    }

    private JPanel buildButtons() {

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        buttons.setOpaque(false);

        JButton refresh = new JButton("REFRESH");
        JButton print = new JButton("PRINT");
        JButton close = new JButton("CLOSE");

        refresh.addActionListener(e -> loadData());
        print.addActionListener(e -> printSelectedTab());
        close.addActionListener(e -> dispose());

        buttons.add(refresh);
        buttons.add(print);
        buttons.add(close);

        return buttons;
    }

    // ONE SUMMARY BOX (big value + small label)
    private JPanel card(String label, String value) {

        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(PINK, 2));

        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 20));
        v.setForeground(DARK);

        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));

        card.add(v);
        card.add(l);

        return card;
    }

    // ONE TAB WITH A TABLE
    private void addTab(String title, TableModel model) {

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setGridColor(PINK);
        table.setAutoCreateRowSorter(true);        // click a header to sort

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, center);

        table.getTableHeader().setBackground(PINK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);

        tabs.addTab(title + " (" + model.getRowCount() + ")", new JScrollPane(table));
    }

    // ============================================
    // LOAD / REFRESH THE REPORT FROM THE DATABASE
    // ============================================

    private void loadData() {

        // SUMMARY BOXES
        summaryPanel.removeAll();

        for (Map.Entry<String, String> e : ReportDAO.getSummary().entrySet()) {
            summaryPanel.add(card(e.getKey(), e.getValue()));
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();

        // TABS (keep the tab the user was viewing)
        int selected = tabs.getSelectedIndex();

        tabs.removeAll();

        addTab("Products", ReportDAO.getProductsReport());
        addTab("Low Stock", ReportDAO.getLowStockReport());
        addTab("Categories", ReportDAO.getCategoriesReport());
        addTab("Suppliers", ReportDAO.getSuppliersReport());
        addTab("Stock In", ReportDAO.getStockInReport());
        addTab("Stock Out", ReportDAO.getStockOutReport());

        if (selected >= 0 && selected < tabs.getTabCount()) {
            tabs.setSelectedIndex(selected);
        }

        dateLabel.setText("Generated on: "
                + new SimpleDateFormat("MMMM dd, yyyy  hh:mm a").format(new Date()));
    }

    // ============================================
    // PRINT THE TAB THAT IS OPEN
    // ============================================

    private void printSelectedTab() {

        if (tabs.getSelectedComponent() == null) {
            return;
        }

        JScrollPane pane = (JScrollPane) tabs.getSelectedComponent();
        JTable table = (JTable) pane.getViewport().getView();

        try {
            table.print(JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat("School Canteen Inventory Report"),
                    new MessageFormat("Page {0}"));

        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to print: " + ex.getMessage());
        }
    }
}