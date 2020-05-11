import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class WholeProcess {


    public static String getDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    public static void checkIfMiss(Jedis j1, Jedis j2, Set<String> diffNotice,String source) throws ParseException {
        Jedis jedis5 = new Jedis("localhost");
        jedis5.select(9);//存放漏发布的数据
        String anotherSource;
        if(source == "ShangJiaoSuo"){
            anotherSource = "JuChao";
        }
        else {
            anotherSource = "ShangJiaoSuo";
        }
        String time = getDateAndTime();
        String currentDate =  time.substring(0,10);
        String currentTime =  time.substring(11,19);
        for (String noticeItem:diffNotice){
            Set<String> companyCode0 = j1.smembers(noticeItem);
            String[] arr1 = companyCode0.toArray(new String[0]);
            String companyCode = arr1[0];
            if(j2.sismember(companyCode,noticeItem)){ //通告上线了
                System.out.println("Notice Online");
                jedis5.rpush("Time_"+noticeItem,time);
                if ((!jedis5.sismember("sendedNotice",noticeItem) &&(jedis5.sismember(currentDate+"_"+anotherSource+"_missedNotice",noticeItem)))){
                    sendNoticeOnline(noticeItem,source);
                    jedis5.sadd("sendedNotice",noticeItem);
                }
            }
            else {  //通告遗漏了
                System.out.println("Send");
                if (!jedis5.sismember(currentDate+"_"+anotherSource+"_missedNotice",noticeItem)){
                    sendMissingNotice(noticeItem,source);
                    jedis5.sadd(currentDate+"_"+anotherSource+"_missedNotice",noticeItem);
                }
                }

        }
    }


    public static String generateHTML(String source){
        /*
        *@description：生成source站点的漏发布邮件HTML代码
        *@parameter: source：漏发布公司名
        */
        String companyName; // 公司名
        int selectIndex; // 对应Redis数据库中的DB索引
        String HTML = ""; // HTML表格代码
        Jedis j1 = new Jedis("localhost");
        Jedis j2 = new Jedis("localhost");
        j1.select(9);
        if (source == "ShangJiaoSuo"){
            selectIndex = 8;
            companyName = "上交所";
        }
        else{
            selectIndex = 7;
            companyName = "巨潮";
        }
        j2.select(selectIndex);
        String currentDateAndTime = getDateAndTime();
        String currentDate =  currentDateAndTime.substring(0,10);
        Set<String> missedNoticeSet= j1.smembers(currentDate+"_"+source+"_missedNotice");
        for (String missedNotice:missedNoticeSet){
            /*
            * companyCode0为漏发布通告对应的公司代码集合
            * companyCode为漏发布通告对应的公司代码
            */
            Set<String> companyCode0 = j2.smembers(missedNotice);
            System.out.println(missedNotice);
            System.out.println(companyCode0);
            String[] arr1 = companyCode0.toArray(new String[0]);
            String companyCode = arr1[0];
            HTML = HTML+"<tr><td>"+companyCode+"</td> <td>"+missedNotice+"</td> <td>漏发布</td> <td>"+companyName+"</td> </tr>";
        }
        return HTML;

    }


    /*
     *
     * @param source 漏发不的
     */
    public static void sendTotalMailOneDay(String companyOne,String companyTwo){    //source为漏发布的站点
        String currentDate = getYesterday();
        Jedis j1 = new Jedis("localhost");
        j1.select(5);
        Long juChaoAll = j1.scard(currentDate);
        System.out.println(j1.scard("2020-05-08"));
        System.out.println("\""+currentDate+"\"");
        j1.select(6);
        Long shangJiaoSuoAll = j1.scard(currentDate);
        j1.select(9);
        Long juChaoMissedNumber = j1.scard(currentDate+"_"+companyOne+"_missedNotice");
        Long shangJiaoMissedNumber = j1.scard(currentDate+"_"+companyTwo+"_missedNotice");
        String htmlTableOne = generateHTML(companyOne);
        String htmlTableTwo = generateHTML(companyTwo);
        String emailSubject = "【公告每日统计-" +currentDate+"】毕泽俊";
        String[] receiverList = {"bzj245627014@gmail.com","245627014@qq.com"};
        String emailContent = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n" +
                "    <tr>\n" +
                "        <th colspan=4 bgcolor=\"#F8CD46\">"+currentDate+"公告统计</th bgcolor=\"#F8CD46\">\n" +
                "        \n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td align=center>信息源名称</td>\n" +
                "        <td align=center>公告日期</td>\n" +
                "        <td align=center>当日总量</td>\n" +
                "        <td align=center>漏发布</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td align=center>巨潮</td>\n" +
                "        <td align=center>"+currentDate+"</td>\n" +
                "        <td align=center>"+juChaoAll+"</td>\n" +
                "        <td align=center>"+juChaoMissedNumber+"</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td align=center>上交所</td>\n" +
                "        <td align=center>"+currentDate+"</td>\n" +
                "        <td align=center>"+shangJiaoSuoAll+"</td>\n" +
                "        <td align=center>"+shangJiaoMissedNumber+"</td>\n" +
                "    </tr>\n" +
                "</table>"+"<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "    <tr>\n" +
                "        <th colspan=4 bgcolor=\"#F8CD46\">明细统计</th bgcolor=\"#F8CD46\">\n" +
                "        \n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td align=center>代码</td>\n" +
                "        <td align=center>标题</td>\n" +
                "        <td align=center>错误类型</td>\n" +
                "        <td align=center>信息源</td>\n" +
                "    </tr>\n" + htmlTableOne+htmlTableTwo+ "</table>";
        NotifyMail N1 = new NotifyMail(receiverList,emailSubject,emailContent," ");
        N1.send();
    }

    public static void sendMissingNotice(String possibleMissingNitice,String source){
        Jedis jedis5 = new Jedis("localhost");
        jedis5.select(9);
        String currentDateAndTime = getDateAndTime();
        jedis5.sadd(possibleMissingNitice,currentDateAndTime);
        String juChaoTime ="";
        String shangJiaoTime ="";
        if (source == "ShangJiaoSuo"){
            juChaoTime =" ";
            shangJiaoTime = currentDateAndTime;
        }
        if (source == "JuChao"){
            shangJiaoTime =" ";
            juChaoTime = currentDateAndTime;
        }
        String currentDate =  currentDateAndTime.substring(0,10);
        String missingTitle = possibleMissingNitice;
        String emailSubject = "【公告发布告警-" +currentDate+"】毕泽俊";
        String[] receiverList = {"bzj245627014@gmail.com","245627014@qq.com"};
        String emailContent = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "    <tr>\n" +
                "        <th bgcolor=\"#F8CD46\">报告名</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">来源</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">巨潮发布时间</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">交易所发布时间</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">状态</th bgcolor=\"#F8CD46\">\n" +
                "        \n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td align=center>"+missingTitle+"</td>\n" +
                "        <td align=center>"+source+"</td>\n" +
                "        <td align=center>"+juChaoTime+"</td>\n" +
                "        <td align=center>"+shangJiaoTime+"</td>\n" +
                "        <td align=center><font color=\"red\">异常（公告丢失）</font></td>\n" +
                "    </tr>\n" +
                "</table>";
        NotifyMail N1 = new NotifyMail(receiverList,emailSubject,emailContent,missingTitle);
        N1.send();
    }


    public static String getMissingTime(String possibleMissingNitice) throws ParseException {
        String currentDateAndTime = getDateAndTime();
        Jedis jedis5 = new Jedis("localhost");
        jedis5.select(9);//存放漏发布的数据
        String oldTime = jedis5.lrange("Time_"+possibleMissingNitice, 0, 0).get(0);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = df.parse(currentDateAndTime);
        Date date = df.parse(oldTime);
        long l=now.getTime()-date.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        long s=(l/1000-day*24*60*60-hour*60*60-min*60);
        String missTime = min+"分"+s+"秒";
        return missTime;
    }


    public static void sendNoticeOnline(String possibleMissingNitice,String source) throws ParseException {
        Jedis jedis5 = new Jedis("localhost");
        jedis5.select(9);//存放漏发布的数据
        String currentDateAndTime = getDateAndTime();
        String missingTime = getMissingTime(possibleMissingNitice);
        String juChaoTime ="";
        String shangJiaoTime ="";
        String sourceCompany = "";
        if (source == "ShangJiaoSuo"){
            juChaoTime = jedis5.lrange("Time_"+possibleMissingNitice, 0, 0).get(0);
            shangJiaoTime = currentDateAndTime;
            sourceCompany = "上交所";
        }
        if (source == "JuChao"){
            shangJiaoTime =jedis5.lrange("Time_"+possibleMissingNitice, 0, 0).get(0);
            juChaoTime = currentDateAndTime;
            sourceCompany = "巨潮";
        }

        String currentDate =  currentDateAndTime.substring(0,10);
        String missingTitle = possibleMissingNitice;
        String emailSubject = "【公告发布告警-" +currentDate+"】毕泽俊";
        String[] receiverList = {"bzj245627014@gmail.com","245627014@qq.com"};
        String emailContent ="<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "    <tr>\n" +
                "        <th bgcolor=\"#F8CD46\">报告名</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">来源</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">巨潮发布时间</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">交易所发布时间</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">状态</th bgcolor=\"#F8CD46\">\n" +
                "        <th bgcolor=\"#F8CD46\">超时时间</th bgcolor=\"#F8CD46\">\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>"+missingTitle+"</td>\n" +
                "        <td>"+sourceCompany+"</td>\n" +
                "        <td>"+juChaoTime+"</td>\n" +
                "        <td>"+shangJiaoTime+"</td>\n" +
                "        <td><font color=\"#53AE5F\">超时发布</font></td>\n" +
                "        <td><font color=\"#53AE5F\">"+missingTime+"</font></td>\n" +
                "    </tr>\n" +
                "</table>";
        NotifyMail N1 = new NotifyMail(receiverList,emailSubject,emailContent,missingTitle);
        N1.send();
    }



    public static String getYesterday(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime()).substring(0,10);

        return yesterday;
    }

    public static void start() throws ParseException, IOException {
        JuChao.getNotice();
        ShangJiao.getNotice();
        Jedis jedis1 = new Jedis("localhost");
        jedis1.select(7);
        Jedis jedis2 = new Jedis("localhost");
        jedis2.select(8);
        Jedis jedis3 = new Jedis("localhost");
        jedis3.select(4);
        Jedis jedis4 = new Jedis("localhost");
        jedis4.select(3);
        Set<String> s1 = jedis1.sdiff("JuChao_1","ShangJiaoSuo_1");
        Set<String> s2 = jedis1.sdiff("ShangJiaoSuo_1","JuChao_1");
        System.out.println(s1.size());
        System.out.println(s2.size());
        System.out.println(s1);
        System.out.println(s2);
        checkIfMiss(jedis2, jedis3, s2,"JuChao");
        checkIfMiss(jedis1, jedis4, s1,"ShangJiaoSuo");

    }
}
