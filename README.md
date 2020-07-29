# sqs-to-s3-download

#### Purpose:
 Monitors a SQS queue for messages, parses the messages, and then downloads specified file types (.zips) 
 

#### Deploying:

###### Requirements
* [Helm](https://helm.sh/docs/intro/install/)
###### Optional
* [Skaffold](https://skaffold.dev/docs/install/)

_Note_: The application can be deployed using vanilla Helm, or Skaffold which uses Helm under the hood.  Skaffold
 allows for rapid deployment capabilities while developing.
 
 ###### Mandatory Steps
1. Log into Kubernetes environment: `gimme-aws-creds`
2. Log into Docker: `docker login <DOCKER_REPOSITORY>`
3. Set the following environment variable: `export SQS_QUEUE=arn:aws:<YOUR_QUEUE_REGION>:<YOUR_ACCOUNT>:<YOUR_SQS_QUEUE_NAME>`

###### Helm only
`helm install --set sqsQueue=$SQS_QUEUE <HELM_DEPLOY_NAME> chart`

###### Skaffold with Helm

`skaffold dev` Uses Helm to deploy to Kubernetes. However, Skaffold is set to monitor any code or config changes, and
 automatically redeploys the application with the new changes.
 
 `skaffold run` Uses Helm to deploy to Kubernetes.  This is the same as the vanilla Helm install. 

