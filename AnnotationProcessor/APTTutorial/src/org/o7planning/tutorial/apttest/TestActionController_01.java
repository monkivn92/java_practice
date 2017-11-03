package org.o7planning.tutorial.apttest;

import org.o7planning.ann.Action;
import org.o7planning.ann.Controller;

@Controller
public class TestActionController_01
{

    @Action
    public String exit() {
        return null;
    }

    @Action
    public void print() {

    }

    @Action
    public int error() {
        return 0;
    }
}
