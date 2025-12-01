package pageClasses;

import org.openqa.selenium.WebDriver;


public class PageManager {

    private SearchEnginePage searchEnginePage;
    private ChallengesPage challengesPage;

    //constructor
    public PageManager(WebDriver driver){
        this.searchEnginePage = new SearchEnginePage(driver);
        this.challengesPage = new ChallengesPage(driver);
    }




    public ChallengesPage challengesPage(){
        return this.challengesPage;
    }

    public SearchEnginePage searchEnginePage(){
        return this.searchEnginePage;
    }


}
