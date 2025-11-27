package Service;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;

import Domain.Book;

public class BookServiceTest {

    private BookService book ;

    @BeforeEach
    public void setup() throws Exception
    {
        PrintWriter pw = new PrintWriter(new FileWriter("books_test.txt"));
        pw.println("Clean Code;Robert Martin;111");
        pw.println("Messages from the Qur'an;Adham Sharkawi;40");
        pw.println("Morning Talk;Adham Sharkawi;40");
        pw.close();

        FileBookRepository.repoPath = "books_test.txt";

        book = new BookService();

    }

    @Test
    public void TestAddBook() throws Exception {
        Book newb = new Book("salam", "adham", "50");
        book.addBook(newb);

        BufferedReader br = new BufferedReader(new FileReader("books_test.txt"));
        String lastLine = null;
        String line;

        while ((line = br.readLine()) != null)
        {
            lastLine = line ;
        }

        br.close();

        Assertions.assertEquals("salam;adham;50", lastLine);

    }



    @Test
    public void TestSuccessSearchBookByTitle()
    {
        List<Book> result = book.searchByTitle("Messages from the Qur'an");
        //Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Messages from the Qur'an", result.get(0).getTitle());

    }

    @Test
    public void TestFailedSearchBookByTitle()
    {
        List<Book> result = book.searchByTitle("Data Structures");
        Assertions.assertEquals("Data Structures", result.get(0).getTitle());
    }




    @Test
    public void TestSuccessSearchBookByauthor()
    {
        List<Book> result = book.searchByAuthor("Robert");
        Assertions.assertEquals("Robert Martin", result.get(0).getAuthor());

    }

    @Test
    public void TestFailedSearchBookByauthor()
    {
        List<Book> result = book.searchByAuthor("ahmad");
        Assertions.assertEquals("ahmad", result.get(0).getAuthor());

    }


    @Test
    public void TestSuccessSearchBookByauthor2()
    {
        List<Book> result = book.searchByAuthor("Adham");
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Adham Sharkawi", result.get(0).getAuthor());

    }

    @Test
    public void TestFailedSearchBookByauthor2()
    {
        List<Book> result = book.searchByAuthor("Adham");
        Assertions.assertEquals(1, result.size());
    }




    @Test
    public void TestSuccessSearchBookByisbn()
    {
        List<Book> result = book.searchByISBN("111");
        Assertions.assertEquals("111", result.get(0).getIsbn());
    }


    @Test
    public void TestFailedSearchBookByisbn()
    {
        List<Book> result = book.searchByISBN("30");
        Assertions.assertEquals("30", result.get(0).getIsbn());
    }



}
