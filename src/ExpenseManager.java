import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpenseManager {
    private ArrayList<Expense> expenses = new ArrayList<>();
    private int nextId = 1;
    private double monthlyBudget = 0;

    private final String dataFile = "data/expenses.csv";
    private final String budgetFile = "data/budget.txt";

    public ExpenseManager() {
        loadExpenses();
        loadBudget();
    }

    public void addExpense(LocalDate date, String category, String description,
                           double amount, String paymentMethod) {
        Expense expense = new Expense(
            nextId++, date, category, description, amount, paymentMethod
        );

        expenses.add(expense);
        saveExpenses();

        System.out.println("Expense added successfully with ID: " + expense.getId());
        checkBudget();
    }

    public void viewAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        printHeader();

        for (Expense e : expenses) {
            System.out.println(e);
        }

        System.out.printf("TOTAL: Rs. %.2f%n", getTotalExpense());
    }

    public boolean viewExpenseById(int id) {
        Expense expense = findExpenseById(id);

        if (expense == null) {
            return false;
        }

        printHeader();
        System.out.println(expense);
        return true;
    }

    public boolean editExpense(int id, LocalDate date, String category,
                               String description, double amount, String paymentMethod) {
        Expense expense = findExpenseById(id);

        if (expense == null) {
            return false;
        }

        expense.setDate(date);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setPaymentMethod(paymentMethod);

        saveExpenses();
        return true;
    }

    public boolean deleteExpense(int id) {
        Expense expense = findExpenseById(id);

        if (expense == null) {
            return false;
        }

        expenses.remove(expense);
        saveExpenses();
        return true;
    }

    public void searchExpenses(String keyword) {
        boolean found = false;

        System.out.println("\nSearch results for: " + keyword);
        printHeader();

        for (Expense e : expenses) {
            if (e.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                e.getCategory().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(e);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching expenses found.");
        }
    }

    public void filterByCategory(String category) {
        boolean found = false;
        double total = 0;

        System.out.println("\nExpenses in category: " + category);
        printHeader();

        for (Expense e : expenses) {
            if (e.getCategory().equalsIgnoreCase(category)) {
                System.out.println(e);
                total += e.getAmount();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No expenses found for this category.");
        } else {
            System.out.printf("CATEGORY TOTAL: Rs. %.2f%n", total);
        }
    }

    public void categoryWiseReport() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses available.");
            return;
        }

        LinkedHashMap<String, Double> categoryTotals = new LinkedHashMap<>();

        for (Expense e : expenses) {
            categoryTotals.put(
                e.getCategory(),
                categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount()
            );
        }

        System.out.println("\n===== CATEGORY-WISE EXPENSE REPORT =====");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.printf("%-20s : Rs. %.2f%n", entry.getKey(), entry.getValue());
        }

        System.out.printf("%-20s : Rs. %.2f%n", "TOTAL", getTotalExpense());
    }

    public void monthlyReport(int year, int month) {
        double total = 0;
        int count = 0;

        System.out.println("\n===== MONTHLY REPORT: " + YearMonth.of(year, month) + " =====");
        printHeader();

        for (Expense e : expenses) {
            if (e.getDate().getYear() == year && e.getDate().getMonthValue() == month) {
                System.out.println(e);
                total += e.getAmount();
                count++;
            }
        }

                if (count == 0) {
            System.out.println("No expenses found for this month.");
            return;
        }

        System.out.printf("TOTAL EXPENSES: Rs. %.2f%n", total);
        System.out.printf("AVERAGE EXPENSE: Rs. %.2f%n", total / count);

        if (monthlyBudget > 0) {
            System.out.printf("MONTHLY BUDGET: Rs. %.2f%n", monthlyBudget);

            if (total > monthlyBudget) {
                System.out.printf("OVER BUDGET BY: Rs. %.2f%n", total - monthlyBudget);
            } else {
                System.out.printf("REMAINING BUDGET: Rs. %.2f%n", monthlyBudget - total);
            }
        }
    }

    public void setMonthlyBudget(double budget) {
        this.monthlyBudget = budget;
        saveBudget();
        System.out.println("Monthly budget set to Rs. " + budget);
    }

    public void checkBudget() {
        if (monthlyBudget <= 0) {
            return;
        }

        YearMonth currentMonth = YearMonth.now();
        double currentMonthTotal = 0;

        for (Expense e : expenses) {
            if (YearMonth.from(e.getDate()).equals(currentMonth)) {
                currentMonthTotal += e.getAmount();
            }
        }

        System.out.printf("Current month spending: Rs. %.2f / Rs. %.2f%n",
                currentMonthTotal, monthlyBudget);

        if (currentMonthTotal > monthlyBudget) {
            System.out.println("WARNING: You have exceeded your monthly budget!");
        }
    }

    public void exportReport() {
        String fileName = "reports/expense_report_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
                + ".csv";

        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("ID,Date,Category,Description,Amount,Payment Method\n");

            for (Expense e : expenses) {
                writer.write(e.toCsv());
                writer.newLine();
            }

            writer.close();
            System.out.println("Report exported successfully: " + fileName);
        } catch (IOException e) {
            System.out.println("Error exporting report: " + e.getMessage());
        }
    }

    private Expense findExpenseById(int id) {
        for (Expense e : expenses) {
            if (e.getId() == id) {
                return e;
            }
        }

        return null;
    }

    private double getTotalExpense() {
        double total = 0;

        for (Expense e : expenses) {
            total += e.getAmount();
        }

        return total;
    }

    private void printHeader() {
        System.out.println("\nID   | DATE         | CATEGORY        | DESCRIPTION               | AMOUNT        | PAYMENT");
        System.out.println("---------------------------------------------------------------------------------------------");
    }

    private void saveExpenses() {
        try {
            File file = new File(dataFile);
            file.getParentFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (Expense e : expenses) {
                writer.write(e.toCsv());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        File file = new File(dataFile);

        if (!file.exists()) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);

                if (parts.length == 6) {
                    int id = Integer.parseInt(parts[0]);
                    LocalDate date = LocalDate.parse(parts[1]);
                    String category = parts[2];
                    String description = parts[3];
                    double amount = Double.parseDouble(parts[4]);
                    String paymentMethod = parts[5];

                    expenses.add(new Expense(
                        id, date, category, description, amount, paymentMethod
                    ));

                    if (id >= nextId) {
                        nextId = id + 1;
                    }
                }
            }

            reader.close();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    private void saveBudget() {
        try {
            File file = new File(budgetFile);
            file.getParentFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(String.valueOf(monthlyBudget));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving budget: " + e.getMessage());
        }
    }

    private void loadBudget() {
        File file = new File(budgetFile);

        if (!file.exists()) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            if (line != null) {
                monthlyBudget = Double.parseDouble(line);
            }

            reader.close();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading budget: " + e.getMessage());
        }
    }
}