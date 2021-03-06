package stroom.agent.util.zip;

import stroom.agent.util.concurrent.SimpleExecutor;
import stroom.agent.util.logging.StroomLogger;
import stroom.agent.util.shared.Monitor;
import stroom.agent.util.thread.ThreadScopeRunnable;

public abstract class StroomZipRepositorySimpleExecutorProcessor extends StroomZipRepositoryProcessor {

	private SimpleExecutor simpleExecutor;

	private final StroomLogger LOGGER = StroomLogger.getLogger(StroomZipRepositorySimpleExecutorProcessor.class);
	private int threadCount = 1;

	public StroomZipRepositorySimpleExecutorProcessor(final Monitor monitor) {
		super(monitor);
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(final int threadCount) {
		this.threadCount = threadCount;
	}

	@Override
	public final synchronized void startExecutor() {
		if (simpleExecutor != null && !simpleExecutor.isStopped()) {
			throw new RuntimeException("simpleExecutor is still running?");
		}
		// Start up the thread worker pool
		simpleExecutor = new SimpleExecutor(getThreadCount());

	}

	@Override
	public void stopExecutor(final boolean now) {
		if (simpleExecutor != null) {
			simpleExecutor.stop(now);
		}
	}

	@Override
	public void waitForComplete() {
		simpleExecutor.waitForComplete();

	}

	@Override
	public void execute(final String message, final Runnable runnable) {
		simpleExecutor.execute(new ThreadScopeRunnable() {
			@Override
			protected void exec() {
				try {
					runnable.run();
				} catch (final Exception ex) {
					LOGGER.error("doRunWork()", ex);
				}
			}
		});
	}
}
