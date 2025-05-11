package com.dealership;

import com.dealership.db.DatabaseUtil;
import com.dealership.service.*;
import com.dealership.service.impl.*;
import com.dealership.ui.DealershipGUI;
import com.dealership.ui.LoginDialog;

import javax.swing.*;

public class DealershipApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (!loginDialog.isAuthenticated()) {
                System.out.println("Giriş başarısız. Uygulama sonlandırılıyor.");
                System.exit(0);
            }

            System.out.println("Giriş başarılı. Uygulama başlatılıyor...");

            try {
                 DatabaseUtil.initializeSchema();
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(null, "Veritabanı bağlantısı kurulamadı veya şema başlatılamadı.\nLütfen veritabanı sunucusunun çalıştığından ve bağlantı ayarlarının doğru olduğundan emin olun.\n\nHata: " + e.getMessage(), "Veritabanı Hatası", JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
                 System.exit(1);
            }

            StockService stockService = new PostgresStockService();
            CustomerService customerService = new PostgresCustomerService();
            OrderService orderService = new PostgresOrderService(stockService, customerService);
            TestDriveService testDriveService = new PostgresTestDriveService(customerService, stockService);
            QuoteService quoteService = new PostgresQuoteService(customerService);
            ReportService reportService = new PostgresReportService(orderService);

            if (DatabaseUtil.isDatabaseEmpty()) {
                DatabaseUtil.populateWithSampleData(stockService, customerService, orderService);
            }

            DealershipGUI gui = new DealershipGUI(stockService, customerService, orderService, testDriveService, quoteService, reportService);
            gui.setVisible(true);
        });
    }
}
