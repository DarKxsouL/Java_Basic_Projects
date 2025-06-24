import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import org.json.JSONObject;

public class CurrencyConverterGUI extends JFrame {

    private static final String API_KEY = "77396cf1522014005651d753"; // Replace with your key
    private static final String[] CURRENCIES = { "USD", "INR", "EUR", "GBP", "JPY", "CAD", "AUD", "CNY" };

    private JComboBox<String> fromCurrencyBox;
    private JComboBox<String> toCurrencyBox;
    private JTextField amountField;
    private JButton convertButton;
    private JLabel resultLabel;

    public CurrencyConverterGUI() {
        setTitle("Currency Converter");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // From currency
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("From:"), gbc);

        fromCurrencyBox = new JComboBox<>(CURRENCIES);
        gbc.gridx = 1;
        add(fromCurrencyBox, gbc);

        // To currency
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("To:"), gbc);

        toCurrencyBox = new JComboBox<>(CURRENCIES);
        gbc.gridx = 1;
        add(toCurrencyBox, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Amount:"), gbc);

        amountField = new JTextField(10);
        gbc.gridx = 1;
        add(amountField, gbc);

        // Convert button
        convertButton = new JButton("Convert");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(convertButton, gbc);

        // Result label
        resultLabel = new JLabel(" ");
        gbc.gridy = 4;
        add(resultLabel, gbc);

        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });
    }

    private void convertCurrency() {
        String fromCurrency = fromCurrencyBox.getSelectedItem().toString();
        String toCurrency = toCurrencyBox.getSelectedItem().toString();
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);
            double rate = getExchangeRate(fromCurrency, toCurrency);
            double result = amount * rate;
            resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, fromCurrency, result, toCurrency));
        } catch (NumberFormatException ex) {
            resultLabel.setText("Please enter a valid amount.");
        } catch (Exception ex) {
            resultLabel.setText("Error fetching exchange rate.");
        }
    }

    private double getExchangeRate(String from, String to) throws IOException {
        String urlStr = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + from;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            json.append(line);
        }
        in.close();
        conn.disconnect();

        JSONObject obj = new JSONObject(json.toString());
        JSONObject rates = obj.getJSONObject("conversion_rates");

        return rates.getDouble(to);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CurrencyConverterGUI().setVisible(true);
        });
    }
}
