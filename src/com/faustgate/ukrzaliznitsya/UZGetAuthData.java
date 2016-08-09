package com.faustgate.ukrzaliznitsya;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by werwolf on 8/7/16.
 */

public class UZGetAuthData {
    String rem_head;
    String rem_foot;

    public UZGetAuthData() {
        rem_head = "<script>var em = $v.rot13(GV.site.email_support);$$v('#contactEmail')." +
                "attach({ href: 'mailto:' + em, innerHTML: em});" +
                "$v.domReady(function () {Common.performModule();" +
                "Common.pageInformation();" +
                "Common.setOpacHover($$v('#footer .cards_ribbon a, #footer .left a')," +
                " 50);Common.setOpacHover($$v('#footer .right a'), 70);});var _gaq =" +
                " _gaq || [];_gaq.push(['_setAccount', 'UA-33134148-1']);_gaq.push" +
                "(['_trackPageview']);";
        rem_foot = "(function () {var ga = document.createElement('script');" +
                "ga.async = true;" +
                "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + " +
                "'.google-analytics.com/ga.js';" +
                "var s = document.getElementsByTagName('script')[0];s.parentNode.insertBefore(ga, s);})();</script>";
    }

    public String getAuthToken(String page) {
        String token;
        Document doc = Jsoup.parse(page, "UTF-8");
        Element script_node = doc.select("html>body>script").first();
        String script = script_node.toString().replace(rem_head, "").replace(rem_foot, "");

        try {
            token = new JJDecoder(script).decode();
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


}
