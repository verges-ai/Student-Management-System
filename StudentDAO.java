import java.io.*;
import java.util.*;

public class StudentDAO {
private final List<Student> students = new ArrayList<>();


public List<Student> getAll() {
return Collections.unmodifiableList(students);
}


public void add(Student s) {
students.add(s);
}


public void removeByIndex(int idx) {
if (idx >= 0 && idx < students.size()) students.remove(idx);
}


public void update(int idx, Student s) {
if (idx >= 0 && idx < students.size()) students.set(idx, s);
}


public void clear() { students.clear(); }


// Save to CSV
public void saveToFile(File f) throws IOException {
try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
// header
bw.write("id,firstName,lastName,dob,course,grade\n");
for (Student s : students) {
bw.write(s.toCSVLine());
bw.write("\n");
}
}
}


/// Load from CSV (simple parser, ignores header)
public void loadFromFile(File f) throws IOException {
List<Student> tmp = new ArrayList<>();
try (BufferedReader br = new BufferedReader(new FileReader(f))) {
String line;
boolean first = true;
while ((line = br.readLine()) != null) {
if (first && line.toLowerCase().startsWith("id,")) { first = false; continue; }
first = false;
if (line.trim().isEmpty()) continue;
Student s = Student.fromCSVLine(line);
if (s != null) tmp.add(s);
}
}
// replace content
clear();
students.addAll(tmp);
}

// Simple search: returns list of indices matching the query (case-insensitive)
public List<Integer> searchIndices(String q) {
List<Integer> out = new ArrayList<>();
String lower = q == null ? "" : q.toLowerCase();
for (int i = 0; i < students.size(); i++) {
Student s = students.get(i);
if (s.getId().toLowerCase().contains(lower)
|| s.getFirstName().toLowerCase().contains(lower)
|| s.getLastName().toLowerCase().contains(lower)
|| s.getCourse().toLowerCase().contains(lower)) {
out.add(i);
}
}
return out;
}


public double averageGrade() {
if (students.isEmpty()) return 0.0;
double sum = 0.0;
for (Student s : students) sum += s.getGrade();
return sum / students.size();
}
}

