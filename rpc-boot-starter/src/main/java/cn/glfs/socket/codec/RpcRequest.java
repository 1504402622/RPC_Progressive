package cn.glfs.socket.codec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class RpcRequest implements Serializable {

    private String serviceVersion;
    private String className;
    private String methodName;
    private Integer methodCode;
    private Object parameter;
    private Class<?> parameterTypes;

    private Map<String,Object> serviceAttachments = new HashMap<>();
    private Map<String,Object> clientAttachments = new HashMap<>();


    public RpcRequest() {
    }

    public Map<String, Object> getServiceAttachments() {
        return serviceAttachments;
    }

    public void setServiceAttachments(Map<String, Object> serviceAttachments) {
        this.serviceAttachments = serviceAttachments;
    }

    public Map<String, Object> getClientAttachments() {
        return clientAttachments;
    }

    public void setClientAttachments(Map<String, Object> clientAttachments) {
        this.clientAttachments = clientAttachments;
    }

    public void setMethodCode(Integer methodCode) {
        this.methodCode = methodCode;
    }

    public Integer getMethodCode() {
        return methodCode;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public void setParameterTypes(Class<?> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?> getParameterTypes() {
        return parameterTypes;
    }
}
