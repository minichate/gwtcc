package com.gwtgcc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class PrecompilationUploader {

	PrecompiledResource resource;

	public PrecompilationUploader(PrecompiledResource resource) {
		this.resource = resource;
	}

	public void upload() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://localhost:8000/");
		FileBody bin = new FileBody(new File(resource.getPrecompilation()));

		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("ser", bin);

		try {
			reqEntity.addPart("module", new StringBody(resource.getModule()));
			reqEntity.addPart(
					"permcount",
					new StringBody(Integer.toString(resource
							.getPermutationCount())));

			httppost.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			ZipInputStream zis = new ZipInputStream(resEntity.getContent());
			ZipEntry entry;

			BufferedOutputStream dest = null;
			final int BUFFER = 2048;

			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " + entry);
				int count;
				byte data[] = new byte[BUFFER];
				// write the files to the disk

				FileOutputStream fos = new FileOutputStream(
						resource.getPrecompileDir() + "/" + entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
