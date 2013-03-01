package berlin.reiche.securitas.controller;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public abstract class Controller {

	/**
	 * Inbox handler receives messages from the activity and its {@link Looper}
	 * processes the messages.
	 */
	final Handler inboxHandler;

	/**
	 * Thread for the inbox handler that contains already a {@link Looper}.
	 */
	final HandlerThread inboxHandlerThread;
	final List<Handler> outboxHandlers;

	public Controller() {
		inboxHandlerThread = new HandlerThread("Controller Inbox");
		inboxHandlerThread.start();

		inboxHandler = new InboxHandler(inboxHandlerThread.getLooper(), this);
		outboxHandlers = new ArrayList<Handler>();
	}

	/**
	 * Needs to be overridden by the specific controller in order to react to
	 * different messages.
	 * 
	 * @param msg
	 *            the message received.
	 */
	abstract void handleMessage(Message msg);

	/**
	 * Asks the inbox handler thread to shutdown gracefully.
	 */
	public void dispose() {
		inboxHandlerThread.getLooper().quit();
	}

	public final void addOutboxHandler(Handler handler) {
		outboxHandlers.add(handler);
	}

	public final void removeOutboxHandler(Handler handler) {
		outboxHandlers.remove(handler);
	}

	final void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj) {

		if (!outboxHandlers.isEmpty()) {
			for (Handler handler : outboxHandlers) {
				Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
			}
		}
	}

	public Handler getInboxHandler() {
		return inboxHandler;
	}

}
