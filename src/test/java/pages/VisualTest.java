package pages;

import io.github.bonigarcia.wdm.WebDriverManager;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisualTest {
    private WebDriver driver;

    @BeforeAll
    static void initOpenCV() {
        //OpenCV.loadShared(); // Initialisation d'OpenCV une seule fois
        // Plus propre pour Java 17
        nu.pattern.OpenCV.loadLocally();
    }

    @BeforeEach
    void setup() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Connexion rapide
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    @DisplayName("Vérifier que le bouton Remove est rouge via OpenCV")
    void testButtonColor() throws IOException {
        // 1. Ajouter un produit pour faire apparaître le bouton "Remove"
        WebElement btn = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        btn.click();

        // 2. Capturer l'élément "Remove" en image
        WebElement removeBtn = driver.findElement(By.id("remove-sauce-labs-backpack"));
        File screenshot = removeBtn.getScreenshotAs(OutputType.FILE);
        String path = "target/button_remove.png";
        org.apache.commons.io.FileUtils.copyFile(screenshot, new File(path));

        // 3. Charger l'image avec OpenCV
        Mat image = Imgcodecs.imread(path);

        // 4. Analyser le pixel central (Attention : OpenCV utilise le format BGR, pas RGB)
        double[] bgr = image.get(image.rows() / 2, image.cols() / 2);
        double blue = bgr[0];
        double green = bgr[1];
        double red = bgr[2];

        System.out.println("Couleur détectée (BGR) : B=" + blue + " G=" + green + " R=" + red);

        // 5. ASSERTION : Le rouge doit être dominant (R > 150, B et G faibles)
        assertTrue(red > 150 && green < 100, "Le bouton devrait être à dominante rouge !");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) { // Vérification de sécurité
            driver.quit();
        }
    }
}