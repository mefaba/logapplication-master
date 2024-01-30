package com.example.logapp;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class LogControllerTest {
    LogController LogController = new LogController();
    @Test
    void processLogEntries() throws IOException {
        // Define the date format
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
//        // Parse the strings to LocalDate
//        LocalDate date1 = LocalDate.parse("January 1, 2024", formatter);
//        LocalDate date2 = LocalDate.parse("January 26, 2024", formatter);
//        LogController.processLogEntries("C:\\Temp\\core_cuvwa00a3106.ent.wfb.bank.corp12152023_122739\\debug_core.log.20231212.1", "ServerCertRenewalTimer",date1,date2);

    }

    @Test
    void isDateInRange() {
    }

    @Test
    void writeResultToFile() {
    }
}