package biblioConnect_v3;

public class PhysicalBook extends Book {
    private String location;

    public PhysicalBook(String title, String author, String isbn, String location) {
        super(title, author, isbn, "Physical");
        this.location = location;
    }

    public String getLocation() { return location; }

    @Override
    public String getDetails() {
        return "Physical Book - Location: " + location;
    }
}