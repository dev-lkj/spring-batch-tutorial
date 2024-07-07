package com.example.springBatchTutorial.job.FileDataReadWrite.dto;

import lombok.Data;

@Data
public class PlayerYears {

    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
}
