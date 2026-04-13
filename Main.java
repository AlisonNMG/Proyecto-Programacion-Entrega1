public class Main {
    public static void main(String[] args) {
        try {
            // When executed, generates the required report files
            // (products, salesmen, and sales files per salesman).
            GenerateInfoFiles.generateRequiredFiles(10, 10);
            System.out.println("Success: files generated correctly.");
        } catch (Exception e) {
            System.err.println("Error: files could not be generated. Details: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}