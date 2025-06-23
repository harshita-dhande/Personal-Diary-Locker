package ui;

import diary.DiaryEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class DiaryUI 
{
    private static final String FILE_NAME = "diary.txt";
    private static final String PASSWORD = "1234";
    private static int loginAttempts = 0;

    private static Map<String, String> entries = new LinkedHashMap<>();
    private static JTextArea textArea;
    private static DefaultListModel<String> listModel;
    private static JList<String> entryList;
    private static String originalTextBeforeEdit = "";

    public static void main(String[] args) 
    {
        showLoginUI();
    }

    private static void showLoginUI() 
    {
        JFrame loginFrame = new JFrame("Diary Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel());
        panel.add(loginButton);

        loginFrame.add(panel);
        loginFrame.setVisible(true);

        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if ("user".equals(user) && PASSWORD.equals(pass)) 
	    {
                loginFrame.dispose();
                buildDiaryUI();
            } 
	    else 
	    {
                loginAttempts++;
                if (loginAttempts >= 3) 
		{
                    JOptionPane.showMessageDialog(loginFrame, "3 Failed Attempts. Exiting...");
                    System.exit(0);
                } 
		else 
		{
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials! Attempts left: " + (3 - loginAttempts));
                }
            }
        });
    }

    private static void buildDiaryUI() {
        JFrame frame = new JFrame("Personal Diary Locker");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(entryList);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, scrollPane);
        splitPane.setDividerLocation(180);

        JButton newEntryButton = new JButton("New Entry");
        JButton saveButton = new JButton("Save New");
        JButton updateButton = new JButton("Update");
        JButton cancelEditButton = new JButton("Cancel Edit");
        JButton deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(newEntryButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelEditButton);
        buttonPanel.add(deleteButton);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        loadEntries();

        entryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) 
	    {
                String selectedTitle = entryList.getSelectedValue();
                if (selectedTitle != null) 
		{
                    textArea.setText(entries.get(selectedTitle));
                    originalTextBeforeEdit = entries.get(selectedTitle);
                }
            }
        });

        newEntryButton.addActionListener(e -> {
            entryList.clearSelection();
            textArea.setText("");
            originalTextBeforeEdit = "";
        });

        saveButton.addActionListener(e -> {
             String title = new java.text.SimpleDateFormat("EEE dd MMMM yyyy hh:mm:ss a").format(new Date());
             String content = textArea.getText();
             if (!content.trim().isEmpty()) 
             {
                entries.put(title, content);
                listModel.addElement(title);
                saveEntriesToFile();
             } 
             else 
             {
                JOptionPane.showMessageDialog(null, "Entry is empty. Please write something.");
             }
         });

        updateButton.addActionListener(e -> {
            String selectedTitle = entryList.getSelectedValue();
            if (selectedTitle != null) 
	    {
                entries.put(selectedTitle, textArea.getText().trim());
                saveEntriesToFile();
                JOptionPane.showMessageDialog(frame, "Entry updated successfully!");
            }
        });

        cancelEditButton.addActionListener(e -> {
            textArea.setText(originalTextBeforeEdit);
        });

        deleteButton.addActionListener(e -> {
            String selectedTitle = entryList.getSelectedValue();
            if (selectedTitle != null) 
	    {
                int confirm = JOptionPane.showConfirmDialog(frame, "Delete this entry?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) 
		{
                    entries.remove(selectedTitle);
                    listModel.removeElement(selectedTitle);
                    textArea.setText("");
                    saveEntriesToFile();
                }
            }
        });

        frame.setVisible(true);
    }

    private static void loadEntries() 
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) 
	    return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
	{
            String line, title = null,Builder="";
            while ((line = br.readLine()) != null) 
	    {
                if (line.startsWith("### ")) 
		{
                    if (title != null && !Builder.isEmpty()) 
		    {
                        entries.put(title, Builder.strip());
                        listModel.addElement(title);
                        Builder = "";
                    }
                    title = line.substring(4).trim();
                } 
		else 
		{
                    Builder += line + "\n";
                }
            }
            if (title != null && !Builder.isEmpty()) 
	    {
                entries.put(title, Builder.strip());
                listModel.addElement(title);
            }
        } 
	catch (IOException e) 
	{
            e.printStackTrace();
        }
    }

    private static void saveEntriesToFile() 
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) 
	{
            for (Map.Entry<String, String> entry : entries.entrySet()) 
	    {
                bw.write("### " + entry.getKey());
                bw.newLine();
                bw.write(entry.getValue());
                bw.newLine();
            }
        } 
	catch (IOException e) 
	{
            e.printStackTrace();
        }
    }
}