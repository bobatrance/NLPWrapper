package com.bobatrance.NLPWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import com.bobatrance.NLPWrapper.EnumModel.SegmentModel;
import com.bobatrance.NLPWrapper.EnumModel.TaggerModel;

public class GuiApp {

    static HashSet<File> fileList = new HashSet<File>();
    static SegmentModel chosenSegmentModel = SegmentModel.PKU;
    static TaggerModel chosenTaggerModel = TaggerModel.DIST;

    private static void createMainWindow() {

        JFrame frame = new JFrame("Segmenter and POS TagSegmentger");

        try {
            // theme
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            System.out.println("Error setting window theme: " + e);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // component setup
        // top panel
        JPanel panelSelection = new JPanel();
        panelSelection.setLayout(new BorderLayout());
        JPanel panelSelectionL = new JPanel();
        panelSelectionL.setLayout(new BoxLayout(panelSelectionL, BoxLayout.PAGE_AXIS));
        JPanel panelSelectionR = new JPanel();
        panelSelectionR.setLayout(new BoxLayout(panelSelectionR, BoxLayout.PAGE_AXIS));
        
        JPanel panelBrowse = new JPanel();
        JLabel labelBrowse = new JLabel("Choose file(s) to process: ");
        panelBrowse.add(labelBrowse);
        JButton buttonBrowse = new JButton("Browse");
        labelBrowse.setLabelFor(buttonBrowse);
        panelBrowse.add(buttonBrowse);
        panelSelectionL.add(panelBrowse);

        JPanel panelClear = new JPanel();
        JLabel labelClear = new JLabel("Clear the file list: ");
        panelClear.add(labelClear);
        JButton buttonClear = new JButton("Clear");
        labelClear.setLabelFor(buttonClear);
        panelClear.add(buttonClear);
        panelSelectionL.add(panelClear);

        panelSelection.add(panelSelectionL, BorderLayout.WEST);

        JPanel panelSegmentModel = new JPanel();
        JLabel labelSegmentModel = new JLabel("Choose segmenter model: ");
        panelSegmentModel.add(labelSegmentModel);
        JRadioButton radioSegmentModelCTB = new JRadioButton("CTB");
        JRadioButton radioSegmentModelPKU = new JRadioButton("PKU", true);
        //Group the radio buttons.
        ButtonGroup radioSegmentGroup = new ButtonGroup();
        radioSegmentGroup.add(radioSegmentModelCTB);
        radioSegmentGroup.add(radioSegmentModelPKU);
        panelSegmentModel.add(radioSegmentModelCTB);
        panelSegmentModel.add(radioSegmentModelPKU);
        panelSelectionR.add(panelSegmentModel);

        JPanel panelTaggerModel = new JPanel();
        JLabel labelTaggerModel = new JLabel("Choose tagger model: ");
        panelTaggerModel.add(labelTaggerModel);
        JRadioButton radioTaggerModelDist = new JRadioButton("Dist", true);
        JRadioButton radioTaggerModelNoDist = new JRadioButton("NoDist");
        //Group the radio buttons.
        ButtonGroup radioTaggerGroup = new ButtonGroup();
        radioTaggerGroup.add(radioTaggerModelDist);
        radioTaggerGroup.add(radioTaggerModelNoDist);
        panelTaggerModel.add(radioTaggerModelDist);
        panelTaggerModel.add(radioTaggerModelNoDist);
        panelSelectionR.add(panelTaggerModel);
        
        JPanel panelProcess = new JPanel();
        JLabel labelProcess = new JLabel("Begin file processing: ");
        panelProcess.add(labelProcess);
        JButton buttonProcess = new JButton("Process");
        buttonProcess.setEnabled(false);
        labelProcess.setLabelFor(buttonProcess);
        panelProcess.add(buttonProcess);
        panelSelectionR.add(panelProcess);

        panelSelection.add(panelSelectionR, BorderLayout.EAST);

        // bottom panel
        JTextArea listArea = new JTextArea();
        JScrollPane panelShowListArea = new JScrollPane(listArea);
        listArea.setEditable(false);

        frame.add(panelSelection, BorderLayout.NORTH);
        frame.add(panelShowListArea, BorderLayout.CENTER);
        frame.setMinimumSize(new Dimension(900, 400));

        // component logic
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);

        buttonBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(buttonBrowse);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] chosenFiles = fc.getSelectedFiles();
                    try {
                        for(File file : chosenFiles) {
                            if(fileList.add(file)) {
                                listArea.append(file.getCanonicalPath() + "\n");
                            }
                        }
                        buttonProcess.setEnabled(true);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileList.clear();
                buttonProcess.setEnabled(false);
                listArea.setText(null);
            }
        });

        radioSegmentModelCTB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chosenSegmentModel = SegmentModel.CTB;
            }
        });

        radioSegmentModelPKU.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chosenSegmentModel = SegmentModel.PKU;
            }
        });

        radioTaggerModelDist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chosenTaggerModel = TaggerModel.DIST;
            }
        });

        radioTaggerModelNoDist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chosenTaggerModel = TaggerModel.NODIST;
            }
        });

        buttonProcess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NLPHelper.runProcessing(fileList, chosenSegmentModel, chosenTaggerModel);
            }
        });

        // finalize display
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                createMainWindow();
            }
        });
    }

}