package com.project.analytics.database.minio;

import com.project.analytics.database.postgres.Container;
import com.project.analytics.database.postgres.Trigger;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MinioController {
    public MinioClient minioClient;
    MinioController(){
        minioClient =
                MinioClient.builder()
                        .endpoint("http://127.0.0.1:9000")
                        .credentials("0T4djQNXJSgmwQIw", "yUPbWBqXSmPttDJBeds2E8t4S4KGOnKn")
                        .build();
    }
    public InputStream getObject(String bucket, String object) {
        System.out.println("trying to get" + bucket + " " + object);
        try {
            String url =
                    minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(bucket)
                                    .object(object)
                                    .expiry(1, TimeUnit.DAYS)
                                    .expiry(1, TimeUnit.DAYS)
                                    .build());
            System.out.println(url);
            return new URL(url).openStream();
        } catch (MinioException e){
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void sendScripts(String filePath, String bucketName, String objectName, Boolean isNew) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (!isNew){
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        }
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(filePath)
                        .build());
    }

    public void deleteMinIOScripts(List<Container> deletedContainer, String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<DeleteObject> objects = new LinkedList<>();
        for (Container c : deletedContainer){
            System.out.println("delete " + objectName+c.getContainerId().toString());
            objects.add(new DeleteObject(objectName+c.getContainerId().toString()));
        }
        System.out.println(objects);
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(bucketName)
                        .objects(objects)
                        .build());
        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            System.out.println(
                    "Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }
}
