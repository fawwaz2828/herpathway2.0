package com.herpathway.controller;

import com.herpathway.model.MenteeProfile;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Adds the mentee-only goal fields to {@link AbstractProfileFormController}. */
public abstract class AbstractMenteeFormController extends AbstractProfileFormController {

    @FXML
    protected TextField mainGoalField;
    @FXML
    protected TextField fieldOfStudyGoalField;
    @FXML
    protected TextField expectationsField;

    @Override
    protected List<TextField> allFields() {
        List<TextField> fields = new ArrayList<>(super.allFields());
        fields.add(mainGoalField);
        fields.add(fieldOfStudyGoalField);
        fields.add(expectationsField);
        return fields;
    }

    protected MenteeProfile readProfile(long userId) {
        MenteeProfile p = new MenteeProfile();
        readInto(p);
        p.setUserId(userId);
        p.setMainGoal(mainGoalField.getText());
        p.setFieldOfStudyGoal(fieldOfStudyGoalField.getText());
        p.setExpectations(expectationsField.getText());
        return p;
    }

    protected void fillFrom(MenteeProfile p) {
        super.fillFrom(p);
        mainGoalField.setText(p.getMainGoal());
        fieldOfStudyGoalField.setText(p.getFieldOfStudyGoal());
        expectationsField.setText(p.getExpectations());
    }
}
