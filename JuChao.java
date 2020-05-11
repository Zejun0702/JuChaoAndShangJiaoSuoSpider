
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.log4j.BasicConfigurator;
//import org.json.JSONObject;
import org.jsoup.nodes.Document;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JuChao {
    public static String getDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        String wholeDate = sdf.format(date);
        return wholeDate;
    }


    public static String sendJuChaoPost(String url) throws IOException {
        Document doc = null;
        String res = "";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("column", "sse_latest")
                .addFormDataPart("pageSize", "30")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Proxy-Connection", " keep-alive")
                .addHeader("Referer", " http://www.cninfo.com.cn/new/commonUrl?url=disclosure/list/notice")
                .addHeader("Cookie", "JSESSIONID=74B0B6735B0CE160A8DDCECFF63E2078; insert_cookie=45380249")
                .build();
        Response response = client.newCall(request).execute();

        res = response.body().string();
        return res;
    }
    public static void process(String url) throws IOException {
        String wholeDate = getDateAndTime();
        String data = wholeDate.substring(0,10);
        Jedis jedis1 = new Jedis("localhost");
        jedis1.select(3);
        Jedis jedis2 = new Jedis("localhost");
        jedis2.select(5);
        Jedis jedis3 = new Jedis("localhost");
        jedis3.select(7);
        jedis3.del("JuChao_1");
        jedis3.del("ShangJiaoSuo_1");
        jedis3.flushDB();
        Jedis jedis4 = new Jedis("localhost");
        String html = "" ;
        html = sendJuChaoPost(url);
        JSONObject jsonObj = JSONObject.parseObject(html);
        String ss1 = jsonObj.getString("classifiedAnnouncements").replace("["," ");
        String ss2 = ss1.replace("]"," ");
        for (int i = 0;i<20;i++){
            String[] temp = ss2.split(" ,")[i].trim().replace("},{","} {").split(" ");;
            for (String s6:temp){
                JSONObject jsonObj1 = JSONObject.parseObject(s6);
                String companyName = jsonObj1.getString("secName");
                String companyCode = jsonObj1.getString("secCode");
                String notice0 = jsonObj1.getString("announcementTitle");
                String notice = "\""+companyName+notice0+"\"";
//                System.out.println(notice);
                jedis1.sadd(companyCode,notice);
                jedis2.sadd(data,notice);
                jedis3.sadd("JuChao_1",notice);
                jedis3.sadd(notice,companyCode);
            }
        }
    }



    public static void getNotice() throws IOException {
        BasicConfigurator.configure();
        process("http://www.cninfo.com.cn/new/disclosure");
    }

//    public static void main(String[] args) throws IOException {
//        BasicConfigurator.configure();
//         process("http://www.cninfo.com.cn/new/disclosure");
//    }

}