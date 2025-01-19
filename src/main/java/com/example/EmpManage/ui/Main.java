package com.example.EmpManage.ui;

import com.example.EmpManage.model.Employee;
import com.example.EmpManage.model.EmploymentStatus;
import net.miginfocom.swing.MigLayout;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
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

        addEmployeeButton.addActionListener(e -> showAddEmployeeDialog());
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim(); // Get search query
            System.out.println("Search query: " + query);
            if (query.isEmpty()) {
                fetchEmployees(); // If query is empty, show all employees
            } else {
                List<Employee> employees = searchEmployees(query); // Perform search
                updateTable(employees); // Update the JTable with new data
            }
        });
    }

    private void fetchEmployees() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/employees";

        try {
            Employee[] employeeArray = restTemplate.getForObject(url, Employee[].class);
            employees = new ArrayList<>(Arrays.asList(employeeArray));

            String[] columnNames = {"Employee ID", "First Name", "Last Name", "Job Title", "Hire Date", "Status", "Contact", "Address", "Delete", "Update"};
            // Create the table with the new model
            employeeTable = new JTable(new EmployeeTableModel(columnNames));
            employeeTable.setFillsViewportHeight(true);

            // Set a custom cell renderer for the delete button
            employeeTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
            employeeTable.getColumn("Delete").setCellEditor(
                    new ButtonEditor(new JCheckBox(), (EmployeeTableModel) employeeTable.getModel())
            );

            employeeTable.getColumn("Update").setCellRenderer(new ButtonRenderer());
            employeeTable.getColumn("Update").setCellEditor(
                    new ButtonEditor(new JCheckBox(), (EmployeeTableModel) employeeTable.getModel())
            );

            // Add the table to the employee list panel
            JPanel employeeListPanel = (JPanel) tabbedPane.getComponentAt(0);
            if (tableScrollPane != null) {
                employeeListPanel.remove(tableScrollPane);
            }
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

    private void showAddEmployeeDialog() {
        JDialog addEmployeeDialog = new JDialog(this, "Add New Employee", true);
        addEmployeeDialog.setSize(400, 300); // Adjusted the height to make it smaller
        addEmployeeDialog.setLayout(new MigLayout("wrap 2", "[grow][grow]"));

        JTextField firstNameField = new JTextField(15);
        JTextField lastNameField = new JTextField(15);
        JTextField jobTitleField = new JTextField(15);
        JTextField departmentField = new JTextField(15);
        JTextField hireDateField = new JTextField(10); // Adjusted size for date field
        JComboBox<EmploymentStatus> employmentStatusComboBox = new JComboBox<>(EmploymentStatus.values());
        JTextField contactInformationField = new JTextField(15);
        JTextField addressField = new JTextField(15);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        addEmployeeDialog.add(new JLabel("First Name:"));
        addEmployeeDialog.add(firstNameField, "growx");
        addEmployeeDialog.add(new JLabel("Last Name:"));
        addEmployeeDialog.add(lastNameField, "growx");
        addEmployeeDialog.add(new JLabel("Job Title:"));
        addEmployeeDialog.add(jobTitleField, "growx");
        addEmployeeDialog.add(new JLabel("Department:"));
        addEmployeeDialog.add(departmentField, "growx");

        // Hire Date Spinner, compact and with a custom editor
        JSpinner hireDateSpinner = new JSpinner(new SpinnerDateModel());
        hireDateSpinner.setEditor(new JSpinner.DateEditor(hireDateSpinner, "yyyy-MM-dd"));
        hireDateSpinner.setPreferredSize(new Dimension(100, 25)); // Make the spinner smaller
        addEmployeeDialog.add(new JLabel("Hire Date:"));
        addEmployeeDialog.add(hireDateSpinner, "growx");

        addEmployeeDialog.add(new JLabel("Employment Status:"));
        addEmployeeDialog.add(employmentStatusComboBox, "growx");
        addEmployeeDialog.add(new JLabel("Contact Information:"));
        addEmployeeDialog.add(contactInformationField, "growx");
        addEmployeeDialog.add(new JLabel("Address:"));
        addEmployeeDialog.add(addressField, "growx");
        addEmployeeDialog.add(saveButton, "split 2, span, center");
        addEmployeeDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String jobTitle = jobTitleField.getText();
                String department = departmentField.getText();
                LocalDate hireDate = ((java.util.Date) hireDateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                EmploymentStatus employmentStatus = (EmploymentStatus) employmentStatusComboBox.getSelectedItem();
                int contactInformation = Integer.parseInt(contactInformationField.getText());
                String address = addressField.getText();

                Employee newEmployee = new Employee(null, firstName, lastName, jobTitle, department, hireDate, employmentStatus, contactInformation, address);
                addNewEmployee(newEmployee);
                addEmployeeDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input, please check all fields.");
            }
        });

        cancelButton.addActionListener(e -> addEmployeeDialog.dispose());

        addEmployeeDialog.setLocationRelativeTo(this);
        addEmployeeDialog.setVisible(true);
    }

    private void showUpdateEmployeeDialog(Employee employee) {
        JDialog updateEmployeeDialog = new JDialog(this, "Update Employee", true);
        updateEmployeeDialog.setSize(400, 300);
        updateEmployeeDialog.setLayout(new MigLayout("wrap 2", "[grow][grow]"));

        JTextField firstNameField = new JTextField(employee.getFirstName(), 15);
        JTextField lastNameField = new JTextField(employee.getLastName(), 15);
        JTextField jobTitleField = new JTextField(employee.getJobTitle(), 15);
        JTextField departmentField = new JTextField(employee.getDepartment(), 15);
        JTextField hireDateField = new JTextField(employee.getHireDate().toString(), 10);
        JComboBox<EmploymentStatus> employmentStatusComboBox = new JComboBox<>(EmploymentStatus.values());
        employmentStatusComboBox.setSelectedItem(employee.getEmploymentStatus());
        JTextField contactInformationField = new JTextField(String.valueOf(employee.getContactInformation()), 15);
        JTextField addressField = new JTextField(employee.getAddress(), 15);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Add components to dialog
        updateEmployeeDialog.add(new JLabel("First Name:"));
        updateEmployeeDialog.add(firstNameField, "growx");
        updateEmployeeDialog.add(new JLabel("Last Name:"));
        updateEmployeeDialog.add(lastNameField, "growx");
        updateEmployeeDialog.add(new JLabel("Job Title:"));
        updateEmployeeDialog.add(jobTitleField, "growx");
        updateEmployeeDialog.add(new JLabel("Department:"));
        updateEmployeeDialog.add(departmentField, "growx");
        updateEmployeeDialog.add(new JLabel("Hire Date:"));
        updateEmployeeDialog.add(hireDateField, "growx");
        updateEmployeeDialog.add(new JLabel("Employment Status:"));
        updateEmployeeDialog.add(employmentStatusComboBox, "growx");
        updateEmployeeDialog.add(new JLabel("Contact Information:"));
        updateEmployeeDialog.add(contactInformationField, "growx");
        updateEmployeeDialog.add(new JLabel("Address:"));
        updateEmployeeDialog.add(addressField, "growx");
        updateEmployeeDialog.add(saveButton, "split 2, span, center");
        updateEmployeeDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String jobTitle = jobTitleField.getText();
                String department = departmentField.getText();
                LocalDate hireDate = LocalDate.parse(hireDateField.getText());
                EmploymentStatus employmentStatus = (EmploymentStatus) employmentStatusComboBox.getSelectedItem();
                int contactInformation = Integer.parseInt(contactInformationField.getText());
                String address = addressField.getText();

                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setJobTitle(jobTitle);
                employee.setDepartment(department);
                employee.setHireDate(hireDate);
                employee.setEmploymentStatus(employmentStatus);
                employee.setContactInformation(contactInformation);
                employee.setAddress(address);

                updateEmployee(employee);
                updateEmployeeDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input, please check all fields.");
            }
        });

        cancelButton.addActionListener(e -> updateEmployeeDialog.dispose());

        updateEmployeeDialog.setLocationRelativeTo(this);
        updateEmployeeDialog.setVisible(true);
    }



    private void addNewEmployee(Employee newEmployee) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/employees";

        try {
            restTemplate.postForObject(url, newEmployee, Employee.class);
            JOptionPane.showMessageDialog(this, "Employee added successfully");
            fetchEmployees(); // Refresh the table
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add employee");
        }
    }

    private void updateEmployee(Employee employee) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/employees/" + employee.getEmployeeId();

        try {
            restTemplate.put(url, employee);
            JOptionPane.showMessageDialog(this, "Employee updated successfully");
            fetchEmployees(); // Refresh the table
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update employee");
        }
    }

    private List<Employee> searchEmployees(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/employees/search?query=" + query; // Replace with your actual base URL
        try {
            Employee[] employees = restTemplate.getForObject(url, Employee[].class);
            return Arrays.asList(employees);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching search results. Please try again.");
            return List.of(); // Return an empty list in case of an error
        }
    }

    private void updateTable(List<Employee> newEmployees) {
        ((EmployeeTableModel) employeeTable.getModel()).updateData(newEmployees);
    }





    private class EmployeeTableModel extends AbstractTableModel {
        private final String[] columnNames;

        public EmployeeTableModel(String[] columnNames) {
            this.columnNames = columnNames;
        }

        @Override
        public int getRowCount() {
            return employees.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        public void updateData(List<Employee> newEmployees) {
            employees.clear();
            employees.addAll(newEmployees);
            fireTableDataChanged();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= employees.size()) {
                return null; // Return null if the row index is out of bounds
            }
            Employee emp = employees.get(rowIndex);
            switch (columnIndex) {
                case 0: return emp.getEmployeeId();
                case 1: return emp.getFirstName();
                case 2: return emp.getLastName();
                case 3: return emp.getJobTitle();
                case 4: return emp.getHireDate();
                case 5: return emp.getEmploymentStatus();
                case 6: return emp.getContactInformation();
                case 7: return emp.getAddress();
                case 8: return "Delete";
                case 9: return "Update";
                default: return null;
            }
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 8 || columnIndex == 9;  // Allow editing for both Delete and Update columns
        }

        private void deleteEmployee(Long employeeId) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/employees/" + employeeId;
            try {
                restTemplate.delete(url);
                JOptionPane.showMessageDialog(Main.this, "Employee deleted successfully");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(Main.this, "Failed to delete employee");
            }
        }


        public void removeRow(int rowIndex) {
            if (rowIndex >= 0 && rowIndex < employees.size()) {
                employees.remove(rowIndex);
                fireTableDataChanged(); // This will refresh the entire table
            }
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString()); // Set the text based on the column (Delete or Update)
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final EmployeeTableModel model;
        private int column;

        public ButtonEditor(JCheckBox checkBox, EmployeeTableModel model) {
            super(checkBox);
            this.model = model;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                int row = employeeTable.getSelectedRow();
                if (row != -1) {
                    if (column == 8) { // Delete button
                        Long employeeId = employees.get(row).getEmployeeId();
                        model.deleteEmployee(employeeId);
                        model.removeRow(row);
                    } else if (column == 9) { // Update button
                        Employee employee = employees.get(row);
                        showUpdateEmployeeDialog(employee);
                    }
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.column = column;
            button.setText(value.toString());
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