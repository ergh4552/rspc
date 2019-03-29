package com.kpoy.rspc.model;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GpioAccess {
    final GpioController gpio = GpioFactory.getInstance();

    final GpioPinDigitalOutput pin_00_LED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "GPIO_00");

    public void toggleLED()
    {
        log.debug("toggleLED");
        pin_00_LED.toggle();
    }
}
