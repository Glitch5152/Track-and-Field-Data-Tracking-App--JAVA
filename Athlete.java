import java.util.ArrayList;
import java.util.List;

public class Athlete {
    private String name;
    private int age;
    private String gender;
    private List<Workout> workouts;

    public Athlete(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.workouts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public List<Workout> getWorkouts() {
        return workouts;
    }
    public void addWorkout(Workout workout) {
        workouts.add(workout);
    }
    
    public void removeWorkout(int index) {
        if (index >= 0 && index < workouts.size()) {
            workouts.remove(index);
        }
    }
    
    @Override
    public String toString() {
        
        return name;
    }


}