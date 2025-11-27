package Service;
import org.junit.jupiter.api.*;
import java.io.*;
import Domain.User;


public class AdminServiceTest {

    private AdminService adminService;

    @BeforeEach
    public void setup() throws Exception {

        PrintWriter pw = new PrintWriter(new FileWriter("users_test.txt"));
        pw.println("ahmad;123;admin");
        pw.println("yahya;141186;admin");
        pw.println("yousef;2492004;user");
        pw.close();

        FileUserRepository.repoPath = "users_test.txt";

        adminService = new AdminService();

    }

    @Test
    public void testAdminLoginSuccess()
    {
        Assertions.assertTrue(adminService.login("yahya", "141186"));
    }

    @Test
    public void testAdminLoginWrongPassword()
    {
        Assertions.assertFalse(adminService.login("yahya", "1234"));
    }


    @Test
    public void testAdminLoginNotAdmin()
    {
        Assertions.assertFalse(adminService.login("yousef", "2492004"));
    }


    @Test
    public void testLogout()
    {
        adminService.login("yahya", "141186");
        adminService.logout();
        Assertions.assertFalse(adminService.isLoggedIn());
    }


}
