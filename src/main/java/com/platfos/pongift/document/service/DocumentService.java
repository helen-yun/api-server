package com.platfos.pongift.document.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.platfos.pongift.category.service.GoodsCategoryService;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.controller.*;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.annotation.ExternalController;
import com.platfos.pongift.validate.annotation.GWController;
import com.platfos.pongift.document.model.DocCode;
import com.platfos.pongift.exception.controller.WebErrorController;
import com.platfos.pongift.validate.model.ParamClass;
import com.platfos.pongift.validate.service.ParamService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.*;

@Service
public class DocumentService {
    /** 파라메터 서비스 **/
    @Autowired
    ParamService paramService;

    /** 기초코드 서비스 **/
    @Autowired
    SIMCodeService codeService;

    /** 상품(전시)카테고리 서비스 **/
    @Autowired
    GoodsCategoryService goodsCategoryService;

    /** Application Properties 서비스 **/
    @Autowired
    ApplicationProperties properties;

    //API 제외 클래스 리스트
    public static final Map<Class<?>, Boolean> exclusions =
            ImmutableMap.of(
                    WebController.class, true,
                    TestController.class, true,
                    WebErrorController.class, true,
                    DocumentController.class, true
            );

    /**
     * 내부 서버 전용 ParamClass 리스트 설정
     * @param responses
     * @throws Exception
     */
    public void paramExternalClasses(List<ParamClass> responses) throws Exception{
        //클래스 스캐너
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        //Include Annotaion ExternalController List
        scanner.addIncludeFilter(new AnnotationTypeFilter(ExternalController.class));
        for (BeanDefinition bean : scanner.findCandidateComponents("com.platfos.pongift")){
            Class<?> ctrlClzz = Class.forName(bean.getBeanClassName());
            if(!exclusions.containsKey(ctrlClzz)){
                ParamClass paramClass = paramService.clzz(true, ctrlClzz, APIRole.EXTERNAL_SERVICE);
                if(paramClass != null) responses.add(paramClass);
            }
        }
    }
    /**
     * Gateway 전용 ParamClass 리스트 설정
     * @param responses
     * @throws Exception
     */
    public void paramGWClasses(List<ParamClass> responses) throws Exception{
        //클래스 스캐너
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        //Include Annotaion GWController List
        scanner.addIncludeFilter(new AnnotationTypeFilter(GWController.class));
        for (BeanDefinition bean : scanner.findCandidateComponents("com.platfos.pongift")){
            Class<?> ctrlClzz = Class.forName(bean.getBeanClassName());
            if(!exclusions.containsKey(ctrlClzz)){
                ParamClass paramClass = paramService.clzz(true, ctrlClzz, APIRole.EXTERNAL_GATEWAY_CHANNEL);
                if(paramClass != null) responses.add(paramClass);
            }
        }
    }

    /**
     * 권한 별 ParamClass 리스트 조회
     * @param apiRole 권한
     * @return
     * @throws Exception
     */
    public List<ParamClass> paramClasses(APIRole apiRole) throws Exception {
        List<ParamClass> paramClasses = new ArrayList<>();
        //클래스 스캐너
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        //Include Annotaion Controller List
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        for (BeanDefinition bean : scanner.findCandidateComponents("com.platfos.pongift")){
            Class<?> ctrlClzz = Class.forName(bean.getBeanClassName());
            if(!exclusions.containsKey(ctrlClzz)){
                ParamClass paramClass = paramService.clzz(false, ctrlClzz, apiRole);
                if(paramClass != null) paramClasses.add(paramClass);
            }
        }
        //Gateway 전용 ParamClass 리스트 설정
        if(apiRole == null | apiRole == APIRole.EXTERNAL_GATEWAY_CHANNEL) paramGWClasses(paramClasses);
        //내부 서버 전용 ParamClass 리스트 설정
        if(apiRole == null | apiRole == APIRole.EXTERNAL_SERVICE) paramExternalClasses(paramClasses);

        Collections.sort(paramClasses, new Comparator<ParamClass>() {
            @Override
            public int compare(ParamClass p1, ParamClass p2) {
                return p1.getName().compareTo(p2.getName());
            }
        });

        for(ParamClass clzz : paramClasses){
            if(clzz.getName() != null && clzz.getName().indexOf(".") > -1){
                String[] names = clzz.getName().split("[.]");
                if(names != null && names.length>1){
                    clzz.setName(names[1]);
                }
            }
        }

        return paramClasses;
    }

    /**
     * API Document HTML
     * @param apiRole 권한
     * @return
     * @throws Exception
     */
    public String createHtml(String apiRole) throws Exception{
        String data = null;
        if(properties != null && !StringUtils.isEmpty(properties.getApiDoc())){
            //API Document Contents read
            FileInputStream fis = new FileInputStream(properties.getApiDoc());
            data = IOUtils.toString(fis, "UTF-8");

            //Template Code Replace in Javascript
            //권한
            if(!StringUtils.isEmpty(apiRole)){
                data = data.replace("[APIROLE]", "\""+apiRole.toUpperCase()+"\"");
            }else{
                data = data.replace("[APIROLE]", "null");
            }
            //API 버전
            data = data.replace("[APIVERSION]", "\""+"2.0"+"\"");
            //시스템 업로드 파일 사이즈
            data = data.replace("[SYSTEMFILESIZE]", "\""+properties.strUploadMaxSize()+"\"");

            ObjectMapper mapper = new ObjectMapper();
            //시스템 업로드 지원 파일형 정보(MIME-TYPE)
            data = data.replace("[SYSTEMMIMETYPEMAP]", mapper.writeValueAsString(properties.getMimeTypeMap()));
            //상품(전시) 카테고리 리스트
            data = data.replace("[GOODSCATEGORIES]", mapper.writeValueAsString(goodsCategoryService.getGoodsCategoryList("Y")));
        }
        return data;
    }

    /**
     * 기초 코드 정보(Map)
     * @return
     */
    public Map<String, DocCode> docCodeMap(){
        Map<String, DocCode> map = new HashMap<>();
        for(SIMCodeGroup groupCode : SIMCodeGroup.values()){
            List<SIMCode> simCodeList = codeService.getSIMCodeList(groupCode);
            String groupNm = null;
            if(!ObjectUtils.isEmpty(simCodeList)){
                groupNm = simCodeList.get(0).getGroupNm();
            }
            map.put(groupCode.name(), DocCode.builder().name(groupNm).roles(groupCode.getRoles()).isNotUseParentURL(groupCode.isNotUseParentURL()).parentCodeGroup(groupCode.getParentCodeGroup()).parentValue(groupCode.getParentValue()).docCodes(simCodeList).build());
        }
        return map;
    }
}
