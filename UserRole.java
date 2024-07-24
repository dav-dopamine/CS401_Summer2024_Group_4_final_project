package biblioConnect_v3;

public enum UserRole {
    STUDENT(5, 14),
    FACULTY(10, 30),
    LIBRARIAN(15, 45);

    private final int maxBooksAllowed;
    private final int loanDuration;

    UserRole(int maxBooksAllowed, int loanDuration) {
        this.maxBooksAllowed = maxBooksAllowed;
        this.loanDuration = loanDuration;
    }

    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }

    public int getLoanDuration() {
        return loanDuration;
    }
}