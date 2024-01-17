package com.platfos.pongift.validate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.gateway.response.GWResponseBase;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.MapConstraints;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamField {
    private String name;
    private String description;
    private Class<?> type;
    private Class<?> subType1;
    private Class<?> subType2;
    private ParamFieldValidate validate;
    private List<ParamField> member;

    public static ParamField responseParam(Field field){
        if(field.isAnnotationPresent(JsonIgnore.class)) return null;
        else if(field.isAnnotationPresent(JsonProperty.class) && field.getAnnotation(JsonProperty.class).access() == JsonProperty.Access.WRITE_ONLY) return null;

        List<ParamField> member = null;
        String name = field.getName();

        String description = null;
        if(field.isAnnotationPresent(Description.class)) description = field.getAnnotation(Description.class).value();
        ParamFieldValidate validate = ParamFieldValidate.convert(null, null, name, field);

        Class<?> type = field.getType();
        Class<?> subType1 = null;
        Class<?> subType2 = null;

        if(field.getGenericType() instanceof ParameterizedType){
            Type[] genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();

            if(genericType.length == 1){
                try { subType1 =  Class.forName(genericType[0].getTypeName()); }catch (Exception e){e.printStackTrace();}
            }else if(genericType.length == 2){
                try { subType1 =  Class.forName(genericType[0].getTypeName()); }catch (Exception e){e.printStackTrace();}
                try { subType2 =  Class.forName(genericType[1].getTypeName()); }catch (Exception e){e.printStackTrace();}
            }
        }

        Class<?> memberClass = type;
        if(type == List.class) memberClass = subType1;
        else if(type == Map.class) memberClass = subType2;

        if(!isPrimitive(memberClass)){
            for(Field childField : memberClass.getDeclaredFields()){
                Class<?> childType = childField.getType();
                Class<?> childSubType1 = null;
                Class<?> childSubType2 = null;
                Class<?> childMemberClass = null;

                if(childField.getGenericType() instanceof ParameterizedType){
                    Type[] genericType = ((ParameterizedType) childField.getGenericType()).getActualTypeArguments();

                    if(genericType.length == 1){
                        try { childSubType1 =  Class.forName(genericType[0].getTypeName()); }catch (Exception e){e.printStackTrace();}
                    }else if(genericType.length == 2){
                        try { childSubType1 =  Class.forName(genericType[0].getTypeName()); }catch (Exception e){e.printStackTrace();}
                        try { childSubType2 =  Class.forName(genericType[1].getTypeName()); }catch (Exception e){e.printStackTrace();}
                    }
                }

                childMemberClass = childType;
                if(childType == List.class) childMemberClass = childSubType1;
                else if(childType == Map.class) childMemberClass = childSubType2;

                boolean isHierarchy = (memberClass == childMemberClass);

                ParamField memberParamField = null;
                if(!isHierarchy){
                    memberParamField = responseParam(childField);
                }/*else{
                    String childDescription = null;
                    if(childField.isAnnotationPresent(Description.class)) childDescription = childField.getAnnotation(Description.class).value();
                    ParamFieldValidateImpl childValidate = ParamFieldValidateImpl.convert(null, childField);
                    memberParamField = ParamField.builder().name(description).type(childType).subType1(childSubType1).subType2(childSubType2).validate(childValidate).description(childDescription).build();
                }*/

                if(memberParamField != null) {
                    if(ObjectUtils.isEmpty(member)) member = new ArrayList<>();
                    member.add(memberParamField);
                }
            }
        }

        return ParamField.builder().name(name).type(type).subType1(subType1).subType2(subType2).member(member).validate(validate).description(description).build();
    }

    public static ParamField responseParam(Method method){
        List<ParamField> member = null;
        Class<?> type = method.getReturnType();

        if(!isPrimitive(type)){
            Class<?> clzzT = null;
            if(method.getGenericReturnType() instanceof ParameterizedType){
                try {
                    clzzT = Class.forName(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].getTypeName());
                } catch (ClassNotFoundException e) {e.printStackTrace();}
            }

            List<Field> fields = new ArrayList<Field>();
            if(type.getSuperclass() == BaseResponse.class | type.getSuperclass() == GWResponseBase.class){
                fields.addAll(Arrays.asList(type.getSuperclass().getDeclaredFields()));
                fields.addAll(Arrays.asList(type.getDeclaredFields()));
            }else{
                fields.addAll(Arrays.asList(type.getDeclaredFields()));
            }

            for(Field childField : fields){
                if(ObjectUtils.isEmpty(member)) member = new ArrayList<>();

                String memberName = childField.getName();
                String memberDesc = null;
                if(childField.isAnnotationPresent(Description.class)) memberDesc = childField.getAnnotation(Description.class).value();

                Class<?> memberType = null;
                Class<?> memberSubType1 = null;
                Class<?> memberSubType2 = null;
                Class<?> memberClzz = null;

                String genericTypeName = childField.getGenericType().getTypeName();
                if(genericTypeName.equals("T") | genericTypeName.endsWith("<T>")){
                    if(genericTypeName.startsWith("java.util.List")){
                        memberType = List.class;
                        memberSubType1 = clzzT;
                    }else{
                        memberType = clzzT;
                    }
                    memberClzz = clzzT;
                }else{
                    try {
                        memberType = Class.forName(childField.getType().getName());
                        memberClzz = memberType;
                    } catch (ClassNotFoundException e) {e.printStackTrace();}
                }

                List<ParamField> memberChild = null;
                for(Field memberChildField : memberClzz.getDeclaredFields()){
                    if(ObjectUtils.isEmpty(memberChild)) memberChild = new ArrayList<>();

                    ParamField memberChildParamField = responseParam(memberChildField);
                    if(memberChildParamField != null) memberChild.add(memberChildParamField);
                }

                member.add(ParamField.builder().name(memberName).type(memberType).subType1(memberSubType1).subType2(memberSubType2).member(memberChild).description(memberDesc).build());
            }
        }

        return ParamField.builder().type(type).member(member).build();
    }

    public static Map<String, Object> loadField(Object obj){
        Map<String, Object> result = new HashMap<>();

        if(obj != null){
            Class<?> clzz = obj.getClass();

            Class<?> type = null;
            try {
                Method getTypeMethod = clzz.getMethod("getType");
                if(getTypeMethod != null) type = (Class<?>) getTypeMethod.invoke(obj);
            } catch (Exception e) {e.printStackTrace();}
            result.put("type", type);

            if(type == null) return null;

            String name = null;
            try { name = BeanUtils.getProperty(obj, "name"); } catch (Exception e) {e.printStackTrace();}
            result.put("name", name);

            Type genericType = null;
            for(Method method : clzz.getMethods()){
                Type returnType = method.getReturnType();

                if(returnType == Type.class){
                    try {
                        genericType = (Type) method.invoke(obj);
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
            if(genericType instanceof ParameterizedType){
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();

                for(int i=0;i<actualTypeArguments.length;i++){
                    String typeName = actualTypeArguments[i].getTypeName();
                    if(typeName.contains("<")) typeName = typeName.split("<")[0];

                    Class<?> subType = null;
                    try {
                        subType = Class.forName(typeName);
                    } catch (Exception e) { e.printStackTrace(); }
                    result.put("subType"+(i+1), subType);
                }
            }
        }

        return result;
    }

    public static ParamField requestParam(Class<?>[] validated, Object obj, Object parent) {
        List<ParamField> member = null;
        String description = null;

        Map<String, Object> fieldMap = loadField(obj);
        String name = (fieldMap.get("name") != null)?(String) fieldMap.get("name") : null;
        Class<?> type = (fieldMap.get("type") != null)?(Class<?>) fieldMap.get("type") : null;
        Class<?> subType1 = (fieldMap.get("subType1") != null)?(Class<?>) fieldMap.get("subType1") : null;
        Class<?> subType2 = (fieldMap.get("subType2") != null)?(Class<?>) fieldMap.get("subType2") : null;
        ParamFieldValidate validate = null;

        Class<?> memberClass = type;
        if(type == List.class) memberClass = subType1;
        else if(type == Map.class) memberClass = subType2;

        if(!isPrimitive(memberClass)){
            for(Field childField : memberClass.getDeclaredFields()){
                if(ObjectUtils.isEmpty(member)) member = new ArrayList<>();

                ParamField memberParamField = requestParam(validated, childField, obj);
                if(memberParamField != null) member.add(memberParamField);
            }
        }

        if(obj instanceof AnnotatedElement){
            AnnotatedElement elem = (AnnotatedElement) obj;
            if(elem.isAnnotationPresent(ParamGroup.class)) {
                ParamGroup ano = elem.getAnnotation(ParamGroup.class);
                if(!ParamField.isValidatedContains(validated, ano.groups())) return null;
            }

            Map<String, Object> parentMap = loadField(parent);
            Class<?> parentType = (parentMap.get("subType1") != null)?((parentMap.get("subType1") != List.class)? (Class<?>) parentMap.get("subType1"): (Class<?>) parentMap.get("subType2")):(Class<?>) parentMap.get("type");

            if(elem.isAnnotationPresent(Description.class)) description = elem.getAnnotation(Description.class).value();

            if(elem.isAnnotationPresent(MapConstraints.class)){
                MapConstraints mapConstraints = elem.getAnnotation(MapConstraints.class);
                Class<?> clazz = mapConstraints.clazz();

                if(!isPrimitive(clazz)){
                    for(Field childField : clazz.getDeclaredFields()){
                        if(ObjectUtils.isEmpty(member)) member = new ArrayList<>();

                        ParamField memberParamField = requestParam(validated, childField, obj);
                        if(memberParamField != null) member.add(memberParamField);
                    }
                }
                validate = ParamFieldValidate.convert(validated, parentType, name, clazz);
            }else{
                validate = ParamFieldValidate.convert(validated, parentType, name, elem);
            }
        }

        return ParamField.builder().name(name).type(type).subType1(subType1).subType2(subType2).member(member).description(description).validate(validate).build();
    }

    public static boolean isPrimitive(Class<?> clzz){
        if(clzz == null) return true;
        return (ClassUtils.isPrimitiveOrWrapper(clzz) | clzz == String.class | clzz == Date.class | clzz == Class.class | clzz.getSuperclass() == null |
                (clzz.getSuperclass() != null && clzz.getSuperclass().getName().startsWith("java.lang.Enum")) |
                (clzz.getName().startsWith("[L"))
        );
    }

    public static boolean isValidatedContains(Class<?>[] validated, Class<?>[] targets){
        if(ObjectUtils.isEmpty(validated)) return true;
        if(ObjectUtils.isEmpty(targets)) return true;
        for(Class<?> validate : validated){
            for(Class<?> target : targets){
                if(validate == target) return true;
            }
        }
        return false;
    }



    @JsonIgnore
    public ParamField clone(){
        return ParamField.builder()
                .name(name)
                .description(description)
                .type(type)
                .subType1(subType1)
                .subType2(subType2)
                .validate(validate)
                .member(member).build();
    }
}
