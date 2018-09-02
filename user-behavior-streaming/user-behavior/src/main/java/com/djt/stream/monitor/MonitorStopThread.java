package com.djt.stream.monitor;

import com.djt.stream.service.BehaviorStatService;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.StreamingContextState;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Properties;

public class MonitorStopThread implements Runnable {
  private Logger log = Logger.getLogger(MonitorStopThread.class);

  private JavaStreamingContext javaStreamingContext;
  private Properties prop;

  public MonitorStopThread(JavaStreamingContext javaStreamingContext, Properties prop) {
    this.javaStreamingContext = javaStreamingContext;
    this.prop = prop;
  }

  public boolean isToStop() {
    BehaviorStatService service = BehaviorStatService.getInstance(prop);
    if ("stop".equals(service.getStatus())) {
      return true;
    }

    return false;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      StreamingContextState state = javaStreamingContext.getState();
      log.info("the streamingContextState of javaStreamingContext is " + state.toString());

      if (isToStop()) {
        log.info("begin to stop streaming...");

        if (state == StreamingContextState.ACTIVE) {
          javaStreamingContext.stop(true, true);
        } else if (state == StreamingContextState.STOPPED) {
          log.info("streaming be stoped");
          break;
        }
      }
    }
  }
}
