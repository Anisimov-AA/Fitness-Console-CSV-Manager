package fitnesscsvlogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Handles all CSV file I/O operations for fitness data persistence.
 *  This utility class provides static methods for saving and loading
 *  {@link FitnessEntry} objects to/from CSV files. It automatically manages
 *  the data directory structure and handles file format validation.
 */
public class CsvHandler {

  private static final String DATA_DIRECTORY = "data"; // default data directory name
  private static final int EXPECTED_CSV_COLUMNS = 6; // expected number of CSV columns for fitness entries

  /**
   * Creates the data directory if it doesn't exist.
   *
   * This method ensures the data storage location is available
   * before attempting file operations. Called automatically by
   * save operations.
   */
  private static void ensureDataDirectory() {
    File dataDir = new File(DATA_DIRECTORY);
    if (!dataDir.exists()) {
      dataDir.mkdirs();
    }
  }

  /**
   * Converts a filename to its full path with data directory prefix.
   *
   * If the filename already includes the data directory path,
   * it is returned unchanged. Otherwise, the data directory prefix
   * is added automatically.
   *
   * @param filename the base filename
   * @return the full path including data directory
   */
  private static String getFullPath(String filename) {
    if (filename.startsWith(DATA_DIRECTORY + "/") || filename.startsWith(DATA_DIRECTORY + "\\")) {
      return filename;
    }
    return DATA_DIRECTORY + "/" + filename;
  }

  /**
   * Saves a list of fitness entries to a CSV file.
   *
   * @param entries the list of fitness entries to save, must not be null
   * @param filename the target CSV filename (data/ prefix added automatically)
   * @return true if save was successful, false if an error occurred
   * @throws IllegalArgumentException if entries is null
   */
  public static boolean saveToFile(List<FitnessEntry> entries, String filename) {
    if (entries == null) {
      throw new IllegalArgumentException("Entries list cannot be null");
    }

    ensureDataDirectory();
    String fullPath = getFullPath(filename);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
      // write CSV header
      writer.write(FitnessEntry.getCsvHeader());
      writer.newLine();

      // write each fitness entry
      for (FitnessEntry entry : entries) {
        writer.write(entry.toCsvLine());
        writer.newLine();
      }

      return true;

    } catch (IOException e) {
      System.err.println("Save failed for file '" + filename + "': " + e.getMessage());
      return false;
    }
  }

  /**
   * Loads fitness entries from a CSV file.
   *
   * @param filename the CSV filename to load from (data/ prefix added automatically)
   * @return list of successfully parsed fitness entries (never null, may be empty)
   */
  public static List<FitnessEntry> loadFromFile(String filename) {
    String fullPath = getFullPath(filename);
    List<FitnessEntry> entries = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
      // Skip header line
      reader.readLine();

      // Process each data line
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          try {
            entries.add(parseLine(line));
          } catch (Exception e) {
            System.err.println("Warning: Skipping invalid line '" + line + "' - " + e.getMessage());
          }
        }
      }

    } catch (IOException e) {
      System.err.println("Load failed for file '" + filename + "': " + e.getMessage());
    }

    return entries;
  }

  /**
   * Parses a single CSV line into a FitnessEntry object.
   *
   * @param line the CSV line to parse, must not be null or empty
   * @return new FitnessEntry with parsed values
   * @throws IllegalArgumentException if line format is invalid or parsing fails
   */
  private static FitnessEntry parseLine(String line) {
    if (line == null || line.trim().isEmpty()) {
      throw new IllegalArgumentException("Line cannot be null or empty");
    }

    String[] parts = line.split(",");
    if (parts.length != EXPECTED_CSV_COLUMNS) {
      throw new IllegalArgumentException(
          String.format("Invalid CSV format - expected %d columns, got %d",
              EXPECTED_CSV_COLUMNS, parts.length));
    }

    try {
      return new FitnessEntry(
          parts[0].trim(),
          Integer.parseInt(parts[1].trim()),
          Integer.parseInt(parts[2].trim()),
          Integer.parseInt(parts[3].trim()),
          Double.parseDouble(parts[4].trim().replace(',', '.')),
          Double.parseDouble(parts[5].trim().replace(',', '.'))
      );
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number format in line: " + line, e);
    }
  }

  /**
   * Checks if a CSV file exists in the data directory.
   *
   * @param filename the CSV filename to check (data/ prefix added automatically)
   * @return true if the file exists and is readable, false otherwise
   */
  public static boolean fileExists(String filename) {
    String fullPath = getFullPath(filename);
    return new File(fullPath).exists();
  }

}
