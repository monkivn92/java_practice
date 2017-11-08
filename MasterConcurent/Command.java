public abstract class Command 
{
    protected String[] command;
    public Command (String [] command) 
    {
        this.command=command;
    }
    public abstract String execute ();
}

