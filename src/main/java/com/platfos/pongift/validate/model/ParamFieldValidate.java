package com.platfos.pongift.validate.model;

import com.platfos.pongift.definition.AttachDataType;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.constraints.*;
import com.platfos.pongift.validate.constraints.Number;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamFieldValidate {
    private boolean require;
    private List<ParamRequireDependence> requireDependences;
    private int maxLength;
    private long min;
    private long max;
    private double maxD;
    private String dateTimeFormat;
    private SIMCodeGroup[] groupCode;
    private String dependenceFieldName;
    private SIMIndex[] simIndex;
    private String groupCodeIndexDependence;
    private String[] customCode;
    private boolean isSimCode;
    private String[] requestFileTypes;
    private String maxFileSize;
    private int minImageWidth;
    private int minImageHeight;
    private int maxImageWidth;
    private int maxImageHeight;
    private String[] supportFileFormat;
    private boolean isFile;
    private boolean isParentId;
    private int maxLevel;
    private boolean isNumber;
    private String description;


    public static ParamFieldValidate convert(Class<?>[] validated, Class<?> parent, String fieldName, AnnotatedElement elem){
        boolean require = false;
        if(elem.isAnnotationPresent(Require.class)) {
            Require ano = elem.getAnnotation(Require.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) require = true;
        }
        if(!require & elem.isAnnotationPresent(RequestParam.class)) {
            require = elem.getAnnotation(RequestParam.class).required();
        }

        long min = Integer.MIN_VALUE;
        if(elem.isAnnotationPresent(Min.class)) {
            Min ano = elem.getAnnotation(Min.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) min = ano.value();
        }

        long max = Integer.MAX_VALUE;
        if(elem.isAnnotationPresent(Max.class)) {
            Max ano = elem.getAnnotation(Max.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) max = ano.value();
        }

        double dMax = Double.MAX_VALUE;
        if(elem.isAnnotationPresent(DecimalMax.class)) {
            DecimalMax ano = elem.getAnnotation(DecimalMax.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) {
                String val = ano.value();
                try {
                    dMax = Double.parseDouble(val);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        int maxLength = 0;
        if(elem.isAnnotationPresent(Length.class)) {
            Length ano = elem.getAnnotation(Length.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) maxLength = ano.max();
        }

        boolean isNumber = false;
        if(elem.isAnnotationPresent(Number.class)) {
            Number ano = elem.getAnnotation(Number.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) isNumber = true;
        }

        String dateTimeFormat = "";
        if(elem.isAnnotationPresent(DateTime.class)) {
            DateTime ano = elem.getAnnotation(DateTime.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) dateTimeFormat = ano.format();
        }

        String[] customCode = {};
        if(elem.isAnnotationPresent(CustomCode.class)) {
            CustomCode ano = elem.getAnnotation(CustomCode.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) customCode = ano.codes();
        }

        List<String> requestFileTypes = new ArrayList<>();
        String maxFileSize = "";
        int minImageWidth = Integer.MIN_VALUE;
        int minImageHeight = Integer.MIN_VALUE;
        int maxImageWidth = Integer.MIN_VALUE;
        int maxImageHeight = Integer.MIN_VALUE;
        String[] supportFileFormat = {};
        boolean isFile = false;
        if(elem.isAnnotationPresent(File.class)) {
            File ano = elem.getAnnotation(File.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) {
                for(AttachDataType attachDataType : ano.supportAttachType()) requestFileTypes.add(attachDataType.name());
                maxFileSize = ano.maxFileSize();
                minImageWidth = ano.minImageWidth();
                minImageHeight = ano.minImageHeight();
                maxImageWidth = ano.maxImageWidth();
                maxImageHeight = ano.maxImageHeight();
                supportFileFormat = ano.supportFileFormat();
                isFile = true;
            }
        }

        SIMIndex[] simIndex = {};
        if(elem.isAnnotationPresent(SimIndex.class)) {
            SimIndex ano = elem.getAnnotation(SimIndex.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) simIndex = ano.simIndex();
        }

        List<Conditional> conditionals = new ArrayList<>();
        if(parent != null){
            Conditionals tmpConditionals = parent.getAnnotation(Conditionals.class);
            if(tmpConditionals != null) {
                conditionals = new ArrayList<Conditional>(Arrays.asList(tmpConditionals.value()));
            }

            Conditional conditional = parent.getAnnotation(Conditional.class);
            if(conditional != null) conditionals.add(conditional);
        }


        boolean isSimCode = false;
        SIMCodeGroup[] groupCode = {};
        String dependenceFieldName = "";
        List<ParamRequireDependence> requireDependences = null;
        if(elem.isAnnotationPresent(SimCode.class)) {
            SimCode ano = elem.getAnnotation(SimCode.class);
            if(ParamField.isValidatedContains(validated, ano.groups())) {
                if(!ObjectUtils.isEmpty(conditionals)){
                    for(Conditional conditional : conditionals){
                        String selected = conditional.selected();
                        String value = conditional.value();
                        String[] targets = conditional.targets();

                        if(selected.equals(fieldName) & !StringUtils.isEmpty(value)){
                            for(String target : targets){
                                Field targetField = null;
                                try { targetField = parent.getDeclaredField(target); } catch (Exception e) {e.printStackTrace();}
                                if(!ObjectUtils.isEmpty(targetField)){
                                    if(targetField.isAnnotationPresent(SimCode.class)){
                                        if(ObjectUtils.isEmpty(requireDependences)) requireDependences = new ArrayList<>();
                                        requireDependences.add(ParamRequireDependence.builder()
                                                .field(target)
                                                .value(value)
                                                .build());
                                    }
                                }
                            }
                            groupCode = ano.groupCode();
                            isSimCode = true;
                            break;
                        }else{
                            for(String target : targets){
                                if(target.equals(fieldName)){
                                    Field selectedField = null;
                                    try { selectedField = parent.getDeclaredField(selected); } catch (Exception e) {e.printStackTrace();}
                                    if(selectedField.isAnnotationPresent(SimCode.class)){
                                        if(!ObjectUtils.isEmpty(selectedField.getAnnotation(SimCode.class).groupCode())){
                                            SIMCodeGroup selectedSimCodeGroup = selectedField.getAnnotation(SimCode.class).groupCode()[0];
                                            groupCode = SIMCodeGroup.findChildCodeGroupByParentCodeGroup(selectedSimCodeGroup);
                                            isSimCode = true;
                                            dependenceFieldName = selected;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if(isSimCode) break;
                    }
                    if(!isSimCode){
                        groupCode = ano.groupCode();
                        isSimCode = true;
                    }
                }else{
                    groupCode = ano.groupCode();
                    isSimCode = true;
                }
            }
        }

        return ParamFieldValidate.builder()
                .require(require)
                .min(min)
                .max(max)
                .maxLength(maxLength)
                .isNumber(isNumber)
                .dateTimeFormat(dateTimeFormat)
                .customCode(customCode)
                .isFile(isFile)
                .maxFileSize(maxFileSize)
                .minImageWidth(minImageWidth)
                .minImageHeight(minImageHeight)
                .maxImageWidth(maxImageWidth)
                .maxImageHeight(maxImageHeight)
                .supportFileFormat(supportFileFormat)
                .requestFileTypes(requestFileTypes.toArray(new String[requestFileTypes.size()]))
                .simIndex(simIndex)
                .groupCode(groupCode)
                .isSimCode(isSimCode)
                .dependenceFieldName(dependenceFieldName)
                .requireDependences(requireDependences)
                .build();
    }

    @Override
    public String toString() {
        String strRequireDependences = "";
        if(ObjectUtils.isEmpty(requireDependences)){
            for(ParamRequireDependence requireDependence : requireDependences){
                String text = requireDependence.getField() + (StringUtils.isEmpty(requireDependence.getValue())?"":(" : "+requireDependence.getValue()));
                if(StringUtils.isEmpty(strRequireDependences)) strRequireDependences = text;
                else strRequireDependences += ", " + text;
            }
        }
        return "ParamFieldValidateImpl{" +
                "require=" + require +
                ", requireDependences='" + strRequireDependences + '\'' +
                ", maxLength=" + maxLength +
                ", min=" + min +
                ", max=" + max +
                ", dateTimeFormat='" + dateTimeFormat + '\'' +
                ", groupCode=" + groupCode +
                ", dependenceFieldName='" + dependenceFieldName + '\'' +
                ", simIndex=" + simIndex +
                ", customCode=" + Arrays.toString(customCode) +
                ", isSimCode=" + isSimCode +
                ", isImageFile=" + isFile +
                ", isParentId=" + isParentId +
                ", maxLevel=" + maxLevel +
                ", description=" + description +
                '}';
    }
}
