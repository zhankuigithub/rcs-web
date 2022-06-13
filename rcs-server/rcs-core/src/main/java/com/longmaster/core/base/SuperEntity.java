package com.longmaster.core.base;

import javax.validation.groups.Default;
import java.io.Serializable;

public class SuperEntity implements Serializable {

    public interface Login extends Default {
    }

    public interface Create extends Default {
    }

    public interface Update extends Default {
    }

    public interface Delete extends Default {
    }


}
