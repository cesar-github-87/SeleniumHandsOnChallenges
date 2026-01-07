package pageClasses;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By skillLocattor = By.xpath("//span[contains(., 'Add a Skill')]/ancestor::div[contains(@class, 'MuiInputBase-root')]//input");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(skillLocattor));

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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        By thumbLocator = By.xpath("//span[contains(@class,'MuiSlider-thumb')]");
        WebElement thumb = driver.findElement(thumbLocator);
        wait.until(ExpectedConditions.elementToBeClickable(thumb));

        Actions actions = new Actions(driver);

        WebElement slider = driver.findElement(By.xpath("//span[contains(@class,'MuiSlider-rail')]"));
        int width = slider.getSize().width;

        thumb.click();
        actions.dragAndDropBy(thumb,(width/10*rate),0).build().perform();


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

    public Map<String, String> getPreviewData() {

        Map<String, String> data = new HashMap<>();
        List<WebElement> singleRows = driver.findElements(By.xpath("//div[contains(@class,'MuiDialogContent-root')]//p[contains(@class, 'MuiTypography-body1')]"));

        for (WebElement row : singleRows) {
            // El texto viene como "Name: cesar c". Lo separamos.
            String fullText = row.getText();
            if (fullText.contains(":")) {
                String[] parts = fullText.split(":", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                data.put(key, value);

            }else{
                if(row.getText().contains(" / 10")){
                    String key = "Rating";
                    String value = row.getText().split(" / 10")[0].trim();
                    data.put(key, value);

                }if(row.getText().contains("Accepted")){
                    String key = "Terms";
                    String value = row.getText().trim();
                    data.put(key, value);
                }
                //testing organization commits on intellij
            }

        }
        //Added this to test sourcetree
        data.put("Skills", getChipsText("Skills"));
        data.put("Job Roles", getChipsText("Job Roles"));

        return data;
    }

    //Método auxiliar para obtener texto de los Chips basado en el título de la sección
    private String getChipsText(String sectionTitle) {
        // XPath avanzado: Busca el título h6, luego el siguiente div hermano, y dentro los chips

        String xpath = String.format("//div[@role='dialog']//h6[text()='%s']/following-sibling::div[1]//span[contains(@class, 'MuiChip-label')]", sectionTitle);

        List<WebElement> chips = driver.findElements(By.xpath(xpath));
        List<String> chipTexts = new ArrayList<>();

        for (WebElement chip : chips) {
            chipTexts.add(chip.getText());
        }

        // Retornamos como string unido por comas para facilitar la comparación, o devuelve una List si prefieres
        return String.join(", ", chipTexts);
    }




}

