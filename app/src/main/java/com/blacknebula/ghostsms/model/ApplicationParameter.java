package com.blacknebula.ghostsms.model;

/**
 * ApplicationParameter represents a parameter saved in Parameters Table
 *
 * @author hazem
 */
public class  ApplicationParameter <T> {
    public String name;
    public T value;

    public ApplicationParameter() {
    }

    public ApplicationParameter(String name, T value) {
        this.value = value;
        this.name = name;
    }


    public void setValue(T value) {
        this.value = value;
    }


}
