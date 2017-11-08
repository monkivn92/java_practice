@Override
public String execute() 
{
    ConcurrentServer.shutdown();
    return "Server stopped";
}