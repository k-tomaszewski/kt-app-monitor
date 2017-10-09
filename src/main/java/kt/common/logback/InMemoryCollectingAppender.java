package kt.common.logback;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.LayoutBase;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Custom appender for Logback framework.
 */
public class InMemoryCollectingAppender<E> extends AppenderBase<E> {
	
	static final int MSG_LIMIT = 250;
	static final int COUNTER_LIMIT = 2 * MSG_LIMIT;
	private static final List<LogMessage> logMessages = Collections.synchronizedList(new LinkedList<>());
	private static final AtomicInteger counter = new AtomicInteger();	
	
	private LayoutBase<E> layout;
	private boolean started;

	@Override
	protected void append(E e) {
		if (started) {
			logMessages.add(new LogMessage(layout.doLayout(e), System.currentTimeMillis()));
			if (counter.incrementAndGet() > COUNTER_LIMIT) {
				prune(logMessages);
				counter.set(logMessages.size());
			}
		}
	}
	
	@Override
	public void start() {
		started = (layout != null);
		if (!started) {
			System.err.println("InMemoryCollectingAppender not started");
		} else {
			super.start();
		}
	}

	public void setLayout(LayoutBase<E> layout) {
		this.layout = layout;
	}
	
	public static List<String> getLogMessages(long startingFromMillis) {
		List<String> result = new LinkedList<>();
		synchronized (logMessages) {
			Iterator<LogMessage> iter = logMessages.iterator();
			while (iter.hasNext()) {
				LogMessage logMsg = iter.next();
				if (logMsg.getTime() < startingFromMillis) {
					iter.remove();
					counter.decrementAndGet();
				} else {
					result.add(logMsg.getText());
				}
			}
		}
		return result;
	}

	static void prune(List<LogMessage> logMessages) {
		final int toRemove = logMessages.size() - MSG_LIMIT;
		if (toRemove > 0) {
			synchronized (logMessages) {
				for (int i = 0; i < toRemove; ++i) {
					logMessages.remove(0);
				}
			}
		}
	}
}
