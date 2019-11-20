import json
import boto3

def lambda_handler(event, context):
    # TODO implement
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('sojuRanking')

    
    data = sorted(table.scan()['Items'], key=lambda k: k['drinking'] , reverse=True)
    
    response_body = '<!DOCTYPE html> \
<html lang="ko"> \
<head> \
<title>Ranking of Soju</title> \
<meta charset="UTF-8"> \
<meta name="viewport" content="width=device-width, initial-scale=1"> \
<!--===============================================================================================--> \
<link rel="icon" type="image/png" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/images/icons/favicon.ico"/> \
<!--===============================================================================================--> \
<link rel="stylesheet" type="text/css" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/bootstrap/css/bootstrap.min.css"> \
<!--===============================================================================================--> \
<link rel="stylesheet" type="text/css" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/fonts/font-awesome-4.7.0/css/font-awesome.min.css"> \
<!--===============================================================================================--> \
<link rel="stylesheet" type="text/css" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/animate/animate.css"> \
<!--===============================================================================================--> \
<link rel="stylesheet" type="text/css" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/select2/select2.min.css"> \
<!--===============================================================================================--> \
<link rel="stylesheet" type="text/css" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/perfect-scrollbar/perfect-scrollbar.css"> \
<!--===============================================================================================--> \
<link rel="stylesheet" type="text/css" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/css/util.css">'
    response_body += '<link rel="stylesheet" type="text/css" href="https://soju-css.s3.ap-northeast-2.amazonaws.com/css/main.css"> \
<!--===============================================================================================--> \
</head> \
<body> \
<div class="limiter"> \
<div class="container-table100"> \
<div class="wrap-table100"> \
<div class="table"> \
<div class="row header"> \
<div class="cell"> \
Ranking \
</div> \
<div class="cell"> \
Name \
</div> \
<div class="cell"> \
Drinking \
</div> \
</div> \
'
    for i in range(len(data)):
        response_body += '<div class="row"> \
<div class="cell" data-title="Ranking"> \
{} \
</div> \
<div class="cell" data-title="Name"> \
{} \
</div> \
<div class="cell" data-title="Drinking"> \
{} \
</div> \
</div>'.format(i+1, data[i]['name'], data[i]['drinking'])

    response_body +='</div> \
</div> \
</div> \
</div> \
<!--===============================================================================================--> \
<script src="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/jquery/jquery-3.2.1.min.js"></script> \
<!--===============================================================================================--> \
<script src="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/bootstrap/js/popper.js"></script> \
<script src="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/bootstrap/js/bootstrap.min.js"></script> \
<!--===============================================================================================--> \
<script src="https://soju-css.s3.ap-northeast-2.amazonaws.com/vendor/select2/select2.min.js"></script> \
<!--===============================================================================================--> \
<script src="https://soju-css.s3.ap-northeast-2.amazonaws.com/js/main.js"></script> \
</body> \
</html>'
    return response_body
