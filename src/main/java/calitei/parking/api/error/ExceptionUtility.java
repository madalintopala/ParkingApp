package calitei.parking.api.error;


import calitei.parking.api.repositories.MethodType;

public class ExceptionUtility {

    public static String createErrorMessage(String paramType, String paramName, MethodType type){
        String errorMessage = "";
        switch ( type ){
            case CREATE:
                errorMessage = paramType + " with ID " + paramName + " already exists!";
                break;
            case DELETE:
            case UPDATE:
                errorMessage = paramType + " with ID " + paramName + " not found!";
                break;
            default:
                errorMessage = paramType + " with ID " + paramName + " not found!";
        }
        return errorMessage;
    }

    public static String createErrorMessage(String paramType, String paramName, String customMessage){
        return paramType + " with ID " + paramName + " " + customMessage;
    }
}
