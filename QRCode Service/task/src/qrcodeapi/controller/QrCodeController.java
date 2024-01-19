package qrcodeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.utils.QrCodeUtil;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class QrCodeController {
    private static final int WIDTH = 250;
    private static final String DEFAULT_TYPE = "png";

    private static final String DEFAULT_CORRECTION_LVL = "L";

    private static final String ERROR_MSG = "error";
    private static final String DIMENSION_ERROR_MSG = "Image size must be between 150 and 350 pixels";
    private static final  String IMAGETYPE_ERROR_MSG = "Only png, jpeg and gif image types are supported";

    private static final String CORRECTION_LVL_ERROR_MSG = "Permitted error correction levels are L, M, Q, H";

    private static final String CONTENT_ERROR_MSG = "Contents cannot be null or blank";
    @GetMapping("/health")
    public ResponseEntity<String> getHealth(){
        return new ResponseEntity<>("200", HttpStatus.OK);
    }

    @GetMapping("/qrcode")
    public ResponseEntity<byte[]> getQrCode(@RequestParam Optional<Integer> size,@RequestParam Optional<String> contents,
                                            @RequestParam Optional<String>  type,@RequestParam Optional<String> correction) throws IOException {
        Integer pixels = size.orElse(WIDTH);
        String imgType = type.orElse(DEFAULT_TYPE);
        String content = contents.orElse(null);
        String correctionLevel = correction.orElse(DEFAULT_CORRECTION_LVL);

        if(content == null){
            return ResponseEntity.ok().body(new byte[]{});
        }
        if(QrCodeUtil.isInValidContent(content)){
            String json = QrCodeUtil.getOutPutResponseJson(ERROR_MSG,CONTENT_ERROR_MSG);
            return ResponseEntity.
                    badRequest().contentType(MediaType.APPLICATION_JSON).body(json.getBytes());
        }
        if(QrCodeUtil.isInValidDimension(pixels)){

            String json = QrCodeUtil.getOutPutResponseJson(ERROR_MSG,DIMENSION_ERROR_MSG);
            return ResponseEntity.
                    badRequest().contentType(MediaType.APPLICATION_JSON).body(json.getBytes());
        }

        if(QrCodeUtil.isInValidLevel(correctionLevel)){

            String json = QrCodeUtil.getOutPutResponseJson(ERROR_MSG,CORRECTION_LVL_ERROR_MSG);
            return ResponseEntity.
                    badRequest().contentType(MediaType.APPLICATION_JSON).body(json.getBytes());
        }
        if(QrCodeUtil.isInValidType(imgType)){

            String json = QrCodeUtil.getOutPutResponseJson(ERROR_MSG,IMAGETYPE_ERROR_MSG);
            return ResponseEntity.
                    badRequest().contentType(MediaType.APPLICATION_JSON).body(json.getBytes());
        }

//        BufferedImage image = new BufferedImage(pixels, pixels ,BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = image.createGraphics();
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, pixels, pixels);
          BufferedImage image = QrCodeUtil.getQRCode(content,pixels,pixels,correctionLevel);
        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, imgType, baos); // writing the image in the PNG format
            byte[] bytes = baos.toByteArray();
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType("image/" + imgType))
                    .body(bytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new byte[]{});

        }

    }
}
