import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

                createVendorSalesFile(docType, docNumber);
            }
        }
    }

    /**
     * Creates a flat file for each salesman: sales_CC_12345.txt
     */
    public static void createVendorSalesFile(String docType, long docNumber) throws IOException {
        Random rand = new Random();
        String fileName = "sales_" + docType + "_" + docNumber + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(docType + ";" + docNumber);
            writer.newLine();

            int salesLines = rand.nextInt(5) + 1;
            for (int i = 0; i < salesLines; i++) {
                int productId = rand.nextInt(5) + 1;
                int quantity = rand.nextInt(10) + 1;
                writer.write(productId + ";" + quantity + ";");
                writer.newLine();
            }
        }
    }
}