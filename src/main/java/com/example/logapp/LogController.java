package com.example.logapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
public class LogController {
    /**
     * Processes log entries from the specified file based on given parameters.
     * This method reads log entries from the specified file, extracts relevant information,
     * and filters entries based on inclusion/exclusion terms, and a specified date-time range.
     *
     * @param filePath      The path to the log file.
     * @param includeTerms   The inclusion terms for filtering log entries (empty for no inclusion filter).
     * @param excludeTerms   The exclusion terms for filtering log entries (empty for no exclusion filter).
     * @param startDate     The start date-time for filtering log entries.
     * @param endDate       The end date-time for filtering log entries.
     * @return A list of log entries that match the specified criteria.
     * @throws IOException If there is an error reading the log file.
     */
    public List<String> processLogEntries(String filePath, String[] includeTerms, String[] excludeTerms, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        List<String> resultEntries = new ArrayList<>();

        try{
            Stream<String> lines = Files.lines(Paths.get(filePath));
            boolean isParentLogActive = false;

            for (String line : lines.toList()) {
                String[] parts;
                if (line.matches("\\[\\w*\\s*].*")) {
                    parts = line.split(",");
                }
                else if(isParentLogActive){
                    resultEntries.add(line);
                    continue;
                }else{
                    continue;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd'T'HH:mm:ss.SSSZ']'");
                String logDateStr = parts[1];
                LocalDateTime logDate = LocalDateTime.parse(logDateStr,formatter);

                if ((Arrays.stream(includeTerms).anyMatch(term -> line.contains(term))) &&
                        (Arrays.stream(excludeTerms).anyMatch(term -> line.contains(term))) && isDateInRange(logDate, startDate, endDate)) {
                    isParentLogActive = true;
                    resultEntries.add(line);
                }else {
                    isParentLogActive = false;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return resultEntries;
    }

    public boolean isDateInRange(LocalDateTime logDate, LocalDateTime startDate, LocalDateTime endDate) {
        return (startDate == null || !logDate.isBefore(startDate)) && (endDate == null || !logDate.isAfter(endDate));
    }

    public void writeResultToFile(List<String> resultEntries, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (String entry : resultEntries) {
                writer.write(entry);
                writer.newLine();
            }
        }
    }

    public void appendResultToFile(List<String> resultEntries, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            for (String entry : resultEntries) {
                writer.write(entry);
                writer.newLine();
            }
        }
    }
}
