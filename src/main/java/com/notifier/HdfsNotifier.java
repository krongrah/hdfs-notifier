package com.notifier;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSInotifyEventInputStream;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent;
import org.apache.hadoop.hdfs.inotify.Event.UnlinkEvent;
import org.apache.hadoop.hdfs.inotify.EventBatch;
import org.apache.hadoop.hdfs.inotify.MissingEventsException;


//based on the hdfs notify example by OneFourSix, found at https://github.com/onefoursix/hdfs-inotify-example
//other source: https://www.slideshare.net/Hadoop_Summit/keep-me-in-the-loop-inotify-in-hdfs
public class HdfsNotifier {

	public static void main(String[] args) throws IOException, InterruptedException, MissingEventsException {

		//each change to the files in hdfs is given an id, called tx id
		long lastReadTxid = 0;

		//a tx id can be parsed as the second argument when starting the application,
		// to continue where it left off, rather than start over in each restart.
		if (args.length > 1) {
			lastReadTxid = Long.parseLong(args[1]);
		}

		System.out.println("lastReadTxid = " + lastReadTxid);

		//here, the first argument is parsed to the hdfs admin,
		// containing the path to the hdfs being listened to.
		HdfsAdmin admin = new HdfsAdmin(URI.create(args[0]), new Configuration());

		//an event stream is created here with the newest tx id, insuring that
		DFSInotifyEventInputStream eventStream = admin.getInotifyEventStream(lastReadTxid);

		while (true) {
			//each time this point is reached, the event batch collects all changes made
			// since it checked last, and to then process them below.
			EventBatch batch = eventStream.take();
			System.out.println("TxId = " + batch.getTxid());

			//the events are sorted by type, processed, whatever that means... todo
			for (Event event : batch.getEvents()) {
				System.out.println("event type = " + event.getEventType());
				switch (event.getEventType()) {
				case CREATE:
					CreateEvent createEvent = (CreateEvent) event;
					System.out.println("  path = " + createEvent.getPath());
					System.out.println("  owner = " + createEvent.getOwnerName());
					System.out.println("  ctime = " + createEvent.getCtime());
					break;
				case UNLINK:
					UnlinkEvent unlinkEvent = (UnlinkEvent) event;
					System.out.println("  path = " + unlinkEvent.getPath());
					System.out.println("  timestamp = " + unlinkEvent.getTimestamp());
					break;
				
				case APPEND:
					System.out.println("append");
					break;
				case CLOSE:
					System.out.println("close");
					break;
				case RENAME:
					System.out.println("rename");
					break;
				default:
					System.out.println("something else");
					break;
				}
			}
		}
	}
}

