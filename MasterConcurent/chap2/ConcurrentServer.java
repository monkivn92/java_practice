public class ConcurrentServer 
{

    private static ThreadPoolExecutor executor;
    private static ParallelCache cache;
    private static ServerSocket serverSocket;
    private static volatile boolean stopped = false;

    public static void main(String[] args) throws IOException 
    {
        serverSocket=null;

        WDIDAO dao = WDIDAO.getDAO();

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        cache = new ParallelCache();

        Logger.initializeLog();

        System.out.println("Initialization completed.");

        /* 
        We can't use a try-with-resources statement to manage the server socket. When we
        receive a stop command, we need to shut down the server, but the server is waiting
        in the accept() method of the serverSocket object. To force the server to leave
        that method, we need to explicitly close the server (we'll do that in the shutdown()
        method), so we can't leave the try-with-resources statement close the socket for us
        */
        serverSocket = new ServerSocket(Constants.CONCURRENT_PORT);

        do {
            try {
                Socket clientSocket = serverSocket.accept();
                RequestTask task = new RequestTask(clientSocket);
                executor.execute(task);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        } while (!stopped);

        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("Shutting down cache");
        cache.shutdown();
        System.out.println("Cache ok");
        System.out.println("Main server thread ended");


    }
    
    public static void shutdown() 
    {
        stopped = true;
        System.out.println("Shutting down the server...");
        System.out.println("Shutting down executor");

        executor.shutdown();

        System.out.println("Executor ok");
        System.out.println("Closing socket");

        try {
            serverSocket.close();
            System.out.println("Socket ok");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Shutting down logger");
        Logger.sendMessage("Shutting down the logger");

        Logger.shutdown();
        
        System.out.println("Logger ok");
    }



}//end class