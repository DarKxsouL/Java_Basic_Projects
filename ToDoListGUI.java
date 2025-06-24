import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

class Task {
    String title, category;
    LocalDate deadline;
    boolean isCompleted;

    public Task(String title, String category, LocalDate deadline, boolean isCompleted) {
        this.title = title;
        this.category = category;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return "[" + (isCompleted ? "✔" : "✘") + "] " + title + " | " + category + " | " + deadline;
    }

    public String toFileString() {
        return title + ";" + category + ";" + deadline + ";" + isCompleted;
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split(";");
        return new Task(parts[0], parts[1], LocalDate.parse(parts[2]), Boolean.parseBoolean(parts[3]));
    }
}

public class ToDoListGUI extends JFrame {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final DefaultListModel<Task> listModel = new DefaultListModel<>();
    private final JList<Task> taskList = new JList<>(listModel);
    private final JTextField titleField = new JTextField(10);
    private final JTextField categoryField = new JTextField(10);
    private final JTextField deadlineField = new JTextField(10);
    private final String FILE_NAME = "tasks.txt";

    public ToDoListGUI() {
        setTitle("📝 To-Do List App");
        setSize(650, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Deadline (yyyy-MM-dd):"));
        inputPanel.add(deadlineField);

        JButton addButton = new JButton("➕ Add Task");
        addButton.addActionListener(e -> addTask());
        inputPanel.add(addButton);
        add(inputPanel, BorderLayout.NORTH);

        // Task List with Custom Renderer
        taskList.setCellRenderer(new TaskCellRenderer());
        JScrollPane scrollPane = new JScrollPane(taskList);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton completeBtn = new JButton("✔ Mark Completed");
        completeBtn.addActionListener(e -> markCompleted());
        JButton deleteBtn = new JButton("🗑 Delete Task");
        deleteBtn.addActionListener(e -> deleteTask());
        JButton saveBtn = new JButton("💾 Save");
        saveBtn.addActionListener(e -> saveTasksToFile());
        JButton loadBtn = new JButton("📂 Load");
        loadBtn.addActionListener(e -> {
            loadTasksFromFile();
            showReminders();
        });

        buttonPanel.add(completeBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadTasksFromFile(); // Load on start
        showReminders();     // Show due reminders
        setVisible(true);
    }

    private void addTask() {
        String title = titleField.getText().trim();
        String category = categoryField.getText().trim();
        String deadlineStr = deadlineField.getText().trim();

        if (title.isEmpty() || category.isEmpty() || deadlineStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            LocalDate deadline = LocalDate.parse(deadlineStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Task task = new Task(title, category, deadline, false);
            tasks.add(task);
            sortAndRefresh();
            titleField.setText("");
            categoryField.setText("");
            deadlineField.setText("");

            if (deadline.equals(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "🔔 Reminder: Task is due today!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.");
        }
    }

    private void sortAndRefresh() {
        tasks.sort(Comparator.comparing(t -> t.deadline));
        refreshTaskList();
    }

    private void refreshTaskList() {
        listModel.clear();
        for (Task task : tasks) {
            listModel.addElement(task);
        }
    }

    private void markCompleted() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            tasks.get(index).isCompleted = true;
            sortAndRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to mark as completed.");
        }
    }

    private void deleteTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            tasks.remove(index);
            sortAndRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to delete.");
        }
    }

    private void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.write(task.toFileString());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "✅ Tasks saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks.");
        }
    }

    private void loadTasksFromFile() {
        tasks.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(Task.fromFileString(line));
            }
            sortAndRefresh();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks.");
        }
    }

    private void showReminders() {
        LocalDate today = LocalDate.now();
        StringBuilder reminders = new StringBuilder();
        for (Task task : tasks) {
            if (!task.isCompleted && (task.deadline.isBefore(today) || task.deadline.equals(today))) {
                reminders.append("• ").append(task.title).append(" (").append(task.deadline).append(")\n");
            }
        }
        if (!reminders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "🔔 You have tasks due or overdue:\n" + reminders);
        }
    }

    // Custom cell renderer for coloring
    class TaskCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            if (value instanceof Task task) {
                if (!task.isCompleted) {
                    if (task.deadline.isBefore(LocalDate.now())) {
                        label.setForeground(Color.RED);
                    } else if (task.deadline.equals(LocalDate.now())) {
                        label.setForeground(Color.ORANGE);
                    } else {
                        label.setForeground(new Color(0, 128, 0)); // greenish
                    }
                } else {
                    label.setForeground(Color.GRAY);
                }
            }
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListGUI::new);
    }
}
