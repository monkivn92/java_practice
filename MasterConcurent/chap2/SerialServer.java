public class SerialServer 
{
public static void main(String[] args) throws IOException 
{
    WDIDAO dao = WDIDAO.getDAO();

    boolean stopServer = false;

    System.out.println("Initialization completed.");

    try (ServerSocket serverSocket = new   ServerSocket(Constants.SERIAL_PORT)) 
    {

        do {
                try (Socket clientSocket = serverSocket.accept();
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new  
                                                        InputStreamReader(clientSocket.getInputStream()));
                    ) 
                
                {
                    String line = in.readLine();
                    Command command;
                    String[] commandData = line.split(";");

                    System.out.println("Command: " + commandData[0]);

                    switch (commandData[0]) 
                    {
                        case "q":
                            System.out.println("Query");
                            command = new QueryCommand(commandData);
                            break;
                        case "r":
                            System.out.println("Report");
                            command = new ReportCommand(commandData);                       
                            break;
                        case "z":
                            System.out.println("Stop");
                            command = new StopCommand(commandData);
                            stopServer = true;
                            break;
                        default:
                            System.out.println("Error");
                            command = new ErrorCommand(commandData);
                    }

                    String response = command.execute();
                    System.out.println(response);

                    out.println(response);

                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }

            } while (!stopServer);

    }
    catch(Exception e)
    {
        e.printStackTrace();
    }

}