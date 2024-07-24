package biblioConnect_v3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.time.temporal.ChronoUnit;

public class LibraryUI implements AutoCloseable {
    private LibraryManagementSystem system;
    private Scanner scanner;
    private User currentUser;

    public LibraryUI(LibraryManagementSystem system) {
        this.system = system;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.out.println("\n===== Error =====\n");
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
                System.out.println("\nPress Enter to restart the application...");
                scanner.nextLine();
            }
        }
    }

    private void showLoginMenu() {
        while (true) {
            System.out.println("\n===== BiblioConnect Login =====\n");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            int choice = getValidIntInput("Choose an option: ", 1, 2);
            
            switch (choice) {
                case 1:
                    login();
                    return;
                case 2:
                    System.out.println("\n===== Goodbye =====\n");
                    System.out.println("Thank you for using BiblioConnect. Goodbye!");
                    System.exit(0);
            }
        }
    }

    private void login() {
        System.out.println("\n===== User Login =====\n");
        String username = getValidUsername();
        String password = getValidPassword();

        if (system.getUserManagementService().authenticateUser(username, password)) {
            currentUser = system.getUserManagementService().getUser(username);
            System.out.println("\n===== Welcome =====\n");
            System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } else {
            System.out.println("\n===== Login Failed =====\n");
            System.out.println("Invalid username or password.");
            System.out.println("\nPress Enter to return to the login menu...");
            scanner.nextLine();
        }
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n===== BiblioConnect Main Menu =====\n");
            System.out.println("1. Book Services");
            System.out.println("2. Borrowing Services");
            System.out.println("3. User Management");
            System.out.println("4. Search");
            System.out.println("5. Reports");
            System.out.println("6. Logout");

            int choice = getValidIntInput("Choose an option: ", 1, 6);

            switch (choice) {
                case 1:
                    showBookServicesMenu();
                    break;
                case 2:
                    showBorrowingServicesMenu();
                    break;
                case 3:
                    showUserManagementMenu();
                    break;
                case 4:
                    searchBook();
                    break;
                case 5:
                    showReportsMenu();
                    break;
                case 6:
                    logout();
                    return;
            }
        }
    }

    private void showBookServicesMenu() {
        while (true) {
            System.out.println("\n===== Book Services =====\n");
            System.out.println("1. List all books");
            if (currentUser.getRole() == UserRole.LIBRARIAN) {
                System.out.println("2. Add a book");
                System.out.println("3. Remove a book");
                System.out.println("4. Return to main menu");
            } else {
                System.out.println("2. Return to main menu");
            }

            int maxChoice = (currentUser.getRole() == UserRole.LIBRARIAN) ? 4 : 2;
            int choice = getValidIntInput("Choose an option: ", 1, maxChoice);

            switch (choice) {
                case 1:
                    listAllBooks();
                    break;
                case 2:
                    if (currentUser.getRole() == UserRole.LIBRARIAN) {
                        addBook();
                    } else {
                        return;
                    }
                    break;
                case 3:
                    if (currentUser.getRole() == UserRole.LIBRARIAN) {
                        removeBook();
                    }
                    break;
                case 4:
                    return;
            }
        }
    }

    private void showBorrowingServicesMenu() {
        while (true) {
            System.out.println("\n===== Borrowing Services =====\n");
            System.out.println("1. Borrow a book");
            System.out.println("2. Return a book");
            System.out.println("3. Reserve a book");
            System.out.println("4. View my reservations");
            System.out.println("5. View borrowing history");
            System.out.println("6. View my overdue books");
            if (currentUser.getRole() == UserRole.LIBRARIAN) {
                System.out.println("7. View all overdue books");
                System.out.println("8. Return to main menu");
            } else {
                System.out.println("7. Return to main menu");
            }

            int maxChoice = (currentUser.getRole() == UserRole.LIBRARIAN) ? 8 : 7;
            int choice = getValidIntInput("Choose an option: ", 1, maxChoice);

            switch (choice) {
                case 1:
                    borrowBook();
                    break;
                case 2:
                    returnBook();
                    break;
                case 3:
                    reserveBook();
                    break;
                case 4:
                    viewMyReservations();
                    break;
                case 5:
                    viewBorrowingHistory();
                    break;
                case 6:
                    viewMyOverdueBooks();
                    break;
                case 7:
                    if (currentUser.getRole() == UserRole.LIBRARIAN) {
                        viewAllOverdueBooks();
                    } else {
                        return;
                    }
                    break;
                case 8:
                    return;
            }
        }
    }

    private void showUserManagementMenu() {
        while (true) {
            System.out.println("\n===== User Management =====\n");
            System.out.println("1. Update my profile");
            if (currentUser.getRole() == UserRole.LIBRARIAN) {
                System.out.println("2. Register new user");
                System.out.println("3. List all users");
                System.out.println("4. Remove user");
                System.out.println("5. Return to main menu");
            } else {
                System.out.println("2. Return to main menu");
            }

            int maxChoice = (currentUser.getRole() == UserRole.LIBRARIAN) ? 5 : 2;
            int choice = getValidIntInput("Choose an option: ", 1, maxChoice);

            switch (choice) {
                case 1:
                    updateProfile();
                    break;
                case 2:
                    if (currentUser.getRole() == UserRole.LIBRARIAN) {
                        registerNewUser();
                    } else {
                        return;
                    }
                    break;
                case 3:
                    if (currentUser.getRole() == UserRole.LIBRARIAN) {
                        listAllUsers();
                    }
                    break;
                case 4:
                    if (currentUser.getRole() == UserRole.LIBRARIAN) {
                        removeUser();
                    }
                    break;
                case 5:
                    return;
            }
        }
    }

    private void showReportsMenu() {
        while (true) {
            System.out.println("\n===== Reports =====\n");
            if (currentUser.getRole() == UserRole.LIBRARIAN || currentUser.getRole() == UserRole.FACULTY) {
                System.out.println("1. View database content report");
                System.out.println("2. Return to main menu");
                
                int choice = getValidIntInput("Choose an option: ", 1, 2);
                
                switch (choice) {
                    case 1:
                        reportDatabaseContent();
                        break;
                    case 2:
                        return;
                }
            } else {
                System.out.println("You do not have access to any reports.");
                System.out.println("\nPress Enter to return to the main menu...");
                scanner.nextLine();
                return;
            }
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("\n===== Logout =====\n");
        System.out.println("You have been logged out successfully.");
        System.out.println("\nPress Enter to return to the login menu...");
        scanner.nextLine();
    }

    // Book Management Methods

    private void listAllBooks() {
        System.out.println("\n===== All Books in the Library =====\n");
        List<Book> books = system.getLibraryManagementService().listAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            for (Book book : books) {
                printBookDetails(book);
            }
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void addBook() {
        System.out.println("\n===== Add a New Book =====\n");
        String title = getInputWithPrompt("Enter book title: ");
        String author = getInputWithPrompt("Enter author: ");
        String isbn = getValidISBN();

        if (title == null || author == null || isbn == null) {
            System.out.println("\nError: Book title, author, and ISBN cannot be empty.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        if (isbnExists(isbn)) {
            System.out.println("\nError: A book with this ISBN already exists in the library.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        String bookType = getValidBookType();
        if (bookType == null) {
            System.out.println("\nError: Book type cannot be empty.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        String[] additionalInfo = null;
        switch (bookType.toLowerCase()) {
            case "physical":
                additionalInfo = new String[]{getInputWithPrompt("Enter shelf location (e.g., 'Fiction A1', 'Non-fiction B3'): ")};
                break;
            case "ebook":
                additionalInfo = getEBookInfo();
                break;
            case "audiobook":
                additionalInfo = getAudioBookInfo();
                break;
        }

        if (additionalInfo == null || additionalInfo.length == 0) {
            System.out.println("\nError: Additional book information is required.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        try {
            system.getLibraryManagementService().addBook(currentUser.getUserId(), title, author, isbn, bookType, additionalInfo);
            System.out.println("\nBook added successfully.");
        } catch (Exception e) {
            System.out.println("\nError adding book: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void removeBook() {
        System.out.println("\n===== Remove a Book =====\n");
        String bookId = getInputWithPrompt("Enter book ID to remove: ");
        if (bookId == null || bookId.trim().isEmpty()) {
            System.out.println("\nError: Book ID cannot be empty.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        try {
            system.getLibraryManagementService().removeBook(currentUser.getUsername(), bookId);
            System.out.println("\nBook removed successfully.");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Cannot remove book. It has associated borrowing records.")) {
                System.out.println("\nError: Cannot remove this book because it has associated borrowing records.");
                System.out.println("You may need to archive the book instead of removing it.");
            } else {
                System.out.println("\nError removing book: " + e.getMessage());
            }
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    // Borrowing Methods

    private void borrowBook() {
        System.out.println("\n===== Borrow a Book =====\n");
        
        if (currentUser == null) {
            System.out.println("Error: No user is currently logged in.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        System.out.println("Current user information:");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("User ID: " + currentUser.getUserId());
        System.out.println("Role: " + currentUser.getRole());

        System.out.println("To borrow a book, you need to enter its Book ID.");
        System.out.println("You can find the Book ID by searching for books or viewing the book list.");

        int borrowedCount = getCurrentBorrowedCount(currentUser.getUsername());
        int maxAllowed = currentUser.getRole().getMaxBooksAllowed();
        System.out.println("\nYour current borrowing status:");
        System.out.println("Books currently borrowed: " + borrowedCount);
        System.out.println("Maximum books you can borrow: " + maxAllowed);
        System.out.println("Remaining borrowing capacity: " + (maxAllowed - borrowedCount));

        if (borrowedCount >= maxAllowed) {
            System.out.println("\nYou have reached your borrowing limit. Please return a book before borrowing a new one.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        String bookId = getInputWithPrompt("\nEnter the Book ID of the book you want to borrow: ");
        if (bookId == null || bookId.trim().isEmpty()) {
            System.out.println("\nError: Book ID cannot be empty.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        try {
            System.out.println("Attempting to borrow book with ID: " + bookId + " for user: " + currentUser.getUsername());
            boolean success = system.getLibraryManagementService().borrowBook(currentUser.getUsername(), bookId);
            
            if (success) {
                System.out.println("\n===== Book Borrowed Successfully =====\n");
                Book borrowedBook = system.getLibraryManagementService().getBook(bookId);
                if (borrowedBook != null) {
                    System.out.println("Book details:");
                    printBookDetails(borrowedBook);
                    if (borrowedBook instanceof EBook) {
                        System.out.println("Instructions for eBook:");
                        System.out.println("- You will receive an email with a download link.");
                        System.out.println("- Use the provided link to download the eBook within 24 hours.");
                    } else if (borrowedBook instanceof AudioBook) {
                        System.out.println("Instructions for AudioBook:");
                        System.out.println("- You will receive an email with streaming information.");
                        System.out.println("- Use the provided credentials to access the audiobook online.");
                    } else {
                        System.out.println("Please collect the physical book from the library counter.");
                    }
                }
                System.out.println("\nUpdated borrowing status:");
                System.out.println("Books now borrowed: " + (borrowedCount + 1));
                System.out.println("Remaining borrowing capacity: " + (maxAllowed - borrowedCount - 1));
                System.out.println("The loan duration for your role is " + currentUser.getRole().getLoanDuration() + " days.");
            } else {
                Book book = system.getLibraryManagementService().getBook(bookId);
                if (book == null) {
                    System.out.println("\nError: Book not found. Please check the Book ID and try again.");
                } else if (!book.isAvailable()) {
                    System.out.println("\nError: The book is currently unavailable for borrowing.");
                } else {
                    System.out.println("\nUnable to borrow the book due to a system error. Please try again later.");
                }
            }
        } catch (RuntimeException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }
    
    private void returnBook() {
        System.out.println("\n===== Return a Book =====\n");
        System.out.println("To return a book, you need to enter its Book ID.");
        
        List<BorrowingRecord> borrowedBooks = getCurrentlyBorrowedBooks();
        if (borrowedBooks.isEmpty()) {
            System.out.println("You don't have any books to return.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\nYour currently borrowed books:");
        for (BorrowingRecord record : borrowedBooks) {
            Book book = system.getLibraryManagementService().getBook(record.getBookId());
            if (book != null) {
                System.out.println("Book ID: " + book.getBookId() + " - Title: " + book.getTitle() + " - Type: " + book.getBookType());
            }
        }

        String bookId = getInputWithPrompt("\nEnter the Book ID of the book you want to return: ");
        if (bookId == null || bookId.trim().isEmpty()) {
            System.out.println("\nError: Book ID cannot be empty.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Attempting to return book with ID: " + bookId);
        boolean returnSuccessful = system.getLibraryManagementService().returnBook(currentUser.getUsername(), bookId);
        
        if (returnSuccessful) {
            System.out.println("\nBook returned successfully.");
        } else {
            System.out.println("\nUnable to return the book. It may not exist or you haven't borrowed it.");
        }
        
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void reserveBook() {
        System.out.println("\n===== Reserve a Book =====\n");
        String bookId = getInputWithPrompt("Enter book ID to reserve: ");
        if (bookId == null) {
            System.out.println("\nError: Book ID cannot be empty.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        try {
            system.getLibraryManagementService().reserveBook(currentUser.getUsername(), bookId);
            System.out.println("\nBook reserved successfully.");
            LocalDateTime expectedReturnDate = system.getLibraryManagementService().getExpectedReturnDate(bookId);
            if (expectedReturnDate != null) {
                System.out.println("Expected return date: " + expectedReturnDate);
            }
        } catch (RuntimeException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void viewMyReservations() {
        System.out.println("\n===== Your Active Reservations =====\n");
        List<BorrowingRecord> reservations = system.getLibraryManagementService().getUserReservations(currentUser.getUsername());
        if (reservations.isEmpty()) {
            System.out.println("You have no active reservations.");
        } else {
            for (BorrowingRecord reservation : reservations) {
                Book book = system.getLibraryManagementService().getBook(reservation.getBookId());
                if (book != null) {
                    System.out.println("Title: " + book.getTitle());
                    System.out.println("Reserved on: " + reservation.getBorrowDate());
                    LocalDateTime expectedReturnDate = system.getLibraryManagementService().getExpectedReturnDate(reservation.getBookId());
                    if (expectedReturnDate != null) {
                        System.out.println("Expected return date: " + expectedReturnDate);
                    }
                    System.out.println("--------------------");
                }
            }
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void viewBorrowingHistory() {
        System.out.println("\n===== Your Borrowing History =====\n");
        List<BorrowingRecord> history = system.getLibraryManagementService().getBorrowingHistory(currentUser.getUsername());
        if (history.isEmpty()) {
            System.out.println("You have no borrowing history.");
        } else {
            for (BorrowingRecord record : history) {
                Book book = system.getLibraryManagementService().getBook(record.getBookId());
                if (book != null) {
                    System.out.println("Title: " + book.getTitle());
                    System.out.println("Borrowed: " + formatDateTime(record.getBorrowDate()));
                    System.out.println("Due: " + formatDateTime(record.getDueDate()));
                    if (record.getReturnDate() != null) {
                        System.out.println("Returned: " + formatDateTime(record.getReturnDate()));
                    } else {
                        System.out.println("Status: Not returned yet");
                        if (record.getDueDate().isBefore(LocalDateTime.now())) {
                            System.out.println("OVERDUE");
                        }
                    }
                    System.out.println("--------------------");
                }
            }
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void viewMyOverdueBooks() {
        System.out.println("\n===== Your Overdue Books =====\n");
        try {
            List<BorrowingRecord> overdueBooks = system.getLibraryManagementService().getUserOverdueBooks(currentUser.getUsername());
            if (overdueBooks.isEmpty()) {
                System.out.println("You have no overdue books.");
            } else {
                for (BorrowingRecord record : overdueBooks) {
                    Book book = system.getLibraryManagementService().getBook(record.getBookId());
                    if (book != null) {
                        System.out.println("Title: " + book.getTitle());
                        System.out.println("Due Date: " + record.getDueDate());
                        System.out.println("Days Overdue: " + ChronoUnit.DAYS.between(record.getDueDate(), LocalDateTime.now()));
                        System.out.println("--------------------");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving overdue books: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void viewAllOverdueBooks() {
        System.out.println("\n===== All Overdue Books =====\n");
        List<BorrowingRecord> overdueBooks = system.getLibraryManagementService().getAllOverdueBooks(currentUser.getUsername());
        if (overdueBooks.isEmpty()) {
            System.out.println("No overdue books.");
        } else {
            for (BorrowingRecord record : overdueBooks) {
                Book book = system.getLibraryManagementService().getBook(record.getBookId());
                User user = system.getUserManagementService().getUser(record.getUserId());
                if (book != null && user != null) {
                    System.out.println("Title: " + book.getTitle());
                    System.out.println("User: " + user.getName());
                    System.out.println("Due Date: " + record.getDueDate());
                    System.out.println("Days Overdue: " + ChronoUnit.DAYS.between(record.getDueDate(), LocalDateTime.now()));
                    System.out.println("--------------------");
                }
            }
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    // User Management Methods

    private void updateProfile() {
        System.out.println("\n===== Update Your Profile =====\n");
        String name = getValidName();
        String email = getValidEmail();
        String username = getValidUsername();
        String password = getValidPassword();

        if (name == null || email == null || username == null || password == null) {
            System.out.println("\nError: All fields are required.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        try {
            if (!username.equals(currentUser.getUsername()) && system.getUserManagementService().getUser(username) != null) {
                System.out.println("\nError: This username is already taken. Please choose a different username.");
                System.out.println("\nPress Enter to return to the main menu...");
                scanner.nextLine();
                return;
            }

            system.getUserManagementService().updateUser(currentUser.getUsername(), name, email, username, password);
            System.out.println("\nProfile updated successfully.");
            currentUser = system.getUserManagementService().getUser(username);
        } catch (Exception e) {
            System.out.println("\nError updating profile: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void registerNewUser() {
        System.out.println("\n===== Register New User =====\n");
        String name = getValidName();
        String email = getValidEmail();
        String username = getValidUsername();
        String password = getValidPassword();
        UserRole role = getValidUserRole();

        try {
            if (emailExists(email)) {
                System.out.println("\nError: This email is already registered. Please use a different email address.");
                System.out.println("\nPress Enter to return to the main menu...");
                scanner.nextLine();
                return;
            }

            system.getUserManagementService().registerUser(name, email, username, password, role);
            System.out.println("\nUser registered successfully.");
        } catch (Exception e) {
            System.out.println("\nError registering user: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void listAllUsers() {
        System.out.println("\n===== All Users =====\n");
        List<User> users = system.getUserManagementService().listAllMembers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.println("Name: " + user.getName());
                System.out.println("Username: " + user.getUsername());
                System.out.println("Role: " + user.getRole());
                System.out.println("--------------------");
            }
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void removeUser() {
        System.out.println("\n===== Remove User =====\n");
        String username = getValidUsername();
        try {
            User userToRemove = system.getUserManagementService().getUser(username);
            if (userToRemove != null) {
                system.getUserManagementService().removeMember(username);
                System.out.println("\nUser removed successfully.");
            } else {
                System.out.println("\nUser not found.");
            }
        } catch (Exception e) {
            System.out.println("\nError removing user: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    // Search Method

    private void searchBook() {
        System.out.println("\n===== Search for a Book =====\n");
        System.out.println("You can search by title, author, or ISBN.");
        System.out.println("Examples:");
        System.out.println("- Enter a book title (or part of it): 'To Kill a Mockingbird'");
        System.out.println("- Enter an author's name: 'Harper Lee'");
        System.out.println("- Enter an ISBN: '9780446310789'");
        
        String query = getInputWithPrompt("\nEnter your search query: ");
        if (query == null) {
            System.out.println("\nError: Search query cannot be empty.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }

        List<Book> results = system.getLibraryManagementService().searchBooks(query);
        if (results == null || results.isEmpty()) {
            System.out.println("\nNo books found matching your query.");
        } else {
            System.out.println("\n===== Search Results =====\n");
            for (Book book : results) {
                printBookDetails(book);
            }
        }
        
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }

    // Report Method

    private void reportDatabaseContent() {
        try {
            List<User> users = system.getUserManagementService().listAllMembers();
            List<Book> books = system.getLibraryManagementService().listAllBooks();
            List<BorrowingRecord> allBorrowings = system.getLibraryManagementService().getAllOverdueBooks("");

            int borrowedBooks = 0;
            int overdueBooks = 0;
            LocalDateTime now = LocalDateTime.now();

            System.out.println("\nLibrary Statistics:");
            System.out.println("Users in database: " + users.size());
            System.out.println("Books in database: " + books.size());

            System.out.println("\nOverdue Books:");
            for (BorrowingRecord record : allBorrowings) {
                if (record.getReturnDate() == null && !record.isReservation()) {
                    borrowedBooks++;
                    if (record.getDueDate().isBefore(now)) {
                        overdueBooks++;
                        Book book = system.getLibraryManagementService().getBook(record.getBookId());
                        User user = system.getUserManagementService().getUser(record.getUserId());
                        if (book != null && user != null) {
                            System.out.println(book.getTitle() + " - Borrowed by: " + user.getName());
                        }
                    }
                }
            }

            System.out.println("\nBooks currently borrowed: " + borrowedBooks);
            System.out.println("Books overdue: " + overdueBooks);
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error generating database content report: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the reports menu...");
        scanner.nextLine();
    }

    // Utility Methods

    private String getValidUsername() {
        while (true) {
            String username = getInputWithPrompt("Enter username: ");
            if (username != null && username.matches("^[a-zA-Z0-9_]{3,20}$")) {
                return username;
            }
            System.out.println("Invalid username. Use 3-20 characters, only letters, numbers, and underscores.");
        }
    }

    private String getValidPassword() {
        while (true) {
            String password = getInputWithPrompt("Enter password: ");
            if (password != null && password.length() >= 8) {
                return password;
            }
            System.out.println("Password must be at least 8 characters long.");
        }
    }

    private String getValidName() {
        while (true) {
            String name = getInputWithPrompt("Enter name: ");
            if (name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z ]+$")) {
                return name;
            }
            System.out.println("Invalid name. Please use only letters and spaces.");
        }
    }

    private String getValidEmail() {
        while (true) {
            String email = getInputWithPrompt("Enter email: ");
            if (email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return email;
            }
            System.out.println("Invalid email format. Please try again.");
        }
    }

    private UserRole getValidUserRole() {
        while (true) {
            String roleString = getInputWithPrompt("Enter role (STUDENT/FACULTY/LIBRARIAN): ");
            if (roleString != null) {
                roleString = roleString.toUpperCase();
                try {
                    return UserRole.valueOf(roleString);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid role. Please enter STUDENT, FACULTY, or LIBRARIAN.");
                }
            } else {
                System.out.println("Role cannot be empty. Please try again.");
            }
        }
    }

    private String getValidISBN() {
        while (true) {
            String isbn = getInputWithPrompt("Enter ISBN (13 digits): ");
            if (isbn != null && isbn.matches("\\d{13}")) {
                return isbn;
            }
            System.out.println("Invalid ISBN. Please enter a 13-digit number.");
        }
    }

    private String getValidBookType() {
        while (true) {
            System.out.println("Available book types:");
            System.out.println("1. Physical - A traditional, physical book");
            System.out.println("2. EBook - An electronic book");
            System.out.println("3. AudioBook - An audio recording of a book");
            String type = getInputWithPrompt("Enter book type (Physical/EBook/AudioBook): ");
            if (type != null) {
                type = type.toLowerCase();
                if (type.equals("physical") || type.equals("ebook") || type.equals("audiobook")) {
                    return type;
                }
            }
            System.out.println("Invalid book type. Please enter Physical, EBook, or AudioBook.");
        }
    }

    private String[] getEBookInfo() {
        System.out.println("\nFor EBooks, we need the following information:");
        String format = getInputWithPrompt("Enter format (e.g., PDF, EPUB, MOBI): ");
        int fileSize = getValidIntInput("Enter file size (in KB): ", 1, 1000000);
        String downloadLink = getInputWithPrompt("Enter download link (URL): ");
        return new String[]{format, String.valueOf(fileSize), downloadLink};
    }

    private String[] getAudioBookInfo() {
        System.out.println("\nFor AudioBooks, we need the following information:");
        String format = getInputWithPrompt("Enter audio format (e.g., MP3, AAC, WAV): ");
        int fileSize = getValidIntInput("Enter file size (in KB): ", 1, 1000000);
        String downloadLink = getInputWithPrompt("Enter download link (URL): ");
        String narrator = getInputWithPrompt("Enter narrator's name: ");
        int duration = getValidIntInput("Enter duration (in minutes): ", 1, 3000);
        return new String[]{format, String.valueOf(fileSize), downloadLink, narrator, String.valueOf(duration)};
    }

    private String getInputWithPrompt(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    private int getValidIntInput(String prompt, int min, int max) {
        while (true) {
            String input = getInputWithPrompt(prompt);
            if (input != null) {
                try {
                    int value = Integer.parseInt(input);
                    if (value >= min && value <= max) {
                        return value;
                    }
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
    }

    private void printBookDetails(Book book) {
        if (book == null) {
            System.out.println("Error: Book details are not available.");
            return;
        }

        System.out.println("Title: " + book.getTitle());
        System.out.println("Author: " + book.getAuthor());
        System.out.println("ID: " + book.getBookId());
        System.out.println("ISBN: " + book.getIsbn());
        System.out.println("Type: " + book.getBookType());
        System.out.println("Available: " + (book.isAvailable() ? "Yes" : "No"));

        if (book instanceof PhysicalBook) {
            PhysicalBook physicalBook = (PhysicalBook) book;
            System.out.println("Location: " + physicalBook.getLocation());
        } else if (book instanceof EBook) {
            EBook eBook = (EBook) book;
            System.out.println("Format: " + eBook.getFormat());
            System.out.println("File Size: " + eBook.getFileSize() + " KB");
            System.out.println("Download Link: " + eBook.getDownloadLink());
        } else if (book instanceof AudioBook) {
            AudioBook audioBook = (AudioBook) book;
            System.out.println("Format: " + audioBook.getFormat());
            System.out.println("File Size: " + audioBook.getFileSize() + " KB");
            System.out.println("Download Link: " + audioBook.getDownloadLink());
            System.out.println("Narrator: " + audioBook.getNarrator());
            System.out.println("Duration: " + audioBook.getDuration() + " minutes");
        }

        System.out.println("--------------------");
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private boolean isbnExists(String isbn) {
        List<Book> allBooks = system.getLibraryManagementService().listAllBooks();
        return allBooks.stream().anyMatch(book -> book.getIsbn().equals(isbn));
    }

    private boolean emailExists(String email) {
        List<User> allUsers = system.getUserManagementService().listAllMembers();
        return allUsers.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private int getCurrentBorrowedCount(String username) {
        List<BorrowingRecord> borrowingRecords = system.getLibraryManagementService().getBorrowingHistory(username);
        return (int) borrowingRecords.stream()
                .filter(record -> record.getReturnDate() == null)
                .count();
    }

    private List<BorrowingRecord> getCurrentlyBorrowedBooks() {
        List<BorrowingRecord> allRecords = system.getLibraryManagementService().getBorrowingHistory(currentUser.getUsername());
        return allRecords.stream()
                .filter(record -> record.getReturnDate() == null)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}