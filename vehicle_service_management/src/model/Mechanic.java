package model;

public class Mechanic {
    private int mechanicId;
    private String name;
    private String specialization;
    private String phone;
    private String availability;
    private double rating;
    private int activeJobs;
    private int completedJobs;

    public Mechanic() {}

    public Mechanic(int mechanicId, String name, String specialization, String phone, String availability, double rating) {
        this.mechanicId = mechanicId;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.availability = availability;
        this.rating = rating;
    }

    public int getMechanicId() { return mechanicId; }
    public void setMechanicId(int mechanicId) { this.mechanicId = mechanicId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getActiveJobs() { return activeJobs; }
    public void setActiveJobs(int activeJobs) { this.activeJobs = activeJobs; }
    public int getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(int completedJobs) { this.completedJobs = completedJobs; }

    /**
     * Returns mechanic name + specialization for display in JComboBox dropdowns.
     */
    @Override
    public String toString() {
        if (name == null) return "Unassigned";
        return name + (specialization != null && !specialization.isEmpty() ? " (" + specialization + ")" : "");
    }
}
