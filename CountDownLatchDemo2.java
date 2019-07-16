import java.util.concurrent.CountDownLatch;

class Service implements Runnable {
    private String name;
    private int initialDelay;
    private CountDownLatch latch;

    public Service(String name, int initialDelay, CountDownLatch latch) {
        this.name = name;
        this.initialDelay = initialDelay;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting service ["+this.name+"] in "+this.initialDelay+" seconds");
            Thread.sleep(this.initialDelay + 6000);
            System.out.println(this.name+" service started");
        } catch (InterruptedException e) {

        } finally {
            this.latch.countDown();
        }
    }
}

class InitServices {
    private CountDownLatch latch = new CountDownLatch(3);
    private Thread cacheService = new Thread(new Service("CacheService", 2000, latch));
    private Thread applicationService = new Thread(new Service("ApplicationService", 500, latch));
    private Thread dataService = new Thread(new Service("DataService", 1000, latch));

    public void init() {
        this.cacheService.start();
        this.applicationService.start();
        this.dataService.start();
        try {
            this.latch.await();
        } catch (InterruptedException e) {}
    }
}

public class CountDownLatchServiceUpSimulation {
    public static void main(String... args) {
        InitServices initServices = new InitServices();
        initServices.init();
        System.out.println("All service are up");
    }
}
