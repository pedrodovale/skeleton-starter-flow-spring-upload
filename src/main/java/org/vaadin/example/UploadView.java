package org.vaadin.example;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.TransferContext;
import com.vaadin.flow.server.streams.TransferProgressAwareHandler;
import com.vaadin.flow.server.streams.UploadEvent;
import com.vaadin.flow.server.streams.UploadHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Route("")
public class UploadView extends Div {
    public UploadView() {
        Upload upload = new Upload(new CustomUploadHandler());
        add(upload);
    }
    
    static class CustomUploadHandler extends TransferProgressAwareHandler<UploadEvent, CustomUploadHandler> implements UploadHandler {
        @Override
        public void handleUploadRequest(UploadEvent uploadEvent) throws IOException {
            try {
                System.out.println("Starting upload for file: " + uploadEvent.getFileName());
                FakeExternalStorage.upload(uploadEvent.getInputStream());
            } catch (IOException exc) {
                notifyError(uploadEvent, exc);
                throw exc;
            }
        }

        @Override
        protected TransferContext getTransferContext(UploadEvent transferEvent) {
            return new TransferContext(
                    transferEvent.getRequest(),
                    transferEvent.getResponse(),
                    transferEvent.getSession(),
                    transferEvent.getFileName(),
                    transferEvent.getOwningElement(),
                    transferEvent.getFileSize());
        }
    }

    static class FakeExternalStorage {
        private static final int MAX_SIZE = 1024 * 1024; // 1MB

        public static void upload(InputStream inputStream) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            int bytesRead;
            int totalBytes = 0;

            while ((bytesRead = inputStream.read(chunk)) != -1) {
                totalBytes += bytesRead;
                if (totalBytes > MAX_SIZE) {
                    throw new IOException("Upload exceeds maximum size of 1MB");
                }
                buffer.write(chunk, 0, bytesRead);
            }
        }
    }
}