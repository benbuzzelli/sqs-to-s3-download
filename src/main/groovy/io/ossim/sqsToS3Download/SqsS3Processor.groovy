package io.ossim.sqsToS3Download


import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Singleton
import org.apache.camel.builder.RouteBuilder
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import io.micronaut.context.annotation.Value
import groovy.json.JsonSlurper

@Singleton
class SqsS3Processor extends RouteBuilder
{
    Logger logger = LoggerFactory.getLogger("logFile")

    @Value('${app.sqs.queue}')
    String sqsQueueName

    String msgEventNameCheck = "ObjectCreated:CompleteMultipartUpload"
    String msgFileExtensionCheck = "zip"

    @Override
    void configure() throws Exception
    {

        Map<String, String> data

        bindToRegistry('client', AmazonSQSClientBuilder.defaultClient())
        bindToRegistry('s3client', AmazonS3ClientBuilder.defaultClient())

        from("aws-sqs://${sqsQueueName}?amazonSQSClient=#client&delay=500&maxMessagesPerPoll=10&deleteAfterRead=true")
            .unmarshal().json()
            .process { exchange ->

                logger.info("-"*75)

                def jsonSqsMsg = new JsonSlurper().parseText(exchange.in.body.Message)

                data = [
                    eventName: jsonSqsMsg.Records[0].eventName,
                    bucketName: jsonSqsMsg.Records[0].s3.bucket.name,
                    objectKey: jsonSqsMsg.Records[0].s3.object.key
                ] as Map<String, String>

                // Gets the file name from the object key
                List<String> objectKeyItems = data.objectKey.split("/")
                String objectKeyName = objectKeyItems.last()

                // Gets the file extension type (zip, tif, png, txt, etc)
                List<String> fileItems = objectKeyName.split("\\.")
                String fileExtension = fileItems.last()

                String satName

                // TODO: Remove old nga bucket name: kno-gv-o1
                switch(data.bucketName) {
                    case "kno-gv-o1":
                        satName = "blacksky"
                        break
                    case "nga-diode-o1":
                        satName = "blacksky"
                        break
                    case "nga-non-diode-o1":
                        satName = "skysat"
                        break
                    default:
                        return
                        break
                }

                logger.info( "Processing ${objectKeyName} from bucket: ${data.bucketName}")

                // If it the eventName attribute is ObjectCreated:CompleteMultipartUpload then the file has been completely uploaded
                // and is available for download.  In addition, if the file type is a .zip then begin processing it.
                if (data.eventName == msgEventNameCheck && fileExtension == msgFileExtensionCheck) {

                    // Check to see if it is blacksky and a 'nitf-non-ortho' file.
                    if (satName == "blacksky" && objectKeyName.contains("nitf-non-ortho")) {
                        logger.info( "👍  It is a Blacksky image, and the filename contains the phrase 'nitf-non-ortho'. We should download it! ")
                        downloadSatImage(data?.bucketName, data?.objectKey, objectKeyName, satName)

                    }
                    // Check to see if it is skysat and a 'SkySatScene'.
                    else if (satName == "skysat" && objectKeyName.contains("SkySatScene")){
                        logger.info("👍  It is a Skysat image, and the filename contains the phrase 'SkySatScene'. We should download it!")
                        downloadSatImage(data?.bucketName, data?.objectKey, objectKeyName, satName)

                    } else {
                        logger.info("🚫  Nope, it doesn't fit our criteria (nitf-non-ortho or SkysatScene). We shouldn't download it!")
                    }
                } else {
                    logger.info("🚫  Nope, it doesn't fit our criteria (zip and MPU). Event Name:${data.eventName}, File Extension:${fileExtension}  We shouldn't download it!")
                }

            }
            //.to("log:DEBUG?showBody=true&showHeaders=true")

    }

    void downloadSatImage (String bucketName, String objectKey, String objectKeyName, String satName) {

        logger.info( "Attempting download of: ${bucketName}/${objectKey}")

        // TODO: Make this configurable
        File downloadPath = new File("/3pa-${satName}/processing/")
        File file = new File("${downloadPath}/${objectKeyName}.temp")

        logger.info("Creating file: ${file}")

        if (!file.exists()) {

            InputStream inputStream = null
            try {

                def s3client = getContext().getRegistry().lookupByName("s3client")
                inputStream = s3client.getObject(bucketName, objectKey).getObjectContent()

                file.withOutputStream {out ->
                    out << inputStream
                }

                file.renameTo("/3pa-${satName}/ingest/${objectKeyName}")
                logger.info("Renamed file to: /3pa-${satName}/ingest/${objectKeyName}")
                logger.info("✅  File downloaded successfully.")


            }
            catch (Exception e) {
                logger.error("${e.message}")
                logger.error("❗ Unable to download file")

            }
             catch (AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                 logger.error("${e.message}")

            } catch (SdkClientException e) {
                // Amazon S3 couldn't be contacted for a response, or the client
                // couldn't parse the response from Amazon S3.
                logger.error("${e.message}")

            }
            finally {
                inputStream?.close()

            }
    } else {
            logger.info("⚠️  File already exists on disk. Skipping.")
        }

}

}