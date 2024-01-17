package com.platfos.pongift.quantum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.service.CIMLedgerService;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.definition.SystemKey;
import com.platfos.pongift.quantum.model.QuantumResponse;
import com.platfos.pongift.security.SHA256Util;
import com.platfos.pongift.util.Util;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;

/**
 * 양자 난수(Quantum) 연동 서비스
 */
@Service
@RequiredArgsConstructor
public class QuantumService {
    private static final Logger logger = LoggerFactory.getLogger(QuantumService.class);

    /**
     * Application Properties
     */

    private final ApplicationProperties properties;

    /**
     * 시스템 정보 서비스
     **/
    private final SIMSystemInfoService systemInfoService;

    /**
     * 상품권 원장 서비스
     **/
    private final CIMLedgerService ledgerService;

    /**
     * Http 모듈
     **/
    private final RestTemplate restTemplate;

    /**
     * Index 서비스
     **/
    private final SIMIndexService service;

    /**
     * 서비스 활성화 상태 확인
     **/
    public boolean isReady() {
        //시스템 정보 내 양자난수 서버 HOST 존재 유무 확인
        return !StringUtils.isEmpty(systemInfoService.getValue(SystemKey.QUANTUM_SERVER_HOST));
    }

    /**
     * 양자 난수(Quantum) 데이터 조회
     *
     * @param len 난수 길이
     * @return
     */
    public QuantumResponse quantum(Integer len) {
        //서비스 활성화 상태 확인
        if (!isReady()) {
            return QuantumResponse.builder().success(false).message("Quantum Service is not ready.").build();
        }

        //요청 URL 생성
        String url = systemInfoService.getValue(SystemKey.QUANTUM_SERVER_HOST) + "?len=" + len;

        logger.info("============================================================================");
        logger.info("URL : {}", url);

        //양자 난수 조회 요청
        try {
            ObjectMapper mapper = new ObjectMapper();
            ResponseEntity<QuantumResponse> response = restTemplate.getForEntity(url, QuantumResponse.class);

            //조회 성공
            if (response.getStatusCode().is2xxSuccessful() && (response.getBody() != null && response.getBody() instanceof QuantumResponse)) {
                logger.info("============================================================================");
                logger.info(Util.logJsonSecretFilter(mapper.writeValueAsString(response), "data", "[SECRET VALUE]"));
                logger.info("============================================================================");

                return response.getBody();
            } //조회 실패
            else {
                logger.error("============================================================================");
                logger.error(Util.logJsonSecretFilter(mapper.writeValueAsString(response), "data", "[SECRET VALUE]"));
                logger.error("============================================================================");
                return QuantumResponse.builder().success(false).message(mapper.writeValueAsString(response)).build();
            }
        } catch (Exception e) {
            logger.error("Quantum Service", e);
            return QuantumResponse.builder().success(false).message(e.getMessage()).build();
        }
    }

    /**
     * 바코드(PINNUMBER) 생성/조회
     *
     * @return
     * @throws Exception
     */
    public String pinNumber() throws Exception {
        int len = 8;

        //양자 난수(Quantum) 데이터 조회
        QuantumResponse response = quantum(len);

        if (response.isSuccess()) { //성공 시
            return pinNumber(response.getData());
        } else { //실패 시 (DB Procedure에서 난수 생성)
            return pinNumber(generateRandomNumber(8));
        }
    }

    /**
     * 바코드(PINNUMBER) 생성/조회
     *
     * @param quantum 양자난수
     * @return
     * @throws Exception
     */
    public String pinNumber(String quantum) throws Exception {
        return pinNumber(quantum, null);
    }

    /**
     * 바코드(PINNUMBER) 생성/조회
     *
     * @param quantum   양자난수
     * @param pinTypeGb 핀타입
     * @return
     * @throws Exception
     */
    public String pinNumber(String quantum, String pinTypeGb) throws Exception {
        //핀 타입 초기화
        if (StringUtils.isEmpty(pinTypeGb)) pinTypeGb = "P1";

        //핀번호 조회
        String pinNumber = service.getPinNo(quantum, pinTypeGb);

        //핀번호 중복 체크
        CIMLedger ledger = ledgerService.getCIMLedgerByGiftNo(SHA256Util.encode(pinNumber));
        if (ledger == null) return pinNumber;
        else return pinNumber(quantum, pinTypeGb);
    }

    /**
     * 무작위 숫자 문자열 생성
     *
     * @param size
     * @return
     */
    private String generateRandomNumber(int size) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(secureRandom.nextInt(10));
        }

        return sb.toString();
    }
}
