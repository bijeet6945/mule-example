# mule-logging
---
Example project to update Mule logging for
1. adding an "Apache / Nginx" access log 
2. adding a truncated Mule message.id to all Mule flow log messages

## Access Log
The formatted access log displays the following fields:
* Timestamp
* truncated Mule message.id
* client's remote IP address
* http method
* http status
* show an exception symbol, if exception occured within flow
* peformance time of flow in milliseconds
* client's user agent
* flow name
* if exists, correlation id, else display full Mule message.id

An example access log:
```
2019-03-14 12:09:04,462  7ce02b  /127.0.0.1:58444         GET       200    http://localhost/correlation  |  255ms, userAgent=curl/7.59.0, flowName=CorrelationIdExampleFlow, correlationId=7ce
2019-03-14 12:09:16,765  845fb9  /127.0.0.1:58447         GET       200    http://localhost/simple  |   9ms, userAgent=curl/7.59.0, flowName=SimpleExampleFlow, message.id=845fb940-4673-11e9-
2019-03-14 12:09:56,792  993017  /127.0.0.1:58451         GET       200    http://localhost/wait  |  5116ms, userAgent=curl/7.59.0, flowName=WaitExampleFlow, message.id=993017c0-4673-11e9-ad
2019-03-14 12:10:15,733  a77e33  /127.0.0.1:58454         GET       ??? E  http://localhost/error  |  57ms, userAgent=curl/7.59.0, flowName=ErrorExampleFlow, message.id=a77e33c0-4673-11e9-ad
```

## Prefix application log messages with a Mule message.id
An example application log with a truncated Mule message.id:
```
INFO  2019-03-14 13:25:17,372 [.worker.01] m.clark.mule.i.AccessLogInterceptor: 22af53 SimpleExampleFlow - GET http://localhost/simple - /127.0.0.1:59128
INFO  2019-03-14 13:25:17,374 [.worker.01] g.mule.api.p.LoggerMessageProcessor: 22af53 some simple logging
INFO  2019-03-14 13:25:21,337 [.worker.01] m.clark.mule.i.AccessLogInterceptor: 251382 WaitExampleFlow - GET http://localhost/wait - /127.0.0.1:59130
INFO  2019-03-14 13:25:21,337 [.worker.01] g.mule.api.p.LoggerMessageProcessor: 251382 before wait
INFO  2019-03-14 13:25:26,576 [.worker.01] g.mule.api.p.LoggerMessageProcessor: 251382 after wait
INFO  2019-03-14 13:25:30,911 [.worker.01] m.clark.mule.i.AccessLogInterceptor: 2ac862 CorrelationIdExampleFlow - GET http://localhost/correlation - /127.0.0.1:59132
INFO  2019-03-14 13:25:30,911 [.worker.01] g.mule.api.p.LoggerMessageProcessor: 2ac862 setting correlationId
```
