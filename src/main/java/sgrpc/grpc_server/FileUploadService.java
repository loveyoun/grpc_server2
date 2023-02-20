package sgrpc.grpc_server;

import com.google.protobuf.ByteString;
import file.*;
import io.grpc.stub.StreamObserver;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class FileUploadService extends FileServiceGrpc.FileServiceImplBase {

    /**
     * @Autowired private MinioClient minioClient;
     * @Value("${minio.bucket.name}") private String bucket;
     **/

    /*@Value("${minio.bucket.name}")
    private static String bucket;*/

    private static final Path SERVER_BASE_PATH = Paths.get("src/main/resources/output");
    String path = "D:\\output.jpg";
    File file = new File(path);

    private final MinioClient minioClient;
    public FileUploadService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    long startTime, endTime;


    @Override
    public StreamObserver<FileUploadRequest> upload(StreamObserver<FileUploadResponse> responseObserver) {

        return new StreamObserver<FileUploadRequest>() {
            OutputStream writer;
            ByteArrayOutputStream bos;
            ByteArrayInputStream bis;

            Status status = Status.IN_PROGRESS;

            @Override
            public void onNext(FileUploadRequest fileUploadRequest) {
                try {
                    /**if(fileUploadRequest.getMetadata() != null){ //fileUploadRequest.hasMetadata() 안 됨
                     writer = getFilePath(fileUploadRequest);
                     }else{
                     writeFile(writer, fileUploadRequest.getFile().getContents());
                     }**/
                    if (bos == null) {
                        /*writer = new FileOutputStream(file);
                        writer.write(fileUploadRequest.getFile().getContents().toByteArray());*/
                        bos = new ByteArrayOutputStream();
                        startTime = System.nanoTime();
                        /**시간 측정 시작**/
                        bos.write(fileUploadRequest.getFile().getContents().toByteArray());
                    } else {
                        //writer.write(fileUploadRequest.getFile().getContents().toByteArray());
                        bos.write(fileUploadRequest.getFile().getContents().toByteArray());
                    }
                } catch (IOException e) {
                    this.onError(e);
                    //throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                status = Status.FAILED;
                this.onCompleted();
            }

            @Override
            public void onCompleted() {
                try {
                    /**시간 측정 끝**/
                    endTime = System.nanoTime();
                    System.out.println("ElapsedTime " + (endTime - startTime));

                    bis = new ByteArrayInputStream(bos.toByteArray());
                    //System.out.println(bos.toByteArray());

                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket("test")
                            .object("test.jpg")
                            .stream(bis, bos.toByteArray().length, -1)
                            .build());


                    //closeFile(writer);
                    status = Status.IN_PROGRESS.equals(status) ? Status.SUCCESS : status;
                    FileUploadResponse response = FileUploadResponse.newBuilder()
                            .setStatus(status)
                            .build();
                    //response를 보내주는 StreamObserver
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                } catch (ErrorResponseException e) {
                    throw new RuntimeException(e);
                } catch (InsufficientDataException e) {
                    throw new RuntimeException(e);
                } catch (InternalException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (InvalidResponseException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (ServerException e) {
                    throw new RuntimeException(e);
                } catch (XmlParserException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }


    private OutputStream getFilePath(FileUploadRequest request) throws IOException {
        var fileName = request.getMetadata().getName() + "." + request.getMetadata().getType(); //ex)suzy.jpg
        return Files.newOutputStream(SERVER_BASE_PATH.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void writeFile(OutputStream writer, ByteString contents) throws IOException {
        writer.write(contents.toByteArray());
        writer.flush();
    }

    private void closeFile(OutputStream writer) {
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
    }

}
