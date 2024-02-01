package com.example.logapp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
public class LogController {
    /**
     * Processes log entries from the specified file based on given parameters.
     *
     * This method reads log entries from the specified file, extracts relevant information,
     * and filters entries based on inclusion/exclusion terms, and a specified date-time range.
     *
     * @param filePath      The path to the log file.
     * @param includeTerm   The inclusion term for filtering log entries (empty for no inclusion filter).
     * @param excludeTerm   The exclusion term for filtering log entries (empty for no exclusion filter).
     * @param startDate     The start date-time for filtering log entries.
     * @param endDate       The end date-time for filtering log entries.
     * @return A list of log entries that match the specified criteria.
     * @throws IOException If there is an error reading the log file.
     */
    public List<String> processLogEntries(String filePath, String includeTerm, String excludeTerm, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        List<String> resultEntries = new ArrayList<>();

        try{
            Stream<String> lines = Files.lines(Paths.get(filePath));
            boolean isLogAdded = false;

            for (String line : lines.toList()) {
                String[] parts;
                if (line.matches("\\[[a-zA-Z]*\\].*")) {
                    parts = line.split(",");
                }
                else if(isLogAdded){
                    resultEntries.add(line);
                    continue;
                }else{
                    continue;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd'T'HH:mm:ss.SSSZ']'");
                String logDateStr = parts[1];
                LocalDateTime logDate = LocalDateTime.parse(logDateStr,formatter);

                if ((includeTerm.isEmpty() || line.contains(includeTerm)) &&
                        (excludeTerm.isEmpty() || !line.contains(excludeTerm)) && isDateInRange(logDate, startDate, endDate)) {
                    isLogAdded = true;
                    resultEntries.add(line);
                }else {
                    isLogAdded = false;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(resultEntries.size());
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
