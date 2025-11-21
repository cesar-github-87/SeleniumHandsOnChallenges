package practiceExercises;
import com.sun.source.tree.AssertTree;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pageClasses.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SocialMediaChallenge {
    WebDriver driver;
    SocialMediaPage smp;
    WebDriverWait wait;


   @BeforeMethod
   void instantiate(){
       driver = new ChromeDriver();

       driver.manage().deleteAllCookies();
       driver.get("https://www.cnarios.com/challenges/social-media-feed#challenge");
       driver.manage().window().maximize();
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

    driver.close();

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
        driver.close();
    }

    @Test
    void smf003GenerateNotification() throws InterruptedException {
    /*
    *   Verificar que el badge no tenga un numero desplegado
    *   Click like on a post
        Check notification badge shows count increment
        Open notifications modal
        * Verifica que el mensaje sea de like
        Verify new notification text is displayed with a dot
    * */
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List <Map<String, WebElement>> posts = smp.getAllPosts();

        //WebElement notifBell = driver.findElement(By.xpath("//button[contains(@class, 'MuiIconButton-root')]//span[contains(@class, 'MuiBadge-root')]"));


        //System.out.println("TextoBefore :" +notifCount.getText());
        posts.get(1).get("likeButton").click();
        Thread.sleep(3000);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[contains(@class, 'MuiIconButton-root') and .//span[contains(@class, 'MuiBadge-root')]]")));
        WebElement notifBell =  driver.findElement(By.xpath("//button[contains(@class, 'MuiIconButton-root') and .//span[contains(@class, 'MuiBadge-root')]]"));
        WebElement notifCount =  notifBell.findElement(By.xpath(".//span[contains(@class, 'MuiBadge-badge')]"));

        notifBell.click();

        System.out.println("TextoAfter :" + notifCount.getText());



    }

}
