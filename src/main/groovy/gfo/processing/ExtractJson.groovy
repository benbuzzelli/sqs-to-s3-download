package gfo.processing

import groovy.json.JsonSlurper
import io.micronaut.context.annotation.Value
import javax.inject.Singleton
import org.apache.camel.builder.RouteBuilder

import com.amazonaws.services.sqs.AmazonSQSClientBuilder

@Singleton
class ExtractJson extends RouteBuilder
{
    // @Value('${app.sqs.queue:"gegd-tpc"}')
    @Value('${app.sqs.queue}')
    String sqsQueueName

    // @Value('${app.s3.bucket.to:"nga-blacksky-data-test"}
    @Value('${app.s3.bucket.BlackSkyTo}')
    String s3BucketBlackSkyTo

    // @Value('${app.s3.bucket.from:"kno-gv-o1"}')
    @Value('${app.s3.bucket.BlackSkyFrom}')
    String s3BucketBlackSkyFrom

    // @Value('${app.s3.bucket.SkySatTo:"nga-skysat-data-test"}
    @Value('${app.s3.bucket.SkySatTo}')
    String s3BucketSkySatTo

    // @Value('${app.s3.bucket.from:"nga-non-diode-o1"}')
    @Value('${app.s3.bucket.SkySatFrom}')
    String s3BucketSkySatFrom

    // @Value('${app.s3.processed.dir:"ingest"}')
    @Value('${app.s3.processed.dir}')
    String processedDirectoryName

     @Override
    public void configure() throws Exception {
        def data

        bindToRegistry('client', AmazonSQSClientBuilder.defaultClient())

        from("aws-sqs://${sqsQueueName}?amazonSQSClient=#client&delay=1000&maxMessagesPerPoll=5")
                .unmarshal().json()
                .process { exchange ->

                    def jsonSlurper = new JsonSlurper().parseText(exchange.in.body.Message)

                    data = [
                            bucketName: jsonSlurper.Records[0].s3.bucket.name,
                            objectKey: jsonSlurper.Records[0].s3.object.key
                    ]

                    List<String> objectKeyItems = data.objectKey.split("/")
                    String objectKeyName = objectKeyItems.last()

                    String body = exchange.getIn().getBody(String.class);
                    exchange.getOut().setBody(body)
                    exchange.getOut().setHeaders(exchange.getIn().getHeaders())

                    println("#" * 25 + " NEW MESSAGE COPY " + "#" * 102)
                    println("-" * 25 + " OBJECT KEY " + "-" * 108)
                    println("objectKey: " + data.objectKey)
                    println("-" * 25 + " BUCKET NAME " + "-" * 107)
                    println("BucketName: " + data.bucketName)

                    // The key (file name) that will be copied from the S3_BUCKET_NAME_FROM
                    exchange.in.setHeader("CamelAwsS3Key", "${data.objectKey}")

                    //The key (file name) that will be used for the copied object
                    //exchange.in.setHeader("CamelAwsS3DestinationKey", "${processedDirectoryName}/${objectKeyName}")

                    if ((data.bucketName).toString().contentEquals("kno-gv-o1")) {

                        // The bucket we are copying to
                        //exchange.in.setHeader("CamelAwsS3BucketDestinationName", "${s3BucketBlackSkyTo}")

                        println("-" * 25 + " SQS INFO " + "-" * 110)
                        println "SQS message received.\nCopying from S3 bucket: ${s3BucketBlackSkyFrom}/${data.objectKey} " +
                                "\nto S3 bucket: ${s3BucketBlackSkyTo}/${processedDirectoryName}/${objectKeyName}"

                        ProcessBuilder pb = new ProcessBuilder( )
                        String[] command = "aws s3 sync s3://kno-gv-o1/16928865/SXO00000/Imagery/products/bsg/BSG-101-20200615-030558-1570502-nitf-non-ortho.zip ./BSG"
                        Process p = Runtime.getRuntime().exec(command);

//                    }else if((data.bucketName).toString().contentEquals("nga-skysat-data-test")) {
//
//                        // The bucket we are copying to
//                        exchange.in.setHeader("CamelAwsS3BucketDestinationName", "${s3BucketSkySatTo}")
//
//                        println("-"*25 + " SQS INFO " + "-"*110)
//                        println "SQS message received.\nCopying from S3 bucket: ${s3BucketSkySatFrom}/${data.objectKey} " +
//                                "\nCopying to S3 bucket: ${s3BucketSkySatTo}/${processedDirectoryName}/${objectKeyName}"
//                    }
                    }
                }
        //Logging information has been processed
        .to("log:info")
        .process { exchange ->
               exchange.in.setHeader("Content-Type", "application/json")
               exchange.getIn().setBody('{"bucketName": "' + data.bucketName + '", "objectKey": "' + data.objectKey + '"}')
        }
    }
}