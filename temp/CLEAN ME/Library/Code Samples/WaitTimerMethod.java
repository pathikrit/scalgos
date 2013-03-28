import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

// NOTE: this class is currently broken for some unknown reason.

public final class WaitTimerMethod extends Thread {
	public final Method method;
	public final Object receiver;
	public final Object[] args;	
	public volatile Object value;
	public volatile Throwable exception;	
	public volatile boolean finished;
	
	public WaitTimerMethod(Method method, Object receiver, Object... args) {		
		this.method = method;
		this.receiver = receiver;
		this.args = args;		
	}
	
	public void run() {			
		try {
			value = method.invoke(receiver, args);			
		} catch(InvocationTargetException ex) {
			exception = ex.getCause();
		} catch(Throwable ex) {
			exception = ex;
		}
		// finally, signal completion
		this.finished = true;
		synchronized(this) {
			notifyAll();
		}
	}	
	
	public static Object exec(int timeout, Method method, Object receiver,
			Object... args) throws Throwable {
						
		WaitTimerMethod stm = new WaitTimerMethod(method,receiver,args);		
		
		synchronized(stm) {
			// start child thread
			stm.start();				
			// now, wait for him to call notify 				
			stm.wait(timeout);			
		}

		if (stm.finished) {
			if (stm.exception != null) {
				throw stm.exception;
			} else {
				// clear interrupted status
				return stm.value;
			}
		}
		
		// ok, timeout now in effect
		stm.stop(); // why do I have to use a deprecated method?
		throw new TimeoutException("timed out after " + timeout + "ms");		
	}		
}
