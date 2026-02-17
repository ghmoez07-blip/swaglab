package pages;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class checkoutpage {
    WebDriver driver;
    WebDriverWait wait;

    public checkoutpage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @FindBy(id = "checkout")
    WebElement checkoutButton;

    @FindBy(id = "first-name")
    WebElement firstNameField;

    @FindBy(id = "last-name")
    WebElement lastNameField;

    @FindBy(id = "postal-code")
    WebElement postalCodeField;

    @FindBy(id = "continue")
    WebElement continueButton;

    @FindBy(id = "finish")
    WebElement finishButton;

    public void clickChekout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
    }

    public void setFirstName(String name) {
        // CORRECTION : Attente explicite de visibilité avant d'écrire
        wait.until(ExpectedConditions.visibilityOf(firstNameField)).sendKeys(name);
    }

    public void setLastName(String name) {
        lastNameField.sendKeys(name);
    }

    public void setPostalCode(String code) {
        postalCodeField.sendKeys(code);
    }

    public void clickContinueButton() {
        continueButton.click();
    }

    public void clickFinishButton() {
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
    }
}