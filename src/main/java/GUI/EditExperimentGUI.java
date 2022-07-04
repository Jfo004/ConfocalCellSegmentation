/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

/**
 *
 * @author janlu
 */
public class EditExperimentGUI extends javax.swing.JDialog {

    /**
     * Creates new form EditExperimentGUI
     */
    public EditExperimentGUI(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BackButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ExperimentOverviewTree = new javax.swing.JTree();
        SaveExperimentButton = new javax.swing.JToggleButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        EditExperimentPanel = new javax.swing.JPanel();
        ExperimentNameLabel = new javax.swing.JLabel();
        ExperimentNameField = new javax.swing.JTextField();
        FertilizationTimeLabel = new javax.swing.JLabel();
        FertilizationDateLabel = new javax.swing.JFormattedTextField();
        FertilizationTimeField = new javax.swing.JFormattedTextField();
        InjectionTimeLabel = new javax.swing.JLabel();
        InectionDateField = new javax.swing.JFormattedTextField();
        InjectionTimeField = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        AddGroupNameField = new javax.swing.JTextField();
        AddGroupButton = new javax.swing.JButton();
        EditGroupPanel = new javax.swing.JPanel();
        GroupNameLabel = new javax.swing.JLabel();
        GroupNameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        AddFishNameLabel = new javax.swing.JLabel();
        AddFishNameField = new javax.swing.JTextField();
        AddFishButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        BackButton.setText("Back");

        jScrollPane1.setViewportView(ExperimentOverviewTree);

        SaveExperimentButton.setText("Save Experiment");

        EditExperimentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ExperimentNameLabel.setText("Experiment Name:");

        ExperimentNameField.setText(" ");

        FertilizationTimeLabel.setText("Fertilization Time:");

        FertilizationDateLabel.setText(" ");

        FertilizationTimeField.setText(" ");

        InjectionTimeLabel.setText("Injection Time:");

        InectionDateField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("y/MM/dd"))));

        InjectionTimeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));

        jLabel1.setText("Confocal Directory:");

        jToggleButton1.setText("Changel Directory");

        jLabel2.setText("Add Group:");

        AddGroupNameField.setText(" ");

        AddGroupButton.setText("Add Group");

        javax.swing.GroupLayout EditExperimentPanelLayout = new javax.swing.GroupLayout(EditExperimentPanel);
        EditExperimentPanel.setLayout(EditExperimentPanelLayout);
        EditExperimentPanelLayout.setHorizontalGroup(
            EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditExperimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EditExperimentPanelLayout.createSequentialGroup()
                        .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(FertilizationTimeLabel)
                            .addComponent(ExperimentNameLabel)
                            .addComponent(jLabel1)
                            .addComponent(InjectionTimeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(EditExperimentPanelLayout.createSequentialGroup()
                                .addComponent(InectionDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(InjectionTimeField))
                            .addGroup(EditExperimentPanelLayout.createSequentialGroup()
                                .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ExperimentNameField)
                                    .addComponent(FertilizationDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FertilizationTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jToggleButton1)
                    .addComponent(jLabel2)
                    .addGroup(EditExperimentPanelLayout.createSequentialGroup()
                        .addComponent(AddGroupNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AddGroupButton)))
                .addContainerGap(424, Short.MAX_VALUE))
        );
        EditExperimentPanelLayout.setVerticalGroup(
            EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditExperimentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ExperimentNameLabel)
                    .addComponent(ExperimentNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FertilizationTimeLabel)
                    .addComponent(FertilizationDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FertilizationTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InectionDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(InjectionTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(InjectionTimeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditExperimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddGroupNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddGroupButton))
                .addContainerGap(616, Short.MAX_VALUE))
        );

        GroupNameLabel.setText("Group Name:");

        GroupNameField.setText(" ");

        jLabel3.setText("Add Fish:");

        AddFishNameLabel.setText("Fish Name:");

        AddFishNameField.setText(" ");

        AddFishButton.setText("Add");

        javax.swing.GroupLayout EditGroupPanelLayout = new javax.swing.GroupLayout(EditGroupPanel);
        EditGroupPanel.setLayout(EditGroupPanelLayout);
        EditGroupPanelLayout.setHorizontalGroup(
            EditGroupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditGroupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EditGroupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EditGroupPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(AddFishNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddFishNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddFishButton))
                    .addGroup(EditGroupPanelLayout.createSequentialGroup()
                        .addComponent(GroupNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GroupNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addContainerGap(458, Short.MAX_VALUE))
        );
        EditGroupPanelLayout.setVerticalGroup(
            EditGroupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditGroupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EditGroupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GroupNameLabel)
                    .addComponent(GroupNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditGroupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddFishNameLabel)
                    .addComponent(AddFishNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddFishButton))
                .addContainerGap(721, Short.MAX_VALUE))
        );

        jLayeredPane1.setLayer(EditExperimentPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(EditGroupPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 726, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(EditExperimentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(EditGroupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(EditExperimentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(EditGroupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BackButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SaveExperimentButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLayeredPane1)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 801, Short.MAX_VALUE)
                    .addComponent(jLayeredPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BackButton)
                    .addComponent(SaveExperimentButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditExperimentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditExperimentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditExperimentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditExperimentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditExperimentGUI dialog = new EditExperimentGUI(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton AddFishButton;
    private javax.swing.JTextField AddFishNameField;
    private javax.swing.JLabel AddFishNameLabel;
    private javax.swing.JButton AddGroupButton;
    private javax.swing.JTextField AddGroupNameField;
    private javax.swing.JButton BackButton;
    private javax.swing.JPanel EditExperimentPanel;
    private javax.swing.JPanel EditGroupPanel;
    private javax.swing.JTextField ExperimentNameField;
    private javax.swing.JLabel ExperimentNameLabel;
    private javax.swing.JTree ExperimentOverviewTree;
    private javax.swing.JFormattedTextField FertilizationDateLabel;
    private javax.swing.JFormattedTextField FertilizationTimeField;
    private javax.swing.JLabel FertilizationTimeLabel;
    private javax.swing.JTextField GroupNameField;
    private javax.swing.JLabel GroupNameLabel;
    private javax.swing.JFormattedTextField InectionDateField;
    private javax.swing.JFormattedTextField InjectionTimeField;
    private javax.swing.JLabel InjectionTimeLabel;
    private javax.swing.JToggleButton SaveExperimentButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}