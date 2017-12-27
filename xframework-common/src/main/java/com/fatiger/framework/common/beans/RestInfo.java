package com.fatiger.framework.common.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestInfo {

    private String servicehost;
    private String relativePath;
    private int timeout = 60;
    private String httpMethod;


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
                Object this$servicehost = this.getServicehost();
                Object other$servicehost = other.getServicehost();
                if (this$servicehost == null) {
                    if (other$servicehost != null) {
                        return false;
                    }
                } else if (!this$servicehost.equals(other$servicehost)) {
                    return false;
                }

                Object this$relativePath = this.getRelativePath();
                Object other$relativePath = other.getRelativePath();
                if (this$relativePath == null) {
                    if (other$relativePath != null) {
                        return false;
                    }
                } else if (!this$relativePath.equals(other$relativePath)) {
                    return false;
                }

                if (this.getTimeout() != other.getTimeout()) {
                    return false;
                } else {
                    Object this$httpMethod = this.getHttpMethod();
                    Object other$httpMethod = other.getHttpMethod();
                    if (this$httpMethod == null) {
                        if (other$httpMethod != null) {
                            return false;
                        }
                    } else if (!this$httpMethod.equals(other$httpMethod)) {
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

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $servicehost = this.getServicehost();
        result = result * 59 + ($servicehost == null ? 0 : $servicehost.hashCode());
        Object $relativePath = this.getRelativePath();
        result = result * 59 + ($relativePath == null ? 0 : $relativePath.hashCode());
        result = result * 59 + this.getTimeout();
        Object $httpMethod = this.getHttpMethod();
        result = result * 59 + ($httpMethod == null ? 0 : $httpMethod.hashCode());
        return result;
    }

    public String toString() {
        return "RestInfo(servicehost=" + this.getServicehost() + ", relativePath=" + this.getRelativePath() + ", timeout=" + this.getTimeout() + ", httpMethod=" + this.getHttpMethod() + ")";
    }

}
