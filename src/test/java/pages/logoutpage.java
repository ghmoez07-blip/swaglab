package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class logoutpage {
    WebDriver driver;
    WebDriverWait wait;

    public logoutpage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @FindBy(id = "react-burger-menu-btn")
    WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    WebElement logoutLink;

    public void clickmenuButton() {
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
    }

    public void clickLogoutButton() {
        // CORRECTION : Attendre que le lien soit VISIBLE après l'animation du menu
        wait.until(ExpectedConditions.visibilityOf(logoutLink));
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }
}