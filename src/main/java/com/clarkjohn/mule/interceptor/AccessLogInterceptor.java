package com.clarkjohn.mule.interceptor;

import org.apache.log4j.MDC;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.interceptor.AbstractEnvelopeInterceptor;
import org.mule.management.stats.ProcessingTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogInterceptor extends AbstractEnvelopeInterceptor {

    final private static Logger LOG = LoggerFactory.getLogger(AccessLogInterceptor.class);
    final private static Logger ACCESS_LOG = LoggerFactory.getLogger("accessLog");

    private static final String ACCESS_LOG_EXECPTION_SYMBOL = "E";
    private static final int MESSAGE_ID_SUBSTRING_LENGTH = 6;

    final private static String VARIABLE_METHOD = "accessLog.http.method";
    final private static String VARIABLE_REQUEST_URI = "accessLog.requestUri";
    final private static String VARIABLE_REMOTE_ADDRESS = "accessLog.http.remote.address";
    final private static String VARIABLE_USER_AGENT = "accessLog.user-agent";

    @Override
    public MuleEvent before(MuleEvent muleEvent) throws MuleException {
      
        String shortMessageId = getShortMessageId(muleEvent.getMessage().getUniqueId());
        
        MDC.put("requestDetails", shortMessageId + " ");

        String httpMethod = muleEvent.getMessage().getInboundProperty("http.method", "NA");
        String requestUri = muleEvent.getMessage().getInboundProperty("http.scheme", "NA") + //
                "://" + muleEvent.getMessage().getInboundProperty("host", "NA") + //
                muleEvent.getMessage().getInboundProperty("http.request.uri", "NA"); //
        String remoteAddress = muleEvent.getMessage().getInboundProperty("http.remote.address", "NA");

        muleEvent.getMessage().setInvocationProperty(VARIABLE_METHOD, httpMethod);
        muleEvent.getMessage().setInvocationProperty(VARIABLE_REQUEST_URI, requestUri);
        muleEvent.getMessage().setInvocationProperty(VARIABLE_REMOTE_ADDRESS, remoteAddress);
        muleEvent.getMessage().setInvocationProperty(VARIABLE_USER_AGENT, muleEvent.getMessage().getInboundProperty("user-agent", "NA"));

        if (logger.isInfoEnabled()) {
            LOG.info("{} - {} {} - {}", 
                    muleEvent.getFlowConstruct().getName(), 
                    httpMethod, 
                    requestUri, 
                    remoteAddress); 
        }

        return muleEvent;
    }

    private String getShortMessageId(String messageId) {
        if (messageId.length() > MESSAGE_ID_SUBSTRING_LENGTH) {
            return messageId.substring(0, MESSAGE_ID_SUBSTRING_LENGTH);
        } else {
            return messageId;
        }
    }

    @Override
    public MuleEvent after(MuleEvent muleEvent) throws MuleException {
        return muleEvent;
    }

    @Override
    public MuleEvent last(MuleEvent muleEvent, ProcessingTime time, long startTime, boolean exceptionWasThrown) throws MuleException {

        if (logger.isInfoEnabled()) {

            String httpMethod = muleEvent.getMessage().getInvocationProperty(VARIABLE_METHOD);
            String requestUri = muleEvent.getMessage().getInvocationProperty(VARIABLE_REQUEST_URI);
            String remoteAddress = muleEvent.getMessage().getInvocationProperty(VARIABLE_REMOTE_ADDRESS);
            String userAgent = muleEvent.getMessage().getInvocationProperty(VARIABLE_USER_AGENT);

            ACCESS_LOG.info(String.format("%-23s  %-8s  %-3s %-1s  %s  |  %4s, userAgent=%s, flowName=%s%s", //
                    remoteAddress, //
                    httpMethod, //
                    muleEvent.getMessage().getInboundProperty("http.status", (exceptionWasThrown) ? "???" : 200), //
                    (exceptionWasThrown) ? ACCESS_LOG_EXECPTION_SYMBOL : " ", //
                    requestUri, //
                    System.currentTimeMillis() - startTime + "ms", //
                    userAgent, //
                    muleEvent.getFlowConstruct().getName(), //
                    getMessageId(muleEvent.getMessage()))); //
        }

        MDC.clear();

        return muleEvent;
    }

    //use correlationId if set, else use Mule's message id
    private String getMessageId(MuleMessage muleMessage) {
       return ", " + ((muleMessage.getCorrelationId() != null) ? "correlationId=" + muleMessage.getCorrelationId() : "message.id=" + muleMessage.getUniqueId());
    }

}
