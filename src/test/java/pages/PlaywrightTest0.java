package pages;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public class PlaywrightTest0 {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        // launch() télécharge automatiquement le navigateur si besoin
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testLoginPlaywright() {
        page.navigate("https://www.saucedemo.com");

        // Pas besoin d'attendre, Playwright le fait tout seul
        page.locator("#user-name").fill("standard_user");
        page.locator("#password").fill("secret_sauce");
        page.click("#login-button");

        // Vérification
        Assertions.assertTrue(page.isVisible(".inventory_list"));
    }

    @AfterEach
    void closeContext() { context.close(); }

    @AfterAll
    static void closeBrowser() { browser.close(); playwright.close(); }
}