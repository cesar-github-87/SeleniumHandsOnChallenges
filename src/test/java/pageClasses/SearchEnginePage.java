package pageClasses;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SearchEnginePage {
    WebDriver driver;


    //constructor
    public SearchEnginePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy (xpath="//input") WebElement searchBox;
    @FindBy (xpath="//div[contains(@class, 'flex-1')]//button") WebElement searchButton;


    //Functions
    public void fillSearchBar(String searchText){
            searchBox.sendKeys(searchText);
    }

    public void clickSearchbutton(){
        searchButton.click();
    }


}
