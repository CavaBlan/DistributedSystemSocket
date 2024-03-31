package csc435.app;

public class FileRetrievalServer
{
    public static void main( String[] args )
    {
    	int port = 12345;
        IndexStore store = new IndexStore();
		ServerSideEngine engine = new ServerSideEngine(store, port);
        ServerAppInterface appInterface = new ServerAppInterface(engine);
        
        engine.initialize();
        appInterface.readCommands();
    }
}
