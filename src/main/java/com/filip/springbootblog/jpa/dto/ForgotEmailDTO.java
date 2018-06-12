package com.filip.springbootblog.jpa.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
public class ForgotEmailDTO implements Serializable {


    private static final long serialVersionUID = -8601768018174173027L;

    @NotEmpty
    private String email;

}
