package fitnesscsvlogger;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FitnessEntryTest {

  /**
   * Tests successful creation of a FitnessEntry with valid input data
   */
  @Test
  void testValidCreation() {
    String date = "2024-01-01";
    int heartRate = 70;
    int steps = 7500;
    int calories = 400;
    double sleep = 8.0;
    double weight = 71.0;

    FitnessEntry entry = new FitnessEntry(date, heartRate, steps, calories, sleep, weight);

    assertEquals(date, entry.getDate());
    assertEquals(heartRate, entry.getHeartRate());
    assertEquals(steps, entry.getSteps());
    assertEquals(calories, entry.getCalories());
    assertEquals(sleep, entry.getSleep());
    assertEquals(weight, entry.getWeight());
  }

  /**
   * Tests that invalid input data properly triggers validation exceptions
   */
  @Test
  void testInvalidInputValidation() {
    // null date
    assertThrows(IllegalArgumentException.class,
        () -> new FitnessEntry(null, 70, 7500, 400, 8.0, 71.0));

    // invalid heart rate
    assertThrows(IllegalArgumentException.class,
        () -> new FitnessEntry("2024-01-01", -10, 7500, 400, 8.0, 71.0));

    // invalid sleep hours
    assertThrows(IllegalArgumentException.class,
        () -> new FitnessEntry("2024-01-01", 70, 7500, 400, 25.0, 71.0));
  }

  /**
   * Tests CSV serialization functionality for data persistence
   */
  @Test
  void testCsvConversion() {
    FitnessEntry entry = new FitnessEntry("2024-01-01", 70, 7500, 400, 8.0, 71.0);

    String csvLine = entry.toCsvLine();
    String header = FitnessEntry.getCsvHeader();

    assertEquals("2024-01-01,70,7500,400,8.0,71.0", csvLine);
    assertEquals("Date,HeartRate,Steps,Calories,Sleep,Weight", header);
    assertTrue(csvLine.contains("2024-01-01"));
    assertTrue(csvLine.contains("70"));
  }

}