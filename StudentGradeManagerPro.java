import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Student {
    private String name;
    private int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
}

public class StudentGradeManagerPro extends JFrame {
    private JTextField nameField, scoreField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel avgLabel, highLabel, lowLabel;
    private ArrayList<Student> students;

    public StudentGradeManagerPro() {
        students = new ArrayList<>();

        
        setTitle("🎓 Student Grade Manager");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

    
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Student Details"));
        inputPanel.setBackground(new Color(240, 248, 255));

        nameField = new JTextField();
        scoreField = new JTextField();

        inputPanel.add(new JLabel("Student Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Score:"));
        inputPanel.add(scoreField);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        JButton addButton = new JButton("➕ Add Student");
        JButton reportButton = new JButton("📊 Generate Report");
        addButton.setBackground(new Color(60, 179, 113));
        addButton.setForeground(Color.WHITE);
        reportButton.setBackground(new Color(70, 130, 180));
        reportButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(reportButton);

       
        String[] columns = {"Name", "Score"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane tableScroll = new JScrollPane(table);

       
        JPanel resultPanel = new JPanel(new GridLayout(3, 1));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        resultPanel.setBackground(new Color(240, 248, 255));

        avgLabel = new JLabel("Average Score: -");
        highLabel = new JLabel("Highest Score: -");
        lowLabel = new JLabel("Lowest Score: -");

        avgLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        highLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        lowLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        resultPanel.add(avgLabel);
        resultPanel.add(highLabel);
        resultPanel.add(lowLabel);

        
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.EAST);
        add(resultPanel, BorderLayout.SOUTH);

        
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String scoreText = scoreField.getText().trim();

            if (name.isEmpty() || scoreText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both name and score.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int score = Integer.parseInt(scoreText);
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(null, "Score must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                students.add(new Student(name, score));
                tableModel.addRow(new Object[]{name, score});

                nameField.setText("");
                scoreField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid score. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        reportButton.addActionListener(e -> {
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No students added yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int total = 0, highest = Integer.MIN_VALUE, lowest = Integer.MAX_VALUE;
            String topStudent = "", lowStudent = "";

            for (Student s : students) {
                int score = s.getScore();
                total += score;

                if (score > highest) {
                    highest = score;
                    topStudent = s.getName();
                }
                if (score < lowest) {
                    lowest = score;
                    lowStudent = s.getName();
                }
            }

            double average = (double) total / students.size();

            avgLabel.setText("Average Score: " + String.format("%.2f", average));
            highLabel.setText("Highest Score: " + highest + " (by " + topStudent + ")");
            lowLabel.setText("Lowest Score: " + lowest + " (by " + lowStudent + ")");
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeManagerPro());
    }
}
