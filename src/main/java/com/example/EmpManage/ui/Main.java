package com.example.EmpManage.ui;

import com.example.EmpManage.model.Employee;
import net.miginfocom.swing.MigLayout;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends JFrame {
    private JPanel mainPanel;
    private JTable employeeTable;
    private JScrollPane tableScrollPane;
    private JTabbedPane tabbedPane;
    private List<Employee> employees;

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
        tabbedPane = new JTabbedPane();

        // Employee List Tab
        JPanel employeeListPanel = new JPanel(new MigLayout());
        tabbedPane.addTab("Employee List", employeeListPanel);

        // Title and Search Panel
        JPanel searchPanel = new JPanel(new MigLayout("wrap 5, insets 10"));
        JButton addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.setPreferredSize(new Dimension(120, 30));

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
        searchPanel.add(addEmployeeButton, "cell 0 0");
        searchPanel.add(searchField, "cell 0 1, growx");
        searchPanel.add(searchButton, "cell 1 1");
        searchPanel.add(departmentFilter, "cell 2 1");
        searchPanel.add(jobTitleFilter, "cell 3 1");
        searchPanel.add(dateSpinner, "cell 4 1");
        searchPanel.add(filterButton, "cell 5 1");

        // Add the searchPanel to the employee list tab
        employeeListPanel.add(searchPanel, "cell 0 0, growx");

        // Employee Table (Fetching data from backend)
        fetchEmployees();

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

    private void fetchEmployees() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/employees"; // Ensure the API URL is correct

        try {
            // Fetch the employee list from the backend
            Employee[] employeeArray = restTemplate.getForObject(url, Employee[].class);
            employees = new ArrayList<>(Arrays.asList(employeeArray));

            // Set column names and data for the table
            String[] columnNames = {"Employee ID", "First Name", "Last Name", "Job Title", "Hire Date", "Status", "Contact", "Address", "Delete"};
            Object[][] rowData = new Object[employees.size()][9];

            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                rowData[i][0] = emp.getEmployeeId();
                rowData[i][1] = emp.getFirstName();
                rowData[i][2] = emp.getLastName();
                rowData[i][3] = emp.getJobTitle();
                rowData[i][4] = emp.getHireDate();
                rowData[i][5] = emp.getEmploymentStatus();
                rowData[i][6] = emp.getContactInformation();
                rowData[i][7] = emp.getAddress();
                // Add the delete button in the last column
                rowData[i][8] = new JButton("Delete");
            }

            // Create the table and scroll pane
            employeeTable = new JTable(new EmployeeTableModel(rowData, columnNames));
            employeeTable.setFillsViewportHeight(true);

            // Set a custom cell renderer for the delete button
            employeeTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
            employeeTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), new EmployeeTableModel(rowData, columnNames)));

            // Add the table to the employee list panel
            JPanel employeeListPanel = (JPanel) tabbedPane.getComponentAt(0);
            tableScrollPane = new JScrollPane(employeeTable);
            employeeListPanel.add(tableScrollPane, "cell 0 1, grow, push");

            // Refresh the UI
            getContentPane().revalidate();
            getContentPane().repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch employees. Check your backend.");
        }
    }

    private class EmployeeTableModel extends AbstractTableModel {
        private final Object[][] rowData;
        private final String[] columnNames;

        public EmployeeTableModel(Object[][] rowData, String[] columnNames) {
            this.rowData = rowData;
            this.columnNames = columnNames;
        }

        @Override
        public int getRowCount() {
            return rowData.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rowData[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 8;  // Only the Delete column is editable
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 8) {
                // When delete button is clicked, handle the deletion
                Long employeeId = employees.get(rowIndex).getEmployeeId();  // Use Long for employeeId
                deleteEmployee(employeeId);
                // Remove the row from the table
                removeRow(rowIndex);
            }
        }

        private void deleteEmployee(Long employeeId) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/employees/" + employeeId; // API URL for delete
            try {
                restTemplate.delete(url);
                JOptionPane.showMessageDialog(Main.this, "Employee deleted successfully");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(Main.this, "Failed to delete employee");
            }
        }

        private void removeRow(int rowIndex) {
            employees.remove(rowIndex);  // Remove employee from list
            fireTableRowsDeleted(rowIndex, rowIndex);  // Refresh the table view
        }
    }

    // Custom cell renderer for the Delete button
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Delete");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Custom cell editor for the Delete button
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;

        // Pass the EmployeeTableModel to the ButtonEditor
        public ButtonEditor(JCheckBox checkBox, EmployeeTableModel model) {
            super(checkBox);
            button = new JButton("Delete");
            button.setOpaque(true);
            button.addActionListener(e -> {
                int row = employeeTable.getSelectedRow();
                if (row != -1) {
                    Long employeeId = employees.get(row).getEmployeeId();
                    model.deleteEmployee(employeeId);
                    model.removeRow(row);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
