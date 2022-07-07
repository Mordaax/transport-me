package sg.edu.np.mad.transportme;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Expense {
    private LocalDate date;
    private LocalTime time;
    private String cost;
    private String selected;

    public Expense(LocalDate date, LocalTime time, String cost, String selected) {
        this.date = date;
        this.time = time;
        this.cost = cost;
        this.selected = selected;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public static ArrayList<Expense> expenseArrayList = new ArrayList<>();

    public static ArrayList<Expense> expensePerDate(LocalDate date){
        ArrayList<Expense> expenses = new ArrayList<>();
        for(Expense expense : expenseArrayList)
        {
            if (expense.getDate().equals(date))
            {
                expenses.add(expense);
            }
        }
        return expenses;
    }

}
