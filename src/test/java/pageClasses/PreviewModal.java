package pageClasses;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewModal extends JobPage
{
    private By salutationLocator = By.xpath("//div[@aria-modal='true']//p[strong='Salutation:']");
    private By nameLocator = By.xpath("//div[@aria-modal='true']//p[strong='Name:']");
    private By eMailLocator = By.xpath("//div[@aria-modal='true']//p[strong='Email:']");
    private By mobileLocator = By.xpath("//div[@aria-modal='true']//p[strong='Mobile:']");
    private By genderLocator = By.xpath("//div[@aria-modal='true']//p[strong='Gender:']");
    private By languagesLocator = By.xpath("//div[@aria-modal='true']//p[strong='Languages:']");
    private By dateLocator = By.xpath("//div[@aria-modal='true']//p[strong='Date:']");
    private By timeLocator = By.xpath("//div[@aria-modal='true']//p[strong='Time:']");

    private By skillsLocator = By.xpath("//div[@aria-modal='true']//h6[text()='Skills']//following-sibling::div[1]//span");
    private By jobRolesLocator = By.xpath("//div[@aria-modal='true']//h6[text()='Job Roles']//following-sibling::div[1]//span");
    private By selfRatingLocator = By.xpath("//div[@aria-modal='true']//h6[text()='Self Rating']//following-sibling::p[1]");
    private By termsLocator = By.xpath("//div[@aria-modal='true']//h6[text()='Terms & Conditions']//following-sibling::p[1]");

    public PreviewModal(WebDriver driver)
    {
        super(driver);
    }

    public String getSalutation()
    {
        return getValue(getText(salutationLocator));
    }

    public String getName()
    {
        return getValue(getText(nameLocator));
    }

    public String getEmail()
    {
        return getValue(getText(eMailLocator));
    }

    public String getMobile()
    {
        return getValue(getText(mobileLocator));
    }

    public String getGender()
    {
        return getValue(getText(genderLocator));
    }

    public String getLanguage()
    {
        return getValue(getText(languagesLocator));
    }

    public String getDate()
    {
        return getValue(getText(dateLocator));
    }

    public String getTime()
    {
        return getValue(getText(timeLocator));
    }

    public String getTerms()
    {
        return getText(termsLocator);
    }

    public String getSelfRating()
    {
        return getText(selfRatingLocator);
    }

    public List<WebElement> getSkills()
    {
        return driver.findElements(skillsLocator);
    }

    public List<WebElement> getJobRoles()
    {
        return driver.findElements(jobRolesLocator);
    }

    private String getValue(String text)
    {
        return text.substring(text.indexOf(':') + 1).trim();
    }
}
