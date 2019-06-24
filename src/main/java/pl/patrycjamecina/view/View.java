package pl.patrycjamecina.view;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.patrycjamecina.controller.ActionListenersController;
import pl.patrycjamecina.controller.impl.ActionListenersControllerImpl;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
public class View extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton addDataButton;
    private JButton databaseButton;
    private JButton addFileButton;
    private JButton deleteFileButton;
    private JButton goToDatabaseButton;
    private JButton addMoreDataButton;
    private JTable table1;
    private JPasswordField passwordField;
    private JButton loadDataButton;
    private JButton goToDatabaseButton1;
    private JButton loadDataButton1;
    private JButton goToDatabaseTableButton;
    private JTextField dataSourceUrlField;
    private JTextField login_field;
    private JPasswordField pass_field;
    private JButton conn_button;
    private JButton disconn_button;
    private JButton load_data_button;
    private JPanel connectedPanel;
    private JButton extractDataToCSVButton;
    private JPanel fieldsMapping;
    private JPanel login_panel;
    private JComboBox supplier;
    private JButton newSupplierBtn;
    private JButton add_file;
    private JButton delete_file_button;
    private JProgressBar progressBar;
    private JLabel fileLoadedJLabel;
    private JPanel fieldsMappingForExcel;
    private JComboBox comboBoxDatabaseName;
    private JButton loadDataExcel;
    private JButton goToDatabaseExcel;
    private JPanel fieldsMappingExcel;
    private JComboBox[] comboBoxes;
    private JComboBox[] comboBoxesExcel;
    public Map<String, JTextField> textFieldsByName;

    public void initView() {
        JFrame frame = new JFrame("View");
        frame.setTitle("Data Matcher");
        frame.setContentPane(this.panel1);
        frame.setSize(new Dimension(1000, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        ActionListenersController controller = new ActionListenersControllerImpl(this);
    }
}
