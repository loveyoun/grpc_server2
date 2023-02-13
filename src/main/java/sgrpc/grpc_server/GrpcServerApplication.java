package sgrpc.grpc_server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcServerApplication implements ApplicationRunner {

	/**final int PORT = 50051;
	Server server = ServerBuilder.forPort(PORT)
			.addService(new GreeterImpl())
			.build();**/

	final int PORT = 50052;
	 Server server = ServerBuilder.forPort(PORT)
			 .addService(new FileUploadService())
			 .build();

	@Override
	public void run(ApplicationArguments args) throws Exception {
		server.start();
		System.out.println("Server Started, listening on " + PORT);
	}


	public static void main(String[] args) {
		SpringApplication.run(GrpcServerApplication.class, args);
	}

}
