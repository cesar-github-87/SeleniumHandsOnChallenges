package pageClasses;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SearchEnginePage {
    WebDriver driver;

    private By searchBox = By.xpath("//input");
    @FindBy (xpath="//div[contains(@class, 'flex-1')]//button") WebElement searchButton;

    //constructor
    public SearchEnginePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }




    //Functions
    public void fillSearchBar(String searchText){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // CORRECCIÓN: Usar ExpectedConditions para encontrar el elemento en tiempo real
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(searchBox));

        // Limpiar y luego escribir
        element.clear();
        element.sendKeys(searchText);
    }

    public void clickSearchbutton(){
        searchButton.click();
    }


}
