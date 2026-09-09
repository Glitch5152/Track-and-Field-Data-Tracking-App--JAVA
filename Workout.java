import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Workout {
    private List<Double> times;
    private WorkoutType workoutType; 
    private LocalDate date;
    private String notes;

    public Workout(LocalDate date, WorkoutType workoutType) {
        this.workoutType = workoutType;
        this.date = date;
        this.times = new ArrayList<>();
        this.notes = "";
    }
    public List<Double> getTimes() {
        return times;
    }
    public void addTime(double time) {
        times.add(time);
    }
    public WorkoutType getWorkoutType() {
        return workoutType;
    }
    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public void removeTime(int index) {
        if (index >= 0 && index < times.size()) {
            times.remove(index);
        }
    }
    



}