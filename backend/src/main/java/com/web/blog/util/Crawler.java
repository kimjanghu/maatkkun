package com.web.blog.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

/*
public static void main(String[] args) {
    
    final String menu = "할리스";
    final String url = "https://place.map.kakao.com/1969587022";
    try{
        Document doc = Jsoup.connect(url).get();
        System.out.println(doc.html());
    }catch(Exception e){
        System.out.println(e.getMessage());
    }
}
*/
// 1596523926447
// 1596524019896
    
    public static void main(final String[] args) {
        // final String menu = "할리스";
        // final String url = "https://www.google.com/maps/search/"+menu;


        final String menu = "할리스역삼점";
        // final String url = "https://search.daum.net/search?nil_suggest=btn&w=tot&DA=SBC&q="+menu;
        final String url = "https://place.map.kakao.com/1989327162#kakaoBody";
        
        // System.out.println(url);

        
        try{
            Document doc = Jsoup.connect(url).get();
            
            System.out.println(doc.body().html());
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
            //     }
            //     System.out.println("=================================================");
            // }
            
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        

        
    }
    
}