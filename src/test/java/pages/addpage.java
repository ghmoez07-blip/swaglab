package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class addpage {

    WebDriver driver;
    WebDriverWait wait;


    public addpage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @FindBy(xpath = "//*[@id='add-to-cart-sauce-labs-backpack']")
    WebElement addButton;
    public void clickAddToCartButton() {

        addButton.click();
    }

    @FindBy(className = "shopping_cart_link")
    WebElement panierButton;

    public void clickPanierButton() {
        wait.until(ExpectedConditions.elementToBeClickable(panierButton)).click();
    }

    @FindBy(xpath = "//*[@id='item_4_title_link']/div")
    WebElement productTitleBefore;



    @FindBy(xpath = "//*[@id='item_4_title_link']/div")
    WebElement productTitleInCart;

    public void verifyProductAdded() {
        // Attente pour que le produit soit visible
        wait.until(ExpectedConditions.visibilityOf(productTitleBefore));
        String text1 = productTitleBefore.getText();

        // Click sur le panier
        //wait.until(ExpectedConditions.elementToBeClickable(panierButton)).click();

        // Attente pour que le produit dans le panier soit visible
        wait.until(ExpectedConditions.visibilityOf(productTitleInCart));
        String text2 = productTitleInCart.getText();

        if (text1.equals(text2)) {
            System.out.println("✅ add successful!");
        } else {
            System.out.println("❌ add failed!");
        }
    }

    @FindBy(xpath = "//*[@id=\"remove-sauce-labs-backpack\"]")
    WebElement suppButton;
    public void clickSuppButton() {

        suppButton.click();
    }

    public Boolean isProductRemoved() {
        return wait.until(ExpectedConditions.invisibilityOf(productTitleInCart));


    }

}
