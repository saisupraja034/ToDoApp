import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ToDoApp extends JFrame {
    private DefaultListModel<String> model = new DefaultListModel<>();
    private JList<String> taskList = new JList<>(model);
    private JTextField input = new JTextField(20);
    private static final Path SAVE_FILE = Paths.get("tasks.txt");

    public ToDoApp() {
        super("To-Do App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        JButton addBtn = new JButton("Add");
        top.add(input);
        top.add(addBtn);
        add(top, BorderLayout.NORTH);

        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(taskList);
        scroll.setPreferredSize(new Dimension(350, 300));
        add(scroll, BorderLayout.CENTER);

        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton deleteBtn = new JButton("Delete");
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        Runnable addTaskAction = () -> {
            String text = input.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a task first", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            model.addElement(text);
            input.setText("");
            input.requestFocus();
        };
        addBtn.addActionListener(e -> addTaskAction.run());
        input.addActionListener(e -> addTaskAction.run()); // Enter key adds

        deleteBtn.addActionListener(e -> {
            int ix = taskList.getSelectedIndex();
            if (ix != -1) model.remove(ix);
            else JOptionPane.showMessageDialog(this, "Select a task to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        loadTasks();

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadTasks() {
        if (Files.exists(SAVE_FILE)) {
            try {
                List<String> lines = Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8);
                for (String l : lines) if (!l.isBlank()) model.addElement(l);
            } catch (IOException ex) {
                System.err.println("Could not load tasks: " + ex.getMessage());
            }
        }
    }

    private void saveTasks() {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) out.add(model.get(i));
        try {
            Files.write(SAVE_FILE, out, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.err.println("Could not save tasks: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoApp::new);
    }
}
