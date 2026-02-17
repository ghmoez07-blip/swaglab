package pages;

import com.microsoft.playwright.*;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.*;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlaywrightVisualTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void init() {
        OpenCV.loadLocally();
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeEach
    void setup() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720)
                .setDeviceScaleFactor(1));
        page = context.newPage();
    }

    @Test
    @DisplayName("Test avec Reporting Visuel")
    void testWithVisualReporting() {
        page.navigate("https://www.saucedemo.com");

        // On vérifie et on génère la preuve pour le logo
        boolean isLogoFound = detectAndDraw("logo_ref.png", "RESULT_LOGO.png");

        // On vérifie et on génère la preuve pour le bouton login
        boolean isBtnFound = detectAndDraw("btn_login.png", "RESULT_BUTTON.png");

        assertTrue(isLogoFound && isBtnFound, "Certains éléments visuels manquent !");
    }

    /**
     * Méthode "Tout-en-un" : Détecte, Dessine et Sauvegarde la preuve
     */
    private boolean detectAndDraw(String templateName, String evidenceName) {
        // 1. Capture de l'écran actuel
        String tempScreen = "target/temp_" + evidenceName;
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(tempScreen)));

        Mat scene = Imgcodecs.imread(tempScreen);
        Mat template = Imgcodecs.imread("src/test/resources/templates/" + templateName);

        if (template.empty()) return false;

        // 2. Recherche OpenCV
        Mat result = new Mat();
        Imgproc.matchTemplate(scene, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        // 3. Dessin du rectangle si le score est bon (> 0.9)
        if (mmr.maxVal >= 0.9) {
            Rect rect = new Rect((int) mmr.maxLoc.x, (int) mmr.maxLoc.y, template.cols(), template.rows());

            // Dessine un rectangle rouge (BGR: 0, 0, 255)
            Imgproc.rectangle(scene, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);

            // Ajoute le texte du score juste au-dessus
            String scoreText = String.format("%.2f%%", mmr.maxVal * 100);
            Imgproc.putText(scene, scoreText, new Point(rect.x, rect.y - 5),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 255), 1);

            // Sauvegarde du résultat final
            Imgcodecs.imwrite("target/EVIDENCE_" + evidenceName, scene);
            System.out.println("📸 Preuve générée : target/EVIDENCE_" + evidenceName + " (Score: " + scoreText + ")");
            return true;
        }

        return false;
    }

    @AfterEach
    void tearDown() { context.close(); }

    @AfterAll
    static void close() { playwright.close(); }
}