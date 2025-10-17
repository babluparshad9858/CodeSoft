import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StudentGradeManager {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Professional Grade Calculator");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel subjectsLabel = new JLabel("Number of subjects:");
        JTextField subjectsField = new JTextField(5);
        JButton generateFieldsBtn = new JButton("Generate Fields");
        topPanel.add(subjectsLabel);
        topPanel.add(subjectsField);
        topPanel.add(generateFieldsBtn);

        JPanel marksPanel = new JPanel();
        marksPanel.setLayout(new BoxLayout(marksPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(marksPanel);
        scrollPane.setPreferredSize(new Dimension(480, 250));

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton calculateBtn = new JButton("Calculate Grade");
        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        bottomPanel.add(calculateBtn);
        bottomPanel.add(new JScrollPane(resultArea));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        generateFieldsBtn.addActionListener(e -> {
            marksPanel.removeAll();
            int subjects;
            try {
                subjects = Integer.parseInt(subjectsField.getText());
                if (subjects <= 0) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid number of subjects!");
                return;
            }

            for (int i = 1; i <= subjects; i++) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel label = new JLabel("Marks obtained in subject " + i + " (out of 100): ");
                JTextField markField = new JTextField(5);
                panel.add(label);
                panel.add(markField);
                marksPanel.add(panel);
            }
            marksPanel.revalidate();
            marksPanel.repaint();
        });

        calculateBtn.addActionListener(e -> {
            Component[] components = marksPanel.getComponents();
            int totalMarks = 0;
            int subjects = components.length;
            boolean valid = true;

            for (int i = 0; i < subjects; i++) {
                JPanel panel = (JPanel) components[i];
                JTextField markField = (JTextField) panel.getComponent(1);
                int mark;
                try {
                    mark = Integer.parseInt(markField.getText());
                    if (mark < 0 || mark > 100) throw new Exception();
                    totalMarks += mark;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid marks (0-100) for subject " + (i + 1));
                    valid = false;
                    break;
                }
            }

            if (!valid) return;

            double average = (double) totalMarks / subjects;
            String grade;
            if (average >= 90) grade = "A+";
            else if (average >= 80) grade = "A";
            else if (average >= 70) grade = "B";
            else if (average >= 60) grade = "C";
            else if (average >= 50) grade = "D";
            else grade = "F";

            resultArea.setText("Total Marks: " + totalMarks + " / " + (subjects * 100) +
                    "\nAverage Percentage: " + String.format("%.2f", average) + "%" +
                    "\nGrade: " + grade);
        });

        frame.setVisible(true);
    }
}
