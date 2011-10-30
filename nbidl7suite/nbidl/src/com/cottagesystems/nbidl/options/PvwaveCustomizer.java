/*
 * PvwaveCustomizer.java
 *
 * Created on August 1, 2006, 9:56 AM
 */

package com.cottagesystems.nbidl.options;

import com.cottagesystems.nbidl.Initialize;
import java.io.File;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author  jbf
 */
public class PvwaveCustomizer extends javax.swing.JPanel {
    
    /** Creates new form PvwaveCustomizer */
    public PvwaveCustomizer() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        reparseTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        idlHomeLabel = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        jButton1.setText("jButton1");

        jLabel1.setText("Source Reparse Delay (sec):");
        jLabel1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                PvwaveCustomizer.this.propertyChange(evt);
            }
        });

        reparseTextField.setText("5");

        jLabel2.setText("IDL Home: ");

        idlHomeLabel.setText("c:\\rsi\\idl62\\");

            jButton2.setText("choose");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                            .add(jLabel1)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(reparseTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(layout.createSequentialGroup()
                            .add(jLabel2)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jButton2))
                        .add(layout.createSequentialGroup()
                            .add(10, 10, 10)
                            .add(idlHomeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 219, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(161, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(reparseTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel2)
                        .add(jButton2))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(idlHomeLabel)
                    .addContainerGap(221, Short.MAX_VALUE))
            );
        }// </editor-fold>//GEN-END:initComponents
            
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        pickPvwaveHome();
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void propertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_propertyChange
        changed= true;
    }//GEN-LAST:event_propertyChange
    
    public void populateUI( PvwaveSetting setting ) {
        DecimalFormat df= new DecimalFormat("0.0");
        this.reparseTextField.setText( df.format( setting.getReparseDelaySeconds() ) );
        this.idlHomeLabel.setText( setting.getIDLHome() );
        changed= false;
    }
    
    public PvwaveSetting returnSetting() {
        PvwaveSetting setting= new PvwaveSetting();
        try {
            setting.setReparseDelaySeconds( Double.parseDouble( this.reparseTextField.getText() ) );
            setting.setIDLHome( this.idlHomeLabel.getText() );
        } catch ( NumberFormatException e ) {
            throw new RuntimeException(e);
        }
        return setting;
    }
    
    public boolean changed= false;
    
    private void pickPvwaveHome() {
        JFileChooser chooser= new JFileChooser( new File( idlHomeLabel.getText() ));
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        int result= chooser.showOpenDialog(this);
        if ( result==JFileChooser.APPROVE_OPTION ) {
            idlHomeLabel.setText(String.valueOf(chooser.getSelectedFile()));
            changed= true;
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel idlHomeLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField reparseTextField;
    // End of variables declaration//GEN-END:variables
    
}
