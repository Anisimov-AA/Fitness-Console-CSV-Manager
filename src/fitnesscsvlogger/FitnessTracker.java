package fitnesscsvlogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Fitness Tracker system.
 *
 * This class provides a console-based user interface for managing fitness data.
 * Users can create, read, delete, load, and save fitness entries through an
 * interactive menu system. The application automatically loads existing data
 * on startup and provides data persistence through CSV files.
 *
 * Key features:
 * - Interactive console menu for all CRUD operations</li>
 * - Automatic data loading on application startup</li>
 * - CSV file import/export functionality</li>
 * - Data validation and error handling</li>
 * - User-friendly error messages and prompts</li>
 */
public class FitnessTracker {

  /** Default filename for fitness data storage */
  private static final String DATA_FILE = "fitness.csv";

  /** Menu option constants for better maintainability */
  private static final int CREATE_ENTRY = 1;
  private static final int READ_ENTRIES = 2;
  private static final int UPDATE_ENTRY = 3;
  private static final int DELETE_ENTRY = 4;
  private static final int LOAD_FROM_FILE = 5;
  private static final int SAVE_TO_FILE = 6;
  private static final int EXIT = 7;

  /** List of fitness entries managed by this tracker */
  private final List<FitnessEntry> data;

  /** Scanner for user input throughout the application */
  private final Scanner scanner;

  /**
   * Creates a new FitnessTracker instance.
   *
   * <p>Initializes the data storage and input scanner. The fitness data
   * list starts empty and will be populated when {@link #loadData()} is called.</p>
   */
  public FitnessTracker() {
    this.data = new ArrayList<>();
    this.scanner = new Scanner(System.in);
  }

  /**
   * Application entry point.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    new FitnessTracker().run();
  }

  /**
   * Runs the main application loop.
   *
   * <p>Loads existing data, then enters the interactive menu loop where
   * users can perform various fitness tracking operations. The loop
   * continues until the user chooses to exit.</p>
   */
  public void run() {
    loadData();

    while (true) {
      showMenu();
      int choice = getMenuChoice();

      switch (choice) {
        case CREATE_ENTRY:
          createEntry();
          break;
        case READ_ENTRIES:
          readAllEntries();
          break;
        case UPDATE_ENTRY:
          updateEntry();
          break;
        case DELETE_ENTRY:
          deleteEntry();
          break;
        case LOAD_FROM_FILE:
          loadFromFile();
          break;
        case SAVE_TO_FILE:
          saveData();
          break;
        case EXIT:
          return;
        default:
          System.out.println("Invalid choice! Please select 1-7.");
      }

      // Only pause after operations that show results
      if (choice >= 1 && choice <= 6) {
        waitForUserInput();
      }
    }
  }

  /**
   * Automatically loads existing fitness data when the program starts.
   *
   * <p>Users expect their previously saved data to be available when they
   * restart the program. This method runs once at startup to restore their
   * data from the default fitness data file.</p>
   */
  private void loadData() {
    if (CsvHandler.fileExists(DATA_FILE)) {
      List<FitnessEntry> loaded = CsvHandler.loadFromFile(DATA_FILE);
      data.clear();
      data.addAll(loaded);
      if (loaded.size() > 0) {
        System.out.println("Loaded " + loaded.size() + " entries.");
      }
    }
  }

  /**
   * Displays the main menu options with current entry count.
   *
   * <p>Shows all available operations and the current number of fitness
   * entries to give users context about their data.</p>
   */
  private void showMenu() {
    System.out.println(
        "\n=== Fitness Tracker (" + data.size() + " entries) ===" + "\n" +
            "1. Create  2. Read  3. Update  4. Delete  5. Load  6. Save  7. Exit" + "\n" +
            "Choice: ");
  }

  /**
   * Gets and validates the user's menu choice.
   *
   * @return the selected menu option number
   */
  private int getMenuChoice() {
    try {
      int choice = scanner.nextInt();
      scanner.nextLine(); // consume the newline
      return choice;
    } catch (Exception e) {
      scanner.nextLine(); // clear invalid input
      return -1; // invalid choice
    }
  }

  /**
   * Handles creating new fitness entries through user input.
   *
   * <p>Prompts the user for all required fitness metrics, validates the input,
   * and adds the new entry to the data collection. Provides clear error
   * messages if validation fails.</p>
   */
  private void createEntry() {
    try {
      System.out.print("Date (YYYY-MM-DD): ");
      String date = scanner.nextLine().trim();

      System.out.print("Heart rate: ");
      int heartRate = Integer.parseInt(scanner.nextLine().trim());

      System.out.print("Steps: ");
      int steps = Integer.parseInt(scanner.nextLine().trim());

      System.out.print("Calories: ");
      int calories = Integer.parseInt(scanner.nextLine().trim());

      System.out.print("Sleep hours: ");
      double sleep = parseDoubleInput(scanner.nextLine().trim());

      System.out.print("Weight: ");
      double weight = parseDoubleInput(scanner.nextLine().trim());

      FitnessEntry newEntry = new FitnessEntry(date, heartRate, steps, calories, sleep, weight);
      data.add(newEntry);

      System.out.println("Entry created!");

    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid number format.");
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  /**
   * Displays all fitness entries to the user.
   *
   * <p>Shows each entry with a number for easy reference during delete
   * operations. If no entries exist, displays an appropriate message.</p>
   */
  private void readAllEntries() {
    if (data.isEmpty()) {
      System.out.println("No entries found.");
      return;
    }

    for (int i = 0; i < data.size(); i++) {
      System.out.printf("%d. %s%n", (i + 1), data.get(i));
    }
  }

  /**
   * Handles updating existing fitness entries through user input.
   *
   * <p>Shows all current entries, prompts the user to select one for updating,
   * then allows modification of individual fields. Users can skip fields they
   * don't want to change by pressing Enter. Creates a new entry with updated
   * values and replaces the old one.</p>
   */
  private void updateEntry() {
    if (data.isEmpty()) {
      System.out.println("No entries to update.");
      return;
    }

    readAllEntries();

    try {
      System.out.print("Entry number to update: ");
      int num = Integer.parseInt(scanner.nextLine().trim());

      if (num < 1 || num > data.size()) {
        System.out.println("Invalid entry number.");
        return;
      }

      FitnessEntry current = data.get(num - 1);
      System.out.println("Current: " + current);
      System.out.println("Enter new values (press Enter to keep current):");

      String date = getUpdatedStringValue("Date", current.getDate());
      int heartRate = getUpdatedIntValue("Heart rate", current.getHeartRate());
      int steps = getUpdatedIntValue("Steps", current.getSteps());
      int calories = getUpdatedIntValue("Calories", current.getCalories());
      double sleep = getUpdatedDoubleValue("Sleep", current.getSleep());
      double weight = getUpdatedDoubleValue("Weight", current.getWeight());

      FitnessEntry updated = new FitnessEntry(date, heartRate, steps, calories, sleep, weight);
      data.set(num - 1, updated);

      System.out.println("Entry updated!");

    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid number.");
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  /**
   * Handles deleting fitness entries from the system.
   *
   * <p>Shows all current entries, prompts the user to select one for deletion,
   * validates the selection, and removes the entry. Provides confirmation
   * of the deletion.</p>
   */
  private void deleteEntry() {
    System.out.println("\n=== Delete Fitness Entry ===");

    if (data.isEmpty()) {
      System.out.println("No entries to delete.");
      return;
    }

    readAllEntries();

    try {
      System.out.print("\nEntry number to delete (1-" + data.size() + "): ");
      int num = Integer.parseInt(scanner.nextLine().trim());

      if (num < 1 || num > data.size()) {
        System.out.println("❌ Invalid entry number. Please select 1-" + data.size() + ".");
        return;
      }

      FitnessEntry deletedEntry = data.remove(num - 1);
      System.out.println("✅ Entry deleted successfully!");
      System.out.println("Deleted: " + deletedEntry);

    } catch (NumberFormatException e) {
      System.out.println("❌ Please enter a valid number.");
    }
  }

  /**
   * Allows users to import fitness data from any CSV file.
   *
   * <p>Users might want to import data from different sources such as backup
   * files, data from other applications, or shared fitness data. This method
   * prompts for a filename and adds the loaded entries to the current data set.</p>
   */
  private void loadFromFile() {
    System.out.print("Filename (Enter for default): ");
    String filename = scanner.nextLine().trim();

    if (filename.isEmpty()) {
      filename = DATA_FILE;
    }

    List<FitnessEntry> loaded = CsvHandler.loadFromFile(filename);

    if (!loaded.isEmpty()) {
      data.addAll(loaded);
      System.out.println("Loaded " + loaded.size() + " entries.");
    } else {
      System.out.println("No data loaded.");
    }
  }

  /**
   * Saves all current fitness data to the default file.
   *
   * <p>Users expect their data to be saved reliably. This method provides
   * a simple way to save all current data to the standard location with
   * clear confirmation of the operation's success.</p>
   */
  private void saveData() {
    if (data.isEmpty()) {
      System.out.println("No data to save.");
      return;
    }

    if (CsvHandler.saveToFile(data, DATA_FILE)) {
      System.out.println("Saved " + data.size() + " entries.");
    } else {
      System.out.println("Save failed.");
    }
  }

  /**
   * Waits for user input before continuing to the next menu.
   *
   * <p>Provides a pause in the application flow so users can read
   * the results of their operations before the menu appears again.</p>
   */
  private void waitForUserInput() {
    System.out.print("\nPress Enter to continue...");
    scanner.nextLine();
  }

  /**
   * Parses a double value from user input with locale support.
   *
   * <p>Handles both comma and dot decimal separators to accommodate
   * different user locale preferences.</p>
   *
   * @param input the string input from the user
   * @return parsed double value
   * @throws NumberFormatException if the input cannot be parsed as a double
   */
  private double parseDoubleInput(String input) {
    return Double.parseDouble(input.replace(',', '.'));
  }

  /**
   * Gets the current number of fitness entries.
   *
   * @return the size of the fitness data collection
   */
  public int getEntryCount() {
    return data.size();
  }

  /**
   * Gets a copy of the current fitness entries.
   *
   * @return a new list containing all current fitness entries
   */
  public List<FitnessEntry> getEntries() {
    return new ArrayList<>(data);
  }

  /**
   * Gets an updated string value from user input, keeping current value if empty.
   *
   * @param fieldName the name of the field being updated
   * @param currentValue the current value to keep if user enters nothing
   * @return the updated value or current value if input is empty
   */
  private String getUpdatedStringValue(String fieldName, String currentValue) {
    System.out.print(fieldName + " (" + currentValue + "): ");
    String input = scanner.nextLine().trim();
    return input.isEmpty() ? currentValue : input;
  }

  /**
   * Gets an updated integer value from user input, keeping current value if empty.
   *
   * @param fieldName the name of the field being updated
   * @param currentValue the current value to keep if user enters nothing
   * @return the updated value or current value if input is empty
   * @throws NumberFormatException if the input is not a valid integer
   */
  private int getUpdatedIntValue(String fieldName, int currentValue) {
    System.out.print(fieldName + " (" + currentValue + "): ");
    String input = scanner.nextLine().trim();
    return input.isEmpty() ? currentValue : Integer.parseInt(input);
  }

  /**
   * Gets an updated double value from user input, keeping current value if empty.
   *
   * @param fieldName the name of the field being updated
   * @param currentValue the current value to keep if user enters nothing
   * @return the updated value or current value if input is empty
   * @throws NumberFormatException if the input is not a valid double
   */
  private double getUpdatedDoubleValue(String fieldName, double currentValue) {
    System.out.print(fieldName + " (" + currentValue + "): ");
    String input = scanner.nextLine().trim();
    return input.isEmpty() ? currentValue : parseDoubleInput(input);
  }
}