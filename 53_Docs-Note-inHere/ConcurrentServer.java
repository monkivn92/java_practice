public class ConcurrentServer 
{
    private static ParallelCache cache;
    private static volatile boolean stopped=false;

    /* store the sockets of the clients that
    sends a message to the server. These sockets will be processed by the
    RequestTask class. */
    private static LinkedBlockingQueue<Socket> pendingConnections;

    /* store the Future objects associated with every
    task executed in the executor. The key will be the username of the users
    that sends the queries, and the values will be another Map whose key will be
    the ConcurrenCommand objects, and the value will be the Future instance
    associated with that task. We use these Future instances to cancel the
    execution of tasks. */
    private static ConcurrentMap<String, ConcurrentMap<ConcurrentCommand, ServerTask<?> > >   taskController;

    private static Thread requestThread;
    private static RequestTask task;

    public static void main(String[] args) 
    {
        WDIDAO dao=WDIDAO.getDAO();

        cache=new ParallelCache();

        Logger.initializeLog();

        pendingConnections = new LinkedBlockingQueue<Socket>();

        taskController = new ConcurrentHashMap<String,  ConcurrentHashMap<Integer, Future<?>>>();

        task = new RequestTask(pendingConnections, taskController);

        requestThread = new Thread(task);

        requestThread.start();

        System.out.println("Initialization completed.");
    
        serverSocket = new ServerSocket(Constants.CONCURRENT_PORT);

        do 
        {
            try 
            {
                Socket clientSocket = serverSocket.accept();
                pendingConnections.put(clientSocket);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        } while (!stopped);

        finishServer();
        System.out.println("Shutting down cache");
        cache.shutdown();
        System.out.println("Cache ok" + new Date());
    }

    public static void shutdown() 
    {
        stopped=true;
        try 
        {
            serverSocket.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private static void finishServer() 
    {
        System.out.println("Shutting down the server...");
        task.shutdown();

        System.out.println("Shutting down Request task");
        requestThread.interrupt();

        System.out.println("Request task ok");
        System.out.println("Closing socket");
        System.out.println("Shutting down logger");
        Logger.sendMessage("Shutting down the logger");

        Logger.shutdown();

        System.out.println("Logger ok");
        System.out.println("Main server thread ended");
    }

}