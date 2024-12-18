package controller;

import com.nwaibe.DatabaseConnection;
import com.nwaibe.models.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {


    @FXML
    private TextField firstName;
    @FXML
    private TextField middleName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField email;
    @FXML
    private TextField address;
    @FXML
    private RadioButton radiobtn_male;
    @FXML
    private RadioButton radiobtn_female;
    @FXML
    private TableView<Student> table;
    @FXML
    private TableColumn<Student, String> colFN;
    @FXML
    private TableColumn<Student, String> colMN;
    @FXML
    private TableColumn<Student, String> colLN;
    @FXML
    private TableColumn<Student, String> colPN;
    @FXML
    private TableColumn<Student, String> colE;
    @FXML
    private TableColumn<Student, String> colA;

    public TableColumn<Student, String> colG;

    private DatabaseConnection connection;
    private boolean isEditing = false;
    private int studentId = 0;
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        connection = new DatabaseConnection();


        ToggleGroup genderGroup = new ToggleGroup();
        radiobtn_male.setToggleGroup(genderGroup);
        radiobtn_female.setToggleGroup(genderGroup);


        colFN.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        colMN.setCellValueFactory(new PropertyValueFactory<>("middle_name"));
        colLN.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        colPN.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        colE.setCellValueFactory(new PropertyValueFactory<>("email"));
        colA.setCellValueFactory(new PropertyValueFactory<>("address"));
        colG.setCellValueFactory(new PropertyValueFactory<>("gender"));

        loadStudents();
    }

    public void loadStudents() throws SQLException {
        studentList.clear();
        String sql = "SELECT * FROM students";
        Statement stmt = connection.getConnection().createStatement();
        ResultSet result = stmt.executeQuery(sql);
        while (result.next()) {
            Student student = new Student(
                    result.getInt("id"),
                    result.getString("first_name"),
                    result.getString("middle_name"),
                    result.getString("last_name"),
                    result.getString("phone_number"),
                    result.getString("email"),
                    result.getString("address"),
                    result.getString("gender")
            );
            studentList.add(student);
        }
        table.setItems(studentList);
    }

    @FXML
    private void Save() throws SQLException {

        // Determine gender from radio button selection
        String gender = radiobtn_male.isSelected() ? "Male" : radiobtn_female.isSelected() ? "Female" : "";

        if (!isEditing) {
            // Inserting a new student
            String sql = "INSERT INTO students(first_name, middle_name, last_name, phone_number, email, address, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
                stmt.setString(1, firstName.getText());
                stmt.setString(2, middleName.getText());
                stmt.setString(3, lastName.getText());
                stmt.setString(4, phoneNumber.getText());
                stmt.setString(5, email.getText());
                stmt.setString(6, address.getText());
                stmt.setString(7, gender);
                if (stmt.executeUpdate() == 1) {
                    clearForm(); // Clear form fields
                    loadStudents(); // Reload student data
                }
            }
        } else {
            // Updating an existing student
            String sql = "UPDATE students SET first_name = ?, middle_name = ?, last_name = ?, phone_number = ?, email = ?, address = ?, gender = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
                stmt.setString(1, firstName.getText());
                stmt.setString(2, middleName.getText());
                stmt.setString(3, lastName.getText());
                stmt.setString(4, phoneNumber.getText());
                stmt.setString(5, email.getText());
                stmt.setString(6, address.getText());
                stmt.setString(7, gender);
                stmt.setInt(8, studentId);
                if (stmt.executeUpdate() == 1) {
                    clearForm(); // Clear form fields
                    loadStudents(); // Reload student data
                }
            }
        }

        // Reset editing state
        isEditing = false;
        studentId = 0;
    }

    @FXML
    private void delete() {
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            String sql = "DELETE FROM students WHERE id = ?";
            try {
                PreparedStatement stmt = connection.getConnection().prepareStatement(sql);
                stmt.setInt(1, selectedStudent.getId());
                stmt.executeUpdate();
                studentList.remove(selectedStudent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void edit() {
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            firstName.setText(selectedStudent.getFirst_name());
            middleName.setText(selectedStudent.getMiddle_name());
            lastName.setText(selectedStudent.getLast_name());
            phoneNumber.setText(selectedStudent.getPhone_number());
            email.setText(selectedStudent.getEmail());
            address.setText(selectedStudent.getAddress());

            // Set the gender radio button based on the selected student's gender
            String gender = selectedStudent.getGender();
            if ("Male".equalsIgnoreCase(gender)) {
                radiobtn_male.setSelected(false);
            } else if ("Female".equalsIgnoreCase(gender)) {
                radiobtn_female.setSelected(false);
            }

            isEditing = true;
            studentId = selectedStudent.getId();
        }
    }
    private void clearForm() {
        firstName.clear();
        middleName.clear();
        lastName.clear();
        phoneNumber.clear();
        email.clear();
        address.clear();
        radiobtn_male.setSelected(false);
        radiobtn_female.setSelected(false);
    }
}
