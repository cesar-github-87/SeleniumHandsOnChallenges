package pageClasses;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ChallengesPage {

    WebDriver driver;


    //constructor
    public ChallengesPage(WebDriver driver){
        this.driver=driver;
        PageFactory.initElements(driver, this);

    }

    //Locators
    @FindBy(xpath="//div[contains(@class, 'MuiCard-root')][contains(.,'Search Engine')] //button") WebElement searchEngine;
    @FindBy(xpath="//div[contains(@class, 'MuiCard-root')][contains(.,'Job Application')] //button") WebElement jobPage;

    public void goToSearchEnginePage(){
        this.searchEngine.click();
    }

    public void goToJobPage(){
        this.jobPage.click();
    }




}
