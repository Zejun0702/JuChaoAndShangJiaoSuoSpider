import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class ScheduledExecutorServiceTest {
    private static long getTimeMillis(String time) {
    try {
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
        Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
        return curDate.getTime();
    }
    catch (ParseException e) {
        e.printStackTrace();
    }
        return 0;
    }


    public static void main(String[] args) throws Exception {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        TimerTask timerTask = new TimerTask(); // 任务需要 2000 ms 才能执行完毕
        timer.scheduleAtFixedRate(timerTask, 1000, 60000, TimeUnit.MILLISECONDS);
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay  = getTimeMillis("1:00:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new TimerTask2(), initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

    private static class TimerTask implements Runnable {
        public void run() {
            try {
                WholeProcess.start();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static class TimerTask2 implements Runnable {
        public void run() {
            WholeProcess.sendTotalMailOneDay("JuChao","ShangJiaoSuo");
        }
    }


    }

