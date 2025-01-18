package com.example.EmpManage.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {
    private JPanel mainPanel;

    public Main() {
        setTitle("Employee Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Title Panel (Center aligned)
        JPanel titlePanel = new JPanel(new MigLayout("align center, insets 20"));
        JLabel titleLabel = new JLabel("Employee Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel, "cell 0 0, align center");

        // Main Panel for Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Employee List Tab
        JPanel employeeListPanel = new JPanel(new MigLayout());
        tabbedPane.addTab("Employee List", employeeListPanel);

        // Title and Search Panel
        JPanel searchPanel = new JPanel(new MigLayout("wrap 5, insets 10"));
        JButton addEmployeeButton = new JButton("Add Employee");

        // Set a preferred size for the "Add Employee" button to prevent it from stretching
        addEmployeeButton.setPreferredSize(new Dimension(120, 30));  // Adjust size as needed

        // Search Panel layout with search field, filters, and buttons
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        JComboBox<String> departmentFilter = new JComboBox<>(new String[]{"All Departments", "HR", "IT", "Sales"});
        JComboBox<String> jobTitleFilter = new JComboBox<>(new String[]{"All Job Titles", "Manager", "Engineer", "Developer"});

        // JSpinner for Date Picker
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        JButton filterButton = new JButton("Filter");

        // Add components to search panel
        searchPanel.add(addEmployeeButton, "cell 0 0");  // No growx to prevent stretching
        searchPanel.add(searchField, "cell 0 1, growx");
        searchPanel.add(searchButton, "cell 1 1");
        searchPanel.add(departmentFilter, "cell 2 1");
        searchPanel.add(jobTitleFilter, "cell 3 1");
        searchPanel.add(dateSpinner, "cell 4 1");
        searchPanel.add(filterButton, "cell 5 1");

        // Add the searchPanel to the employee list tab
        employeeListPanel.add(searchPanel, "cell 0 0, growx");

        // Employee Table (Mock Example)
        String[] columns = {"Name", "Department", "Job Title", "Date of Joining", "Edit", "Delete", "Change Role"};
        Object[][] data = {
                {"John Doe", "HR", "Manager", "2022-01-15", "Edit", "Delete", "Change Role"},
                {"Jane Smith", "IT", "Developer", "2021-06-23", "Edit", "Delete", "Change Role"}
        };

        JTable employeeTable = new JTable(data, columns);

        // Make Edit, Delete, Change Role clickable by adding MouseListener
        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = employeeTable.rowAtPoint(e.getPoint());
                int col = employeeTable.columnAtPoint(e.getPoint());

                // Check if the clicked cell contains "Edit", "Delete", or "Change Role"
                if (col >= 4) {  // Columns for actions (Edit, Delete, Change Role)
                    String action = employeeTable.getValueAt(row, col).toString();
                    String name = employeeTable.getValueAt(row, 0).toString();  // Get the employee name for context

                    switch (action) {
                        case "Edit":
                            JOptionPane.showMessageDialog(employeeTable, "Edit action triggered for: " + name);
                            break;
                        case "Delete":
                            JOptionPane.showMessageDialog(employeeTable, "Delete action triggered for: " + name);
                            break;
                        case "Change Role":
                            JOptionPane.showMessageDialog(employeeTable, "Change Role action triggered for: " + name);
                            break;
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        employeeListPanel.add(tableScrollPane, "cell 0 1, grow, push");

        // Audit Log Tab (Dummy Example for now)
        JPanel auditLogPanel = new JPanel(new MigLayout());
        tabbedPane.addTab("Audit Log", auditLogPanel);
        JTextArea auditLogArea = new JTextArea(20, 50);
        auditLogArea.setText("Audit log entries will be shown here...");
        JScrollPane auditLogScroll = new JScrollPane(auditLogArea);
        auditLogPanel.add(auditLogScroll, "cell 0 0, grow, push");

        // Add all components to the JFrame
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
