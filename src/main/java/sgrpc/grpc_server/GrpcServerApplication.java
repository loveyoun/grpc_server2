package sgrpc.grpc_server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class GrpcServerApplication implements ApplicationRunner {

    /**
     * final int PORT = 50051;
     * Server server = ServerBuilder.forPort(PORT)
     * .addService(new GreeterImpl())
     * .build();
     **/


    /**이렇게 안되고, bean으로 등록되어도 new로 객체생성을 해줘야 되는건가...?
     * @Autowired
    private static FileUploadService fileUploadService;

    public GrpcServerApplication(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }**/

    //@Autowired private FileUploadService fileUploadService; //nullpointException 남
    @Autowired private FileUploadService fileUploadService;
    public GrpcServerApplication(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    final int PORT = 50052;
    Server server = ServerBuilder.forPort(PORT)
            .addService(fileUploadService)
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
