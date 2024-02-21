package com.example.novigrad;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private String requestId;
    private String associatedService;
    private  String requesterId;
    private String branchId;
    private String status;
    private List<String> files=new ArrayList<>();

    public Request(){
        //empty constructor
    }

    public Request(String requestId, String name, String requesterId, String branchId, String status, List<String>files){
        this.requestId=requestId;
        this.associatedService=name;
        this.requesterId=requesterId;
        this.branchId=branchId;
        this.status=status;
        this.files=files;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getAssociatedService() {
        return associatedService;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setAssociatedService(String name) {
        this.associatedService = name;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

}
