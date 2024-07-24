package biblioConnect_v3;

import java.time.LocalDateTime;
import java.util.List;

public interface LibraryManagementService {
    /**
     * Adds a new book to the library.
     * Pre-condition: All parameters are non-null, isbn is unique
     * Post-condition: A new book is added to the library
     */
    void addBook(String username, String title, String author, String isbn, String bookType, String... additionalInfo);

    /**
     * Removes a book from the library.
     * Pre-condition: bookId exists in the library
     * Post-condition: The book is removed from the library
     */
    void removeBook(String username, String bookId);

    /**
     * Retrieves a book by its ID.
     * Pre-condition: bookId is non-null
     * Post-condition: Returns the Book object if found, null otherwise
     */
    Book getBook(String bookId);

    /**
     * Borrows a book for a user.
     * Pre-condition: username and bookId exist, book is available, user has not exceeded borrowing limit
     * Post-condition: Book is marked as borrowed, a new BorrowingRecord is created
     */
    boolean borrowBook(String username, String bookId);

    /**
     * Returns a borrowed book.
     * Pre-condition: username and bookId exist, book is currently borrowed by the user
     * Post-condition: Book is marked as available, BorrowingRecord is updated with return date
     */
    boolean returnBook(String username, String bookId);

    /**
     * Searches for books based on a query.
     * Pre-condition: query is non-null
     * Post-condition: Returns a list of books matching the query
     */
    List<Book> searchBooks(String query);

    /**
     * Retrieves borrowing history for a user.
     * Pre-condition: username exists
     * Post-condition: Returns a list of BorrowingRecord objects for the user
     */
    List<BorrowingRecord> getBorrowingHistory(String username);

    /**
     * Reserves a book for a user.
     * Pre-condition: username and bookId exist, book is not available for immediate borrowing
     * Post-condition: A new reservation is created for the user
     */
    void reserveBook(String username, String bookId);

    /**
     * Retrieves reservations for a user.
     * Pre-condition: username exists
     * Post-condition: Returns a list of BorrowingRecord objects representing the user's reservations
     */
    List<BorrowingRecord> getUserReservations(String username);

    /**
     * Gets the expected return date for a borrowed book.
     * Pre-condition: bookId exists and is currently borrowed
     * Post-condition: Returns the expected return date for the book
     */
    LocalDateTime getExpectedReturnDate(String bookId);

    /**
     * Lists all books in the library.
     * Pre-condition: None
     * Post-condition: Returns a list of all Book objects in the library
     */
    List<Book> listAllBooks();

    /**
     * Retrieves overdue books for a user.
     * Pre-condition: username exists
     * Post-condition: Returns a list of BorrowingRecord objects for overdue books
     */
    List<BorrowingRecord> getUserOverdueBooks(String username);

    /**
     * Retrieves all overdue books in the library.
     * Pre-condition: None
     * Post-condition: Returns a list of all overdue BorrowingRecord objects
     */
    List<BorrowingRecord> getAllOverdueBooks(String username);
}