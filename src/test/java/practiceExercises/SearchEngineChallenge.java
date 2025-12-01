package practiceExercises;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pageClasses.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SearchEngineChallenge {
    WebDriver driver;
    PageManager pm;


    @BeforeTest
    void instantiate(){
        this.driver = new ChromeDriver();
        driver.get("https://www.cnarios.com/challenges");
        this.pm =  new PageManager(driver);
        pm.challengesPage().goToSearchEnginePage();

    }

    @Test
    void SSE_001_Perform_Valid_Search(){
        /*
        *   Locate the search input and type 'React Testing'
            Click the search button
            Verify that at least 3 results appear
            Validate each result includes a clickable title, URL, and snippet
        *
        */

        this.pm.searchEnginePage().fillSearchBar("React Testing");
        this.pm.searchEnginePage().clickSearchbutton();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        List<WebElement> results = driver.findElements(By.xpath("//div[contains(@class, \"w-full\")]//div[contains(@class, \"MuiCard-root\")]"));
        System.out.println("Returned search results: "+results.size());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        Assert.assertTrue(results.size()>=3);

        SoftAssert sa = new SoftAssert();
        results.forEach(result->{
            System.out.println("Element: " + result.findElement(By.xpath(".//a")).getText());
            sa.assertNotNull(result.findElement(By.xpath(".//a")).getAttribute("href"),"Title is not clickable, href attribute non existing");
            sa.assertTrue(result.findElement(By.xpath(".//a")).isEnabled(),"Link not clickable - Disabled");
            sa.assertTrue(result.findElement(By.xpath(".//p[contains(@class , 'text-green-700')]")).getText().contains("https://www.cnarios.com"), "No URL page");
            sa.assertTrue(result.findElement(By.xpath(".//p[contains(@class , 'text-slate-70')]")).isDisplayed(), "Snippet not Displayed");
        });

        //Assert.assertEquals(results.size(), 3);


    }









}
