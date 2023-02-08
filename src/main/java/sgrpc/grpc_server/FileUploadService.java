package sgrpc.grpc_server;

import com.google.protobuf.ByteString;
import file.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileUploadService extends FileServiceGrpc.FileServiceImplBase{

    private static final Path SERVER_BASE_PATH = Paths.get("src/main/resources/output");

    @Override
    public StreamObserver<FileUploadRequest> upload(StreamObserver<FileUploadResponse> responseObserver) {
        System.out.println("Server Started, listening on {{port}}");

        return new StreamObserver<FileUploadRequest>() {
            OutputStream writer;
            Status status = Status.IN_PROGRESS;

            @Override
            public void onNext(FileUploadRequest fileUploadRequest) {
                try {
                    if(fileUploadRequest.getMetadata() == null){ //fileUploadRequest.hasMetadata() 안 됨
                        writer = getFilePath(fileUploadRequest);
                    }else{
                        writeFile(writer, fileUploadRequest.getFile().getContents());
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
                closeFile(writer);
                status = Status.IN_PROGRESS.equals(status) ? Status.SUCCESS : status;
                FileUploadResponse response = FileUploadResponse.newBuilder()
                        .setStatus(status)
                        .build();

                //response를 주는 StreamObserver
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    private OutputStream getFilePath(FileUploadRequest request) throws IOException {
        var fileName = request.getMetadata().getName() + "." + request.getMetadata().getType(); //ex)suzy.jpg
        return Files.newOutputStream(SERVER_BASE_PATH.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    private void writeFile(OutputStream writer, ByteString contents) throws IOException{
        writer.write(contents.toByteArray());
        writer.flush();
    }
    private void closeFile(OutputStream writer){
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
    }

}
