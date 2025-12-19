package pageClasses;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class JobPage {
    WebDriver driver;

    public JobPage(WebDriver driver){
        this.driver=driver;
    }

    public void fillPersonaData(String salut, String fName,  String lName, String eMail, String mobile, String gender, String lang){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By salutLocator = By.xpath("//input[contains (@name, 'salutation')]");
        By fNameLocator =  By.xpath("//input[contains (@name, 'firstName')]");
        By lNameLocator =  By.xpath("//input[contains (@name, 'lastName')]");
        By emailLocator =  By.xpath("//input[contains (@name, 'email')]");
        By genderLocator =  By.xpath("//div[contains (@role, \"radiogroup\")]");
        By langLocator = By.xpath("//input[@type='checkbox']");
        By mobileLocator = By.xpath("//input[@name='mobile']");

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(salutLocator));

        WebElement salutField = driver.findElement(salutLocator);
        salutField.sendKeys(salut);

        WebElement fNameField = driver.findElement(fNameLocator);
        fNameField.sendKeys(fName);

        WebElement lNameField = driver.findElement(lNameLocator);
        lNameField.sendKeys(lName);

        WebElement emailField = driver.findElement(emailLocator);
        emailField.sendKeys(eMail);

        WebElement genderCheckbox = driver.findElement(genderLocator).findElement(By.xpath("//span[contains(text(), '"+gender+"')]"));
        genderCheckbox.click();

        WebElement langCheckbox = driver.findElement(langLocator).findElement(By.xpath("//span[text()='"+lang+"']"));
        langCheckbox.click();

        WebElement mobileField =  driver.findElement(mobileLocator);
        mobileField.sendKeys(mobile);
    }

    public void addSkills(String[] skills) {
        By skillLocattor = By.xpath("//span[contains(., 'Add a Skill')]/ancestor::div[contains(@class, 'MuiInputBase-root')]//input");
        for(String skill : skills ){
            WebElement element = driver.findElement(skillLocattor);
            element.sendKeys(skill);
            element.sendKeys(Keys.ENTER );

        }
    }

    public void getRoles(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By roleLocator = By.xpath("//div[@id='mui-component-select-jobRoles']");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(roleLocator));
        driver.findElement(roleLocator).click();

        By rolesListLocator =  By.xpath("//ul[@role='listbox']//li");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rolesListLocator));
        List<WebElement> rolesList = driver.findElements(rolesListLocator);
        for(WebElement role : rolesList){
           role.click();
        }

        Actions action = new Actions(driver);
        action.sendKeys(Keys.ESCAPE).build().perform();

    }

    public void selectRating(Integer rate){
        // Localizamos el input que React controla
        WebElement sliderInput = driver.findElement(By.cssSelector("input[name='rating']"));

        // El "truco" para que React acepte el cambio de estado inmediatamente
        String jsScript =
                "var input = arguments[0];" +
                        "var value = arguments[1];" +
                        "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "setter.call(input, value);" +
                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('change', { bubbles: true }));";

        ((JavascriptExecutor) driver).executeScript(jsScript, sliderInput, rate);

        System.out.println("Java: Slider actualizado a " + rate + " mediante JS Inject.");


    }

    public void enterDate(String date){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By dateLocator = By.xpath("//input[@type='date']");
        wait.until(ExpectedConditions.presenceOfElementLocated(dateLocator));

        driver.findElement(dateLocator).sendKeys(date);

    }

    public void enterTime(String time){
        By timeLocator =  By.xpath("//input[@type='time']");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(timeLocator));
        driver.findElement(timeLocator).sendKeys(time);
    }
}

