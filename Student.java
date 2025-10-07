public class Student {
private String id;
private String firstName;
private String lastName;
private String dob; // simple string like 2005-03-21 (ISO-like). For production use java.time.LocalDate.
private String course;
private double grade; // 0.0 - 100.0


public Student(String id, String firstName, String lastName, String dob, String course, double grade) {
this.id = id;
this.firstName = firstName;
this.lastName = lastName;
this.dob = dob;
this.course = course;
this.grade = grade;
}


// Getters/Setters
public String getId() { return id; }
public void setId(String id) { this.id = id; }
public String getFirstName() { return firstName; }
public void setFirstName(String firstName) { this.firstName = firstName; }
public String getLastName() { return lastName; }
public void setLastName(String lastName) { this.lastName = lastName; }
public String getDob() { return dob; }
public void setDob(String dob) { this.dob = dob; }
public String getCourse() { return course; }
public void setCourse(String course) { this.course = course; }
public double getGrade() { return grade; }
public void setGrade(double grade) { this.grade = grade; }


public String getFullName() { return firstName + " " + lastName; }


// Simple CSV conversion. NOTE: commas in fields are replaced with semicolons to keep parsing simple.
public String toCSVLine() {
return escape(id) + "," + escape(firstName) + "," + escape(lastName) + "," + escape(dob) + "," + escape(course) + "," + grade;
}


private String escape(String s) {
if (s == null) return "";
return s.replace(",", ";");
}


public static Student fromCSVLine(String line) {
String[] parts = line.split(",", -1);
if (parts.length < 6) return null;
String id = parts[0];
String first = parts[1];
String last = parts[2];
String dob = parts[3];
String course = parts[4];
double grade = 0.0;
try { grade = Double.parseDouble(parts[5]); } catch (Exception e) { grade = 0.0; }
return new Student(id, first, last, dob, course, grade);
}
}

