package pl.patrycjamecina.controller.impl;
import pl.patrycjamecina.controller.ChooseFieldsCreationController;
import pl.patrycjamecina.view.View;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
public class ChooseFieldsCreationControllerImpl implements ChooseFieldsCreationController {
    private View view;
    private JComboBox[] comboBoxes;
    private JComboBox[] comboBoxesExcel;
    private JPanel fieldsMapping;
    private JPanel fieldsMappingExcel;

    public ChooseFieldsCreationControllerImpl(final View view) {
        this.view = view;
        this.comboBoxes = view.getComboBoxes();
        this.fieldsMapping = view.getFieldsMapping();
        this.fieldsMappingExcel = view.getFieldsMappingExcel();
    }

    @Override
    public Map<String, String> getFieldTypeByNames(ResultSet rs) throws SQLException {
        Map<String, String> fieldTypeByNames = new TreeMap<>();
        while (rs.next()) {
            String name = rs.getString("COLUMN_NAME");
            String type = rs.getString("TYPE_NAME");
            fieldTypeByNames.put(name.toLowerCase(), type.toLowerCase());
        }
        return fieldTypeByNames;
    }

    @Override
    public void initFieldsToMap(Map<String, String> internalFieldTypeByNames) {
        if (comboBoxes == null) {
            fieldsMapping = view.getFieldsMapping();
            fieldsMapping.setLayout(new GridLayout(internalFieldTypeByNames.size(), 2));
            final List<String> comboBoxItems = new ArrayList<>();
            comboBoxItems.add("");
            comboBoxes = new JComboBox[internalFieldTypeByNames.size()];
            view.setComboBoxes(comboBoxes);
            int i = 0;
            for (String key : internalFieldTypeByNames.keySet()) {
                JLabel l = new JLabel(key + " (" + internalFieldTypeByNames.get(key) + ")");
                l.setHorizontalAlignment(JLabel.CENTER);
                fieldsMapping.add(l);
                comboBoxes[i] = new JComboBox(new DefaultComboBoxModel(comboBoxItems.toArray()));
                comboBoxes[i].setName(key);
                comboBoxes[i].addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        JComboBox<String> cb = (JComboBox<String>) e.getSource();
                        ComboBoxModel<String> cbm = cb.getModel();
                        Object selectedItem = cbm.getSelectedItem();
                        List<Object> valuesToExclude = new ArrayList<>();
                        for (int i = 0; i < comboBoxes.length; i++) {
                            if (!comboBoxes[i].getSelectedItem().equals("")) {
                                valuesToExclude.add(comboBoxes[i].getSelectedItem());
                            }
                        }
                        List<Object> newModel = new ArrayList<>();
                        for (int i = 0; i < cbm.getSize(); i++) {
                            if (!valuesToExclude.contains(cbm.getElementAt(i))) {
                                newModel.add(cbm.getElementAt(i));
                            }
                        }
                        if (!selectedItem.equals("")) {
                            newModel.add(selectedItem);
                        }
                        cb.setModel(new DefaultComboBoxModel(newModel.toArray()));
                        cb.setSelectedItem(selectedItem);
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                fieldsMapping.add(comboBoxes[i]);
                i++;
            }
        }
    }

    @Override
    public void initFieldsToMapExcel(Map<String, String> internalFieldTypeByNames) {
        if (comboBoxesExcel == null) {
            fieldsMappingExcel = view.getFieldsMappingExcel();
            fieldsMappingExcel.setLayout(new GridLayout(internalFieldTypeByNames.size(), 2));
            final List<String> comboBoxItems = new ArrayList<>();
            comboBoxItems.add("");
            comboBoxesExcel = new JComboBox[internalFieldTypeByNames.size()];
            view.setComboBoxesExcel(comboBoxesExcel);
            int i = 0;
            for (String key : internalFieldTypeByNames.keySet()) {
                JLabel l = new JLabel(key + " (" + internalFieldTypeByNames.get(key) + ")");
                l.setHorizontalAlignment(JLabel.CENTER);
                fieldsMappingExcel.add(l);
                comboBoxesExcel[i] = new JComboBox(new DefaultComboBoxModel(comboBoxItems.toArray()));
                comboBoxesExcel[i].setName(key);
                comboBoxesExcel[i].addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        JComboBox<String> cb = (JComboBox<String>) e.getSource();
                        ComboBoxModel<String> cbm = cb.getModel();
                        Object selectedItem = cbm.getSelectedItem();
                        List<Object> valuesToExclude = new ArrayList<>();
                        for (int i = 0; i < comboBoxesExcel.length; i++) {
                            if (!comboBoxesExcel[i].getSelectedItem().equals("")) {
                                valuesToExclude.add(comboBoxesExcel[i].getSelectedItem());
                            }
                        }
                        List<Object> newModel = new ArrayList<>();
                        for (int i = 0; i < cbm.getSize(); i++) {
                            if (!valuesToExclude.contains(cbm.getElementAt(i))) {
                                newModel.add(cbm.getElementAt(i));
                            }
                        }
                        if (!selectedItem.equals("")) {
                            newModel.add(selectedItem);
                        }
                        cb.setModel(new DefaultComboBoxModel(newModel.toArray()));
                        cb.setSelectedItem(selectedItem);
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                fieldsMappingExcel.add(comboBoxesExcel[i]);
                i++;
            }
        }
    }

    @Override
    public void setComboBoxes(Map<String, String> externalFieldTypeByNames) {
        List<String> comboBoxItems = new ArrayList<>();
        comboBoxItems.add("");
        JComboBox[] comboBoxes = view.getComboBoxes();
        for (String key : externalFieldTypeByNames.keySet()) {
            String s = key + " (" + externalFieldTypeByNames.get(key) + ")";
            comboBoxItems.add(s);
        }
        for (int i = 0; i < comboBoxes.length; i++) {
            comboBoxes[i].setModel(new DefaultComboBoxModel(comboBoxItems.toArray()));
        }
    }

    @Override
    public void setComboBoxesExcel(List<String> externalFieldNames) {
        List<String> comboBoxItems = new ArrayList<>();
        comboBoxItems.add("");
        JComboBox[] comboBoxes = view.getComboBoxesExcel();
        for (String fieldName : externalFieldNames) {
            String s = fieldName;
            comboBoxItems.add(s);
        }
        for (int i = 0; i < comboBoxes.length; i++) {
            comboBoxes[i].setModel(new DefaultComboBoxModel(comboBoxItems.toArray()));
        }
    }

    @Override
    public List<JComboBox> getSelectedComboBoxes() {
        List<JComboBox> selectedComboBoxes = new ArrayList<>();
        for (JComboBox cb : comboBoxes) {
            if (!cb.getSelectedItem().equals("")) {
                selectedComboBoxes.add(cb);
            }
        }
        return selectedComboBoxes;
    }

    @Override
    public List<JComboBox> getSelectedComboBoxesExcel() {
        List<JComboBox> selectedComboBoxes = new ArrayList<>();
        for (JComboBox cb : comboBoxesExcel) {
            if (!cb.getSelectedItem().equals("")) {
                selectedComboBoxes.add(cb);
            }
        }
        return selectedComboBoxes;
    }

    @Override
    public void resetFieldMappingComboboxes(Connection con, String tableName, JComboBox[] comboBoxes) throws SQLException {
        this.comboBoxes = comboBoxes;
        setComboBoxes(getFieldNamesMap(con, tableName));
    }

    @Override
    public Map<String, String> getFieldNamesMap(Connection con, String tableName) throws SQLException {
        ResultSet rsColumns = con.getMetaData().getColumns(null, null, tableName, null);
        return getFieldTypeByNames(rsColumns);
    }

    @Override
    public void display(Map<String, String> fieldNamesMap) {
        fieldNamesMap.remove("id");
        view.textFieldsByName = new TreeMap<>();
        JPanel panel = new JPanel(new GridLayout(fieldNamesMap.size(), 2));
        for (String key : fieldNamesMap.keySet()) {
            JLabel label = new JLabel(key + " (" + fieldNamesMap.get(key) + ")");
            panel.add(label);
            JTextField textField = new JTextField();
            view.textFieldsByName.put(key, textField);
            panel.add(textField);
        }
        int result = JOptionPane.showConfirmDialog(null, panel, "Create new supplier",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
        } else {
        }
    }
}
