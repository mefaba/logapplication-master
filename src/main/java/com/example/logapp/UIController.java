package com.example.logapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        //add zero padding for hours and minutes 1 => 01  (hours and minutes)
        startHourChoiceBox.setConverter(createZeroPadding());
        startMinuteChoiceBox.setConverter(createZeroPadding());
        endHourChoiceBox.setConverter(createZeroPadding());
        endMinuteChoiceBox.setConverter(createZeroPadding());

        //Set values for choice box. (hours and minutes)
        startHourChoiceBox.getItems().addAll(hoursArray);
        startMinuteChoiceBox.getItems().addAll(minutesArray);
        endHourChoiceBox.getItems().addAll(hoursArray);
        endMinuteChoiceBox.getItems().addAll(minutesArray);


        // Set default values
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
    /**
     * name: analyze()
     * description:
     * Analyzes log entries based on user input parameters and generates result log files.
     * This method retrieves input parameters such as inclusion/exclusion terms, output folder path,
     * selected files, start and end date-time range. It validates input paths, date range, and
     * processes log entries accordingly. The analysis results are then written to result log files.
     *
     * @throws IOException If there is an error processing log entries or writing result files.
     */
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

        //validate filePathField
        for (String selectedFile : selectedFilesArray) {
            if (!isValidFile(selectedFile)) {
                statusLabel.setText("Invalid input file path");
                return;
            }
        }

        //validate outputFolder field
        if (!isValidDirectory(outputFolder)) {
            statusLabel.setText("Invalid output folder path");
            return;
        }

        // Validate date range
        if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime)) {
            statusLabel.setText("Error: End date must be after start date.");
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


    private boolean isValidDirectory(String path) {
        Path directoryPath = Paths.get(path);
        return !path.isEmpty() && Files.isDirectory(directoryPath) && Files.isWritable(directoryPath);
    }

    private boolean isValidFile(String path) {
        Path filePath = Paths.get(path);
        return Files.isRegularFile(filePath) && Files.isReadable(filePath);
    }
}