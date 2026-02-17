package pages;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateMatchingTest {
    private WebDriver driver;

    @BeforeAll
    static void init() { OpenCV.loadLocally(); }

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    @DisplayName("Recherche du logo Swag Labs par reconnaissance d'image")
    void testFindLogoByImage() throws IOException {
        driver.get("https://www.saucedemo.com");

        // 1. Prendre une capture d'écran de la page entière
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String scenePath = "target/full_page.png";
        Files.copy(screenshotFile.toPath(), new File(scenePath).toPath(), StandardCopyOption.REPLACE_EXISTING);

        // 2. Charger la scène (page) et le template (logo)
        // Note : Assurez-vous d'avoir sauvegardé une petite image du logo sous "logo_ref.png"
        Mat scene = Imgcodecs.imread(scenePath);
        Mat template = Imgcodecs.imread("src/test/resources/logo_ref.png");

        // 3. Appliquer le Template Matching
        Mat result = new Mat();
        Imgproc.matchTemplate(scene, template, result, Imgproc.TM_CCOEFF_NORMED);

        // 4. Localiser le meilleur match
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        double similarity = mmr.maxVal; // Valeur entre 0 et 1

        System.out.println("Taux de ressemblance avec le logo : " + (similarity * 100) + "%");

        // 5. ASSERTION : On accepte le match s'il est > 90%
        assertTrue(similarity > 0.9, "Le logo n'a pas été détecté visuellement sur la page !");

        // 6. BONUS : Cliquer sur le logo trouvé (via ses coordonnées OpenCV)
        int centerX = (int) (mmr.maxLoc.x + template.cols() / 2);
        int centerY = (int) (mmr.maxLoc.y + template.rows() / 2);

        new Actions(driver).moveByOffset(centerX, centerY).click().perform();
    }

    @AfterEach
    void tearDown() { if (driver != null) driver.quit(); }
}
