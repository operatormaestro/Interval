import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        int THREADS = 7;
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Callable<Integer>> threads = new ArrayList<>();
        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            Callable<Integer> logic = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            threads.add(logic);
        }
        List<Future<Integer>> list = new ArrayList<>();
        for (Callable<Integer> z : threads) {
            Future<Integer> task = pool.submit(z);
            list.add(task);
        }
        pool.shutdown();
        int max = 0;
        for (Future<Integer> x : list) {
            int res = x.get();
            if (res > max) {
                max = res;
            }
        }
        System.out.println("Максимальное число символов 'а' подряд из всех строк: " + max);
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}