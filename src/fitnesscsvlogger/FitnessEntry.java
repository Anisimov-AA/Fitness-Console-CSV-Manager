package fitnesscsvlogger;

/**
 * This class represents a single fitness data entry for a specific date
 */
public class FitnessEntry {
  // Private fields for fitness data
  private final String date;
  private final int heartRate;
  private final int steps;
  private final int calories;
  private final double sleep;
  private final double weight;

  /**
   * Creates a new fitness entry with comprehensive validation.
   *
   * @param date the date in any format (will be trimmed), must not be null or empty
   * @param heartRate the heart rate in beats per minute, must be positive
   * @param steps the number of steps taken, must be non-negative
   * @param calories the calories burned, must be non-negative
   * @param sleep the hours of sleep, must be between 0 and 24 inclusive
   * @param weight the weight in kilograms, must be positive
   * @throws IllegalArgumentException if any parameter fails validation
   */
  public FitnessEntry(String date, int heartRate, int steps, int calories, double sleep, double weight) {
    // validate inputs
    if (date == null || date.trim().isEmpty()) {
      throw new IllegalArgumentException("Date cannot be null or empty");
    }
    if (heartRate <= 0) {
      throw new IllegalArgumentException("Heart rate must be positive");
    }
    if (steps < 0) {
      throw new IllegalArgumentException("Steps cannot be negative");
    }
    if (calories < 0) {
      throw new IllegalArgumentException("Calories cannot be negative");
    }
    if (sleep < 0 || sleep > 24) {
      throw new IllegalArgumentException("Sleep must be between 0 and 24 hours");
    }
    if (weight <= 0) {
      throw new IllegalArgumentException("Weight must be positive");
    }

    // initialize fields - made final for immutability
    this.date = date.trim();
    this.heartRate = heartRate;
    this.steps = steps;
    this.calories = calories;
    this.sleep = sleep;
    this.weight = weight;
  }

  // Getter methods
  public String getDate() {
    return date;
  }

  public int getHeartRate() {
    return heartRate;
  }

  public int getSteps() {
    return steps;
  }

  public int getCalories() {
    return calories;
  }

  public double getSleep() {
    return sleep;
  }

  public double getWeight() {
    return weight;
  }

  /**
   * String representation for display
   */
  @Override
  public String toString() {
    return String.format(
        "%s | HR: %d | Steps: %d | Calories: %d | Sleep: %.1fh | Weight: %.1fkg",
        date, heartRate, steps, calories, sleep, weight
    );
  }

  /**
   * Converts this fitness entry to CSV format for file storage.
   * Uses US locale formatting to ensure consistent decimal separators
   *
   * @return comma-separated values representing this entry
   */
  public String toCsvLine() {
    return String.format(
        java.util.Locale.US,
        "%s,%d,%d,%d,%.1f,%.1f",
        date, heartRate, steps, calories, sleep, weight
    );
  }

  /**
   * Get the CSV header line
   *
   * @return the CSV header string: "Date,HeartRate,Steps,Calories,Sleep,Weight"
   */
  public static String getCsvHeader() {
    return "Date,HeartRate,Steps,Calories,Sleep,Weight";
  }
}