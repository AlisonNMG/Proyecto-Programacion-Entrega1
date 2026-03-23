import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    // Test data for generating random names and products
    static String[] firstNames = {"Jose", "Roberto", "Juancho", "Antonia", "Samuel", "Camila", "Valentino"};
    static String[] lastNames = {"Gomez", "Rodriguez", "Perez", "Sanchez", "Garcia", "Lopez", "Martinez"};
    static String[] products = {"Laptop", "Mouse", "Keyboard", "Monitor", "Webcam", "Printer", "Hard Drive"};

    public static void main(String[] args) {
        // 1. Create the master products file
        createProductsFile(6);
        
        // 2. Create the salesmen information file
        // This also triggers the creation of individual sales files for each salesman
        createSalesMenInfoFile(6);
        
        System.out.println("Files generated successfully.");
    }

    /**
     * Generates the master products file: ProductID;ProductName;Price
     */
    public static void createProductsFile(int productCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"))) {
            for (int i = 1; i <= productCount; i++) {
                // Format: ProductID;ProductName;Price
                writer.write(i + ";" + products[i - 1] + ";" + (100 * i));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the salesmen master file and calls the sales file creation for each
     */
    public static void createSalesMenInfoFile(int salesmenCount) {
        Random rand = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("salesmen_info.txt"))) {
            for (int i = 1; i <= salesmenCount; i++) {
                String docType = "CC";
                long docNumber = 1000000 + rand.nextInt(9000000);
                String firstName = firstNames[rand.nextInt(firstNames.length)];
                String lastName = lastNames[rand.nextInt(lastNames.length)];

                // Write to the master salesmen info file
                writer.write(docType + ";" + docNumber + ";" + firstName + ";" + lastName);
                writer.newLine();

                // Create the individual sales file for THIS salesman
                createVendorSalesFile(docType, docNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a flat file for each salesman: sales_CC_12345.txt
     */
    public static void createVendorSalesFile(String docType, long docNumber) {
        Random rand = new Random();
        String fileName = "sales_" + docType + "_" + docNumber + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // First line: DocumentType;DocumentNumber
            writer.write(docType + ";" + docNumber);
            writer.newLine();

            // Generate between 1 and 5 random sales lines
            int salesLines = rand.nextInt(5) + 1;
            for (int i = 0; i < salesLines; i++) {
                int productId = rand.nextInt(5) + 1;
                int quantity = rand.nextInt(10) + 1;
                // Format: ProductID;QuantitySold;
                writer.write(productId + ";" + quantity + ";");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}