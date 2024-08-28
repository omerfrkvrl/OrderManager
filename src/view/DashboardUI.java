package view;
import business.CustomerController;
import business.ProductController;
import core.Helper;
import core.Item;
import entity.Customer;
import entity.Product;
import entity.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DashboardUI extends JFrame {
    private JPanel container;
    private JTabbedPane tab_menu;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JTextField fld_f_customer_name;
    private JComboBox<Customer.TYPE> cmb_f_customer_type;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JButton btn_customer_new;
    private JPanel pnl_product;
    private JScrollPane scrl_product;
    private JTable tbl_product;
    private JPanel pnl_product_filter;
    private JTextField fld_f_product_name;
    private JTextField fld_f_product_code;
    private JComboBox<Item> cmb_f_product_stock;
    private JButton btn_product_filter;
    private JButton btn_product_filter_reset;
    private JButton btn_product_new;
    private JLabel lbl_f_product_name;
    private JLabel lbl_f_product_code;
    private JLabel lbl_f_product_stock;
    private JLabel lbl_f_customer_name;
    private JLabel lbl_f_customer_type;
    private JPanel pnl_customer_filter;
    private User user ;

    private CustomerController customerController;
    private ProductController productController;

    private DefaultTableModel tmdl_customer = new DefaultTableModel();
    private DefaultTableModel tmdl_product = new DefaultTableModel();

    private JPopupMenu popup_customer = new JPopupMenu();
    private JPopupMenu popup_product = new JPopupMenu();



    public DashboardUI(User user){
        this.user = user;
        this.customerController = new CustomerController();
        this.productController = new ProductController();

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

        //CUSTOMER TAB
        loadCustomerTable(null); // Database deki müşteri bilgilerinin Tabloya getirildiği metot
        loadCustomerPopupMenu();
        loadCustomerButtonEvent();
        // Müşteri Tipini belirleyerek Arama Aşağıdaki kodu gpt yazdı
// Enum değerlerini içeren model oluşturulur
        DefaultComboBoxModel<Customer.TYPE> model = new DefaultComboBoxModel<>(Customer.TYPE.values());
// Boş bir seçenek eklenir
        model.insertElementAt(null, 0);
        this.cmb_f_customer_type.setModel(model);
// İlk başta boş seçenek seçili olarak ayarlanır
        this.cmb_f_customer_type.setSelectedItem(null);


        //PRODUCT TAB
        loadProductTable(null);
        loadProductPopupMenu();
        loadProductButtonEvent();
        this.cmb_f_product_stock.addItem(new Item(1,"Stokta var"));
        this.cmb_f_product_stock.addItem(new Item(2, "Stokta yok"));
        this.cmb_f_product_stock.setSelectedItem(null); // ****
    }


    private void loadCustomerButtonEvent(){
        //Dashboard sayfasındaki bütün butonlar buradan dinlenip aksiyon alıyor.
        this.btn_customer_new.addActionListener(e -> {
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

        this.btn_customer_filter.addActionListener(e -> {
            ArrayList<Customer> filteredCustomers = this.customerController.filter(
                    this.fld_f_customer_name.getText(),
                    (Customer.TYPE) this.cmb_f_customer_type.getSelectedItem()
            );
            loadCustomerTable(filteredCustomers);
        });

        btn_customer_filter_reset.addActionListener(e -> {
            loadCustomerTable(null);
            this.cmb_f_customer_type.setSelectedItem(null);
            this.fld_f_customer_name.setText(null);


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
            selectId = Integer.parseInt(  tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0 ).toString());
            CustomerUI customerUI = new CustomerUI(this.customerController.getById(selectId));

            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                }
            });

        });
        this.popup_customer.add("Sil").addActionListener(e -> {
            int selectId = Integer.parseInt(  tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0 ).toString());
            if (Helper.confirm("sure")){
                if(this.customerController.delete(selectId)){
                    Helper.showMsg("done");
                    loadCustomerTable(null);
                }else {
                    Helper.showMsg("error");
                }
            }

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


    private void loadProductButtonEvent(){
        this.btn_product_new.addActionListener(e -> {
            ProductUI productUI = new ProductUI(new Product());
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                }
            });

        });

        this.btn_customer_filter.addActionListener(e -> {

        });
    }
    private void loadProductPopupMenu(){
        this.tbl_product.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectRow = tbl_product.rowAtPoint(e.getPoint());
                tbl_product.setRowSelectionInterval(selectRow,selectRow);
            }
        });
        this.popup_product.add("Güncelle").addActionListener(e -> {
            int selectId;
            selectId = Integer.parseInt(  tbl_product.getValueAt(tbl_product.getSelectedRow(), 0 ).toString());
            ProductUI productUI = new ProductUI(this.productController.getById(selectId));

            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                }
            });

        });
        this.popup_product.add("Sil").addActionListener(e -> {
            int selectId = Integer.parseInt(  tbl_product.getValueAt(tbl_product.getSelectedRow(), 0 ).toString());
            if (Helper.confirm("sure")){
                if(this.productController.delete(selectId)){
                    Helper.showMsg("done");
                    loadProductTable(null);
                }else {
                    Helper.showMsg("error");
                }
            }

        });
        this.tbl_product.setComponentPopupMenu(this.popup_product);

    }
    private void loadProductTable(ArrayList<Product> products){

        // Tablo kolon başlıklarını tanımlar ve atar
        Object[] columnProducts = {"ID", "Ürün Adı", "Ürün Kodu", "Ürün Fiyatı", "Stok Durumu"};
        this.tmdl_product = new DefaultTableModel(columnProducts, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bütün hücreler düzenlenemez
            }
        };

        // ProductDao Ya gidip bütün tabloyu getir methodu uygular
        if (products == null){
            products = this.productController.findAll();
        }
        // Tablo Sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_product.getModel();
        clearModel.setRowCount(0);

        //datanın tabloya eklenmesi
        this.tmdl_product.setColumnIdentifiers(columnProducts);
        for(Product product : products){
            Object[] rowObject = {
                    product.getId(),
                    product.getName(),
                    product.getCode(),
                    product.getPrice(),
                    product.getStock(),
            };
            this.tmdl_product.addRow(rowObject);
        }

        this.tbl_product.setModel(tmdl_product);// tbl_customer Tablosuna tmdl_customer Tablo modelini gönder
        this.tbl_product.getTableHeader().setReorderingAllowed(false);// Tablo ayarı
        this.tbl_product.getColumnModel().getColumn(0).setMaxWidth(50);// Tablo ayarı
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
