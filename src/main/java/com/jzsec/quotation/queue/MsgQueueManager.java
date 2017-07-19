package com.jzsec.quotation.queue;

import com.jzsec.quotation.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by caodaoxi on 17-7-13.
 */
public class MsgQueueManager  {
    BlockingQueue<Message> queues = new LinkedBlockingQueue<Message>(1000);

    public void put(Message message) throws InterruptedException {
        queues.put(message);
    }

    public Message take() throws InterruptedException {
        return queues.take();
    }



}
