package practiceExercises;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageClasses.JobPage;
import pageClasses.PageManager;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.Duration;
//import pageClasses.SocialMediaPage;

public class JobApplicationChallengeTest {
    ChromeOptions options;
    WebDriver driver;
    PageManager pm;
    JobPage jp;
    WebDriverWait wait;



    @BeforeMethod
    public void instantiate(){
        options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().deleteAllCookies();

        driver.get("https://www.cnarios.com/challenges");
        pm = new PageManager(driver);

        pm.challengesPage().goToJobPage();

    }


    @Test
    public void JAF_001_subnit_Form_with_Valid_Data(){
        System.out.println("INSIDE");
        /*
        Enter valid salutation, first name, last name, email, and mobile
        Select gender and languages
        Upload a valid .pdf resume
        Add skills using Enter key
        Select multiple job roles
        Set rating slider to 7
        Pick valid date and time
        Check 'I accept terms' checkbox
        Click Submit
        Verify success snackbar message
        * */
        jp = new JobPage(driver);
        jp.fillPersonaData("MR","Cesar","Barragan","cesar.bh87@gmail.com","1234567890","Male","English");


        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        String filePath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "Cesar_Barragan_Resume_2025-11.pdf").toAbsolutePath().toString();
        System.out.println(filePath);

        By fileLocator = By.xpath("//input[@type='file']");
        driver.findElement(fileLocator).sendKeys(filePath);

        jp.addSkills(new String[]{"hello", "this is", "an array"});

        jp.getRoles();
        jp.selectRating(8);
        jp.enterDate("05-30-2026");
        jp.enterTime("1125p");

        WebElement accept =  driver.findElement(By.xpath("//input[@name='termsAccepted']"));
        accept.click();

        WebElement submit =  driver.findElement(By.xpath("//button[text()='Submit']"));
        submit.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By messageLocator = By.xpath("//div[@role=\"presentation\"]//div[contains(@class,\"MuiAlert-message\")]") ;
        wait.until(ExpectedConditions.presenceOfElementLocated(messageLocator));

        WebElement message = driver.findElement(messageLocator);
        System.out.println("TEST "+message.getText());

        Assert.assertEquals(message.getText(),"Application Submitted Successfully!");



    }

  /*  @AfterMethod
    void tearDown(Method method){


        driver.quit();
    }*/



}
