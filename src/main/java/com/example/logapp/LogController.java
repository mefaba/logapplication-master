package com.example.logapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class LogController {
    public List<String> processLogEntries(String filePath, String includeTerm, String excludeTerm, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        List<String> resultEntries = new ArrayList<>();
        Set<String> uniqueThreadIdSet = new HashSet<>();
        int lineNumber = 0;
        try {
            Stream<String> lines = Files.lines(Paths.get(filePath));
            String previousThreadId = null;

            for (String line : lines.collect(Collectors.toList())) {
                lineNumber++;
                String[] parts = line.split(","); // Assuming there are 6 parts in each log entry
                if(parts.length < 3)
                {
                    continue;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd'T'HH:mm:ss.SSSZ']'");
                String logDateStr = parts[1];
                LocalDateTime logDate = LocalDateTime.parse(logDateStr,formatter);
                //String threadId = parts[2];

                if ((includeTerm.isEmpty() || line.contains(includeTerm)) &&
                        (excludeTerm.isEmpty() || !line.contains(excludeTerm)) && isDateInRange(logDate, startDate, endDate)) {
                    //uniqueThreadIdSet.add(threadId);
                    resultEntries.add(line);
                }

            }

            //TODO optimization
            /*Stream<String> lines2 = Files.lines(Paths.get(filePath));
            for (String line : lines2.collect(Collectors.toList())) {
                String[] parts = line.split(",");
                if(parts.length < 3)
                {
                    continue;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd'T'HH:mm:ss.SSSZ']'");
                String logDateStr = parts[1];
                LocalDate logDate = LocalDate.parse(logDateStr,formatter);
                String threadId = parts[2];
                if (uniqueThreadIdSet.contains(threadId) && isDateInRange(logDate, startDate, endDate)) {
                    resultEntries.add(line);
                }
            }*/
        }
        catch (Exception e){
            System.out.println(lineNumber);
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
