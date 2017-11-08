public class RequestTask implements Runnable 
{
    private Socket clientSocket;

    public RequestTask(Socket clientSocket) 
    {
        this.clientSocket = clientSocket;
    }

    public void run() 
    {
        try (PrintWriter out = new  PrintWriter(clientSocket.getOutputStream(), true);
               BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) 
        {
            String line = in.readLine();
            Logger.sendMessage(line);

            ParallelCache cache = ConcurrentServer.getCache();
            String ret = cache.get(line);

            if (ret == null) 
            {
                Command command;
                String[] commandData = line.split(";");

                System.out.println("Command: " + commandData[0]);

                switch (commandData[0]) 
                {
                    case "q":
                        System.err.println("Query");
                        command = new ConcurrentQueryCommand(commandData);
                        break;
                    case "r":
                        System.err.println("Report");
                        command = new ConcurrentReportCommand(commandData);
                        break;
                    case "s":
                        System.err.println("Status");
                        command = new ConcurrentStatusCommand(commandData);
                        break;
                    case "z":
                        System.err.println("Stop");
                        command = new ConcurrentStopCommand(commandData);
                        break;
                    default:
                        System.err.println("Error");
                        command = new ConcurrentErrorCommand(commandData);
                        break;
                }

                ret = command.execute();

                if (command.isCacheable()) 
                {
                    cache.put(line, ret);
                }
            } 
            else 
            {
                Logger.sendMessage("Command "+line+" was found in the cache");        
            }

            System.out.println(ret);
            
            out.println(ret);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                clientSocket.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }

        
}