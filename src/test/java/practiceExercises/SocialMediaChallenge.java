package practiceExercises;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageClasses.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;


public class SocialMediaChallenge {
    ChromeOptions options;
    WebDriver driver;
    SocialMediaPage smp;
    WebDriverWait wait;


   @BeforeMethod
   void instantiate(){
       options = new ChromeOptions();
       options.addArguments("--headless");
       options.addArguments("--no-sandbox");
       options.addArguments("--disable-dev-shm-usage");
       options.addArguments("--window-size=1920,1080");
       driver = new ChromeDriver(options);

       driver.manage().deleteAllCookies();


       driver.get("https://www.cnarios.com/challenges/social-media-feed#challenge");
       // 1. Limpia LocalStorage para eliminar el estado persistente de los "likes"
       JavascriptExecutor js = (JavascriptExecutor) driver;
       js.executeScript("window.localStorage.clear();"); // <-- ¡Esta línea es clave!
       //driver.manage().window().maximize();
       driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
       smp = new SocialMediaPage(driver);
   }


   @Test
    void likePostTest(){
       /*
       *
       * Description:
        Click the like button on a post and confirm the like count increases and icon is filled

        Steps to Execute:
        Locate a post by user name
        Click the like button (outlined heart)
        Verify like count increases by 1
        Verify heart icon changes to filled state*/


       //SocialMediaPage smp = new SocialMediaPage(driver);

       List<Map<String,WebElement>> posts = smp.getAllPosts();


       for(Map<String, WebElement> element:posts){

           String likes = element.get("likesText").getText();
           Integer previousLikes = Integer.parseInt(likes.replace("likes", "").trim());
           System.out.println("LOS PUROS likes: "+previousLikes);

           if(element.get("user").getText().equals("Liam")){
               element.get("likeButton").click();
               Integer newLike = Integer.parseInt(element.get("likesText").getText().replace("likes", "").trim());
               System.out.println("AFTER LIKE :"+ newLike);
               Assert.assertEquals(newLike, previousLikes+1);
               System.out.println(element.get("likeButton").findElement(By.cssSelector("svg")).getAttribute("class"));
               Assert.assertTrue(element.get("likeButton").findElement(By.cssSelector("svg")).getAttribute("class").contains("MuiSvgIcon-colorError"));
           }
       }

  //  driver.close();

    }


    @Test
    void unlikePostTest(){
        /*
         *
         * Test Steps & Details
         * Description:
         * Click the like button again to unlike a post and confirm count decreases
         *
         * Steps to Execute:
         * Locate a post that is already liked
         * Click the filled heart icon
         * Verify like count decreases by 1
         * Verify heart icon returns to outlined state
         */

        //Click on all posts
        //smp = new SocialMediaPage(driver);

        smp.waitForPostsToLoad();


        List<Map<String, WebElement>> posts =  smp.getAllPosts();

        for(Map<String, WebElement> element:posts){
            element.get("likeButton").click();
        }

        //verify whether the third post is liked or not
        WebElement thirdPost = driver.findElement(By.xpath("(//div[contains(@class, 'flex')]//div[contains(@class, 'MuiPaper-root')]//button)[3]"));
        String isLiked = thirdPost.findElement(By.cssSelector("svg")).getAttribute("class");
        Assert.assertTrue(isLiked.contains("MuiSvgIcon-colorError"));


        //get the amount of likes on the second post
        posts = smp.getAllPosts();
        Integer beforeDislikeCount = Integer.parseInt(posts.get(1).get("likesText").getText().replace("likes", "").trim());

        //dislike secondPost
        posts.get(1).get("likeButton").click();
        Integer afterDislikeCount = Integer.parseInt(posts.get(1).get("likesText").getText().replace("likes", "").trim());

        //Verify count after dislike

        Assert.assertEquals(afterDislikeCount, beforeDislikeCount-1, "not equal");

        //Verify Heart icon is no longer filled
        Assert.assertFalse(posts.get(1).get("likeButton").findElement(By.cssSelector("svg")).getAttribute("class").contains("MuiSvgIcon-colorError"),"Heart appears to be filled");
       // driver.close();
    }

    @Test
    void smf003GenerateNotification() throws InterruptedException {
        WebElement windowTop = driver.findElement(By.xpath("//div[contains(@class,'MuiBox-root')]//h6[contains(text(), 'Background')]"));
        List <Map<String, WebElement>> posts = smp.getAllPosts();

        //LIKE,  CHECK LIKE HEART, AND NOTIFICATION COUNTER IS 1
        posts.get(1).get("likeButton").click();
        String heartColor = posts.get(1).get("likeButton").findElement(By.cssSelector("svg")).getCssValue(("color"));
        System.out.println("Filled heart color: "+ heartColor);
        Assert.assertTrue(heartColor.contains("211, 47, 47"));
        Assert.assertTrue(posts.get(1).get("likeButton").findElement(By.cssSelector("svg")).getAttribute("class").contains("MuiSvgIcon-colorError"),"Heart appears to be unfilled");

        WebElement notifBell = smp.getNoticationBell();
       // WebElement notifCount = smp.getNotificationCounter();

        Assert.assertEquals(Integer.parseInt(smp.getNotificationCounter().getText()),1,"not equal");


        /*Actions actions = new Actions(driver);
        actions.moveToElement(windowTop).perform();*/



        //DISLIKE AND CHECK LIKE HEART COLOR AND VERIFY COUNTER EQUALS 2
        posts.get(1).get("likeButton").click();
        heartColor = posts.get(1).get("likeButton").findElement(By.cssSelector("svg")).getCssValue(("color"));
        System.out.println("Unfilled heart color: "+ heartColor);
        Assert.assertTrue(heartColor.contains("0, 0, 0"));
        Assert.assertFalse(posts.get(1).get("likeButton").findElement(By.cssSelector("svg")).getAttribute("class").contains("MuiSvgIcon-colorError"),"Heart appears to be filled");


        Assert.assertEquals(Integer.parseInt(smp.getNotificationCounter().getText()),2,"not equal");

        //VERIFY NOTIFICATION BACKGROUND COLOR
        Assert.assertTrue(smp.getNotificationCounter().getCssValue("background-color").contains("211, 47, 47"), "No Notification counter");



        System.out.println("Color of notification counter: " + smp.getNotificationCounter().getCssValue("background-color"));
        System.out.println("Notification Counter: "+ smp.getNotificationCounter().getText());

        //driver.quit();
    }

    @Test
    void smf004_MarkNotificatiosAsSeen(){
       /*
       *Click like on a post to generate notification
        Verify badge shows count
        Open notifications modal
        Verify notification dot is removed and text is gray
        Close modal and confirm badge count is 0
       * */

        WebElement windowTop = driver.findElement(By.xpath("//div[contains(@class,'MuiBox-root')]//h6[contains(text(), 'Background')]"));

        List <Map<String, WebElement>> posts = smp.getAllPosts();

        posts.get(2).get("likeButton").click();
        System.out.println(smp.getNotificationCounter().getText());
        Assert.assertEquals(Integer.parseInt(smp.getNotificationCounter().getText()),1, "not counting");

        Actions action = new Actions(driver);
        action.moveToElement(windowTop).perform();

        smp.getNoticationBell().click();

        //VERIFY NOTIFICATION DOT IS REMOVED -usando su clase porque no se como funciona el CSS en este caso.
        Assert.assertFalse(smp.getNotificationCounter().getAttribute("class").contains("MuiBadge-insvisible"));
        System.out.println(smp.getNotificationCounter().getCssValue("background-color"));

        //VERIFY TEXT IS GRAY
        List<WebElement> notifications = smp.getNotificationText().findElements(By.xpath("//p[contains (@class, \"css-by7j5z\")]"));

        for(WebElement notif : notifications){
            System.out.println(notif.getText());
            System.out.println((notif.getCssValue("color")));
            Assert.assertTrue(notif.getCssValue("color").contains("0.707 0.022 261.325"));
        }


        action.sendKeys(Keys.ESCAPE).perform();
        System.out.println(smp.getNotificationCounter().getText());
        Assert.assertEquals(Integer.parseInt(smp.getNotificationCounter().getText()),0, "Counter more than 0");

        //driver.close(); //SOLO CIERRA LA VENTANA, NO CIERRA LA SESION

    }

    @Test
    void smf005_Like_Multiple_Posts(){
        /*
        *Like multiple posts and confirm each maintains independent state

        Steps to Execute:
        * Like the first post
        * Like the second post
        * Verify both posts show incremented counts and filled hearts
        * Verify other posts remain unaffected
        * */

        Actions action = new Actions(driver);
        List <Map<String, WebElement>> posts =  smp.getAllPosts();

        int firstPostLikes = Integer.parseInt(posts.get(0).get("likesText").getText().replace("likes", "").trim());
        int secondPostLikes = Integer.parseInt(posts.get(1).get("likesText").getText().replace("likes", "").trim());

        System.out.println(firstPostLikes);
        System.out.println(secondPostLikes);



      //  action.moveToElement(likeButton).perform();
        posts.get(0).get("likeButton").click();
        posts.get(1).get("likeButton").click();

        int firstPostNewLikes = Integer.parseInt(posts.get(0).get("likesText").getText().replace("likes", "").trim());
        int secondPostNewLikes = Integer.parseInt(posts.get(1).get("likesText").getText().replace("likes", "").trim());

        Assert.assertEquals(firstPostNewLikes, firstPostLikes+1, "Not Equal");
        Assert.assertEquals(secondPostNewLikes, secondPostLikes+1, "Not Equal");

        Assert.assertTrue(posts.get(0).get("likeButton").findElement(By.cssSelector("svg")).getCssValue(("color")).contains("211, 47, 47"));
        Assert.assertTrue(posts.get(1).get("likeButton").findElement(By.cssSelector("svg")).getCssValue(("color")).contains("211, 47, 47"));
        Assert.assertFalse(posts.get(2).get("likeButton").findElement(By.cssSelector("svg")).getCssValue(("color")).contains("211, 47, 47"));

        //driver.close(); SOLO CIERRA LA VENTANA, NO CIERRA LA SESION

    }

    @AfterMethod
    public void tearDown(){
        driver.quit();
    }

}
