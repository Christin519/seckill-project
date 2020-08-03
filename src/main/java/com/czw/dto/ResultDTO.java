package com.czw.dto;

import com.czw.enums.CodeMsg;
import lombok.Data;

@Data
public class ResultDTO<T> {
	private Integer code;
	private String msg;
	private T data;

	private ResultDTO(CodeMsg codeMsg) {
		this.code = codeMsg.getCode();
		this.msg = codeMsg.getMsg();
	}

	public static <T> ResultDTO<T> success(T data){
		ResultDTO<T> resultDto = new ResultDTO<T>(CodeMsg.SUCCESS);
		resultDto.setData(data);
		return resultDto;
	}

	public static ResultDTO error(CodeMsg codeMsg){
		return new ResultDTO(codeMsg);
	}

	public static ResultDTO error(CodeMsg codeMsg,String msg){
		ResultDTO resultDTO = new ResultDTO(codeMsg);
		resultDTO.setMsg(resultDTO.getMsg() + ":" + msg);
		return resultDTO;
	}
}
