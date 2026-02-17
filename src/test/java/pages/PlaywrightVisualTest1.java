package pages;

import com.microsoft.playwright.*;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlaywrightVisualTest1 {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void init() {
        OpenCV.loadLocally(); // 1. OpenCV : Initialisation du moteur
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setup() {
        // Standardisation de l'affichage pour la comparaison d'images
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720)
                .setDeviceScaleFactor(1));
        page = context.newPage();
    }

    /**
     * POINT DU COMPTE RENDU : Détection d'objets et vérification graphique
     */
    @Test
    @DisplayName("Vérification visuelle du bouton Login et du Logo")
    void validateUIElementsVisually() {
        page.navigate("https://www.saucedemo.com");

        // A. CHARGEMENT ET COMPARAISON (Point 1)
        // On cherche le bouton Login via son image de référence
        boolean isLoginBtnPresent = verifyElementVisually("btn_login.png", 0.9);
        assertTrue(isLoginBtnPresent, "Le bouton Login est graphiquement incorrect ou absent !");

        // B. DÉTECTION ET INTERACTION (Point 2)
        // On détecte le logo et on récupère sa position pour cliquer dessus
        Point logoLocation = getElementCoordinates("logo_ref.png");
        if (logoLocation != null) {
            page.mouse().click(logoLocation.x, logoLocation.y);
            System.out.println("✅ Objet détecté et cliqué aux coordonnées : " + logoLocation);
        }
    }

    // --- MÉTHODES OUTILS (Le moteur de ton projet) ---

    private boolean verifyElementVisually(String templateName, double threshold) {
        String screenshotPath = "target/current_screen.png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));

        Mat scene = Imgcodecs.imread(screenshotPath);
        Mat template = Imgcodecs.imread("src/test/resources/templates/" + templateName);

        if (template.empty()) return false;

        Mat result = new Mat();
        Imgproc.matchTemplate(scene, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        System.out.println("🔍 Comparaison [" + templateName + "] : " + (mmr.maxVal * 100) + "%");
        return mmr.maxVal >= threshold;
    }

    private Point getElementCoordinates(String templateName) {
        String screenshotPath = "target/current_screen.png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));

        Mat scene = Imgcodecs.imread(screenshotPath);
        Mat template = Imgcodecs.imread("src/test/resources/templates/" + templateName);

        Mat result = new Mat();
        Imgproc.matchTemplate(scene, template, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        if (mmr.maxVal >= 0.9) {
            // Calcul du centre de l'élément détecté
            double x = mmr.maxLoc.x + (template.cols() / 2.0);
            double y = mmr.maxLoc.y + (template.rows() / 2.0);
            return new Point(x, y);
        }
        return null;
    }

    @AfterEach
    void tearDown() { context.close(); }

    @AfterAll
    static void close() { playwright.close(); }
/*
    @Test
    void generatePerfectTemplates() {
        page.navigate("https://www.saucedemo.com");

        // On attend que les éléments soient bien chargés
        page.waitForSelector("#login-button");

        // On capture EXACTEMENT ce que Playwright voit pour le bouton
        page.locator("#login-button").screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get("src/test/resources/templates/btn_login.png")));

        // On capture EXACTEMENT ce que Playwright voit pour le logo
        page.locator(".login_logo").screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get("src/test/resources/templates/logo_ref.png")));

        System.out.println("✅ Nouveaux templates générés dans src/test/resources/templates/");
    }*/
private void saveDetectionEvidence(Mat scene, Core.MinMaxLocResult mmr, Mat template, String outputName) {
    // On définit le rectangle autour de la zone trouvée
    Rect rect = new Rect((int) mmr.maxLoc.x, (int) mmr.maxLoc.y, template.cols(), template.rows());

    // On dessine le rectangle (Couleur BGR : Rouge = 0,0,255)
    Imgproc.rectangle(scene, rect.tl(), rect.br(), new Scalar(0, 0, 255), 3);

    // On ajoute le score sur l'image
    Imgproc.putText(scene, "Match: " + Math.round(mmr.maxVal * 100) + "%",
            new Point(rect.x, rect.y - 10),
            Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(0, 0, 255), 2);

    // On sauvegarde l'image de preuve
    Imgcodecs.imwrite("target/EVIDENCE_" + outputName, scene);
    System.out.println("📸 Preuve visuelle enregistrée : target/EVIDENCE_" + outputName);
}
}