public class WorkoutType {
    private String name;
    private String description;
    private String distance; 

    public WorkoutType(String name, String description, String distance) {
        this.name = name;
        this.description = description;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
    
    @Override
    public String toString() {
        
        return name;
    }
}