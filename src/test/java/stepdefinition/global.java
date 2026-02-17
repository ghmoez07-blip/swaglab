package stepdefinition;

import com.epam.healenium.SelfHealingDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.*;
import org.junit.Assert; // Pour les validations

import java.time.Duration;

public class global {
    WebDriver driver;
    WebDriverWait wait;
    loginpage loginpage;
    addpage addpage;
    checkoutpage checkoutpage;
    logoutpage logoutpage;

    @Given("je suis dans le site swaglab")
    public void je_suis_dans_le_site_swaglab() {
        WebDriver delegate = new ChromeDriver();
// On utilise .create() au lieu de .delegate()
        SelfHealingDriver driver = SelfHealingDriver.create(delegate);
        driver.manage().window().maximize();
        driver.navigate().to("https://www.saucedemo.com/");

        // Initialisation des attentes et des pages
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        loginpage = new loginpage(driver);
        addpage = new addpage(driver);
        checkoutpage = new checkoutpage(driver);
        logoutpage = new logoutpage(driver);
    }

    @When("je saisie username {string}")
    public void jeSaisieUsername(String user) {
        loginpage.setUsername(user);
    }

    @And("je saisie password {string}")
    public void jeSaisiePassword(String pass) {
        loginpage.setPassword(pass);
    }

    @And("je clique sur le bouton login")
    public void jeCliqueSurLeBoutonLogin() {
        loginpage.clickLoginButton();
    }

    @Then("redirection vers la page Home et verifier alert")
    public void redirectionVersLaPageHomeEtVerifierAlert() {
        // CORRECTION : Supprimé wait.until(ExpectedConditions.alertIsPresent()) car il n'y a pas d'alerte.

        // On vérifie qu'on est sur la bonne URL
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        WebElement topMenu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='title']"))
        );

        Assert.assertTrue("Le titre 'Products' n'est pas affiché", topMenu.getText().contains("Products"));
        System.out.println("✅ Login successful!");
    }

    @When("je clique Add to cart")
    public void jeCliqueAddToCart() {
        addpage.clickAddToCartButton();
        // On ne clique PAS sur le panier ici si on veut juste vérifier l'ajout
    }

    @Then("ajout succes")
    public void ajoutSucces() {
        addpage.clickPanierButton(); // On va dans le panier pour vérifier
        addpage.verifyProductAdded();
    }

    @When("je clique chekout")
    public void jeCliqueChekout() {
        // 1. CLIQUER SUR LE PANIER D'ABORD (Action indispensable)
        addpage.clickPanierButton();

        // 2. ATTENDRE QUE LA PAGE DU PANIER CHARGE
        wait.until(ExpectedConditions.urlContains("cart.html"));

        // 3. MAINTENANT CLIQUER SUR CHECKOUT
        // Utilisons le même réflexe que pour le logout : wait + clic
        wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))).click();
    }

    @And("je saisie firstName {string}")
    public void jeSaisieFirstName(String name) {
        checkoutpage.setFirstName(name);
    }

    @And("je saisie lastName {string}")
    public void jeSaisieLastName(String last) {
        checkoutpage.setLastName(last);
    }

    @And("je saisie code poste {string}")
    public void jeSaisieCodePoste(String code) {
        checkoutpage.setPostalCode(code);
    }

    @And("je clique continue")
    public void jeCliqueContinue() {
        checkoutpage.clickContinueButton();
    }

    @And("je clique Finish")
    public void jeCliqueFinish() {
        checkoutpage.clickFinishButton();
    }

    @Then("checkout succes")
    public void checkoutSucces() {
        wait.until(ExpectedConditions.urlContains("checkout-complete.html"));
        System.out.println("✅ Checkout completed successfully!");
    }

    @When("je clique menu")
    public void jeCliqueMenu() {
        // CORRECTION : Supprimé l'alerte inexistante
        logoutpage.clickmenuButton();
    }

    @And("je clique logout")
    public void jeCliqueLogout() {
        // 1. On attend quand même un peu que l'élément soit là
        WebElement logoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logout_sidebar_link")));

        // 2. On utilise JavaScript pour forcer le clic
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", logoutBtn);

        System.out.println("✅ Logout forcé avec succès !");
    }

    @Then("logout succes")
    public void logoutSucces() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("index.html"),
                ExpectedConditions.urlToBe("https://www.saucedemo.com/")
        ));        System.out.println("✅ Logout successful!");
        driver.quit(); // Fermeture propre
    }

    @And("je clique remove")
    public void jeCliqueRemove() {
        // 1. Cliquer sur l'icône du panier pour changer de page
        addpage.clickPanierButton();

        // 2. Attendre que la page change réellement
        wait.until(ExpectedConditions.urlContains("cart.html"));

        // 3. Cliquer sur le bouton rouge "Remove"
        addpage.clickSuppButton();
    }
    @Then("produit supp")
    public void produitSupp() {
        Assert.assertTrue("Le produit n'a pas été supprimé", addpage.isProductRemoved());
        System.out.println("✅ Product removed successfully!");
        driver.quit();
    }
}

