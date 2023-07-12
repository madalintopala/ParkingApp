package calitei.parking.api.error;

import calitei.parking.api.error.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@RestController
@ControllerAdvice
public class ContentExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ExecutionException.class, InterruptedException.class})
    public final ResponseEntity<ExceptionResponse> handleException(Exception ex){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleException(NotFoundException ex){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public final ResponseEntity<ExceptionResponse> handleException(AlreadyExistsException ex){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParkingSpotException.class)
    public final ResponseEntity<ExceptionResponse> handleException(ParkingSpotException ex){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotOwnerException.class)
    public final ResponseEntity<ExceptionResponse> handleException(NotOwnerException ex){
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
