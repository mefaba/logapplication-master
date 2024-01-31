package com.example.logapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class UIController {

    private final LogController LogController = new LogController();
    @FXML
    private TextField filePathField;

    @FXML
    private TextField outputFolderField;

    @FXML
    private CheckBox singleFileCheckbox;

    @FXML
    private TextField includeField;
    @FXML
    private TextField excludeField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ChoiceBox<Integer> startHourChoiceBox;

    @FXML
    private ChoiceBox<Integer> startMinuteChoiceBox;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ChoiceBox<Integer> endHourChoiceBox;

    @FXML
    private ChoiceBox<Integer> endMinuteChoiceBox;

    @FXML
    private Label statusLabel;




    @FXML
    private void initialize() {
        // Initialize any additional setup if needed
        Integer[] hoursArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
        Integer[] minutesArray = {0, 10, 20, 30, 40, 50, 59};
        //add zero padding 1 => 01
        startHourChoiceBox.setConverter(createZeroPadding());
        startMinuteChoiceBox.setConverter(createZeroPadding());
        endHourChoiceBox.setConverter(createZeroPadding());
        endMinuteChoiceBox.setConverter(createZeroPadding());

        //Set values
        startHourChoiceBox.getItems().addAll(hoursArray);
        startMinuteChoiceBox.getItems().addAll(minutesArray);
        endHourChoiceBox.getItems().addAll(hoursArray);
        endMinuteChoiceBox.getItems().addAll(minutesArray);


        // Set default values or prompt texts
        startHourChoiceBox.getSelectionModel().selectFirst(); // Select the first item as default
        startMinuteChoiceBox.getSelectionModel().selectFirst();
        endHourChoiceBox.getSelectionModel().selectFirst();
        endMinuteChoiceBox.getSelectionModel().selectFirst();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void browseFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            filePathField.setText(selectedFiles.toString().replace("[", "").replace("]", ""));
        }
    }

    @FXML
    private void browseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Folder");

        File selectedFolder = directoryChooser.showDialog(null);
        if (selectedFolder != null) {
            outputFolderField.setText(selectedFolder.toString());
        }
    }

    @FXML
    private void analyze() {
        String includeTerm = includeField.getText();
        String excludeTerm = excludeField.getText();
        String outputFolder = outputFolderField.getText();
        String[] selectedFilesArray = filePathField.getText().split(", ");  // Assuming files are separated by ','
        LocalDate startDate = startDatePicker.getValue();
        LocalTime startTime = LocalTime.of(startHourChoiceBox.getValue(), startMinuteChoiceBox.getValue());
        LocalDateTime startDateTime = LocalDateTime.of(startDate,startTime);
        LocalDate endDate = endDatePicker.getValue();
        LocalTime endTime = LocalTime.of(endHourChoiceBox.getValue(), endMinuteChoiceBox.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(endDate,endTime);

        // Validate date range
        if (startDate.isAfter(endDate)) {
            statusLabel.setText("Error: Start date cannot be after end date.");
            return;
        }
        // Process log entries
        if(singleFileCheckbox.isSelected())
        {
            //Generate single output file regardless.
            String outputFilePath = outputFolder + File.separator + "result_log_entries.log";
            for(String selectedFile: selectedFilesArray)
            {
                try {
                    List<String> resultEntries = LogController.processLogEntries(selectedFile, includeTerm, excludeTerm, startDateTime, endDateTime);
                    // Output the result entries to a new log file
                    if (!resultEntries.isEmpty()) {
                        LogController.appendResultToFile(resultEntries, outputFilePath);
                        statusLabel.setText("Result log files created at: " + outputFolder);
                    } else {
                        statusLabel.setText("No matching entries found.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    statusLabel.setText("Error processing log entries.");
                }
            }
        }
        else {
            //Generate an output file for each input file
            for(String selectedFile: selectedFilesArray)
            {
                try {
                    List<String> resultEntries = LogController.processLogEntries(selectedFile, includeTerm, excludeTerm, startDateTime, endDateTime);
                    // Output the result entries to a new log file
                    if (!resultEntries.isEmpty()) {
                        String fileName = new File(selectedFile).getName();
                        String outputFilePath = outputFolder + File.separator + "result_" + fileName;
                        LogController.writeResultToFile(resultEntries, outputFilePath);
                        statusLabel.setText("Result log file created at: " + outputFilePath);
                    } else {
                        statusLabel.setText("No matching entries found.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    statusLabel.setText("Error processing log entries.");
                }
            }
        }
    }

    private StringConverter<Integer> createZeroPadding() {
        return new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                return String.format("%02d", object);
            }

            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        };
    }
}