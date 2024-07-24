package biblioConnect_v3;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class LibraryManagementServiceImpl implements LibraryManagementService {
    private UserManagementService userManagementService;

    public LibraryManagementServiceImpl(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @Override
    public void addBook(String username, String title, String author, String isbn, String bookType, String... additionalInfo) {
        Book book = BookFactory.createBook(bookType, title, author, isbn, additionalInfo);
        try {
            DatabaseConnection.create(book);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding book: " + e.getMessage(), e);
        }
    }

  
    @Override
    public void removeBook(String username, String bookId) {
        // System.out.println("LibraryManagementServiceImpl: Attempting to remove book with ID: " + bookId);
        try {
            User user = userManagementService.getUser(username);
            if (user == null) {
                // System.out.println("LibraryManagementServiceImpl: User not found: " + username);
                throw new RuntimeException("User not found.");
            }
            if (user.getRole() != UserRole.LIBRARIAN) {
                // System.out.println("LibraryManagementServiceImpl: User not authorized to remove books");
                throw new RuntimeException("You are not authorized to remove books.");
            }

            Book book = DatabaseConnection.read(Book.class, bookId);
            if (book == null) {
                // System.out.println("LibraryManagementServiceImpl: Book not found with ID: " + bookId);
                throw new RuntimeException("Book not found with ID: " + bookId);
            }

            if (!book.isAvailable()) {
                // System.out.println("LibraryManagementServiceImpl: Cannot remove book. It is currently borrowed.");
                throw new RuntimeException("Cannot remove book. It is currently borrowed.");
            }

            DatabaseConnection.deleteBook(bookId);
            // System.out.println("LibraryManagementServiceImpl: Book removed successfully: " + book.getTitle());
        } catch (SQLException e) {
            // System.out.println("LibraryManagementServiceImpl: SQL error while removing book: " + e.getMessage());
            throw new RuntimeException("Error removing book: " + e.getMessage(), e);
        }
    }

    
    @Override
    public Book getBook(String bookId) {
        try {
            return DatabaseConnection.read(Book.class, bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting book: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean borrowBook(String username, String bookId) {
        // System.out.println("LibraryManagementServiceImpl: Attempting to borrow book for user: " + username);
        try {
            User user = userManagementService.getUser(username);
            if (user == null) {
                // System.out.println("LibraryManagementServiceImpl: User not found for username: " + username);
                return false;
            }
            // System.out.println("LibraryManagementServiceImpl: User found - ID: " + user.getUserId() + ", Role: " + user.getRole());

            Book book = DatabaseConnection.read(Book.class, bookId);
            if (book == null) {
                // System.out.println("LibraryManagementServiceImpl: Invalid book ID: " + bookId);
                return false;
            }
            // System.out.println("LibraryManagementServiceImpl: Book found - Title: " + book.getTitle() + ", Available: " + book.isAvailable());

            if (!book.isAvailable()) {
                // System.out.println("LibraryManagementServiceImpl: Book is not available for borrowing");
                return false;
            }

            int currentlyBorrowed = getBorrowedBooksCount(user.getUserId());
            // System.out.println("LibraryManagementServiceImpl: User's currently borrowed books: " + currentlyBorrowed + ", Max allowed: " + user.getRole().getMaxBooksAllowed());
            if (currentlyBorrowed >= user.getRole().getMaxBooksAllowed()) {
                // System.out.println("LibraryManagementServiceImpl: User has reached maximum borrowing limit");
                return false;
            }

            book.setAvailable(false);
            DatabaseConnection.update(book);

            BorrowingRecord record = new BorrowingRecord(user.getUserId(), bookId, LocalDateTime.now());
            record.setDueDate(LocalDateTime.now().plusDays(user.getRole().getLoanDuration()));
            DatabaseConnection.create(record);

            // System.out.println("LibraryManagementServiceImpl: Book borrowed successfully");
            return true;
        } catch (SQLException e) {
            // System.out.println("LibraryManagementServiceImpl: Database error while borrowing book: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean returnBook(String username, String bookId) {
        try {
            User user = userManagementService.getUser(username);
            if (user == null) {
                return false;
            }

            Book book = DatabaseConnection.read(Book.class, bookId);
            if (book == null) {
                return false;
            }

            List<BorrowingRecord> records = DatabaseConnection.getBorrowingRecordsByUserId(user.getUserId());
            BorrowingRecord activeBorrowing = records.stream()
                .filter(record -> record.getBookId().equals(bookId) && record.getReturnDate() == null)
                .findFirst()
                .orElse(null);

            if (activeBorrowing == null) {
                return false;
            }

            activeBorrowing.setReturnDate(LocalDateTime.now());
            DatabaseConnection.update(activeBorrowing);

            book.setAvailable(true);
            DatabaseConnection.update(book);

            return true;

        } catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    public List<Book> searchBooks(String query) {
        try {
            List<Book> allBooks = DatabaseConnection.readAll(Book.class);
            return allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                                book.getIsbn().equals(query))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error searching books: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowingRecord> getBorrowingHistory(String username) {
        try {
            User user = userManagementService.getUser(username);
            if (user == null) {
                // Handle the case where the user is not found
                System.out.println("User not found: " + username);
                return new ArrayList<>(); // Return an empty list
            }
            List<BorrowingRecord> allRecords = DatabaseConnection.readAll(BorrowingRecord.class);
            return allRecords.stream()
                .filter(record -> record.getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting borrowing history: " + e.getMessage(), e);
        }
    }

    @Override
    public void reserveBook(String username, String bookId) {
        try {
            User user = userManagementService.getUser(username);
            Book book = DatabaseConnection.read(Book.class, bookId);
            if (book == null) {
                throw new RuntimeException("Book not found with ID: " + bookId);
            }
            
            if (book.isAvailable()) {
                throw new RuntimeException("Book is currently available and can be borrowed directly.");
            }
            
            // Check if user already has an active borrowing or reservation for this book
            List<BorrowingRecord> userRecords = DatabaseConnection.getBorrowingRecordsByUserId(user.getUserId());
            boolean alreadyBorrowedOrReserved = userRecords.stream()
                    .anyMatch(record -> record.getBookId().equals(bookId) && 
                             (record.getReturnDate() == null || record.isReservation()));
            if (alreadyBorrowedOrReserved) {
                throw new RuntimeException("You already have borrowed or reserved this book.");
            }
            
            BorrowingRecord reservation = new BorrowingRecord(user.getUserId(), bookId, LocalDateTime.now());
            reservation.setReservation(true);
            DatabaseConnection.create(reservation);
            System.out.println("LibraryManagementServiceImpl: Book reserved successfully");
        } catch (SQLException e) {
            throw new RuntimeException("Error reserving book: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowingRecord> getUserReservations(String username) {
        try {
            User user = userManagementService.getUser(username);
            List<BorrowingRecord> allRecords = DatabaseConnection.readAll(BorrowingRecord.class);
            return allRecords.stream()
                .filter(record -> record.getUserId().equals(user.getUserId()) && record.isReservation() && record.getReturnDate() == null)
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user reservations: " + e.getMessage(), e);
        }
    }

    @Override
    public LocalDateTime getExpectedReturnDate(String bookId) {
        try {
            List<BorrowingRecord> records = DatabaseConnection.readAll(BorrowingRecord.class);
            return records.stream()
                .filter(record -> record.getBookId().equals(bookId) && !record.isReservation() && record.getReturnDate() == null)
                .findFirst()
                .map(BorrowingRecord::getDueDate)
                .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting expected return date: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> listAllBooks() {
        try {
            return DatabaseConnection.readAll(Book.class);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing all books: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowingRecord> getUserOverdueBooks(String username) {
        try {
            User user = userManagementService.getUser(username);
            List<BorrowingRecord> allRecords = DatabaseConnection.readAll(BorrowingRecord.class);
            LocalDateTime now = LocalDateTime.now();
            return allRecords.stream()
                .filter(record -> record.getUserId().equals(user.getUserId()) && 
                                  !record.isReservation() && 
                                  record.getReturnDate() == null && 
                                  record.getDueDate().isBefore(now))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user's overdue books: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowingRecord> getAllOverdueBooks(String username) {
        try {
            List<BorrowingRecord> allRecords = DatabaseConnection.readAll(BorrowingRecord.class);
            if (username.isEmpty()) {
                return allRecords;  // Return all records if no username is provided
            }
            LocalDateTime now = LocalDateTime.now();
            return allRecords.stream()
                .filter(record -> record.getUserId().equals(username) && 
                                  !record.isReservation() && 
                                  record.getReturnDate() == null && 
                                  record.getDueDate().isBefore(now))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all overdue books: " + e.getMessage(), e);
        }
    }
    
    private int getBorrowedBooksCount(String userId) {
        try {
            List<BorrowingRecord> records = DatabaseConnection.getBorrowingRecordsByUserId(userId);
            return (int) records.stream()
                    .filter(record -> record.getReturnDate() == null)
                    .count();
        } catch (SQLException e) {
            System.out.println("Error getting borrowed books count: " + e.getMessage());
            return 0;
        }
    }
}