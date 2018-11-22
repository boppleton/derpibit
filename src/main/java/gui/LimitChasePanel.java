package gui;

import utils.LimitChase;
import utils.LimitChaseContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LimitChasePanel extends JPanel {

    private GridBagConstraints gbc = new GridBagConstraints();

    private JPanel leftSidePanel;
    private JPanel rightSidePanel;

    public LimitChasePanel() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("limit chase"));


        leftSidePanel();
        rightSidePanel();

        //left side
        singleOrChasePanel();
        sizePanel();

        //right side
        rightSidePanel();
        startButtonPanel();
        sidePanel();
//        previewPanel();

        //spacers
        gbc.gridy++;
        gbc.weighty = 1;
        add(new JLabel("spacer"), gbc);




    }

    private void startButtonPanel() {

        JPanel startbuttonPanel = new JPanel(new GridBagLayout());


        JButton startButton = new JButton("start");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LimitChaseContainer.addChaseSingle(new LimitChase((double)sizeSpinner.getValue(), buyRadio.isSelected()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        gbc.fill = GridBagConstraints.NONE;

        gbc.anchor = GridBagConstraints.NORTHWEST;
        startbuttonPanel.add(startButton, gbc);


        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        rightSidePanel.add(startbuttonPanel, gbc);
    }

    private void previewPanel() {

        JTextArea previewTextArea = new JTextArea();

        previewTextArea.setText("order previjjjjjkkkkkkkkkkkkkkkkkkkkkkkkjjew1\n order pjjjjjjjreview1\n order preview1\n order preview1\n order jjjjjjjjjpreview1\n order preview1\n order preview1\n order preview1\n order preview1\n ");

        JScrollPane previewScrollPane = new JScrollPane(previewTextArea);
        previewScrollPane.setPreferredSize(new Dimension(100,200));

        gbc.gridy = 1;
        rightSidePanel.add(previewScrollPane, gbc);
    }

    private void rightSidePanel() {

        rightSidePanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(rightSidePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;


    }

    private void leftSidePanel() {

        leftSidePanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(leftSidePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;


    }

    private JSpinner sizeSpinner;
    private void sizePanel() {

        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setPreferredSize(new Dimension(130,65));
        sizePanel.setBorder(BorderFactory.createTitledBorder("size"));

        sizeSpinner = new JSpinner(new SpinnerNumberModel(1000,.001,10000000,1000));

        sizePanel.add(sizeSpinner, BorderLayout.CENTER);

        gbc.gridy = 2;
        leftSidePanel.add(sizePanel, gbc);
    }

    private JRadioButton buyRadio;
    private JPanel sidePanel;
    private void sidePanel() {

        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.X_AXIS));
        sidePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "side"));

        buyRadio = new JRadioButton("buy");
        buyRadio.setSelected(true);
        sidePanel.add(buyRadio);

        sidePanel.add(Box.createHorizontalStrut(9));

        JRadioButton sellRadio = new JRadioButton("sell");
        sidePanel.add(sellRadio);

        ButtonGroup sideButtonGroup = new ButtonGroup();
        sideButtonGroup.add(buyRadio);
        sideButtonGroup.add(sellRadio);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        rightSidePanel.add(sidePanel, gbc);

        buyRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buyRadio.isSelected()) {
                    sidePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "side"));
                }
            }
        });

        sellRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sellRadio.isSelected()) {
                    sidePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.red), "side"));

                }
            }
        });


    }

    private void singleOrChasePanel() {

        JPanel singleorchasePanel = new JPanel();
        singleorchasePanel.setLayout(new BoxLayout(singleorchasePanel, BoxLayout.Y_AXIS));
        singleorchasePanel.setBorder(BorderFactory.createTitledBorder("type"));

        JRadioButton singleOrderRadio = new JRadioButton("single");
        singleOrderRadio.setSelected(true);
        singleorchasePanel.add(singleOrderRadio);

        JRadioButton scaleOrderRadio = new JRadioButton("scaled");
        scaleOrderRadio.setEnabled(false);
        singleorchasePanel.add(scaleOrderRadio);

        JCheckBox stopOrderCheckbox = new JCheckBox("stop");
        stopOrderCheckbox.setEnabled(false);
        singleorchasePanel.add(stopOrderCheckbox);

        ButtonGroup singlescaledButtonGroup = new ButtonGroup();
        singlescaledButtonGroup.add(singleOrderRadio);
        singlescaledButtonGroup.add(scaleOrderRadio);

        gbc.gridy = 0;
        leftSidePanel.add(singleorchasePanel, gbc);
    }
}
