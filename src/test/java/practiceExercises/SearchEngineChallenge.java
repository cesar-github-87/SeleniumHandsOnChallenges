package practiceExercises;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pageClasses.*;

import java.time.Duration;
import java.util.EventListener;
import java.util.List;
import java.lang.Thread;
import java.util.Map;

public class SearchEngineChallenge {
    ChromeOptions options;
    WebDriver driver;
    PageManager pm;


    @BeforeMethod
    void instantiate(){
        options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        this.driver = new ChromeDriver(options);
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
            System.out.println("Snip: "+ result.findElement(By.xpath(".//p[contains(@class , 'text-slate-70')]")).getText());
            sa.assertTrue(result.findElement(By.xpath(".//p[contains(@class , 'text-slate-700')]")).isDisplayed(), "Snippet not Displayed");
        });



    }

    @Test
    void SSE_002_Attempt_search_with_empty_input(){
        /*
            * Ensure the search input is empty
              Click the search button
              Verify results section remains empty
        */
        this.pm.searchEnginePage().fillSearchBar("");
        this.pm.searchEnginePage().clickSearchbutton();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        List<WebElement> results = driver.findElements(By.xpath("//div[contains(@class, \"w-full\")]//div[contains(@class, \"MuiCard-root\")]"));

        if(results.isEmpty()){
            System.out.println("No results found");
            Assert.assertTrue(true, "Results not available");
        }else{
            System.out.println(" results found");
            Assert.assertFalse(results.get(0).isDisplayed(), "Results section should not be displayed");
        }

    }

    @Test
    void SSE_003_ReUse_old_search_after_reRender() throws InterruptedException {
        /*Locate the search input and enter 'Flights to London'
        Click the search button
        Wait for search results to load (input re-renders)
        Attempt to type into the old input handle
        Observe stale element exception
        Recover by re-locating the search input and enter 'Hotels in Paris'*/

        WebElement field = driver.findElement(By.xpath("//input"));
        field.sendKeys("Flight to London");
        this.pm.searchEnginePage().clickSearchbutton();




        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, \"w-full\")]//div[contains(@class, \"MuiCard-root\")]")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input")));
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys("Flight to Paris");
    }

    @Test
    void SSE_004_Second_Valid_Search(){

    }


    @AfterMethod
    void tearDown(){
        driver.quit();
    }








}
