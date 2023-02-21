package sgrpc.grpc_server;

import io.grpc.BindableService;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

//@Configuration
public class MinioConfiguration {

    @Value("${minio.access.key}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String secretKey;

    //Minio server console URL
    @Value("${minio.url}")
    private String minioUrl;


//    @Primary
//    @Bean
    public FileUploadService fileUploadService(){
        return new FileUploadService(minioClient());
    }
    //@Primary
    //@Bean
    public MinioClient minioClient(){
        //MinIO 서버에 접근할 수 있도록 Minio Client 객체 생성
        try {
            MinioClient client = new MinioClient.Builder()
                .credentials(accessKey, secretKey)
                .endpoint(minioUrl)
                .build();
            return client;
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
