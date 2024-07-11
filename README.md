# EasyConcurrent
 Java Concurrent Utility class helps to design easy implementation of thread handling with callback.

 To use FutureApi<GENERIC_TYPE>(for more information please check FutureApi.java file)
```
public class Test {
    private final StringBuilder finalOutput = new StringBuilder();

    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();
        final boolean[] temp = new boolean[1];

        FutureApi<String> stringFutureApi = new FutureApi<>(new Callable<String>() {
            @Override
            public String call() throws Exception {

                for (int i = 0; i < 27; i++) {
                    test.getBuilder().append((char) (65 + i));
                    Thread.sleep(400);
                }
                return test.getBuilder().toString();
            }
        }, true);

        stringFutureApi.setListener(new FutureApi.Listener<String>() {
            @Override
            public void onCompleted(String value, boolean taskStatus) {
              System.out.println("onCompleted(): "+value);
			  System.out.println("onCompleted(): "+taskStatus);
                temp[0] = taskStatus;

            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }

            @Override
            public void onStopThread(String error) {
                System.out.println(error);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                stringFutureApi.stopAnotherThread();
                System.out.println("Value: " + test.getBuilder());
                System.out.println("Task Status: " + temp[0]);
                stringFutureApi.shutDown(1, TimeUnit.SECONDS);
                stringFutureApi.clear();

            }
        }).start();
        stringFutureApi.execute();
}

    public StringBuilder getBuilder() {
        return finalOutput;
    }
}
```

To Use Thread Tool:(for more information please check ThreadTool.java file)
```
ThreadTool threadTool=new ThreadTool() {
            @Override
            public void beforeThreadStarted() {
                System.out.println("Before thread started");
            }

            @Override
            public void onThreadStarted() {
                System.out.println("Thread started: "+Thread.currentThread().getName());
                normalShutDown();
            }

            @Override
            public void onThreadCatchedError(String err) {
                System.out.println(err);
            }
        }.startExecution();
```
