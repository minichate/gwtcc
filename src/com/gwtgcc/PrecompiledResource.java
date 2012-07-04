package com.gwtgcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.google.gwt.dev.PrecompileTaskOptions;

class PrecompiledResource {

	private String workDir;

	private String precompileDir;

	private String precompilation;

	private String module;

	private int permCount;

	public PrecompiledResource(String module, PrecompileTaskOptions options) {
		this.workDir = options.getWorkDir().getAbsolutePath();
		this.setPrecompileDir(workDir + File.separator + module + File.separator
				+ "compiler");
		this.setPrecompilation(this.getPrecompileDir() + File.separator
				+ "precompilation.ser");

		setPermutationCount(fetchPermutationCount());
		setModule(module);

	}

	private Integer fetchPermutationCount() {
		File permCount = new File(getPrecompileDir() + File.separator
				+ "permCount.txt");

		FileInputStream stream = null;
		try {
			stream = new FileInputStream(permCount);
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			return Integer.valueOf(Charset.defaultCharset().decode(bb)
					.toString().trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public int getPermutationCount() {
		return permCount;
	}

	public void setPermutationCount(int permCount) {
		this.permCount = permCount;
	}

	public String getPrecompilation() {
		return precompilation;
	}

	public void setPrecompilation(String precompilation) {
		this.precompilation = precompilation;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getPrecompileDir() {
		return precompileDir;
	}

	public void setPrecompileDir(String precompileDir) {
		this.precompileDir = precompileDir;
	}

}
