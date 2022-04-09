package de.bitsandbooks.finance.controllers.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("api/messages")
@RestController
public class HelloController {

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public String hello() {
    return "Hello From DigitalFinanceAccountant";
  }
}
