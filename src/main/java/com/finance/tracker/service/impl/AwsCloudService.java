package com.finance.tracker.service.impl;

import com.finance.tracker.model.enums.MonthEnum;
import com.finance.tracker.model.properties.AwsProperties;
import com.finance.tracker.service.CloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

/**
 * AwsCloudService provides implementation of CloudService interface, for uploading files to Amazon S3
 * and generating secure pre-signed URLs for file access.
 * This service leverages the AWS SDK for Java (v2) to handle file storage operations,
 * using configuration values provided through AwsProperties.
 *
 * The main purpose of this class is : Upload PDF reports or documents to an S3 bucket.
 * Generate time-limited pre-signed URLs for secure download access.
 *
 */
@Service
@RequiredArgsConstructor
public class AwsCloudService implements CloudService {

    /**
     * AWS S3 client for performing storage operations.
     */
    private final S3Client s3Client;

    /**
     * AWS configuration properties such as region, bucket name, and URL expiry duration.
     */
    private final AwsProperties awsProperties;

    /**
     * Uploads a file to AWS S3 and returns a pre-signed URL for secure access.
     *
     * @param fileContent the byte content of the file to be uploaded.
     * @param userId      the unique identifier of the user uploading the file.
     * @param monthEnum   the month associated with the uploaded report.
     * @param year        the year associated with the uploaded report.
     * @param fileName    the name (key) to assign to the file in the S3 bucket.
     * @return a pre-signed URL granting temporary access to the uploaded file.
     * @throws RuntimeException if the upload process fails due to S3 errors.
     */
    @Override
    public String uploadFileToCLoudStorage(byte[] fileContent, UUID userId, MonthEnum monthEnum, int year, String fileName) {
        // Create a unique file name

        try {
            // Upload the file to S3
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(awsProperties.getBucket())
                    .key(fileName)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(fileContent));

            // Generate a pre-signed URL for secure access
            return generatePreSignedUrl(fileName);

        } catch (S3Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error uploading file to S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Generate a pre-signed URL for the uploaded file, valid for 24 hours.
     * The generated URL provides secure, temporary access to the S3 object and
     * automatically expires after a duration defined in AwsProperties.
     * @param fileKey the key (name) of the file in the S3 bucket.
     * @return a pre-signed URL as a String.
     * @throws RuntimeException if URL generation fails due to AWS or network errors.
     */
    public String generatePreSignedUrl(String fileKey) {
        try (S3Presigner preSigner = S3Presigner.builder()
                .region(Region.of(awsProperties.getRegion()))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(awsProperties.getBucket())
                    .key(fileKey)
                    .build();

            GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(awsProperties.getSignedUrlExpiry()))
                    .getObjectRequest(getObjectRequest)
                    .build();

            URL presignedUrl = preSigner.presignGetObject(preSignRequest).url();
            return presignedUrl.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error generating pre-signed URL", e);
        }
    }
}
