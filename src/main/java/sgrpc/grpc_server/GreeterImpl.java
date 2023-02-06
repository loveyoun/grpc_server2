package sgrpc.grpc_server;

import greet.GreeterGrpc;
import greet.GreeterOuterClass;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GreeterImpl extends GreeterGrpc.GreeterImplBase{
    @Override
    public void hello(GreeterOuterClass.Request request, StreamObserver<GreeterOuterClass.Response> responseObserver) {
        log.info("GreeterImpl#hello - {}, {}", request.getName(), request.getAge());
        GreeterOuterClass.Response response = GreeterOuterClass.Response.newBuilder()
                .setStr(" = grpc service response")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        //super.hello(request, responseObserver);
    }

}
