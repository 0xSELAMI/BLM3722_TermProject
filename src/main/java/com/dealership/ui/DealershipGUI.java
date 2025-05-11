package com.dealership.ui;

import com.dealership.model.*;
import com.dealership.service.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// import java.awt.event.ActionEvent; // Kullanılmıyorsa kaldırılabilir
// import java.awt.event.ActionListener; // Kullanılmıyorsa kaldırılabilir
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DealershipGUI extends JFrame {
    private final StockService stockService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final TestDriveService testDriveService;
    private final QuoteService quoteService;
    private final ReportService reportService;

    private JTabbedPane tabbedPane;

    private JTextField txtChassis, txtBrand, txtModel, txtYear, txtPackage, txtPrice;
    private JComboBox<VehicleStatus> cmbVehicleStatusUpdate;
    private JTextField txtQueryBrand, txtQueryModel, txtQueryYear, txtQueryPackage;
    private JTable tblStockQueryResults;
    private DefaultTableModel stockTableModel;
    private JComboBox<Vehicle> cmbVehiclesForStatusUpdateLocal;

    private JTextField txtCustName, txtCustContact;
    private JTable tblCustomerQueryResults;
    private DefaultTableModel customerTableModel;

    private JComboBox<Customer> cmbOrderCustomer;
    private JComboBox<Vehicle> cmbOrderVehicle;
    private JTextField txtOrderPrice;
    private JTable tblOrders;
    private DefaultTableModel orderTableModel;
    private JComboBox<Order> cmbUpdateOrderStatusOrder;
    private JComboBox<OrderStatus> cmbUpdateOrderStatusValue;

    private JComboBox<Customer> cmbTestDriveCustomer;
    private JComboBox<Vehicle> cmbTestDriveVehicle;
    private JTextField txtTestDriveDateTime;
    private JTextField txtTestDriveDuration;
    private JTable tblTestDrives;
    private DefaultTableModel testDriveTableModel;
    private JComboBox<TestDrive> cmbUpdateTestDriveStatusTestDrive;
    private JComboBox<TestDriveStatus> cmbUpdateTestDriveStatusValue;

    private JComboBox<Customer> cmbQuoteCustomer;
    private JComboBox<Vehicle> cmbQuoteVehicle;
    private JTextField txtQuotePrice;
    private JTextField txtQuoteValidUntil;
    private JTable tblQuotes;
    private DefaultTableModel quoteTableModel;

    private JTextArea areaSalesReport;
    private JTextField txtForecastPeriods;
    private JTextArea areaSalesForecast;
    private JComboBox<Customer> cmbCustomerReport;
    private JTextArea areaCustomerInteractionReport;
    private JPanel pnlSalesPieChart;
    private JTable tblDetailedSalesReport;
    private DefaultTableModel detailedVehicleSalesReportTableModel;
    private JPanel pnlDetailedSalesPieChartContainer;

    public DealershipGUI(StockService stockService, CustomerService customerService, OrderService orderService, TestDriveService testDriveService, QuoteService quoteService, ReportService reportService) {
        this.stockService = stockService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.testDriveService = testDriveService;
        this.quoteService = quoteService;
        this.reportService = reportService;

        setTitle("Araç Satış Bilgi Sistemi (PostgreSQL - Maven)");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadUIData();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Araç İşlemleri", createVehiclePanel());
        tabbedPane.addTab("Müşteri İşlemleri", createCustomerPanel());
        tabbedPane.addTab("Sipariş İşlemleri", createOrderPanel());
        tabbedPane.addTab("Test Sürüşü", createTestDrivePanel());
        tabbedPane.addTab("Fiyat Teklifi", createQuotePanel());
        tabbedPane.addTab("Raporlama", createReportPanel());
        add(tabbedPane);
    }

    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JTabbedPane vehicleSubTabs = new JTabbedPane();
        JPanel addVehiclePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcAdd = new GridBagConstraints();
        gbcAdd.insets = new Insets(5,5,5,5); gbcAdd.fill = GridBagConstraints.HORIZONTAL;
        gbcAdd.gridx = 0; gbcAdd.gridy = 0; addVehiclePanel.add(new JLabel("Şase No:"), gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 0; txtChassis = new JTextField(20); addVehiclePanel.add(txtChassis, gbcAdd);
        gbcAdd.gridx = 0; gbcAdd.gridy = 1; addVehiclePanel.add(new JLabel("Marka:"), gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 1; txtBrand = new JTextField(20); addVehiclePanel.add(txtBrand, gbcAdd);
        gbcAdd.gridx = 0; gbcAdd.gridy = 2; addVehiclePanel.add(new JLabel("Model:"), gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 2; txtModel = new JTextField(20); addVehiclePanel.add(txtModel, gbcAdd);
        gbcAdd.gridx = 0; gbcAdd.gridy = 3; addVehiclePanel.add(new JLabel("Yıl:"), gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 3; txtYear = new JTextField(20); addVehiclePanel.add(txtYear, gbcAdd);
        gbcAdd.gridx = 0; gbcAdd.gridy = 4; addVehiclePanel.add(new JLabel("Paket:"), gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 4; txtPackage = new JTextField(20); addVehiclePanel.add(txtPackage, gbcAdd);
        gbcAdd.gridx = 0; gbcAdd.gridy = 5; addVehiclePanel.add(new JLabel("Fiyat:"), gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 5; txtPrice = new JTextField(20); addVehiclePanel.add(txtPrice, gbcAdd);
        gbcAdd.gridx = 1; gbcAdd.gridy = 6; gbcAdd.fill = GridBagConstraints.NONE; gbcAdd.anchor = GridBagConstraints.EAST;
        JButton btnAddVehicle = new JButton("Stoğa Ekle"); btnAddVehicle.addActionListener(e -> addVehicleToStockAction());
        addVehiclePanel.add(btnAddVehicle, gbcAdd); vehicleSubTabs.addTab("Stoğa Araç Ekle", addVehiclePanel);
        JPanel queryStockPanel = new JPanel(new BorderLayout(5,5));
        JPanel queryFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtQueryBrand = new JTextField(10); txtQueryModel = new JTextField(10); txtQueryYear = new JTextField(5); txtQueryPackage = new JTextField(10);
        JButton btnQueryStock = new JButton("Sorgula");
        queryFormPanel.add(new JLabel("Marka:")); queryFormPanel.add(txtQueryBrand); queryFormPanel.add(new JLabel("Model:")); queryFormPanel.add(txtQueryModel);
        queryFormPanel.add(new JLabel("Yıl:")); queryFormPanel.add(txtQueryYear); queryFormPanel.add(new JLabel("Paket:")); queryFormPanel.add(txtQueryPackage);
        queryFormPanel.add(btnQueryStock);
        btnQueryStock.addActionListener(e -> queryStockAction());
        DocumentListener stockQueryUpdater = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { queryStockAction(); }
            public void removeUpdate(DocumentEvent e) { queryStockAction(); }
            public void insertUpdate(DocumentEvent e) { queryStockAction(); }
        };
        txtQueryBrand.getDocument().addDocumentListener(stockQueryUpdater);
        txtQueryModel.getDocument().addDocumentListener(stockQueryUpdater);
        txtQueryYear.getDocument().addDocumentListener(stockQueryUpdater);
        txtQueryPackage.getDocument().addDocumentListener(stockQueryUpdater);
        stockTableModel = new DefaultTableModel(new String[]{"Şase No", "Marka", "Model", "Yıl", "Paket", "Fiyat", "Durum"}, 0){
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblStockQueryResults = new JTable(stockTableModel); queryStockPanel.add(queryFormPanel, BorderLayout.NORTH);
        queryStockPanel.add(new JScrollPane(tblStockQueryResults), BorderLayout.CENTER);
        vehicleSubTabs.addTab("Stok Sorgula", queryStockPanel);
        JPanel manageVehiclePanel = new JPanel(new GridBagLayout()); GridBagConstraints gbcManage = new GridBagConstraints();
        gbcManage.insets = new Insets(5,5,5,5); gbcManage.fill = GridBagConstraints.HORIZONTAL;
        gbcManage.gridx = 0; gbcManage.gridy = 0;
        gbcManage.gridwidth = 1;
        manageVehiclePanel.add(new JLabel("Araç Seç:"), gbcManage);
        cmbVehiclesForStatusUpdateLocal = new JComboBox<>();
        gbcManage.gridx = 1; gbcManage.gridy = 0;
        manageVehiclePanel.add(cmbVehiclesForStatusUpdateLocal, gbcManage);
        gbcManage.gridx = 0; gbcManage.gridy = 1;
        manageVehiclePanel.add(new JLabel("Yeni Durum:"), gbcManage);
        gbcManage.gridx = 1; gbcManage.gridy = 1;
        cmbVehicleStatusUpdate = new JComboBox<>(VehicleStatus.values());
        manageVehiclePanel.add(cmbVehicleStatusUpdate, gbcManage);
        gbcManage.gridx = 1; gbcManage.gridy = 2;
        gbcManage.fill = GridBagConstraints.NONE;
        gbcManage.anchor = GridBagConstraints.EAST;
        JButton btnUpdateVehicleStatus = new JButton("Durum Güncelle");
        manageVehiclePanel.add(btnUpdateVehicleStatus, gbcManage);
        btnUpdateVehicleStatus.addActionListener(e -> {
            Vehicle selectedVehicle = (Vehicle) cmbVehiclesForStatusUpdateLocal.getSelectedItem();
            VehicleStatus newStatus = (VehicleStatus) cmbVehicleStatusUpdate.getSelectedItem();
            if (selectedVehicle != null && newStatus != null && !selectedVehicle.getChassisNumber().equals("NO_VEHICLE_ID")) {
                updateVehicleStatusAction(selectedVehicle.getChassisNumber(), newStatus);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen geçerli bir araç ve yeni durum seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
        vehicleSubTabs.addTab("Araç Yönetimi", manageVehiclePanel);
        panel.add(vehicleSubTabs, BorderLayout.CENTER);
        return panel;
    }

    private void addVehicleToStockAction() {
        try {
            String chassis = txtChassis.getText().trim(); String brand = txtBrand.getText().trim(); String model = txtModel.getText().trim();
            String yearStr = txtYear.getText().trim(); String carPackage = txtPackage.getText().trim(); String priceStr = txtPrice.getText().trim();
            if (chassis.isEmpty() || brand.isEmpty() || model.isEmpty() || yearStr.isEmpty() || priceStr.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Şase No, Marka, Model, Yıl ve Fiyat boş olamaz!", "Giriş Hatası", JOptionPane.ERROR_MESSAGE); return;
            }
            int year = Integer.parseInt(yearStr); double price = Double.parseDouble(priceStr);
            Vehicle vehicle = new Vehicle(chassis, brand, model, year, carPackage, price);
            if (stockService.addVehicleToStock(vehicle)) {
                JOptionPane.showMessageDialog(this, "Araç stoğa eklendi: " + vehicle.getChassisNumber(), "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                txtChassis.setText(""); txtBrand.setText(""); txtModel.setText(""); txtYear.setText(""); txtPackage.setText(""); txtPrice.setText("");
                loadUIData();
            } else {
                JOptionPane.showMessageDialog(this, "Bu şase numarası zaten mevcut veya ekleme sırasında bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Yıl veya Fiyat için geçerli bir sayı girin!", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void queryStockAction() {
        String brand = txtQueryBrand.getText();
        String modelText = txtQueryModel.getText();
        Integer yearVal = null;
        try {
            if (!txtQueryYear.getText().trim().isEmpty()) {
                yearVal = Integer.parseInt(txtQueryYear.getText().trim());
            }
        } catch (NumberFormatException ex) { /* Hata yok */ }
        String packageText = txtQueryPackage.getText();
        List<Vehicle> results = stockService.queryStock(brand, modelText, yearVal, packageText);
        stockTableModel.setRowCount(0);
        for (Vehicle v : results) {
            stockTableModel.addRow(new Object[]{v.getChassisNumber(), v.getBrand(), v.getModel(), v.getYear(), v.getCarPackage(), v.getPrice(), v.getStatus().getDisplayName()});
        }
    }

    private void updateVehicleStatusAction(String chassisNumber, VehicleStatus newStatus) {
        if (stockService.updateVehicleStatus(chassisNumber, newStatus)) {
            JOptionPane.showMessageDialog(this, chassisNumber + " şaseli aracın durumu güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            loadUIData();
        } else {
            JOptionPane.showMessageDialog(this, "Araç durumu güncellenemedi.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JTabbedPane customerSubTabs = new JTabbedPane();
        JPanel registerCustomerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcRegCust = new GridBagConstraints();
        gbcRegCust.insets = new Insets(5,5,5,5); gbcRegCust.fill = GridBagConstraints.HORIZONTAL;
        gbcRegCust.gridx = 0; gbcRegCust.gridy = 0; registerCustomerPanel.add(new JLabel("Ad Soyad:"), gbcRegCust);
        gbcRegCust.gridx = 1; gbcRegCust.gridy = 0; txtCustName = new JTextField(20); registerCustomerPanel.add(txtCustName, gbcRegCust);
        gbcRegCust.gridx = 0; gbcRegCust.gridy = 1; registerCustomerPanel.add(new JLabel("İletişim Bilgisi:"), gbcRegCust);
        gbcRegCust.gridx = 1; gbcRegCust.gridy = 1; txtCustContact = new JTextField(20); registerCustomerPanel.add(txtCustContact, gbcRegCust);
        gbcRegCust.gridx = 1; gbcRegCust.gridy = 2; gbcRegCust.fill = GridBagConstraints.NONE; gbcRegCust.anchor = GridBagConstraints.EAST;
        JButton btnRegisterCustomer = new JButton("Müşteri Kaydet"); btnRegisterCustomer.addActionListener(e -> registerCustomerAction());
        registerCustomerPanel.add(btnRegisterCustomer, gbcRegCust);
        customerSubTabs.addTab("Müşteri Kaydet", registerCustomerPanel);
        JPanel listCustomerPanel = new JPanel(new BorderLayout(5,5));
        customerTableModel = new DefaultTableModel(new String[]{"Müşteri ID", "Ad Soyad", "İletişim"}, 0){
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCustomerQueryResults = new JTable(customerTableModel);
        JButton btnRefreshCustomers = new JButton("Müşterileri Yenile");
        btnRefreshCustomers.addActionListener(e -> loadAllCustomersToTableAction());
        listCustomerPanel.add(btnRefreshCustomers, BorderLayout.NORTH);
        listCustomerPanel.add(new JScrollPane(tblCustomerQueryResults), BorderLayout.CENTER);
        customerSubTabs.addTab("Müşterileri Listele", listCustomerPanel);
        panel.add(customerSubTabs, BorderLayout.CENTER);
        return panel;
    }
    private void registerCustomerAction() {
        String custId = "CUST-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String name = txtCustName.getText().trim();
        String contact = txtCustContact.getText().trim();
        if (name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ad Soyad ve İletişim Bilgisi boş olamaz!", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Customer customer = new Customer(custId, name, contact);
        if (customerService.registerCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Müşteri kaydedildi: " + customer.getName() + " (ID: " + custId + ")", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            String initialVisitEventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            InteractionEvent initialVisitEvent = new InteractionEvent(
                initialVisitEventId, customer.getCustomerId(), InteractionEventType.INITIAL_VISIT,
                LocalDateTime.now(), null,
                "Müşteri kaydı ile otomatik oluşturulan ilk ziyaret."
            );
            customerService.addInteractionEvent(initialVisitEvent);
            txtCustName.setText(""); txtCustContact.setText("");
            loadUIData();
        } else {
            JOptionPane.showMessageDialog(this, "Müşteri kaydı sırasında bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadAllCustomersToTableAction() {
        List<Customer> customers = customerService.getAllCustomers(); customerTableModel.setRowCount(0);
        for (Customer c : customers) customerTableModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getContactInfo()});
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JTabbedPane orderSubTabs = new JTabbedPane();
        JPanel createOrderPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcOrder = new GridBagConstraints();
        gbcOrder.insets = new Insets(5,5,5,5); gbcOrder.fill = GridBagConstraints.HORIZONTAL;
        gbcOrder.gridx = 0; gbcOrder.gridy = 0; createOrderPanel.add(new JLabel("Müşteri Seç:"), gbcOrder);
        gbcOrder.gridx = 1; gbcOrder.gridy = 0; cmbOrderCustomer = new JComboBox<>(); createOrderPanel.add(cmbOrderCustomer, gbcOrder);
        gbcOrder.gridx = 0; gbcOrder.gridy = 1; createOrderPanel.add(new JLabel("Araç Seç:"), gbcOrder);
        gbcOrder.gridx = 1; gbcOrder.gridy = 1; cmbOrderVehicle = new JComboBox<>(); createOrderPanel.add(cmbOrderVehicle, gbcOrder);
        gbcOrder.gridx = 0; gbcOrder.gridy = 2; createOrderPanel.add(new JLabel("Sipariş Fiyatı:"), gbcOrder);
        gbcOrder.gridx = 1; gbcOrder.gridy = 2; txtOrderPrice = new JTextField(15); createOrderPanel.add(txtOrderPrice, gbcOrder);
        gbcOrder.gridx = 1; gbcOrder.gridy = 3; JButton btnPlaceOrder = new JButton("Sipariş Oluştur");
        btnPlaceOrder.addActionListener(e -> placeOrderAction()); createOrderPanel.add(btnPlaceOrder, gbcOrder);
        orderSubTabs.addTab("Sipariş Oluştur", createOrderPanel);
        JPanel listOrdersPanel = new JPanel(new BorderLayout(5,5));
        orderTableModel = new DefaultTableModel(new String[]{"Sipariş ID", "Müşteri ID", "Araç Şase No", "Tarih", "Fiyat", "Durum"}, 0){
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblOrders = new JTable(orderTableModel);
        tblOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrders.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tblOrders.getSelectedRow() != -1) {
                    String selectedOrderId = (String) tblOrders.getValueAt(tblOrders.getSelectedRow(), 0);
                    Order selectedOrder = orderService.findOrderById(selectedOrderId);
                    if (selectedOrder != null) {
                        cmbUpdateOrderStatusOrder.setSelectedItem(selectedOrder);
                    }
                }
            }
        });
        JButton btnRefreshOrders = new JButton("Siparişleri Yenile");
        btnRefreshOrders.addActionListener(e -> loadAllOrdersToTableAction());
        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        updatePanel.add(new JLabel("Sipariş Seç:")); cmbUpdateOrderStatusOrder = new JComboBox<>(); updatePanel.add(cmbUpdateOrderStatusOrder);
        updatePanel.add(new JLabel("Yeni Durum:")); cmbUpdateOrderStatusValue = new JComboBox<>(OrderStatus.values()); updatePanel.add(cmbUpdateOrderStatusValue);
        JButton btnUpdateOrder = new JButton("Durumu Güncelle"); btnUpdateOrder.addActionListener(e -> updateOrderStatusAction());
        updatePanel.add(btnUpdateOrder);
        JPanel topPanel = new JPanel(new BorderLayout()); topPanel.add(btnRefreshOrders, BorderLayout.NORTH); topPanel.add(updatePanel, BorderLayout.SOUTH);
        listOrdersPanel.add(topPanel, BorderLayout.NORTH); listOrdersPanel.add(new JScrollPane(tblOrders), BorderLayout.CENTER);
        orderSubTabs.addTab("Siparişleri Listele/Güncelle", listOrdersPanel);
        panel.add(orderSubTabs, BorderLayout.CENTER); return panel;
    }
    private void placeOrderAction() {
        Customer customer = (Customer) cmbOrderCustomer.getSelectedItem(); Vehicle vehicle = (Vehicle) cmbOrderVehicle.getSelectedItem();
        String priceStr = txtOrderPrice.getText().trim();
        if (customer == null || vehicle == null || priceStr.isEmpty() || "NO_VEHICLE_ID".equals(vehicle.getChassisNumber())) {
             JOptionPane.showMessageDialog(this, "Müşteri, geçerli bir araç ve fiyat zorunludur.", "Hata", JOptionPane.ERROR_MESSAGE); return;
        }
        try {
            double price = Double.parseDouble(priceStr);
            Order order = orderService.placeOrder(customer.getCustomerId(), vehicle.getChassisNumber(), price);
            if (order != null) {
                JOptionPane.showMessageDialog(this, "Sipariş oluşturuldu: " + order.getOrderId(), "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                loadUIData();
            } else { JOptionPane.showMessageDialog(this, "Sipariş oluşturulamadı. Araç uygun olmayabilir veya stokta kalmamış olabilir.", "Hata", JOptionPane.ERROR_MESSAGE); }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Fiyat için geçerli bir sayı girin.", "Format Hatası", JOptionPane.ERROR_MESSAGE); }
    }
    private void updateOrderStatusAction() {
        Order selectedOrder = (Order) cmbUpdateOrderStatusOrder.getSelectedItem(); OrderStatus newStatus = (OrderStatus) cmbUpdateOrderStatusValue.getSelectedItem();
        if (selectedOrder == null || newStatus == null) { JOptionPane.showMessageDialog(this, "Lütfen sipariş ve yeni durum seçin.", "Hata", JOptionPane.ERROR_MESSAGE); return; }
        if (orderService.updateOrderStatus(selectedOrder.getOrderId(), newStatus)) {
            JOptionPane.showMessageDialog(this, "Sipariş durumu güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            loadUIData();
        } else { JOptionPane.showMessageDialog(this, "Sipariş durumu güncellenemedi.", "Hata", JOptionPane.ERROR_MESSAGE); }
    }
     private void loadAllOrdersToTableAction() {
        List<Order> orders = orderService.getAllOrders(); orderTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Order o : orders) orderTableModel.addRow(new Object[]{o.getOrderId(), o.getCustomerIdFk(), o.getVehicleChassisNumberFk(), o.getOrderDate().format(formatter), o.getTotalPrice(), o.getStatus().getDisplayName()});
    }

    private JPanel createTestDrivePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); JTabbedPane testDriveSubTabs = new JTabbedPane();
        JPanel schedulePanel = new JPanel(new GridBagLayout()); GridBagConstraints gbcTD = new GridBagConstraints();
        gbcTD.insets = new Insets(5,5,5,5); gbcTD.fill = GridBagConstraints.HORIZONTAL;
        gbcTD.gridx = 0; gbcTD.gridy = 0; schedulePanel.add(new JLabel("Müşteri Seç:"), gbcTD);
        gbcTD.gridx = 1; gbcTD.gridy = 0; cmbTestDriveCustomer = new JComboBox<>(); schedulePanel.add(cmbTestDriveCustomer, gbcTD);
        gbcTD.gridx = 0; gbcTD.gridy = 1; schedulePanel.add(new JLabel("Araç Seç:"), gbcTD);
        gbcTD.gridx = 1; gbcTD.gridy = 1; cmbTestDriveVehicle = new JComboBox<>(); schedulePanel.add(cmbTestDriveVehicle, gbcTD);
        gbcTD.gridx = 0; gbcTD.gridy = 2; schedulePanel.add(new JLabel("Tarih/Saat (yyyy-AA-gg SS:dd):"), gbcTD);
        gbcTD.gridx = 1; gbcTD.gridy = 2; txtTestDriveDateTime = new JTextField(16); schedulePanel.add(txtTestDriveDateTime, gbcTD);
        gbcTD.gridx = 0; gbcTD.gridy = 3; schedulePanel.add(new JLabel("Süre (dakika):"), gbcTD);
        gbcTD.gridx = 1; gbcTD.gridy = 3; txtTestDriveDuration = new JTextField("60", 5); schedulePanel.add(txtTestDriveDuration, gbcTD);
        gbcTD.gridx = 1; gbcTD.gridy = 4; JButton btnScheduleTestDrive = new JButton("Test Sürüşü Planla");
        btnScheduleTestDrive.addActionListener(e -> scheduleTestDriveAction()); schedulePanel.add(btnScheduleTestDrive, gbcTD);
        testDriveSubTabs.addTab("Test Sürüşü Planla", schedulePanel);
        JPanel listTestDrivesPanel = new JPanel(new BorderLayout(5,5));
        testDriveTableModel = new DefaultTableModel(new String[]{"ID", "Müşteri ID", "Araç Şase No", "Planlanan Zaman", "Süre (dk)", "Durum"}, 0){
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTestDrives = new JTable(testDriveTableModel);
        tblTestDrives.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTestDrives.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblTestDrives.getSelectedRow() != -1) {
                String selectedTestDriveId = (String) tblTestDrives.getValueAt(tblTestDrives.getSelectedRow(), 0);
                TestDrive selectedTestDrive = testDriveService.findTestDriveById(selectedTestDriveId);
                if (selectedTestDrive != null) {
                    cmbUpdateTestDriveStatusTestDrive.setSelectedItem(selectedTestDrive);
                }
            }
        });
        JButton btnRefreshTestDrives = new JButton("Test Sürüşlerini Yenile");
        btnRefreshTestDrives.addActionListener(e -> loadAllTestDrivesToTableAction());
        JPanel updateTDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        updateTDPanel.add(new JLabel("Test Sürüşü Seç:")); cmbUpdateTestDriveStatusTestDrive = new JComboBox<>(); updateTDPanel.add(cmbUpdateTestDriveStatusTestDrive);
        updateTDPanel.add(new JLabel("Yeni Durum:")); cmbUpdateTestDriveStatusValue = new JComboBox<>(TestDriveStatus.values()); updateTDPanel.add(cmbUpdateTestDriveStatusValue);
        JButton btnUpdateTD = new JButton("Durumu Güncelle"); btnUpdateTD.addActionListener(e -> updateTestDriveStatusAction());
        updateTDPanel.add(btnUpdateTD);
        JPanel topTDPanel = new JPanel(new BorderLayout()); topTDPanel.add(btnRefreshTestDrives, BorderLayout.NORTH); topTDPanel.add(updateTDPanel, BorderLayout.SOUTH);
        listTestDrivesPanel.add(topTDPanel, BorderLayout.NORTH); listTestDrivesPanel.add(new JScrollPane(tblTestDrives), BorderLayout.CENTER);
        testDriveSubTabs.addTab("Test Sürüşlerini Listele/Güncelle", listTestDrivesPanel);
        panel.add(testDriveSubTabs, BorderLayout.CENTER); return panel;
    }
    private void scheduleTestDriveAction() {
        Customer customer = (Customer) cmbTestDriveCustomer.getSelectedItem();
        Vehicle vehicle = (Vehicle) cmbTestDriveVehicle.getSelectedItem();
        String dateTimeStr = txtTestDriveDateTime.getText().trim();
        String durationStr = txtTestDriveDuration.getText().trim();
        if (customer == null || vehicle == null || dateTimeStr.isEmpty() || durationStr.isEmpty() || "NO_VEHICLE_ID".equals(vehicle.getChassisNumber())) {
            JOptionPane.showMessageDialog(this, "Müşteri, geçerli bir araç, tarih/saat ve süre zorunludur.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            LocalDateTime scheduledTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            int durationMinutes = Integer.parseInt(durationStr);
            if (durationMinutes <= 0) {
                JOptionPane.showMessageDialog(this, "Süre pozitif bir değer olmalıdır.", "Format Hatası", JOptionPane.ERROR_MESSAGE);
                return;
            }
            TestDrive td = testDriveService.scheduleTestDrive(customer.getCustomerId(), vehicle.getChassisNumber(), scheduledTime, durationMinutes);
            if (td != null) {
                JOptionPane.showMessageDialog(this, "Test sürüşü planlandı: " + td.getTestDriveId(), "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                loadUIData();
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Tarih/Saat formatı hatalı (yyyy-AA-gg SS:dd).", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Süre için geçerli bir sayı girin.", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
     private void updateTestDriveStatusAction() {
        TestDrive selectedTestDrive = (TestDrive) cmbUpdateTestDriveStatusTestDrive.getSelectedItem(); TestDriveStatus newStatus = (TestDriveStatus) cmbUpdateTestDriveStatusValue.getSelectedItem();
        if (selectedTestDrive == null || newStatus == null) { JOptionPane.showMessageDialog(this, "Lütfen test sürüşü ve yeni durum seçin.", "Hata", JOptionPane.ERROR_MESSAGE); return; }
        if (testDriveService.updateTestDriveStatus(selectedTestDrive.getTestDriveId(), newStatus)) {
            JOptionPane.showMessageDialog(this, "Test sürüşü durumu güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE); loadUIData();
        } else { JOptionPane.showMessageDialog(this, "Test sürüşü durumu güncellenemedi.", "Hata", JOptionPane.ERROR_MESSAGE); }
    }
    private void loadAllTestDrivesToTableAction() {
        List<TestDrive> testDrives = testDriveService.getAllTestDrives(); testDriveTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (TestDrive td : testDrives) {
            testDriveTableModel.addRow(new Object[]{
                td.getTestDriveId(),
                td.getCustomerIdFk(),
                td.getVehicleChassisNumberFk(),
                td.getScheduledDateTime().format(formatter),
                td.getDurationMinutes(),
                td.getStatus().getDisplayName()
            });
        }
    }

    private JPanel createQuotePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); JTabbedPane quoteSubTabs = new JTabbedPane();
        JPanel createQuotePanel = new JPanel(new GridBagLayout()); GridBagConstraints gbcQuote = new GridBagConstraints();
        gbcQuote.insets = new Insets(5,5,5,5); gbcQuote.fill = GridBagConstraints.HORIZONTAL;
        gbcQuote.gridx = 0; gbcQuote.gridy = 0; createQuotePanel.add(new JLabel("Müşteri Seç:"), gbcQuote);
        gbcQuote.gridx = 1; gbcQuote.gridy = 0; cmbQuoteCustomer = new JComboBox<>(); createQuotePanel.add(cmbQuoteCustomer, gbcQuote);
        gbcQuote.gridx = 0; gbcQuote.gridy = 1; createQuotePanel.add(new JLabel("Araç Seç (Opsiyonel):"), gbcQuote);
        cmbQuoteVehicle = new JComboBox<>();
        gbcQuote.gridx = 1; gbcQuote.gridy = 1; createQuotePanel.add(cmbQuoteVehicle, gbcQuote);
        gbcQuote.gridx = 0; gbcQuote.gridy = 2; createQuotePanel.add(new JLabel("Teklif Fiyatı:"), gbcQuote);
        gbcQuote.gridx = 1; gbcQuote.gridy = 2; txtQuotePrice = new JTextField(15); createQuotePanel.add(txtQuotePrice, gbcQuote);
        gbcQuote.gridx = 0; gbcQuote.gridy = 3; createQuotePanel.add(new JLabel("Geçerlilik Tarihi (yyyy-AA-gg):"), gbcQuote);
        gbcQuote.gridx = 1; gbcQuote.gridy = 3; txtQuoteValidUntil = new JTextField(10); createQuotePanel.add(txtQuoteValidUntil, gbcQuote);
        gbcQuote.gridx = 1; gbcQuote.gridy = 4; JButton btnCreateQuote = new JButton("Teklif Oluştur");
        btnCreateQuote.addActionListener(e -> createQuoteAction()); createQuotePanel.add(btnCreateQuote, gbcQuote);
        quoteSubTabs.addTab("Teklif Oluştur", createQuotePanel);
        JPanel listQuotesPanel = new JPanel(new BorderLayout(5,5));
        quoteTableModel = new DefaultTableModel(new String[]{"ID", "Müşteri ID", "Araç Şase No", "Teklif Tarihi", "Fiyat", "Geçerlilik"}, 0){
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblQuotes = new JTable(quoteTableModel);
        tblQuotes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton btnRefreshQuotes = new JButton("Teklifleri Yenile");
        btnRefreshQuotes.addActionListener(e -> loadAllQuotesToTableAction());
        listQuotesPanel.add(btnRefreshQuotes, BorderLayout.NORTH);
        listQuotesPanel.add(new JScrollPane(tblQuotes), BorderLayout.CENTER);
        quoteSubTabs.addTab("Teklifleri Listele", listQuotesPanel);
        panel.add(quoteSubTabs, BorderLayout.CENTER); return panel;
    }
    private void createQuoteAction() {
        Customer customer = (Customer) cmbQuoteCustomer.getSelectedItem();
        Vehicle vehicle = (Vehicle) cmbQuoteVehicle.getSelectedItem();
        String priceStr = txtQuotePrice.getText().trim();
        String validUntilStr = txtQuoteValidUntil.getText().trim();
        if (customer == null || priceStr.isEmpty() || validUntilStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Müşteri, fiyat ve geçerlilik tarihi zorunludur.", "Hata", JOptionPane.ERROR_MESSAGE); return;
        }
        String vehicleChassisNumber = (vehicle != null && !vehicle.getChassisNumber().equals("NO_VEHICLE_ID")) ? vehicle.getChassisNumber() : null;
        try {
            double price = Double.parseDouble(priceStr);
            LocalDate validUntil = LocalDate.parse(validUntilStr, DateTimeFormatter.ISO_LOCAL_DATE);
            Quote quote = quoteService.createQuote(customer.getCustomerId(), vehicleChassisNumber, price, validUntil);
            if (quote != null) {
                JOptionPane.showMessageDialog(this, "Fiyat teklifi oluşturuldu: " + quote.getQuoteId(), "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                loadUIData();
            } else {
                JOptionPane.showMessageDialog(this, "Fiyat teklifi oluşturulamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Fiyat için geçerli bir sayı girin.", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Geçerlilik tarihi formatı hatalı (yyyy-AA-gg).", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadAllQuotesToTableAction() {
        List<Quote> quotes = quoteService.getAllQuotes(); quoteTableModel.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Quote q : quotes) {
            quoteTableModel.addRow(new Object[]{
                q.getQuoteId(),
                q.getCustomerIdFk(),
                q.getVehicleChassisNumberFk() != null ? q.getVehicleChassisNumberFk() : "N/A",
                q.getQuoteDate().format(dateTimeFormatter),
                q.getPrice(),
                q.getValidUntil().format(dateFormatter)
            });
        }
    }

    private JTabbedPane createReportPanel() {
        JTabbedPane reportTabs = new JTabbedPane();
        JPanel salesReportTabPanel = new JPanel(new BorderLayout(5,5));
        areaSalesReport = new JTextArea(15, 70);
        areaSalesReport.setEditable(false);
        areaSalesReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JButton btnGenSalesReport = new JButton("Satış Raporu Oluştur (Teslim Edilenler)");
        btnGenSalesReport.addActionListener(e -> generateSalesReportAction());
        salesReportTabPanel.add(btnGenSalesReport, BorderLayout.NORTH);
        salesReportTabPanel.add(new JScrollPane(areaSalesReport), BorderLayout.CENTER);
        reportTabs.addTab("Genel Satış Raporu", salesReportTabPanel);
        JPanel forecastTabPanel = new JPanel(new BorderLayout(5,5));
        JPanel forecastInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        forecastInputPanel.add(new JLabel("Geçmiş Satış Periyot Sayısı (n):"));
        txtForecastPeriods = new JTextField("3", 5);
        forecastInputPanel.add(txtForecastPeriods);
        JButton btnGenForecast = new JButton("Tahmin Oluştur (Hareketli Ortalama)");
        btnGenForecast.addActionListener(e -> generateSalesForecastAction());
        forecastInputPanel.add(btnGenForecast);
        areaSalesForecast = new JTextArea(8, 70);
        areaSalesForecast.setEditable(false);
        areaSalesForecast.setFont(new Font("Monospaced", Font.PLAIN, 12));
        forecastTabPanel.add(forecastInputPanel, BorderLayout.NORTH);
        forecastTabPanel.add(new JScrollPane(areaSalesForecast), BorderLayout.CENTER);
        reportTabs.addTab("Satış Tahmini", forecastTabPanel);
        JPanel customerInteractionTabPanel = new JPanel(new BorderLayout(5,5));
        JPanel customerReportInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerReportInputPanel.add(new JLabel("Müşteri Seç:"));
        cmbCustomerReport = new JComboBox<>();
        customerReportInputPanel.add(cmbCustomerReport);
        JButton btnGenCustomerReport = new JButton("Rapor Oluştur");
        btnGenCustomerReport.addActionListener(e -> generateCustomerInteractionReportAction());
        customerReportInputPanel.add(btnGenCustomerReport);
        areaCustomerInteractionReport = new JTextArea(15, 70);
        areaCustomerInteractionReport.setEditable(false);
        areaCustomerInteractionReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        customerInteractionTabPanel.add(customerReportInputPanel, BorderLayout.NORTH);
        customerInteractionTabPanel.add(new JScrollPane(areaCustomerInteractionReport), BorderLayout.CENTER);
        reportTabs.addTab("Müşteri Etkileşimleri", customerInteractionTabPanel);
        JPanel pieChartTabPanel = new JPanel(new BorderLayout(5,5));
        pnlSalesPieChart = new JPanel(new BorderLayout());
        pnlSalesPieChart.setPreferredSize(new Dimension(400, 400));
        JButton btnRefreshPieChart = new JButton("Grafiği Yenile (Model Bazlı)");
        btnRefreshPieChart.addActionListener(e -> drawSalesPieChartAction());
        pieChartTabPanel.add(btnRefreshPieChart, BorderLayout.NORTH);
        pieChartTabPanel.add(pnlSalesPieChart, BorderLayout.CENTER);
        reportTabs.addTab("Model Satış Grafiği", pieChartTabPanel);
        JPanel detailedVehicleReportPanel = new JPanel(new BorderLayout(10, 10));
        detailedVehicleSalesReportTableModel = new DefaultTableModel(
            new String[]{"Marka", "Model", "Yıl", "Paket", "Satılan Adet", "Satış Yüzdesi (%)"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblDetailedSalesReport = new JTable(detailedVehicleSalesReportTableModel);
        JScrollPane tableScrollPane = new JScrollPane(tblDetailedSalesReport);
        pnlDetailedSalesPieChartContainer = new JPanel(new BorderLayout());
        pnlDetailedSalesPieChartContainer.setPreferredSize(new Dimension(400, 350));
        JButton btnGenerateDetailedReport = new JButton("Detaylı Satış Raporunu Oluştur/Yenile");
        btnGenerateDetailedReport.addActionListener(e -> generateDetailedVehicleSalesReportAction());
        detailedVehicleReportPanel.add(btnGenerateDetailedReport, BorderLayout.NORTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, pnlDetailedSalesPieChartContainer);
        splitPane.setResizeWeight(0.6);
        detailedVehicleReportPanel.add(splitPane, BorderLayout.CENTER);
        reportTabs.addTab("Detaylı Satış Raporu (Yıl/Paket)", detailedVehicleReportPanel);
        return reportTabs;
    }

    private void generateDetailedVehicleSalesReportAction() {
        if (reportService == null) {
            JOptionPane.showMessageDialog(this, "Rapor servisi başlatılamamış.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Map<String, Object>> reportData = reportService.generateDetailedVehicleSalesReport();
        detailedVehicleSalesReportTableModel.setRowCount(0);
        Map<String, Double> pieChartData = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.##");
        if (reportData.isEmpty()) {
            detailedVehicleSalesReportTableModel.addRow(new Object[]{"Veri yok", "", "", "", "", ""});
            pnlDetailedSalesPieChartContainer.removeAll();
            pnlDetailedSalesPieChartContainer.add(new JLabel("Pasta grafik için satış verisi bulunmuyor.", SwingConstants.CENTER), BorderLayout.CENTER);
            pnlDetailedSalesPieChartContainer.revalidate();
            pnlDetailedSalesPieChartContainer.repaint();
            return;
        }
        for (Map<String, Object> row : reportData) {
            String brand = (String) row.get("brand");
            String model = (String) row.get("model");
            int year = (Integer) row.get("year");
            String carPackage = (String) row.get("carPackage");
            long quantitySold = (Long) row.get("quantitySold");
            double salesPercentage = (Double) row.get("salesPercentage");
            detailedVehicleSalesReportTableModel.addRow(new Object[]{
                brand, model, year, carPackage, quantitySold, df.format(salesPercentage) + "%"
            });
            String pieKey = brand + " " + model + " " + year + " " + (carPackage != null && !carPackage.isEmpty() ? carPackage : "N/A");
            pieChartData.put(pieKey, salesPercentage);
        }
        pnlDetailedSalesPieChartContainer.removeAll();
        if (!pieChartData.isEmpty()) {
            PieChartPanel detailedPiePanel = new PieChartPanel(pieChartData);
            pnlDetailedSalesPieChartContainer.add(detailedPiePanel, BorderLayout.CENTER);
        } else {
            pnlDetailedSalesPieChartContainer.add(new JLabel("Pasta grafik için veri yok.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        pnlDetailedSalesPieChartContainer.revalidate();
        pnlDetailedSalesPieChartContainer.repaint();
    }

    private void generateSalesReportAction() {
        List<Map<String, Object>> report = reportService.generateSalesReport(new HashMap<>());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-20s %-15s %-15s %-18s %-15s\n", "Sipariş ID", "Müşteri Adı", "Marka", "Model", "Sipariş Tarihi", "Fiyat"));
        sb.append("-------------------------------------------------------------------------------------------------------------------\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (report.isEmpty()) {
            sb.append("Gösterilecek teslim edilmiş satış raporu bulunmamaktadır.\n");
        } else {
            for (Map<String, Object> row : report) {
                LocalDateTime date = (LocalDateTime) row.get("orderDate");
                sb.append(String.format("%-12s %-20.20s %-15.15s %-15.15s %-18s %-15.2f\n",
                        row.get("orderId"), row.get("customerName"), row.get("brand"),
                        row.get("model"), date != null ? date.format(formatter) : "N/A", row.get("totalPrice")));
            }
        }
        areaSalesReport.setText(sb.toString());
        areaSalesReport.setCaretPosition(0);
    }
    private void generateSalesForecastAction() {
        try {
            int n = Integer.parseInt(txtForecastPeriods.getText().trim());
            if (n <= 0) { areaSalesForecast.setText("Periyot sayısı pozitif olmalıdır."); return; }
            List<Double> pastSalesData = orderService.getAllOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .map(Order::getTotalPrice)
                .collect(Collectors.toList());
             if(pastSalesData.size() < n) {
                 areaSalesForecast.setText("Tahmin için yeterli geçmiş satış verisi bulunmuyor (" + pastSalesData.size() + " adet 'Teslim Edildi' siparişi bulundu, en az " + n + " gerekli).");
                 return;
             }
            double forecast = reportService.generateSalesForecast(pastSalesData, n);
            areaSalesForecast.setText("Son " + n + " 'Teslim Edilmiş' satışların tutarları (en yeniden en eskiye):\n");
            for(int i=0; i < n; i++){
                areaSalesForecast.append(String.format("  %.2f", pastSalesData.get(i)) + (i < n -1 ? ",\n" : "\n"));
            }
            areaSalesForecast.append("\nGelecek dönem için tahmini satış (tutar): " + String.format("%.2f", forecast));
        } catch (NumberFormatException ex) {
            areaSalesForecast.setText("Periyot sayısı için geçerli bir sayı girin.");
        }
        areaSalesForecast.setCaretPosition(0);
    }
    private void generateCustomerInteractionReportAction() {
        Customer selectedCustomer = (Customer) cmbCustomerReport.getSelectedItem();
        if (selectedCustomer == null) { areaCustomerInteractionReport.setText("Lütfen bir müşteri seçin."); return; }
        List<InteractionEvent> interactions = reportService.generateCustomerInteractionReport(selectedCustomer.getCustomerId(), new HashMap<>(), this.customerService);
        StringBuilder sb = new StringBuilder("Müşteri: " + selectedCustomer.getName() + " (" + selectedCustomer.getCustomerId() + ")\n");
        sb.append("--------------------------------------------------------------------------------------------------\n");
        if (interactions.isEmpty()) {
            sb.append("Bu müşteri için etkileşim kaydı bulunmamaktadır.\n");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (InteractionEvent event : interactions) {
                sb.append(String.format("ID: %-10s Tür: %-25s Tarih: %-16s", event.getEventId(), event.getEventType().getDisplayName(), event.getEventDate().format(formatter)));
                if (event.getVehicleChassisNumberFk() != null) {
                    sb.append(String.format(" Araç: %-15s", event.getVehicleChassisNumberFk()));
                }
                sb.append(String.format("\n   Detay: %s\n", event.getDetails()));
                sb.append("--------------------------------------------------------------------------------------------------\n");
            }
        }
        areaCustomerInteractionReport.setText(sb.toString());
        areaCustomerInteractionReport.setCaretPosition(0);
    }
    private void drawSalesPieChartAction() {
        Map<String, Double> salesData = reportService.getSalesPercentageByModel(this.stockService);
        pnlSalesPieChart.removeAll();
        if (salesData.isEmpty()) {
            pnlSalesPieChart.add(new JLabel("Pasta grafik için 'Teslim Edilmiş' satış verisi bulunmuyor.", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            pnlSalesPieChart.add(new PieChartPanel(salesData), BorderLayout.CENTER);
        }
        pnlSalesPieChart.revalidate();
        pnlSalesPieChart.repaint();
    }

    private void loadUIData() {
        try {
            List<Vehicle> allVehicles = stockService.getAllVehicles();
            List<Vehicle> availableForOrderAndTestDrive = allVehicles.stream()
                .filter(v -> v.getStatus() == VehicleStatus.IN_STOCK || v.getStatus() == VehicleStatus.IN_SHOWROOM_DISPLAY || v.getStatus() == VehicleStatus.IN_SHOWROOM_ORDER)
                .collect(Collectors.toList());
            updateComboBoxModel(cmbVehiclesForStatusUpdateLocal, allVehicles, true);
            List<Vehicle> vehiclesForQuote = new ArrayList<>();
            vehiclesForQuote.add(new Vehicle("NO_VEHICLE_ID", "", "Araç Yok", 0, "", 0.0));
            vehiclesForQuote.addAll(availableForOrderAndTestDrive);
            updateComboBoxModel(cmbOrderVehicle, availableForOrderAndTestDrive, true);
            updateComboBoxModel(cmbTestDriveVehicle, availableForOrderAndTestDrive, true);
            updateComboBoxModel(cmbQuoteVehicle, vehiclesForQuote, true);
            List<Customer> customers = customerService.getAllCustomers();
            updateComboBoxModel(cmbOrderCustomer, customers, true);
            updateComboBoxModel(cmbTestDriveCustomer, customers, true);
            updateComboBoxModel(cmbQuoteCustomer, customers, true);
            updateComboBoxModel(cmbCustomerReport, customers, true);
            List<Order> orders = orderService.getAllOrders();
            updateComboBoxModel(cmbUpdateOrderStatusOrder, orders, false);
            List<TestDrive> testDrives = testDriveService.getAllTestDrives();
            updateComboBoxModel(cmbUpdateTestDriveStatusTestDrive, testDrives, false);
            if (tblStockQueryResults != null) queryStockAction();
            if (tblCustomerQueryResults != null) loadAllCustomersToTableAction();
            if (tblOrders != null) loadAllOrdersToTableAction();
            if (tblTestDrives != null) loadAllTestDrivesToTableAction();
            if (tblQuotes != null) loadAllQuotesToTableAction();
            if (pnlSalesPieChart != null && reportService != null) drawSalesPieChartAction();
            if (tblDetailedSalesReport != null) generateDetailedVehicleSalesReportAction();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Arayüz verileri yüklenirken bir hata oluştu: " + e.getMessage(), "Veri Yükleme Hatası", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private <T> void updateComboBoxModel(JComboBox<T> comboBox, List<T> items, boolean resetSelection) {
        if (comboBox == null) return;
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) comboBox.getModel();
        T selectedItem = null;
        if (!resetSelection) {
            selectedItem = (T) comboBox.getSelectedItem();
        }
        model.removeAllElements();
        for (T item : items) {
            model.addElement(item);
        }
        if (selectedItem != null) {
            boolean found = false;
            for (int i = 0; i < model.getSize(); i++) {
                if (selectedItem.equals(model.getElementAt(i))) {
                    comboBox.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
             if (!found && model.getSize() > 0 && resetSelection) {
                comboBox.setSelectedIndex(0);
            }
        } else if (model.getSize() > 0 && resetSelection) {
            comboBox.setSelectedIndex(0);
        }
    }
}
