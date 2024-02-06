package com.example.logapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class LogControllerTest {
    LogController LogController;

    @BeforeEach
    void setUp() {
        LogController = new LogController();
    }
    @Test
    void processLogEntries_ValidInput_ValidOutput_Scenario1() throws IOException {
        // Data for testing
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy H:m");
        LocalDateTime date1 = LocalDateTime.parse("January 1 2024 00:00", formatter);
        LocalDateTime date2 = LocalDateTime.parse("January 26 2024 00:00", formatter);
        String[] includeTerms = "ServerCertRenewalTimer".split(", ");
        String[] excludeTerms ="[DEBUG]".split(", ");
        String filepath = "src/test/resources/debug_dm.log.20240119.8";
        //Run unit test
        LogController.processLogEntries(filepath, includeTerms, excludeTerms,date1,date2);

    }

    @Test
    void processLogEntries_ValidInput_ValidOutput_Scenario2() throws IOException {
        // Data for testing
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy H:m");
        LocalDateTime date1 = LocalDateTime.parse("January 1 2024 00:00", formatter);
        LocalDateTime date2 = LocalDateTime.parse("January 26 2024 00:00", formatter);
        String[] includeTerms = "ServerCertRenewalTimer".split(", ");
        String[] excludeTerms ="[DEBUG]".split(", ");
        String filepath = "src/test/resources/debug_dm.log.20240119.8";
        //Run unit test
        LogController.processLogEntries(filepath, includeTerms, excludeTerms,date1,date2);

    }

    @Test
    void processLogEntries_ValidInput_ValidOutput_Scenario3() throws IOException {
        // Data for testing
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy H:m");
        LocalDateTime date1 = LocalDateTime.parse("January 1 2024 00:00", formatter);
        LocalDateTime date2 = LocalDateTime.parse("January 26 2024 00:00", formatter);
        String[] includeTerms = "ServerCertRenewalTimer".split(", ");
        String[] excludeTerms ="[DEBUG]".split(", ");
        String filepath = "src/test/resources/debug_dm.log.20240119.8";
        //Run unit test
        LogController.processLogEntries(filepath, includeTerms, excludeTerms,date1,date2);

    }
}