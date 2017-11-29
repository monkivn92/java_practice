public abstract class ConcurrentCommand extends Command implements Comparable<ConcurrentCommand>, Runnable
{
    private String username;
    private byte priority;
    private Socket socket;

    public ConcurrentCommand(Socket socket, String[] command) 
    {
        super(command);

        username=command[1];

        priority=Byte.parseByte(command[2]);

        this.socket=socket;
    }


    @Override
    public abstract String execute();

    @Override
    public void run() 
    {
        String message="Running a Task: Username: " +username +"; Priority: "    +priority;

        Logger.sendMessage(message);

        String ret=execute();

        ParallelCache cache = ConcurrentServer.getCache();

        if (isCacheable()) 
        {
            cache.put(String.join(";",command), ret);
        }

        try {
            PrintWriter out = new  PrintWriter(socket.getOutputStream(),true);
            
            out.println(ret);

            socket.close();

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        System.out.println(ret);

    }

    @Override
    public int compareTo(ConcurrentCommand o) 
    {
        return Byte.compare(o.getPriority(), this.getPriority());
    }

    
}