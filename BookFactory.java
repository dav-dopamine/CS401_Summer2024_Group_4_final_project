package biblioConnect_v3;

public class BookFactory {
    public static Book createBook(String bookType, String title, String author, String isbn, String... additionalInfo) {
        switch (bookType.toLowerCase()) {
            case "physical":
                if (additionalInfo.length < 1) {
                    throw new IllegalArgumentException("Physical book requires location");
                }
                return new PhysicalBook(title, author, isbn, additionalInfo[0]);
            case "ebook":
                if (additionalInfo.length < 3) {
                    throw new IllegalArgumentException("EBook requires format, fileSize, and downloadLink");
                }
                return new EBook(title, author, isbn, additionalInfo[0], Integer.parseInt(additionalInfo[1]), additionalInfo[2]);
            case "audiobook":
                if (additionalInfo.length < 5) {
                    throw new IllegalArgumentException("AudioBook requires format, fileSize, downloadLink, narrator, and duration");
                }
                return new AudioBook(title, author, isbn, additionalInfo[0], Integer.parseInt(additionalInfo[1]), 
                                     additionalInfo[2], additionalInfo[3], Integer.parseInt(additionalInfo[4]));
            default:
                throw new IllegalArgumentException("Unknown book type: " + bookType);
        }
    }
}