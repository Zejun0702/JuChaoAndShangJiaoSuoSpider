import org.apache.log4j.BasicConfigurator;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.*;


public class ShangJiao{

    public static String getDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String wholeDate = sdf.format(date);
        return wholeDate;
    }

    public static String sendShangJiaoPost(String url) throws IOException {
        String res = "";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        res = response.body().string();
        return res;
    }


    public static void process(String url) throws IOException {
        String html = sendShangJiaoPost(url);
        String wholeDate = getDateAndTime();
        String data = wholeDate.substring(0,10);
        Jedis jedis1 = new Jedis("localhost");
        jedis1.select(4);
        Jedis jedis2 = new Jedis("localhost");
        jedis2.select(6);
        Jedis jedis3 = new Jedis("localhost");
        jedis3.select(7);
        Jedis jedis4 = new Jedis("localhost");
        jedis4.select(8);
        jedis3.del("ShangJiaoSuo_1");
        for (int i=1;i<91;i++) {
            String t_companyCode = html.split("data-seecode=")[i];
            String companyCode = t_companyCode.substring(1, 7);
            String notice0 = t_companyCode.split("title=")[1].split(">" + companyCode)[0];
            String notice1;
            try{
                String s2 = notice0.split("：")[0];
                String s3 = notice0.split("：")[1];
                notice1 = s2+s3;
            }
            catch(ArrayIndexOutOfBoundsException e1){
                notice1 = notice0;
            }
            String notice = notice1.trim();
            jedis1.sadd(companyCode,notice);
            jedis2.sadd(data,notice);
            jedis3.sadd("ShangJiaoSuo_1",notice);
            jedis4.sadd(notice,companyCode);
            System.out.println(wholeDate);
        }

    }
    public static void getNotice() throws IOException {
        BasicConfigurator.configure();
        process("http://www.sse.com.cn/disclosure/listedinfo/bulletin/s_docdatesort_desc_2019openpdf.htm");
    }

//    public static void main(String[] args) throws IOException {
//        BasicConfigurator.configure();
//        process("http://www.sse.com.cn/disclosure/listedinfo/bulletin/s_docdatesort_desc_2019openpdf.htm");
//    }

}