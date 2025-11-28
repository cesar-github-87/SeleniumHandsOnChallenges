package pageClasses;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SocialMediaPage {
    WebDriver driver;
    WebDriverWait wait;

    //Constructor
    public SocialMediaPage(WebDriver driver){

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);

    }

    //Locators
    @FindAll({@FindBy(xpath="//div[contains(@class, 'flex')]//div[contains(@class, 'MuiPaper-root')]")}) List<WebElement> loc_posts;


    public List<Map<String,WebElement>> getAllPosts(){

        List<Map<String,WebElement>> posts = new ArrayList<>();

        for(WebElement post:loc_posts){
            Map<String, WebElement> entries =  new HashMap<>();
            entries.put("user", post.findElement(By.xpath(".//h6")));
            entries.put("likesText",  post.findElement(By.xpath(".//p[contains(@class, 'MuiTypography-body1')]")));
            entries.put("likeButton", post.findElement(By.xpath(".//button")));

            posts.add(entries);
            //System.out.println(entries.get("user").getText());
        }

        return posts;
    }


    public void waitForPostsToLoad(){
        //WebElement posts = driver.findElement(By.xpath("//div[contains(@class, 'flex')]//div[contains(@class, 'MuiPaper-root')]"));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'flex')]//div[contains(@class, 'MuiPaper-root')]")));
        wait.until(driver -> {
            List<WebElement> posts = driver.findElements((By.xpath("//div[contains(@class, 'flex')]//div[contains(@class, 'MuiPaper-root')]")));
            return !posts.isEmpty() && posts.get(0).isDisplayed();
        });
        wait.until(ExpectedConditions.elementToBeClickable(getAllPosts().getFirst().get("likeButton")));

    }

    public WebElement getNoticationBell(){
            return driver.findElement(By.xpath("//div[contains(@class,'pr-6')]//button"));

    }

    public WebElement getNotificationCounter(){
            return this.getNoticationBell().findElement(By.xpath(".//span[contains(@class, 'MuiBadge-badge')]"));
    }

    public WebElement getNotificationText(){
            return this.driver.findElement(By.xpath("//div[contains(@class, 'shadow-lg')]"));
    }


   // public Map<String, >



}
