package com.platfos.pongift.validate.service;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.controller.WebController;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.lang.reflect.*;
import java.util.*;

@Service
public class ParamService {
    public ParamClass clzz(boolean isGateway, Class<?> clzz, APIRole apiRole) throws Exception{
        String name = null;
        if(clzz.isAnnotationPresent(ParamClzz.class)){
            name = clzz.getAnnotation(ParamClzz.class).name();
        }
        List<ParamMethod> methods = new ArrayList<>();
        for(Method method : clzz.getMethods()){
            if(method.isAnnotationPresent(RequestMapping.class)) {
                boolean isApiRoleCheck = (apiRole == null);
                if(method.isAnnotationPresent(APIPermission.class)){
                    APIPermission apiPermission = method.getAnnotation(APIPermission.class);
                    for(APIRole role : apiPermission.roles()){
                        if(apiRole != null && apiRole == role) isApiRoleCheck = true;
                        else if(role == APIRole.ANONYMOUS) isApiRoleCheck = true;
                        else if(apiRole != null && role == APIRole.AUTHENTICATED) isApiRoleCheck = true;
                    }
                }
                if(isApiRoleCheck) {
                    if(!(clzz == WebController.class & method.getName().equals("favicon"))) methods.add(method(false, isGateway, method));
                }
            }
        }
        if(methods.size() > 0) {
            Collections.sort(methods, new Comparator<ParamMethod>() {
                @Override
                public int compare(ParamMethod p1, ParamMethod p2) {
                    int order1 = orderMethod(p1.getMethod());
                    int order2 = orderMethod(p2.getMethod());

                    /*if(p2.getPath().compareTo(p1.getPath()) == 0){
                        return (order1>order2?1:(order1==order2?0:-1));
                    }else{
                        return p2.getPath().compareTo(p1.getPath());
                    }*/
                    if(order1 == order2){
                        return p1.getPath().compareTo(p2.getPath());
                    }else{
                        return (order1>order2?1:(order1==order2?0:-1));
                    }
                }
            });

            return ParamClass.builder().clzz(clzz).name(name).methods(methods).build();
        }
        else return null;
    }

    public int orderMethod(String method){
        int ordered = 99;
        if(method != null){
            if(method.equalsIgnoreCase("GET")) ordered = 1;
            else if(method.equalsIgnoreCase("PUT")) ordered = 2;
            else if(method.equalsIgnoreCase("POST")) ordered = 3;
            else if(method.equalsIgnoreCase("DELETE")) ordered = 4;
        }
        return ordered;
    }

    public ParamMethod method(boolean isRequestOnly, boolean isGateway, Method method) throws Exception{
        String name = method.getName();
        List<ParamField> request = request(method);
        ParamField response = (isRequestOnly)?null:response(method);

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        String path = (!ObjectUtils.isEmpty(requestMapping.path()))?requestMapping.path()[0]:"";
        if(!path.startsWith("/")) path = "/" + path;

        RequestMethod requestMethod = (!ObjectUtils.isEmpty(requestMapping.method()))?requestMapping.method()[0]:null;
        String strMethod = (requestMethod!= null ?requestMethod.name():RequestMethod.GET.name());
        String contentType = (strMethod.equals("GET")?null: MediaType.APPLICATION_JSON_VALUE);

        Parameter[] parameters = method.getParameters();
        for(Parameter parameter : parameters){
            Class<?> type = parameter.getType();
            Class<?> subType1 = null;
            Class<?> subType2 = null;

            if(parameter.getParameterizedType() instanceof ParameterizedType){
                Type[] genericType = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments();
                if(genericType.length == 1){
                    subType1 =  Class.forName(genericType[0].getTypeName());
                }else if(genericType.length == 2){
                    subType1 =  Class.forName(genericType[0].getTypeName());
                    subType2 =  Class.forName(genericType[1].getTypeName());
                }
            }

            if(type == MultipartFile.class | subType1 == MultipartFile.class | subType2 == MultipartFile.class){
                contentType = MediaType.MULTIPART_FORM_DATA_VALUE;
                break;
            }
        }

        String methodName = "";
        if(method.isAnnotationPresent(ParamMethodValidate.class)){
            ParamMethodValidate methodValidate = method.getAnnotation(ParamMethodValidate.class);
            methodName = methodValidate.methodName();
        }

        return ParamMethod.builder().name(name).path(path).method(strMethod).methodName(methodName).contentType(contentType).request(request).response(response).build();
    }

    public ParamField response(Method method){
        return ParamField.responseParam(method);
    }

    public List<ParamField> request(Method method) throws Exception{
        Parameter[] parameters = method.getParameters();
        List<ParamField> request = null;
        Class<?>[] validated = null;
        if(method.isAnnotationPresent(Validated.class)) validated = method.getAnnotation(Validated.class).value();

        for(Parameter parameter : parameters){
            if(parameter.isAnnotationPresent(RequestBody.class) | parameter.isAnnotationPresent(RequestParam.class)){
                if(ObjectUtils.isEmpty(request)) request = new ArrayList<>();

                ParamField paramField = ParamField.requestParam(validated, parameter, null);
                request.add(paramField);
            }
        }
        return request;
    }
}
