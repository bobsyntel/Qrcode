package qrcodeapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.*;

public class QrCodeUtil {
    private static final int HEIGHT = 150;
    private static final int WIDTH = 350;


//    static Set<String> mapOfLevel = Set.of("L"
//            ,"H",
//            "M",
//            "Q");
    static List<String> imageTypes = Arrays.asList("png","jpeg","gif");

    static Map<String,?> mapOfLevelToCorrection = Map.of("L", ErrorCorrectionLevel.L
            ,"H",ErrorCorrectionLevel.H,
            "M",ErrorCorrectionLevel.M,
            "Q",ErrorCorrectionLevel.Q);

    public static boolean isInValidDimension(int size){
        return !(size >= HEIGHT && size <= WIDTH);
    }

    public static boolean isInValidType(String type){
        return !imageTypes.contains(type);
    }

    public static boolean isInValidContent(String content){
        return content.isBlank() || content.isEmpty() ;
    }

    public static boolean isInValidLevel(String level){
        return  !mapOfLevelToCorrection.containsKey(level);
    }

    public static BufferedImage getQRCode(String data, int width, int height, String level){
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION,
                mapOfLevelToCorrection.get(level));

        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height,hints);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return bufferedImage;
        } catch (WriterException e) {
            e.printStackTrace();
            // handle the WriterException
        }
        return null;
    }

    public static String getOutPutResponseJson(String errorString,String errorMsg) throws JsonProcessingException {
        HashMap<String,String> map = new HashMap<>();
        map.put(errorString,errorMsg);
        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(map);
        return json;
    }


}
