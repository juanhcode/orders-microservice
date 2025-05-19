package com.develop.orders_microservice.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//Indica que esta excepcion formara parte del cuerpo de respuesta de la peticion
@ResponseBody
//Indica que al lanzarse una excepcion, reportara un codigo de respuesta 404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message){
        super(message);
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}
