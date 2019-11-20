import json
import boto3

def lambda_handler(event, context):
    # TODO implement
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('sojuRanking')
    
    
    
    table.put_item(
        Item={
            'name': event['name'],
            'phoneNumber': event['phoneNumber'],
            'drinking' : event['drinking']
        })
    return {
        'statusCode': 200
    }
