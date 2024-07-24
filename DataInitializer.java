package biblioConnect_v3;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataInitializer {
    private List<User> users = new ArrayList<>();
    private List<Book> books = new ArrayList<>();
    private Random random = new Random();

    public void initializeData() {
        try {
            clearBorrowingRecords();
            resetBookAvailability();
            createUsers();
            createBooks();
            saveToDatabase();
            createSampleBorrowings();
        } catch (SQLException e) {
            System.err.println("Error initializing data: " + e.getMessage());
        }
    }

    private void clearBorrowingRecords() throws SQLException {
        DatabaseConnection.executeUpdate("DELETE FROM BorrowingRecords");
        System.out.println("Cleared all borrowing records.");
    }

    private void resetBookAvailability() throws SQLException {
        DatabaseConnection.executeUpdate("UPDATE Books SET isAvailable = true");
        System.out.println("Reset availability of all books.");
    }

    private void createUsers() {
        users.add(new User("Aisha Patel", "aisha.patel@example.com", "aishap", "password", UserRole.STUDENT));
        users.add(new User("Carlos Rodriguez", "carlos.rodriguez@example.com", "carlosr", "password", UserRole.STUDENT));
        users.add(new User("Zhang Wei", "zhang.wei@example.com", "zhangw", "password", UserRole.STUDENT));
        users.add(new User("Oluwaseun Adebayo", "oluwaseun.adebayo@example.com", "oluwaseuna", "password", UserRole.STUDENT));
        users.add(new User("Emma Thompson", "emma.thompson@example.com", "emmat", "password", UserRole.STUDENT));
        users.add(new User("Hiroshi Tanaka", "hiroshi.tanaka@example.com", "hiroshit", "password", UserRole.STUDENT));
        users.add(new User("Fatima Al-Sayed", "fatima.alsayed@example.com", "fatimaa", "password", UserRole.STUDENT));
        users.add(new User("Raj Sharma", "raj.sharma@example.com", "rajs", "password", UserRole.STUDENT));
        users.add(new User("Dr. Maria Garcia", "maria.garcia@example.com", "mariag", "password", UserRole.FACULTY));
        users.add(new User("Prof. Kwame Nkrumah", "kwame.nkrumah@example.com", "kwamen", "password", UserRole.FACULTY));
        users.add(new User("Dr. Yuki Yamamoto", "yuki.yamamoto@example.com", "yukiy", "password", UserRole.FACULTY));
        users.add(new User("Prof. Anastasia Petrova", "anastasia.petrova@example.com", "anastasiap", "password", UserRole.FACULTY));
        users.add(new User("Sarah Johnson", "sarah.johnson@example.com", "sarahj", "password", UserRole.LIBRARIAN));
        users.add(new User("Miguel Hernandez", "miguel.hernandez@example.com", "miguelh", "password", UserRole.LIBRARIAN));
    }

    private void createBooks() {
        books.add(new PhysicalBook("To Kill a Mockingbird", "Harper Lee", "9780446310789", "Fiction A1"));
        books.add(new PhysicalBook("1984", "George Orwell", "9780451524935", "Fiction A2"));
        books.add(new PhysicalBook("Pride and Prejudice", "Jane Austen", "9780141439518", "Fiction A3"));
        books.add(new PhysicalBook("The Catcher in the Rye", "J.D. Salinger", "9780316769174", "Fiction A4"));
        books.add(new PhysicalBook("To the Lighthouse", "Virginia Woolf", "9780156907392", "Fiction A5"));
        books.add(new PhysicalBook("Things Fall Apart", "Chinua Achebe", "9780385474542", "Fiction B1"));
        books.add(new PhysicalBook("One Hundred Years of Solitude", "Gabriel García Márquez", "9780060883287", "Fiction B2"));
        books.add(new PhysicalBook("The God of Small Things", "Arundhati Roy", "9780812979657", "Fiction B3"));
        books.add(new PhysicalBook("Beloved", "Toni Morrison", "9781400033416", "Fiction B4"));
        books.add(new PhysicalBook("The Alchemist", "Paulo Coelho", "9780062315007", "Fiction B5"));
        books.add(new EBook("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "PDF", 2048, "https://example.com/greatgatsby.pdf"));
        books.add(new EBook("Brave New World", "Aldous Huxley", "9780060850524", "EPUB", 1536, "https://example.com/bravenewworld.epub"));
        books.add(new EBook("The Hobbit", "J.R.R. Tolkien", "9780547951973", "PDF", 3072, "https://example.com/hobbit.pdf"));
        books.add(new EBook("Dune", "Frank Herbert", "9780441172719", "EPUB", 2560, "https://example.com/dune.epub"));
        books.add(new EBook("Neuromancer", "William Gibson", "9780441569595", "PDF", 1792, "https://example.com/neuromancer.pdf"));
        books.add(new EBook("The Handmaid's Tale", "Margaret Atwood", "9780385490818", "EPUB", 1920, "https://example.com/handmaidstale.epub"));
        books.add(new EBook("The Color Purple", "Alice Walker", "9780151191543", "PDF", 1664, "https://example.com/colorpurple.pdf"));
        books.add(new AudioBook("Harry Potter and the Philosopher's Stone", "J.K. Rowling", "9780747532743", "MP3", 307200, "https://example.com/harrypotter1.mp3", "Stephen Fry", 480));
        books.add(new AudioBook("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "9780345391803", "MP3", 256000, "https://example.com/hitchhiker.mp3", "Stephen Moore", 360));
        books.add(new AudioBook("The Martian", "Andy Weir", "9780804139021", "MP3", 409600, "https://example.com/martian.mp3", "R.C. Bray", 600));
        books.add(new AudioBook("Becoming", "Michelle Obama", "9781524763138", "MP3", 512000, "https://example.com/becoming.mp3", "Michelle Obama", 720));
        books.add(new AudioBook("Born a Crime", "Trevor Noah", "9780399588174", "MP3", 358400, "https://example.com/bornacrime.mp3", "Trevor Noah", 540));
    }

    private void createSampleBorrowings() {
        try {
            List<User> dbUsers = DatabaseConnection.readAll(User.class);
            List<Book> dbBooks = DatabaseConnection.readAll(Book.class);
            int totalBorrowings = 0;

            for (User user : dbUsers) {
                if (random.nextBoolean()) {
                    int borrowLimit = user.getRole().getMaxBooksAllowed();
                    int borrowCount = random.nextInt(borrowLimit + 1);
                    System.out.println("User " + user.getUsername() + " attempting to borrow " + borrowCount + " books.");

                    for (int i = 0; i < borrowCount; i++) {
                        Book randomBook = getRandomAvailableBook(dbBooks);
                        if (randomBook != null) {
                            LocalDateTime borrowDate = LocalDateTime.now().minusDays(random.nextInt(30));
                            BorrowingRecord borrowing = new BorrowingRecord(user.getUserId(), randomBook.getBookId(), borrowDate);
                            
                            borrowing.setDueDate(borrowDate.plusDays(user.getRole().getLoanDuration()));
                            
                            if (random.nextDouble() >= 0.7) {  // 30% chance of not returning
                                randomBook.setAvailable(false);
                            } else {
                                borrowing.setReturnDate(LocalDateTime.now().minusDays(random.nextInt(14)));
                            }

                            DatabaseConnection.create(borrowing);
                            DatabaseConnection.update(randomBook);
                            totalBorrowings++;
                            System.out.println("Book " + randomBook.getTitle() + " borrowed by " + user.getUsername());
                        } else {
                            System.out.println("No available books to borrow.");
                            break;  // Exit the loop if no more books are available
                        }
                    }
                } else {
                    System.out.println("User " + user.getUsername() + " did not borrow any books.");
                }
            }
            System.out.println("Total borrowings created: " + totalBorrowings);
        } catch (SQLException e) {
            System.err.println("Error creating sample borrowings: " + e.getMessage());
        }
    }

    private Book getRandomAvailableBook(List<Book> dbBooks) {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : dbBooks) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks.isEmpty() ? null : availableBooks.get(random.nextInt(availableBooks.size()));
    }

    private void saveToDatabase() {
        try {
            List<User> existingUsers = DatabaseConnection.readAll(User.class);
            List<Book> existingBooks = DatabaseConnection.readAll(Book.class);

            for (User user : users) {
                if (!userExists(existingUsers, user)) {
                    DatabaseConnection.create(user);
                }
            }
            for (Book book : books) {
                if (!bookExists(existingBooks, book)) {
                    DatabaseConnection.create(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving sample data to database: " + e.getMessage());
        }
    }

    private boolean userExists(List<User> existingUsers, User newUser) {
        for (User user : existingUsers) {
            if (user.getUsername().equals(newUser.getUsername()) || user.getEmail().equals(newUser.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private boolean bookExists(List<Book> existingBooks, Book newBook) {
        for (Book book : existingBooks) {
            if (book.getIsbn().equals(newBook.getIsbn())) {
                return true;
            }
        }
        return false;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Book> getBooks() {
        return books;
    }
}