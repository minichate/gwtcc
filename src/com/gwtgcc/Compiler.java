package com.gwtgcc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.CompileTaskRunner;
import com.google.gwt.dev.CompileTaskRunner.CompileTask;
import com.google.gwt.dev.GWTCCPrecompileTaskArgProcessor;
import com.google.gwt.dev.GWTCCPrecompileTaskOptionsImpl;
import com.google.gwt.dev.Link;
import com.google.gwt.dev.Precompile;
import com.google.gwt.dev.shell.CheckForUpdates;
import com.google.gwt.dev.shell.CheckForUpdates.UpdateResult;
import com.google.gwt.dev.util.Memory;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

public class Compiler {

	public static void main(String[] args) throws Exception {
		Memory.initialize();
		SpeedTracerLogger.init();
		Event precompileEvent = SpeedTracerLogger
				.start(CompilerEventType.PRECOMPILE);

		File workDir = new File("/tmp/gwtcc");

		final GWTCCPrecompileTaskOptionsImpl options = new GWTCCPrecompileTaskOptionsImpl();
		GWTCCPrecompileTaskArgProcessor argProcessor = new GWTCCPrecompileTaskArgProcessor(
				options);

		String[] work = new String[] { "-workDir", workDir.getAbsolutePath() };

		String[] result = Arrays.copyOf(args, args.length + work.length);
		System.arraycopy(work, 0, result, args.length, work.length);

		boolean success = false;
		if (argProcessor.processArgs(result)) {
			CompileTask task = new CompileTask() {
				@Override
				public boolean run(TreeLogger logger)
						throws UnableToCompleteException {
					FutureTask<UpdateResult> updater = null;
					if (!options.isUpdateCheckDisabled()) {
						updater = CheckForUpdates
								.checkForUpdatesInBackgroundThread(logger,
										CheckForUpdates.ONE_DAY);
					}

					boolean success = new Precompile(options).run(logger);
					if (success) {
						CheckForUpdates.logUpdateAvailable(logger, updater);
					}
					return success;
				}
			};

			if (CompileTaskRunner.runWithAppropriateLogger(options, task)) {
				success = true;
			}
		}

		precompileEvent.end();

		if (!success) {
			System.exit(-1);
		}

		List<Thread> threads = new ArrayList<Thread>();

		for (final String module : options.getModuleNames()) {
			Runnable r = new Runnable() {

				@Override
				public void run() {
					PrecompiledResource resource = new PrecompiledResource(
							module, options);
					final PrecompilationUploader uploader = new PrecompilationUploader(
							resource);

					uploader.upload();
				}
			};

			Thread t = new Thread(r);
			threads.add(t);
			t.setDaemon(false);
			t.setName(module);
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}

		Link.main(result);
	}
}
