package com.omardev.event_ticketing.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.omardev.event_ticketing.exception.ApiException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class QrCodeGenerator {

    /**
     * Generate a QR code image (PNG) from a text value.
     *
     * @param text the content to encode (usually a unique QR token)
     * @return QR code image as byte array
     */
    public byte[] generate(String text) {

        try {
            // Create QR code writer
            QRCodeWriter writer = new QRCodeWriter();

            // Encode text into QR matrix (250x250 image)
            BitMatrix matrix = writer.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    250,
                    250
            );

            // Output stream to store image bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Convert matrix → PNG image
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);

            // Return image as byte[]
            return outputStream.toByteArray();

        } catch (Exception e) {
            // Wrap and rethrow (avoid exposing low-level exception)
            throw new ApiException("Failed to generate QR code");
        }
    }
}