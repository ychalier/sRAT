
import web.RequestHandler;
import web.Server;

public class Main {

	public static void main(String[] args) throws Exception {
						
		Server server = new Server(new RequestHandler(){

			@Override
			public String getResponse(String request) {
				return "Hello world!";
			}
			
		});
		server.start();
		
	}
	

}
