package view;
import business.CustomerController;
import core.Helper;
import entity.Customer;
import entity.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;

public class DashboardUI extends JFrame {
    private JPanel container;
    private JTabbedPane tab_menu;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JPanel pnl_customer_filter;
    private JTextField fld_f_customer_name;
    private JComboBox cmb_f_customer_type;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JButton btn_customer_new;
    private JLabel lbl_f_customer_name;
    private JLabel lbl_f_customer_type;
    private User user ;
    private CustomerController customerController;
    private DefaultTableModel tmdl_customer = new DefaultTableModel();
    private JPopupMenu popup_customer = new JPopupMenu();



    public DashboardUI(User user){
        this.user = user;
        this.customerController = new CustomerController();

        if (user == null){
            Helper.showMsg("error");
            dispose();
        }

        this.add(container);
        this.setTitle("Dashboard sayfası");
        this.setSize(1000,500);

        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width)/2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height)/2;
        this.setLocation(x,y);
        this.setVisible(true);

        this.lbl_welcome.setText("Hoşgeldin  : "+ this.user.getName());


        loadCustomerTable(null); // Database deki müşteri bilgilerinin Tabloya getirildiği metot

        loadCustomerPopupMenu();
        loadCustomerButtonEvent();

    }
    private void loadCustomerButtonEvent(){
        //Dashboard sayfasındaki bütün butonlar buradan dinlenip aksiyon alıyor.
        this.btn_customer_new.addActionListener(e -> {
            // CustomerUI a new Customer() eklememizin sebebi id sine bakıp varsa güncelle yoksa ekle diyeceğiz.
            CustomerUI customerUI = new CustomerUI(new Customer());
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                }
            });
        });
        this.btn_logout.addActionListener(e -> {
            dispose();
            LoginUI loginUI = new LoginUI();
        });

    }

    private void loadCustomerPopupMenu(){
        this.tbl_customer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectRow = tbl_customer.rowAtPoint(e.getPoint());
                tbl_customer.setRowSelectionInterval(selectRow,selectRow);
            }
        });
        this.popup_customer.add("Güncelle").addActionListener(e -> {
            int selectId;
            selectId = (int) tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0 );
            CustomerUI customerUI = new CustomerUI(this.customerController.getById(selectId));

            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                }
            });

        });
        this.popup_customer.add("Sil").addActionListener(e -> {
            System.out.println("Sile tıklandı");
        });

        this.tbl_customer.setComponentPopupMenu(this.popup_customer);
    }

    private void loadCustomerTable(ArrayList<Customer> customers){

        // Tablo kolon başlıklarını tanımlar ve atar
        Object[] columnCustomers = {"ID", "Müşteri Adı", "Tipi", "Telefon", "Mail", "Adres"};
        this.tmdl_customer = new DefaultTableModel(columnCustomers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bütün hücreler düzenlenemez
            }
        };

        if (customers == null){
            customers = this.customerController.findAll();
        }
        // Tablo Sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_customer.getModel();
        clearModel.setRowCount(0);

        //datanın tabloya eklenmesi
        for(Customer customer : customers){
            Object[] rowObject = {
                    customer.getId(),
                    customer.getName(),
                    customer.getType(),
                    customer.getPhone(),
                    customer.getMail(),
                    customer.getAddress()
            };
            this.tmdl_customer.addRow(rowObject);
        }

        this.tbl_customer.setModel(tmdl_customer);// tbl_customer Tablosuna tmdl_customer Tablo modelini gönder
        this.tbl_customer.getTableHeader().setReorderingAllowed(false);// Tablo ayarı
        this.tbl_customer.getColumnModel().getColumn(0).setMaxWidth(50);// Tablo ayarı
    }


}
