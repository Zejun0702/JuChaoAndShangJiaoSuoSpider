import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifyMail extends EmailSend{
    public String currentDate = "";
    public String currentTime = "";
    public static String reportTitle = "";
    public NotifyMail( String[] receiverList, String subject, String emailContent,String reportTitle) {
        super(receiverList, subject, emailContent);
        reportTitle = reportTitle;
    }

    public static String getDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        String wholeDate = sdf.format(date);
        return wholeDate;
    }

//    public static void main(String[] args){
//        String wholeDate = getDateAndTime();
//        String currentDate =  wholeDate.substring(0,10);
//        String currentTime =  wholeDate.substring(11,19);
//        String missingTitle = "丢失title";
//        String sub = "【公告发布告警-" +currentDate+"】毕泽俊";
//        String[] receiverList = {"bzj245627014@gmail.com","245627014@qq.com"};
//        String emailcon = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n" +
//                "    <tr>\n" +
//                "        <th bgcolor=\"#F8CD46\">报告名</th bgcolor=\"#F8CD46\">\n" +
//                "        <th bgcolor=\"#F8CD46\">来源</th bgcolor=\"#F8CD46\">\n" +
//                "        <th bgcolor=\"#F8CD46\">巨潮发布时间</th bgcolor=\"#F8CD46\">\n" +
//                "        <th bgcolor=\"#F8CD46\">交易所发布时间</th bgcolor=\"#F8CD46\">\n" +
//                "        <th bgcolor=\"#F8CD46\">状态</th bgcolor=\"#F8CD46\">\n" +
//                "        \n" +
//                "    </tr>\n" +
//                "    <tr>\n" +
//                "        <td align=center>"+missingTitle+"</td>\n" +
//                "        <td align=center>上交所</td>\n" +
//                "        <td align=center>"+wholeDate+"</td>\n" +
//                "        <td align=center> </td>\n" +
//                "        <td align=center><font color=\"red\">异常（公告丢失）</font></td>\n" +
//                "    </tr>\n" +
//                "</table>";
//
//        NotifyMail N1 = new NotifyMail(receiverList,sub,emailcon,missingTitle);
//        N1.send();
//
//    }
}
