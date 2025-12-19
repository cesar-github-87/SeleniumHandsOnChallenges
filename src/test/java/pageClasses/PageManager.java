package pageClasses;

import org.openqa.selenium.WebDriver;


public class PageManager {

    private final SearchEnginePage searchEnginePage;
    private final ChallengesPage challengesPage;
    private final JobPage jobPage;


    //constructor
    public PageManager(WebDriver driver){
        this.searchEnginePage = new SearchEnginePage(driver);
        this.challengesPage = new ChallengesPage(driver);
        this.jobPage =  new JobPage(driver);
    }

    public ChallengesPage challengesPage(){
        return this.challengesPage;
    }

    public SearchEnginePage searchEnginePage(){

        return this.searchEnginePage;
    }

    public JobPage jobPage(){
        return this.jobPage;
    }

}
