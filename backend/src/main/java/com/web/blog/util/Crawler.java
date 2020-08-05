package com.web.blog.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Crawler {
    public static void main(final String[] args) {
        final String WEB_DRIVER_ID = "webdriver.chrome.driver";
        final String WEB_DRIVER_PATH = "C:\\Users\\multicampus\\Desktop\\sel\\chromedriver.exe";
        // final String WEB_DRIVER_PATH = "/usr/local/bin/chromedriver";

        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");

        WebDriver driver = new ChromeDriver(options);

        String url = "https://place.map.kakao.com/1989327162";

        
        try{
            driver.get(url);
            Thread.sleep(500);
            Document doc = Jsoup.parse(driver.getPageSource());
            Elements el = doc.body().getElementsByClass("link_evaluation");
            double star = Double.parseDouble(el.text().split(" ")[1].substring(0,3));
            System.out.println(star);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            driver.close();
        }
        

        
    }
    
}