package com.dealership.db;

import com.dealership.model.*;
import com.dealership.service.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/dealership_db";
    private static final String USER = "postgres";
    private static final String PASS = "1234"; // Kendi şifrenizi yazın

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Sürücüsü bulunamadı!");
            e.printStackTrace();
            throw new SQLException("PostgreSQL JDBC Sürücüsü bulunamadı!", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void initializeSchema() {
        String createVehiclesTable = "CREATE TABLE IF NOT EXISTS Vehicles ("
                + "chassisNumber VARCHAR(50) PRIMARY KEY,"
                + "brand VARCHAR(50),"
                + "model VARCHAR(50),"
                + "year INT,"
                + "carPackage VARCHAR(50),"
                + "status VARCHAR(30),"
                + "price DECIMAL(10, 2)"
                + ");";

        String createCustomersTable = "CREATE TABLE IF NOT EXISTS Customers ("
                + "customerId VARCHAR(50) PRIMARY KEY,"
                + "name VARCHAR(100),"
                + "contactInfo VARCHAR(100)"
                + ");";

        String createInteractionEventsTable = "CREATE TABLE IF NOT EXISTS InteractionEvents ("
                + "eventId VARCHAR(50) PRIMARY KEY,"
                + "customerIdFk VARCHAR(50) REFERENCES Customers(customerId) ON DELETE CASCADE,"
                + "eventType VARCHAR(50),"
                + "eventDate TIMESTAMP,"
                + "vehicleChassisNumberFk VARCHAR(50) NULL REFERENCES Vehicles(chassisNumber) ON DELETE SET NULL,"
                + "details TEXT"
                + ");";

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS Orders ("
                + "orderId VARCHAR(50) PRIMARY KEY,"
                + "customerIdFk VARCHAR(50) REFERENCES Customers(customerId) ON DELETE SET NULL,"
                + "vehicleChassisNumberFk VARCHAR(50) REFERENCES Vehicles(chassisNumber) ON DELETE RESTRICT,"
                + "orderDate TIMESTAMP,"
                + "totalPrice DECIMAL(12, 2),"
                + "status VARCHAR(30)"
                + ");";

        String createTestDrivesTable = "CREATE TABLE IF NOT EXISTS TestDrives ("
                + "testDriveId VARCHAR(50) PRIMARY KEY,"
                + "customerIdFk VARCHAR(50) REFERENCES Customers(customerId) ON DELETE CASCADE,"
                + "vehicleChassisNumberFk VARCHAR(50) REFERENCES Vehicles(chassisNumber) ON DELETE CASCADE,"
                + "scheduledDateTime TIMESTAMP,"
                + "status VARCHAR(30),"
                + "durationMinutes INT DEFAULT 60"
                + ");";

        String createQuotesTable = "CREATE TABLE IF NOT EXISTS Quotes ("
                + "quoteId VARCHAR(50) PRIMARY KEY,"
                + "customerIdFk VARCHAR(50) REFERENCES Customers(customerId) ON DELETE CASCADE,"
                + "vehicleChassisNumberFk VARCHAR(50) NULL REFERENCES Vehicles(chassisNumber) ON DELETE CASCADE,"
                + "quoteDate TIMESTAMP,"
                + "price DECIMAL(12, 2),"
                + "validUntil DATE"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createVehiclesTable);
            stmt.execute(createCustomersTable);
            stmt.execute(createInteractionEventsTable);
            stmt.execute(createOrdersTable);
            stmt.execute(createTestDrivesTable);
            stmt.execute(createQuotesTable);
            System.out.println("Veritabanı şeması başarıyla başlatıldı/güncellendi.");
        } catch (SQLException e) {
            System.err.println("Veritabanı şeması başlatılırken hata oluştu.");
            e.printStackTrace();
        }
    }

    public static void clearDatabase() {
        String[] tablesToDrop = {
            "InteractionEvents", "Orders", "TestDrives", "Quotes",
            "Vehicles", "Customers"
        };
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String tableName : tablesToDrop) {
                System.out.println(tableName + " tablosu siliniyor (DROP TABLE IF EXISTS)...");
                stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName + " CASCADE");
            }
            System.out.println("Veritabanındaki tüm tablolar başarıyla silindi.");
        } catch (SQLException e) {
            System.err.println("Veritabanı temizlenirken (DROP TABLE) hata oluştu: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı temizlenirken bir hata oluştu:\n" + e.getMessage(), "Veritabanı Sıfırlama Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean isDatabaseEmpty() {
        String checkCustomersSql = "SELECT COUNT(*) FROM Customers";
        String checkVehiclesSql = "SELECT COUNT(*) FROM Vehicles";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            int customerCount = 0;
            try (ResultSet rsCustomers = stmt.executeQuery(checkCustomersSql)) {
                if (rsCustomers.next()) customerCount = rsCustomers.getInt(1);
            } catch (SQLException e) {
                if (e.getMessage().toLowerCase().contains("does not exist")) return true;
                throw e;
            }
            int vehicleCount = 0;
            try (ResultSet rsVehicles = stmt.executeQuery(checkVehiclesSql)) {
                 if (rsVehicles.next()) vehicleCount = rsVehicles.getInt(1);
            } catch (SQLException e) {
                 if (e.getMessage().toLowerCase().contains("does not exist")) return customerCount == 0;
                throw e;
            }
            return customerCount == 0 && vehicleCount == 0;
        } catch (SQLException e) {
            System.err.println("Veritabanı boşluk durumu kontrol edilirken hata: " + e.getMessage());
            if (e.getMessage().toLowerCase().contains("relation") && e.getMessage().toLowerCase().contains("does not exist")) return true;
            return false;
        }
    }

    public static void populateWithSampleData(StockService stockService, CustomerService customerService, OrderService orderService) {
        System.out.println("Veritabanı boş veya yeni oluşturuldu. Örnek veriler ekleniyor...");
        Random random = new Random();
        String[] brands = {"Toyota", "Ford", "Honda", "Volkswagen", "BMW", "Mercedes", "Audi", "Hyundai", "Kia", "Nissan", "Renault"};
        String[] toyotaModels = {"Corolla", "Camry", "RAV4", "Yaris", "Hilux"};
        String[] fordModels = {"Focus", "Fiesta", "Kuga", "Ranger", "Mustang"};
        String[] hondaModels = {"Civic", "Accord", "CR-V", "Jazz"};
        String[] vwModels = {"Golf", "Polo", "Passat", "Tiguan", "T-Roc"};
        String[] bmwModels = {"3 Serisi", "5 Serisi", "X3", "X5", "1 Serisi"};
        String[] mercedesModels = {"C Serisi", "E Serisi", "GLC", "A Serisi"};
        String[] audiModels = {"A3", "A4", "Q3", "Q5"};
        String[] hyundaiModels = {"i20", "i30", "Tucson", "Kona"};
        String[] kiaModels = {"Rio", "Ceed", "Sportage", "Stonic"};
        String[] nissanModels = {"Micra", "Qashqai", "Juke", "Navara"};
        String[] renaultModels = {"Clio", "Megane", "Captur", "Kadjar", "Talisman", "12"};

        Map<String, String[]> modelMap = new HashMap<>();
        modelMap.put("Toyota", toyotaModels); modelMap.put("Ford", fordModels); modelMap.put("Honda", hondaModels);
        modelMap.put("Volkswagen", vwModels); modelMap.put("BMW", bmwModels); modelMap.put("Mercedes", mercedesModels);
        modelMap.put("Audi", audiModels); modelMap.put("Hyundai", hyundaiModels); modelMap.put("Kia", kiaModels);
        modelMap.put("Nissan", nissanModels); modelMap.put("Renault", renaultModels);

        String[] packages = {"Comfort", "Style", "Premium", "Sport", "Luxury", "Business", "Broadway", "Toros"};

        for (int i = 0; i < 40; i++) {
            String brand = brands[random.nextInt(brands.length)];
            String[] availableModels = modelMap.get(brand);
            String model = availableModels[random.nextInt(availableModels.length)];
            int year = 2015 + random.nextInt(10);
            String carPackage = packages[random.nextInt(packages.length)];
            if ("Renault".equals(brand) && "12".equals(model)) {
                year = 1990 + random.nextInt(10);
                carPackage = random.nextBoolean() ? "Broadway" : "Toros";
            }
            double price = 300000 + random.nextInt(3700000);
            String chassisNumber = "CHASSIS-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
            stockService.addVehicleToStock(new Vehicle(chassisNumber, brand, model, year, carPackage, price));
        }
        System.out.println("40 örnek araç eklendi.");

        String[] firstNames = {"Ahmet", "Ayşe", "Mehmet", "Fatma", "Mustafa", "Zeynep", "Ali", "Elif", "Hasan", "Emine", "Can", "Deniz", "Burak", "Selin"};
        String[] lastNames = {"Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Öztürk", "Aydın", "Arslan", "Doğan", "Polat", "Kılıç", "Kurt", "Bulut", "Güneş"};

        for (int i = 0; i < 15; i++) {
            String name = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
            String contactInfo = "05" + (random.nextInt(899999999) + 100000000);
            String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Customer newCustomer = new Customer(customerId, name, contactInfo);
            if (customerService.registerCustomer(newCustomer)) {
                String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                InteractionEvent initialVisit = new InteractionEvent(
                        eventId, customerId, InteractionEventType.INITIAL_VISIT,
                        LocalDateTime.now().minusDays(random.nextInt(60)), null,
                        "Örnek veri ile otomatik oluşturulan ilk ziyaret."
                );
                customerService.addInteractionEvent(initialVisit);
            }
        }
        System.out.println("15 örnek müşteri ve ilk ziyaret etkileşimleri eklendi.");

        try {
            List<Customer> customers = customerService.getAllCustomers();
            // Örnek satışlar için tüm araçları değil, sadece uygun durumda olanları almayı deneyebiliriz
            // Ancak populateWithSampleData çağrıldığında stockService üzerinden queryStock kullanmak daha doğru
            // List<Vehicle> vehicles = stockService.getAllVehicles().stream()
            //                         .filter(v -> v.getStatus() == VehicleStatus.IN_STOCK || v.getStatus() == VehicleStatus.IN_SHOWROOM_ORDER)
            //                         .collect(Collectors.toList());

            if (!customers.isEmpty()) { // Sadece müşteri varlığı yeterli, araçlar dinamik sorgulanacak
                int salesToCreate = Math.min(15, customers.size()); // En fazla müşteri sayısı kadar veya 15
                System.out.println(salesToCreate + " adet örnek satış oluşturuluyor...");
                for (int i = 0; i < salesToCreate; i++) {
                    Customer c = customers.get(random.nextInt(customers.size()));

                    List<Vehicle> availableVehicles = stockService.queryStock(null, null, null, null).stream()
                        .filter(veh -> veh.getStatus() == VehicleStatus.IN_STOCK || veh.getStatus() == VehicleStatus.IN_SHOWROOM_ORDER || veh.getStatus() == VehicleStatus.IN_SHOWROOM_DISPLAY)
                        .collect(Collectors.toList());

                    if(availableVehicles.isEmpty()) {
                        System.out.println("Örnek satış için uygun araç kalmadı.");
                        break; // Uygun araç yoksa döngüden çık
                    }

                    Vehicle v = availableVehicles.get(random.nextInt(availableVehicles.size()));

                    Order placedOrder = orderService.placeOrder(c.getCustomerId(), v.getChassisNumber(), v.getPrice() * (0.9 + random.nextDouble() * 0.2));
                    if (placedOrder != null) {
                        System.out.println("Örnek sipariş (Verildi): " + placedOrder.getOrderId() + " Müşteri: " + c.getName() + " Araç: " + v.getBrand() + " " + v.getModel());
                        if (random.nextBoolean()) {
                            orderService.updateOrderStatus(placedOrder.getOrderId(), OrderStatus.PAID);
                            if (random.nextBoolean()) {
                                orderService.updateOrderStatus(placedOrder.getOrderId(), OrderStatus.DELIVERY_SCHEDULED);
                                if (random.nextBoolean()) {
                                    orderService.updateOrderStatus(placedOrder.getOrderId(), OrderStatus.DELIVERED);
                                    System.out.println("Örnek satış (Teslim Edildi): " + placedOrder.getOrderId() + " Araç: " + v.getBrand() + " " + v.getModel());
                                }
                            }
                        }
                    } else {
                         System.out.println("Örnek sipariş oluşturulamadı: " + c.getName() + " için araç " + v.getBrand() + " " + v.getModel());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Örnek satış verileri ve etkileşimleri oluşturulurken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
