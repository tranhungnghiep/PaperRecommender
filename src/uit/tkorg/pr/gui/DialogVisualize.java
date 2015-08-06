/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Vinh
 */
public class DialogVisualize extends javax.swing.JFrame {

    /**
     * Creates new form Visualized
     */
    public DialogVisualize() {
        initComponents();

    }

    private XYDataset createDataset(String measure) throws FileNotFoundException, IOException {
        final XYSeries series1 = new XYSeries("Content - based");
        final XYSeries series2 = new XYSeries("CF using KNN Pearson");
        final XYSeries series3 = new XYSeries("CF using KNN Cosine");
        final XYSeries series4 = new XYSeries("CF using SVD");
        final XYSeries series5 = new XYSeries("Hybrid");

        String path = path_TextField.getText().trim();

        FileReader file = new FileReader(new File(path));
        BufferedReader textReader = new BufferedReader(file);
        String line = null;
        String[] tokens;
        while ((line = textReader.readLine()) != null) {
            tokens = line.split(",");
            if (tokens.length == 4) {
                if (tokens[1].equals(measure)) {
                    if (tokens[0].equals("Content - based")) {
                        series1.add(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
                    } else if (tokens[0].equals("CF using KNN Pearson")) {
                        series2.add(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
                    } else if (tokens[0].equals("CF using KNN Cosine")) {
                        series3.add(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
                    } else if (tokens[0].equals("CF using SVD")) {
                        series4.add(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
                    } else if (tokens[0].equals("Hybrid")) {
                        series5.add(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
                    }
                }
            }
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        dataset.addSeries(series4);
        dataset.addSeries(series5);

        return dataset;

    }

    private JFreeChart createChart(final XYDataset dataset, String measure) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                measure, // chart title
                "TopRank", // x axis label
                "Accuracy", // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //   plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShapesVisible(2, true);
        renderer.setSeriesShapesVisible(3, true);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        path_TextField = new javax.swing.JTextField();
        browse_Button = new javax.swing.JButton();
        chart_Panel = new javax.swing.JPanel();
        measureEvaluation_ComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        close_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Visualize");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Choose File:");

        browse_Button.setText("Browse...");
        browse_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browse_ButtonActionPerformed(evt);
            }
        });

        chart_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        chart_Panel.setName(""); // NOI18N

        javax.swing.GroupLayout chart_PanelLayout = new javax.swing.GroupLayout(chart_Panel);
        chart_Panel.setLayout(chart_PanelLayout);
        chart_PanelLayout.setHorizontalGroup(
            chart_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        chart_PanelLayout.setVerticalGroup(
            chart_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        measureEvaluation_ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Precision", "Recall", "F1", "MAP", "NDCG", "MRR" }));
        measureEvaluation_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measureEvaluation_ComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("Choose Evaluation Measure:");

        close_Button.setText("Close");
        close_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path_TextField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browse_Button))
            .addGroup(layout.createSequentialGroup()
                .addComponent(chart_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(measureEvaluation_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(close_Button))
                    .addComponent(jLabel2)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(path_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browse_Button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(measureEvaluation_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 349, Short.MAX_VALUE)
                        .addComponent(close_Button))
                    .addComponent(chart_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void browse_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browse_ButtonActionPerformed
        String path = GuiUtilities.chooseFileJChooserTXT("Choose File");
        if (path != null) {
            try {
                path_TextField.setText(path);
                if (measureEvaluation_ComboBox.getSelectedIndex() == 0) {
                    try {
                        drawChart("Precision");
                    } catch (IOException ex) {
                        Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (measureEvaluation_ComboBox.getSelectedIndex() == 1) {
                    try {
                        drawChart("Recall");
                    } catch (IOException ex) {
                        Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (measureEvaluation_ComboBox.getSelectedIndex() == 2) {
                    try {
                        drawChart("F1");
                    } catch (IOException ex) {
                        Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (measureEvaluation_ComboBox.getSelectedIndex() == 3) {
                    try {
                        drawChart("MAP");
                    } catch (IOException ex) {
                        Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (measureEvaluation_ComboBox.getSelectedIndex() == 4) {
                    try {
                        drawChart("NDCG");
                    } catch (IOException ex) {
                        Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (measureEvaluation_ComboBox.getSelectedIndex() == 5) {
                    try {
                        drawChart("MRR");
                    } catch (IOException ex) {
                        Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPane, "Can't draw chart from this file!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

    }//GEN-LAST:event_browse_ButtonActionPerformed
    public void drawChart(String measure) throws IOException {
        chart_Panel.removeAll();
        final XYDataset dataset = createDataset(measure);
        final JFreeChart chart = createChart(dataset, measure);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(chart_Panel.getWidth(), chart_Panel.getHeight()));
        chart_Panel.setLayout(new java.awt.BorderLayout());
        //chart_Panel.add(chartPanel,BorderLayout.CENTER);
        chart_Panel.add(chartPanel, BorderLayout.CENTER);
        chart_Panel.validate();
    }
    private void close_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_ButtonActionPerformed
        this.hide();
    }//GEN-LAST:event_close_ButtonActionPerformed

    private void measureEvaluation_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measureEvaluation_ComboBoxActionPerformed
        if (!path_TextField.getText().isEmpty()) {
            if (measureEvaluation_ComboBox.getSelectedIndex() == 0) {
                try {
                    drawChart("Precision");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 1) {
                try {
                    drawChart("Recall");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 2) {
                try {
                    drawChart("F1");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 3) {
                try {
                    drawChart("MAP");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 4) {
                try {
                    drawChart("NDCG");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 5) {
                try {
                    drawChart("MRR");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Please choose file to draw chart...", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_measureEvaluation_ComboBoxActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        File file = new File("Temp\\EvaluationResult.txt");
        if (file.exists()) {
            path_TextField.setText(file.getAbsolutePath());
            if (measureEvaluation_ComboBox.getSelectedIndex() == 0) {
                try {
                    drawChart("Precision");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 1) {
                try {
                    drawChart("Recall");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 2) {
                try {
                    drawChart("F1");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 3) {
                try {
                    drawChart("MAP");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 4) {
                try {
                    drawChart("NDCG");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (measureEvaluation_ComboBox.getSelectedIndex() == 5) {
                try {
                    drawChart("MRR");
                } catch (IOException ex) {
                    Logger.getLogger(DialogVisualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.out.println("Unable to load Windows look and feel");
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogVisualize dialog = null;

                dialog = new DialogVisualize();

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browse_Button;
    private javax.swing.JPanel chart_Panel;
    private javax.swing.JButton close_Button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox measureEvaluation_ComboBox;
    private javax.swing.JTextField path_TextField;
    // End of variables declaration//GEN-END:variables
}
