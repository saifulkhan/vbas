package uk.ac.isc.textview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

public class PhaseEditDialog extends JDialog {

    private JButton buttonCancel;
    private JButton buttonOK;
    private JButton button_applyToAll;
    private JComboBox comboBox_deleteAmp;
    private JComboBox comboBox_fix;
    private JComboBox comboBox_nondef;
    private JComboBox comboBox_phaseBreak;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JTextField textField_put;
    private JTextField textField_timeShift;
    private JTextField textField_phaseType;

    private JTable table_edit;
    private PhaseEditTableModel phaseEditTableModel;

    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Command formulatedCommand = Global.getFormulatedCommand();

    public PhaseEditDialog() {

        setTitle("Phase Edit");
        setModal(true);
        initComponents();
        table_edit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    public void showPhaseEditDialog(ArrayList<PhaseEditData> phaseEditDataList) {

        phaseEditTableModel = new PhaseEditTableModel(phaseEditDataList);
        table_edit.setModel(phaseEditTableModel);

        table_edit.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    onValueChanged(lse);
                }
            }
        });

        setUpPhaseBreakColumn(table_edit, table_edit.getColumnModel().getColumn(6));

        setVisible(true);
    }

    // Combox in the table.
    private void setUpPhaseBreakColumn(JTable table_edit, TableColumn column) {
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("-");
        comboBox.addItem("Take");
        comboBox.addItem("Delete");
        comboBox.addItem("Put");
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }

    private void onValueChanged(ListSelectionEvent lse) {
        int selectedRow = table_edit.getSelectedRow();
        int selectedCol = table_edit.getSelectedColumn();

        if (selectedRow >= 0 && selectedCol >= 1) {
            switch (selectedCol) {
                case 1:
                    textField_phaseType.setText("NA");
                    break;
                case 2:
                    comboBox_fix.setSelectedIndex(0);
                    break;
                case 3:
                    comboBox_nondef.setSelectedIndex(0);
                    break;
                case 4:
                    textField_timeShift.setText("NA");
                    break;
                case 5:
                    comboBox_deleteAmp.setSelectedIndex(0);
                    break;
                case 6:
                    comboBox_phaseBreak.setSelectedIndex(0);
                    break;
                case 7:
                    textField_put.setText("NA");
                    break;

            }
        }

    }

    private void buttonOKActionPerformed(ActionEvent evt) {

        System.out.println(Global.debugAt());

        PhaseEditTableModel model = (PhaseEditTableModel) table_edit.getModel();
        int nRow = model.getRowCount(), nCol = model.getColumnCount();

        Object[][] tableData = new Object[nRow][nCol];
        for (int i = 0; i < nRow; i++) {
            String command = "<phid> " + (Integer) model.getValueAt(i, 0) + " ";

            System.out.println(model.getValueAt(i, 1) + " "
                    + model.getValueAt(i, 2) + " "
                    + model.getValueAt(i, 3) + " "
                    + model.getValueAt(i, 4) + " "
                    + model.getValueAt(i, 5) + " "
                    + model.getValueAt(i, 6) + " "
                    + model.getValueAt(i, 7) + " ");

            // TODO: phase type; get the old phase type value
            // TODO: compare old and new
            if (!model.getValueAt(i, 1).equals("null")) {
                command += "<attr> " + "phase_fixed "
                        + "<value> " + (String) model.getValueAt(i, 1) + " </value>"
                        + "<prev_value> " + "null" + " </prev_value>"; // TODO:
            }

            if (model.getValueAt(i, 2) != null) {
                command += "<attr> " + "phase_fixed "
                        + "<value> " + (Boolean) model.getValueAt(i, 2) + " </value>"
                        + "<prev_value> " + "null" + " </prev_value>";
            }

            if (model.getValueAt(i, 3) != null) {
                command += "<attr> " + "nondef "
                        + "<value> " + (Boolean) model.getValueAt(i, 3) + " </value>"
                        + "<prev_value> " + "null" + " </prev_value>";
            }

            if (model.getValueAt(i, 4) != null) {
                command += "<attr> " + "time_shift "
                        + "<value> " + (Integer) model.getValueAt(i, 4) + " </value>"
                        + "<prev_value> " + "null" + " </prev_value>";
            }

            if (model.getValueAt(i, 5) != null) {
                command += "<attr> " + "delete_amp "
                        + "<value> " + (Boolean) model.getValueAt(i, 5) + " </value>"
                        + "<prev_value> " + "null" + " </prev_value>";
            }

            if (model.getValueAt(i, 6) != null) {
                if (!model.getValueAt(i, 6).equals("-")) {
                    command += "<attr> " + "phase_break "
                            + "<value> " + (String) model.getValueAt(i, 6) + " </value>"
                            + "<prev_value> " + "null" + " </prev_value>";

                    if (model.getValueAt(i, 7) != null) {
                        if (!model.getValueAt(i, 7).equals("Put")) {
                            command += "<attr> " + "put_value "
                                    + "<value> " + (String) model.getValueAt(i, 7) + " </value>"
                                    + "<prev_value> " + "null" + " </prev_value>";
                        } else {
                            JOptionPane.showMessageDialog(null, "Put (value) at row: " + i, "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            }

            command += " </phid>";

            boolean ret = SeisDataDAO.updateCommandTable(selectedSeisEvent.getEvid(), "chphase", command);
            if (ret) {
                System.out.println(Global.debugAt() + " \nCommand=" + command + " \nFired: New Command from the 'Phase Edit' dialog.");
                formulatedCommand.fireSeisDataChanged();  // Notify the Command table to update from the database.            
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }

        //this.dispose();
    }

    private void buttonCancelActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        this.dispose();
    }

    private void button_applyToAllActionPerformed(ActionEvent evt) {

        PhaseEditTableModel model = (PhaseEditTableModel) table_edit.getModel();
        int nRow = model.getRowCount();

        for (int i = 0; i < nRow; i++) {
            if (!textField_phaseType.getText().equals("NA")) {
                model.setValueAt(textField_phaseType.getText(), i, 1);
            }

            if (!comboBox_fix.getSelectedItem().equals("NA")) {
                model.setValueAt(Boolean.valueOf(comboBox_fix.getSelectedItem().toString()), i, 2);
            }

            if (!comboBox_nondef.getSelectedItem().equals("NA")) {
                model.setValueAt(Boolean.valueOf(comboBox_nondef.getSelectedItem().toString()), i, 3);
            }

            if (!textField_timeShift.getText().equals("NA")) {
                model.setValueAt(Integer.valueOf(textField_timeShift.getText()), i, 4);
            }

            if (!comboBox_deleteAmp.getSelectedItem().equals("NA")) {
                model.setValueAt(Boolean.valueOf(comboBox_deleteAmp.getSelectedItem().toString()), i, 5);
            }

            if (!comboBox_phaseBreak.getSelectedItem().equals("NA")) {
                model.setValueAt(comboBox_phaseBreak.getSelectedItem(), i, 6);
            }

            if (!textField_put.getText().equals("NA")) {
                model.setValueAt(Double.valueOf(textField_put.getText()), i, 7);
            }

        }

    }

    private void textField_phaseTypeActionPerformed(ActionEvent evt) {

    }

    private void textField_timeShiftActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void textField_putActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void comboBox_fixActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void comboBox_nondefActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void comboBox_deleteAmpActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void comboBox_phaseBreakActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void initComponents() {

        buttonOK = new JButton();
        buttonCancel = new JButton();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        table_edit = new JTable();
        textField_timeShift = new JTextField();
        textField_put = new JTextField();
        textField_phaseType = new JTextField();
        button_applyToAll = new JButton();
        comboBox_fix = new JComboBox();
        comboBox_nondef = new JComboBox();
        comboBox_deleteAmp = new JComboBox();
        comboBox_phaseBreak = new JComboBox();

        buttonOK.setBackground(new java.awt.Color(45, 137, 239));
        buttonOK.setForeground(new java.awt.Color(255, 255, 255));
        buttonOK.setText("OK");
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        buttonCancel.setBackground(new java.awt.Color(45, 137, 239));
        buttonCancel.setForeground(new java.awt.Color(255, 255, 255));
        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        jPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Input"));

        jScrollPane1.setViewportView(table_edit);

        textField_phaseType.setText("NA");
        textField_phaseType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                textField_phaseTypeActionPerformed(evt);
            }
        });

        textField_timeShift.setText("NA");
        textField_timeShift.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                textField_timeShiftActionPerformed(evt);
            }
        });

        textField_put.setText("NA");
        textField_put.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                textField_putActionPerformed(evt);
            }
        });

        button_applyToAll.setBackground(new java.awt.Color(45, 137, 239));
        button_applyToAll.setForeground(new java.awt.Color(255, 255, 255));
        button_applyToAll.setText("Apply");
        button_applyToAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_applyToAllActionPerformed(evt);
            }
        });

        comboBox_fix.setModel(new DefaultComboBoxModel(new String[]{"NA", "True", "False"}));
        comboBox_fix.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboBox_fixActionPerformed(evt);
            }
        });

        comboBox_nondef.setModel(new DefaultComboBoxModel(new String[]{"NA", "True", "False"}));
        comboBox_nondef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboBox_nondefActionPerformed(evt);
            }
        });

        comboBox_deleteAmp.setModel(new DefaultComboBoxModel(new String[]{"NA", "True", "False"}));
        comboBox_deleteAmp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboBox_deleteAmpActionPerformed(evt);
            }
        });

        comboBox_phaseBreak.setModel(new DefaultComboBoxModel(new String[]{"NA", "Take", "Delete", "Put"}));
        comboBox_phaseBreak.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboBox_phaseBreakActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(button_applyToAll)
                        .addGap(12, 12, 12)
                        .addComponent(textField_phaseType, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBox_fix, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBox_nondef, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textField_timeShift, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBox_deleteAmp, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBox_phaseBreak, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textField_put, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(14, Short.MAX_VALUE))
                .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(button_applyToAll)
                                .addComponent(textField_phaseType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboBox_fix, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboBox_nondef, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(textField_timeShift, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboBox_deleteAmp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboBox_phaseBreak, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(textField_put, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 210, GroupLayout.PREFERRED_SIZE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonOK, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(buttonCancel))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(buttonOK)
                                .addComponent(buttonCancel))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    /*
     *****************************************************************************************
     * The Phase Edit dialog presents the data in a table.
     *****************************************************************************************
     */
    public class PhaseEditTableModel extends AbstractTableModel {

        private final String[] columnNames = {
            "Phase ID",
            "Type",
            "Fix",
            "Nondef",
            "Time Shift +/- n (mdh)",
            "Delete Amp",
            "Phase Break",
            "Put (Value)"
        };

        private final Class[] columnTypes = new Class[]{
            Integer.class,
            String.class,
            Boolean.class,
            Boolean.class,
            Integer.class,
            Boolean.class,
            Object.class,
            Double.class
        };

        private ArrayList<PhaseEditData> phaseEditDataList;

        public PhaseEditTableModel(ArrayList<PhaseEditData> phaseEditDataList) {
            this.phaseEditDataList = phaseEditDataList;
        }

        public ArrayList<PhaseEditData> getPhaseEditDataList() {
            return phaseEditDataList;
        }

        @Override
        public int getRowCount() {
            return phaseEditDataList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int r, int c) {
            Object retObject = null;

            switch (c) {
                case 0:
                    retObject = phaseEditDataList.get(r).getPhaseId();
                    break;
                case 1:
                    retObject = phaseEditDataList.get(r).getType();
                    break;
                case 2:
                    retObject = phaseEditDataList.get(r).getFix();
                    break;
                case 3:
                    retObject = phaseEditDataList.get(r).getNondef();
                    break;
                case 4:
                    retObject = phaseEditDataList.get(r).getTimeShift();
                    break;
                case 5:
                    retObject = phaseEditDataList.get(r).getDeleteAmp();
                    break;
                case 6:
                    retObject = phaseEditDataList.get(r).getPhaseBreak();
                    break;
                case 7:
                    retObject = phaseEditDataList.get(r).getPutValue();
                    break;
                default:
                    String message = Global.debugAt() + "\nSee the error log file for more information. ";
                    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }

            return retObject;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Class getColumnClass(int c) {
            //System.out.println(Global.debugAt() + "c= " + c + ", getValueAt(0, c)=" + getValueAt(0, c));
            //return getValueAt(0, c).getClass();
            return columnTypes[c];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return false;
            } else {
                return true;
            }
        }

        // Table value can change
        public void setValueAt(Object value, int row, int col) {

            /*System.out.println("Setting value at " + row + "," + col
                    + " to " + value
                    + " (an instance of "
                    + value.getClass() + ")");*/

            PhaseEditData editedData = (PhaseEditData) phaseEditDataList.get(row);
            switch (col) {
                case 1:
                    editedData.setType((String) value);
                    break;
                case 2:
                    editedData.setFix((Boolean) value);
                    break;
                case 3:
                    editedData.setNondef((Boolean) value);
                    break;
                case 4:
                    editedData.setTimeShift((Integer) value);
                    break;
                case 5:
                    editedData.setDeleteAmp((Boolean) value);
                    break;
                case 6:
                    editedData.setPhaseBreak((String) value);
                    break;
                case 7:
                    editedData.setPutValue((Double) value);
                    break;
                default:
                    String message = Global.debugAt() + "\nSee the error log file for more information. ";
                    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }

            phaseEditDataList.set(row, editedData);
            fireTableCellUpdated(row, col);

            //printDebugData();
        }

        private void printDebugData() {

            System.out.println(Global.debugAt());

            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (PhaseEditData d : phaseEditDataList) {
                System.out.println(d.getPhaseId() + "   " + d.getType() + "   "
                        + d.getFix() + "   " + d.getNondef() + "   "
                        + d.getTimeShift() + "   " + d.getDeleteAmp() + "   "
                        + d.getPhaseBreak() + "   " + d.getPutValue());

            }
            System.out.println("--------------------------");
        }
    }

}
