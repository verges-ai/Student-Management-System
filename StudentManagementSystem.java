import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.regex.Pattern;

public class StudentManagementSystem {
    private final StudentDAO dao = new StudentDAO();
    private final StudentTableModel model = new StudentTableModel(dao);

    // UI components
    private JFrame frame;
    private JTable table;
    private JTextField tfId, tfFirst, tfLast, tfDob, tfCourse, tfGrade, tfSearch;
    private TableRowSorter<StudentTableModel> sorter;

    public StudentManagementSystem() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Table
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("ID:"), c);
        c.gridx = 1; tfId = new JTextField(15); form.add(tfId, c);
        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("First Name:"), c);
        c.gridx = 1; tfFirst = new JTextField(15); form.add(tfFirst, c);
        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Last Name:"), c);
        c.gridx = 1; tfLast = new JTextField(15); form.add(tfLast, c);
        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("DOB (YYYY-MM-DD):"), c);
        c.gridx = 1; tfDob = new JTextField(15); form.add(tfDob, c);
        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Course:"), c);
        c.gridx = 1; tfCourse = new JTextField(15); form.add(tfCourse, c);
        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Grade (0-100):"), c);
        c.gridx = 1; tfGrade = new JTextField(15); form.add(tfGrade, c);
        y++;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnLoad = new JButton("Load CSV");
        JButton btnSave = new JButton("Save CSV");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnLoad);
        btnPanel.add(btnSave);

        c.gridx = 0;
        c.gridy = y;
        c.gridwidth = 2;
        form.add(btnPanel, c);
        y++;

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        tfSearch = new JTextField(20);
        searchPanel.add(tfSearch);

        frame.add(searchPanel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, form);
        split.setResizeWeight(0.7);
        frame.add(split, BorderLayout.CENTER);

        // Bottom status / average
        JLabel lblStatus = new JLabel("No students loaded.");
        frame.add(lblStatus, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> {
            String id = tfId.getText().trim();
            String first = tfFirst.getText().trim();
            String last = tfLast.getText().trim();
            String dob = tfDob.getText().trim();
            String course = tfCourse.getText().trim();
            String gradeStr = tfGrade.getText().trim();

            if (id.isEmpty() || first.isEmpty() || last.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "ID, First Name and Last Name are required.");
                return;
            }

            double grade;
            try {
                grade = Double.parseDouble(gradeStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Grade must be a number.");
                return;
            }

            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(frame, "Grade must be between 0 and 100.");
                return;
            }

            Student s = new Student(id, first, last, dob, course, grade);
            dao.add(s);
            model.refresh();
            lblStatus.setText("Students: " + dao.getAll().size() + " | Avg grade: " + String.format("%.2f", dao.averageGrade()));
            clearForm();
        });

        btnUpdate.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a row to update.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            String id = tfId.getText().trim();
            String first = tfFirst.getText().trim();
            String last = tfLast.getText().trim();
            String dob = tfDob.getText().trim();
            String course = tfCourse.getText().trim();
            String gradeStr = tfGrade.getText().trim();

            if (id.isEmpty() || first.isEmpty() || last.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "ID, First Name and Last Name are required.");
                return;
            }

            double grade;
            try {
                grade = Double.parseDouble(gradeStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Grade must be a number.");
                return;
            }

            Student s = new Student(id, first, last, dob, course, grade);
            dao.update(modelRow, s);
            model.refresh();
            lblStatus.setText("Students: " + dao.getAll().size() + " | Avg grade: " + String.format("%.2f", dao.averageGrade()));
            clearForm();
        });

        btnDelete.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a row to delete.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            int ok = JOptionPane.showConfirmDialog(frame, "Delete selected student?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                dao.removeByIndex(modelRow);
                model.refresh();
                lblStatus.setText("Students: " + dao.getAll().size() + " | Avg grade: " + String.format("%.2f", dao.averageGrade()));
                clearForm();
            }
        });

        btnSave.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int ret = fc.showSaveDialog(frame);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    dao.saveToFile(f);
                    JOptionPane.showMessageDialog(frame, "Saved to " + f.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to save: " + ex.getMessage());
                }
            }
        });

        btnLoad.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int ret = fc.showOpenDialog(frame);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    dao.loadFromFile(f);
                    model.refresh();
                    lblStatus.setText("Students: " + dao.getAll().size() + " | Avg grade: " + String.format("%.2f", dao.averageGrade()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to load: " + ex.getMessage());
                }
            }
        });

        // Table selection -> populate form
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow == -1) return;
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    Student s = dao.getAll().get(modelRow);
                    tfId.setText(s.getId());
                    tfFirst.setText(s.getFirstName());
                    tfLast.setText(s.getLastName());
                    tfDob.setText(s.getDob());
                    tfCourse.setText(s.getCourse());
                    tfGrade.setText(String.valueOf(s.getGrade()));
                }
            }
        });

        // Live search
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void apply() {
                String text = tfSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        String regex = "(?i)" + Pattern.quote(text);
                        sorter.setRowFilter(javax.swing.RowFilter.regexFilter(regex));
                    } catch (Exception ex) {
                        sorter.setRowFilter(null);
                    }
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { apply(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { apply(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { apply(); }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void clearForm() {
        tfId.setText("");
        tfFirst.setText("");
        tfLast.setText("");
        tfDob.setText("");
        tfCourse.setText("");
        tfGrade.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        new StudentManagementSystem();
    }
}
