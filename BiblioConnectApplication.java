package biblioConnect_v3;

public class BiblioConnectApplication {
    public static void main(String[] args) {
        System.out.println("Starting BiblioConnect Library Management System...");
        
        try (LibraryManagementSystem system = new LibraryManagementImpl()) {
            try (LibraryUI ui = new LibraryUI(system)) {
                ui.start();
            }
        } catch (Exception e) {
            System.out.println("An error occurred while running BiblioConnect: " + e.getMessage());
            e.printStackTrace();
        }
    }
}