package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.StoreImage;
import com.mpnp.baechelin.store.repository.StoreImgRepository;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.util.AwsS3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.transaction.Transactional;
import java.io.*;


import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreImageService {
    private final AwsS3Manager awsS3Manager;
    private final StoreImgRepository storeImgRepository;
    private final StoreRepository storeRepository;
    @Value("${user.agent}")
    private String userAgent;
    @Transactional
    public void saveImage(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_INPUT));
        String storeImgUrl = saveImageByStoreId(storeId);
        if (storeImgUrl == null) return;
        StoreImage img = StoreImage.builder()
                .store(store)
                .storeImageUrl(storeImgUrl)
                .build();
        storeImgRepository.saveAndFlush(img);
    }

    private String saveImageByStoreId(Long storeId) {
        String url = "https://place.map.kakao.com/placePrint.daum?confirmid=" + storeId;
        Connection conn = Jsoup.connect(url);
        Document doc = null;
        Elements elem = null;
        try {
            Connection.Response response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .userAgent(userAgent)
                    .execute();
            Document document = response.parse();
            Elements select = document.select("body div div div.popup_body div.wrap_info div img");
            String val = select.select("img").attr("src");
            if (val.equals("")) return null;
            return downloadImage("https:" + val);
        } catch (IOException ignored) {
            throw new CustomException(ErrorCode.IMAGE_PROCESS_FAIL);
        }
    }

    private String downloadImage(String imgUrl) throws IOException {
        log.info("imageurlcheck : {}", imgUrl);
        ClassPathResource resource = new ClassPathResource("");
        String fileName = resource.getPath() + UUID.randomUUID() + ".jpg";
        URL url = new URL(imgUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(fileName);
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();

        File file = new File(fileName);
        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = fileItem.getOutputStream();
            IOUtils.copy(fileInputStream, outputStream);
            fileInputStream.close();
            outputStream.close();
        } catch (IOException ex) {
            throw new CustomException(ErrorCode.IMAGE_PROCESS_FAIL);
        }

        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        boolean deleteResult = file.delete();
        return awsS3Manager.uploadFile(multipartFile);
    }
}
