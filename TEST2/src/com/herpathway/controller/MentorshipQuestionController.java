
package com.herpathway.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class MentorshipQuestionController {

    @FXML
    private RadioButton verySatisfiedRadioButton;
    
    @FXML
    private RadioButton satisfiedRadioButton;
    
    @FXML
    private RadioButton neutralRadioButton;
    
    @FXML
    private RadioButton dissatisfiedRadioButton;
    
    @FXML
    private RadioButton veryDissatisfiedRadioButton;
    
    @FXML
    private CheckBox networkingCheckBox;
    
    @FXML
    private CheckBox learningSkillsCheckBox;
    
    @FXML
    private CheckBox careerAdviceCheckBox;
    
    @FXML
    private CheckBox personalDevelopmentCheckBox;
    
    @FXML
    private TextArea suggestionsTextArea;
    
    @FXML
    private VBox parentVBox; // Parent container for navigation
    
    private ToggleGroup satisfactionToggleGroup;

    @FXML
    public void initialize() {
        // Initialize radio buttons toggle group
        satisfactionToggleGroup = new ToggleGroup();
        verySatisfiedRadioButton.setToggleGroup(satisfactionToggleGroup);
        satisfiedRadioButton.setToggleGroup(satisfactionToggleGroup);
        neutralRadioButton.setToggleGroup(satisfactionToggleGroup);
        dissatisfiedRadioButton.setToggleGroup(satisfactionToggleGroup);
        veryDissatisfiedRadioButton.setToggleGroup(satisfactionToggleGroup);
    }
    
    @FXML
    private void handleSubmitButtonAction() {
        // Collect data from the form
        String satisfactionLevel = getSelectedSatisfactionLevel();
        String beneficialAspects = getSelectedBeneficialAspects();
        String suggestions = suggestionsTextArea.getText();
        
        // Example: Print collected data (you can store in database, etc.)
        System.out.println("Satisfaction Level: " + satisfactionLevel);
        System.out.println("Beneficial Aspects: " + beneficialAspects);
        System.out.println("Suggestions: " + suggestions);
        
        // Clear form or navigate back to previous page
        clearForm();
    }
    
    private String getSelectedSatisfactionLevel() {
        RadioButton selectedRadioButton = (RadioButton) satisfactionToggleGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText();
        }
        return "";
    }
    
    private String getSelectedBeneficialAspects() {
        StringBuilder selectedAspects = new StringBuilder();
        if (networkingCheckBox.isSelected()) {
            selectedAspects.append("Networking opportunities, ");
        }
        if (learningSkillsCheckBox.isSelected()) {
            selectedAspects.append("Learning new skills, ");
        }
        if (careerAdviceCheckBox.isSelected()) {
            selectedAspects.append("Career advice, ");
        }
        if (personalDevelopmentCheckBox.isSelected()) {
            selectedAspects.append("Personal development, ");
        }
        // Remove trailing comma and space
        return selectedAspects.length() > 0 ? selectedAspects.substring(0, selectedAspects.length() - 2) : "";
    }
    
    private void clearForm() {
        // Clear all selections and text fields
        satisfactionToggleGroup.selectToggle(null);
        networkingCheckBox.setSelected(false);
        learningSkillsCheckBox.setSelected(false);
        careerAdviceCheckBox.setSelected(false);
        personalDevelopmentCheckBox.setSelected(false);
        suggestionsTextArea.clear();
    }
}
