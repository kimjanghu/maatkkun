package com.web.blog.util;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Crawler {

    public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static final String WEB_DRIVER_PATH = "C:\\Users\\multicampus\\Desktop\\sel\\chromedriver.exe";
        
    public static void main(final String[] args) {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();

        String url = "https://place.map.kakao.com/1989327162";
        driver.get(url);
        


        
        driver.get(url);
        List<WebElement> elements = null;
        while (elements == null)
        {
            elements = driver.findElements(By.className("marker"));
            if (!valuePresent(elements))
            {
                elements = null;
            }
        }

        if (elements != null)
        {
            processElements(elements);
        }




        // final String menu = "할리스";
        // final String url = "https://www.google.com/maps/search/"+menu;

        // System.out.println(url);





        
        try{
            // for(int i = 0; i< 2; i++){
            //     Document doc = Jsoup.parse(driver.getPageSource());
            //     System.out.println(doc.body().html());  
            //     Thread.sleep(1000);
            //     System.out.println("================================================================================");
            // }
            
            // Element el = doc.getElementById("kakaoIndex");
            // System.out.println(el);
            // Elements el = doc.getElementsByClass("fn_tit");
            // for(Element e : el ){
            //     System.out.println("=================================================");
            //     if(e.text().contains("역삼")){


            //         Document doo = Jsoup.connect(e.parent().getElementsByTag("a").attr("href")).get();
            //         System.out.println(e.parent().getElementsByTag("a").attr("href"));
            //         System.out.println(doo.html());
                    

            //         //link_evaluation
            //         break;
            //     }~
            //     System.out.println("=================================================");
            // }
            
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        

        
    }
    
}