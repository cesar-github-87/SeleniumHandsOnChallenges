package org.example;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.cnarios.com/challenges/social-media-feed#challenge");

        //driver.wait(1500);
        //WebElement posts = driver.findElement(By.xpath())
        List<WebElement> posts = driver.findElements(By.xpath("//div[contains(@class, 'flex')]//div[contains(@class, 'MuiPaper-root')]"
        ));
        System.out.println(posts.size());
        for(WebElement post:posts){
            System.out.println("Inside");
            try {
                WebElement botons = post.findElement(By.xpath(".//button[contains(@class, 'MuiButtonBase-root')]"));
                botons.click();

            }catch(Exception e){

                    System.out.println("Failed to click button: " + e.getMessage());
            }

        }
    //driver.close();

    }
}