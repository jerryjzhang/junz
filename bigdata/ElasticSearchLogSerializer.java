package com.ms.msqe.tdms.log.serializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.elasticsearch.ContentBuilderUtil;
import org.apache.flume.sink.elasticsearch.ElasticSearchEventSerializer;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearchLogSerializer implements ElasticSearchEventSerializer {
	private static final Pattern PATTERN = Pattern
			.compile("(\\d+:\\d+:\\d+.\\d+) \\[(.*)]\\s+(\\S+)\\s+(\\S+) (.*)");
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Override
	public void configure(Context context) {
		// NO-OP...
	}

	@Override
	public void configure(ComponentConfiguration conf) {
		// NO-OP...
	}

	@Override
	public XContentBuilder getContentBuilder(Event event) throws IOException {
		XContentBuilder builder = jsonBuilder().startObject();
		
		appendHeaders(builder, event);
		appendBody(builder, event);

		return builder;
	}

	private void appendBody(XContentBuilder builder, Event event)
			throws IOException {
		String str = new String(event.getBody(), "UTF-8");
		Matcher m = PATTERN.matcher(str);
		if (m.find()) {
			Date timestamp = new Date();
			try {
				Date date = sdf.parse(m.group(1));
				timestamp.setHours(date.getHours());
				timestamp.setMinutes(date.getMinutes());
				timestamp.setSeconds(date.getSeconds());
			} catch (ParseException e) {
				timestamp = null;
			}
			if (timestamp != null) {
				// ContentBuilderUtil.appendField(builder, "timestamp",
				// ddf.format(timestamp).getBytes());
				builder.field("@timestamp", timestamp);
			}
			ContentBuilderUtil.appendField(builder, "thread", m.group(2)
					.getBytes());
			ContentBuilderUtil.appendField(builder, "level", m.group(3)
					.getBytes());
			ContentBuilderUtil.appendField(builder, "logger", m.group(4)
					.getBytes());
			ContentBuilderUtil.appendField(builder, "message", m.group(5)
					.getBytes());
		}
	}
	
	private void appendHeaders(XContentBuilder builder, Event event)
			throws IOException {
		Map<String, String> headers = event.getHeaders();
		for (String key : headers.keySet()) {
			ContentBuilderUtil.appendField(builder, key, headers.get(key)
					.getBytes(charset));
		}
	}

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date date = sdf.parse("08:11:44.362");
		Date dateTime = new Date();
		dateTime.setHours(date.getHours());
		dateTime.setMinutes(date.getMinutes());
		dateTime.setSeconds(date.getSeconds());
		System.out.println(ddf.format(dateTime));
	}
}
