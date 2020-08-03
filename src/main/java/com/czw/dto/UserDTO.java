package com.czw.dto;

import com.czw.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author: ChengZiwang
 * @date: 2020/7/21
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull
    @IsMobile
    private String id;
    @NotNull
    private String password;
}
