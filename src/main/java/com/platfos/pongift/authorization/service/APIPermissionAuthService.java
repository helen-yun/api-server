package com.platfos.pongift.authorization.service;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.definition.AttributeKey;
import com.platfos.pongift.data.sim.model.SIMConnectInfo;
import com.platfos.pongift.data.sim.service.SIMConnectInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 서비스 권한 서비스
 */
@Service
public class APIPermissionAuthService {
    /** 서비스 접속 관리 서비스(DB) **/
    @Autowired
    SIMConnectInfoService simConnectInfoService;
    /**
     * 추가 권한 관리(DB)
     */
    @Autowired
    MIMServiceService mimServiceService;
    /** Application Properties **/
    @Autowired
    ApplicationProperties properties;

    /**
     * 권한 리스트에서 해당 권한 존재 유무 확인
     * @param roles 권한 리스트
     * @param find 목적 권한
     * @return
     */
    public boolean isFindRole(List<APIRole> roles, APIRole find){
        for(APIRole role:roles){
            if(role == find) return true;
        }
        return false;
    }

    /**
     * Request에서 해당 권한 존재 유무 확인
     * @param request HttpServletRequest
     * @param role 목적 권한
     * @return
     */
    public boolean hasRole(HttpServletRequest request, APIRole role){
        List<APIRole> roles = (List<APIRole>) request.getAttribute(AttributeKey.API_ROLES.getKey());
        return isFindRole(roles, role);
    }

    /**
     * 서비스 아이디와 접속 IP로 해당 서비스 정보 조회 후, 권한 리스트에 추가
     * @param roles 권한 리스트
     * @param serviceId 서비스 아이디
     * @param accessIp 접속 IP
     */
    private void addRolesByServiceId(List<APIRole> roles, String serviceId, String accessIp){
        //비인증 권한 추가
        addRole(roles, APIRole.ANONYMOUS);

        if(!StringUtils.isEmpty(accessIp)){
            if(!StringUtils.isEmpty(serviceId)){
                //서비스 아이디와 접속 IP로 해당 서비스 정보 조회
                List<SIMConnectInfo> list = simConnectInfoService.getSIMConnectInfoList(serviceId, accessIp);
                if(list != null && list.size() > 0){
                    String dbServiceTpCode = list.get(0).getServiceTp();
                    String dbSubServiceTpCode = list.get(0).getSubServiceTp();
                    APIRole role = APIRole.findByCode(dbServiceTpCode);
                    APIRole subRole = APIRole.findByCode(dbSubServiceTpCode);
                    if(role != null) {
                        //인증 권한 추가
                        addRole(roles, APIRole.AUTHENTICATED);
                        //해당 서비스의 권한 추가
                        addRole(roles, role);
                    }
                    //추가 권한 정보
                    if(subRole != null){
                        addRole(roles, subRole);
                    }
                }
            }
        }
    }

    /**
     * 내부 서버 접근 권한(or 유통채널 Gateway 권한) 추가
     * @param roles 권한 리스트
     * @param accessIp 접속 IP
     * @param serverIp 서버 IP
     */
    private void addRoles(List<APIRole> roles, String accessIp, String serverIp){
        //비인증 권한 추가
        addRole(roles, APIRole.ANONYMOUS);

        if(!StringUtils.isEmpty(accessIp)){
            //접속 IP가 Local IP인 경우
            if(accessIp.equals("0:0:0:0:0:0:0:1") | accessIp.equals("127.0.0.1") | accessIp.startsWith(serverIp)){
                addRole(roles, APIRole.AUTHENTICATED);
                addRole(roles, APIRole.EXTERNAL_SERVICE);
            }

            //application.properties 파일에 명시된 IP인 경우(platfos.accessible)
            if(properties != null && !ObjectUtils.isEmpty(properties.getAccessible())){
                for(String externallyAccessibleAddress : properties.getAccessible()){
                    if(!StringUtils.isEmpty(externallyAccessibleAddress) && externallyAccessibleAddress.equals(accessIp)){
                        addRole(roles, APIRole.AUTHENTICATED);
                        addRole(roles, APIRole.EXTERNAL_SERVICE);
                    }
                }
            }

            //application.properties 파일에 명시된 유통채널 Gateway IP인 경우(platfos.channels)
            if(properties != null && !ObjectUtils.isEmpty(properties.getChannels())){
                for(String channelAddress : properties.getChannels()){
                    if(!StringUtils.isEmpty(channelAddress) && channelAddress.equals(accessIp)){
                        addRole(roles, APIRole.AUTHENTICATED);
                        addRole(roles, APIRole.EXTERNAL_GATEWAY_CHANNEL); //유통채널 Gateway 권한
                    }
                }
            }
        }
    }

    /**
     * 접속 IP가 해당 권한이 있는지 확인
     * @param accessIp 접속 IP
     * @param serverIp 서버 IP
     * @param descRole 목적 권한
     * @return
     */
    public boolean permission(String accessIp, String serverIp, APIRole descRole){
        List<APIRole> roles = new ArrayList<>();
        addRole(roles, APIRole.ANONYMOUS);

        List<SIMConnectInfo> list = simConnectInfoService.getSIMConnectInfoList(accessIp);
        if(list != null && list.size() > 0){
            String dbServiceTpCode = list.get(0).getServiceTp();
            APIRole role = APIRole.findByCode(dbServiceTpCode);
            if(role != null) {
                addRole(roles, APIRole.AUTHENTICATED);
                addRole(roles, role);
            }
        }

        addRoles(roles, accessIp, serverIp);

        for(APIRole role : roles){
            if(role == descRole) return true;
        }
        return false;
    }

    /**
     * 해당 Request/Method에 접근할 권한이 있는 지 확인
     * @param request HttpServletRequest
     * @param ctrlMethod method(in RestController)
     * @param serviceId 서비스 아이디
     * @param accessIp 접속 IP
     * @param serverIp 서버 IP
     * @return
     */
    public boolean permission(HttpServletRequest request, Method ctrlMethod, String serviceId, String accessIp, String serverIp){
        //Method 권한 리스트
        List<APIRole> methodRoles = new ArrayList<>();

        //Method 권한 리스트 생성
        if(ctrlMethod.isAnnotationPresent(APIPermission.class)){
            APIPermission apiPermission = ctrlMethod.getAnnotation(APIPermission.class);
            for(APIRole role : apiPermission.roles()){
                methodRoles.add(role);
            }
        }else{
            methodRoles.add(APIRole.ANONYMOUS);
        }

        //접속 IP의 권한 리스트
        List<APIRole> roles = new ArrayList<>();

        addRole(roles, APIRole.ANONYMOUS);
        addRolesByServiceId(roles, serviceId, accessIp);
        addRoles(roles, accessIp, serverIp);

        //set Request 권한 정보
        request.setAttribute(AttributeKey.API_ROLES.getKey(), roles);

        //접속 가능 여부 확인
        for(APIRole role : roles){
            for(APIRole methodRole : methodRoles){
                if(role == methodRole) return true;
            }
        }
        return false;
    }

    private void addRole(List<APIRole> roles, APIRole role){
        if(!isFindRole(roles, role)) roles.add(role);
    }
}
