package org.xpen.hello.storm;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldBolt extends BaseRichBolt {
	
    public static Logger LOG = LoggerFactory.getLogger(HelloWorldBolt.class);
	
	private static final long serialVersionUID = -841805977046116528L;
	
	private int myCount = 0;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
	}

	@Override
	public void execute(Tuple input) {
		String test = input.getStringByField("sentence");
		if(test == "Hello World"){
			myCount++;
			System.out.println("Found a Hello World! My Count is now: " + Integer.toString(myCount));
			LOG.debug("Found a Hello World! My Count is now: " + Integer.toString(myCount));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("myCount"));

	}

}
