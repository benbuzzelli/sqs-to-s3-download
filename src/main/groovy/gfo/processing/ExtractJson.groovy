package gfo.processing

import groovy.json.JsonSlurper
import io.micronaut.context.annotation.Value
import javax.inject.Singleton
import org.apache.camel.builder.RouteBuilder

import com.amazonaws.services.sqs.AmazonSQSClientBuilder

@Singleton
class ExtractJson extends RouteBuilder {

    @Value('${app.sqs.gfoQueue}')
    String gfoQueue

    @Value('${app.s3.bucket.to}')
    String s3BucketNameTo

    @Value('${app.s3.bucket.from}')
    String s3BucketNameFrom

    @Value('${app.s3.directory.blackTempZip}')
    String BlackSkyDirectory

    @Value('${app.s3.directory.skyTempZip}')
    String SkySatDirectory

    @Value('${app.s3.directory.GFOTempZip}')
    String GFOTempDirectory

    public String objectKeyName

    @Override
    public void configure() throws Exception {
        bindToRegistry('client', AmazonSQSClientBuilder.defaultClient())

            from("aws-sqs://${gfoQueue}?amazonSQSClient=#client&delay=1000&maxMessagesPerPoll=5")
                    .unmarshal().json()
                    .process { exchange ->

                        println("--------Begin Message---------------------------")
                        println(exchange.in.body.Message)
                        println("--------End Message-----------------------------")

                        def jsonSlurper = new JsonSlurper().parseText(exchange.in.body.Message)
                        def data = [
                                bucketName: jsonSlurper.Records[0].s3.bucket.name,
                                objectKey : jsonSlurper.Records[0].s3.object.key]

                        List<String> objectKeyItems = data.objectKey.split("/")
                        objectKeyName = objectKeyItems.last()

                        exchange.in.setHeaders(exchange.getIn().getHeaders())
                        exchange.in.setHeader("CamelAwsS3Key", objectKeyName)

                        exchange.in.setHeader("CamelAwsS3DestinationKey", objectKeyName)
                        exchange.in.setHeader("CamelAwsS3BucketName", s3BucketNameFrom)
                        exchange.in.setHeader("CamelAwsS3BucketDestinationName", s3BucketNameTo)

                        println("ObjectKeyName: " + objectKeyName)

                    }
                    .choice()
                        .when(header("CamelAwsS3Key").endsWith("nitf-non-ortho.zip"))
                            .to("aws-s3://${s3BucketNameTo}?useIAMCredentials=true&deleteAfterRead=false&operation=copyObject")
                    .end()
    }
}

