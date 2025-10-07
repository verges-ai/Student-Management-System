import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {
private final String[] cols = {"ID", "First Name", "Last Name", "DOB", "Course", "Grade"};
private StudentDAO dao;


public StudentTableModel(StudentDAO dao) {
this.dao = dao;
}


@Override
public int getRowCount() {
return dao.getAll().size();
}


@Override
public int getColumnCount() { return cols.length; }


@Override
public String getColumnName(int column) { return cols[column]; }


@Override
public Object getValueAt(int rowIndex, int columnIndex) {
List<Student> list = dao.getAll();
if (rowIndex < 0 || rowIndex >= list.size()) return null;
Student s = list.get(rowIndex);
switch (columnIndex) {
case 0: return s.getId();
case 1: return s.getFirstName();
case 2: return s.getLastName();
case 3: return s.getDob();
case 4: return s.getCourse();
case 5: return s.getGrade();
default: return null;
}
}


@Override
public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }


public void refresh() {
fireTableDataChanged();
}
}