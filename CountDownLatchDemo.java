import java.util.concurrent.CountDownLatch;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.FileInputStream;

class Util {
    public static String readFile(String fileName) throws Exception {
        BufferedReader f1 = new BufferedReader(new FileReader(fileName));
        StringBuffer curr = new StringBuffer();
        while(true) {
            String line = f1.readLine();
            if (line == null) {
                break;
            }
            curr.append(line);
        }
        return curr.toString();
    }
    public static List<String> toList(String s, String splitter) {
        return Arrays.asList(s.split(splitter));
    }
}

class DownloadCourse {
    
    private final CountDownLatch loadingLatch = new CountDownLatch(10);
    private static final List<String> words = new ArrayList<String>();
    private Object _lock = new Object();

    private Thread readFile(String fileName) {
        return new Thread("read:"+fileName) {
            @Override
            public void run() {
                List<String> _words = new ArrayList<String>();
                try {
                    _words = Util.toList(Util.readFile(fileName), "\\s+");
                } catch (Exception e) {}
                synchronized(_lock) {
                    for (String word : _words) {
                        if (!words.contains(word)) {
                            words.add(word);
                        }
                    }
                }
                loadingLatch.countDown();
            }
        };
    }

    public void finishAllDownloads() {
        List<String> words = Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten");
        for(String word : words) {
            this.readFile(word+".txt").start();
        }
        try {
            this.loadingLatch.await();
        } catch (InterruptedException e) {}
    }

    public List<String> getWords() {
        return this.words;
    }

}

public class CountDownLatchDemo {
    public static void main(String... args) {
        DownloadCourse download = new DownloadCourse();
        download.finishAllDownloads();
        System.out.println(download.getWords());
    }
}
