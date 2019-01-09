package com.github.fishlikewater.spring.boot.schedule.server.console.config;


import com.github.fishlikewater.spring.boot.schedule.server.console.kit.CodeEnum;
import com.github.fishlikewater.spring.boot.schedule.server.console.kit.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ExceptionController
 * @Description
 * @date 2018年12月20日 22:51
 **/
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
@Slf4j
public class ExceptionController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    public ExceptionController(ErrorAttributes errorAttributes,
                                ErrorProperties errorProperties) {
        this(errorAttributes, errorProperties, Collections.emptyList());
    }

    public ExceptionController(ErrorAttributes errorAttributes,
                                ErrorProperties errorProperties, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        Assert.notNull(errorProperties, "ErrorProperties must not be null");
        this.errorProperties = errorProperties;
    }

    @Override
    public String getErrorPath() {
        return this.errorProperties.getPath();
    }

    @RequestMapping(produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request,
                                  HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(request, false));
        Throwable cause = getCause(request);
        //错误信息
        String message = (String) model.get("message");
        log.error("status:{},this error message:{}", status, message);
        ResponseEntity result = getErrorMessage(cause);
        response.setStatus(status.value());
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        if(modelAndView == null){
            modelAndView = new ModelAndView();
        }
        if(status == HttpStatus.NOT_FOUND){
            modelAndView.setViewName("404");
            result.setCode(CodeEnum.NOTFOUND.getCode());
        }else if(status == HttpStatus.UNAUTHORIZED){
            modelAndView.setViewName("401");
            result.setCode(CodeEnum.UNAUTHORIZED.getCode());
        }else {
            modelAndView.setViewName("500");
        }
        return modelAndView;
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(request, false));
        //错误信息
        String message = (String) model.get("message");
        log.error("status:{},this error message:{}", status, message);
        Throwable cause = getCause(request);
        ResponseEntity result = getErrorMessage(cause);
        if(status == HttpStatus.NOT_FOUND){
            result.setCode(CodeEnum.NOTFOUND.getCode());
        }else if(status == HttpStatus.UNAUTHORIZED){
            result.setCode(CodeEnum.UNAUTHORIZED.getCode());
        }
        return result;
    }

    /**
     * Determine if the stacktrace attribute should be included.
     * @param request the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request,
                                          MediaType produces) {
        ErrorProperties.IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }

    /**
     * Provide access to the error properties.
     * @return the error properties
     */
    protected ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }

    protected Throwable getCause(HttpServletRequest request){
        Throwable error = (Throwable) request.getAttribute("javax.servlet.error.exception");
        if(error != null){
            while (error instanceof ServletException && error.getCause() != null){
                error = ((ServletException)error).getCause();
            }
        }
        return error;
    }

    protected ResponseEntity getErrorMessage(Throwable ex){
        if(ex instanceof MissingServletRequestParameterException){
            MissingServletRequestParameterException ex1 = (MissingServletRequestParameterException)ex;
            return sendFail("参数["+ex1.getParameterName() + "]不能为空");
        } else if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException ex1 = (MethodArgumentNotValidException)ex;
            return sendFail(ex1.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        }else if(ex instanceof ConstraintViolationException){
            ConstraintViolationException ex1 = (ConstraintViolationException)ex;
            if(!ex1.getConstraintViolations().isEmpty()){
                String message = ex1.getConstraintViolations().iterator().next().getMessage();
                return sendFail(message);
            }
            return sendFail("系统异常");
        }else if(ex instanceof NullPointerException){
            return sendFail("空指针异常,请检测参数");
        } else {
            return sendFail("系统异常");
        }
    }

    protected ResponseEntity sendFail(String message){
        return new ResponseEntity().setCode(CodeEnum.FAIL.getCode())
                .setMessage(message);
    }
}
