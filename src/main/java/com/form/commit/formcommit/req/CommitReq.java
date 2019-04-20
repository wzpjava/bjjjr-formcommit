package com.form.commit.formcommit.req;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommitReq {

    private String id;
    private String carNum;
    private String vin;


    private boolean compulsory;
    private String compulsoryTime;
    private double compulsoryAmount;
    private double compulsoryTax;

    private boolean business;
    private String businessTime;
    private double businessAmount;
}
