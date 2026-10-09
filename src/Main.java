import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ExpenseManager manager = new ExpenseManager();

    public static void main(String[] args) {
        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addExpense();
                case "2" -> manager.viewAllExpenses();
                case "3" -> editExpense();
                case "4" -> deleteExpense();
                case "5" -> searchExpenses();
                case "6" -> filterByCategory();
                case "7" -> manager.categoryWiseReport();
                case "8" -> monthlyReport();
                case "9" -> setBudget();
                case "10" -> manager.exportReport();
                case "11" -> {
                    System.out.println("Exiting Expense Tracker. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter 1-11.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n========== EXPENSE TRACKER ==========");
        System.out.println("1.  Add Expense");
        System.out.println("2.  View All Expenses");
        System.out.println("3.  Edit Expense");
        System.out.println("4.  Delete Expense");
        System.out.println("5.  Search Expenses");
        System.out.println("6.  Filter by Category");
        System.out.println("7.  Category-wise Report");
        System.out.println("8.  Monthly Report");
        System.out.println("9.  Set Monthly Budget");
        System.out.println("10. Export Expense Report");
        System.out.println("11. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addExpense() {
        try {
            LocalDate date = readDate();
            String category = readNonEmptyText("Enter category: ");
            String description = readNonEmptyText("Enter description: ");
            double amount = readPositiveAmount("Enter amount: ");
            String paymentMethod = readNonEmptyText("Enter payment method: ");

            manager.addExpense(date, category, description, amount, paymentMethod);
        } catch (Exception e) {
            System.out.println("Invalid input. Expense not added.");
        }
    }

    private static void editExpense() {
        try {
            System.out.print("Enter expense ID to edit: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (!manager.viewExpenseById(id)) {
                System.out.println("Expense ID not found.");
                return;
            }

            LocalDate date = readDate();
            String category = readNonEmptyText("Enter new category: ");
            String description = readNonEmptyText("Enter new description: ");
            double amount = readPositiveAmount("Enter new amount: ");
            String paymentMethod = readNonEmptyText("Enter new payment method: ");

            if (manager.editExpense(id, date, category, description, amount, paymentMethod)) {
                System.out.println("Expense updated successfully.");
            } else {
                System.out.println("Expense ID not found.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Expense not updated.");
        }
    }

    private static void deleteExpense() {
        try {
            System.out.print("Enter expense ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (manager.deleteExpense(id)) {
                System.out.println("Expense deleted successfully.");
            } else {
                System.out.println("Expense ID not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static void searchExpenses() {
        String keyword = readNonEmptyText("Enter keyword to search: ");
        manager.searchExpenses(keyword);
    }

    private static void filterByCategory() {
        String category = readNonEmptyText("Enter category: ");
        manager.filterByCategory(category);
    }

    private static void monthlyReport() {
        try {
            System.out.print("Enter year (YYYY): ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter month (1-12): ");
            int month = Integer.parseInt(scanner.nextLine().trim());

            if (month < 1 || month > 12) {
                System.out.println("Invalid month.");
                return;
            }

            manager.monthlyReport(year, month);
        } catch (NumberFormatException e) {
            System.out.println("Invalid year or month.");
        }
    }

    private static void setBudget() {
        try {
            double budget = readPositiveAmount("Enter monthly budget: ");
            manager.setMonthlyBudget(budget);
        } catch (Exception e) {
            System.out.println("Invalid budget amount.");
        }
    }

    private static LocalDate readDate() {
        while (true) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private static double readPositiveAmount(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                double amount = Double.parseDouble(input);

                if (amount > 0) {
                    return amount;
                }

                System.out.println("Amount must be greater than 0.");
            } catch (NumberFormatException e) {
                                System.out.println("Amount must be greater than 0.");
            }
        }
    }

    private static String readNonEmptyText(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("This field cannot be empty.");
        }
    }
}