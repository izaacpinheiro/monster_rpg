package app;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private JFrame window;
    private JTextField nameField;
    private JTextField lifeField;
    private JButton createButton;
    private JButton applyButton;
    private JLabel statusLabel;
    private JButton removeButton;
    private JButton cleanListButton;
    private JButton cloneButton;
    private JTextField amountField;
    private DefaultListModel<Monster> monsterListModel;
    private JList<Monster> monsterListView;

    public GUI() {
        configWindow();
        initComponents();
        setupLayout();
        addListeners();
    }

    private void configWindow() {
        window = new JFrame();
        window.setTitle("Monster Tracker RPG");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(420,420);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
    }

    private void initComponents() {
        nameField = new JTextField(10);
        lifeField = new JTextField(5);
        amountField = new JTextField(5);

        createButton = new JButton("Create monster");
        applyButton = new JButton("Apply");
        removeButton = new JButton("Remove");
        cleanListButton = new JButton("\uD83D\uDDD1\uFE0F");
        cloneButton = new JButton("Clone");

        statusLabel = new JLabel(" ");
        statusLabel.setPreferredSize(new Dimension(380, 30));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createLineBorder(Color.gray));

        applyButton.setEnabled(false);
        removeButton.setEnabled(false);
        cloneButton.setEnabled(false);

        monsterListModel = new DefaultListModel<>();
        monsterListView = new JList<>(monsterListModel);
        monsterListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        monsterListView.setVisibleRowCount(8);
        // change ScrollPanel list information
        monsterListView.setCellRenderer(new ListCellRenderer<Monster>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Monster> list, Monster value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel(value.getName() + " - Life: " + value.getLife());
                if (isSelected) {
                    label.setOpaque(true);
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                }
                return label;
            }
        });
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // row create monster
        JPanel createPanel = new JPanel();
        createPanel.add(new JLabel("Name:"));
        createPanel.add(nameField);
        createPanel.add(new JLabel("Life:"));
        createPanel.add(lifeField);
        createPanel.add(createButton);

        // control panel -> to apply damage and heal
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(amountField);
        controlPanel.add(applyButton);
        controlPanel.add(removeButton);
        controlPanel.add(cloneButton);
        controlPanel.add(cleanListButton);

        // monster list
        JScrollPane listScrollPane = new JScrollPane(monsterListView);

        // status label
        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);

        // add everything to the main panel
        mainPanel.add(createPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(listScrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(controlPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusPanel);

        // add main panel to window
        window.setContentPane(mainPanel);
        window.setVisible(true);
    }

    private void addListeners() {
        createButton.addActionListener(e -> {
            String nameText = nameField.getText().trim();
            String lifeText = lifeField.getText().trim();

            if (nameText.isEmpty() || lifeText.isBlank()) {
                JOptionPane.showMessageDialog(window, "Fill in the name and the life!");
                return;
            }

            try {
                int life = Integer.parseInt(lifeText);
                if (life <= 0) throw new NumberFormatException();

                Monster newMonster = new Monster(nameText, life);
                monsterListModel.addElement(newMonster);

                // clean the fields
                nameField.setText("");
                lifeField.setText("");
                statusLabel.setText("Monster created: " + newMonster.getName());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(window, "Life should be a integer number!");
            }
        });

        monsterListView.addListSelectionListener(e -> {
            boolean selected = monsterListView.getSelectedIndex() != -1;
            applyButton.setEnabled(selected);
            removeButton.setEnabled(selected);
            cloneButton.setEnabled(selected);
        });

        applyButton.addActionListener(e -> {
            Monster selected = monsterListView.getSelectedValue();
            if (selected == null) return;

            try {
                int amount = Integer.parseInt(amountField.getText().trim());
                selected.changeLife(amount);
                monsterListView.repaint();
                amountField.setText("");
                monsterListView.clearSelection();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(window, "Enter an integer (positive or negative)");
            }
        });

        removeButton.addActionListener(e -> {
            int index = monsterListView.getSelectedIndex();
            if (index != -1) {
                Monster removed = monsterListModel.remove(index);
                statusLabel.setText("Monster removed: " + removed.getName());
            }
        });

        cleanListButton.addActionListener(e -> {
            if (monsterListModel.isEmpty()){
                statusLabel.setText("There are no monsters created.");
            } else {
                int confirm = JOptionPane.showConfirmDialog(window,
                        "Do you want to remove all monsters?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    monsterListModel.clear();
                    monsterListView.clearSelection();
                    statusLabel.setText("All monsters have been removed.");
                }
            }
        });

        cloneButton.addActionListener(e -> {
            Monster selected = monsterListView.getSelectedValue();
            if (selected == null) return; // nothing selected

            Monster copiedMonster = selected.copy(); // clone the selected monster
            String newName = JOptionPane.showInputDialog(window,
                    "Enter new name for the cloned monster:",
                    "Clone monster",
                    JOptionPane.PLAIN_MESSAGE);
            if (newName != null && !newName.trim().isEmpty()) {
                copiedMonster.setName(newName); // set new name to the copied monster
                monsterListModel.addElement(copiedMonster);
                statusLabel.setText(selected.getName() + " cloned to " + newName);
            } else {
                statusLabel.setText("Clone canceled or invalid name.");
            }
        });
    }
}