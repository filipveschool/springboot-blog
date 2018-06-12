package com.filip.springbootblog.jpa.services.interfaces;

import com.filip.springbootblog.jpa.dto.AccessDTO;

public interface IAccessService {

    boolean isEmailApproved(String email);

    AccessDTO createAccessDTO(String email);
}
