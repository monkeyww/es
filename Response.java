package com.bestpay.insurance.cbs.api.model;

import java.io.Serializable;

/**
 * H5版收银台实体
 *
 * @author cnn
 * @version 1.0
 * @project com.bestpay.insurance
 * @title Response
 * @date 2016
 */
public class Response<T> implements Serializable {

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success;
    private T result;
    private String errorCode;
    private String errorMsg;

    public Response() {
    }

    public Response(T result) {
        this.success = true;
        this.result = result;
    }

    public Response(boolean flag, T result) {
        if (flag) {
            this.success = true;
            this.result = result;
        } else {
            this.success = false;
            this.errorCode = (String) result;
        }

    }

    public Response(String errorCode) {
        this.success = false;
        this.errorCode = errorCode;
    }

    public Response(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.success = true;
        this.result = result;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.success = false;
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Response response = (Response) o;
            boolean isErrorCode = !this.errorCode.equals(response.errorCode) ? false : this.result.equals(response.result);
            return this.success != response.success ? false : (isErrorCode);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result1 = this.success ? 1 : 0;
        result1 = 31 * result1 + this.result.hashCode();
        result1 = 31 * result1 + this.errorCode.hashCode();
        return result1;
    }

    @Override
    public String toString() {
    	return "success";
    }
}
