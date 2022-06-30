package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlUtils {
    public static Map<String,List<String>> resolveHtmlLabel(String label){
        Map<String, List<String>> result = new HashMap<>();
        result.put("p",new ArrayList<>());
        result.put("img",new ArrayList<>());
        result.put("video",new ArrayList<>());

        Document document = Jsoup.parse(label);
        Elements pElement = document.getElementsByTag("p");
        Elements imgElement = document.getElementsByTag("img");
        Elements videoElement = document.getElementsByTag("video");
        for (Element e:
             pElement) {
            result.get("p").add(e.getElementsByTag("p").eq(0).text());
        }
        for (Element e:
             imgElement) {
            result.get("img").add(e.attr("src"));
        }
        for (Element e:
             videoElement) {
            result.get("video").add(e.attr("src"));
        }
        return result;
    }

    public static void main(String[] args) {
        Map<String,List<String>> res = resolveHtmlLabel("<p>通过ThingJS-X数字孪生可视化平台打造的智慧医院运营管理系统，以“一平台三中心”为架构，助力医院实现信息聚合、数字建模，三维映射，搭建一个智能化数字空间，依托数据治理、知识图谱、轻量建模技术，提升医院运营管理效率。系统实现了面向医院物理实体和业务逻辑层面的全面融合连接，打造了动态感知、协</p><video src=\"http://47.100.208.16:8222/api/file/8efd73bd-f7bc-49e9-8df5-4c7961cb9002.2022-06-30.test.mp4\" controls=\"controls\" style=\"max-width:100%\"></video><p><br/></p><p>同高效、可视交互的现代智慧医院运营管理新方式。2112<span style=\"background-color: rgb(194, 79, 74);\"></span></p><table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><th></th><th></th><th></th><th></th><th></th></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr></tbody></table><pre><code class=\"Bash\">&lt;div&gt;迪马&lt;div&gt;</code></pre>");
        System.out.println(res);
    }
}