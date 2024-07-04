package com.herpathway.controller;
import javafx.stage.Stage;
import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.MentorData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class MentorMenu {

    @FXML
    private VBox mentorVBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField searchField;

    private ObservableList<MentorData> mentorDataList;

    @FXML
    public void initialize() {
        mentorDataList = FXCollections.observableArrayList();
        loadMentorData();
        displayMentorData();

        // Tambahkan listener ke field pencarian
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMentorData(newValue));
    }

    private void loadMentorData() {
        String query = "SELECT displayName, fieldOfStudy, lastEducationLevel, companyName, currentJob, profilePicture FROM form_Mentor";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String displayName = resultSet.getString("displayName");
                String fieldOfStudy = resultSet.getString("fieldOfStudy");
                String lastEducationLevel = resultSet.getString("lastEducationLevel");
                String companyName = resultSet.getString("companyName");
                String currentJob = resultSet.getString("currentJob");
                byte[] profilePictureData = resultSet.getBytes("profilePicture");

                Image profilePicture = null;
                if (profilePictureData != null) {
                    profilePicture = new Image(new ByteArrayInputStream(profilePictureData));
                }

                MentorData mentorData = new MentorData(displayName, fieldOfStudy, lastEducationLevel, companyName, currentJob, profilePicture);
                mentorDataList.add(mentorData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayMentorData() {
        mentorVBox.getChildren().clear(); // Hapus item yang ada
        for (MentorData mentor : mentorDataList) {
            HBox mentorBox = new HBox();
            mentorBox.setSpacing(10);
            mentorBox.setPadding(new Insets(10, 10, 10, 10)); // Tambahkan padding di sekitar setiap mentorBox

            ImageView profilePicture = new ImageView(mentor.getProfilePicture());
            profilePicture.setFitHeight(100);  // Ukuran ditingkatkan
            profilePicture.setFitWidth(100);   // Ukuran ditingkatkan

            VBox textBox = new VBox();
            textBox.setSpacing(5);
            Text displayName = new Text("Name : " + mentor.getDisplayName());
            displayName.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text fieldOfStudy = new Text("Field of study : " + mentor.getFieldOfStudy());
            fieldOfStudy.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text lastEducationLevel = new Text("Last level education : " + mentor.getLastEducationLevel());
            lastEducationLevel.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text companyName = new Text("Work at : " + mentor.getCompanyName());
            companyName.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text currentJob = new Text("Work as : " + mentor.getCurrentJob());
            currentJob.setFont(new Font(18));  // Ukuran font ditingkatkan
            textBox.getChildren().addAll(displayName, fieldOfStudy, lastEducationLevel, companyName, currentJob);

            Button viewProfileButton = new Button("Lihat Profil");
            viewProfileButton.setOnAction(e -> handleViewProfileButtonAction(mentor));

            mentorBox.getChildren().addAll(profilePicture, textBox, viewProfileButton);
            mentorVBox.getChildren().add(mentorBox);
        }
    }

    private void filterMentorData(String query) {
        if (query == null || query.isEmpty()) {
            displayMentorData(); // Jika query kosong, tampilkan semua data
        } else {
            ObservableList<MentorData> filteredList = mentorDataList.stream()
                    .filter(mentor -> mentor.getDisplayName().toLowerCase().contains(query.toLowerCase()) ||
                                      mentor.getFieldOfStudy().toLowerCase().contains(query.toLowerCase()) ||
                                      mentor.getLastEducationLevel().toLowerCase().contains(query.toLowerCase()) ||
                                      mentor.getCompanyName().toLowerCase().contains(query.toLowerCase()) ||
                                      mentor.getCurrentJob().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            displayFilteredMentorData(filteredList);
        }
    }

    private void displayFilteredMentorData(ObservableList<MentorData> filteredList) {
        mentorVBox.getChildren().clear(); // Hapus item yang ada
        for (MentorData mentor : filteredList) {
            HBox mentorBox = new HBox();
            mentorBox.setSpacing(10);
            mentorBox.setPadding(new Insets(10, 10, 10, 10)); // Tambahkan padding di sekitar setiap mentorBox

            ImageView profilePicture = new ImageView(mentor.getProfilePicture());
            profilePicture.setFitHeight(100);  // Ukuran ditingkatkan
            profilePicture.setFitWidth(100);   // Ukuran ditingkatkan

            VBox textBox = new VBox();
            textBox.setSpacing(5);
            Text displayName = new Text("Name " + mentor.getDisplayName());
            displayName.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text fieldOfStudy = new Text("Filed of study " + mentor.getFieldOfStudy());
            fieldOfStudy.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text lastEducationLevel = new Text("Last level education: " + mentor.getLastEducationLevel());
            lastEducationLevel.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text companyName = new Text("Work at: " + mentor.getCompanyName());
            companyName.setFont(new Font(18));  // Ukuran font ditingkatkan
            Text currentJob = new Text("Work as : " + mentor.getCurrentJob());
            currentJob.setFont(new Font(18));  // Ukuran font ditingkatkan
            textBox.getChildren().addAll(displayName, fieldOfStudy, lastEducationLevel, companyName, currentJob);

            Button viewProfileButton = new Button("Lihat Profil");
            viewProfileButton.setOnAction(e -> handleViewProfileButtonAction(mentor));

            mentorBox.getChildren().addAll(profilePicture, textBox, viewProfileButton);
            mentorVBox.getChildren().add(mentorBox);
        }
    }

    @FXML
    private void handleViewProfileButtonAction(MentorData mentor) {
        Stage stage = (Stage) mentorVBox.getScene().getWindow();
        if (stage != null) {
            HomepageController homepageController = (HomepageController) stage.getUserData();
            if (homepageController != null) {
                homepageController.switchToMentorProfile(mentor);
            } else {
                System.err.println("HomepageController is null.");
            }
        } else {
            System.err.println("Stage is null.");
        }
    }
    
    
    
}    
