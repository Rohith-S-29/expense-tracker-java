import java.time.LocalDate;

public class Expense {
    private int id;
    private LocalDate date;
    private String category;
    private String description;
    private double amount;
    private String paymentMethod;

    public Expense(int id, LocalDate date, String category,
                   String description, double amount, String paymentMethod) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String toCsv() {
        return id + "," + date + "," + category + "," +
               description + "," + amount + "," + paymentMethod;
    }

    @Override
    public String toString() {
        return String.format(
            "%-4d | %-12s | %-15s | %-25s | Rs.%10.2f | %-12s",
            id, date, category, description, amount, paymentMethod
        );
    }
}