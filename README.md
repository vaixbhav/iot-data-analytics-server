# MP1a: The Land of Erehwon

Implemented a serverless application running on **AWS Lambda**.`template.yaml` is the config file for the **Lambda** function that specifies the **ReST API path** : `\lmbdafn`, and the **Handler** : `cpen221.mp3.lambda.Function::handleRequest`.
Then utilized **AWS's** `sam` to build and deploy `mp3-Erling` on AWS. Currently the lambda function is triggered at the endpoint `https://2a9yquga3f.execute-api.us-east-2.amazonaws.com/default/IoT_Prediction_Overhead/lmbdafn` with a `HTTP GET`  request with three parameters. This endpoint is hit by a `PredictorThread`.
