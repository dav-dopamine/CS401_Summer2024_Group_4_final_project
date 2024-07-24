DROP TABLE IF EXISTS BorrowingRecords;
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    userId VARCHAR(6) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'FACULTY', 'LIBRARIAN') NOT NULL
);

CREATE TABLE Books (
    bookId VARCHAR(6) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    bookType ENUM('Physical', 'EBook', 'AudioBook') NOT NULL,
    location VARCHAR(50),
    format VARCHAR(50),
    fileSize INT,
    downloadLink VARCHAR(255),
    narrator VARCHAR(100),
    duration INT,
    isAvailable BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE BorrowingRecords (
    recordId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(6) NOT NULL,
    bookId VARCHAR(6) NOT NULL,
    borrowDate DATETIME NOT NULL,
    returnDate DATETIME,
    isReservation BOOLEAN NOT NULL DEFAULT FALSE,
    dueDate DATETIME NOT NULL,
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (bookId) REFERENCES Books(bookId)
);

CREATE INDEX idx_books_title ON Books(title);
CREATE INDEX idx_books_author ON Books(author);
CREATE INDEX idx_books_isbn ON Books(isbn);
CREATE INDEX idx_borrowing_userid ON BorrowingRecords(userId);
CREATE INDEX idx_borrowing_bookid ON BorrowingRecords(bookId);
CREATE INDEX idx_borrowing_duedate ON BorrowingRecords(dueDate);
