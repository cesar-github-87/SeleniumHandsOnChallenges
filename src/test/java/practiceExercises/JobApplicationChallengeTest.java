package practiceExercises;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


        // 1. Crear mapa de preferencias
        Map<String, Object> prefs = new HashMap<>();
        // "0" significa desactivar el popup. Chrome guardará automáticamente.
        prefs.put("profile.default_content_settings.popups", 0);
        // CRUCIAL: Le dices explícitamente "No me preguntes dónde guardar"
        prefs.put("download.prompt_for_download", false);
        // Define una ruta segura (aunque uses el truco de chrome://downloads, Chrome necesita una carpeta física)
        String downloadPath = Paths.get(System.getProperty("user.dir"), "target", "downloads").toString();
        System.out.println("Download Path: " + downloadPath);
        prefs.put("download.default_directory", downloadPath);
        // Evita que bloquee archivos "peligrosos" (xml, exe, jar) que pausan la descarga
        prefs.put("safebrowsing.enabled", true);
        // Inyectar las preferencias

        // options.addArguments("--headless=new");
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
        String name = "Cesar";
        String lName =  "Barragan";
        String salut = "Mr.";
        String email = "cesar.bh87@gmail.com";
        String phone = "1234567890";
        String gender = "Male";
        String lang = "English";



        jp = new JobPage(driver);
        jp.fillPersonaData(salut,name,lName,email,phone,gender,lang);
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
        System.out.println(jp.getPreviewData());

        Assert.assertEquals(jp.getPreviewData().get("Email"), email);
        Assert.assertEquals(jp.getPreviewData().get("Name"), name+" "+lName);
        Assert.assertEquals(jp.getPreviewData().get("Salutation"), salut);
        Assert.assertEquals(jp.getPreviewData().get("Time"), "23:25");

    }

    @Test
    public void JAF_006_Clear_All_Form_Fields(){
        /*
        * Fill some fields in the form
          Click Clear button
          Verify all fields reset to defaults
        * */
        jp = new JobPage(driver);

        String name = "Cesar";
        String lName =  "Barragan";
        String salut = "Mr.";
        String email = "cesar.bh87@gmail.com";
        String phone = "1234567890";
        String gender = "Male";
        String lang = "English";

        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        String filePath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "Cesar_Barragan_Resume_2025-11.pdf").toAbsolutePath().toString();

        By fileLocator = By.xpath("//input[@type='file']");
        Actions action = new Actions(driver);
        action.moveToElement(driver.findElement(fileLocator)).perform();
        driver.findElement(fileLocator).sendKeys(filePath);


        jp.fillPersonaData(salut,name,lName,email,phone,gender,lang);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By resumeLoc =  By.xpath("//p[text()=\"Resume:\"]");

        driver.findElement(By.xpath("//button[text()='Clear']")).click();

        Boolean noResume = wait.until(ExpectedConditions.invisibilityOfElementLocated(resumeLoc));
        Assert.assertTrue(noResume);

        Assert.assertNull(jp.getPreviewData().get("Email"));
        Assert.assertNull(jp.getPreviewData().get("Name"));
        Assert.assertNull(jp.getPreviewData().get("Salutation"));
        Assert.assertNull(jp.getPreviewData().get("Time"));


    }

    @Test
    public void JAF_007_Download_JSON() throws InterruptedException {
        /*
        Fill first name and last name fields
        Click Download button
        Verify file downloads as 'FirstName.LastName.json'
        * */
        String name = "Cesar";
        String lName =  "Barragan";

        jp = new JobPage(driver);
        jp.fillPersonaData("", name, lName, "cesr@dsfa.com","","","");


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement download = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Download']")));


        //Thread.sleep(500);
        download.click();
        download.click();


    }

    /*
    @Test
    public void newTest() throws InterruptedException {
        driver.get("https://amazon.com.mx");
        Thread.sleep(35000);
        Actions action = new Actions(driver);


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By historial = By.xpath("//div[@class=\"a-carousel-row-inner\"]//div[@class=\"a-section a-spacing-mini\"]");

        do{
            action.sendKeys(Keys.END).pause(Duration.ofMillis(1500)).perform();
            action.moveToElement()

        }while(!isElementDisplayed(historial, wait));

        List<WebElement> hist = driver.findElements(By.xpath("//div[@class=\"a-carousel-row-inner\"]//div[@class=\"a-section a-spacing-mini\"]"));
        for(WebElement h:hist){
            System.out.println("here");
            System.out.println(h.getAttribute("alt"));
        }
/*


    }

    public boolean isElementDisplayed(By locator, WebDriverWait wait)
    {

        try
        {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        }
        catch(TimeoutException e)
        {
            return false;
        }
    }
*/

/*
    @AfterMethod
    public void tearDown(Method method) {

        driver.quit();
    }*/





}





