package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class loginpage {

    WebDriver driver;

    public loginpage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id='user-name']")
    WebElement userName;

    public void setUsername(String userName) {
        this.userName.sendKeys(userName);
        PageFactory.initElements(driver, this);

    }


    @FindBy(xpath = "//*[@id='password']")
    WebElement setPassword;
    public void setPassword(String setPassword) {
        this.setPassword.sendKeys(setPassword);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id='login-button']")
    public WebElement loginButton;
    public void clickLoginButton() {

        loginButton.click();
    }
}
