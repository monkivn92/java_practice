public class RejectedTaskController implements RejectedExecutionHandler 
{
    @Override
    public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) 
    {
        ConcurrentCommand command = (ConcurrentCommand)task;

        Socket clientSocket = command.getSocket();

        try {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
        String message="The server is shutting down."
                        +" Your request can not be served."
                        +" Shutting Down: "
                        +String.valueOf(executor.isShutdown())
                        +". Terminated: "
                        +String.valueOf(executor.isTerminated())
                        +". Terminating: "
                        +String.valueOf(executor.isTerminating());
        out.println(message);
        out.close();
        clientSocket.close();

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

}


