import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Trackapp extends JFrame {
    private List<Athlete> athletes;
    private List<WorkoutType> workoutTypes;

    private JTabbedPane tabbedPane;
    private JComboBox<Athlete> athleteComboBox;
    private JComboBox<WorkoutType> workoutTypeComboBox;
    private JTextField timeField;
    private JTextArea timesDisplay;
    private JSpinner dateSpinner;
    
    // Components for manage athletes panel
    private JTable athleteTable;//explain how 
    private DefaultTableModel athleteTableModel;

    public Trackapp() {
        athletes = new ArrayList<>();
        workoutTypes = new ArrayList<>();
        
        // Load existing data from CSV files
        loadAllData();
        
        setTitle("Track Time App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initializeGui();
        





                
        updateAthleteComboBox();
        updateWorkoutTypeComboBox();
    }
    private void initializeGui() {
        tabbedPane = new JTabbedPane();

        // Tab 1: Add times here
        tabbedPane.addTab("Add Workout", createAddWorkoutPanel());
        
        // Tab 2: Check all workouts done here
        tabbedPane.addTab("View Workouts", createViewWorkoutsPanel());
        
        // Tab 3: Add edit and delete athletes here
        tabbedPane.addTab("Manage Athletes", createManageAthletesPanel());
        
        // Tab 4: create new workout types and edit existing ones
        tabbedPane.addTab("Manage Workout Types", createManageWorkoutTypesPanel());

        add(tabbedPane);
    }
    private JPanel createAddWorkoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());
        //Gridbaglayout allows for better control of where the components are, instead of being scattered everywhere
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        //insets adds space between the components.
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Athlete:"), gbc);
        gbc.gridx = 1;
        panel.add(topPanel, BorderLayout.NORTH);
        athleteComboBox = new JComboBox<>();
        
        topPanel.add(athleteComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Workout Type:"), gbc);
        gbc.gridx = 1;
        workoutTypeComboBox = new JComboBox<>();
        topPanel.add(workoutTypeComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        topPanel.add(dateSpinner, gbc);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel timeInputPanel = new JPanel(new FlowLayout());
        timeInputPanel.add(new JLabel("Time (seconds):"));
        timeField = new JTextField(10);
        timeInputPanel.add(timeField);
        JButton addTimeButton = new JButton("Add Time");
        addTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Athlete selectedAthlete = (Athlete) athleteComboBox.getSelectedItem();
                WorkoutType selectedWorkoutType = (WorkoutType) workoutTypeComboBox.getSelectedItem();
                if (selectedAthlete != null && selectedWorkoutType != null) {
                    try {
                        double time = Double.parseDouble(timeField.getText());
                        LocalDate date = LocalDate.parse(new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateSpinner.getValue()));
                        Workout workout = new Workout(date, selectedWorkoutType);
                        workout.addTime(time);
                        selectedAthlete.addWorkout(workout);
                        timesDisplay.append("Added time " + time + "s for " + selectedAthlete.getName() + " on " + date + " (" + selectedWorkoutType.getName() + ")\n");
                        timeField.setText("");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(Trackapp.this, "Invalid time format.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(Trackapp.this, "Please select an athlete and workout type.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        timeInputPanel.add(addTimeButton);
        centerPanel.add(timeInputPanel, BorderLayout.NORTH);
        timesDisplay = new JTextArea(10, 50);
        timesDisplay.setEditable(false);
        centerPanel.add(new JScrollPane(timesDisplay), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveWorkoutButton = new JButton("Save Workout");
        saveWorkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAllData();
            }
        });
        buttonPanel.add(saveWorkoutButton);
        JButton clearButton = new JButton("Clear Times");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Athlete selectedAthlete = (Athlete) athleteComboBox.getSelectedItem();
                WorkoutType selectedWorkoutType = (WorkoutType) workoutTypeComboBox.getSelectedItem();
                
                if (selectedAthlete != null && selectedWorkoutType != null) {
                    try {
                        // Get the selected date from the spinner
                        LocalDate selectedDate = LocalDate.parse(new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateSpinner.getValue()));
                        
                        // Find the specific workout that matches the criteria
                        Workout targetWorkout = null;
                        for (Workout workout : selectedAthlete.getWorkouts()) {
                            if (workout.getDate().equals(selectedDate) && 
                                workout.getWorkoutType().getName().equals(selectedWorkoutType.getName())) {
                                targetWorkout = workout;
                                break;
                            }
                        }
                        
                        if (targetWorkout != null) {
                            // Get the number of times before clearing
                            int timesCount = targetWorkout.getTimes().size();
                            
                            // Clear all times from the workout
                            targetWorkout.getTimes().clear();
                            
                            // Update the display
                            timesDisplay.append("Cleared " + timesCount + " times from " + selectedAthlete.getName() + 
                                            "'s " + selectedWorkoutType.getName() + " workout on " + selectedDate + "\n");
                            
                            JOptionPane.showMessageDialog(Trackapp.this, 
                                "Successfully cleared " + timesCount + " times from the workout.", 
                                "Times Cleared", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(Trackapp.this, 
                                "No workout found for " + selectedAthlete.getName() + " on " + selectedDate + 
                                " with workout type: " + selectedWorkoutType.getName(), 
                                "Workout Not Found", JOptionPane.WARNING_MESSAGE);
                        }
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Trackapp.this, 
                            "Error clearing times: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(Trackapp.this, 
                        "Please select an athlete and workout type.", 
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(clearButton);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;

    }
    private JPanel createViewWorkoutsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Filter Panel at the top
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Athlete Filter with dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Athlete:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> athleteFilterCombo = new JComboBox<>();
        athleteFilterCombo.addItem("All Athletes");
        for (Athlete athlete : athletes) {
            athleteFilterCombo.addItem(athlete.getName());
        }
        filterPanel.add(athleteFilterCombo, gbc);
        
        // Workout Type Filter with dropdown
        gbc.gridx = 0; gbc.gridy = 1;
        filterPanel.add(new JLabel("Workout Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> workoutTypeFilterCombo = new JComboBox<>();
        workoutTypeFilterCombo.addItem("All Workout Types");
        for (WorkoutType wt : workoutTypes) {
            workoutTypeFilterCombo.addItem(wt.getName());
        }
        filterPanel.add(workoutTypeFilterCombo, gbc);
        
        // Date Filter
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        filterPanel.add(new JLabel("Date Range:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox allDatesCheckbox = new JCheckBox("All Dates", true);
        JSpinner fromDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor fromDateEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
        fromDateSpinner.setEditor(fromDateEditor);
        fromDateSpinner.setEnabled(false);
        
        JSpinner toDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor toDateEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
        toDateSpinner.setEditor(toDateEditor);
        toDateSpinner.setEnabled(false);
        
        allDatesCheckbox.addActionListener(e -> {
            boolean allDates = allDatesCheckbox.isSelected();
            fromDateSpinner.setEnabled(!allDates);
            toDateSpinner.setEnabled(!allDates);
        });
        
        datePanel.add(allDatesCheckbox);
        datePanel.add(new JLabel("From:"));
        datePanel.add(fromDateSpinner);
        datePanel.add(new JLabel("To:"));
        datePanel.add(toDateSpinner);
        filterPanel.add(datePanel, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton displayButton = new JButton("Display Information");
        JButton clearFiltersButton = new JButton("Clear Filters");
        buttonPanel.add(displayButton);
        buttonPanel.add(clearFiltersButton);
        filterPanel.add(buttonPanel, gbc);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Results Table (removed Best Time, Avg Time, Notes columns)
        String[] columnNames = {"Athlete", "Date", "Workout Type", "Times"};
        DefaultTableModel workoutTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable workoutTable = new JTable(workoutTableModel);
        workoutTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane tableScroll = new JScrollPane(workoutTable);
        panel.add(tableScroll, BorderLayout.CENTER);
        
        
        JLabel statusLabel = new JLabel("Select filters and click 'Display Information' to view workouts");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        // Display Button Action
        displayButton.addActionListener(e -> {
            workoutTableModel.setRowCount(0); // Clear table
            
            // Get selected athlete
            String selectedAthleteName = (String) athleteFilterCombo.getSelectedItem();
            List<Athlete> selectedAthletes;
            if (selectedAthleteName.equals("All Athletes")) {
                selectedAthletes = athletes;
            } else {
                selectedAthletes = new ArrayList<>();
                for (Athlete athlete : athletes) {
                    if (athlete.getName().equals(selectedAthleteName)) {
                        selectedAthletes.add(athlete);
                        break;
                    }
                }
            }
            
            // Get selected workout type
            String selectedWorkoutTypeName = (String) workoutTypeFilterCombo.getSelectedItem();
            List<WorkoutType> selectedWorkoutTypes;
            if (selectedWorkoutTypeName.equals("All Workout Types")) {
                selectedWorkoutTypes = workoutTypes;
            } else {
                selectedWorkoutTypes = new ArrayList<>();
                for (WorkoutType wt : workoutTypes) {
                    if (wt.getName().equals(selectedWorkoutTypeName)) {
                        selectedWorkoutTypes.add(wt);
                        break;
                    }
                }
            }
            
            // Get date range
            LocalDate fromDate = null;
            LocalDate toDate = null;
            if (!allDatesCheckbox.isSelected()) {
                try {
                    fromDate = LocalDate.parse(new java.text.SimpleDateFormat("yyyy-MM-dd").format(fromDateSpinner.getValue()));
                    toDate = LocalDate.parse(new java.text.SimpleDateFormat("yyyy-MM-dd").format(toDateSpinner.getValue()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid date format", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Filter and display workouts
            int count = 0;
            for (Athlete athlete : selectedAthletes) {
                for (Workout workout : athlete.getWorkouts()) {
                    // Check if workout type matches
                    boolean typeMatches = false;
                    for (WorkoutType wt : selectedWorkoutTypes) {
                        if (workout.getWorkoutType().getName().equals(wt.getName())) {
                            typeMatches = true;
                            break;
                        }
                    }
                    
                    if (!typeMatches) continue;
                    
                    // Check if date is in range
                    if (fromDate != null && toDate != null) {
                        LocalDate workoutDate = workout.getDate();
                        if (workoutDate.isBefore(fromDate) || workoutDate.isAfter(toDate)) {
                            continue;
                        }
                    }
                    
                    // Build times string
                    StringBuilder timesStr = new StringBuilder();
                    for (int i = 0; i < workout.getTimes().size(); i++) {
                        if (i > 0) timesStr.append(", ");
                        timesStr.append(String.format("%.2f", workout.getTimes().get(i)));
                    }
                    
                    // Add row to table (only 4 columns now)
                    workoutTableModel.addRow(new Object[]{
                        athlete.getName(),
                        workout.getDate(),
                        workout.getWorkoutType().getName(),
                        timesStr.toString()
                    });
                    count++;
                }
            }
            
            statusLabel.setText("Displaying " + count + " workout(s)");
        });
        
        // Clear Filters Button Action
        clearFiltersButton.addActionListener(e -> {
            athleteFilterCombo.setSelectedIndex(0);
            workoutTypeFilterCombo.setSelectedIndex(0);
            allDatesCheckbox.setSelected(true);
            fromDateSpinner.setEnabled(false);
            toDateSpinner.setEnabled(false);
            workoutTableModel.setRowCount(0);
            statusLabel.setText("Filters cleared. Click 'Display Information' to view all workouts");
        });
        
        return panel;
    }
    
    private JPanel createManageAthletesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"Name", "Age", "Gender", "Workouts Count"};
        athleteTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        athleteTable = new JTable(athleteTableModel);
        athleteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(athleteTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Populate table
        refreshAthleteTable();
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Athlete");
        addButton.addActionListener(e -> {
            JTextField nameField = new JTextField(15);
            JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(18, 1, 100, 1));
            JComboBox<String> genderBox = new JComboBox<>(new String[]{"male", "female", "other"});
            
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Age:"));
            inputPanel.add(ageSpinner);
            inputPanel.add(new JLabel("Gender:"));
            inputPanel.add(genderBox);
            
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Athlete", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && !nameField.getText().trim().isEmpty()) {
                String name = nameField.getText().trim();
                boolean exists = athletes.stream().anyMatch(a -> a.getName().equalsIgnoreCase(name));
                if (exists) {
                    JOptionPane.showMessageDialog(this, "Athlete name already exists!");
                    return;
                }
                
                Athlete newAthlete = new Athlete(name, (Integer)ageSpinner.getValue(), (String)genderBox.getSelectedItem());
                athletes.add(newAthlete);
                refreshAthleteTable();
                updateAthleteComboBox();
                saveAthletes(); // Auto-save
                JOptionPane.showMessageDialog(this, "Athlete added!");
            }
        });
        
        JButton editButton = new JButton("Edit Athlete");
        editButton.addActionListener(e -> {
            int selectedRow = athleteTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an athlete to edit.");
                return;
            }
            
            Athlete athlete = athletes.get(selectedRow);
            JTextField nameField = new JTextField(athlete.getName(), 15);
            JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(athlete.getAge(), 1, 100, 1));
            JComboBox<String> genderBox = new JComboBox<>(new String[]{"male", "female", "other"});
            genderBox.setSelectedItem(athlete.getGender());
            
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Age:"));
            inputPanel.add(ageSpinner);
            inputPanel.add(new JLabel("Gender:"));
            inputPanel.add(genderBox);
            
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Athlete", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && !nameField.getText().trim().isEmpty()) {
                String name = nameField.getText().trim();
                boolean exists = athletes.stream().anyMatch(a -> a != athlete && a.getName().equalsIgnoreCase(name));
                if (exists) {
                    JOptionPane.showMessageDialog(this, "Athlete name already exists!");
                    return;
                }
                
                athlete.setName(name);
                athlete.setAge((Integer)ageSpinner.getValue());
                athlete.setGender((String)genderBox.getSelectedItem());
                refreshAthleteTable();
                updateAthleteComboBox();
                saveAthletes(); // Auto-save
                JOptionPane.showMessageDialog(this, "Athlete updated!");
            }
        });
        
        JButton deleteButton = new JButton("Delete Athlete");
        deleteButton.addActionListener(e -> {
            int selectedRow = athleteTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an athlete to delete.");
                return;
            }
            
            Athlete athlete = athletes.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete athlete '" + athlete.getName() + "'?\nThis will delete " + athlete.getWorkouts().size() + " workouts.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                athletes.remove(selectedRow);
                refreshAthleteTable();
                updateAthleteComboBox();
                saveAthletes(); // Auto-save
                saveWorkouts(); // Also save workouts since they changed
                JOptionPane.showMessageDialog(this, "Athlete deleted!");
            }
        });
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshAthleteTable());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(new JLabel("Athlete Management", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    private JPanel createManageWorkoutTypesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table setup
        String[] columnNames = {"Name", "Description", "Distance"};
        DefaultTableModel workoutTypeTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable workoutTypeTable = new JTable(workoutTypeTableModel);
        workoutTypeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(workoutTypeTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Populate table
        for (WorkoutType wt : workoutTypes) {
            workoutTypeTableModel.addRow(new Object[]{wt.getName(), wt.getDescription(), wt.getDistance()});
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add New");
        addButton.addActionListener(e -> {
            JTextField nameField = new JTextField(15);
            JTextField descField = new JTextField(15);
            JTextField distField = new JTextField(15);
            
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Description:"));
            inputPanel.add(descField);
            inputPanel.add(new JLabel("Distance:"));
            inputPanel.add(distField);
            
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Workout Type", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && !nameField.getText().trim().isEmpty()) {
                WorkoutType newWT = new WorkoutType(nameField.getText().trim(), descField.getText().trim(), distField.getText().trim());
                workoutTypes.add(newWT);
                workoutTypeTableModel.addRow(new Object[]{newWT.getName(), newWT.getDescription(), newWT.getDistance()});
                updateWorkoutTypeComboBox();
                saveWorkoutTypes(); // Auto-save
                JOptionPane.showMessageDialog(this, "Workout type added!");
            }
        });
        
        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener(e -> {
            int selectedRow = workoutTypeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a workout type to edit.");
                return;
            }
            
            WorkoutType wt = workoutTypes.get(selectedRow);
            JTextField nameField = new JTextField(wt.getName(), 15);
            JTextField descField = new JTextField(wt.getDescription(), 15);
            JTextField distField = new JTextField(wt.getDistance(), 15);
            
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Description:"));
            inputPanel.add(descField);
            inputPanel.add(new JLabel("Distance:"));
            inputPanel.add(distField);
            
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Workout Type", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && !nameField.getText().trim().isEmpty()) {
                wt.setName(nameField.getText().trim());
                wt.setDescription(descField.getText().trim());
                wt.setDistance(distField.getText().trim());
                workoutTypeTableModel.setValueAt(wt.getName(), selectedRow, 0);
                workoutTypeTableModel.setValueAt(wt.getDescription(), selectedRow, 1);
                workoutTypeTableModel.setValueAt(wt.getDistance(), selectedRow, 2);
                updateWorkoutTypeComboBox();
                saveWorkoutTypes(); // Auto-save
                JOptionPane.showMessageDialog(this, "Workout type updated!");
            }
        });
        
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> {
            int selectedRow = workoutTypeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a workout type to delete.");
                return;
            }
            
            WorkoutType wt = workoutTypes.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete workout type '" + wt.getName() + "'?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                workoutTypes.remove(selectedRow);
                workoutTypeTableModel.removeRow(selectedRow);
                updateWorkoutTypeComboBox();
                saveWorkoutTypes(); // Auto-save
                JOptionPane.showMessageDialog(this, "Workout type deleted!");
            }
        });
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            workoutTypeTableModel.setRowCount(0);
            for (WorkoutType wt : workoutTypes) {
                workoutTypeTableModel.addRow(new Object[]{wt.getName(), wt.getDescription(), wt.getDistance()});
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(new JLabel("Workout Type Management", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateAthleteComboBox() {
        athleteComboBox.removeAllItems();
        for (Athlete athlete : athletes) {
            athleteComboBox.addItem(athlete);
        }
    }
    private void updateWorkoutTypeComboBox() {
        workoutTypeComboBox.removeAllItems();
        for (WorkoutType wt : workoutTypes) {
            workoutTypeComboBox.addItem(wt);
        }
    }
    // Helper methods for athlete management
    private void refreshAthleteTable() {
        athleteTableModel.setRowCount(0); 
        for (Athlete athlete : athletes) {
            Object[] rowData = {
                athlete.getName(),
                athlete.getAge(),
                athlete.getGender(),
                athlete.getWorkouts().size()
            };
            athleteTableModel.addRow(rowData);
        }
    }
    
    // CSV Save/Load Functions
    
    // Save all athletes to CSV
    private void saveAthletes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("athletes.csv"))) {
            writer.println("Name,Age,Gender");
            for (Athlete athlete : athletes) {
                writer.println(escape(athlete.getName()) + "," + 
                    athlete.getAge() + "," + 
                    escape(athlete.getGender()));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving athletes: " + e.getMessage());
        }
    }
    
    // Load athletes from CSV
    private void loadAthletes() {
        File file = new File("athletes.csv");
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length >= 3) {
                    String name = parts[0];
                    int age = Integer.parseInt(parts[1]);
                    String gender = parts[2];
                    athletes.add(new Athlete(name, age, gender));
                }
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading athletes: " + e.getMessage());
        }
    }
    
    // Save all workout types to CSV
    private void saveWorkoutTypes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("workout_types.csv"))) {
            writer.println("Name,Description,Distance");
            for (WorkoutType wt : workoutTypes) {
                writer.println(escape(wt.getName()) + "," + 
                    escape(wt.getDescription()) + "," + 
                    escape(wt.getDistance()));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving workout types: " + e.getMessage());
        }
    }
    
    // Load workout types from CSV
    private void loadWorkoutTypes() {
        File file = new File("workout_types.csv");
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length >= 3) {
                    String name = parts[0];
                    String description = parts[1];
                    String distance = parts[2];
                    workoutTypes.add(new WorkoutType(name, description, distance));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading workout types: " + e.getMessage());
        }
    }
    
    // Save all workouts for all athletes to CSV
    private void saveWorkouts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("workouts.csv"))) {
            writer.println("AthleteName,Date,WorkoutTypeName,Times,Notes");
            for (Athlete athlete : athletes) {
                for (Workout workout : athlete.getWorkouts()) {
                    StringBuilder times = new StringBuilder();
                    for (int i = 0; i < workout.getTimes().size(); i++) {
                        if (i > 0) times.append(";");
                        times.append(workout.getTimes().get(i));
                    }
                    writer.println(escape(athlete.getName()) + "," + 
                        workout.getDate() + "," + 
                        escape(workout.getWorkoutType().getName()) + "," + 
                        escape(times.toString()) + "," + 
                        escape(workout.getNotes()));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving workouts: " + e.getMessage());
        }
    }
    
    // Load workouts from CSV
    private void loadWorkouts() {
        File file = new File("workouts.csv");
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length >= 5) {
                    String athleteName = parts[0];
                    LocalDate date = LocalDate.parse(parts[1]);
                    String workoutTypeName = parts[2];
                    String timesStr = parts[3];
                    String notes = parts[4];
                    
                    // Find the athlete
                    Athlete athlete = null;
                    for (Athlete a : athletes) {
                        if (a.getName().equals(athleteName)) {
                            athlete = a;
                            break;
                        }
                    }
                    
                    // Find the workout type
                    WorkoutType workoutType = null;
                    for (WorkoutType wt : workoutTypes) {
                        if (wt.getName().equals(workoutTypeName)) {
                            workoutType = wt;
                            break;
                        }
                    }
                    
                    if (athlete != null && workoutType != null) {
                        Workout workout = new Workout(date, workoutType);
                        workout.setNotes(notes);
                        
                        // Parse times
                        if (!timesStr.isEmpty()) {
                            String[] timeArray = timesStr.split(";");
                            for (String timeStr : timeArray) {
                                try {
                                    workout.addTime(Double.parseDouble(timeStr));
                                } catch (NumberFormatException e) {
                                    // Skip invalid times
                                }
                            }
                        }
                        
                        athlete.addWorkout(workout);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading workouts: " + e.getMessage());
        }
    }
    
    // Save all data
    public void saveAllData() {
        saveAthletes();
        saveWorkoutTypes();
        saveWorkouts();
        JOptionPane.showMessageDialog(this, "All data saved successfully!");
    }
    
    // Load all data
    private void loadAllData() {
        loadAthletes();
        loadWorkoutTypes();
        loadWorkouts();
    }
    
    // Helper method to escape CSV special characters
    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    // Helper method to parse CSV line with proper quote handling
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    
}
