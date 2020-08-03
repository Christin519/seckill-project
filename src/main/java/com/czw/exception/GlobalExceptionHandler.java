package com.czw.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.czw.dto.ResultDTO;
import com.czw.enums.CodeMsg;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
	@ExceptionHandler(value=Exception.class)
	public ResultDTO<String> exceptionHandler(Exception e){
		if(e instanceof GlobalException) {
			GlobalException ex = (GlobalException)e;
			return ResultDTO.error(ex.getCm());
		}else if(e instanceof BindException) {
			BindException ex = (BindException)e;
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();
			return ResultDTO.error(CodeMsg.BIND_ERROR,msg);
		}else {
			return ResultDTO.error(CodeMsg.SERVER_ERROR);
		}
	}
}
