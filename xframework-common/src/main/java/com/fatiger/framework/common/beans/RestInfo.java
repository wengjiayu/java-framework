package com.fatiger.framework.common.beans;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wengjiayu
 */
@Getter
@Setter
public class RestInfo {

    private String serviceHost;
    private String relativePath;
    private int timeout = 60;
    private String httpMethod;


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RestInfo)) {
            return false;
        } else {
            RestInfo other = (RestInfo) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object thisServiceHost = this.getServiceHost();
                Object otherServiceHost = other.getServiceHost();
                if (thisServiceHost == null) {
                    if (otherServiceHost != null) {
                        return false;
                    }
                } else if (!thisServiceHost.equals(otherServiceHost)) {
                    return false;
                }

                Object thisRelativePath = this.getRelativePath();
                Object otherRelativePath = other.getRelativePath();
                if (thisRelativePath == null) {
                    if (otherRelativePath != null) {
                        return false;
                    }
                } else if (!thisRelativePath.equals(otherRelativePath)) {
                    return false;
                }

                if (this.getTimeout() != other.getTimeout()) {
                    return false;
                } else {
                    Object thisHttpMethod = this.getHttpMethod();
                    Object otherHttpMethod = other.getHttpMethod();
                    if (thisHttpMethod == null) {
                        if (otherHttpMethod != null) {
                            return false;
                        }
                    } else if (!thisHttpMethod.equals(otherHttpMethod)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestInfo;
    }

    @Override
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object serviceHost = this.getServiceHost();
        result = result * 59 + (serviceHost == null ? 0 : serviceHost.hashCode());
        Object relativePath = this.getRelativePath();
        result = result * 59 + (relativePath == null ? 0 : relativePath.hashCode());
        result = result * 59 + this.getTimeout();
        Object httpMethod = this.getHttpMethod();
        result = result * 59 + (httpMethod == null ? 0 : httpMethod.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "RestInfo(serviceHost=" + this.getServiceHost() + ", relativePath=" + this.getRelativePath() + ", timeout=" + this.getTimeout() + ", httpMethod=" + this.getHttpMethod() + ")";
    }

}
