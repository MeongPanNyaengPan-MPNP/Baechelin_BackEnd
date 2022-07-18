package com.mpnp.baechelin.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
@XmlRootElement(name = "facInfoList")
public class PublicApiCategoryForm {
    private String resultCode;
    private String resultMessage;
    private List<ServList> servList;

    @NoArgsConstructor
    @Getter @Setter
    @XmlRootElement(name = "servList")
    public static class ServList{
        private String evalInfo;
        private String faclNm;
        private String wfcltId;
    }
}
