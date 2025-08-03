package fitnesscsvlogger;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CsvHandlerTest {

  private static final String TEST_FILENAME = "test_fitness.csv";
  private List<FitnessEntry> testEntries;

  /**
   * Sets up test data before each test method.
   *
   */
  @BeforeEach
  void setUp() {
    testEntries = new ArrayList<>();
    testEntries.add(new FitnessEntry("2024-01-01", 70, 7500, 400, 8.0, 71.0));
    testEntries.add(new FitnessEntry("2024-01-02", 75, 8200, 450, 7.5, 70.8));
    testEntries.add(new FitnessEntry("2024-01-03", 68, 6800, 380, 8.5, 71.2));
  }

  /**
   * Cleans up test files after each test method
   */
  @AfterEach
  void tearDown() {
    // Clean up test file
    File testFile = new File("data/" + TEST_FILENAME);
    if (testFile.exists()) {
      testFile.delete();
    }
  }

  /**
   * Tests successful save and load cycle with valid fitness data.
   *
   * Test Scenario: Save a list of fitness entries,
   * then load them back and verify all data matches exactly
   */
  @Test
  void testSaveAndLoadCycle() {
    // given - test data is set up in @BeforeEach

    // when - save entries to file
    boolean saveResult = CsvHandler.saveToFile(testEntries, TEST_FILENAME);

    // then - save should succeed
    assertTrue(saveResult, "Save operation should succeed");
    assertTrue(CsvHandler.fileExists(TEST_FILENAME), "File should exist after save");

    // when - load entries from file
    List<FitnessEntry> loadedEntries = CsvHandler.loadFromFile(TEST_FILENAME);

    // then - loaded data should match original
    assertEquals(testEntries.size(), loadedEntries.size(), "Should load same number of entries");

    for (int i = 0; i < testEntries.size(); i++) {
      FitnessEntry original = testEntries.get(i);
      FitnessEntry loaded = loadedEntries.get(i);

      assertEquals(original.getDate(), loaded.getDate(), "Date should match");
      assertEquals(original.getHeartRate(), loaded.getHeartRate(), "Heart rate should match");
      assertEquals(original.getSteps(), loaded.getSteps(), "Steps should match");
      assertEquals(original.getCalories(), loaded.getCalories(), "Calories should match");
      assertEquals(original.getSleep(), loaded.getSleep(), 0.01, "Sleep should match");
      assertEquals(original.getWeight(), loaded.getWeight(), 0.01, "Weight should match");
    }
  }

  /**
   * Tests error handling for invalid input parameters
   */
  @Test
  void testInvalidInputHandling() {
    // Test null entries list
    assertThrows(IllegalArgumentException.class,
        () -> CsvHandler.saveToFile(null, TEST_FILENAME),
        "Should throw exception for null entries list");

    // Test loading non-existent file
    List<FitnessEntry> result = CsvHandler.loadFromFile("nonexistent.csv");
    assertNotNull(result, "Should return non-null list for missing file");
    assertTrue(result.isEmpty(), "Should return empty list for missing file");

    // Test file existence check
    assertFalse(CsvHandler.fileExists("nonexistent.csv"),
        "Should return false for non-existent file");
  }

  /**
   *  Tests handling of empty data and edge cases
   *
   * Scenarios:
   * Empty entries list should create valid CSV with header only
   * Loading empty CSV should return empty list
   * File operations should create data directory automatically
   */
  @Test
  void testEdgeCases() throws IOException {
    // Test saving empty list
    List<FitnessEntry> emptyList = new ArrayList<>();
    boolean saveResult = CsvHandler.saveToFile(emptyList, TEST_FILENAME);

    assertTrue(saveResult, "Should successfully save empty list");
    assertTrue(CsvHandler.fileExists(TEST_FILENAME), "File should exist even with empty data");

    // Verify file contains header
    Path filePath = Path.of("data/" + TEST_FILENAME);
    List<String> lines = Files.readAllLines(filePath);
    assertEquals(1, lines.size(), "Empty data file should contain header only");
    assertEquals(FitnessEntry.getCsvHeader(), lines.get(0), "Header should match expected format");

    // Test loading from header-only file
    List<FitnessEntry> loadedEntries = CsvHandler.loadFromFile(TEST_FILENAME);
    assertNotNull(loadedEntries, "Should return non-null list");
    assertTrue(loadedEntries.isEmpty(), "Should return empty list for header-only file");

    // Verify data directory was created
    File dataDir = new File("data");
    assertTrue(dataDir.exists(), "Data directory should be created automatically");
    assertTrue(dataDir.isDirectory(), "Data path should be a directory");
  }

  /**
   * Tests CSV format validation and malformed data handling
   *
   * Test Scenario:
   * Create a CSV file with mixed valid
   * and invalid lines, then verify only valid lines are loaded while
   * invalid lines are skipped with appropriate logging
   */
  @Test
  void testMalformedDataHandling() throws IOException {
    // Create a CSV file with mixed valid and invalid lines
    String csvContent = FitnessEntry.getCsvHeader() + "\n" +
        "2024-01-01,70,7500,400,8.0,71.0\n" +  // Valid line
        "invalid,line,with,wrong,format\n" +     // Invalid - wrong format
        "2024-01-02,75,8200,450,7.5,70.8\n" +   // Valid line
        "2024-01-03,invalid,8000,400,8.0,71.0\n"; // Invalid - bad number

    // Write test file directly
    Path testPath = Path.of("data/" + TEST_FILENAME);
    Files.createDirectories(testPath.getParent());
    Files.writeString(testPath, csvContent);

    // Load and verify only valid entries are returned
    List<FitnessEntry> loadedEntries = CsvHandler.loadFromFile(TEST_FILENAME);

    assertEquals(2, loadedEntries.size(), "Should load only valid entries");
    assertEquals("2024-01-01", loadedEntries.get(0).getDate(), "First entry should be valid");
    assertEquals("2024-01-02", loadedEntries.get(1).getDate(), "Second entry should be valid");
  }
}