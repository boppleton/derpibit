package gui;

import utils.LimitChase;
import utils.LimitChaseContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

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
        add(new JLabel("spacerrr"), gbc);




        activeChasePanel = new JPanel();
        activeChasePanel.setLayout(new BoxLayout(activeChasePanel, BoxLayout.Y_AXIS));
        activeChasePanel.setBorder(BorderFactory.createTitledBorder("active chases"));

        activeChasePanel.add(new JLabel("chase ojfepofeaf"));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(activeChasePanel, gbc);

        activeChaseLoop();


    }

    private JPanel activeChasePanel;

    private void activeChaseLoop() {

        Thread chasethread = new Thread(()->{

            for (;;) {

//                System.out.println("chasethread..");

                activeChasePanel.removeAll();

                try {

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // anything inside of this will run on the Event Dispatch thread, for the java swing UI
                    SwingUtilities.invokeLater(() -> {

//                    System.out.println("chase stuff..");

                        ArrayList<LimitChase> chases = LimitChaseContainer.getSingleChaseList();

                        if (chases.size() > 0) {

//                            System.out.println("removeAll chases..");

//                            activeChasePanel.removeAll();

                            for (int i = 0; i < chases.size(); i++) {


                                LimitChase chase = chases.get(i);

//                                System.out.println("adding chase " + chase.getSize() + chase.isActive());

                                JPanel singleChasePanel = new JPanel();
                                singleChasePanel.setLayout(new BoxLayout(singleChasePanel, BoxLayout.X_AXIS));


                                JLabel startLabel = new JLabel((chase.isBuy() ? "buy " : "sell ") + chase.getSize());
                                startLabel.setFont(new Font(Font.SANS_SERIF, 0, 18));
                                startLabel.setForeground(chase.isBuy() ? Color.green : Color.red);
                                singleChasePanel.add(startLabel);

                                singleChasePanel.add(Box.createHorizontalStrut(9));

                                JButton stopButton = new JButton("stop");
                                singleChasePanel.add(stopButton);

                                stopButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        System.out.println("stopping chase");

                                        try {
                                            chase.closeOut();
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                        chase.setActive(false);
                                        LimitChaseContainer.removeChaseSingle(chase);
                                    }
                                });


//                                System.out.println("adding chase");

                                activeChasePanel.add(singleChasePanel);

                            }

                        }

                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        chasethread.start();
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
