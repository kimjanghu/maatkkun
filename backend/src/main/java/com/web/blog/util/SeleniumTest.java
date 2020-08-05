package com.web.blog.util;

import java.util.List;

import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumTest {
    //Properties
    public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static final String WEB_DRIVER_PATH = "C:\\Users\\multicampus\\Desktop\\sel\\chromedriver.exe";
    
    //크롤링 할 URL
    private String base_url;

    private WebDriver driver;

    public static void main(String[] args) {
        
        SeleniumTest selTest = new SeleniumTest();
        selTest.crawl();
        
    }

    public SeleniumTest() {
        //System Property SetUp
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        
        //Driver SetUp
        driver = new ChromeDriver();

        // final String menu = "나이스샤워+역삼";
        // base_url = "https://map.kakao.com/?from=total&nil_suggest=btn&tab=place&q="+menu;

        //다음
        // final String menu = "할리스역삼점";
        // base_url = "https://search.daum.net/search?nil_suggest=btn&w=tot&DA=SBC&q="+menu;

        // base_url = "https://www.naver.com";
        // base_url = "https://place.map.kakao.com/1969587022";
        base_url = "https://place.map.kakao.com/1989327162";
    }
 
    public void crawl() {
 
        try {
            //get page (= 브라우저에서 url을 주소창에 넣은 후 request 한 것과 같다)

            
            // WebElement parent = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[aria-labelledby*=\"accessible-list\"]")));
            
            driver.get(base_url);

            WebDriverWait wait = new WebDriverWait(driver, 30);
            WebElement parent = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("kakaoContent")));


            List<WebElement> contents = parent.findElements(By.cssSelector("div.link_evaluation"));
            for(WebElement e : contents){
                System.out.println(e.getText());
            }
            // System.out.println(driver.getPageSource());
            
            // List<WebElement> contents = driver.findElements(By.className("section-result-content"));
            // for(WebElement e : contents){
            //     if(e.getText().contains("역삼")){
            //         System.out.println(e.getText());
            //         // System.out.println(e.findElement(By.className("cards-rating-score")).getText());
            //         break;
            //     }
            // }


            // JavascriptExecutor jse = (JavascriptExecutor)driver;
            // jse.executeScript("window.scrollBy(0,250)");
            // jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            // driver.execute_script("window.scrollTo(0, Y);")
            
    
        } catch (Exception e) {
            
            e.printStackTrace();
        
        } finally {
            
            // driver.close();
        }
 
    }
}