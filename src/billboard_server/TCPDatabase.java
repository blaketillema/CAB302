package billboard_server;

// TODO: add semaphores/mutex locks for database
public class TCPDatabase
{
    private TCPClass pretendDatabase = new TCPClass();

    public TCPClass get()
    {
        return this.pretendDatabase;
    }

    public void set(TCPClass data)
    {
        this.pretendDatabase = data;
    }
}
