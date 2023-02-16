package com.svgtopng.controller;


import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Peter Cheung
 * 2023/2/16 15:21
 */
@RestController
public class SvgToPngController {
    /**
     * 上传svg
     * <p>
     * 然后
     * <p>
     * 下载png
     * <p>
     * consumes定义multipart/form-data
     */
    @GetMapping(path = "download", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void download(float height, float width, MultipartFile file, HttpServletResponse response) throws IOException, TranscoderException {
        //MultipartFile转InputStream
        InputStream in = new ByteArrayInputStream(file.getBytes());
        Transcoder transcoder = new PNGTranscoder();
        //设置png图片的宽和长
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
        try {
            TranscoderInput input = new TranscoderInput(in);
            //清空response
            response.reset();
            //强制下载不打开
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            //设置编码为UTF_8
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition:inline; filename=文件名.mp3"
            //filename表示文件的默认名称，因为网络传输只支持URL编码，因此需要将文件名URL编码后进行传输，前端收到后需要反编码才能获取到真正的名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode((Objects.requireNonNull(file.getOriginalFilename()).split("\\."))[0], StandardCharsets.UTF_8.name()) + ".png");
            TranscoderOutput output = new TranscoderOutput(response.getOutputStream());
            transcoder.transcode(input, output);
        } finally {
            in.close();
        }
    }
}
