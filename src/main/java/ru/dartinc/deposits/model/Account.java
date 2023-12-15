package ru.dartinc.deposits.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Account {
    private BigDecimal amount;
    private final List<Deposit> deposits = new ArrayList<>();
}
