package com.platfos.pongift.exception;


import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** WebError 오류 객체 **/
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class WebException extends Exception {
    @Getter
    private HashMap<String, Object> attrs;

    public WebException(){
        super();
        this.attrs = new HashMap<>();
    }
    public WebException(Map<String, Object> attrs){
        super((((!StringUtils.isEmpty((String) attrs.get("error")))?(System.lineSeparator()+"error : " + (String) attrs.get("error") + System.lineSeparator()):"")
                +
                ((!StringUtils.isEmpty((String) attrs.get("message")))?("message : " + (String) attrs.get("message")):"")));
        this.attrs = new HashMap<>();
        this.attrs.putAll(attrs);
    }
    public void print(){
        Set<String> keyset = attrs.keySet();
        Iterator<String> it = keyset.iterator();
        while(it.hasNext()){
            String key = it.next();
            System.out.println(key + " : " + attrs.get(key));
        }
    }
    public boolean isSendNotification(){
        if(!ObjectUtils.isEmpty(attrs) && attrs.containsKey("path")){
            String path = (String) attrs.get("path");
            if(!StringUtils.isEmpty(path) && path.endsWith(".ico")) return false;
        }
        if(!ObjectUtils.isEmpty(attrs) && attrs.containsKey("error")){
            String error = (String) attrs.get("error");
            if(!StringUtils.isEmpty(error) && error.equals("Not Found")) return false;
        }
        return true;
    }
}
