package cn.glfs.invoke;

import cn.glfs.socket.codec.RpcRequest;


public class Invocation {

    private String serviceVersion;
    private String className;
    private String methodName;
    private Integer methodCode;

    private Object parameter;
    private Class<?> parameterTypes;

    public Invocation(RpcRequest rpcRequest){
        this.serviceVersion = rpcRequest.getServiceVersion();
        this.className = rpcRequest.getClassName();
        this.methodName = rpcRequest.getMethodName();
        this.parameter = rpcRequest.getParameter();
        this.parameterTypes = rpcRequest.getParameterTypes();
        this.methodCode = rpcRequest.getMethodCode();
    }
    public Invocation(){}

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

    public Integer getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(Integer methodCode) {
        this.methodCode = methodCode;
    }
}
