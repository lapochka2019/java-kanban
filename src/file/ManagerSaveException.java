package file;

import java.io.IOException;

public class ManagerSaveException extends  RuntimeException{
    public ManagerSaveException(String s, IOException e){
        super(s,e);
    }
}
