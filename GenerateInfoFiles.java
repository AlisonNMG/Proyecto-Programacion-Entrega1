import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GenerateInfoFiles {

    // Test data for generating random names and products
    static String[] firstNames = {"Jose", "Roberto", "Juancho", "Antonia", "Samuel", "Camila", "Valentino", "Sofia", "Federico", "Isabella"};
    static String[] lastNames = {"Gomez", "Rodriguez", "Perez", "Sanchez", "Garcia", "Lopez", "Martinez", "Diaz", "Fernandez", "Gonzalez"};
    static String[] products = {"Laptop", "Mouse", "Keyboard", "Monitor", "Webcam", "Printer", "Hard Drive", "USB Cable", "Headphones", "Speakers"};

    public static void main(String[] args) throws IOException {
        generateRequiredFiles(10, 10);
    }

    public static void generateRequiredFiles(int productCount, int salesmenCount) throws IOException {
        createProductsFile(productCount);
        createSalesMenInfoFile(salesmenCount);
        createFinalReports();
    }

    /**
     * Generates the master products file: ProductID;ProductName;Price
     */
    public static void createProductsFile(int productCount) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"))) {
            int max = Math.min(productCount, products.length);
            for (int i = 1; i <= max; i++) {
                writer.write(i + ";" + products[i - 1] + ";" + (100 * i));
                writer.newLine();
            }
        }
    }

    /**
     * Generates the salesmen master file and calls the sales file creation for each
     */
    public static void createSalesMenInfoFile(int salesmenCount) throws IOException {
        Random rand = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("salesmen_info.txt"))) {
            for (int i = 1; i <= salesmenCount; i++) {
                String docType = "CC";
                long docNumber = 1000000 + rand.nextInt(9000000);
                String firstName = firstNames[rand.nextInt(firstNames.length)];
                String lastName = lastNames[rand.nextInt(lastNames.length)];

                writer.write(docType + ";" + docNumber + ";" + firstName + ";" + lastName);
                writer.newLine();

                createVendorSalesFile(docType, docNumber, firstName, lastName);
            }
        }
    }

    /**
     * Creates a flat file for each salesman: sales_CC_12345.txt
     */
    public static void createVendorSalesFile(String docType, long docNumber, String firstName, String lastName) throws IOException {
        Random rand = new Random();
        String fileName = "sales_" + docType + "_" + docNumber + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(docType + ";" + docNumber + ";" + firstName + ";" + lastName);
            writer.newLine();

            int salesLines = rand.nextInt(5) + 1;
            List<int[]> salesData = new ArrayList<>();

            // Generamos los datos primero para poder ordenarlos
            for (int i = 0; i < salesLines; i++) {
                int productId = rand.nextInt(5) + 1;
                int quantity = rand.nextInt(10) + 1;
                salesData.add(new int[]{productId, quantity});
            }

            // Ordenamos de mayor a menor basado en la cantidad (índice 1)
            salesData.sort((a, b) -> Integer.compare(b[1], a[1]));

            for (int[] sale : salesData) {
                writer.write(sale[0] + ";" + sale[1] + ";");
                writer.newLine();
            }
        }
    }

    /**
     * Processes all files to generate the two requested reports.
     */
    public static void createFinalReports() throws IOException {
        Map<Integer, String[]> productCatalog = new HashMap<>(); // ID -> [Name, Price]
        Map<String, Integer> productQuantities = new HashMap<>(); // Name -> TotalQuantity
        Map<String, Double> salesmanTotals = new HashMap<>(); // Name -> TotalMoney

        // 1. Read product catalog
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                productCatalog.put(Integer.parseInt(parts[0]), new String[]{parts[1], parts[2]});
            }
        }

        // 2. Read salesmen and their specific sales files
        try (BufferedReader reader = new BufferedReader(new FileReader("salesmen_info.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String fullName = parts[2] + " " + parts[3];
                String salesFile = "sales_" + parts[0] + "_" + parts[1] + ".txt";
                double totalSalesmanMoney = 0;

                try (BufferedReader sReader = new BufferedReader(new FileReader(salesFile))) {
                    sReader.readLine(); // Skip header
                    String sLine;
                    while ((sLine = sReader.readLine()) != null) {
                        String[] sParts = sLine.split(";");
                        int pId = Integer.parseInt(sParts[0]);
                        int qty = Integer.parseInt(sParts[1]);
                        
                        String[] prodInfo = productCatalog.get(pId);
                        if (prodInfo != null) {
                            double price = Double.parseDouble(prodInfo[1]);
                            totalSalesmanMoney += (price * qty);
                            productQuantities.put(prodInfo[0], productQuantities.getOrDefault(prodInfo[0], 0) + qty);
                        }
                    }
                } catch (IOException e) { /* File might not exist or be empty */ }
                salesmanTotals.put(fullName, totalSalesmanMoney);
            }
        }

        // 3. Generate Salesmen Report (Sorted by money DESC)
        List<Map.Entry<String, Double>> sortedSalesmen = new ArrayList<>(salesmanTotals.entrySet());
        sortedSalesmen.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_vendedores.csv"))) {
            for (Map.Entry<String, Double> entry : sortedSalesmen) {
                writer.write(entry.getKey() + ";" + entry.getValue());
                writer.newLine();
            }
        }

        // 4. Generate Products Report (Sorted by quantity DESC)
        List<String[]> productReportData = new ArrayList<>();
        for (Map.Entry<Integer, String[]> entry : productCatalog.entrySet()) {
            String name = entry.getValue()[0];
            String price = entry.getValue()[1];
            int qty = productQuantities.getOrDefault(name, 0);
            productReportData.add(new String[]{name, price, String.valueOf(qty)});
        }
        
        // Sort by quantity (index 2) DESC
        productReportData.sort((a, b) -> Integer.compare(Integer.parseInt(b[2]), Integer.parseInt(a[2])));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_productos.csv"))) {
            for (String[] data : productReportData) {
                // Requerimiento: Nombre y Precio
                writer.write(data[0] + ";" + data[1]);
                writer.newLine();
            }
        }
    }
}