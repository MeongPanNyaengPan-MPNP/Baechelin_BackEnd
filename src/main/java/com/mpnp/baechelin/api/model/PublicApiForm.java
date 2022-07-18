package com.mpnp.baechelin.api.model;

import com.mpnp.baechelin.store.domain.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
@XmlRootElement(name = "facInfoList")
public class PublicApiForm {
    private int totalCount;
    private int resultCode;
    private String resultMessage;
    private ServList[] servList;
    @NoArgsConstructor
    @Getter @Setter
    @XmlRootElement(name = "servList")
    public static class ServList{
        private String estbDate;
        private String faclInfId;
        private String faclLat;
        private String faclLng;
        private String faclRprnNm;
        private String faclTyCd;
        private String lcMnad;
        private String salStaDivCd;
        private String salStaNm;
        private String wfcltDivCd;
        private String wfcltId;
    }

    public List<Store> ApiFormToStore(PublicApiForm publicApiForm){
        List<Store> mappingResult = new ArrayList<>();
        for (ServList serv : publicApiForm.getServList()) {
            mappingResult.add(Store.builder()
                    .address(serv.getLcMnad())
                    .latitude(new BigDecimal(serv.faclLat))
                    .longitude(new BigDecimal(serv.faclLng))
                    .build());
        }
        return mappingResult;
    }
}
