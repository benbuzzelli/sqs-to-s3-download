package gfo.processing

import javax.inject.Singleton
import org.apache.camel.builder.RouteBuilder
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import io.micronaut.context.annotation.Value
import groovy.json.JsonSlurper

@Singleton
class ExtractJson extends RouteBuilder
{
    // @Value('${app.sqs.queue:"LidarData"}')
    @Value('${app.sqs.queue}')
    String sqsQueueName

    @Override
    void configure() throws Exception
    {
        Integer counter = 0

        Map<String, String> data

        bindToRegistry('client', AmazonSQSClientBuilder.defaultClient())
        bindToRegistry('s3client', AmazonS3ClientBuilder.defaultClient())

        from("aws-sqs://${sqsQueueName}?amazonSQSClient=#client&delay=500&maxMessagesPerPoll=10&deleteAfterRead=true")
            .unmarshal().json()
            .process { exchange ->

                def jsonSqsMsg = new JsonSlurper().parseText(exchange.in.body.Message)

                // TODO: This is just here to count how many images we have looked at while
                // monitoring the SQS queue
                if (jsonSqsMsg.Records[0].eventVersion == "2.1") {
                    counter++
                    println "Images processed ${counter}"
                }

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
                String fileType = fileItems.last()

                String satName

                // TODO: Can we make this less hard coded?
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

                // If it the eventName attribute is ObjectCreated:CompleteMultipartUpload then the file has been completely uploaded
                // and is available for download.  In addition, if the file type is a .zip then begin processing it.
                // TODO: eventName and fileType check should be configurable --> static final const at top
                if (data.eventName == "ObjectCreated:CompleteMultipartUpload" && fileType == "zip") {
                    println "-"*100
                    println "Checking to see if we should download ${objectKeyName}..."

                    // Check to see if it is blacksky and a 'nitf-non-ortho' file.
                    if (satName == "blacksky" && objectKeyName.contains("nitf-non-ortho")) {
                        println "It is a Blacksky image. The filename contains the phrase 'nitf-non-ortho'. We should download it! 👍"
                        println "-"*100
                        //downloadSatImage(data?.bucketName, data?.objectKey, objectKeyName, satName)
                    }
                    // Check to see if it is sksat and a 'SkySatScene'.
                    else if (satName == "skysat" && objectKeyName.contains("SkySatScene")){
                        println "It is a Skysat image. The filename contains the phrase 'SkySatScene'. We should download it! 👍"
                        println "-"*100
                        //downloadSatImage(data?.bucketName, data?.objectKey, objectKeyName, satName)
                    } else {
                        println "Nope, it doesn't fit our criteria. We shouldn't download it! 🚫"
                        println "-"*100
                    }

                }

            }
            //.to("log:DEBUG?showBody=true&showHeaders=true")

    }

    void downloadSatImage (String bucketName, String objectKey, String objectKeyName, String satName) {

        println "#"*40
        println "SQS message received. Copying from S3 bucket: ${bucketName}/${objectKey} " +
                "to file: /maxar-${satName}-data/ingest/${objectKeyName}"
        println "#"*40

        // TODO: Find a way to do a check to see if the file exists on the bucket.
        // Otherwise you will get an error:
        // Jul 15, 2020 8:36:57 PM com.amazonaws.services.s3.internal.S3AbortableInputStream close
        // WARNING: Not all bytes were read from the S3ObjectInputStream, aborting HTTP connection.
        // This is likely an error and may result in sub-optimal behavior.
        // Request only the bytes you need via a ranged GET or drain the input stream after use.
        File filePath = new File("/maxar-${satName}-data/ingest/${objectKeyName}.temp")
        //File filePath = new File("/tmp/${objectKeyName}.temp")

        if (!filePath.exists()) {

            InputStream inputStream = null
            try {

                def s3client = getContext().getRegistry().lookupByName("s3client")
                inputStream = s3client.getObject(bucketName, objectKey).getObjectContent()

                filePath.withOutputStream {out ->
                    out << inputStream
                }
                println filePath.renameTo("/maxar-${satName}-data/ingest/${objectKeyName}")
                //println filePath.renameTo("/tmp/${objectKeyName}")

            } finally {
                inputStream?.close()

                println "-"*40
                println "The file has been fully downloaded"
                println "-"*40

            }
    }

}

}