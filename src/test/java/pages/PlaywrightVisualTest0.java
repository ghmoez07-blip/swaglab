package pages;

import com.microsoft.playwright.*;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlaywrightVisualTest0 {
    static Playwright playwright;
    static Browser browser;
    Page page;

    @BeforeAll
    static void init() {
        OpenCV.loadLocally();
        playwright = Playwright.create();
        // Playwright installe ses propres navigateurs, pas de souci de version !
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setup() {
        page = browser.newPage();
    }

    @Test
    @DisplayName("Trouver le logo SwagLabs avec Playwright et OpenCV")
    void testLogoRecognition() {
        page.navigate("https://www.saucedemo.com");

        // 1. Capture d'écran avec Playwright
        String scenePath = "target/playwright_scene.png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(scenePath)));

        // 2. Traitement OpenCV
        Mat scene = Imgcodecs.imread(scenePath);
        Mat template = Imgcodecs.imread("src/test/resources/logo_ref.PNG");

        if (template.empty()) {
            throw new RuntimeException("Template introuvable ! Vérifiez src/test/resources/logo_ref.png");
        }

        // 3. Algorithme de recherche
        Mat result = new Mat();
        Imgproc.matchTemplate(scene, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        System.out.println("Score de matching : " + (mmr.maxVal * 100) + "%");

        // 4. Assertion
        assertTrue(mmr.maxVal > 0.9, "Logo non détecté visuellement");

        // 5. Cliquer au centre du logo trouvé
        double clickX = mmr.maxLoc.x + (template.cols() / 2.0);
        double clickY = mmr.maxLoc.y + (template.rows() / 2.0);

        // Playwright clique directement sur les coordonnées précises
        page.mouse().click(clickX, clickY);
        System.out.println("Clic effectué sur le logo aux coordonnées : " + clickX + "," + clickY);
    }

    @AfterEach
    void tearDown() { page.close(); }

    @AfterAll
    static void close() { browser.close(); playwright.close(); }
}