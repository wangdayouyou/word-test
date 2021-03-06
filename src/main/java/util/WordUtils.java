package util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * docx、doc文档生成工具类  (改变后缀名即可)
 * 在使用制作模板的过程中如果模板中有图片那就保留图片，注意[Content_Types].xml和document.xml.rels文档
 * 如果模板中没有图片 则不需要设置[Content_Types].xml和document.xml.rels
 * 由于word模板的个性化 所以 每次做模板都要重新覆盖原来的模板
 *
 *
 *
 * gaoxueyong
 */
public class WordUtils {

    private final static String separator = File.separator;
    private final static String suffix_docx = "docx";
    private final static String suffix_doc = "doc";
    private final static String general_description = "generalDescription";
    private final static String detail = "detail";
    private final static String func_module = "funcModule";
    private final static String tech_param = "techParam";
    private final static String service_need = "serviceNeed";


    /*
     * @param dataMap               参数数据
     * @param docxTemplateFile      docx模主板名称
     * @param xmlDocument           docx中document.xml模板文件  用来存在word文档的主要数据信息
     * @param xmlDocumentXmlRels    docx中document.xml.rels 模板文件  用来存在word文档的主要数据配置 包括图片的指向
     * @param xmlContentTypes       docx中 [Content_Types].xml 模板文件 用来配置 docx文档中所插入图片的类型 如 png、jpeg、jpg等
     * @param xmlHeader             docx中 header1.xml 模板文件 用来配置docx文档的页眉文件
     * @param templatePath          模板存放路径 如 /templates/
     * @param outputFileTempPath    所生成的docx文件的临时路径文件夹 如果 temp/20180914051811/
     * @param outputFileName        所生成的docx文件名称  如  xxx.docx  或  xxx.doc
     * */
    public static void createDocx(Map dataMap, String docxTemplateFile, String xmlDocument, String xmlDocumentXmlRels,
                                  String xmlContentTypes, String xmlHeader, String templatePath,
                                  String outputFileTempPath, String outputFileName) throws Exception {

        URL basePath = WordUtils.class.getClassLoader().getResource("");
//        System.out.println("basePath.getPath() ==> " + basePath.getPath());
        String realTemplatePath = basePath.getPath() + templatePath;
        //临时文件产出的路径
        String outputPath = basePath.getPath() + outputFileTempPath;
        List<String> delFileList = new ArrayList<>();
        try {


            //================================获取 document.xml.rels 输入流================================
            String xmlDocumentXmlRelsComment = FreeMarkUtils.getFreemarkerContent(dataMap, xmlDocumentXmlRels, templatePath);
            ByteArrayInputStream documentXmlRelsInput =
                    new ByteArrayInputStream(xmlDocumentXmlRelsComment.getBytes());
            //================================获取 document.xml.rels 输入流================================

            //================================获取 header1.xml 输入流================================
            ByteArrayInputStream headerInput = FreeMarkUtils.getFreemarkerContentInputStream(dataMap, xmlHeader, templatePath);

            //================================获取 header1.xml 输入流================================

            //================================获取 [Content_Types].xml 输入流================================
            ByteArrayInputStream contentTypesInput = FreeMarkUtils.getFreemarkerContentInputStream(dataMap, xmlContentTypes, templatePath);
            //================================获取 [Content_Types].xml 输入流================================


            //读取 document.xml.rels  文件 并获取rId 与 图片的关系 (如果没有图片 此文件不用编辑直接读取就行了)
            Document document = DocumentHelper.parseText(xmlDocumentXmlRelsComment);
            Element rootElt = document.getRootElement(); // 获取根节点
            Iterator iter = rootElt.elementIterator();// 获取根节点下的子节点head
            List<Map<String, String>> serviceNeedPicList = (List<Map<String, String>>) dataMap.get("serviceNeedPicList");
            List<Map<String, String>> detailPicList = (List<Map<String, String>>) dataMap.get("detailPicList");
            List<Map<String, String>> generalDescriptionPicList = (List<Map<String, String>>) dataMap.get("generalDescriptionPicList");
            List<Map<String, String>> funcModulePicList = (List<Map<String, String>>) dataMap.get("funcModulePicList");
            List<Map<String, String>> techParamPicList = (List<Map<String, String>>) dataMap.get("techParamPicList");

            // 遍历Relationships节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                String id = recordEle.attribute("Id").getData().toString();
                String target = recordEle.attribute("Target").getData().toString();
                if (target.indexOf("media") == 0) {
                    if(target.contains("serviceNeed")){
                        if(serviceNeedPicList != null){
                            for (Map<String, String> picMap : serviceNeedPicList) {
                                if (target.endsWith(picMap.get("name"))) {
                                    picMap.put("rId", id);
                                }
                            }
                        }
                    }else if(target.contains("generalDescription")){
                        if(generalDescriptionPicList != null){
                            for (Map<String, String> picMap : generalDescriptionPicList) {
                                if (target.endsWith(picMap.get("name"))) {
                                    picMap.put("rId", id);
                                }
                            }
                        }
                    }else if(target.contains("detail")){
                        if(detailPicList != null){
                            for (Map<String, String> picMap : detailPicList) {
                                if (target.endsWith(picMap.get("name"))) {
                                    picMap.put("rId", id);
                                }
                            }
                        }
                    }else if(target.contains("funcModule")){
                        if(funcModulePicList != null){
                            for (Map<String, String> picMap : funcModulePicList) {
                                if (target.endsWith(picMap.get("name"))) {
                                    picMap.put("rId", id);
                                }
                            }
                        }
                    }else if(target.contains("techParam")){
                        if(techParamPicList != null){
                            for (Map<String, String> picMap : techParamPicList) {
                                if (target.endsWith(picMap.get("name"))) {
                                    picMap.put("rId", id);
                                }
                            }
                        }
                    }
                }
            }
            dataMap.put("serviceNeedPicList", serviceNeedPicList);//覆盖原来的picList;
            dataMap.put("detailPicList", detailPicList);//覆盖原来的picList;
            dataMap.put("generalDescriptionPicList", generalDescriptionPicList);//覆盖原来的picList;
            dataMap.put("funcModulePicList", funcModulePicList);//覆盖原来的picList;
            dataMap.put("techParamPicList", techParamPicList);//覆盖原来的picList;
            //================================获取 document.xml 输入流================================
            ByteArrayInputStream documentInput = FreeMarkUtils.getFreemarkerContentInputStream(dataMap, xmlDocument, templatePath);
            //================================获取 document.xml 输入流================================


//            System.out.println("base_path_template+separator+docxTemplate===="+base_path_template+separator+docxTemplate);
            File docxFile = new File(realTemplatePath + separator + docxTemplateFile);
            if (!docxFile.exists()) {
                docxFile.createNewFile();
            }

            ZipFile zipFile = new ZipFile(docxFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            File tempPath = new File(outputPath);
            //如果输出目标文件夹不存在，则创建
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(outputPath + outputFileName));


            //------------------覆盖文档------------------
            int len = -1;
            byte[] buffer = new byte[1024];
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                InputStream is = zipFile.getInputStream(next);
                if (next.toString().indexOf("media") < 0) {
                    // 把输入流的文件传到输出流中 如果是word/document.xml由我们输入
                    zipout.putNextEntry(new ZipEntry(next.getName()));
//                    System.out.println("next.getName()>>>" + next.getName() + "  next.isDirectory()>>>" + next.isDirectory());
                    //写入图片配置类型
                    if (next.getName().equals("[Content_Types].xml")) {
                        if (contentTypesInput != null) {
                            while ((len = contentTypesInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            contentTypesInput.close();
                        }

                    } else if (next.getName().indexOf("document.xml.rels") > 0) {
                        //写入填充数据后的主数据配置信息
                        if (documentXmlRelsInput != null) {
                            while ((len = documentXmlRelsInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            documentXmlRelsInput.close();
                        }
                    } else if ("word/document.xml".equals(next.getName())) {
                        //写入填充数据后的主数据信息
                        if (documentInput != null) {
                            while ((len = documentInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            documentInput.close();
                        }

                    } else if ("word/header1.xml".equals(next.getName())) {
                        //写入填充数据后的页眉信息
                        if (headerInput != null) {
                            while ((len = headerInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            headerInput.close();
                        }

                    } else {
                        while ((len = is.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        is.close();
                    }

                }

            }
            //------------------覆盖文档------------------

            //------------------写入新图片------------------
            len = -1;
            if (serviceNeedPicList != null && !serviceNeedPicList.isEmpty()) {
                for (Map<String, String> pic : serviceNeedPicList) {
                    ZipEntry next = new ZipEntry("word" + separator + "media" + separator + service_need +  pic.get("name"));
                    zipout.putNextEntry(new ZipEntry(next.toString()));
                    InputStream in = new FileInputStream(pic.get("path"));
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                }
            }
            if (generalDescriptionPicList != null && !generalDescriptionPicList.isEmpty()) {
                for (Map<String, String> pic : generalDescriptionPicList) {
                    ZipEntry next = new ZipEntry("word" + separator + "media" + separator + general_description +  pic.get("name"));
                    zipout.putNextEntry(new ZipEntry(next.toString()));
                    InputStream in = new FileInputStream(pic.get("path"));
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                }
            }
            if (detailPicList != null && !detailPicList.isEmpty()) {
                for (Map<String, String> pic : detailPicList) {
                    ZipEntry next = new ZipEntry("word" + separator + "media" + separator + detail +  pic.get("name"));
                    zipout.putNextEntry(new ZipEntry(next.toString()));
                    InputStream in = new FileInputStream(pic.get("path"));
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                }
            }
            if (techParamPicList != null && !techParamPicList.isEmpty()) {
                for (Map<String, String> pic : techParamPicList) {
                    ZipEntry next = new ZipEntry("word" + separator + "media" + separator + tech_param +  pic.get("name"));
                    zipout.putNextEntry(new ZipEntry(next.toString()));
                    InputStream in = new FileInputStream(pic.get("path"));
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                }
            }
            if (funcModulePicList != null && !funcModulePicList.isEmpty()) {
                for (Map<String, String> pic : funcModulePicList) {
                    ZipEntry next = new ZipEntry("word" + separator + "media" + separator + func_module +  pic.get("name"));
                    zipout.putNextEntry(new ZipEntry(next.toString()));
                    InputStream in = new FileInputStream(pic.get("path"));
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                }
            }

            //------------------写入新图片------------------
            zipout.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("生成docx文件失败！");
        }

    }


    /**
     * 删除文件
     *
     * @param listFiles
     */
    public static void delFiles(List<String> listFiles) {
        try {
            if (listFiles != null && !listFiles.isEmpty()) {
                for (String file_temp_path : listFiles) {
                    File file_temp = new File(file_temp_path);
                    if (file_temp.exists()) {
                        file_temp.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        URL basePath = WordUtils.class.getClassLoader().getResource("");
        String picPath = basePath.getPath() + separator + "templates" + separator;

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("ymdhis", DateUtils.getCurrentTime_yyyyMMddHHmmss());
        dataMap.put("programName","ThingJs-X智慧医院解决方案");
        dataMap.put("supplierName","云知声");
        dataMap.put("telephone","01234567892");
        dataMap.put("purchaseMode","直采");
        dataMap.put("industry","智慧医院");
        dataMap.put("serviceDomain","大数据");

        //设置可插入图片类型
        List<String> picTypes = new ArrayList<>();
        picTypes.add("png");
        picTypes.add("jpeg");
        picTypes.add("jpg");
        dataMap.put("picTypes", picTypes);

        Map<String, List<String>> generalDescription = HtmlUtils.resolveHtmlLabel("<p>通过ThingJS-X数字孪生可视化平台打造的智慧医院运营管理系统，以“一平台三中心”为架构，助力医院实现信息聚合、数字建模，三维映射，搭建一个智能化数字空间，依托数据治理、知识图谱、轻量建模技术，提升医院运营管理效率。系统实现了面向医院物理实体和业务逻辑层面的全面融合连接，打造了动态感知、协</p><video src=\"http://47.100.208.16:8222/api/file/8efd73bd-f7bc-49e9-8df5-4c7961cb9002.2022-06-30.test.mp4\" controls=\"controls\" style=\"max-width:100%\"></video><p><br/></p><p>同高效、可视交互的现代智慧医院运营管理新方式。2112<span style=\"background-color: rgb(194, 79, 74);\"></span></p><table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><th></th><th></th><th></th><th></th><th></th></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr></tbody></table><pre><code class=\"Bash\">&lt;div&gt;迪马&lt;div&gt;</code></pre>");
        dataMap.put("generalDescriptionP",generalDescription.get("p").toString());
        List<Map<String, String>> generalDescriptionPicList = new ArrayList<>();

        Map<String, String> picMap = new HashMap<>();
        // 要按顺序
        picMap.put("path", picPath + general_description + "pic2.jpg");
        picMap.put("name", general_description + "pic2.jpg");
        generalDescriptionPicList.add(picMap);

        dataMap.put("generalDescriptionPicList", generalDescriptionPicList);

        dataMap.put("generalDescriptionVideo","https://www.baidu.com");
        Map<String, List<String>> details = HtmlUtils.resolveHtmlLabel("<p>通过ThingJS-X" + "数字孪生可视化平台打造的智慧医院运营管理系统，以“一平台三中心”为架构，助力医院实现信息聚合、数字建模，三维映射，搭建一个智能化数字空间，依托数据治理、知识图谱、轻量建模技术，提升医院运营管理效率。系统实现了面向医院物理实体和业务逻辑层面的全面融合连接，打造了动态感知、协</p><video src=\"http://47.100.208.16:8222/api/file/8efd73bd-f7bc-49e9-8df5-4c7961cb9002.2022-06-30.test.mp4\" controls=\"controls\" style=\"max-width:100%\"></video><p><br/></p><p>同高效、可视交互的现代智慧医院运营管理新方式。2112<span style=\"background-color: rgb(194, 79, 74);\"></span></p><table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><th></th><th></th><th></th><th></th><th></th></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr></tbody></table><pre><code class=\"Bash\">&lt;div&gt;迪马&lt;div&gt;</code></pre>");
        dataMap.put("detailP",details.get("p").toString());
        List<Map<String, String>> detailPicList = new ArrayList<>();

        picMap = new HashMap<>();
        // 要按顺序
        picMap.put("path", picPath + detail + "pic1.jpg");
        picMap.put("name", detail + "pic1.jpg");
        detailPicList.add(picMap);

        dataMap.put("detailPicList", detailPicList);

        dataMap.put("detailVideo","https://www.baidu.com");

        Map<String, List<String>> funcModule = HtmlUtils.resolveHtmlLabel("<p>通过ThingJS-X数字孪生可视化平台打造的智慧医院运营管理系统，以“一平台三中心”为架构，助力医院实现信息聚合、数字建模，三维映射，搭建一个智能化数字空间，依托数据治理、知识图谱、轻量建模技术，提升医院运营管理效率。系统实现了面向医院物理实体和业务逻辑层面的全面融合连接，打造了动态感知、协</p><video src=\"http://47.100.208.16:8222/api/file/8efd73bd-f7bc-49e9-8df5-4c7961cb9002.2022-06-30.test.mp4\" controls=\"controls\" style=\"max-width:100%\"></video><p><br/></p><p>同高效、可视交互的现代智慧医院运营管理新方式。2112<span style=\"background-color: rgb(194, 79, 74);\"></span></p><table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><th></th><th></th><th></th><th></th><th></th></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr></tbody></table><pre><code class=\"Bash\">&lt;div&gt;迪马&lt;div&gt;</code></pre>");
        dataMap.put("FuncModulep",funcModule.get("p").toString());
        List<Map<String, String>> funcModulePicList = new ArrayList<>();

        picMap = new HashMap<>();
        // 要按顺序
        picMap.put("path", picPath + func_module + "pic1.jpg");
        picMap.put("name", func_module + "pic1.jpg");
        funcModulePicList.add(picMap);

        dataMap.put("funcModulePicList", funcModulePicList);
        dataMap.put("funcModuleVideo","https://www.baidu.com");

        Map<String, List<String>> techParam = HtmlUtils.resolveHtmlLabel("<p>通过ThingJS-X数字孪生可视化平台打造的智慧医院运营管理系统，以“一平台三中心”为架构，助力医院实现信息聚合、数字建模，三维映射，搭建一个智能化数字空间，依托数据治理、知识图谱、轻量建模技术，提升医院运营管理效率。系统实现了面向医院物理实体和业务逻辑层面的全面融合连接，打造了动态感知、协</p><video src=\"http://47.100.208.16:8222/api/file/8efd73bd-f7bc-49e9-8df5-4c7961cb9002.2022-06-30.test.mp4\" controls=\"controls\" style=\"max-width:100%\"></video><p><br/></p><p>同高效、可视交互的现代智慧医院运营管理新方式。2112<span style=\"background-color: rgb(194, 79, 74);\"></span></p><table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><th></th><th></th><th></th><th></th><th></th></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr></tbody></table><pre><code class=\"Bash\">&lt;div&gt;迪马&lt;div&gt;</code></pre>");
        dataMap.put("techParamp",techParam.get("p").toString());
        List<Map<String, String>> techParamPicList = new ArrayList<>();

        picMap = new HashMap<>();
        // 要按顺序
        picMap.put("path", picPath + tech_param + "pic1.jpg");
        picMap.put("name", tech_param + "pic1.jpg");
        techParamPicList.add(picMap);

        dataMap.put("techParamPicList", techParamPicList);
        dataMap.put("techParamVideo","https://www.baidu.com");

        Map<String, List<String>> serviceNeed = HtmlUtils.resolveHtmlLabel("<p>通过ThingJS-X数字孪生可视化平台打造的智慧医院运营管理系统，以“一平台三中心”为架构，助力医院实现信息聚合、数字建模，三维映射，搭建一个智能化数字空间，依托数据治理、知识图谱、轻量建模技术，提升医院运营管理效率。系统实现了面向医院物理实体和业务逻辑层面的全面融合连接，打造了动态感知、协</p><video src=\"http://47.100.208.16:8222/api/file/8efd73bd-f7bc-49e9-8df5-4c7961cb9002.2022-06-30.test.mp4\" controls=\"controls\" style=\"max-width:100%\"></video><p><br/></p><p>同高效、可视交互的现代智慧医院运营管理新方式。2112<span style=\"background-color: rgb(194, 79, 74);\"></span></p><table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><th></th><th></th><th></th><th></th><th></th></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr></tbody></table><pre><code class=\"Bash\">&lt;div&gt;迪马&lt;div&gt;</code></pre>");
        dataMap.put("serviceNeedp",serviceNeed.get("p").toString());
        List<Map<String, String>> serviceNeedPicList = new ArrayList<>();

        picMap = new HashMap<>();
        // 要按顺序
        picMap.put("path", picPath + service_need + "pic1.jpg");
        picMap.put("name", service_need + "pic1.jpg");
        serviceNeedPicList.add(picMap);

        picMap = new HashMap<>();
        // 要按顺序
        picMap.put("path", picPath + service_need + "pic2.jpg");
        picMap.put("name", service_need + "pic2.jpg");
        serviceNeedPicList.add(picMap);
        dataMap.put("serviceNeedPicList", serviceNeedPicList);

        dataMap.put("serviceNeedVideo","https://www.baidu.com");
        String timeStr = DateUtils.getCurrentTime_yyyyMMddHHmmssSSS();
        String docxTemplateFile = "docTemplate.docx";
        String xmlDocument = "document.xml";
        String xmlDocumentXmlRels = "document.xml.rels";
        String xmlContentTypes = "[Content_Types].xml";
        String xmlHeader = "header1.xml";//可以用来修改页眉的一些信息
        String templatePath = separator + "templates" + separator;
        String outputFileTempPath = "temp" + separator + timeStr + separator;
        String outputFileName = timeStr + "."+suffix_docx;
//        String outputFileName = timeStr + "."+suffix_doc;


        /*
         * @param dataMap               参数数据
         * @param docxTemplateFile      docx模主板名称
         * @param xmlDocument           docx中document.xml模板文件  用来存在word文档的主要数据信息
         * @param xmlDocumentXmlRels    docx中document.xml.rels 模板文件  用来存在word文档的主要数据配置 包括图片的指向
         * @param xmlContentTypes       docx中 [Content_Types].xml 模板文件 用来配置 docx文档中所插入图片的类型 如 png、jpeg、jpg等
         * @param xmlHeader             docx中 header1.xml 模板文件 用来配置docx文档的页眉文件
         * @param templatePath          模板存放路径 如 /templates/
         * @param outputFileTempPath    所生成的docx文件的临时路径文件夹 如果 temp/20180914051811/
         * @param outputFileName        所生成的docx文件名称  如  xxx.docx 或  xxx.doc
         * */
        try {
            createDocx(dataMap, docxTemplateFile, xmlDocument, xmlDocumentXmlRels, xmlContentTypes,
                    xmlHeader, templatePath, outputFileTempPath, outputFileName);


//            String xmlDocumentXmlRelsComment = FreeMarkUtils.getFreemarkerContent(dataMap,xmlDocumentXmlRels,separator + "templates" );
//            System.out.println(xmlDocumentXmlRelsComment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}