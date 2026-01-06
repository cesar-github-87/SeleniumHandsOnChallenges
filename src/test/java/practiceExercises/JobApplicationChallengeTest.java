package practiceExercises;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
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
       // options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().deleteAllCookies();

        driver.get("https://www.cnarios.com/challenges");
        pm = new PageManager(driver);

        pm.challengesPage().goToJobPage();
        driver.manage().window().maximize();

    }


    @Test
    public void JAF_001_subnit_Form_with_Valid_Data(){
        System.out.println("INSIDE");  //kldsafjlaksdfjñlasdkjf
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


    @Test
    public void JAF_002_Attempt_invalid_eMail(){

        /*
        Enter an invalid email and try to submit the form.
        Steps to Execute:
            Enter invalid email 'abc@xyz'
            Fill remaining required fields
            Try submitting the form
            Verify email error helper text is shown
        */

        jp = new JobPage(driver);
        jp.fillPersonaData("Mr.", "Cessar", "Barragan", "abc@xyz", "3167898888", "Male", "English");

        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//p[contains(@id,\"-helper-text\")]")));
        WebElement helper = driver.findElement(By.xpath("//p[contains(@id,\"-helper-text\")]"));
        System.out.println(helper.getText());

        jp.addSkills(new String[]{"skill_1", "skill_2", "skill_3"});
        jp.getRoles();
        jp.enterDate("05-30-2026");
        jp.enterTime("1125p");

        WebElement accept =  driver.findElement(By.xpath("//input[@name='termsAccepted']"));
        accept.click();

        Assert.assertEquals(helper.getText(),"Enter a valid email");

    }

    @Test
    public void JAF_003_Upload_Invalid_Resume(){
        /*
        Select a .jpg file from system upload
        Verify snackbar shows error message
        * */
        jp = new JobPage(driver);

        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        String filePath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "Lucy.jpg").toAbsolutePath().toString();
        System.out.println(filePath);
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        By fileLocator = By.xpath("//input[@type='file']");
        wait.until(ExpectedConditions.presenceOfElementLocated(fileLocator));
        driver.findElement(fileLocator).sendKeys(filePath);

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class,'MuiAlert-standardError')]")));
        WebElement errorMui= driver.findElement(By.xpath("//div[contains(@class,'MuiAlert-standardError')]"));
        System.out.println(errorMui.getText());
        Assert.assertEquals(errorMui.getText(),"Only .pdf or .docx allowed");

    }


    @Test
    public void JAF_004_Add_and_delete_skill_chips(){
        /*
        *   Type 'React' in skills input and press Enter
            Verify 'React' chip appears
            Delete 'React' chip
            Verify chip is removed
        *  */

        jp = new JobPage(driver);
        jp.addSkills(new String[]{"React", "Otra Cosa"});
        By skillChip =  By.xpath("//div[contains(@class,'MuiChip-root')]//span[text()='React']");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(skillChip));
        WebElement chip = driver.findElement(skillChip);

        WebElement deleteChip = driver.findElement(By.xpath("//div[contains(@class,\"MuiChip-root\")]//span[text()='React']/following-sibling::*[local-name()='svg']"));
        deleteChip.click();


        Boolean isGone = wait.until(ExpectedConditions.invisibilityOfElementLocated(skillChip));

        // 4. Aserción
        Assert.assertTrue(isGone, "El chip de React debería haber desaparecido");
       // System.out.println("TEST "+ chip.getText());

    }

    @Test
    public void JAF_005_Preview_form_data(){
        /**
         *Fill in some fields in the form
         * Click Preview button
         * Verify JSON preview matches entered data
         */
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
        driver.findElement(By.xpath("//button[text()='Preview']")).click();
        jp.getPreviewData();


    }


/*
    @AfterMethod
    public void tearDown(Method method) {

        driver.quit();
    }*/




}





