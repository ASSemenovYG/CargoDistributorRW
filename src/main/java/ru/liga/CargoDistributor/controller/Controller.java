package ru.liga.CargoDistributor.controller;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Controller {
    @ShellMethod("hello")
    public void hello() {
        System.out.println("Hello");
    }
}