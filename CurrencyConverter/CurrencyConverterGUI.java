import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.json.JSONObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CurrencyConverterGUI {

    private JFrame frame;
    private JComboBox<String> baseBox, targetBox;
    private JTextField amountField;
    private JButton convertBtn, swapBtn, loadBtn, clearHistoryBtn;
    private JLabel resultLabel, statusLabel;
    private DefaultListModel<String> historyModel;
    private JList<String> historyList;
    private JProgressBar progressBar;

    public CurrencyConverterGUI() {
        initComponents();
        fetchCurrenciesOnStart(); // try to load all currency codes
    }

    private void initComponents() {
        frame = new JFrame("Currency Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 420);
        frame.setLayout(new BorderLayout(10, 10));

        // Top panel with inputs
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; top.add(new JLabel("Base:"), gbc);
        gbc.gridx = 1;
        baseBox = new JComboBox<>(new String[] {"USD","EUR","INR","GBP","JPY","AUD","CAD","CNY","SGD","CHF"});
        top.add(baseBox, gbc);

        gbc.gridx = 2;
        swapBtn = new JButton("⇄");
        top.add(swapBtn, gbc);

        gbc.gridx = 3; top.add(new JLabel("Target:"), gbc);
        gbc.gridx = 4;
        targetBox = new JComboBox<>(new String[] {"INR","USD","EUR","GBP","JPY","AUD","CAD","CNY","SGD","CHF"});
        top.add(targetBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; top.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 4;
        amountField = new JTextField();
        top.add(amountField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        convertBtn = new JButton("Convert");
        top.add(convertBtn, gbc);

        gbc.gridx = 1;
        loadBtn = new JButton("Load All Currencies");
        top.add(loadBtn, gbc);

        gbc.gridx = 2;
        clearHistoryBtn = new JButton("Clear History");
        top.add(clearHistoryBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 5;
        resultLabel = new JLabel("Result: —");
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 14f));
        top.add(resultLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 5;
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        top.add(progressBar, gbc);
        gbc.gridwidth = 1;

        frame.add(top, BorderLayout.NORTH);

        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        JScrollPane sp = new JScrollPane(historyList);
        sp.setBorder(BorderFactory.createTitledBorder("Conversion History"));
        frame.add(sp, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready");
        frame.add(statusLabel, BorderLayout.SOUTH);

        convertBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 doConvert(); 
                }
        });

        swapBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                swapCurrencies(); 
            }
        });

        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                fetchCurrenciesOnStart(); 
            }
        });

        clearHistoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                historyModel.clear(); resultLabel.setText("Result: —"); 
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void swapCurrencies() {
        Object a = baseBox.getSelectedItem();
        baseBox.setSelectedItem(targetBox.getSelectedItem());
        targetBox.setSelectedItem(a);
    }

    private void doConvert() {
        final String base = ((String) baseBox.getSelectedItem()).trim();
        final String target = ((String) targetBox.getSelectedItem()).trim();
        final String amtStr = amountField.getText().trim();

        if (base.isEmpty() || target.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select both base and target currencies.", "Input error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number for amount.", "Input error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setBusy(true, "Converting...");

        new SwingWorker<Double, Void>() {
            protected Double doInBackground() throws Exception {
                return getExchangeRateAndConvert(base, target, amount);
            }
            protected void done() {
                setBusy(false, "Ready");
                try {
                    Double converted = get();
                    if (converted == null || converted < 0) {
                        JOptionPane.showMessageDialog(frame, "Conversion failed. Please check currency codes or your internet connection.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String res = String.format("%.2f %s = %.2f %s", amount, base, converted, target);
                        resultLabel.setText("Result: " + res);
                        historyModel.addElement(res);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy, String message) {
        convertBtn.setEnabled(!busy);
        loadBtn.setEnabled(!busy);
        swapBtn.setEnabled(!busy);
        progressBar.setIndeterminate(busy);
        progressBar.setVisible(busy);
        statusLabel.setText(message);
    }

    private double getExchangeRateAndConvert(String base, String target, double amount) {
        try {
            String urlStr = "https://open.er-api.com/v6/latest/" + URLEncoder.encode(base, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(7000);
            conn.setReadTimeout(7000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(sb.toString());
            if (!"success".equalsIgnoreCase(json.optString("result", ""))) {
                return -1;
            }
            JSONObject rates = json.getJSONObject("rates");
            if (!rates.has(target)) return -1;
            double rate = rates.getDouble(target);
            return amount * rate;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void fetchCurrenciesOnStart() {
        setBusy(true, "Loading currencies...");
        new SwingWorker<List<String>, Void>() {
            protected List<String> doInBackground() throws Exception {
                return fetchCurrencyCodes();
            }
            protected void done() {
                setBusy(false, "Ready");
                try {
                    List<String> list = get();
                    if (list != null && !list.isEmpty()) {
                        Collections.sort(list);
                        baseBox.removeAllItems();
                        targetBox.removeAllItems();
                        for (String s : list) {
                            baseBox.addItem(s);
                            targetBox.addItem(s);
                        }
                        baseBox.setSelectedItem("USD");
                        targetBox.setSelectedItem("INR");
                    } else {
                        statusLabel.setText("Could not load full list, using default set.");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Could not load currency list.");
                }
            }
        }.execute();
    }

    private List<String> fetchCurrencyCodes() {
        try {
            String urlStr = "https://open.er-api.com/v6/latest/USD";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(7000);
            conn.setReadTimeout(7000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(sb.toString());
            if (!"success".equalsIgnoreCase(json.optString("result", ""))) 
            return Collections.emptyList();
            JSONObject rates = json.getJSONObject("rates");
            Iterator<String> iter = rates.keys();
            List<String> list = new ArrayList<>();
            while (iter.hasNext()) list.add(iter.next());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { 
            public void run() { new CurrencyConverterGUI(); } });
    }
}
